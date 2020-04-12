package it.polimi.ingsw.network;

// necessary imports from other packages of the project
import it.polimi.ingsw.util.ObservableObject;
import it.polimi.ingsw.util.ObserverObjectRemoteView;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;

// necessary imports of Java SE
import java.io.*;

/**
 * This class is the class which receives input from client's view and forward these commands to the controller, after the controller changed something (on the model) this class is notified cause it observes the model and it notifies its observers. It is observed by the controller, this means that when it receives a valid message from the client it notifies the controller 'cause something is changed (also if none of its attributes is changed, it's changed the status) and it will do what it is programmed to.
 */
public class RemoteView extends ObservableObject implements ObserverObjectRemoteView {
	private ServerClientListenerThread clientHandler;

	public RemoteView(ServerClientListenerThread handler) throws NullPointerException {
		if (handler == null) {
			throw new NullPointerException();
		}
		clientHandler = handler;
	}

	// METHODS FOR THE GAME SETUP

	// TODO: remove old files
	/**
	 * This method communicate with player to get players color
	 */
	/*private void setupColors() throws IOException {
		List<Color> colors = new ArrayList<>();
		String[] playersNames = new String[gamers.size()];
		NetColorPreparation receivedObject = null;
		NetColorPreparation sendObject = null;

		// it iterates over players to make them choose their color contacting them by the socket
		for (int playerDone = 0; playerDone < gamers.size(); playerDone++) {
			try {
				sendObject = new NetColorPreparation(Constants.PREP_COLOR_YOU);
				connections.get(playerDone).getSecond().writeObject(sendObject);
				connections.get(playerDone).getSecond().flush();
				receivedObject = (NetColorPreparation) connections.get(playerDone).getFirst().readObject();
				if (receivedObject.getMessage().equals(Constants.PREP_COLOR_CHOICE) && Constants.PREP_COLOR_COLORS.contains(receivedObject.getColor()) && (colors.size() == 0 || !colors.contains(receivedObject.getColor()))) {
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
		// it creates the game, view and controller
		for (int i = 0; i < gamers.size(); i++) {
			playersNames[i] = gamers.get(i).getSecond();
		}
		serverGame = new Game(playersNames,(Color[])colors.toArray());
		serverController = new ServerController(serverGame);
		this.addObserver(serverController);
		serverGame.addObserver(this);
	}*/

	@Override
	public void updateDefeat(Object playerDefeated) throws NullPointerException, WrongPhaseException {

	}
	@Override
	public void updateWinner(Object playerWinner) throws NullPointerException, WrongPhaseException {

	}
	@Override
	public void updateOrder(Object[] order) throws NullPointerException, WrongPhaseException {
		/*	// builds the sequence object of players to send to player
		for (int i = gamers.size()-1; i >= 0; i--) {
			if (i == gamers.size()-1) {
				netFirst = new NetOrderPreparation(Constants.PREP_TURN,gamers.get(i).getSecond(),i);
			} else {
				netFirst = new NetOrderPreparation(Constants.PREP_TURN,gamers.get(i).getSecond(),i,netFirst);
			}
		}
		// sends all the player the ordered list of players in the game
		for (int i = 0; i < gamers.size(); i++) {
			connections.get(i).getSecond().writeObject(netFirst);
			connections.get(i).getSecond().flush();
		}*/
	}
	@Override
	public void updateColors(Object playerColors) throws IllegalArgumentException, WrongPhaseException {

	}
	@Override
	public void updateGods(Object playerGods) throws IllegalArgumentException, WrongPhaseException {

	}
	@Override
	public void updatePositions(Object netObject, boolean finished) throws WrongPhaseException {

	}
	@Override
	public void updateMove(Object netMap) throws NullPointerException, WrongPhaseException {

	}
	@Override
	public void updateBuild(Object netMap) throws NullPointerException, WrongPhaseException {

	}
}
