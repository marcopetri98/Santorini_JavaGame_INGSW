package it.polimi.ingsw.network;

import com.sun.source.tree.NewArrayTree;
import it.polimi.ingsw.network.objects.*;
import it.polimi.ingsw.ui.GraphicInterface;
import it.polimi.ingsw.util.Constants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientMessageListener extends Thread {
	private GraphicInterface viewController;
	private Socket serverSocket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private NetworkPhase currentPhase;
	private boolean active;
	private boolean wantsToPlay;
	private final Object startLock, stateLock, inputLock, outputLock;

	public ClientMessageListener(GraphicInterface controller) {
		viewController = controller;
		active = true;
		wantsToPlay = false;
		startLock = new Object();
		stateLock = new Object();
		inputLock = new Object();
		outputLock = new Object();
	}

	@Override
	public void run() {
		NetObject ingoingObject = null;

		while (active) {
			// waits until the player select a server where to play or close the application
			while (!wantsToPlay && active) {
				synchronized (startLock) {
					try {
						startLock.wait();
					} catch (InterruptedException e) {
						throw new AssertionError("Someone interrupted the wait");
					}
				}
			}

			// the player has chosen a server and here the listener listens for messages from the server
			if (active) {
				try {
					ingoingObject = (NetObject) input.readObject();
				} catch (IOException | ClassNotFoundException e) {
					setActive(false);
				}

				if (ingoingObject != null) {
					switch (currentPhase) {
						case PRELOBBY:
							if (ingoingObject instanceof NetSetup) {
								parseSetupInput((NetSetup) ingoingObject);
							}
							break;

						case LOBBY:
							if (ingoingObject instanceof NetLobbyPreparation) {
								parseLobbyInput((NetLobbyPreparation)ingoingObject);
							}
							break;

						case COLORS:
							if (ingoingObject instanceof NetColorPreparation) {
								parseColorInput((NetColorPreparation)ingoingObject);
							}
							break;

						case GODS:
							if (ingoingObject instanceof NetDivinityChoice) {
								parseDivinityInput((NetDivinityChoice)ingoingObject);
							}
							break;

						case SETUP:
							if (ingoingObject instanceof NetGameSetup) {
								parseGameSetupInput((NetGameSetup)ingoingObject);
							}
							break;

						default:
							if (ingoingObject instanceof NetGaming) {
								parseGamingInput((NetGaming)ingoingObject);
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
	 *	METHODS USED TO PARSE SERVER MESSAGES		*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	public void parseSetupInput(NetSetup msg) {
		viewController.retrieveConnectionMsg(msg);
	}
	public void parseLobbyInput(NetLobbyPreparation msg) {

	}
	public void parseColorInput(NetColorPreparation msg) {

	}
	public void parseDivinityInput(NetDivinityChoice msg) {

	}
	public void parseGameSetupInput(NetGameSetup msg) {

	}
	public void parseGamingInput(NetGaming msg) {

	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *	METHODS USED TO SEND MESSAGES TO THE SERVER	*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	public void connectToServer(String address) {
		try {
			serverSocket = new Socket(address,21005);
			input = new ObjectInputStream(serverSocket.getInputStream());
			output = new ObjectOutputStream(serverSocket.getOutputStream());
			currentPhase = NetworkPhase.PRELOBBY;
		} catch (IOException e) {
			viewController.retrieveConnectionError();
		}
	}
	public void sendMessage(NetObject message) {
		synchronized (outputLock) {
			try {
				output.writeObject(message);
				output.flush();
			} catch (IOException e) {
				setActive(false);
			}
		}
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *	SETTERS OF THIS CLASS USED TO CHANGE STATE	*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	public void setActive(boolean value) {
		synchronized (stateLock) {
			active = value;
			if (!value) {
				synchronized (stateLock) {
					stateLock.notifyAll();
				}
			}
		}
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *	GETTERS OF THIS CLASS USED TO CHANGE STATE	*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	public boolean getActive() {
		synchronized (stateLock) {
			return active;
		}
	}
	public void activePlay() {
		synchronized (startLock) {
			wantsToPlay = true;
		}
	}
}
