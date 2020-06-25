package it.polimi.ingsw.network;

// necessary imports from other packages of the project
import it.polimi.ingsw.controller.ServerController;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.network.objects.NetLobbyPreparation;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.exceptions.AlreadyStartedException;
import it.polimi.ingsw.util.exceptions.FirstPlayerException;

// necessary imports of Java SE
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * This class is the server class and it never ends, it manages the lobby creation and creation of threads which duty is to manage games, it creates ServerClientListenerThread class to communicate with clients and this class expose Thread safe method to modify the state of the lobby.
 */
public class Server implements Runnable {
	private final Map<ServerClientListenerThread, String> lobbyClients;
	private final List<ServerClientListenerThread> preparedListeners;
	private Map<ServerClientListenerThread, String> previousLobby;
	private ServerClientListenerThread creator;
	boolean starting;
	private int lobbyDimension;
	// TODO: implement a queue to define the order of arrive

	public Server() {
		lobbyDimension = -1;
		previousLobby = new LinkedHashMap<>();
		preparedListeners = new ArrayList<>();
		lobbyClients = new LinkedHashMap<>();
		creator = null;
		starting = false;
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

		while (functioning) {
			try {
				receivedConnection = serverSocket.accept();
				try {
					new ServerClientListenerThread(receivedConnection, this).start();
				} catch (IOException e) {
					functioning = true;
				}
			} catch (IOException e) {
				throw new AssertionError("There has been an error with receiving connection from the server");
			}
		}
	}

	/** This method adds a player to the lobby if the lobby already exists, if not it creates the lobby with temporary dimension of 1 and throws a FirstPlayerException which says to the executing thread that is has to ask to the player.
	 * @param name it is the name that the player would like to use in this game
	 * @param handler it is the client messages handler
	 * @return 0 if the player can't be added because there is already a player with the same name or name parameter is null, 1 if the player has been added to a match, 2 the game is starting
	 * @throws FirstPlayerException if this exception is thrown it means that the player must choose the number of player and let the server to create the lobby
	 */
	public int addPlayer(String name, ServerClientListenerThread handler) throws FirstPlayerException {
		synchronized (lobbyClients) {
			if (name == null) {
				return  0;
			}
			while (lobbyDimension == 1 || lobbyClients.size() == lobbyDimension) {
				try {
					lobbyClients.wait();
				} catch (InterruptedException e) {
					//TODO: see every interrupted exception and implement a more elegant way to handle them
					throw new AssertionError("Thread was interrupted and the code never interrupts it");
				}
			}

			// TODO: say to all other players name and other players inside the lobby
			// if there aren't player it creates the lobby, otherwise it add the player to the lobby
			if (lobbyDimension == -1) {
				lobbyClients.put(handler,name);
				lobbyDimension = 1;
				creator = handler;
				throw new FirstPlayerException();
			} else {
				if (lobbyClients.containsValue(name)) {
					return 0;
				}

				// the player doesn't exist already and it is going to be added to the lobby
				if (lobbyClients.size() < lobbyDimension-1) {
					lobbyClients.put(handler,name);
					return 1;
				} else {
					lobbyClients.put(handler,name);
					starting = true;
					new Thread(this::createGame).start();
					return 2;
				}
			}
		}
	}
	/**
	 *It updates players about the current number of players inside the lobby.
	 */
	public void updatePlayerInLobby() {
		synchronized (lobbyClients) {
			NetLobbyPreparation lobbyInfo = null;
			String[] playerNames = lobbyClients.values().toArray(new String[1]);

			// builds the object indicating number of players inside the lobby to send to players (in this way the list is in the reverse order)
			for (int i = 0; i < playerNames.length && playerNames[i] != null; i++) {
				if (i == 0) {
					lobbyInfo = new NetLobbyPreparation(Constants.LOBBY_INFO, playerNames[i], playerNames.length-i);
				} else {
					lobbyInfo = new NetLobbyPreparation(Constants.LOBBY_INFO, playerNames[i], playerNames.length-i, lobbyInfo);
				}
			}
			// sends to all users inside the lobby a message containing every player inside the lobby
			for (ServerClientListenerThread handler : lobbyClients.keySet()) {
				handler.sendMessage(lobbyInfo);
			}
		}
	}
	/**
	 * It store the handler in a list of prepared handlers, it means that the handler and the player connected to it are ready to start a game.
	 * @param handler an handler of a client that is now prepared
	 * @throws IllegalCallerException if the handler is not an handler of a player of the game
	 * @throws IllegalStateException if the handler is already saved in the preparedListeners list
	 */
	public void isNowPrepared(ServerClientListenerThread handler) throws IllegalCallerException, IllegalStateException {
		synchronized (lobbyClients) {
			if (!lobbyClients.containsKey(handler)) {
				throw new IllegalCallerException();
			}
		}
		synchronized (preparedListeners) {
			if (preparedListeners.contains(handler)) {
				throw new IllegalStateException();
			} else {
				preparedListeners.add(handler);
				preparedListeners.notifyAll();
			}
		}
	}
	/**
	 * This method remove a player because it has gone offline for some reason or it has disconnected.
	 * @param name it is the name that the player would like to use in this game
	 * @param handler it is the client messages handler
	 * @throws AlreadyStartedException if the match is already started this exception is thrown
	 */
	public void removePlayer(String name, ServerClientListenerThread handler) throws AlreadyStartedException, IllegalArgumentException {
		synchronized (lobbyClients) {
			if (!lobbyClients.containsValue(name)) {
				if (previousLobby.containsKey(handler)) {
					throw new AlreadyStartedException();
				} else {
					throw new IllegalArgumentException();
				}
			} else {
				if (starting) {
					try {
						lobbyClients.wait();
					} catch (InterruptedException e) {
						//TODO: see every interrupted exception and implement a more elegant way to handle them
						throw new AssertionError("Thread was interrupted and the code never interrupts it");
					}
					throw new AlreadyStartedException();
				}

				if (creator == handler) {
					creator = null;
				}
				synchronized (preparedListeners) {
					if (preparedListeners.contains(handler)) {
						preparedListeners.remove(handler);
						preparedListeners.notifyAll();
					}
				}
				lobbyClients.remove(handler);
				lobbyClients.notifyAll();
				if (lobbyDimension == 1 || lobbyClients.size() == 0) {
					lobbyDimension = -1;
				}
				updatePlayerInLobby();
			}
		}
	}
	/**
	 * It sets the number of players of the game chosen by the first player who connected to the server, it can only be called by the handler of the first player of the lobby.
	 * @param dimension This is the number of player for the game
	 * @param handler This is the thread that is calling the function
	 * @throws IllegalCallerException The thread that called this method isn't the one that hold the connection for the first player
	 * @throws IllegalArgumentException The dimension passed is different from 2 or 3
	 */
	public void setPlayerNumber(int dimension, ServerClientListenerThread handler) throws IllegalCallerException, IllegalArgumentException, IllegalStateException {
		synchronized (lobbyClients) {
			if (!lobbyClients.containsKey(handler) || creator != handler) {
				throw new IllegalCallerException();
			} else if (lobbyDimension == 2 || lobbyDimension == 3) {
				throw new IllegalStateException();
			} else {
				if (dimension == 2 || dimension == 3) {
					lobbyDimension = dimension;
					lobbyClients.notifyAll();
				} else {
					preparedListeners.clear();
					lobbyClients.clear();
					lobbyDimension = -1;
					creator = null;
					throw new IllegalArgumentException();
				}
			}
		}
	}
	/**
	 * Returns the client position in the lobby
	 * @param handler This is the handler for a client
	 * @return return the position in the lobby for that client
	 * @throws IllegalCallerException The thread that called this method isn't a thread which represent a client
	 */
	public int getClientPosition(ServerClientListenerThread handler) throws IllegalCallerException {
		synchronized (lobbyClients) {
			if (!lobbyClients.containsKey(handler)) {
				throw new IllegalCallerException();
			}
			return new ArrayList<>(lobbyClients.keySet()).indexOf(handler);
		}
	}
	/**
	 * Returns if the lobby needs the user input for the player number
	 * @return return true if the lobby needs the user input for the player number
	 */
	public boolean getToBeCreated() {
		synchronized (lobbyClients) {
			return lobbyDimension == 1;
		}
	}

