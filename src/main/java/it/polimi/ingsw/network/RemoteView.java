package it.polimi.ingsw.network;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.core.Map;
import it.polimi.ingsw.core.gods.GodCard;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.game.NetMap;
import it.polimi.ingsw.network.objects.*;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.observers.ObservableGame;
import it.polimi.ingsw.util.observers.ObservableObject;
import it.polimi.ingsw.util.observers.ObservableRemoteView;
import it.polimi.ingsw.util.observers.ObserverRemoteView;

// necessary imports of Java SE
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// TODO: insert the possibility to observe the match when you lost and the other two players are playing [MEANING TO ADD THE NETOBSERVER OBJECT AND HANDLE EVENTS]
/**
 * This class is the class which receives input from client's view and forward these commands to the controller, after the controller changed something (on the model) this class is notified cause it observes the model and it notifies its observers. It is observed by the controller, this means that when it receives a valid message from the client it notifies the controller 'cause something is changed (also if none of its attributes is changed, it's changed the status) and it will do what it is programmed to.
 */
public class RemoteView extends ObservableRemoteView implements ObserverRemoteView {
	private final ServerClientListenerThread clientHandler;
	private int playersNum;

	public RemoteView(ServerClientListenerThread handler) throws NullPointerException {
		if (handler == null) {
			throw new NullPointerException();
		}
		clientHandler = handler;
		playersNum = 1;
	}

	// METHODS USED TO INFORM THE PLAYER ABOUT AN ERROR
	/**
	 * This function is called from the controller when user send a well formed request he cannot send for some reason: it isn't its turn or he cannot because of game state
	 */
	public void communicateError() {

	}

	// METHODS CALLED BY THE CLIENTS WHEN TRYING TO DO A POSSIBLE ACTION REGARDING TO THE PHASE
	/**
	 * It receiver a well formed request of color, meaning that the color is one of the three available colors, the remote view cannot know if the color is already owned by another player or not, for this reason notifies the ServerController. If it receives a callback (a call of this function by the observer) with error true it sends an error to the client, because he couldn't do that action.
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
	public void handleMoveRequest(NetPlayerTurn req, boolean error) {
		if (!error) {
			notifyMove(req);
		} else {
			clientHandler.sendMessage(new NetPlayerTurn(Constants.PLAYER_ERROR));
		}
	}
	/**
	 * It receives a well formed request of build, well formed means that the cell is inside the map, it is needed to check if the build can be done and if all parameters are correct. If it receives a callback (a call of this function by the observer) with error true it sends an error to the client, because he couldn't do that action.
	 * @param req represent the message sent by the client
	 * @param error represent that an error occurred with the request and is called by the controller observer with true to set that it has to reply to the client with an error
	 */
	public void handleBuildRequest(NetPlayerTurn req, boolean error) {
		if (!error) {
			notifyBuild(req);
		} else {
			clientHandler.sendMessage(new NetPlayerTurn(Constants.PLAYER_ERROR));
		}
	}

