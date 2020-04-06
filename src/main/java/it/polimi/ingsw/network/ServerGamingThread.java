package it.polimi.ingsw.network;

// necessary imports from other packages of the project
import it.polimi.ingsw.controller.ServerController;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetOrderPreparation;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.Pair;

// necessary imports of Java SE
import java.awt.Color;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * This class is the class which receives input from client's view and forward these commands to the controller, after the controller changed something (on the model) this class is notified cause it observes the model and it notifies its observers. It is observed by the controller, this means that when it receives a valid message from the client it notifies the controller 'cause something is changed (also if none of its attributes is changed, it's changed the status) and it will do what it is programmed to.
 */
public class ServerGamingThread extends Observable implements Runnable, Observer {
	private List<Pair<Socket,String>> gamers = new ArrayList<>();
	private List<Pair<ObjectInputStream, ObjectOutputStream>> connections = new ArrayList<>();
	private Game serverGame;
	private ServerController serverController;

	public ServerGamingThread(List<Pair<Socket,String>> connections) {
		gamers.addAll(connections);
	}

	@Override
	public void run() {
		try {
			generateOrder();
			setupColors();
			divinityChooseCards();
			playerChooseCards();
		} catch (IOException e) {
			// TODO: put an exception handling
			System.exit(1);
		}
	}

	// SETUP METHODS FOR THE GAME
	/**
	 * This method generate random order of play in the game choosing the first player randomly
	 * @throws IOException It throws an exception it it isn't possible to get input or output streams
	 */
	private void generateOrder() throws IOException {
		int playerNumber, random;
		List<Pair<Socket, String>> temp = new ArrayList<>(gamers);
		NetOrderPreparation netFirst = null;

		playerNumber = temp.size();
		gamers.clear();
		// generate random order
		while (gamers.size() < playerNumber) {
			if (temp.size() > 1) {
				// find a random player to add to the gamers list
				random = (int) (Math.random() * (double) temp.size()-1);
				gamers.add(temp.get(random));
				connections.add(new Pair<ObjectInputStream, ObjectOutputStream>(new ObjectInputStream(gamers.get(gamers.size()-1).getFirst().getInputStream()), new ObjectOutputStream(gamers.get(gamers.size()-1).getFirst().getOutputStream())));
				temp.remove(random);
			} else {
				// adds the last player
				gamers.add(temp.get(0));
			}
		}
		// builds the sequence object of players to send to player
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
		}
	}
	/**
	 * This method communicate with player to get players color
	 */
	private void setupColors() throws IOException {
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
	}
	/**
	 * This is the function which let the first player to choose which cards to play
	 */
	private void divinityChooseCards() {

	}
	private void playerChooseCards() {

	}

	@Override
	public void update(Observable o, Object arg) {

	}
}
