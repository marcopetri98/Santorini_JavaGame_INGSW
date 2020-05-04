package it.polimi.ingsw.ui.cli.controller;

import it.polimi.ingsw.network.ClientMessageListener;
import it.polimi.ingsw.network.NetworkPhase;
import it.polimi.ingsw.network.game.NetMap;
import it.polimi.ingsw.network.objects.NetSetup;
import it.polimi.ingsw.ui.GraphicInterface;
import it.polimi.ingsw.ui.cli.view.CliGame;
import it.polimi.ingsw.ui.cli.view.CliInitial;
import it.polimi.ingsw.ui.cli.view.CliInput;
import it.polimi.ingsw.util.Constants;

import java.net.Socket;
import java.util.List;

public class MainCliController implements GraphicInterface {
	private CliGame gameView;
	private CliInitial pregameView;
	private CliInput inputHandler;
	private ClientMessageListener listener;
	private UserInputController inputController;
	private NetworkPhase currentPhase;
	private NetMap currentMap;
	private List<String> players;

	public MainCliController(CliInitial pregameView) {
		this.pregameView = pregameView;
		currentPhase = NetworkPhase.PRELOBBY;
		inputController = new UserInputController(listener);
		pregameView.setUserInputController(inputController);
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
		pregameView = null;
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
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *	GETTERS OF THIS CLASS USED TO GAIN INFO		*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/


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

	}
	@Override
	public void retrieveConnectionError() {
		// TODO: insert after cli push when parameters are known
		inputHandler.setTimeout();
		// pregameView.printError();
	}
	@Override
	public void retrieveConnectionMsg(NetSetup connMsg) {
		// TODO: insert after cli push when parameters are known
		switch (connMsg.message) {
			case Constants.SETUP_OUT_CONNFAILED, Constants.SETUP_CREATE_WORKED, Constants.SETUP_OUT_CONNWORKED, Constants.SETUP_ERROR -> {
				pregameView.queueMessageMenu(connMsg);
				pregameView.notifyCliMenu();
			}
			case Constants.SETUP_OUT_CONNFINISH, Constants.SETUP_CREATE -> {
				inputHandler.setTimeout();
				pregameView.queueMessageMenu(connMsg);
			}
		}
	}
	@Override
	public void retrieveClientTurn() {

	}
	@Override
	public void retrievePhaseChange() {

	}
	@Override
	public void retrieveGameStart() {

	}
	@Override
	public void retrieveOtherColor() {

	}
	@Override
	public void retrieveChallenger() {

	}
	@Override
	public void retrieveGodsSelection() {

	}
	@Override
	public void retrieveOtherGods() {

	}
	@Override
	public void retrieveStarter() {

	}
	@Override
	public void retrieveOtherWorkers(NetMap map) {

	}
	@Override
	public void retrieveAction() {

	}
	@Override
	public void retrieveOtherAction() {

	}
	@Override
	public void retrieveWinner() {

	}
	@Override
	public void retrieveDefeated() {

	}
	@Override
	public void retrieveDisconnection() {

	}
}
