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
	private int gamePhase; // -1 = before lobby, 0 = lobby, 1 = game color selection, 2 = divinity selection, 3 = your turn, 4 = others turn
	private boolean joinedGame;
	private boolean active;
	private final Object scopeLock, stateLock;

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
		scopeLock = new Object();
		stateLock = new Object();
		try {
			input = new ObjectInputStream(clientSocket.getInputStream());
			output = new ObjectOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			active = false;
		}
	}

	@Override
	public void run() {
		while (active) {
			switch (gamePhase) {
				case -1:
					parseSetupInput();
					break;

				case 0:
					parseLobbyInput();

				case 1:
					parseColorInput();
					break;

				case 2:
					parseDivinityInput();
					break;

				case 3:
					parseTurnInput();
					break;

				case 4:
					parseOtherTurn();
					break;
			}
		}
	}

	// USER INPUT PARSING FUNCTIONS
	private void parseSetupInput() {
		NetSetup setupOutput, setupInput;
		String name = null;
		int serverResponse;
		boolean shouldRemove = false;

		try {
			setupInput = (NetSetup)input.readObject();
			name = setupInput.getPlayer();
			if (setupInput.getMessage().equals(Constants.SETUP_PARTICIPATE) && name != null) {
				serverResponse = lobbyServer.addPlayer(setupInput.getPlayer(),this);
				if (serverResponse == 0) {
					setupOutput = new NetSetup(Constants.SETUP_OUT_CONNFAILED);
				} else if (serverResponse == 1) {
					setupOutput = new NetSetup(Constants.SETUP_OUT_CONNWORKED);
					synchronized (stateLock) {
						gamePhase = 0;
					}
					joinedGame = true;
				} else {
					setupOutput = new NetSetup(Constants.SETUP_OUT_CONNFINISH);
					synchronized (stateLock) {
						gamePhase = 1;
					}
					joinedGame = true;
				}
				output.writeObject(setupOutput);
				output.flush();
			} else if (setupInput.getMessage().equals(Constants.SETUP_SETUPNUM)) {
				if (lobbyServer.getToBeCreated() && lobbyServer.getClientPosition(this) == 0) {
					if (setupInput.getNumber() < 4 && setupInput.getNumber() > 1) {
						lobbyServer.setPlayerNumber(setupInput.getNumber(), this);
						setupOutput = new NetSetup(Constants.SETUP_CREATE_WORKED);
						output.writeObject(setupOutput);
						output.flush();
					} else {
						setupOutput = new NetSetup(Constants.SETUP_ERROR);
						output.writeObject(setupOutput);
						output.flush();
					}
				} else {
					setupOutput = new NetSetup(Constants.SETUP_ERROR);
					output.writeObject(setupOutput);
					output.flush();
				}
			} else {
				setupOutput = new NetSetup(Constants.SETUP_ERROR);
				output.writeObject(setupOutput);
				output.flush();
			}
		} catch (IOException e) {
			shouldRemove = joinedGame;
		} catch (ClassNotFoundException e) {
			setupOutput = new NetSetup(Constants.SETUP_OUT_CONNERROR);
			try {
				output.writeObject(setupOutput);
				output.flush();
			} catch (IOException ex) {
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
				shouldRemove = joinedGame;
			}
		} catch (IllegalCallerException ex) {
			throw new AssertionError("A thread called getClientPosition() or setPlayerNumber() without representing a client");
		} catch (IllegalArgumentException ex) {
			throw new AssertionError("Dimension of the game passed to the server was wrong");
		}
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
