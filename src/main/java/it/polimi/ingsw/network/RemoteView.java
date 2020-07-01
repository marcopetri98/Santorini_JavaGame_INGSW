package it.polimi.ingsw.network;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.*;
import it.polimi.ingsw.core.gods.GodCard;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.game.NetAvailableBuildings;
import it.polimi.ingsw.network.game.NetAvailablePositions;
import it.polimi.ingsw.network.game.NetMap;
import it.polimi.ingsw.network.objects.*;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.observers.ObservableGame;
import it.polimi.ingsw.util.observers.ObservableObject;
import it.polimi.ingsw.util.observers.ObservableRemoteView;
import it.polimi.ingsw.util.observers.ObserverRemoteView;

// necessary imports of Java SE
import it.polimi.ingsw.util.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is the remote view class of the Distributed MVC pattern, it receives input from client's view and forward these commands to the controller with light controls. After the controller has changed something (on the model) this class is notified because it observes the model and the model notifies its observers. It is observed by the controller, this means that when it receives a valid message from the client it notifies the controller because something is changed (also if none of its attributes is changed, it's changed the status).
 */
public class RemoteView extends ObservableRemoteView implements ObserverRemoteView {
	private final ServerClientListenerThread clientHandler;

	/**
	 * It creates a new remote view connected with the passed server handler for the client.
	 * @param handler is the server handler for the client
	 * @throws NullPointerException it handler is null
	 */
	public RemoteView(ServerClientListenerThread handler) throws NullPointerException {
		if (handler == null) {
			throw new NullPointerException();
		}
		clientHandler = handler;
	}

