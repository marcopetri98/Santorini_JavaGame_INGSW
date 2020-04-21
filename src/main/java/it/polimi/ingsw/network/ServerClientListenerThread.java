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
 * This class is the base class for Server and client communication, it receives client input and send to the client server output regarding to the match the client's playing.
 */
public class ServerClientListenerThread extends Thread {
	private Server lobbyServer;
	private RemoteView gameServer;
	private NetworkPhase gamePhase;
	private boolean active;
	private String playerName;
	private final Socket clientSocket;
	private final ObjectInputStream input;
	private final ObjectOutputStream output;
	private final Object scopeLock, stateLock, playerNameLock;

	/**
	 * The constructor initialize a listener for client's messages in the setup stage, this server listens for messages of setup until the games start, when it starts it changes the gamePhase and listen to other messages, depending on the game phase
	 * @param server This parameter is the reference to the server object
	 * @param clientSocket This parameter is the client's socket which this object listen
	 */
	public ServerClientListenerThread(Socket clientSocket, Server server) throws IOException {
		gamePhase = NetworkPhase.PRELOBBY;
		active = true;
		lobbyServer = server;
		gameServer = null;
		this.clientSocket = clientSocket;
		playerName = null;
		scopeLock = new Object();
		stateLock = new Object();
		playerNameLock = new Object();
		input = new ObjectInputStream(clientSocket.getInputStream());
		output = new ObjectOutputStream(clientSocket.getOutputStream());
	}

