package it.polimi.ingsw.network;

// necessary imports from other packages of the project
import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetOrderPreparation;
import it.polimi.ingsw.network.objects.NetSetup;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.exceptions.AlreadyStartedException;
import it.polimi.ingsw.util.exceptions.FirstPlayerException;

// necessary imports of Java SE
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class is the base class for Server and client communication, it receives client input and send to the client server output regarding to the match the client's playing.
 */
public class ServerClientListenerThread extends Thread {
	private Socket clientSocket;
	private Server lobbyServer;
	private RemoteView gameServer;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private int gamePhase; // -1 = before lobby, 0 = lobby, 1 = game color selection, 2 = divinity selection, 3 = worker position on board, 4 = your turn, 5 = others turn
	private boolean joinedGame;
	private boolean active;
	private String playerName;
	private final Object scopeLock, stateLock, communicationLock;

	/**
	 * The constructor initialize a listener for client's messages in the setup stage, this server listens for messages of setup until the games start, when it starts it changes the gamePhase and listen to other messages, depending on the game phase
	 * @param server This parameter is the reference to the server object
	 * @param clientSocket This parameter is the client's socket which this object listen
	 */
	public ServerClientListenerThread(Socket clientSocket, Server server) {
		joinedGame = false;
		gamePhase = -1;
		active = true;
		lobbyServer = server;
		gameServer = null;
		this.clientSocket = clientSocket;
		playerName = null;
		scopeLock = new Object();
		stateLock = new Object();
		communicationLock = new Object();
		try {
			input = new ObjectInputStream(clientSocket.getInputStream());
			output = new ObjectOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			active = false;
		}
	}

	@Override
	public void run() {
		// TODO: change the listener body to permit to the remoteView to access its methods to communicate with the client
		/*
		 * Maybe this can be handler in two phases, when the thread should wait for user input and the phase where it should listen for changes from the server or other components that changes it to notify some actions to the user
		 */
		while (active) {
			switch (gamePhase) {
				case -1:
					parseSetupInput();
					break;

				case 0:
					parseLobbyInput();
					break;

				case 1:
					parseColorInput();
					break;

				case 2:
					parseDivinityInput();
					break;

				case 3:
					parseGameSetupInput();
					break;

				case 4:
					parseTurnInput();
					break;

				case 5:
					parseOtherTurn();
					break;
			}
		}
	}

	// USER INPUT PARSING FUNCTIONS
	/**
	 * This function parses the input that arrives from the client before being inside a lobby, it calls methods on the server to add the player on a lobby, if there isn't a lobby it catches the FirstPlayerException and asks to the player the number of player that the game should have because he is the first and should create a lobby.
	 * @throws AssertionError if this object has passed wrong parameters to the server functions
	 */
	private void parseSetupInput() {
		NetSetup setupOutput, setupInput;
		String name = null;
		int serverResponse;
		boolean shouldRemove = false;

		try {
			setupInput = (NetSetup)input.readObject();
			name = setupInput.getPlayer();
			// if the player wants to participate to a lobby and it has inserted a name it process it
			if (setupInput.getMessage().equals(Constants.SETUP_PARTICIPATE) && name != null) {
				serverResponse = lobbyServer.addPlayer(setupInput.getPlayer(),this);
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
				// TODO: the end of the lobby should be handled in a different way
				} else {
					setupOutput = new NetSetup(Constants.SETUP_OUT_CONNFINISH);
					setGamePhase(1);
					joinedGame = true;
					playerName = name;
				}
				output.writeObject(setupOutput);
				output.flush();
			// if the player is trying to setup the number of player inside the lobby it controls that is can do that
			} else if (setupInput.getMessage().equals(Constants.SETUP_SETUPNUM)) {
				// it controls that the player can set the number of players
				if (lobbyServer.getToBeCreated() && lobbyServer.getClientPosition(this) == 0) {
					// it controls that the number is valid
					if (setupInput.getNumber() < 4 && setupInput.getNumber() > 1) {
						lobbyServer.setPlayerNumber(setupInput.getNumber(), this);
						setupOutput = new NetSetup(Constants.SETUP_CREATE_WORKED);
					// it sets an error message to send to the client
					} else {
						setupOutput = new NetSetup(Constants.SETUP_ERROR);
					}
				// it sets an error message to send to the client
				} else {
					setupOutput = new NetSetup(Constants.SETUP_ERROR);
				}
				output.writeObject(setupOutput);
				output.flush();
			// it sets an error message to send to the client
			} else {
				setupOutput = new NetSetup(Constants.SETUP_ERROR);
				output.writeObject(setupOutput);
				output.flush();
			}
		} catch (IOException e) {
			// the player is gone offline, it sets the field which says to the server to remove it
			shouldRemove = joinedGame;
		} catch (ClassNotFoundException e) {
			// it informs the player that has been an error con communication
			setupOutput = new NetSetup(Constants.SETUP_OUT_CONNERROR);
			try {
				output.writeObject(setupOutput);
				output.flush();
			} catch (IOException ex) {
				// the player is gone offline, it sets the field which says to the server to remove it
				shouldRemove = joinedGame;
			}
		} catch (FirstPlayerException e) {
			// it asks to the player to specify the player number
			synchronized (stateLock) {
				gamePhase = 0;
			}
			setupOutput = new NetSetup(Constants.SETUP_CREATE);
			try {
				output.writeObject(setupOutput);
				output.flush();
			} catch (IOException ex) {
				// the player is gone offline, it sets the field which says to the server to remove it
				shouldRemove = joinedGame;
			}
		} catch (IllegalCallerException ex) {
			throw new AssertionError("A thread called getClientPosition() or setPlayerNumber() without representing a client");
		} catch (IllegalArgumentException ex) {
			throw new AssertionError("Dimension of the game passed to the server was wrong");
		}
		// it controls if the player must be removed and if this is true it removes it
		if (name != null && shouldRemove) {
			try {
				lobbyServer.removePlayer(name,this);
				setActive(false);
			} catch (AlreadyStartedException e) {
				// if the user has disconnected during the game creation it waits for the game creation and notifies that to the gaming server
				synchronized (scopeLock) {
					while (gameServer == null) {
						try {
							scopeLock.wait();
						} catch (InterruptedException ex) {
							// TODO: ask to the tutors how to handle this exception
						}
					}
					notifyDisconnection();
					setActive(false);
				}
			}
		}
	}
	private void parseLobbyInput() {
		NetOrderPreparation colorOutput, colorInput;
	}
	private void parseColorInput() {
		NetColorPreparation orderOutput, orderInput;
	}
	private void parseDivinityInput() {
		NetDivinityChoice divinityOutput, divinityInput;
	}
	private void parseGameSetupInput() {

	}
	private void parseTurnInput() {

	}
	private void parseOtherTurn() {

	}

	// CLASS SETTERS
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

	// METHODS CALLED ON THE REMOTE VIEW
	public void notifyDisconnection() {
		// TODO: implement the notify to the gaming server of user's disconnection
	}

	// METHODS CALLED DURING THE LOBBY CREATION
	public void fatalError() {
		// TODO: communicate at the client that the server had a problem
	}

	// METHODS CALLED DURING THE MATCH SETUP

	// METHODS CALLED DURING THE GAME DURATION

	// METHODS CALLED AT THE GAME END
}
