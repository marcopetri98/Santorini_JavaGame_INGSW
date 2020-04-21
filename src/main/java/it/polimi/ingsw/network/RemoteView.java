package it.polimi.ingsw.network;

// necessary imports from other packages of the project
import it.polimi.ingsw.network.objects.*;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.observers.ObservableRemoteView;
import it.polimi.ingsw.util.observers.ObserverRemoteView;

// necessary imports of Java SE

/**
 * This class is the class which receives input from client's view and forward these commands to the controller, after the controller changed something (on the model) this class is notified cause it observes the model and it notifies its observers. It is observed by the controller, this means that when it receives a valid message from the client it notifies the controller 'cause something is changed (also if none of its attributes is changed, it's changed the status) and it will do what it is programmed to.
 */
public class RemoteView extends ObservableRemoteView implements ObserverRemoteView {
	private final ServerClientListenerThread clientHandler;
	private boolean hasChanged;
	private boolean result;
	private final Object controllerResponseLock;

	public RemoteView(ServerClientListenerThread handler) throws NullPointerException {
		if (handler == null) {
			throw new NullPointerException();
		}
		clientHandler = handler;
		hasChanged = false;
		result = false;
		controllerResponseLock = new Object();
	}

	// TODO: translate old methods in new methods
	/*private void setupColors() throws IOException {
		List<Color> colors = new ArrayList<>();
		String[] playersNames = new String[gamers.size()];
		NetColorPreparation receivedObject = null;
		NetColorPreparation sendObject = null;

		// it iterates over players to make them choose their color contacting them by the socket
		for (int playerDone = 0; playerDone < gamers.size(); playerDone++) {
			try {
				-- sendObject = new NetColorPreparation(Constants.PREP_COLOR_YOU);
				-- connections.get(playerDone).getSecond().writeObject(sendObject);
				-- connections.get(playerDone).getSecond().flush();
				## receivedObject = (NetColorPreparation) connections.get(playerDone).getFirst().readObject();
				if (receivedObject.message.equals(Constants.PREP_COLOR_CHOICE) && Constants.PREP_COLOR_COLORS.contains(receivedObject.getColor()) && (colors.size() == 0 || !colors.contains(receivedObject.getColor()))) {
					colors.add(receivedObject.getColor());
					sendObject = new NetColorPreparation(Constants.PREP_COLOR_SUCCESS);
					connections.get(playerDone).getSecond().writeObject(sendObject);
					connections.get(playerDone).getSecond().flush();
				} else {
					sendObject = new NetColorPreparation(Constants.PREP_COLOR_ERROR,1);
					connections.get(playerDone).getSecond().writeObject(sendObject);
					connections.get(playerDone).getSecond().flush();
					playerDone--;
				}
			} catch (ClassNotFoundException e) {
				sendObject = new NetColorPreparation(Constants.PREP_COLOR_ERROR,2);
				connections.get(playerDone).getSecond().writeObject(sendObject);
				connections.get(playerDone).getSecond().flush();
				playerDone--;
			}
		}
		// it builds the object used to alert players of other players' color
		for (int playerDone = gamers.size()-1; playerDone >= 0; playerDone--) {
			if (playerDone == gamers.size()-1) {
				sendObject = new NetColorPreparation(Constants.PREP_COLOR_OTHER_CHOICE, gamers.get(playerDone).getSecond(), colors.get(playerDone));
			} else {
				sendObject = new NetColorPreparation(Constants.PREP_COLOR_OTHER_CHOICE, gamers.get(playerDone).getSecond(), colors.get(playerDone),sendObject);
			}
		}
		// it alerts other players of other's color
		for (int playerDone = 0; playerDone < gamers.size(); playerDone++) {
			connections.get(playerDone).getSecond().writeObject(sendObject);
			connections.get(playerDone).getSecond().flush();
		}
	}*/

