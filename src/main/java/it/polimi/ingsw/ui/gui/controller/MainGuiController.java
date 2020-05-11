package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.network.ClientMessageListener;
import it.polimi.ingsw.network.objects.*;
import it.polimi.ingsw.ui.GraphicInterface;

/**
 * This is a class which implements the Singleton pattern
 */
public class MainGuiController implements GraphicInterface {
	private static MainGuiController guiController;
	private ClientMessageListener listener;

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
	 *												*
	 *			SETTERS FOR THIS CLASS				*
	 * 												*
	 * 												*
	 ************************************************/
	public void setListener(ClientMessageListener listener) throws NullPointerException {
		if (listener == null) {
			throw new NullPointerException();
		}
		this.listener = listener;
	}

	/* **********************************************
	 *												*
	 *												*
	 *				OVERRIDDEN METHODS				*
	 * 												*
	 * 												*
	 ************************************************/
	@Override
	public void retrieveError() {

	}
	@Override
	public void retrieveConnectionError() {

	}
	@Override
	public void retrieveConnectionMsg(NetSetup connMsg) {

	}
	@Override
	public void retrieveLobbyMsg(NetLobbyPreparation lobbyMsg) {

	}
	@Override
	public void retrieveColorMsg(NetColorPreparation colorMsg) {

	}
	@Override
	public void retrieveGodsMsg(NetDivinityChoice godsMsg) {

	}
	@Override
	public void retrieveGameSetupMsg(NetGameSetup gameSetupMsg) {

	}
	@Override
	public void retrieveGamingMsg(NetGaming gamingMsg) {

	}
}