	// METHODS USED TO INFORM THE CONTROLLER ABOUT A REQUEST OF THE CLIENT
	@Override
	public synchronized void updateDefeat(ObservableGame observed, String playerDefeated) {
		if (observed == null || playerDefeated == null) {
			clientHandler.fatalError("");
		} else {
			if (clientHandler.getGamePhase() == NetworkPhase.PLAYERTURN) {
				NetPlayerTurn yourMessage = new NetPlayerTurn(Constants.GENERAL_DEFEATED, playerDefeated);
			} else if (clientHandler.getGamePhase() == NetworkPhase.OTHERTURN) {
				NetOtherTurn otherMessage = new NetOtherTurn(Constants.GENERAL_DEFEATED, playerDefeated);
			}
			playersNum--;

			if (clientHandler.getPlayerName().equals(playerDefeated)) {
				clientHandler.setGamePhase(NetworkPhase.OBSERVER);
			}
		}
	}
	@Override
	public synchronized void updateWinner(ObservableGame observed, String playerWinner) {
		if (observed == null || playerWinner == null) {
			clientHandler.fatalError("");
		} else {
			if (clientHandler.getGamePhase() == NetworkPhase.PLAYERTURN) {
				NetPlayerTurn yourMessage = new NetPlayerTurn(Constants.GENERAL_WINNER, playerWinner);
			} else if (clientHandler.getGamePhase() == NetworkPhase.OTHERTURN) {
				NetOtherTurn otherMessage = new NetOtherTurn(Constants.GENERAL_WINNER, playerWinner);
			}
		}
	}
	/**
	 * It receives the change of the game state, it receives the order and communicate it to the client
	 * @param observed observable game that called this method
	 * @param order an array with players' order
	 */
	@Override
	public synchronized void updateOrder(ObservableGame observed, String[] order) {
		if (observed == null || order == null || clientHandler.getGamePhase() != NetworkPhase.LOBBY) {
			clientHandler.fatalError("It has been called the notify of player's order in the wrong phase or maybe with a null parameter");
		} else {
			playersNum = order.length;
			NetLobbyPreparation sendOrder = null;
			// builds the sequence object of players to send to player
			for (int i = 0; i < order.length; i++) {
				if (i == 0) {
					sendOrder = new NetLobbyPreparation(Constants.LOBBY_TURN, order[i], i + 1);
				} else {
					sendOrder = new NetLobbyPreparation(Constants.LOBBY_TURN, order[i], i + 1, sendOrder);
				}
			}
			// it communicated to the client the play order
			clientHandler.sendMessage(sendOrder);
			clientHandler.setGamePhase(NetworkPhase.COLORS);

			// it says to the player if it has to choose the color or if it has to wait the others choice
			NetColorPreparation colorMessage;
			if (order[0].equals(clientHandler.getPlayerName())) {
				colorMessage = new NetColorPreparation(Constants.COLOR_YOU);
			} else {
				colorMessage = new NetColorPreparation(Constants.COLOR_OTHER);
			}
			clientHandler.sendMessage(colorMessage);
		}
	}
	/**
	 * When this method is called it informs the player about the color chosen by the other players, if all players have chosen card it change the handler phase and says to the client that he enters in the god selection phase
	 * @param observed observable game that called this method
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

			Game caller = (Game) observed;
			NetDivinityChoice divinityChoice = null;
			// if the players that have chosen the color number is the same as the number of all players in the lobby it must change the phase to gods selection
			if (playerColors.size() == playersNum) {
				clientHandler.setGamePhase(NetworkPhase.GODS);
				if (caller.getPlayerTurn().getPlayerName().equals(clientHandler.getPlayerName())) {
					// the player is the challenger and it is informed about that
					divinityChoice = new NetDivinityChoice(Constants.GODS_CHALLENGER);
				} else {
					// the player isn't the challenger and it is informed about that
					divinityChoice = new NetDivinityChoice(Constants.GODS_OTHER);
				}
			}
			clientHandler.sendMessage(divinityChoice);
		}
	}
	/**
	 * This function says to all players the gods chosen by the challenger for this game
	 * @param observed observable game that called this method
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

			Game caller = (Game) observed;
			// if the player is the first in clockwise order from the challenger it says to it to select a godCard
			if (caller.getPlayers().get(1).getPlayerName().equals(clientHandler.getPlayerName())) {
				godsMessage = new NetDivinityChoice(Constants.GODS_YOU);
			} else {
				godsMessage = new NetDivinityChoice(Constants.GODS_OTHER);
			}
			clientHandler.sendMessage(godsMessage);
		}
	}
	/**
	 * This function says to the player all gods selected till now by the players
	 * @param observed observable game that called this method
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

			Game caller = (Game) observed;
			// if the players that have chosen the color number is the same as the number of all players in the lobby it must change the phase to gods selection
			if (godsInfo.size() == playersNum) {
				NetDivinityChoice divinityChoice = null;
				if (caller.getPlayerTurn().getPlayerName().equals(clientHandler.getPlayerName())) {
					// the player is the challenger and it is informed about that
					divinityChoice = new NetDivinityChoice(Constants.GODS_CHOOSE_STARTER);
				}
				clientHandler.sendMessage(divinityChoice);
			}
		}
	}
	/**
	 * This function says to the player the player who starts to position its workers on the game board
	 * @param observed observable game that called this method
	 * @param godsInfo it contains the information about the player that start to position workers
	 */
	@Override
	public synchronized void updateGods(ObservableObject observed, String godsInfo) {
		if (observed == null || godsInfo == null || clientHandler.getGamePhase() != NetworkPhase.GODS) {
			clientHandler.fatalError("The gods update on a wrong phase or with wrong parameters");
		} else {
			NetDivinityChoice godsMessage = new NetDivinityChoice(Constants.GODS_STARTER,godsInfo);
			clientHandler.sendMessage(godsMessage);
			clientHandler.setGamePhase(NetworkPhase.SETUP);
		}
	}
	@Override
	public synchronized void updatePositions(ObservableGame observed, Map gameMap, boolean finished) {
		if (observed == null || gameMap == null) {
			clientHandler.fatalError("The positions update is called with null parameters");
		} else {
			NetGameSetup gameSetupMessage = new NetGameSetup(Constants.GENERAL_GAMEMAP_UPDATE,new NetMap(gameMap));
			clientHandler.sendMessage(gameSetupMessage);

			if (finished) {
				if (((Game)observed).getPlayerTurn().getPlayerName().equals(clientHandler.getPlayerName())) {
					clientHandler.setGamePhase(NetworkPhase.PLAYERTURN);
				} else {
					clientHandler.setGamePhase(NetworkPhase.OTHERTURN);
				}
			}
		}
	}
	@Override
	public synchronized void updateMove(ObservableObject observed, Map netMap) {
		if (observed == null || netMap != null) {
			clientHandler.fatalError("It is called the move update with wrong or null parameters");
		} else {
			if (clientHandler.getGamePhase() == NetworkPhase.PLAYERTURN) {
				NetPlayerTurn yourMessage = new NetPlayerTurn(Constants.GENERAL_GAMEMAP_UPDATE, new NetMap((Map)netMap));
			} else if (clientHandler.getGamePhase() == NetworkPhase.OTHERTURN) {
				NetOtherTurn otherMessage = new NetOtherTurn(Constants.GENERAL_GAMEMAP_UPDATE, new NetMap((Map)netMap));
			}
		}
	}
	@Override
	public synchronized void updateBuild(ObservableObject observed, Map netMap) {
		if (observed == null || netMap != null) {
			clientHandler.fatalError("It is called the move update with wrong or null parameters");
		} else {
			if (clientHandler.getGamePhase() == NetworkPhase.PLAYERTURN) {
				NetPlayerTurn yourMessage = new NetPlayerTurn(Constants.GENERAL_GAMEMAP_UPDATE, new NetMap((Map)netMap));
			} else if (clientHandler.getGamePhase() == NetworkPhase.OTHERTURN) {
				NetOtherTurn otherMessage = new NetOtherTurn(Constants.GENERAL_GAMEMAP_UPDATE, new NetMap((Map)netMap));
			}
		}
	}
	@Override
	public synchronized void updateQuit(ObservableObject observed, String playerName) {
		if (observed == null || playerName == null) {
			clientHandler.fatalError("It is called the quit of a player with a null parameter");
		} else {
			if (clientHandler.getGamePhase() == NetworkPhase.PLAYERTURN) {
				NetPlayerTurn yourMessage = new NetPlayerTurn(Constants.GENERAL_PLAYER_DISCONNECTED, playerName);
			} else if (clientHandler.getGamePhase() == NetworkPhase.OTHERTURN) {
				NetOtherTurn otherMessage = new NetOtherTurn(Constants.GENERAL_PLAYER_DISCONNECTED, playerName);
			}
			playersNum--;
		}
	}
	@Override
	public void updatePhaseChange(ObservableGame observed, Turn turn) {

	}

