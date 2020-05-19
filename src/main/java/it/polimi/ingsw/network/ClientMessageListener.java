package it.polimi.ingsw.network;

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
	private boolean disconnected;
	private boolean active;
	private boolean wantsToPlay;
	private final Object startLock, stateLock, inputLock, outputLock;

	public ClientMessageListener(GraphicInterface controller) {
		super("ClientMessageListener");
		viewController = controller;
		active = true;
		wantsToPlay = false;
		disconnected = false;
		startLock = new Object();
		stateLock = new Object();
		inputLock = new Object();
		outputLock = new Object();
		currentPhase = NetworkPhase.PRELOBBY;
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
				} catch (ClassNotFoundException e) {
					setActive(false);
				} catch (IOException e) {
					setActive(false);
					if (!disconnected) {
						viewController.retrieveConnectionError();
					}
				}

				if (ingoingObject != null && active) {
					if (ingoingObject.message.equals(Constants.GENERAL_FATAL_ERROR)) {
						viewController.retrieveError();
					} else if (ingoingObject.message.equals(Constants.CHECK)) {
						sendMessage(new NetObject(Constants.CHECK));
					} else {
						switch (currentPhase) {
							case PRELOBBY:
								if (ingoingObject instanceof NetSetup) {
									parseSetupInput((NetSetup) ingoingObject);
								}
								break;

							case LOBBY:
								if (ingoingObject instanceof NetLobbyPreparation) {
									parseLobbyInput((NetLobbyPreparation) ingoingObject);
								}
								break;

							case COLORS:
								if (ingoingObject instanceof NetColorPreparation) {
									parseColorInput((NetColorPreparation) ingoingObject);
								}
								break;

							case GODS:
								if (ingoingObject instanceof NetDivinityChoice) {
									parseDivinityInput((NetDivinityChoice) ingoingObject);
								}
								break;

							case SETUP:
								if (ingoingObject instanceof NetGameSetup) {
									parseGameSetupInput((NetGameSetup) ingoingObject);
								}
								break;

							default:
								if (ingoingObject instanceof NetGaming) {
									parseGamingInput((NetGaming) ingoingObject);
								}
								break;
						}
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
	private void parseSetupInput(NetSetup msg) {
		if (msg.message.equals(Constants.SETUP_CREATE_WORKED) || msg.message.equals(Constants.SETUP_OUT_CONNWORKED) || msg.message.equals(Constants.SETUP_OUT_CONNFINISH)) {
			currentPhase = NetworkPhase.LOBBY;
		}
		viewController.retrieveConnectionMsg(msg);
	}
	private void parseLobbyInput(NetLobbyPreparation msg) {
		if (msg.message.equals(Constants.GENERAL_PHASE_UPDATE)) {
			currentPhase = NetworkPhase.COLORS;
		}
		viewController.retrieveLobbyMsg(msg);
	}
	private void parseColorInput(NetColorPreparation msg) {
		if (msg.message.equals(Constants.GENERAL_PHASE_UPDATE)) {
			currentPhase = NetworkPhase.GODS;
		}
		viewController.retrieveColorMsg(msg);
	}
	private void parseDivinityInput(NetDivinityChoice msg) {
		if (msg.message.equals(Constants.GENERAL_PHASE_UPDATE) && msg.godsEnd) {
			currentPhase = NetworkPhase.SETUP;
		}
		viewController.retrieveGodsMsg(msg);
	}
	private void parseGameSetupInput(NetGameSetup msg) {
		if (msg.message.equals(Constants.GENERAL_PHASE_UPDATE)) {
			currentPhase = NetworkPhase.PLAYERTURN;
		}
		viewController.retrieveGameSetupMsg(msg);
	}
	private void parseGamingInput(NetGaming msg) {
		viewController.retrieveGamingMsg(msg);
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
	public boolean connectToServer(String address) {
		try {
			serverSocket = new Socket(address,21005);
			output = new ObjectOutputStream(serverSocket.getOutputStream());
			input = new ObjectInputStream(serverSocket.getInputStream());
			currentPhase = NetworkPhase.PRELOBBY;
			return true;
		} catch (IOException e) {
			viewController.retrieveConnectionError();
			setActive(false);
			return false;
		}
	}
	public void sendMessage(NetObject message) {
		synchronized (outputLock) {
			try {
				if (message.message.equals(Constants.GENERAL_DISCONNECT)) {
					disconnected = true;
				}
				output.writeObject(message);
				output.flush();
				output.reset();
			} catch (IOException e) {
				// server has crashed and it stops to send messages
				viewController.retrieveError();
				setActive(false);
			}
		}
	}
	public void closeSocketAndTerminate() {
		try {
			serverSocket.close();
			setActive(false);
		} catch (IOException e2) {
			setActive(false);
		}
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 * MODIFIERS OF THIS CLASS USED TO CHANGE STATE	*
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
	public void setWantsToPlay(boolean value) {
		synchronized (startLock) {
			wantsToPlay = value;
			startLock.notifyAll();
		}
	}
	public void resetListening() {
		synchronized (startLock) {
			wantsToPlay = false;
			currentPhase = NetworkPhase.PRELOBBY;
			startLock.notifyAll();
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
	public boolean getWantsToPlay() {
		synchronized (startLock) {
			return wantsToPlay;
		}
	}
}
