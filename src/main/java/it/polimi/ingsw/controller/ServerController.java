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

public class ServerController implements ObserverController {
	private final Game observedModel;
	private final Mover moveController;
	private final Builder buildController;
	private final DefeatManager defeatController;
	private final VictoryManager victoryController;
	private final SetupManager setupController;

	// constructors and setters for this class
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
	public void generateOrder() {
		setupController.generateOrder();
	}
	private List<Move> generateStandardMoves(Worker w) {
		Map gameMap = observedModel.getMap();
		int y = gameMap.getX(w.getPos());
		int x = gameMap.getY(w.getPos());

		List<Move> moves = new ArrayList<>();
		for(int i = -1; i <= 1; i++) {   //i->x   j->y     x1, y1 all the cells where I MAY move
			int x1 = x + i;
			for(int j = -1; j <= 1; j++){
				int y1 = y + j;

				if(x != x1 || y != y1){ //I shall not move where I am already
					if(0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4){   //Check that I am inside the map
						if(gameMap.getCell(x1, y1).getBuilding().getLevel() - gameMap.getCell(x, y).getBuilding().getLevel() <= 1){
							if(!gameMap.getCell(x1, y1).getBuilding().getDome()){   //Check there is NO dome
								if (gameMap.getCell(x1, y1).getWorker() == null) {   //Check there isn't any worker on the cell
									moves.add(new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(x, y), gameMap.getCell(x1, y1), w));
								}
							}
						}
					}
				}
			}
		}
		return moves;
	}
	private List<Build> generateStandardBuilds(Worker w) {
		Map gameMap = observedModel.getMap();
		int y = gameMap.getX(w.getPos());
		int x = gameMap.getY(w.getPos());

		List<Build> builds = new ArrayList<>();
		for(int i = -1; i <= 1; i++) {   //i->x   j->y     x1, y1 all the cells where I MAY build
			int x1 = x + i;
			for (int j = -1; j <= 1; j++) {
				int y1 = y + j;

				if (x != x1 || y != y1) { //I shall not build where I am
					if (0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4) {   //Check that I am inside the map
						if (gameMap.getCell(x1, y1).getWorker() == null) {   //Check there isn't any worker on the cell
							if (!gameMap.getCell(x1, y1).getBuilding().getDome()) {   //Check there is NO dome
								if(gameMap.getCell(x1, y1).getBuilding().getLevel() <= 2) {
									builds.add(new Build(w, gameMap.getCell(x1, y1), false, TypeBuild.SIMPLE_BUILD)); // simple increment level
								} else if(gameMap.getCell(x1, y1).getBuilding().getLevel() == 3) {
									builds.add(new Build(w, gameMap.getCell(x1, y1), true, TypeBuild.SIMPLE_BUILD)); // dome on a 3 level building
								}
							}
						}
					}
				}
			}
		}
		return builds;
	}

	// support methods
	private boolean isMovablePhase() {
		return observedModel.getPhase().getGamePhase() == GamePhase.MOVE;
	}
	private boolean isBuildablePhase() {
		if (observedModel.getPhase().getGamePhase() == GamePhase.BUILD || observedModel.getPhase().getGamePhase() == GamePhase.BEFOREMOVE) {
			return true;
		}
		return false;
	}
	private boolean moveDefeat(Player player) {
		List<Move> worker1Moves = new ArrayList<>();
		List<Move> worker2Moves = new ArrayList<>();
		worker1Moves.addAll(generateWorkerMoves(player,1));
		worker2Moves.addAll(generateWorkerMoves(player,2));
		return defeatController.moveDefeat(worker1Moves,worker2Moves);
	}
	private boolean buildDefeat() {
		Player buildingPlayer = observedModel.getPlayerTurn();
		Worker activeWorker = buildingPlayer.getActiveWorker();
		Turn nextTurn = observedModel.getPhase();
		nextTurn.advance();
		List<Build> builds = new ArrayList<>();
		try {
			builds.addAll(buildingPlayer.getCard().checkBuild(observedModel.getMap(),activeWorker,nextTurn));
		} catch (NoBuildException e) {
			builds.addAll(generateStandardBuilds(activeWorker));
		}
		return defeatController.buildDefeat(builds);
	}
	/**
	 * This function returns possible moves of a worker only if it is the phase move part of the turn, if not it doesn't return
	 * @param player
	 * @param worker
	 * @return
	 * @throws NoMoveException
	 */
	private List<Move> generateWorkerMoves(Player player, int worker) {
		List<Move> moves = new ArrayList<>();
		Turn activeTurn = observedModel.getPhase();
		if (worker == 1) {
			try {
				if (player.getCard().getTypeGod() != TypeGod.OTHER_TURN_GOD) {
					moves.addAll(player.getCard().checkMove(observedModel.getMap(),player.getWorker1(),activeTurn));
				} else {
					moves.addAll(generateStandardMoves(player.getWorker1()));
				}
			} catch (NoMoveException e) {
				if (activeTurn.getGamePhase() == GamePhase.MOVE) {
					moves.addAll(generateStandardMoves(player.getWorker1()));
				}
			}
		} else {
			try {
				if (player.getCard().getTypeGod() != TypeGod.OTHER_TURN_GOD) {
					moves.addAll(player.getCard().checkMove(observedModel.getMap(), player.getWorker2(),activeTurn));
				} else {
					moves.addAll(generateStandardMoves(player.getWorker1()));
				}
			} catch (NoMoveException e) {
				if (activeTurn.getGamePhase() == GamePhase.MOVE) {
					moves.addAll(generateStandardMoves(player.getWorker2()));
				}
			}
		}
		for (Player otherPlayer : observedModel.getPlayers()) {
			if (otherPlayer != observedModel.getPlayerTurn() && otherPlayer.getCard().getTypeGod() == TypeGod.OTHER_TURN_GOD && activeTurn.getGamePhase() == GamePhase.MOVE) {
				try {
					moves.addAll(otherPlayer.getCard().checkMove(observedModel.getMap(),player.getWorker1(),activeTurn));
					moves.addAll(otherPlayer.getCard().checkMove(observedModel.getMap(),player.getWorker2(),activeTurn));
				} catch (NoMoveException e) {
					throw new AssertionError("Controller called a check move on a god that isn't an other turn god for moves");
				}
			}
		}
		return moves;
	}

	// OVERRIDDEN METHODS FROM THE OBSERVER
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
			} catch (BadRequestException e) {
				caller.communicateError();
			}
		}
	}
	/**
	 * The player can pass the turn if he doesn't want to use the power in the before move phase, this happens when he has prometheus and he doesn't want to build before moving
	 * @param observed
	 */
	@Override
	public void updatePass(ObservableRemoteView observed, String playerName) {
		RemoteView caller = (RemoteView) observed;
		String activePlayer = observedModel.getPlayerTurn().getPlayerName();
		if (observedModel.isFinished() || !activePlayer.equals(playerName) || observedModel.getPhase().getGamePhase() != GamePhase.BEFOREMOVE) {
			caller.communicateError();
		} else {
			observedModel.changeTurn();
		}
	}
	/**
	 * This function is called when a player can move and want to perform a certain move, if it can't move this function will never be called
	 * @param observed
	 * @param moveMessage
	 */
	@Override
	public synchronized void updateMove(ObservableObject observed, NetGaming moveMessage) {
		// it controls if the player which sent the request is in its turn and can choose a color
		RemoteView caller = (RemoteView) observed;
		String activePlayer = observedModel.getPlayerTurn().getPlayerName();
		if (observedModel.isFinished() || !activePlayer.equals(moveMessage.player) || !isMovablePhase()) {
			caller.communicateError();
		} else {
			List<Move> possibleMoves = new ArrayList<>();
			Player movingPlayer = observedModel.getPlayerByName(moveMessage.player);
			Worker selectedWorker;
			Turn turn = observedModel.getPhase();
			List<GodCard> playersCards = observedModel.getPlayers().stream().filter((player) -> { try { player.getCard(); return true; } catch (IllegalStateException e) { return false; } }).map((player) -> player.getCard()).collect(Collectors.toList());
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
				possibleMoves.addAll(movingPlayer.getCard().checkMove(observedModel.getMap(),selectedWorker,turn));
				hasMoves = true;
			} catch (NoMoveException e) {
				// if it is the move phase and none of the gods change the standard way of moving it will be called the standard method
				if (turn.getGamePhase() == GamePhase.MOVE) {
					possibleMoves.addAll(generateStandardMoves(selectedWorker));
					hasMoves = true;
				} else {
					// it can't move and it isn't in the move phase
					caller.communicateError();
				}
			}

			if (hasMoves) {
				if (movingPlayer.isWorkerLocked()) {
					if (movingPlayer.getActiveWorker().workerID == moveMessage.move.workerID) {
						if (!moveController.move(moveMessage.move, possibleMoves)) {
							caller.communicateError();
						} else {
							victoryController.checkVictory(selectedWorker.getLastPos(),selectedWorker.getPos(),possibleMoves);
							if (!buildDefeat()) {
								observedModel.changeTurn();
							}
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
						if (!buildDefeat()) {
							observedModel.changeTurn();
						}
					}
				}
			}
		}
	}
	/**
	 * This method is called when a client send a build command to the server, if so it is possible for it to build somewhere, if not it is a loser and this function will never be called.
	 * @param observed
	 * @param buildMessage
	 */
	@Override
	public synchronized void updateBuild(ObservableObject observed, NetGaming buildMessage) {
		// it controls if the player which sent the request is in its turn and can choose a color
		RemoteView caller = (RemoteView) observed;
		String activePlayer = observedModel.getPlayerTurn().getPlayerName();
		if (observedModel.isFinished() || !activePlayer.equals(buildMessage.player) || !isBuildablePhase()) {
			caller.communicateError();
		} else {
			List<Build> possibleBuilds = new ArrayList<>();
			Player buildingPlayer = observedModel.getPlayerByName(buildMessage.player);
			Worker selectedWorker;
			Turn turn = observedModel.getPhase();

			if (buildMessage.move.workerID == buildingPlayer.getPlayerID()+1) {
				selectedWorker = buildingPlayer.getWorker1();
			} else {
				selectedWorker = buildingPlayer.getWorker2();
			}

			try {
				possibleBuilds.addAll(buildingPlayer.getCard().checkBuild(observedModel.getMap(),selectedWorker,turn));
			} catch (NoBuildException e) {
				// if it is the move phase and none of the gods change the standard way of moving it will be called the standard method
				if (turn.getGamePhase() == GamePhase.BUILD) {
					possibleBuilds = generateStandardBuilds(selectedWorker);
				} else {
					// it can't move and it isn't in the move phase
					caller.communicateError();
				}
			}

			if (possibleBuilds.size() != 0) {
				if (buildingPlayer.isWorkerLocked()) {
					if (buildingPlayer.getActiveWorker().workerID == buildMessage.move.workerID) {
						// the phase is the building phase, for this reason i need to check if
						defeatController.buildDefeat(possibleBuilds);
						if (!buildController.build(buildMessage.build, possibleBuilds)) {
							caller.communicateError();
						} else {
							observedModel.changeTurn();
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
					}
				}
			}
		}
	}
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
	@Override
	public synchronized void observerQuit(ObservableRemoteView observed) {
		try {
			observedModel.removeObserver((ObserverRemoteView)observed);
		} catch (IllegalArgumentException e) {
			throw new AssertionError("The server controller has been called to remove an observer that doesn't exist");
		}
	}

	@Override
	public Turn givePhase() {
		return observedModel.getPhase();
	}
	@Override
	public NetAvailablePositions giveAvailablePositions() {
		NetAvailablePositions possibleMoves;
		List<Move> generatedMoves;

		if (observedModel.getPhase().getGamePhase() == GamePhase.MOVE) {
			moveDefeat(observedModel.getPlayerTurn());
		}
		generatedMoves = generateWorkerMoves(observedModel.getPlayerTurn(),1);
		generatedMoves.addAll(generateWorkerMoves(observedModel.getPlayerTurn(),2));
		generatedMoves = DefeatManager.filterMoves(generatedMoves);
		possibleMoves = new NetAvailablePositions(generatedMoves);
		if (generatedMoves.size() == 0) {
			return null;
		}
		return possibleMoves;
	}
	@Override
	public NetAvailableBuildings giveAvailableBuildings() {
		NetAvailableBuildings possibleBuilds;
		List<Build> builds = new ArrayList<>();

		if (observedModel.getPhase().getGamePhase() == GamePhase.BUILD) {
			buildDefeat();
		}
		if (observedModel.getPhase().getGamePhase() == GamePhase.BEFOREMOVE) {
			try {
				builds.addAll(observedModel.getPlayerTurn().getCard().checkBuild(observedModel.getMap(),observedModel.getPlayerTurn().getWorker1(),observedModel.getPhase()));
				builds.addAll(observedModel.getPlayerTurn().getCard().checkBuild(observedModel.getMap(),observedModel.getPlayerTurn().getWorker2(),observedModel.getPhase()));
				possibleBuilds = new NetAvailableBuildings(builds);
			} catch (NoBuildException e) {
				builds = null;
				possibleBuilds = null;
				observedModel.changeTurn();
			}
		} else {
			try {
				builds.addAll(observedModel.getPlayerTurn().getCard().checkBuild(observedModel.getMap(),observedModel.getPlayerTurn().getActiveWorker(),observedModel.getPhase()));
			} catch (NoBuildException e) {
				builds.addAll(generateStandardBuilds(observedModel.getPlayerTurn().getActiveWorker()));
			}
			possibleBuilds = new NetAvailableBuildings(builds);
		}
		if (builds != null && builds.size() == 0) {
			throw new AssertionError("The function has been called by the remote view when the player cannot build because has loose");
		}
		return possibleBuilds;
	}
}
