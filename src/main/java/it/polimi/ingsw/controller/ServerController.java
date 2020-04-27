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
import it.polimi.ingsw.network.objects.NetPlayerTurn;
import it.polimi.ingsw.util.exceptions.BadRequestException;
import it.polimi.ingsw.util.exceptions.NoBuildException;
import it.polimi.ingsw.util.exceptions.NoMoveException;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;
import it.polimi.ingsw.util.observers.ObservableObject;
import it.polimi.ingsw.util.observers.ObserverController;

import java.awt.*;
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
	public List<Move> generateStandardMoves(Worker w) {
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
	public List<Build> generateStandardBuilds(Worker w) {
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
	private boolean isMoveablePhase() {
		if (observedModel.getPhase().getGamePhase() == GamePhase.MOVE) {
			return true;
		}
		return false;
	}
	private boolean isBuildablePhase() {
		if (observedModel.getPhase().getGamePhase() == GamePhase.BUILD || observedModel.getPhase().getGamePhase() == GamePhase.BEFOREMOVE) {
			return true;
		}
		return false;
	}
	private void moveDefeat(Player player) {

	}
	private void otherMoveDefeat() {

	}

	// OVERRIDDEN METHODS FROM THE OBSERVER
	@Override
	public synchronized void updateColors(ObservableObject observed, NetColorPreparation playerColors) {
		// it controls if the player which sent the request is in its turn and can choose a color
		RemoteView caller = (RemoteView) observed;
		String activePlayer = observedModel.getPlayerTurn().getPlayerName();
		// if the player is trying to choose a color already chosen or isn't its turn it returns an error
		if (!activePlayer.equals(playerColors.player)) {
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
		if (!activePlayer.equals(playerGods.player)) {
			caller.communicateError();
		} else {
			try {
				if (!setupController.handleGodMessage(playerGods)) {
					observedModel.changeTurn();
				}
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
		if (!activePlayer.equals(netObject.player)) {
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
	@Override
	public synchronized void updateMove(ObservableObject observed, NetPlayerTurn moveMessage) {
		// it controls if the player which sent the request is in its turn and can choose a color
		RemoteView caller = (RemoteView) observed;
		String activePlayer = observedModel.getPlayerTurn().getPlayerName();
		if (!activePlayer.equals(moveMessage.player) || !isMoveablePhase()) {
			caller.communicateError();
		} else {
			List<Move> possibleMoves = new ArrayList<>();
			Player movingPlayer = observedModel.getPlayerByName(moveMessage.player);
			Worker selectedWorker;
			Turn turn = observedModel.getPhase();
			List<GodCard> playersCards = observedModel.getPlayers().stream().filter((player) -> { try { player.getCard(); return true; } catch (IllegalStateException e) { return false; } }).map((player) -> player.getCard()).collect(Collectors.toList());

			if (moveMessage.move.workerID == movingPlayer.getPlayerID()+1) {
				selectedWorker = movingPlayer.getWorker1();
			} else {
				selectedWorker = movingPlayer.getWorker2();
			}
			for (GodCard card : playersCards) {
				if (card.getTypeGod() == TypeGod.OTHER_TURN_GOD) {
					try {
						possibleMoves.addAll(card.checkMove(observedModel.getMap(),selectedWorker,turn));
					} catch (NoMoveException e) {
						throw new AssertionError("Controller called a check move on a god that isn't an other turn god for moves");
					}
				}
			}

			try {
				possibleMoves.addAll(movingPlayer.getCard().checkMove(observedModel.getMap(),selectedWorker,turn));
			} catch (NoMoveException e) {
				// if it is the move phase and none of the gods change the standard way of moving it will be called the standard method
				if (turn.getGamePhase() == GamePhase.MOVE) {
					if (possibleMoves.size() == 0) {
						possibleMoves = generateStandardMoves(selectedWorker);
					}
				} else {
					// it can't move and it isn't in the move phase
					if (possibleMoves.size() == 0) {
						observedModel.changeTurn();
					}
				}
			} finally {
				if (possibleMoves.size() != 0) {
					if (movingPlayer.isWorkerLocked()) {
						if (movingPlayer.getActiveWorker().workerID == moveMessage.move.workerID) {
							if (!moveController.move(moveMessage.move, possibleMoves)) {
								caller.communicateError();
							} else {
								victoryController.checkVictory(selectedWorker.getLastPos(),selectedWorker.getPos(),possibleMoves);
								observedModel.changeTurn();
							}
						} else {
							caller.communicateError();
						}
					} else {
						if (!moveController.move(moveMessage.move, possibleMoves)) {
							caller.communicateError();
						} else {
							victoryController.checkVictory(selectedWorker.getLastPos(),selectedWorker.getPos(),possibleMoves);
							movingPlayer.chooseWorker(moveMessage.move.workerID-movingPlayer.getPlayerID());
							observedModel.changeTurn();
						}
					}
				}
			}
		}
	}
	@Override
	public synchronized void updateBuild(ObservableObject observed, NetPlayerTurn buildMessage) {
		// it controls if the player which sent the request is in its turn and can choose a color
		RemoteView caller = (RemoteView) observed;
		String activePlayer = observedModel.getPlayerTurn().getPlayerName();
		if (!activePlayer.equals(buildMessage.player) || !isBuildablePhase()) {
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
					observedModel.changeTurn();
				}
			} finally {
				if (possibleBuilds.size() != 0) {
					if (buildingPlayer.isWorkerLocked()) {
						if (buildingPlayer.getActiveWorker().workerID == buildMessage.move.workerID) {
							if (!buildController.build(buildMessage.build, possibleBuilds)) {
								caller.communicateError();
							} else {
								otherMoveDefeat();
								observedModel.changeTurn();
							}
						} else {
							caller.communicateError();
						}
					} else {
						if (!buildController.build(buildMessage.build, possibleBuilds)) {
							caller.communicateError();
						} else {
							otherMoveDefeat();
							buildingPlayer.chooseWorker(buildMessage.move.workerID-buildingPlayer.getPlayerID());
							observedModel.changeTurn();
						}
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
		if (!playerNames.contains(playerName)) {
			caller.communicateError();
		} else {
			observedModel.applyDisconnection(playerName);
		}
	}

	// TODO: is that necessary?
	@Override
	public Turn givePhase() {
		return null;
	}
	@Override
	public NetAvailablePositions giveAvailablePositions() {
		return null;
	}
	@Override
	public NetAvailableBuildings giveAvailableBuildings() {
		return null;
	}
}