	/**
	 * This function contacts the player to say him he must perform an action because is its turn
	 * @param observed observable game that called this method
	 * @param playerName the name of the player that should play at this moment of the game
	 */
	@Override
	public void updateActivePlayer(ObservableGame observed, String playerName) {
		if (observed == null || playerName == null) {
			clientHandler.fatalError("It has been called the update on active player with a null parameter");
		} else {
			if (playerName.equals(clientHandler.getPlayerName())) {
				switch (clientHandler.getGamePhase()) {
					case COLORS -> {
						NetColorPreparation colorPhase = new NetColorPreparation(Constants.COLOR_YOU);
						clientHandler.sendMessage(colorPhase);
					}
					case GODS -> {
						NetDivinityChoice godsPhase = new NetDivinityChoice(Constants.GODS_YOU);
						clientHandler.sendMessage(godsPhase);
					}
					case SETUP -> {
						NetGameSetup setupPhase = new NetGameSetup(Constants.GAMESETUP_PLACE);
						clientHandler.sendMessage(setupPhase);
					}
					case PLAYERTURN -> {
						clientHandler.fatalError("It became the player's turn and the handler thinks it is the same as before");
					}
					case OTHERTURN -> {
						NetOtherTurn otherPlayersTurn = new NetOtherTurn(Constants.OTHERS_FINISHED);
						clientHandler.sendMessage(otherPlayersTurn);
					}
				}
			} else {
				if (clientHandler.getGamePhase() == NetworkPhase.PLAYERTURN) {
					clientHandler.setGamePhase(NetworkPhase.OTHERTURN);
				}
			}
		}
	}
}
