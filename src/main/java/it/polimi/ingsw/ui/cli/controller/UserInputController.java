package it.polimi.ingsw.ui.cli.controller;

import it.polimi.ingsw.ui.cli.view.Command;

public class UserInputController {
	private String message;

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
}
