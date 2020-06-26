package it.polimi.ingsw.ui.cli.controller;

import it.polimi.ingsw.network.ClientMessageListener;
import it.polimi.ingsw.network.objects.*;
import it.polimi.ingsw.ui.GraphicInterface;
import it.polimi.ingsw.ui.cli.view.CliGame;
import it.polimi.ingsw.ui.cli.view.CliInitial;
import it.polimi.ingsw.ui.cli.view.CliInput;
import it.polimi.ingsw.util.Constants;

/**
 * This class is the base class for the Cli message management, it is the first client controller created and is necessary to receive messages from the server, it receives server messages and puts them into Cli queue and stops the input getter to get input setting a timeout on it.
 */
public class MainCliController implements GraphicInterface {
	private CliGame gameView;
	private CliInitial pregameView;
	private CliInput inputHandler;
	private ClientMessageListener listener;
	private UserInputController inputController;
	private boolean pregameStage;

	/**
	 * Creates an initial Cli controller
	 * @param pregameView menu cli view
	 */
	public MainCliController(CliInitial pregameView) {
		this.pregameView = pregameView;
		pregameStage = true;
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
	/**
	 * Set game view passing from the initial phase to the game phase.
	 * @param game a {@link it.polimi.ingsw.ui.cli.view.CliGame}
	 * @throws NullPointerException if {@code game} is null
	 */
	public void setGameView(CliGame game) throws NullPointerException {
		if (game == null) {
			throw new NullPointerException();
		}
		gameView = game;
		gameView.setInputController(inputController);
		inputController.setGameView(game);
	}
	/**
	 * Sets the input getter.
	 * @param handler a {@link it.polimi.ingsw.ui.cli.view.CliInput}
	 * @throws NullPointerException if {@code handler} is null
	 */
	public void setInputHandler(CliInput handler) throws NullPointerException {
		if (handler == null) {
			throw new NullPointerException();
		}
		inputHandler = handler;
	}
	/**
	 * Sets the message listener from the server.
	 * @param listener a {@link it.polimi.ingsw.network.ClientMessageListener}
	 * @throws NullPointerException if {@code listener} is null
	 */
	public void setListener(ClientMessageListener listener) throws NullPointerException {
		if (listener == null) {
			throw new NullPointerException();
		}
		this.listener = listener;
		inputController = new UserInputController(listener);
		pregameView.setUserInputController(inputController);
	}
	/**
	 * Sets a flag to say that the client is not yet in game, it is in lobby or in the menu.
	 * @param value a boolean value
	 */
	public void setPregameStage(boolean value) {
		pregameStage = value;
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *	METHODS OF THE VIEW CALLED BY THE NETWORK	*
	 * 	CLIENT COMPONENT (ClientCli)				*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	/**
	 * When this method is called there has been an error and the information.
	 */
	@Override
	public void retrieveError() {
		if (pregameStage) {
			if (pregameView.isMenuPhase()) {
				NetObject clientErrorMsg = new NetSetup(Constants.GENERAL_FATAL_ERROR);
				pregameView.queueMessage(clientErrorMsg);
				inputHandler.setTimeout();
			} else {
				NetObject clientErrorMsg = new NetLobbyPreparation(Constants.GENERAL_FATAL_ERROR);
				pregameView.queueMessage(clientErrorMsg);
				inputHandler.setTimeout();
			}
		} else {
			NetObject clientErrorMsg = new NetObject(Constants.GENERAL_FATAL_ERROR);
			gameView.addToQueue(clientErrorMsg);
			inputHandler.setTimeout();
		}
	}
	/**
	 * When this method is called there has been a connection error and the information.
	 */
	@Override
	public void retrieveConnectionError() {
		if (pregameStage) {
			if (pregameView.isMenuPhase()) {
				NetObject clientErrorMsg = new NetSetup(Constants.GENERAL_NOT_EXIST_SERVER);
				pregameView.queueMessage(clientErrorMsg);
				inputHandler.setTimeout();
			} else {
				NetObject clientErrorMsg = new NetLobbyPreparation(Constants.GENERAL_FATAL_ERROR);
				pregameView.queueMessage(clientErrorMsg);
				inputHandler.setTimeout();
			}
		} else {
			NetObject clientErrorMsg = new NetObject(Constants.GENERAL_FATAL_ERROR);
			gameView.addToQueue(clientErrorMsg);
			inputHandler.setTimeout();
		}
	}
	/**
	 * It receives asynchronous or synchronous messages from the network component a message that is during the connection to a lobby to participate to a game.
	 * @param connMsg a setup message
	 */
	@Override
	public void retrieveConnectionMsg(NetSetup connMsg) {
		switch (connMsg.message) {
			case Constants.SETUP_OUT_CONNFAILED, Constants.SETUP_CREATE_WORKED, Constants.SETUP_OUT_CONNWORKED, Constants.SETUP_ERROR, Constants.SETUP_CREATE, Constants.SETUP_OUT_CONNFINISH -> {
				pregameView.queueMessage(connMsg);
				pregameView.notifyPregameCli();
			}
		}
	}
	/**
	 * It receives asynchronous or synchronous messages from the network component that there was an error on the message sent or the message about lobby end and list of players.
	 * @param lobbyMsg a lobby message
	 */
	@Override
	public void retrieveLobbyMsg(NetLobbyPreparation lobbyMsg) {
		switch (lobbyMsg.message) {
			case Constants.LOBBY_TURN, Constants.LOBBY_ERROR -> {
				inputHandler.setTimeout();
				pregameView.queueMessage(lobbyMsg);
				if (lobbyMsg.message.equals(Constants.LOBBY_TURN)) {
					gameView.setPlayers(lobbyMsg);
				}
			}
			case Constants.GENERAL_PHASE_UPDATE -> {
				gameView.addToQueue(lobbyMsg);
			}
		}
	}
	/**
	 * It receives asynchronous or synchronous messages from the network component that there was an error on the message sent or a message about.
	 * @param colorMsg a color phase message
	 */
	@Override
	public void retrieveColorMsg(NetColorPreparation colorMsg) {
		inputHandler.setTimeout();
		gameView.addToQueue(colorMsg);
	}
	/**
	 * It receives asynchronous or synchronous messages from the network component that there was an error on the message sent or the message about.
	 * @param godsMsg a gods phase message
	 */
	@Override
	public void retrieveGodsMsg(NetDivinityChoice godsMsg) {
		inputHandler.setTimeout();
		gameView.addToQueue(godsMsg);
	}
	/**
	 * It receives asynchronous or synchronous messages from the network component that there was an error on the message sent or the message about.
	 * @param gameSetupMsg a workers position phase message
	 */
	@Override
	public void retrieveGameSetupMsg(NetGameSetup gameSetupMsg) {
		if (gameSetupMsg.message.equals(Constants.GENERAL_GAMEMAP_UPDATE)) {
			inputController.setMap(gameSetupMsg.gameMap);
		}
		inputHandler.setTimeout();
		gameView.addToQueue(gameSetupMsg);
	}
	/**
	 * It receives asynchronous or synchronous messages from the network component that there was an error on the message sent or the message about.
	 * @param gamingMsg a gaming message
	 */
	@Override
	public void retrieveGamingMsg(NetGaming gamingMsg) {
		if (gamingMsg.message.equals(Constants.GENERAL_GAMEMAP_UPDATE)) {
			inputController.setMap(gamingMsg.gameMap);
		}
		inputHandler.setTimeout();
		gameView.addToQueue(gamingMsg);
	}
}
