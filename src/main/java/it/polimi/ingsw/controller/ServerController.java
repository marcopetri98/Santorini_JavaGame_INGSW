package it.polimi.ingsw.controller;

// necessary imports of Java SE

// other project's classes needed here
import it.polimi.ingsw.core.*;
import it.polimi.ingsw.core.gods.GodCard;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.RemoteView;
import it.polimi.ingsw.network.game.NetAvailableBuildings;
import it.polimi.ingsw.network.game.NetAvailablePositions;
import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetGameSetup;
import it.polimi.ingsw.network.objects.NetGaming;
import it.polimi.ingsw.util.exceptions.BadRequestException;
import it.polimi.ingsw.util.exceptions.NoBuildException;
import it.polimi.ingsw.util.exceptions.NoMoveException;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;
import it.polimi.ingsw.util.observers.ObservableObject;
import it.polimi.ingsw.util.observers.ObservableRemoteView;
import it.polimi.ingsw.util.observers.ObserverController;
import it.polimi.ingsw.util.observers.ObserverRemoteView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the main class for the controller of the game, it is the controller for the Distributed MVC pattern. It owns the game and other controllers and manage the all execution, it calls other controllers when specific actions must be controlled and eventually modifies the game if needed.
 */
public class ServerController implements ObserverController {
	private final Game observedModel;
	private final Mover moveController;
	private final Builder buildController;
	private final DefeatManager defeatController;
	private final VictoryManager victoryController;
	private final SetupManager setupController;

	// constructors and setters for this class
	/**
	 * Creates a {@code ServerController} with the game to control.
	 * @param g is the game to control
	 * @throws NullPointerException if {@code g} is null
	 */
	public ServerController(Game g) throws NullPointerException {
		if (g == null) throw new NullPointerException();
		moveController = new Mover(g);
		buildController = new Builder(g);
		defeatController = new DefeatManager(g);
		victoryController = new VictoryManager(g);
		setupController = new SetupManager(g);
		observedModel = g;
	}

	// actions called by the players or the server
	/**
	 * Calls the generate order function.
	 */
	public void generateOrder() {
		setupController.generateOrder();
	}

	// support methods
	/**
	 * Checks if the current phase is a phase where can be performed a move.
	 * @return true if a move can be performed, otherwise false
	 */
	private boolean isMovablePhase() {
		return observedModel.getPhase().getGamePhase() == GamePhase.MOVE || observedModel.getPhase().getGamePhase() == GamePhase.BEFOREMOVE;
	}
	/**
	 * Checks if the current phase is a phase where can be performed a build.
	 * @return true if a build can be performed, otherwise false
	 */
	private boolean isBuildablePhase() {
		if (observedModel.getPhase().getGamePhase() == GamePhase.BUILD || observedModel.getPhase().getGamePhase() == GamePhase.BEFOREMOVE) {
			return true;
		}
		return false;
	}