	// METHODS USED TO INFORM THE PLAYER ABOUT AN ERROR
	/**
	 * This function is called from the controller when user send a well formed request he cannot send for some reason: it isn't its turn or he cannot perform this command because of game state.
	 */
	public void communicateError() {
		switch (clientHandler.getGamePhase()) {
			case COLORS -> clientHandler.sendMessage(new NetColorPreparation(Constants.COLOR_ERROR));
			case GODS -> clientHandler.sendMessage(new NetDivinityChoice(Constants.GODS_ERROR));
			case SETUP -> clientHandler.sendMessage(new NetGameSetup(Constants.GAMESETUP_ERROR));
			case PLAYERTURN -> clientHandler.sendMessage(new NetGaming(Constants.PLAYER_ERROR));
			case OTHERTURN -> clientHandler.sendMessage(new NetGaming(Constants.OTHERS_ERROR));
		}
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *	METHODS CALLED TO HANDLE CLIENT REQUESTS	*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	// METHODS CALLED BY THE CLIENTS WHEN TRYING TO DO A POSSIBLE ACTION REGARDING TO THE PHASE
	/**
	 * It receives a well formed request of color, meaning that the color is one of the three available colors, the remote view cannot know if the color is already owned by another player or not, for this reason notifies the ServerController. If it may receive a callback (a call of this function by the observer) with error true it sends an error to the client, because he couldn't do that action.
	 * @param req represent the message sent by the client
	 * @param error represent that an error occurred with the request and is called by the controller observer with true to set that it has to reply to the client with an error
	 */
	public void handleColorRequest(NetColorPreparation req, boolean error) {
		if (!error) {
			notifyColors(req);
		} else {
			clientHandler.sendMessage(new NetColorPreparation(Constants.COLOR_ERROR));
		}
	}
	/**
	 * It receives a well formed request of divinity choice, meaning that the divinity exists or all divinity exists and there aren't duplicates if the player is the challenger and is choosing the gods to play with. If it receives a callback (a call of this function by the observer) with error true it sends an error to the client, because he couldn't do that action.
	 * @param req represent the message sent by the client
	 * @param error represent that an error occurred with the request and is called by the controller observer with true to set that it has to reply to the client with an error
	 */
	public void handleDivinityRequest(NetDivinityChoice req, boolean error) {
		if (!error) {
			notifyGods(req);
		} else {
			clientHandler.sendMessage(new NetDivinityChoice(Constants.GODS_ERROR));
		}
	}
	/**
	 * It receives a well formed request of positioning of workers, when with well formed we mean that the position is inside the map. If it receives a callback (a call of this function by the observer) with error true it sends an error to the client, because he couldn't do that action.
	 * @param req represent the message sent by the client
	 * @param error represent that an error occurred with the request and is called by the controller observer with true to set that it has to reply to the client with an error
	 */
	public void handlePositionRequest(NetGameSetup req, boolean error) {
		if (!error) {
			notifyPositions(req);
		} else {
			clientHandler.sendMessage(new NetGameSetup(Constants.GAMESETUP_ERROR));
		}
	}
	/**
	 * It receives a well formed request of a move, well formed means that the cell is inside the map, it is needed to check worker and if is possible to move there. If it receives a callback (a call of this function by the observer) with error true it sends an error to the client, because he couldn't do that action.
	 * @param req represent the message sent by the client
	 * @param error represent that an error occurred with the request and is called by the controller observer with true to set that it has to reply to the client with an error
	 */
	public void handleMoveRequest(NetGaming req, boolean error) {
		if (!error) {
			notifyMove(req);
		} else {
			clientHandler.sendMessage(new NetGaming(Constants.PLAYER_ERROR));
		}
	}
	/**
	 * It receives a well formed request of build, well formed means that the cell is inside the map, it is needed to check if the build can be done and if all parameters are correct. If it receives a callback (a call of this function by the observer) with error true it sends an error to the client, because he couldn't do that action.
	 * @param req represent the message sent by the client
	 * @param error represent that an error occurred with the request and is called by the controller observer with true to set that it has to reply to the client with an error
	 */
	public void handleBuildRequest(NetGaming req, boolean error) {
		if (!error) {
			notifyBuild(req);
		} else {
			clientHandler.sendMessage(new NetGaming(Constants.PLAYER_ERROR));
		}
	}
	/**
	 * A player that is an observer has sent a request to quit the game, the request is forwarded to the controller.
	 */
	public void handleObserverQuit() {
		if (clientHandler.getGamePhase() != NetworkPhase.OBSERVER) {
			notifyObserverQuit();
			removeAllObservers();
		} else {
			throw new AssertionError("It has been called by the ClientListenerThread the quit of an observer in a different phase from the observing");
		}
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *	UPDATE OF THE GAME TO SEND TO THE CLIENT	*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	// METHODS USED TO INFORM THE CONTROLLER ABOUT A REQUEST OF THE CLIENT
	/**
	 * This method sends the message of defeat to the player connected with this remote view.
	 * @param observed is the observable game
	 * @param playerDefeated is the player defeated
	 */
	@Override
	public synchronized void updateDefeat(ObservableGame observed, String playerDefeated) {
		if (observed == null || playerDefeated == null) {
			clientHandler.fatalError("");
		} else {
			NetGaming yourMessage = new NetGaming(Constants.GENERAL_DEFEATED, playerDefeated);
			clientHandler.sendMessage(yourMessage);

			if (clientHandler.getPlayerName().equals(playerDefeated)) {
				clientHandler.setGamePhase(NetworkPhase.OBSERVER);
			}
		}
	}
	/**
	 * This method sends the message of win to the player connected with this remote view.
	 * @param observed is the observable game
	 * @param playerWinner is the winner player
	 */
	@Override
	public synchronized void updateWinner(ObservableGame observed, String playerWinner) {
		if (observed == null || playerWinner == null) {
			clientHandler.fatalError("");
		} else {
			clientHandler.setGamePhase(NetworkPhase.END);
			NetGaming yourMessage = new NetGaming(Constants.GENERAL_WINNER, playerWinner);
			clientHandler.sendMessage(yourMessage);
			clientHandler.closeSocketAndTerminate();
			observed.removeObserver(this);
		}
	}
	/**
	 * This method sends the message with the order of play.
	 * @param observed is the observable game
	 * @param order an array with players' order
	 */
	@Override
	public synchronized void updateOrder(ObservableGame observed, String[] order) {
		if (observed == null || order == null || clientHandler.getGamePhase() != NetworkPhase.LOBBY) {
			clientHandler.fatalError("It has been called the notify of player's order in the wrong phase or maybe with a null parameter");
		} else {
			NetLobbyPreparation sendOrder = null;
			// builds the sequence object of players to send to player
			for (int i = 0; i < order.length; i++) {
				if (i == 0) {
					sendOrder = new NetLobbyPreparation(Constants.LOBBY_TURN, order[order.length-1-i], order.length-i);
				} else {
					sendOrder = new NetLobbyPreparation(Constants.LOBBY_TURN, order[order.length-1-i], order.length-i, sendOrder);
				}
			}
			// it communicated to the client the play order
			clientHandler.sendMessage(sendOrder);
		}
	}
	/**
	 * When this method is called it informs the player about the color chosen by the other players, if all players have chosen card it change the handler phase and says to the client that he enters in the god selection phase
	 * @param observed is the observable game
	 * @param playerColors an HashMap that matches every player with the selected color
	 */
	@Override
	public synchronized void updateColors(ObservableGame observed, HashMap<String, Color> playerColors) {
		if (observed == null || playerColors == null || clientHandler.getGamePhase() != NetworkPhase.COLORS || playerColors.size() == 0) {
			clientHandler.fatalError("It has been called the notify of players' color with null parameter or in the wrong phase");
		} else {
			NetColorPreparation colorMessage = null;
			int i = 0;
			// it builds the entire list about players' colors
			for (String playerName : playerColors.keySet()) {
				if (i == 0) {
					colorMessage = new NetColorPreparation(Constants.COLOR_CHOICES,playerName,playerColors.get(playerName));
					i++;
				} else {
					colorMessage = new NetColorPreparation(Constants.COLOR_CHOICES,playerName,playerColors.get(playerName),colorMessage);
				}
			}
			clientHandler.sendMessage(colorMessage);
		}
	}
	/**
	 * This function says to all players the gods chosen by the challenger for this game
	 * @param observed is the observable game
	 * @param godsInfo it represent the list of gods that are chosen by the challenger
	 */
	@Override
	public synchronized void updateGods(ObservableObject observed, List<GodCard> godsInfo) {
		if (observed == null || godsInfo == null || clientHandler.getGamePhase() != NetworkPhase.GODS) {
			clientHandler.fatalError("The gods update on a wrong phase or with wrong parameters");
		} else {
			List<String> godsNames = new ArrayList<>();
			for (GodCard card : godsInfo) {
				godsNames.add(card.getName());
			}
			NetDivinityChoice godsMessage = new NetDivinityChoice(Constants.GODS_GODS,godsNames);
			clientHandler.sendMessage(godsMessage);
		}
	}
	/**
	 * This function says to the player all gods selected till now by the players
	 * @param observed is the observable game
	 * @param godsInfo it represent the gods chosen by the current player turn
	 */
	@Override
	public synchronized void updateGods(ObservableObject observed, HashMap<String,GodCard> godsInfo) {
		if (observed == null || godsInfo == null || clientHandler.getGamePhase() != NetworkPhase.GODS) {
			clientHandler.fatalError("The gods update on a wrong phase or with wrong parameters");
		} else {
			NetDivinityChoice godsMessage = null;
			int i = 0;
			// it builds the entire list about players' colors
			for (String playerName : godsInfo.keySet()) {
				if (i == 0) {
					godsMessage = new NetDivinityChoice(Constants.GODS_CHOICES,playerName,godsInfo.get(playerName).getName(),false);
					i++;
				} else {
					godsMessage = new NetDivinityChoice(Constants.GODS_CHOICES,playerName,godsInfo.get(playerName).getName(),godsMessage);
				}
			}
			clientHandler.sendMessage(godsMessage);
		}
	}
	/**
	 * This function says to the player the player who starts to position its workers on the game board
	 * @param observed is the observable game
	 * @param godsInfo it contains the information about the player that start to position workers
	 */
	@Override
	public synchronized void updateGods(ObservableObject observed, String godsInfo) {
		if (observed == null || godsInfo == null || clientHandler.getGamePhase() != NetworkPhase.GODS) {
			clientHandler.fatalError("The gods update on a wrong phase or with wrong parameters");
		} else {
			NetDivinityChoice godsMessage = new NetDivinityChoice(Constants.GODS_STARTER,godsInfo);
			clientHandler.sendMessage(godsMessage);
		}
	}
	/**
	 * This method sends a message to the client with the information about the updated map.
	 * @param observed is the observable game
	 * @param gameMap is the game map
	 * @param finished is a boolean which says if the position phase is finished
	 */
	@Override
	public synchronized void updatePositions(ObservableGame observed, Map gameMap, boolean finished) {
		if (observed == null || gameMap == null) {
			clientHandler.fatalError("The positions update is called with null parameters");
		} else {
			NetGameSetup gameSetupMessage = new NetGameSetup(Constants.GENERAL_GAMEMAP_UPDATE,new NetMap(gameMap));
			clientHandler.sendMessage(gameSetupMessage);
		}
	}
	/**
	 * This method sends a message to the client with the information about the updated map.
	 * @param observed is the observable game
	 * @param netMap is the game map
	 */
	@Override
	public synchronized void updateMove(ObservableObject observed, Map netMap) {
		if (observed == null || netMap == null) {
			clientHandler.fatalError("It is called the move update with wrong or null parameters");
		} else {
			NetGaming yourMessage = new NetGaming(Constants.GENERAL_GAMEMAP_UPDATE, new NetMap((Map)netMap));
			clientHandler.sendMessage(yourMessage);
		}
	}
	/**
	 * This method sends a message to the client with the information about the updated map.
	 * @param observed is the observable game
	 * @param netMap is the game map
	 */
	@Override
	public synchronized void updateBuild(ObservableObject observed, Map netMap) {
		if (observed == null || netMap == null) {
			clientHandler.fatalError("It is called the move update with wrong or null parameters");
		} else {
			NetGaming yourMessage = new NetGaming(Constants.GENERAL_GAMEMAP_UPDATE, new NetMap((Map)netMap));
			clientHandler.sendMessage(yourMessage);
		}
	}
	/**
	 * Sends the information to the client about a player that has disconnected.
	 * @param observed is the observable game
	 * @param playerName is the name of the player who quit the game
	 */
	@Override
	public synchronized void updateQuit(ObservableObject observed, String playerName) {
		if (observed == null || playerName == null) {
			clientHandler.fatalError("It is called the quit of a player with a null parameter");
		} else {
			NetGaming yourMessage = new NetGaming(Constants.GENERAL_PLAYER_DISCONNECTED, playerName);
			clientHandler.sendMessage(yourMessage);
		}
	}
	/**
	 * This method sends the information about the current game phase.
	 * @param observed is the observable game
	 * @param turn is the current game turn
	 */
	@Override
	public void updatePhaseChange(ObservableGame observed, Turn turn) {
		if (observed == null || turn == null) {
			clientHandler.fatalError("It has been called the update phase with wrong parameters");
		} else {
			switch (clientHandler.getGamePhase()) {
				case LOBBY -> {
					clientHandler.setGamePhase(NetworkPhase.COLORS);
					clientHandler.sendMessage(new NetLobbyPreparation(Constants.GENERAL_PHASE_UPDATE));
				}
				case COLORS -> {
					clientHandler.setGamePhase(NetworkPhase.GODS);
					clientHandler.sendMessage(new NetColorPreparation(Constants.GENERAL_PHASE_UPDATE));

					Game caller = (Game) observed;
					NetDivinityChoice divinityChoice = new NetDivinityChoice(Constants.GODS_CHALLENGER, caller.getPlayers().get(0).getPlayerName());
					clientHandler.sendMessage(divinityChoice);
				}
				case GODS ->  {
					// if the players that have chosen the color number is the same as the number of all players in the lobby it must change the phase to gods selection
					if (turn.getPhase() == Phase.GODS && turn.getGodsPhase() == GodsPhase.STARTER_CHOICE) {
						clientHandler.sendMessage(new NetDivinityChoice(Constants.GENERAL_PHASE_UPDATE,false));
					} else if (turn.getPhase() == Phase.GODS && turn.getGodsPhase() == GodsPhase.GODS_CHOICE) {
						clientHandler.sendMessage(new NetDivinityChoice(Constants.GENERAL_PHASE_UPDATE,false));
					} else if (turn.getPhase() == Phase.SETUP) {
						clientHandler.setGamePhase(NetworkPhase.SETUP);
						clientHandler.sendMessage(new NetDivinityChoice(Constants.GENERAL_PHASE_UPDATE,true));
					}
				}
				case SETUP ->  {
					clientHandler.setGamePhase(NetworkPhase.OTHERTURN);
					clientHandler.sendMessage(new NetGameSetup(Constants.GENERAL_PHASE_UPDATE));
				}
				case PLAYERTURN, OTHERTURN ->  {
					clientHandler.sendMessage(new NetGaming(Constants.GENERAL_PHASE_UPDATE));
				}
			}
		}
	}

	/**
	 * This method sends the information about the possible action of the current player in this game phase.
	 * @param observed is the observable game
	 * @param moves is a list of moves
	 * @param builds is a list of builds
	 */
	@Override
	public void updatePossibleActions(ObservableGame observed, List<Move> moves, List<Build> builds) {
		if (observed == null) {
			clientHandler.fatalError("It has been called the update on active player with a null parameter");
		} else {
			NetAvailableBuildings possibleBuilds = new NetAvailableBuildings(builds);
			NetAvailablePositions possibleMoves = new NetAvailablePositions(moves);

			Game caller = (Game) observed;
			clientHandler.sendMessage(new NetGaming(Constants.PLAYER_ACTIONS,caller.getPlayerTurn().getPlayerName(),possibleBuilds,possibleMoves));
		}
	}

	/**
	 * This method sends the information about the active player turn and changes the network phase of the ServerClientListenerThread.
	 * @param observed is the observable game
	 * @param playerName the name of the player that should play at this moment of the game
	 */
	@Override
	public void updateActivePlayer(ObservableGame observed, String playerName) {
		if (observed == null || playerName == null) {
			clientHandler.fatalError("It has been called the update on active player with a null parameter");
		} else {
			switch (clientHandler.getGamePhase()) {
				case COLORS -> {
					NetColorPreparation colorPhase = new NetColorPreparation(Constants.TURN_PLAYERTURN, playerName);
					clientHandler.sendMessage(colorPhase);
				}
				case GODS -> {
					NetDivinityChoice godsPhase = new NetDivinityChoice(Constants.TURN_PLAYERTURN, playerName);
					clientHandler.sendMessage(godsPhase);
				}
				case SETUP -> {
					NetGameSetup setupPhase = new NetGameSetup(Constants.TURN_PLAYERTURN, playerName);
					clientHandler.sendMessage(setupPhase);
				}
				case PLAYERTURN, OTHERTURN, OBSERVER -> {
					if (clientHandler.getPlayerName().equals(playerName)) {
						clientHandler.setGamePhase(NetworkPhase.PLAYERTURN);
					} else {
						clientHandler.setGamePhase(NetworkPhase.OTHERTURN);
					}
					NetGaming othersEndTurn = new NetGaming(Constants.TURN_PLAYERTURN, playerName);
					clientHandler.sendMessage(othersEndTurn);
				}
			}
		}
	}
	/**
	 * This method sends the message to the client that the game is finished because someone has disconnected during the setup.
	 * @param observed is the observable game
	 */
	@Override
	public void updateGameFinished(ObservableGame observed) {
		if (observed == null || clientHandler.getGamePhase() == NetworkPhase.PLAYERTURN || clientHandler.getGamePhase() == NetworkPhase.OTHERTURN) {
			throw new AssertionError("The update method has been called with a null parameter or not during the setup");
		} else {
			switch (clientHandler.getGamePhase()) {
				case LOBBY -> clientHandler.sendMessage(new NetLobbyPreparation(Constants.GENERAL_SETUP_DISCONNECT));
				case COLORS -> clientHandler.sendMessage(new NetColorPreparation(Constants.GENERAL_SETUP_DISCONNECT));
				case GODS -> clientHandler.sendMessage(new NetDivinityChoice(Constants.GENERAL_SETUP_DISCONNECT));
				case SETUP -> clientHandler.sendMessage(new NetGameSetup(Constants.GENERAL_SETUP_DISCONNECT));
			}
			clientHandler.closeSocketAndTerminate();
			observed.removeObserver(this);
		}
	}
}
