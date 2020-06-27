package it.polimi.ingsw.network;

import it.polimi.ingsw.network.objects.*;
import it.polimi.ingsw.ui.GraphicInterface;
import it.polimi.ingsw.util.Constants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class is the base network class of the client that is intended to communicate with the server, it is used by a CLI client or by a GUI client.
 */
public class ClientMessageListener extends Thread {
	private GraphicInterface viewController;
	private Socket serverSocket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private NetworkPhase currentPhase;
	private boolean disconnected;
	private boolean active;
	private boolean wantsToPlay;
	private boolean finished;
	private final Object startLock, stateLock, inputLock, outputLock;

	/**
	 * It creates a client message listener for the graphic interface controller passed as parameter.
	 * @param controller is the graphic interface controller
	 */
	public ClientMessageListener(GraphicInterface controller) {
		super("ClientMessageListener");
		viewController = controller;
		active = true;
		wantsToPlay = false;
		disconnected = false;
		finished = false;
		startLock = new Object();
		stateLock = new Object();
		inputLock = new Object();
		outputLock = new Object();
		currentPhase = NetworkPhase.PRELOBBY;
	}

	/**
	 * This method listen for messages from the server and analyzes it and pass it to the parsing method for the current network phase.
	 */
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
					// if the server has disconnected there has been a connection error only if the player hasn't disconnected and the game hasn't finished
					if (!disconnected && !finished) {
						viewController.retrieveConnectionError();
					}
				}

				if (ingoingObject != null && active) {
					if (ingoingObject.message.equals(Constants.GENERAL_FATAL_ERROR)) {
						viewController.retrieveError();
					} else if (ingoingObject.message.equals(Constants.CHECK)) {
						sendMessage(new NetObject(Constants.CHECK));
					} else {
						if (ingoingObject.message.equals(Constants.GENERAL_WINNER) || ingoingObject.message.equals(Constants.GENERAL_PLAYER_DISCONNECTED) || ingoingObject.message.equals(Constants.GENERAL_SETUP_DISCONNECT)) {
							finished = true;
						}
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
	/**
	 * This method parses the setup messages sending them to the controller and it changes the network phase id a specific message arrives.
	 * @param msg is the message arrived from the server
	 */
	private void parseSetupInput(NetSetup msg) {
		if (msg.message.equals(Constants.SETUP_CREATE_WORKED) || msg.message.equals(Constants.SETUP_OUT_CONNWORKED) || msg.message.equals(Constants.SETUP_OUT_CONNFINISH)) {
			currentPhase = NetworkPhase.LOBBY;
		}
		viewController.retrieveConnectionMsg(msg);
	}
	/**
	 * This method parses the lobby messages sending them to the controller and it changes the network phase id a specific message arrives.
	 * @param msg is the message arrived from the server
	 */
	private void parseLobbyInput(NetLobbyPreparation msg) {
		if (msg.message.equals(Constants.GENERAL_PHASE_UPDATE)) {
			currentPhase = NetworkPhase.COLORS;
		}
		viewController.retrieveLobbyMsg(msg);
	}
	/**
	 * This method parses the color messages sending them to the controller and it changes the network phase id a specific message arrives.
	 * @param msg is the message arrived from the server
	 */
	private void parseColorInput(NetColorPreparation msg) {
		if (msg.message.equals(Constants.GENERAL_PHASE_UPDATE)) {
			currentPhase = NetworkPhase.GODS;
		}
		viewController.retrieveColorMsg(msg);
	}
	/**
	 * This method parses the gods phase messages sending them to the controller and it changes the network phase id a specific message arrives.
	 * @param msg is the message arrived from the server
	 */
	private void parseDivinityInput(NetDivinityChoice msg) {
		if (msg.message.equals(Constants.GENERAL_PHASE_UPDATE) && msg.godsEnd) {
			currentPhase = NetworkPhase.SETUP;
		}
		viewController.retrieveGodsMsg(msg);
	}
	/**
	 * This method parses the game setup messages sending to the controller and it changes the network phase if a specific message arrives.
	 * @param msg is the message arrived from the server
	 */
	private void parseGameSetupInput(NetGameSetup msg) {
		if (msg.message.equals(Constants.GENERAL_PHASE_UPDATE)) {
			currentPhase = NetworkPhase.PLAYERTURN;
		}
		viewController.retrieveGameSetupMsg(msg);
	}
	/**
	 * This method parses the gaming messages.
	 * @param msg is the message arrived from the server
	 */
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
	/**
	 * This methods try to connect the client with the specified server, it returns a boolean to indicate if the operation succeeded.
	 * @param address is the server address
	 * @return true if it succeed in connecting to the server
	 */
	public boolean connectToServer(String address) {
		try {
			serverSocket = new Socket(address, 21005);
			System.out.println("\n\n\t\tConnected or not connected to server\n\n");
			output = new ObjectOutputStream(serverSocket.getOutputStream());
			input = new ObjectInputStream(serverSocket.getInputStream());
			currentPhase = NetworkPhase.PRELOBBY;
			return true;
		} catch (UnknownHostException e) {
			viewController.retrieveConnectionError();
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			viewController.retrieveConnectionError();
			return false;
		}
	}
	/**
	 * It sends a message to the server writing to the output stream and after this it resets the stream.
	 * @param message is a {@link it.polimi.ingsw.network.objects.NetObject} to send to the server
	 */
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

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 * MODIFIERS OF THIS CLASS USED TO CHANGE STATE	*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	/**
	 * Sets a value used by the client message listener to compute if it must terminate the thread or if it must continue to work.
	 * @param value is a boolean value which represent if the client message listener is functioning
	 */
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
	/**
	 * Sets a flag inside the client message listener which indicate that the player want or does not want to play.
	 * @param value is the boolean value indicating if the player wants to play
	 */
	public void setWantsToPlay(boolean value) {
		synchronized (startLock) {
			wantsToPlay = value;
			startLock.notifyAll();
		}
	}
	/**
	 * It reset the listening putting the flag which indicated if the player wants to play to false and the active variable to true.
	 */
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
	/**
	 * Gets a value that represents if the client wants to play.
	 * @return true if the player wants to play, false instead
	 */
	public boolean getWantsToPlay() {
		synchronized (startLock) {
			return wantsToPlay;
		}
	}
}
