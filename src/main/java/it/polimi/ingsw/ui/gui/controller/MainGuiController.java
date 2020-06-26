package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.network.ClientMessageListener;
import it.polimi.ingsw.network.objects.*;
import it.polimi.ingsw.ui.GraphicInterface;
import it.polimi.ingsw.ui.gui.viewModel.GameState;
import it.polimi.ingsw.util.Constants;
import javafx.application.Platform;

import java.io.IOException;

/**
 * This is a class which implements the Singleton pattern and is the main controller class for the GUI version of the game, it is the class which interacts with the client network component and forwards the messages to the appropriate JavaFX controller.
 */
public class MainGuiController implements GraphicInterface {
	private static MainGuiController guiController;
	private ClientMessageListener listener;
	private GameState gameState;
	private SceneController sceneController;
	/**
	 * This value says if the client is not closing the game.
	 */
	private boolean open;

	/**
	 * Creates the controller for the first time.
	 */
	private MainGuiController() {
		super();
		gameState = new GameState();
		open = true;
	}
	/**
	 * Gets the current existing instance of the controller, if it doesn't exist it creates an instance and returns it.
	 * @return the instance of the controller
	 */
	public static MainGuiController getInstance() {
		if (guiController == null) {
			guiController = new MainGuiController();
		}
		return guiController;
	}

	/* **********************************************
	 *												*
	 *			SETTERS FOR THIS CLASS				*
	 * 												*
	 ************************************************/
	/**
	 * Sets the listener.
	 * @param listener is the listener to set
	 * @throws NullPointerException is {@code listener} is null
	 */
	public void setListener(ClientMessageListener listener) throws NullPointerException {
		if (listener == null) {
			throw new NullPointerException();
		}
		this.listener = listener;
	}
	/**
	 * Sets the {@link #sceneController}.
	 * @param controller is the current {@link it.polimi.ingsw.ui.gui.controller.SceneController}
	 */
	public void setSceneController(SceneController controller) {
		sceneController = controller;
	}

	/* **********************************************
	 *												*
	 *			GETTERS FOR THIS CLASS				*
	 * 												*
	 ************************************************/
	/**
	 * Gets the current game state object.
	 * @return the instance of {@link #gameState}
	 */
	public GameState getGameState() {
		return gameState;
	}
	/**
	 * Gets the current scene controller.
	 * @return the current {@link #sceneController}
	 */
	public SceneController getSceneController() {
		return sceneController;
	}
	/**
	 * Gets the listener.
	 * @return the instance of the current {@link #listener}
	 */
	public ClientMessageListener getListener() {
		return listener;
	}

	/* **********************************************
	 *												*
	 *	METHOD TO COMMUNICATE WITH NETWORK OBJECT	*
	 * 												*
	 ************************************************/
	/**
	 * Re-initialize the controller's attributes.
	 */
	public void refresh() {
		gameState.refresh();
		open = true;
		listener.setWantsToPlay(false);
		listener.setActive(false);
		listener = new ClientMessageListener(guiController);
		listener.start();
	}
	/**
	 * Send a message to the server through the listener.
	 * @param msg is the message to send
	 */
	public void sendMessage(NetObject msg) {
		listener.sendMessage(msg);
	}
	/**
	 * Connects to a Santorini server through the listener.
	 * @param serverAddress is the server's address
	 * @return true if the connection succeeded, false instead
	 */
	public boolean connectToServer(String serverAddress) {
		if (listener.connectToServer(serverAddress)) {
			listener.setWantsToPlay(true);
			return true;
		} else {
			return false;
		}
	}
	/**
	 * It sends a message of disconnection to the server because the player is closing the game.
	 */
	public void closeDisconnect() {
		switch (gameState.getTurn().getPhase()) {
			case PRELOBBY -> {
				NetSetup message = new NetSetup(Constants.GENERAL_DISCONNECT);
				open = false;
				listener.sendMessage(message);
			}
			case LOBBY -> {
				NetLobbyPreparation message = new NetLobbyPreparation(Constants.GENERAL_DISCONNECT);
				open = false;
				listener.sendMessage(message);
			}
			case COLORS -> {
				NetColorPreparation message = new NetColorPreparation(Constants.GENERAL_DISCONNECT);
				open = false;
				listener.sendMessage(message);
			}
			case GODS -> {
				NetDivinityChoice message = new NetDivinityChoice(Constants.GENERAL_DISCONNECT);
				open = false;
				listener.sendMessage(message);
			}
			case SETUP -> {
				NetGameSetup message = new NetGameSetup(Constants.GENERAL_DISCONNECT);
				open = false;
				listener.sendMessage(message);
			}
			case PLAYERTURN -> {
				NetGaming message = new NetGaming(Constants.GENERAL_DISCONNECT);
				open = false;
				listener.sendMessage(message);
			}
		}
	}

	/* **********************************************
	 *												*
	 *				OVERRIDDEN METHODS				*
	 * 												*
	 ************************************************/
	/**
	 * When this method is called there has been an error and the information.
	 */
	@Override
	public void retrieveError() {
		if (open && sceneController != null) {
			Platform.runLater(() -> sceneController.fatalError());
		}
	}
	/**
	 * When this method is called there has been a connection error and the information is sent to the current JavaFX scene.
	 */
	@Override
	public void retrieveConnectionError() {
		if (sceneController != null) {
			Platform.runLater(() -> sceneController.fatalError());
		}
	}
	/**
	 * The message is sent to the current JavaFX scene to change it or handle changes.
	 * @param connMsg is a setup message from the server
	 */
	@Override
	public void retrieveConnectionMsg(NetSetup connMsg) {
		Platform.runLater(() -> {
			try {
				sceneController.deposeMessage(connMsg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	/**
	 * The message is sent to the current JavaFX scene to change it or handle changes.
	 * @param lobbyMsg is a lobby message from the server
	 */
	@Override
	public void retrieveLobbyMsg(NetLobbyPreparation lobbyMsg) {
		Platform.runLater(() -> {
			try {
				sceneController.deposeMessage(lobbyMsg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	/**
	 * The message is sent to the current JavaFX scene to change it or handle changes.
	 * @param colorMsg is a color message from the server
	 */
	@Override
	public void retrieveColorMsg(NetColorPreparation colorMsg) {
		Platform.runLater(() -> {
			try {
				sceneController.deposeMessage(colorMsg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	/**
	 * The message is sent to the current JavaFX scene to change it or handle changes.
	 * @param godsMsg is a gods message from the server
	 */
	@Override
	public void retrieveGodsMsg(NetDivinityChoice godsMsg) {
		Platform.runLater(() -> {
			try {
				sceneController.deposeMessage(godsMsg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	/**
	 * The message is sent to the current JavaFX scene to change it or handle changes.
	 * @param gameSetupMsg is a workers position message from the server
	 */
	@Override
	public void retrieveGameSetupMsg(NetGameSetup gameSetupMsg) {
		Platform.runLater(() -> {
			try {
				sceneController.deposeMessage(gameSetupMsg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	/**
	 * The message is sent to the current JavaFX scene to change it or handle changes.
	 * @param gamingMsg is a gaming message from the server
	 */
	@Override
	public void retrieveGamingMsg(NetGaming gamingMsg) {
		Platform.runLater(() -> {
			try {
				sceneController.deposeMessage(gamingMsg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