	// METHODS CALLED BY THE CLIENTS WHEN TRYING TO DO A POSSIBLE ACTION REGARDING TO THE PHASE
	/**
	 * It receiver a well formed request of color, meaning that the color is one of the three available colors, the remote view cannot know if the color is already owned by another player or not, for this reason notifies the ServerController
	 * @param req represent the message sent by the client
	 */
	public void handleColorRequest(NetColorPreparation req) {
		notifyColors(req);
		waitUntilControllerReplies();
		if (!result) {
			clientHandler.sendMessage(new NetColorPreparation(Constants.COLOR_ERROR));
		}
	}
	/**
	 * It receives a well formed request of divinity choice, meaning that the divinity exists or all divinity exists and there aren't duplicates if the player is the challenger and is choosing the gods to play with
	 * @param req represent the message sent by the client
	 */
	public void handleDivinityRequest(NetDivinityChoice req) {

	}
	/**
	 * It receives a well formed request of positioning of workers, when with well formed we mean that the position is inside the map
	 * @param req represent the message sent by the client
	 */
	public void handlePositionRequest(NetGameSetup req) {

	}
	/**
	 * It receives a well formed request of a move, well formed means that the cell is inside the map, it is needed to check worker and if is possible to move there
	 * @param req represent the message sent by the client
	 */
	public void handleMoveRequest(NetPlayerTurn req) {

	}
	/**
	 * It receives a well formed request of build, well formed means that the cell is inside the map, it is needed to check if the build can be done and if all parameters are correct
	 * @param req represent the message sent by the client
	 */
	public void handleBuildRequest(NetPlayerTurn req) {

	}

	// SUPPORT METHODS
	private void waitUntilControllerReplies() {
		synchronized (controllerResponseLock) {
			while (!hasChanged) {
				try {
					controllerResponseLock.wait();
				} catch (InterruptedException e) {
					// TODO: implement a better way to handle interruptedException
					throw new AssertionError("Error of interruption");
				}
			}
			hasChanged = false;
		}
	}

	// METHODS USED TO INFORM THE CONTROLLER ABOUT A REQUEST OF THE CLIENT
	@Override
	public void getControllerResult(boolean result) {
		synchronized (controllerResponseLock) {
			hasChanged = true;
			this.result = result;
			controllerResponseLock.notifyAll();
		}
	}
	@Override
	public void updateDefeat(Object playerDefeated) {

	}
	@Override
	public void updateWinner(Object playerWinner) {

	}
	@Override
	public void updateOrder(Object[] order) {
		if (order == null || clientHandler.getGamePhase() != NetworkPhase.LOBBY) {
			clientHandler.fatalError("It has been called the notify of player's order in the wrong phase or maybe with a null parameter");
		}

		String[] playerNames = (String[]) order;
		NetLobbyPreparation sendOrder = null;
		// builds the sequence object of players to send to player
		for (int i = 0; i < order.length; i++) {
			if (i == 0) {
				sendOrder = new NetLobbyPreparation(Constants.LOBBY_TURN,playerNames[i],i+1);
			} else {
				sendOrder = new NetLobbyPreparation(Constants.LOBBY_TURN,playerNames[i],i+1,sendOrder);
			}
		}
		// it communicated to the client the play order
		clientHandler.sendMessage(sendOrder);
		clientHandler.setGamePhase(NetworkPhase.COLORS);


		// it says to the player if it has to choose the color or if it has to wait the others choice
		NetColorPreparation colorMessage;
		if (playerNames[0].equals(clientHandler.getPlayerName())) {
			colorMessage = new NetColorPreparation(Constants.COLOR_YOU);
		} else {
			colorMessage = new NetColorPreparation(Constants.COLOR_OTHER);
		}
		clientHandler.sendMessage(colorMessage);
	}
	@Override
	public void updateColors(Object playerColors) {
	}
	@Override
	public void updateGods(Object playerGods) {
	}
	@Override
	public void updatePositions(Object netObject, boolean finished) {
	}
	@Override
	public void updateMove(Object netMap) {
	}
	@Override
	public void updateBuild(Object netMap) {
	}
	@Override
	public void updateQuit(String playerName) {

	}
}
