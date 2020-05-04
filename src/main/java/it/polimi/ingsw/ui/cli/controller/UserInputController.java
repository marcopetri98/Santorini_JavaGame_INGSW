package it.polimi.ingsw.ui.cli.controller;

import it.polimi.ingsw.network.ClientMessageListener;
import it.polimi.ingsw.ui.cli.view.Command;

public class UserInputController {
	private ClientMessageListener listener;
	private String message;

	public UserInputController(ClientMessageListener listener) {
		this.listener = listener;
	}

	/**
	 *
	 * @param command which will be translated in a massage the server can read
	 */
	public boolean getCommand(Command command){
		message = command.commandType;
		//TODO: send this message to the server
		//return true if server received. otherwise:false
		return false;
	}

	public void connect(Command command) {

	}
}