	@Override
	public void run() {
		NetObject ingoingObject, outgoingObject;

		while (active) {
			// it tries to read an object, if it doesn't succeed it sends the user an error, if he has disconnected it flags the variable active to false to terminate the thread and calls the method disconnect
			try {
				ingoingObject = (NetObject)input.readObject();
			} catch(IOException | ClassNotFoundException e) {
				ingoingObject = null;
				sendGeneralError();
			}

			if (ingoingObject != null) {
				NetworkPhase phase;
				synchronized (stateLock) {
					phase = gamePhase;
				}
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
							parseLobbyInput((NetLobbyPreparation)ingoingObject);
						}
						break;

					case COLORS:
						// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
						if (!(ingoingObject instanceof NetColorPreparation)) {
							sendGeneralError();
						} else {
							parseColorInput((NetColorPreparation)ingoingObject);
						}
						break;

					case GODS:
						// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
						if (!(ingoingObject instanceof NetDivinityChoice)) {
							sendGeneralError();
						} else {
							parseDivinityInput((NetDivinityChoice)ingoingObject);
						}
						break;

					case SETUP:
						// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
						if (!(ingoingObject instanceof NetGameSetup)) {
							sendGeneralError();
						} else {
							parseGameSetupInput((NetGameSetup)ingoingObject);
						}
						break;

					case PLAYERTURN:
						// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
						if (!(ingoingObject instanceof NetPlayerTurn)) {
							sendGeneralError();
						} else {
							parseTurnInput((NetPlayerTurn)ingoingObject);
						}
						break;

					case OTHERTURN:
						// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
						if (!(ingoingObject instanceof NetOtherTurn)) {
							sendGeneralError();
						} else {
							parseOtherTurn((NetOtherTurn)ingoingObject);
						}
						break;
				}
			}
		}
	}

	// USER INPUT PARSING FUNCTIONS
	/**
	 * This function parses the input that arrives from the client before being inside a lobby, it calls methods on the server to add the player on a lobby, if there isn't a lobby it catches the FirstPlayerException and asks to the player the number of player that the game should have because he is the first and should create a lobby.
	 * @param setupMessage the message sent by the client
	 * @throws AssertionError if this object has passed wrong parameters to the server functions
	 */
	private void parseSetupInput(NetSetup setupMessage) {
		NetSetup setupOutput;
		String name = null;
		int serverResponse;

		try {
			name = setupMessage.getPlayer();
			// if the player wants to participate to a lobby and it has inserted a name it process it
			if (setupMessage.message.equals(Constants.SETUP_PARTICIPATE) && name != null) {
				serverResponse = lobbyServer.addPlayer(setupMessage.getPlayer(),this);
				// it sends an error to the client because there is a player with such name
				if (serverResponse == 0) {
					setupOutput = new NetSetup(Constants.SETUP_OUT_CONNFAILED);
				// it says the client it is inside the lobby
				} else if (serverResponse == 1) {
					setupOutput = new NetSetup(Constants.SETUP_OUT_CONNWORKED);
					setGamePhase(NetworkPhase.LOBBY);
					setPlayerName(name);
				// it says to the client that the game is starting
				} else {
					// the game phase is updated by the createGame of the server class
					setupOutput = new NetSetup(Constants.SETUP_OUT_CONNFINISH);
					setPlayerName(name);
				}
			// if the player is trying to setup the number of player inside the lobby it controls that is can do that
			} else if (setupMessage.message.equals(Constants.SETUP_SETUPNUM)) {
				// it controls that the player can set the number of players
				if (lobbyServer.getToBeCreated() && lobbyServer.getClientPosition(this) == 0) {
					// it controls that the number is valid
					if (setupMessage.getNumber() < 4 && setupMessage.getNumber() > 1) {
						lobbyServer.setPlayerNumber(setupMessage.getNumber(), this);
						setupOutput = new NetSetup(Constants.SETUP_CREATE_WORKED);
					// it sets an error message to send to the client
					} else {
						setupOutput = new NetSetup(Constants.SETUP_ERROR);
					}
				// it sets an error message to send to the client
				} else {
					setupOutput = new NetSetup(Constants.SETUP_ERROR);
				}
			// it sets an error message to send to the client
			} else {
				setupOutput = new NetSetup(Constants.SETUP_ERROR);
			}
			sendMessage(setupOutput);
		} catch (FirstPlayerException e) {
			// it asks to the player to specify the player number
			setGamePhase(NetworkPhase.LOBBY);
			setupOutput = new NetSetup(Constants.SETUP_CREATE);
			sendMessage(setupOutput);
		} catch (IllegalCallerException ex) {
			throw new AssertionError("A thread called getClientPosition() or setPlayerNumber() without representing a client");
		} catch (IllegalArgumentException ex) {
			throw new AssertionError("Dimension of the game passed to the server was wrong");
		}
	}
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
	private void parseColorInput(NetColorPreparation colorMessage) {
		NetColorPreparation colorOutput;

		if (colorMessage.message.equals(Constants.COLOR_IN_CHOICE) && Constants.COLOR_COLORS.contains(colorMessage.getColor())) {
			// the user is trying to choose the color with a well formed message, this is sent to the remoteView
			gameServer.handleColorRequest(colorMessage);
		} else if (colorMessage.message.equals(Constants.GENERAL_DISCONNECT)) {
			// disconnects the user
			disconnect();
		} else {
			// the user has sent a bad message
			colorOutput = new NetColorPreparation(Constants.GENERAL_ERROR);
			sendMessage(colorOutput);
		}
	}
	private void parseDivinityInput(NetDivinityChoice divinityMessage) {
		NetDivinityChoice divinityOutput;

		if (divinityMessage.message.equals(Constants.GODS_IN_GAME_GODS)) {
			// if the player has sent a well formed message with gods that has to be in the game it sends this to the remote view
			if ((divinityMessage.getDivinities().size() == 2 || divinityMessage.getDivinities().size() == 3) && Constants.GODS_GOD_NAMES.containsAll(divinityMessage.getDivinities())) {
				gameServer.handleDivinityRequest(divinityMessage);
			} else {
				divinityOutput = new NetDivinityChoice(Constants.GODS_ERROR);
				sendMessage(divinityOutput);
			}
		} else if (divinityMessage.message.equals(Constants.GODS_IN_CHOICE)) {
			// if the player chose a divinity it sends the message to the remote view
			if (Constants.GODS_GOD_NAMES.contains(divinityMessage.getDivinity())) {
				gameServer.handleDivinityRequest(divinityMessage);
			} else {
				divinityOutput = new NetDivinityChoice(Constants.GODS_ERROR);
				sendMessage(divinityOutput);
			}
		} else if (divinityMessage.message.equals(Constants.GODS_IN_START_PLAYER) && divinityMessage.getStarter() != null) {
			// if the player is trying to select a start player with a well formed message it sends the message to the remote view
			gameServer.handleDivinityRequest(divinityMessage);
		} else if (divinityMessage.message.equals(Constants.GENERAL_DISCONNECT)) {
			// it disconnects the user
			disconnect();
		} else {
			// message isn't well formed ==> error message sent
			divinityOutput = new NetDivinityChoice(Constants.GENERAL_ERROR);
			sendMessage(divinityOutput);
		}
	}
	private void parseGameSetupInput(NetGameSetup gameSetupMessage) {
		NetGameSetup gameSetupOutput;

		if (gameSetupMessage.message.equals(Constants.GAMESETUP_IN_PLACE)) {
			if (gameSetupMessage.worker1.getFirst() < Constants.MAP_SIDE && gameSetupMessage.worker1.getFirst() >= 0 && gameSetupMessage.worker1.getSecond() < Constants.MAP_SIDE && gameSetupMessage.worker1.getSecond() >= 0 && gameSetupMessage.worker2.getFirst() < Constants.MAP_SIDE && gameSetupMessage.worker2.getFirst() >= 0 && gameSetupMessage.worker2.getSecond() < Constants.MAP_SIDE && gameSetupMessage.worker2.getSecond() >= 0 && !gameSetupMessage.worker1.equals(gameSetupMessage.worker2)) {
				// the user is sending coordinates of the map where it want to put workers (not on the same cell)
				gameServer.handlePositionRequest(gameSetupMessage);
			} else {
				gameSetupOutput = new NetGameSetup(Constants.GAMESETUP_ERROR);
				sendMessage(gameSetupMessage);
			}
		} else if (gameSetupMessage.message.equals(Constants.GENERAL_DISCONNECT)) {
			// it disconnects the user
			disconnect();
		} else {
			// the message isn't well formed
			gameSetupOutput = new NetGameSetup(Constants.GENERAL_ERROR);
			sendMessage(gameSetupMessage);
		}
	}
	private void parseTurnInput(NetPlayerTurn playerTurnMessage) {
		NetPlayerTurn playerTurnOutput;

		if (playerTurnMessage.message.equals(Constants.PLAYER_IN_MOVE)) {
			if (playerTurnMessage.move.cellX >= 0 && playerTurnMessage.move.cellX <= Constants.MAP_SIDE && playerTurnMessage.move.cellY >= 0 && playerTurnMessage.move.cellY <= Constants.MAP_SIDE) {
				gameServer.handleMoveRequest(playerTurnMessage);
			} else {
				playerTurnMessage = new NetPlayerTurn(Constants.PLAYER_ERROR);
				sendMessage(playerTurnMessage);
			}
		} else if (playerTurnMessage.message.equals(Constants.PLAYER_IN_BUILD)) {
			if (playerTurnMessage.build.cellX >= 0 && playerTurnMessage.build.cellX <= Constants.MAP_SIDE && playerTurnMessage.build.cellY >= 0 && playerTurnMessage.build.cellY <= Constants.MAP_SIDE) {
				gameServer.handleBuildRequest(playerTurnMessage);
			} else {
				playerTurnMessage = new NetPlayerTurn(Constants.PLAYER_ERROR);
				sendMessage(playerTurnMessage);
			}
		} else if (playerTurnMessage.message.equals(Constants.GENERAL_DISCONNECT)) {
			// it disconnects the user from the lobby
			disconnect();
		} else {
			playerTurnMessage = new NetPlayerTurn(Constants.GENERAL_ERROR);
			sendMessage(playerTurnMessage);
		}
	}
	private void parseOtherTurn(NetOtherTurn otherTurnMessage) {
		NetOtherTurn othersOutput;

		if (otherTurnMessage.message.equals(Constants.GENERAL_DISCONNECT)) {
			// it disconnects the user from the lobby
			disconnect();
		} else {
			// user sent a wrong message
			othersOutput = new NetOtherTurn(Constants.OTHERS_ERROR);
			sendMessage(othersOutput);
		}
	}

	// CLASS SETTERS AND SUPPORT METHODS
	private void setPlayerName(String name) {
		synchronized (playerNameLock) {
			playerName = name;
		}
	}
	public void setGameServer(RemoteView gameServer) {
		synchronized (scopeLock) {
			this.gameServer = gameServer;
			this.lobbyServer = null;
			scopeLock.notifyAll();
		}
	}
	public void setActive(boolean active) {
		synchronized (stateLock) {
			this.active = active;
		}
	}
	public void setGamePhase(NetworkPhase gamePhase) {
		synchronized (stateLock) {
			this.gamePhase = gamePhase;
		}
	}
	public void sendMessage(NetObject object) {
		synchronized (output) {
			try {
				output.writeObject(object);
				output.flush();
			} catch (IOException e2) {
				setActive(false);
				disconnect();
			}
		}
	}
	public void sendGeneralError() {
		NetObject object = new NetObject(Constants.GENERAL_ERROR);
		sendMessage(object);
	}

	// GETTERS FOR THIS CLASS
	public NetworkPhase getGamePhase() {
		synchronized (stateLock) {
			return gamePhase;
		}
	}
	public String getPlayerName() {
		synchronized (playerNameLock) {
			return playerName;
		}
	}

	// METHODS CALLED ON THE REMOTE VIEW
	/**
	 * This method calls the remote view method that notifies the gaming controller that the player has disconnected
	 */
	public void notifyDisconnection() {
		gameServer.notifyQuit(playerName);
	}

	// METHODS CALLED THAT CHANGES THE STATE OF THE LISTENER THREAD
	/**
	 * This is a class used to communicate to the client that the server had a fatal error and the code is probably wrong
	 * @throws AssertionError always thrown because the server had an error due to incorrect calls to methods
	 */
	public void fatalError(String info) {
		NetObject sendError = new NetObject(Constants.GENERAL_FATAL_ERROR);
		sendMessage(sendError);
		try {
			clientSocket.close();
			setActive(false);
		} catch (IOException e) {
			setActive(false);
		}
		throw new AssertionError("A fatal error occurred and these are the info about it: "+info);
	}
	/**
	 * This method disconnects the user and set the active flag to false to terminate the thread
	 */
	public void disconnect() {
		if (gamePhase == NetworkPhase.LOBBY) {
			try {
				lobbyServer.removePlayer(playerName,this);
				clientSocket.close();
				setActive(false);
				setActive(false);
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
					try {
						clientSocket.close();
						setActive(false);
					} catch (IOException e2) {
						setActive(false);
					}
				}
			} catch (IOException e) {
				setActive(false);
			}
		} else {
			notifyDisconnection();
			try {
				clientSocket.close();
				setActive(false);
			} catch (IOException e2) {
				setActive(false);
			}
		}
	}
}
