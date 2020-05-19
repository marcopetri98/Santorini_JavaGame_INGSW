package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.network.ClientMessageListener;
import it.polimi.ingsw.network.objects.*;
import it.polimi.ingsw.ui.GraphicInterface;
import it.polimi.ingsw.ui.gui.viewModel.GameState;
import it.polimi.ingsw.util.Constants;
import javafx.application.Platform;

import java.io.IOException;

/**
 * This is a class which implements the Singleton pattern
 */
public class MainGuiController implements GraphicInterface {
	private static MainGuiController guiController;
	private ClientMessageListener listener;
	private GameState gameState;
	private SceneController sceneController;
	private boolean open;

	private MainGuiController() {
		super();
		gameState = new GameState();
		open = true;
	}
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
	public void setListener(ClientMessageListener listener) throws NullPointerException {
		if (listener == null) {
			throw new NullPointerException();
		}
		this.listener = listener;
	}
	public void setSceneController(SceneController controller) {
		sceneController = controller;
	}

	/* **********************************************
	 *												*
	 *			GETTERS FOR THIS CLASS				*
	 * 												*
	 ************************************************/
	public GameState getGameState() {
		return gameState;
	}
	public SceneController getSceneController() {
		return sceneController;
	}
	public ClientMessageListener getListener() {
		return listener;
	}

	/* **********************************************
	 *												*
	 *	METHOD TO COMMUNICATE WITH NETWORK OBJECT	*
	 * 												*
	 ************************************************/
	public void refresh() {
		gameState.refresh();
		open = true;
		listener.setWantsToPlay(false);
		listener.setActive(false);
		listener = new ClientMessageListener(guiController);
		listener.start();
	}
	public void sendMessage(NetObject msg) {
		listener.sendMessage(msg);
	}
	public boolean connectToServer(String serverAddress) {
		if (listener.connectToServer(serverAddress)) {
			listener.setWantsToPlay(true);
			return true;
		} else {
			return false;
		}
	}
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
	@Override
	public void retrieveError() {
		if (open && sceneController != null) {
			Platform.runLater(() -> sceneController.fatalError());
		}
	}
	@Override
	public void retrieveConnectionError() {
		if (sceneController != null) {
			Platform.runLater(() -> sceneController.fatalError());
		}
	}
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
