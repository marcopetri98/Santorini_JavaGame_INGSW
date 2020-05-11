package it.polimi.ingsw.ui.cli.controller;

import it.polimi.ingsw.network.ClientMessageListener;
import it.polimi.ingsw.network.objects.*;
import it.polimi.ingsw.ui.GraphicInterface;
import it.polimi.ingsw.ui.cli.view.CliGame;
import it.polimi.ingsw.ui.cli.view.CliInitial;
import it.polimi.ingsw.ui.cli.view.CliInput;
import it.polimi.ingsw.util.Constants;

public class MainCliController implements GraphicInterface {
	private CliGame gameView;
	private CliInitial pregameView;
	private CliInput inputHandler;
	private ClientMessageListener listener;
	private UserInputController inputController;

	public MainCliController(CliInitial pregameView) {
		this.pregameView = pregameView;
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
	public void setGameView(CliGame game) throws NullPointerException {
		if (game == null) {
			throw new NullPointerException();
		}
		gameView = game;
		gameView.setInputController(inputController);
		inputController.setGameView(game);
	}
	public void setPregameView(CliInitial pregame) throws NullPointerException {
		if (pregame == null) {
			throw new NullPointerException();
		}
		gameView = null;
		pregameView = pregame;
	}
	public void setInputHandler(CliInput handler) throws NullPointerException {
		if (handler == null) {
			throw new NullPointerException();
		}
		inputHandler = handler;
	}
	public void setListener(ClientMessageListener listener) throws NullPointerException {
		this.listener = listener;
		inputController = new UserInputController(listener);
		pregameView.setUserInputController(inputController);
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
	@Override
	public void retrieveError() {
		// TODO: implement a way to inform the user that the server crashed
	}
	@Override
	public void retrieveConnectionError() {
		// TODO: insert after cli push when parameters are known
		inputHandler.setTimeout();
		// pregameView.printError();
	}
	/**
	 * It receives asynchronous or synchronous messages from the network component a message that is during the connection to a lobby to participate to a game
	 * @param connMsg
	 */
	@Override
	public void retrieveConnectionMsg(NetSetup connMsg) {
		switch (connMsg.message) {
			case Constants.SETUP_OUT_CONNFAILED, Constants.SETUP_CREATE_WORKED, Constants.SETUP_OUT_CONNWORKED, Constants.SETUP_ERROR, Constants.SETUP_CREATE, Constants.SETUP_OUT_CONNFINISH -> {
				pregameView.queueMessageMenu(connMsg);
				pregameView.notifyCliMenu();
			}
		}
	}
	/**
	 * It receives asynchronous or synchronous messages from the network component that there was an error on the message sent or the message about lobby end and list of players
	 * @param lobbyMsg
	 */
	@Override
	public void retrieveLobbyMsg(NetLobbyPreparation lobbyMsg) {
		switch (lobbyMsg.message) {
			case Constants.LOBBY_TURN, Constants.LOBBY_ERROR -> {
				inputHandler.setTimeout();
				pregameView.queueMessageLobby(lobbyMsg);
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
	 * It receives asynchronous or synchronous messages from the network component that there was an error on the message sent or a message about
	 * @param colorMsg
	 */
	@Override
	public void retrieveColorMsg(NetColorPreparation colorMsg) {
		inputHandler.setTimeout();
		gameView.addToQueue(colorMsg);
	}
	/**
	 * It receives asynchronous or synchronous messages from the network component that there was an error on the message sent or the message about
	 * @param godsMsg
	 */
	@Override
	public void retrieveGodsMsg(NetDivinityChoice godsMsg) {
		inputHandler.setTimeout();
		gameView.addToQueue(godsMsg);
	}
	/**
	 * It receives asynchronous or synchronous messages from the network component that there was an error on the message sent or the message about
	 * @param gameSetupMsg
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
	 * It receives asynchronous or synchronous messages from the network component that there was an error on the message sent or the message about
	 * @param gamingMsg
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
