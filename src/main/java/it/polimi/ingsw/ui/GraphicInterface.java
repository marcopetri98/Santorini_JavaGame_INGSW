package it.polimi.ingsw.ui;

// necessary imports of Java SE for ServerController class
import it.polimi.ingsw.network.objects.*;

/**
 * This interface is the base class that represent the actions that must be performed by the GUI and CLI clients.
 */
public interface GraphicInterface {
	/**
	 * When this method is called there has been an error and the information.
	 */
	void retrieveError();
	/**
	 * When this method is called there has been a connection error and the information.
	 */
	void retrieveConnectionError();
	/**
	 * When this method is called a message must be processed by the client, the message will be sent to a queue to wait to be read.
	 * @param connMsg is a setup message from the server
	 */
	void retrieveConnectionMsg(NetSetup connMsg);
	/**
	 * When this method is called a message must be processed by the client, the message will be sent to a queue to wait to be read.
	 * @param lobbyMsg is a lobby message from the server
	 */
	void retrieveLobbyMsg(NetLobbyPreparation lobbyMsg);
	/**
	 * When this method is called a message must be processed by the client, the message will be sent to a queue to wait to be read.
	 * @param colorMsg is a color message from the server
	 */
	void retrieveColorMsg(NetColorPreparation colorMsg);
	/**
	 * When this method is called a message must be processed by the client, the message will be sent to a queue to wait to be read.
	 * @param godsMsg is a gods message from the server
	 */
	void retrieveGodsMsg(NetDivinityChoice godsMsg);
	/**
	 * When this method is called a message must be processed by the client, the message will be sent to a queue to wait to be read.
	 * @param gameSetupMsg is a workers position message from the server
	 */
	void retrieveGameSetupMsg(NetGameSetup gameSetupMsg);
	/**
	 * When this method is called a message must be processed by the client, the message will be sent to a queue to wait to be read.
	 * @param gamingMsg is a gaming message from the server
	 */
	void retrieveGamingMsg(NetGaming gamingMsg);
}
