package it.polimi.ingsw.network;

// necessary imports from other packages of the project
import it.polimi.ingsw.controller.ServerController;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.util.MultipleList;
import it.polimi.ingsw.util.exceptions.AlreadyStartedException;
import it.polimi.ingsw.util.exceptions.FirstPlayerException;

// necessary imports of Java SE
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * This class is the server class and it never ends, it manages the lobby creation and creation of threads which duty is to manage games, it creates ServerClientListenerThread class to communicate with clients and this class expose Thread safe method to modify the state of the lobby
 */
public class Server implements Runnable {
	private MultipleList<ServerClientListenerThread, String> lobbyClients;
	private int lobbyDimension;
	private final Object lobbyLock;
	// TODO: implement a queue to define the order of arrive

	public Server() {
		lobbyDimension = 0;
		lobbyClients = new MultipleList<>();
		lobbyLock = new Object();
	}

	@Override
	public void run() {
		Socket receivedConnection;
		boolean functioning = true;

		// here the server builds the pre-game lobby where players wait to start the game
		ServerSocket serverSocket;
		try {
			// we assign a port to the server not registered to other services: https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.txt
			serverSocket = new ServerSocket(21005);
		} catch (IOException e) {
			throw new AssertionError("There has been an error while opening the socket");
		}

		while (true) {
			try {
				receivedConnection = serverSocket.accept();
				new ServerClientListenerThread(receivedConnection,this).start();
			} catch (IOException e) {
				throw new AssertionError("There has been an error with receiving connection from the server");
			}
		}
	}

	/** This method adds a player to the lobby if the lobby already exists, if not it creates the lobby with temporary dimension of 1 and throws a FirstPlayerException which says to the executing thread that is has to ask to the player
	 * @param name it is the name that the player would like to use in this game
	 * @param handler it is the client messages handler
	 * @return 0 if the player can't be added because there is already a player with the same name or name parameter is null, 1 if the player has been added to a match, 2 the game is starting
	 * @throws FirstPlayerException if this exception is thrown it means that the player must choose the number of player and let the server to create the lobby
	 */
	public int addPlayer(String name, ServerClientListenerThread handler) throws FirstPlayerException {
		synchronized (lobbyLock) {
			if (name == null) {
				return  0;
			}
			while (lobbyDimension == 1) {
				try {
					lobbyLock.wait();
				} catch (InterruptedException e) {
					// TODO: ask to the tutors how to handle this exception
				}
			}

			// if there aren't player it creates the lobby, otherwise it add the player to the lobby
			if (lobbyDimension == 0) {
				lobbyClients.add(handler,name);
				lobbyDimension = 1;
				throw new FirstPlayerException();
			} else {
				if (lobbyClients.containsValue(name)) {
					return 0;
				}

				// the player doesn't exist already and it is going to be added to the lobby
				if (lobbyClients.size() < lobbyDimension-1) {
					lobbyClients.add(handler,name);
					return 1;
				} else {
					lobbyClients.add(handler,name);
					new Thread(() -> createGame()).start();
					return 1;
				}
			}
		}
	}
	/**
	 * This method remove a player because it has gone offline for some reason and isn't connected
	 * @param name it is the name that the player would like to use in this game
	 * @param handler it is the client messages handler
	 * @throws AlreadyStartedException if the match is already started this exception is thrown
	 */
	public void removePlayer(String name, ServerClientListenerThread handler) throws AlreadyStartedException {
		synchronized (lobbyLock) {
			if (!lobbyClients.containsValue(name)) {
				throw new AlreadyStartedException();
			} else {
				if (lobbyDimension == 1) {
					lobbyDimension--;
					lobbyLock.notifyAll();
				}
				lobbyClients.removeByValue(name);
			}
		}
	}
	/**
	 * It sets the number of player of the game chosen by the first player who connected to the server
	 * @param dimension This is the number of player for the game
	 * @param handler This is the thread that is calling the function
	 * @throws IllegalCallerException The thread that called this method isn't the one that hold the connection for the first player
	 * @throws IllegalArgumentException The dimension passed is different from 2 or 3
	 */
	public void setPlayerNumber(int dimension, ServerClientListenerThread handler) throws IllegalCallerException, IllegalArgumentException {
		synchronized (lobbyLock) {
			if (!lobbyClients.containsKey(handler)) {
				throw new IllegalCallerException();
			} else {
				if (dimension == 2 || dimension == 3) {
					lobbyDimension = dimension;
					lobbyLock.notifyAll();
				} else {
					lobbyDimension = 0;
					lobbyClients = new MultipleList<>();
					throw new IllegalArgumentException();
				}
			}
		}
	}
	/**
	 * This function return the client position in the lobby
	 * @param handler This is the handler for a client
	 * @return return the position in the lobby for that client
	 * @throws IllegalCallerException The thread that called this method isn't a thread which represent a client
	 */
	public int getClientPosition(ServerClientListenerThread handler) throws IllegalCallerException {
		synchronized (lobbyLock) {
			if (!lobbyClients.containsKey(handler)) {
				throw new IllegalCallerException();
			}
			return lobbyClients.getIndexByKey(handler);
		}
	}
	/**
	 * This function return if the lobby needs the user input for the player number
	 * @return return true if the lobby needs the user input for the player number
	 */
	public boolean getToBeCreated() {
		synchronized (lobbyLock) {
			return lobbyDimension == 1;
		}
	}
	/**
	 * This method create a game where player are going to play, it creates a RemoteView for each client and connects it to its handler, at the end it calls the method generateOrder() on the controller in order to create a playing order.
	 */
	private void createGame() {
		try {
			Game game = new Game((String[])lobbyClients.getValueList().toArray());
			ServerController controller = new ServerController(game);
			for (int i = 0; i < lobbyClients.size(); i++) {
				RemoteView remoteView = new RemoteView(lobbyClients.getKey(i));
				lobbyClients.getKey(i).setGamePhase(1);
				lobbyClients.getKey(i).setGameServer(remoteView);
				remoteView.addObserver(controller);
				game.addObserver(remoteView);
				controller.generateOrder();
				// TODO: think how to inform threads about the start of the Game
			}
			lobbyClients = new MultipleList<>();
			lobbyDimension = 0;
		} catch (NullPointerException e) {
			for (ServerClientListenerThread handler : lobbyClients.getKeyList()) {
				handler.fatalError();
			}
			lobbyClients = new MultipleList<>();
			lobbyDimension = 0;
			throw new AssertionError("Something's gone wrong and the server tried to create a gaming server with null list of players or handlers");
		}
	}
}