	// OVERRIDDEN METHODS FROM THE OBSERVER
	/**
	 * This method receives a request from a client through its {@link it.polimi.ingsw.network.RemoteView} and analyze if it's the player's turn and if it is the correct phase (the color phase), if this conditions are all true then the method calls the {@link it.polimi.ingsw.controller.SetupManager}, otherwise it calls the error handling method of the {@link it.polimi.ingsw.network.RemoteView}.
	 * @param observed is the {@link it.polimi.ingsw.util.observers.ObserverObject} which called this method
	 * @param playerColors is the {@link it.polimi.ingsw.network.objects.NetColorPreparation} message sent from the player
	 */
	@Override
	public synchronized void updateColors(ObservableObject observed, NetColorPreparation playerColors) {
		// it controls if the player which sent the request is in its turn and can choose a color
		RemoteView caller = (RemoteView) observed;
		String activePlayer = observedModel.getPlayerTurn().getPlayerName();
		// if the player is trying to choose a color already chosen or isn't its turn it returns an error
		if (observedModel.isFinished() || !activePlayer.equals(playerColors.player) || observedModel.getPhase().getPhase() != Phase.COLORS) {
			caller.communicateError();
		} else {
			// the player can choose this color
			try {
				setupController.changeColor(playerColors);
				observedModel.changeTurn();
			} catch (WrongPhaseException | BadRequestException e) {
				caller.communicateError();
			}
		}
	}
	/**
	 * This method receives a request from a client through its {@link it.polimi.ingsw.network.RemoteView} and analyze if it's the player's turn and if it is the correct phase (the gods phase), if this conditions are all true then the method calls the {@link it.polimi.ingsw.controller.SetupManager}, otherwise it calls the error handling method of the {@link it.polimi.ingsw.network.RemoteView}.
	 * @param observed is the {@link it.polimi.ingsw.util.observers.ObserverObject} which called this method
	 * @param playerGods is the {@link it.polimi.ingsw.network.objects.NetDivinityChoice} message sent from the player
	 */
	@Override
	public synchronized void updateGods(ObservableObject observed, NetDivinityChoice playerGods) {
		// it controls if the player which sent the request is in its turn and can choose a color
		RemoteView caller = (RemoteView) observed;
		String activePlayer = observedModel.getPlayerTurn().getPlayerName();
		if (observedModel.isFinished() || (playerGods.challenger == null && !activePlayer.equals(playerGods.player)) || (playerGods.challenger != null && !activePlayer.equals(playerGods.challenger)) || observedModel.getPhase().getPhase() != Phase.GODS) {
			caller.communicateError();
		} else {
			try {
				setupController.handleGodMessage(playerGods);
				observedModel.changeTurn();
			} catch (WrongPhaseException | BadRequestException e) {
				caller.communicateError();
			}
		}
	}
	/**
	 * This method receives a request from a client through its {@link it.polimi.ingsw.network.RemoteView} and analyze if it's the player's turn and if it is the correct phase (the positioning workers on game map phase), if this conditions are all true then the method calls the {@link it.polimi.ingsw.controller.SetupManager}, otherwise it calls the error handling method of the {@link it.polimi.ingsw.network.RemoteView}.
	 * @param observed is the {@link it.polimi.ingsw.util.observers.ObserverObject} which called this method
	 * @param netObject is the {@link it.polimi.ingsw.network.objects.NetGameSetup} message sent from the player
	 */
	@Override
	public synchronized void updatePositions(ObservableObject observed, NetGameSetup netObject) {
		// it controls if the player which sent the request is in its turn and can choose a color
		RemoteView caller = (RemoteView) observed;
		String activePlayer = observedModel.getPlayerTurn().getPlayerName();
		if (observedModel.isFinished() || !activePlayer.equals(netObject.player) || observedModel.getPhase().getPhase() != Phase.SETUP) {
			caller.communicateError();
		} else {
			try {
				setupController.positionWorkers(netObject);
				observedModel.changeTurn();
				if (observedModel.getPlayers().get(0) == observedModel.getPlayerTurn()) {
					observedModel.computeActions();
					if (observedModel.getPlayerPossibleMoves().size() == 0 && observedModel.getPlayerPossibleBuilds().size() == 0) {
						observedModel.applyDefeat(observedModel.getPlayerTurn());
					} else if (observedModel.getPlayerPossibleBuilds().size() == 0) {
						observedModel.changeTurn();
						observedModel.computeActions();
					}
				}
			} catch (BadRequestException e) {
				caller.communicateError();
			}
		}
	}
	/**
	 * This function is called when a player can move and want to perform a certain move, if it can't move this method is going to call the error handling method of the {@link it.polimi.ingsw.network.RemoteView} and stops.
	 * @param observed is the {@link it.polimi.ingsw.util.observers.ObserverObject} which called this method
	 * @param moveMessage is the {@link it.polimi.ingsw.network.objects.NetGaming} message sent from the player
	 */
	@Override
	public synchronized void updateMove(ObservableObject observed, NetGaming moveMessage) {
		// it controls if the player which sent the request is in its turn and can choose a color
		RemoteView caller = (RemoteView) observed;
		String activePlayer = observedModel.getPlayerTurn().getPlayerName();
		if (observedModel.isFinished() || !activePlayer.equals(moveMessage.player) || !isMovablePhase() || moveMessage.move == null) {
			caller.communicateError();
		} else {
			List<Move> possibleMoves = new ArrayList<>();
			Player movingPlayer = observedModel.getPlayerByName(moveMessage.player);
			Worker selectedWorker;
			Turn turn = observedModel.getPhase();
			List<GodCard> playersCards = observedModel.getPlayers().stream().filter((player) -> { try { player.getCard(); return true; } catch (IllegalStateException e) { return false; } }).map(Player::getCard).collect(Collectors.toList());
			boolean hasMoves = false;

			if (moveMessage.move.workerID == movingPlayer.getPlayerID()+1) {
				selectedWorker = movingPlayer.getWorker1();
			} else {
				selectedWorker = movingPlayer.getWorker2();
			}
			for (GodCard card : playersCards) {
				if (card.getOwner() != movingPlayer && card.getTypeGod() == TypeGod.OTHER_TURN_GOD && turn.getGamePhase() == GamePhase.MOVE) {
					try {
						possibleMoves.addAll(card.checkMove(observedModel.getMap(),selectedWorker,turn));
					} catch (NoMoveException e) {
						throw new AssertionError("Controller called a check move on a god that isn't an other turn god for moves");
					}
				}
			}

			try {
				// if a player has a god that acts on other players turn it doesn't generate moves for him
				if (movingPlayer.getCard().getTypeGod() != TypeGod.OTHER_TURN_GOD) {
					possibleMoves.addAll(movingPlayer.getCard().checkMove(observedModel.getMap(), selectedWorker, turn));
					hasMoves = true;
				} else {
					throw new NoMoveException();
				}
			} catch (NoMoveException e) {
				// if it is the move phase and none of the gods change the standard way of moving it will be called the standard method
				if (turn.getGamePhase() == GamePhase.MOVE) {
					possibleMoves.addAll(GodCard.standardMoves(selectedWorker.getPos().map,selectedWorker,turn));
					hasMoves = true;
				}
			}

			if (hasMoves) {
				if (movingPlayer.isWorkerLocked()) {
					if (movingPlayer.getActiveWorker().workerID == moveMessage.move.workerID) {
						if (!moveController.move(moveMessage.move, possibleMoves)) {
							caller.communicateError();
						} else {
							victoryController.checkVictory(selectedWorker.getLastPos(),selectedWorker.getPos(),possibleMoves);
							observedModel.changeTurn();
							observedModel.computeActions();
							defeatController.buildDefeat(observedModel.getPlayerPossibleBuilds());
						}
					} else {
						caller.communicateError();
					}
				} else {
					if (!moveController.move(moveMessage.move, possibleMoves)) {
						caller.communicateError();
					} else {
						victoryController.checkVictory(selectedWorker.getLastPos(),selectedWorker.getPos(),possibleMoves);
						observedModel.applyWorkerLock(movingPlayer,selectedWorker.workerID-movingPlayer.getPlayerID());

						// if a player with prometheus moved without building before the game must go from before move phase to build phase (2 step on phase advance)
						if (observedModel.getPhase().getGamePhase() == GamePhase.BEFOREMOVE) {
							observedModel.changeTurn();
						}
						observedModel.changeTurn();
						observedModel.computeActions();
						defeatController.buildDefeat(observedModel.getPlayerPossibleBuilds());
					}
				}
			}
		}
	}
	/**
	 * This method is called when a client wants to perform a certain build, if the player cannot perform a build because the indicated one is forbidden or because it isn't its turn or it is the wrong phase, this method is going to call the error handling method of the {@link it.polimi.ingsw.network.RemoteView} and stops.
	 * @param observed is the {@link it.polimi.ingsw.util.observers.ObserverObject} which called this method
	 * @param buildMessage is the {@link it.polimi.ingsw.network.objects.NetGaming} message sent from the player
	 */
	@Override
	public synchronized void updateBuild(ObservableObject observed, NetGaming buildMessage) {
		// it controls if the player which sent the request is in its turn and can choose a color
		RemoteView caller = (RemoteView) observed;
		String activePlayer = observedModel.getPlayerTurn().getPlayerName();
		if (observedModel.isFinished() || !activePlayer.equals(buildMessage.player) || !isBuildablePhase() || buildMessage.build == null) {
			caller.communicateError();
		} else {
			List<Build> possibleBuilds = new ArrayList<>();
			Player buildingPlayer = observedModel.getPlayerByName(buildMessage.player);
			Worker selectedWorker;
			Turn turn = observedModel.getPhase();

			if (buildMessage.build.workerID == buildingPlayer.getPlayerID()+1) {
				selectedWorker = buildingPlayer.getWorker1();
			} else {
				selectedWorker = buildingPlayer.getWorker2();
			}

			try {
				possibleBuilds.addAll(buildingPlayer.getCard().checkBuild(observedModel.getMap(),selectedWorker,turn));
			} catch (NoBuildException e) {
				// if it is the move phase and none of the gods change the standard way of moving it will be called the standard method
				if (turn.getGamePhase() == GamePhase.BUILD) {
					possibleBuilds = GodCard.standardBuilds(selectedWorker.getPos().map,selectedWorker,turn);
				} else {
					// it can't move and it isn't in the move phase
					caller.communicateError();
				}
			}

			if (possibleBuilds.size() != 0) {
				if (buildingPlayer.isWorkerLocked()) {
					if (buildingPlayer.getActiveWorker().workerID == buildMessage.build.workerID) {
						// the phase is the building phase, for this reason i need to check if
						defeatController.buildDefeat(possibleBuilds);
						if (!buildController.build(buildMessage.build, possibleBuilds)) {
							caller.communicateError();
						} else {
							observedModel.changeTurn();
							observedModel.computeActions();
							if (observedModel.getPlayerPossibleBuilds().size() == 0) {
								observedModel.changeTurn();
								observedModel.computeActions();
								defeatController.moveDefeat(observedModel.getPlayerPossibleMovesWorker1(),observedModel.getPlayerPossibleMovesWorker2());
							}
						}
					} else {
						caller.communicateError();
					}
				} else {
					if (!buildController.build(buildMessage.build, possibleBuilds)) {
						caller.communicateError();
					} else {
						observedModel.applyWorkerLock(buildingPlayer,selectedWorker.workerID-buildingPlayer.getPlayerID());
						observedModel.changeTurn();
						observedModel.computeActions();
						defeatController.moveDefeat(observedModel.getPlayerPossibleMovesWorker1(),observedModel.getPlayerPossibleMovesWorker2());
					}
				}
			}
		}
	}
	/**
	 * This method is called when a player wants to quit the game, it make it quit and changes the model, if the player isn't inside the game this method is going to call the error handling method of the {@link it.polimi.ingsw.network.RemoteView} and stops.
	 * @param observed is the observable object
	 * @param playerName is the player's name
	 */
	@Override
	public synchronized void updateQuit(ObservableObject observed, String playerName) {
		// it controls if the player which sent the request is in its turn and can choose a color
		RemoteView caller = (RemoteView) observed;
		List<String> playerNames = observedModel.getPlayers().stream().map(Player::getPlayerName).collect(Collectors.toList());
		if (observedModel.isFinished() || !playerNames.contains(playerName)) {
			caller.communicateError();
		} else {
			observedModel.applyDisconnection(playerName);
		}
	}
	/**
	 * This method is called when a player which is only observing the match wants to quit the game, it make it quit and changes the model, if the player isn't inside the game as observer an {@code AssertionError} is thrown because there has been an error on handling the observers.
	 * @param observed is the {@link it.polimi.ingsw.util.observers.ObservableRemoteView} which called this method
	 */
	@Override
	public synchronized void observerQuit(ObservableRemoteView observed) {
		try {
			observedModel.removeObserver((ObserverRemoteView)observed);
		} catch (IllegalArgumentException e) {
			throw new AssertionError("The server controller has been called to remove an observer that doesn't exist");
		}
	}
}
