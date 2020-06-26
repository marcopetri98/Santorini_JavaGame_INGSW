package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.network.objects.NetObject;

import java.io.IOException;

/**
 * This is the interface used by every scene of the GUI, this interface has two methods to handle errors and messages that arrives from the server.
 */
public interface SceneController {
	/**
	 * This methods handles an error from the server.
	 */
	void fatalError();
	/**
	 * This methods handles messages from the server.
	 * @param message is the message arrived from the server
	 * @throws IOException if there has been an error handling the message
	 */
	void deposeMessage(NetObject message) throws IOException;
}
