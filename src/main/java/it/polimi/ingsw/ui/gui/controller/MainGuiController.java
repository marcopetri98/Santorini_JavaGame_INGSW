package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.network.ClientMessageListener;
import it.polimi.ingsw.network.objects.*;
import it.polimi.ingsw.ui.GraphicInterface;
import it.polimi.ingsw.ui.gui.viewModel.GameState;

/**
 * This is a class which implements the Singleton pattern
 */
public class MainGuiController implements GraphicInterface {
	private static MainGuiController guiController;
	private ClientMessageListener listener;
	private GameState gameState;
	private SceneController sceneController;

	private MainGuiController() {
		super();
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
	public void createGameState() {
		gameState = new GameState();
	}
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

	/* **********************************************
	 *												*
	 *				OVERRIDDEN METHODS				*
	 * 												*
	 ************************************************/
	@Override
	public void retrieveError() {
		sceneController.fatalError();
	}
	@Override
	public void retrieveConnectionError() {
		sceneController.fatalError();
	}
	@Override
	public void retrieveConnectionMsg(NetSetup connMsg) {
		sceneController.deposeMessage(connMsg);
	}
	@Override
	public void retrieveLobbyMsg(NetLobbyPreparation lobbyMsg) {
		sceneController.deposeMessage(lobbyMsg);
	}
	@Override
	public void retrieveColorMsg(NetColorPreparation colorMsg) {
		sceneController.deposeMessage(colorMsg);
	}
	@Override
	public void retrieveGodsMsg(NetDivinityChoice godsMsg) {
		sceneController.deposeMessage(godsMsg);
	}
	@Override
	public void retrieveGameSetupMsg(NetGameSetup gameSetupMsg) {
		sceneController.deposeMessage(gameSetupMsg);
	}
	@Override
	public void retrieveGamingMsg(NetGaming gamingMsg) {
		sceneController.deposeMessage(gamingMsg);
	}
}
