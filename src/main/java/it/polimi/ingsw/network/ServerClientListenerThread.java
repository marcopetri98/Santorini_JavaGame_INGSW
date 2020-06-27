package it.polimi.ingsw.network;

// necessary imports from other packages of the project
import it.polimi.ingsw.network.objects.*;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.exceptions.AlreadyStartedException;
import it.polimi.ingsw.util.exceptions.FirstPlayerException;

// necessary imports of Java SE
import java.io.*;
import java.net.Socket;

/**
 * This class is the class for Server and Client communication, it receives client input using socket with classes of the package {@link it.polimi.ingsw.network.objects} and {@link it.polimi.ingsw.network.game} and is used to send input to the client.
 */
public class ServerClientListenerThread extends Thread {
	/**
	 * This is a private class which only aim is to check that the client is currently online and is playing the game with ping requests implemented using the NetObject class with a special message to indicate this intention.
	 */
	private class ClientConnection implements Runnable {
		private boolean receivedPing;

		ClientConnection() {
			receivedPing = false;
		}

		@Override
		public void run() {
			while (verifyClient && output != null) {
				receivedPing = false;
				synchronized (output) {
					NetObject ping = new NetObject(Constants.CHECK);
					try {
						output.writeObject(ping);
						output.flush();
						output.reset();
					} catch (IOException e) {
						destroyListener();
					}
				}
				try {
					Thread.sleep(5000);
					if (!receivedPing) {
						destroyListener();
					}
				} catch (InterruptedException e) {
					throw new AssertionError("Someone tried to interrupt ClientConnection");
				}
			}
		}

		/**
		 * This function disconnects the server listener from the client and make its thread to stop to execute.
		 */
		private void destroyListener() {
			if (playerName != null) {
				disconnect();
			}
			synchronized (stateLock) {
				verifyClient = false;
			}
		}
		/**
		 * Sets a flag which says that a ping message arrived from the client.
		 */
		private void setReceivedPing() {
			receivedPing = true;
		}
	}

	private Server lobbyServer;
	private RemoteView gameServer;
	private NetworkPhase gamePhase;
	private boolean active;
	private boolean verifyClient;
	private String playerName;
	private ClientConnection connectionListener;
	private final Socket clientSocket;
	private final ObjectInputStream input;
	private final ObjectOutputStream output;
	private final Object scopeLock, stateLock, playerNameLock;
	private static long thread_number;

	/**
	 * The constructor initialize a listener for client's messages in the setup stage.
	 * @param server This parameter is the reference to the server object
	 * @param clientSocket This parameter is the client's socket which this object listen
	 */
	public ServerClientListenerThread(Socket clientSocket, Server server) throws IOException {
		super("ServerClientListenerThread_"+thread_number);
		thread_number++;
		gamePhase = NetworkPhase.PRELOBBY;
		active = true;
		verifyClient = true;
		lobbyServer = server;
		gameServer = null;
		this.clientSocket = clientSocket;
		playerName = null;
		scopeLock = new Object();
		stateLock = new Object();
		playerNameLock = new Object();
		output = new ObjectOutputStream(clientSocket.getOutputStream());
		input = new ObjectInputStream(clientSocket.getInputStream());
		connectionListener = new ClientConnection();
		new Thread(connectionListener).start();
	}
	protected ServerClientListenerThread() {
		super("ServerClientListenerThread");
		gamePhase = NetworkPhase.PRELOBBY;
		active = true;
		verifyClient = true;
		lobbyServer = null;
		gameServer = null;
		this.clientSocket = null;
		playerName = null;
		scopeLock = new Object();
		stateLock = new Object();
		playerNameLock = new Object();
		output = null;
		input = null;
		connectionListener = new ClientConnection();
	}