	/**
	 * Returns the dimension of the lobby
	 * @return dimension of the lobby
	 */
	public int getLobbyDimension() {
		synchronized (lobbyClients) {
			return lobbyDimension;
		}
	}
	/**
	 * This method create a game where players are going to play, it creates a RemoteView for each client and connects it to its handler, at the end it calls the method generateOrder() on the controller in order to create a playing order.
	 */
	private void createGame() {
		int lobbySize;
		synchronized (lobbyClients) {
			lobbySize = lobbyClients.size();
		}
		// it is not needed to synchronize on lobbyClients because add players and remove players cannot execute until create game hasn't finished
		synchronized (preparedListeners) {
			while (preparedListeners.size() != lobbySize) {
				try {
					preparedListeners.wait();
				} catch (InterruptedException e) {
					//TODO: see every interrupted exception and implement a more elegant way to handle them
					throw new AssertionError("Thread was interrupted while waiting for all threads to be ready");
				}
			}
		}

		synchronized (lobbyClients) {
			if (lobbyDimension == lobbyClients.size()) {
				try {
					Game game = new Game(lobbyClients.values().toArray(new String[0]));
					ServerController controller = new ServerController(game);
					List<ServerClientListenerThread> keys = new ArrayList<>(lobbyClients.keySet());
					for (int i = 0; i < lobbyClients.size(); i++) {
						RemoteView remoteView = new RemoteView(keys.get(i));
						keys.get(i).setGamePhase(NetworkPhase.LOBBY);
						keys.get(i).setGameServer(remoteView);
						remoteView.addObserver(controller);
						game.addObserver(remoteView);
					}
					controller.generateOrder();
					previousLobby = lobbyClients;
					lobbyClients.clear();
					lobbyDimension = -1;
					creator = null;
					starting = false;
					synchronized (preparedListeners) {
						preparedListeners.clear();
						preparedListeners.notifyAll();
					}
					lobbyClients.notifyAll();
				} catch (NullPointerException e) {
					// it needs a thread making al thread finish to execute because if not, if a thread terminate itself stops before terminating the others
					for (ServerClientListenerThread handler : lobbyClients.keySet()) {
						handler.fatalError("During game creation the game was created null and passed to server controller");
					}
					lobbyClients.clear();
					lobbyDimension = -1;
					creator = null;
					starting = false;
					synchronized (preparedListeners) {
						preparedListeners.clear();
						preparedListeners.notifyAll();
					}
					lobbyClients.notifyAll();
					throw new AssertionError("Something's gone wrong and the server tried to create a gaming server with null game");
				}
			}
		}
	}
}