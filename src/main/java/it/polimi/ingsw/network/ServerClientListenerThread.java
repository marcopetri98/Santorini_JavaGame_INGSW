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
	private int gamePhase; // -1 = before lobby, 0 = lobby, 1 = game color selection, 2 = divinity selection, 3 = worker position on board (game setup), 4 = your turn, 5 = others turn
	private boolean joinedGame;
	private boolean active;
	private String playerName;
	private final Socket clientSocket;
	private final ObjectInputStream input;
	private final ObjectOutputStream output;
	private final Object scopeLock, stateLock;

	/**
	 * The constructor initialize a listener for client's messages in the setup stage, this server listens for messages of setup until the games start, when it starts it changes the gamePhase and listen to other messages, depending on the game phase
	 * @param server This parameter is the reference to the server object
	 * @param clientSocket This parameter is the client's socket which this object listen
	 */
	public ServerClientListenerThread(Socket clientSocket, Server server) throws IOException {
		joinedGame = false;
		gamePhase = -1;
		active = true;
		lobbyServer = server;
		gameServer = null;
		this.clientSocket = clientSocket;
		playerName = null;
		scopeLock = new Object();
		stateLock = new Object();
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
				int phase;
				synchronized (stateLock) {
					phase = gamePhase;
				}
				switch (phase) {
					case -1:
						// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
						if (!(ingoingObject instanceof NetSetup)) {
							sendGeneralError();
						} else {
							parseSetupInput((NetSetup) ingoingObject);
						}
						break;

					case 0:
						// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
						if (!(ingoingObject instanceof NetLobbyPreparation)) {
							sendGeneralError();
						} else {
							parseLobbyInput((NetLobbyPreparation)ingoingObject);
						}
						break;

					case 1:
						// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
						if (!(ingoingObject instanceof NetColorPreparation)) {
							sendGeneralError();
						} else {
							parseColorInput((NetColorPreparation)ingoingObject);
						}
						break;

					case 2:
						// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
						if (!(ingoingObject instanceof NetDivinityChoice)) {
							sendGeneralError();
						} else {
							parseDivinityInput((NetDivinityChoice)ingoingObject);
						}
						break;

					case 3:
						// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
						if (!(ingoingObject instanceof NetGameSetup)) {
							sendGeneralError();
						} else {
							parseGameSetupInput((NetGameSetup)ingoingObject);
						}
						break;

					case 4:
						// if the user is sending messages he cannot send it will receive an error message (this never happen without a malicious user)
						if (!(ingoingObject instanceof NetPlayerTurn)) {
							sendGeneralError();
						} else {
							parseTurnInput((NetPlayerTurn)ingoingObject);
						}
						break;

					case 5:
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
	 * @throws AssertionError if this object has passed wrong parameters to the server functions
	 */
	private void parseSetupInput(NetSetup setupMessage) {
		NetSetup setupOutput;
		String name = null;
		int serverResponse;

		try {
			name = setupMessage.getPlayer();
			// if the player wants to participate to a lobby and it has inserted a name it process it
			if (setupMessage.getMessage().equals(Constants.SETUP_PARTICIPATE) && name != null) {
				serverResponse = lobbyServer.addPlayer(setupMessage.getPlayer(),this);
				// it sends an error to the client because there is a player with such name
				if (serverResponse == 0) {
					setupOutput = new NetSetup(Constants.SETUP_OUT_CONNFAILED);
				// it says the client it is inside the lobby
				} else if (serverResponse == 1) {
					setupOutput = new NetSetup(Constants.SETUP_OUT_CONNWORKED);
					setGamePhase(0);
					joinedGame = true;
					playerName = name;
				// it says to the client that the game is starting
				} else {
					// the game phase is updated by the createGame of the server class
					setupOutput = new NetSetup(Constants.SETUP_OUT_CONNFINISH);
					joinedGame = true;
					playerName = name;
				}
			// if the player is trying to setup the number of player inside the lobby it controls that is can do that
			} else if (setupMessage.getMessage().equals(Constants.SETUP_SETUPNUM)) {
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
			synchronized (stateLock) {
				gamePhase = 0;
			}
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

		if (lobbyMessage.getMessage().equals(Constants.LOBBY_DISCONNECT)) {
			// it disconnects the user from the lobby
			disconnect();
			try {
				clientSocket.close();
			} catch (IOException e) {
				setActive(false);
			}
		} else {
			// user sent a wrong message
			lobbyOutput = new NetLobbyPreparation(Constants.LOBBY_ERROR);
			sendMessage(lobbyOutput);
		}
	}
	private void parseColorInput(NetColorPreparation colorMessage) {
	}
	private void parseDivinityInput(NetDivinityChoice divinityMessage) {
	}
	private void parseGameSetupInput(NetGameSetup gameSetupMessage) {

	}
	private void parseTurnInput(NetPlayerTurn playerTurnMessage) {

	}
	private void parseOtherTurn(NetOtherTurn otherTurnMessage) {

	}

	// CLASS SETTERS AND SUPPORT METHODS
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
	public void setGamePhase(int gamePhase) {
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

	// METHODS CALLED ON THE REMOTE VIEW
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
		if (gamePhase == 0) {
			try {
				lobbyServer.removePlayer(playerName,this);
				setActive(false);
			} catch (AlreadyStartedException e) {
				// if the user has disconnected during the game creation it waits for the game creation and notifies that to the gaming server
				synchronized (scopeLock) {
					while (gameServer == null) {
						try {
							scopeLock.wait();
						} catch (InterruptedException ex) {
							throw new AssertionError("Thread was interrupted and the code never interrupts it");
						}
					}
					notifyDisconnection();
					setActive(false);
				}
			}
		} else {
			notifyDisconnection();
			setActive(false);
		}
	}
}