	/**
	 * Overrides the Thread run method, it listens for {@link it.polimi.ingsw.network.objects.NetObject} objects on the socket and analyze with the current phase. If the message is dynamically (meaning that its dynamic type is the type needed for the current phase) it passes it to the parsing method for that phase.
	 */
	@Override
	public void run() {
		NetObject ingoingObject;

		while (active) {
			// it tries to read an object, if it doesn't succeed it sends the user an error, if he has disconnected it flags the variable active to false to terminate the thread and calls the method disconnect
			try {
				ingoingObject = (NetObject)input.readObject();
			} catch(ClassNotFoundException e) {
				ingoingObject = null;
				sendGeneralError();
			} catch (IOException e) {
				verifyClient = false;
				ingoingObject = null;
				if (playerName != null) {
					disconnect();
				}
				active = false;
			}

			if (ingoingObject != null) {
				NetworkPhase phase;
				synchronized (stateLock) {
					phase = gamePhase;
				}
				connectionListener.setReceivedPing();
				if (!ingoingObject.message.equals(Constants.CHECK)) {
					switch (phase) {
						case PRELOBBY:
							// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
							if (!(ingoingObject instanceof NetSetup)) {
								sendGeneralError();
							} else {
								parseSetupInput((NetSetup) ingoingObject);
							}
							break;

						case LOBBY:
							// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
							if (!(ingoingObject instanceof NetLobbyPreparation)) {
								sendGeneralError();
							} else {
								parseLobbyInput((NetLobbyPreparation) ingoingObject);
							}
							break;

						case COLORS:
							// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
							if (!(ingoingObject instanceof NetColorPreparation)) {
								sendGeneralError();
							} else {
								parseColorInput((NetColorPreparation) ingoingObject);
							}
							break;

						case GODS:
							// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
							if (!(ingoingObject instanceof NetDivinityChoice)) {
								sendGeneralError();
							} else {
								parseDivinityInput((NetDivinityChoice) ingoingObject);
							}
							break;

						case SETUP:
							// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
							if (!(ingoingObject instanceof NetGameSetup)) {
								sendGeneralError();
							} else {
								parseGameSetupInput((NetGameSetup) ingoingObject);
							}
							break;

						default:
							// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
							if (!(ingoingObject instanceof NetGaming)) {
								sendGeneralError();
							} else {
								parseGamingInput((NetGaming) ingoingObject);
							}
							break;
					}
				}
			}
		}
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *			INPUT PARSING FUNCTIONS				*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	/**
	 * This function parses the input that arrives from the client before being inside a lobby, it calls methods on the server to add the player on a lobby, if there isn't a lobby it catches the FirstPlayerException and asks to the player the number of player that the game should have because he is the first and should create a lobby.
	 * @param setupMessage is the message sent by the client
	 * @throws AssertionError if this object has passed wrong parameters to the server functions
	 */
	// TODO: control that every msg.player is not null and all null pointers
	private void parseSetupInput(NetSetup setupMessage) {
		NetSetup setupOutput;
		String nameReceived;
		int serverResponse;

		try {
			nameReceived = setupMessage.getPlayer();
			// if the player wants to participate to a lobby and it has inserted a name it process it
			if (setupMessage.message.equals(Constants.SETUP_IN_PARTICIPATE) && setupMessage.getPlayer() != null) {
				setPlayerName(nameReceived);
				serverResponse = lobbyServer.addPlayer(setupMessage.getPlayer(),this);
				// it sends an error to the client because there is a player with such name
				if (serverResponse == 0) {
					playerName = null;
					setupOutput = new NetSetup(Constants.SETUP_OUT_CONNFAILED);
					sendMessage(setupOutput);
				// it says the client it is inside the lobby
				} else if (serverResponse == 1) {
					setupOutput = new NetSetup(Constants.SETUP_OUT_CONNWORKED, lobbyServer.getLobbyDimension());
					setGamePhase(NetworkPhase.LOBBY);
					sendMessage(setupOutput);
					lobbyServer.updatePlayerInLobby();
					lobbyServer.isNowPrepared(this);
				// it says to the client that the game is starting
				} else {
					// the game phase is updated by the createGame of the server class
					setupOutput = new NetSetup(Constants.SETUP_OUT_CONNFINISH, lobbyServer.getLobbyDimension());
					setGamePhase(NetworkPhase.LOBBY);
					sendMessage(setupOutput);
					lobbyServer.updatePlayerInLobby();
					lobbyServer.isNowPrepared(this);
				}
			// if the player is trying to setup the number of player inside the lobby it controls that is can do that
			} else if (setupMessage.message.equals(Constants.SETUP_IN_SETUPNUM)) {
				// it controls that the player can set the number of players
				if (lobbyServer.getToBeCreated() && lobbyServer.getClientPosition(this) == 0) {
					// it controls that the number is valid
					if (setupMessage.getNumber() < 4 && setupMessage.getNumber() > 1) {
						setGamePhase(NetworkPhase.LOBBY);
						lobbyServer.setPlayerNumber(setupMessage.getNumber(), this);
						setupOutput = new NetSetup(Constants.SETUP_CREATE_WORKED, lobbyServer.getLobbyDimension());
						sendMessage(setupOutput);
						lobbyServer.isNowPrepared(this);
					// it sets an error message to send to the client
					} else {
						setupOutput = new NetSetup(Constants.SETUP_ERROR);
						sendMessage(setupOutput);
					}
				// it sets an error message to send to the client
				} else {
					setupOutput = new NetSetup(Constants.SETUP_ERROR);
					sendMessage(setupOutput);
				}
			// it sets an error message to send to the client
			} else if (setupMessage.message.equals(Constants.GENERAL_DISCONNECT)) {
				if (playerName != null) {
					disconnect();
				} else {
					setupOutput = new NetSetup(Constants.SETUP_ERROR);
					sendMessage(setupOutput);
				}
			} else {
				setupOutput = new NetSetup(Constants.SETUP_ERROR);
				sendMessage(setupOutput);
			}
		} catch (FirstPlayerException e) {
			// it asks to the player to specify the player number
			setPlayerName(setupMessage.getPlayer());
			setupOutput = new NetSetup(Constants.SETUP_CREATE);
			sendMessage(setupOutput);
		} catch (IllegalCallerException ex) {
			// TODO: this has been thrown while creating a lobby with a gui client, exiting with x during color phase and after joining with needed players and make them quit
			throw new AssertionError("A thread called getClientPosition() or setPlayerNumber() without representing a client");
		} catch (IllegalArgumentException ex) {
			throw new AssertionError("Dimension of the game passed to the server was wrong");
		}
	}

	/**
	 * This function parses the input that arrives from the client in the lobby phase of the game, here the client can only disconnect from the lobby.
	 * @param lobbyMessage is the message sent by the client
	 */
	private void parseLobbyInput(NetLobbyPreparation lobbyMessage) {
		NetLobbyPreparation lobbyOutput;

		if (lobbyMessage.message.equals(Constants.GENERAL_DISCONNECT)) {
			// it disconnects the user from the lobby
			disconnect();
		} else {
			// user sent a wrong message
			lobbyOutput = new NetLobbyPreparation(Constants.LOBBY_ERROR);
			sendMessage(lobbyOutput);
		}
	}
	/**
	 * This function parses the input that arrives from the client in the color choice phase, if the client has sent a color of the game it calls the {@link it.polimi.ingsw.network.RemoteView} handling method.
	 * @param colorMessage is the message sent by the client
	 */
	private void parseColorInput(NetColorPreparation colorMessage) {
		NetColorPreparation colorOutput;

		if (colorMessage.message.equals(Constants.COLOR_IN_CHOICE) && colorMessage.player.equals(playerName) && Constants.COLOR_COLORS.contains(colorMessage.getColor())) {
			// the user is trying to choose the color with a well formed message, this is sent to the remoteView
			gameServer.handleColorRequest(colorMessage,false);
		} else if (colorMessage.message.equals(Constants.GENERAL_DISCONNECT)) {
			// disconnects the user
			disconnect();
		} else {
			// the user has sent a bad message
			colorOutput = new NetColorPreparation(Constants.COLOR_ERROR);
			sendMessage(colorOutput);
		}
	}
	/**
	 * This function parses the input that arrives from the client in the gods phase, in this phase the client can choose a god, game gods or the starter (or also disconnect). In all cases if the message is correctly formed it is sent to the {@link it.polimi.ingsw.network.RemoteView} handling method.
	 * @param divinityMessage is the message sent by the client
	 */
	private void parseDivinityInput(NetDivinityChoice divinityMessage) {
		NetDivinityChoice divinityOutput;

		if (divinityMessage.message.equals(Constants.GODS_IN_GAME_GODS) && divinityMessage.player.equals(playerName)) {
			// if the player has sent a well formed message with gods that has to be in the game it sends this to the remote view
			if ((divinityMessage.getDivinities().size() == 2 || divinityMessage.getDivinities().size() == 3) && Constants.GODS_GOD_NAMES.containsAll(divinityMessage.getDivinities())) {
				gameServer.handleDivinityRequest(divinityMessage,false);
			} else {
				divinityOutput = new NetDivinityChoice(Constants.GODS_ERROR);
				sendMessage(divinityOutput);
			}
		} else if (divinityMessage.message.equals(Constants.GODS_IN_CHOICE) && divinityMessage.player.equals(playerName)) {
			// if the player chose a divinity it sends the message to the remote view
			if (Constants.GODS_GOD_NAMES.contains(divinityMessage.getDivinity())) {
				gameServer.handleDivinityRequest(divinityMessage,false);
			} else {
				divinityOutput = new NetDivinityChoice(Constants.GODS_ERROR);
				sendMessage(divinityOutput);
			}
		} else if (divinityMessage.message.equals(Constants.GODS_IN_START_PLAYER) && divinityMessage.challenger.equals(playerName) && divinityMessage.player != null) {
			// if the player is trying to select a start player with a well formed message it sends the message to the remote view
			gameServer.handleDivinityRequest(divinityMessage,false);
		} else if (divinityMessage.message.equals(Constants.GENERAL_DISCONNECT)) {
			// it disconnects the user
			disconnect();
		} else {
			// message isn't well formed ==> error message sent
			divinityOutput = new NetDivinityChoice(Constants.GODS_ERROR);
			sendMessage(divinityOutput);
		}
	}
	/**
	 * This function parses the input that arrives from the client in the game setup phase, in this phase the player can send its workers position on the game table or disconnect, if the message id well formed it is sent to the {@link it.polimi.ingsw.network.RemoteView} handling method.
	 * @param gameSetupMessage is the message sent by the client
	 */
	private void parseGameSetupInput(NetGameSetup gameSetupMessage) {
		NetGameSetup gameSetupOutput;

		if (gameSetupMessage.message.equals(Constants.GAMESETUP_IN_PLACE) && gameSetupMessage.player.equals(playerName)) {
			if (gameSetupMessage.worker1.getFirst() < Constants.MAP_SIDE && gameSetupMessage.worker1.getFirst() >= 0 && gameSetupMessage.worker1.getSecond() < Constants.MAP_SIDE && gameSetupMessage.worker1.getSecond() >= 0 && gameSetupMessage.worker2.getFirst() < Constants.MAP_SIDE && gameSetupMessage.worker2.getFirst() >= 0 && gameSetupMessage.worker2.getSecond() < Constants.MAP_SIDE && gameSetupMessage.worker2.getSecond() >= 0 && !gameSetupMessage.worker1.equals(gameSetupMessage.worker2) && gameSetupMessage.worker1.getFirst() != null && gameSetupMessage.worker1.getSecond() != null && gameSetupMessage.worker2.getFirst() != null && gameSetupMessage.worker2.getSecond() != null) {
				// the user is sending coordinates of the map where it want to put workers (not on the same cell)
				gameServer.handlePositionRequest(gameSetupMessage,false);
			} else {
				gameSetupOutput = new NetGameSetup(Constants.GAMESETUP_ERROR);
				sendMessage(gameSetupMessage);
			}
		} else if (gameSetupMessage.message.equals(Constants.GENERAL_DISCONNECT)) {
			// it disconnects the user
			disconnect();
		} else {
			// the message isn't well formed
			gameSetupOutput = new NetGameSetup(Constants.GAMESETUP_ERROR);
			sendMessage(gameSetupMessage);
		}
	}
	/**
	 * This function parses the input that arrives from the client in the gaming phase, in this phase a player can move, build or disconnect, if the message is well formed it is sent to the {@link it.polimi.ingsw.network.RemoteView} handling method.
	 * @param gamingMsg is the message sent by the client
	 */
	private void parseGamingInput(NetGaming gamingMsg) {
		NetGaming playerTurnOutput;
		NetworkPhase currentPhase = getGamePhase();

		if (currentPhase == NetworkPhase.PLAYERTURN) {
			if (gamingMsg.message.equals(Constants.PLAYER_IN_MOVE) && gamingMsg.player.equals(playerName)) {
				if (gamingMsg.move != null && gamingMsg.move.isWellFormed()) {
					gameServer.handleMoveRequest(gamingMsg, false);
				} else {
					gamingMsg = new NetGaming(Constants.PLAYER_ERROR);
					sendMessage(gamingMsg);
				}
			} else if (gamingMsg.message.equals(Constants.PLAYER_IN_BUILD) && gamingMsg.player.equals(playerName)) {
				if (gamingMsg.build != null && gamingMsg.build.isWellFormed()) {
					gameServer.handleBuildRequest(gamingMsg, false);
				} else {
					gamingMsg = new NetGaming(Constants.PLAYER_ERROR);
					sendMessage(gamingMsg);
				}
			} else if (gamingMsg.message.equals(Constants.GENERAL_DISCONNECT)) {
				// it disconnects the user from the lobby
				disconnect();
			} else {
				gamingMsg = new NetGaming(Constants.PLAYER_ERROR);
				sendMessage(gamingMsg);
			}
		} else if (currentPhase == NetworkPhase.OTHERTURN) {
			if (gamingMsg.message.equals(Constants.GENERAL_DISCONNECT)) {
				// it disconnects the user from the lobby
				disconnect();
			} else {
				gamingMsg = new NetGaming(Constants.OTHERS_ERROR);
				sendMessage(gamingMsg);
			}
		} else {
			// the player is an observer and can only observe the game or disconnect
			if (gamingMsg.message.equals(Constants.GENERAL_DISCONNECT)) {
				// it disconnects the user from the lobby
				gameServer.handleObserverQuit();
				closeSocketAndTerminate();
			} else {
				gamingMsg = new NetGaming(Constants.GENERAL_ERROR);
				sendMessage(gamingMsg);
			}
		}
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *	MODIFIERS OF THE SERVER LISTENER THREAD		*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	/**
	 * It sets the player's name.
	 * @param name is player's name
	 */
	private void setPlayerName(String name) {
		synchronized (playerNameLock) {
			playerName = name;
		}
	}
	/**
	 * It sets the remote view connected to this client.
	 * @param gameServer is the remote view component of the server which is connected to this client
	 */
	public void setGameServer(RemoteView gameServer) {
		synchronized (scopeLock) {
			this.gameServer = gameServer;
			scopeLock.notifyAll();
		}
	}
	/**
	 * It sets the current thread to {@code active} value, if it is set to false the thread is going to terminate, instead the thread is going to continue its normal work.
	 * @param active
	 */
	public void setActive(boolean active) {
		synchronized (stateLock) {
			this.active = active;
		}
	}
	/**
	 * It sets the network game phase.
	 * @param gamePhase is the game network phase
	 */
	public void setGamePhase(NetworkPhase gamePhase) {
		synchronized (stateLock) {
			this.gamePhase = gamePhase;
		}
	}
	/**
	 * It writes the message on the output stream and send it to the client, after sending the message it is called a {@code output.reset()} method to reset the output stream.
	 * @param object is the {@link it.polimi.ingsw.network.objects.NetObject} to send to the client
	 */
	public void sendMessage(NetObject object) {
		synchronized (output) {
			try {
				output.writeObject(object);
				output.flush();
				output.reset();
			} catch (IOException e2) {
				setActive(false);
				closeSocketAndTerminate();
			}
		}
	}
	/**
	 * It sends a general error to the client to make it know that an unknown error occurred.
	 */
	public void sendGeneralError() {
		NetObject object = new NetObject(Constants.GENERAL_ERROR);
		sendMessage(object);
	}
	/**
	 * It closes the socket and calls {@code setActive(false);}.
	 */
	public void closeSocketAndTerminate() {
		try {
			clientSocket.close();
			playerName = null;
			setActive(false);
		} catch (IOException e2) {
			playerName = null;
			setActive(false);
		}
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *			GETTERS FOR THIS CLASS				*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	/**
	 * Gets game phase.
	 * @return game phase
	 */
	public NetworkPhase getGamePhase() {
		synchronized (stateLock) {
			return gamePhase;
		}
	}
	/**
	 * Gets player name.
	 * @return player name
	 */
	public String getPlayerName() {
		synchronized (playerNameLock) {
			return playerName;
		}
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *			UPDATERS OF THE REMOTE VIEW			*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	/**
	 * This method calls the remote view method which handles the disconnection of the client.
	 */
	public void notifyDisconnection() {
		gameServer.notifyQuit(playerName);
		gameServer.removeAllObservers();
	}

	// METHODS CALLED THAT CHANGES THE STATE OF THE LISTENER THREAD
	/**
	 * This method is called when a programming error occurred, it sends a message to the client to notify it that this game has crashed and throws an AssertionError on the server.
	 * @throws AssertionError always thrown because the server had an error due to incorrect calls to methods
	 */
	public void fatalError(String info) {
		verifyClient = false;
		NetObject sendError = new NetObject(Constants.GENERAL_FATAL_ERROR);
		sendMessage(sendError);
		disconnect();
		closeSocketAndTerminate();
		throw new AssertionError("A fatal error occurred and these are the info about it: "+info);
	}
	/**
	 * This method disconnects the user.
	 */
	public void disconnect() {
		if (gamePhase == NetworkPhase.LOBBY || gamePhase == NetworkPhase.PRELOBBY) {
			try {
				lobbyServer.removePlayer(playerName,this);
				closeSocketAndTerminate();
			} catch (AlreadyStartedException e) {
				// if the user has disconnected during the game creation it waits for the game creation and notifies that to the gaming server
				synchronized (scopeLock) {
					while (gameServer == null) {
						try {
							scopeLock.wait();
						} catch (InterruptedException e2) {
							throw new AssertionError("Thread was interrupted and the code never interrupts it");
						}
					}
					notifyDisconnection();
					closeSocketAndTerminate();
				}
			}
		} else {
			notifyDisconnection();
			closeSocketAndTerminate();
		}
	}
}
