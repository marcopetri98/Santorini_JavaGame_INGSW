package it.polimi.ingsw;

// necessary imports from other packages of the project
import it.polimi.ingsw.network.ClientGUI;

/**
 * This class is the base App class for GUI clients, it starts the GUI client as
 * an object, the client lives until the player doesn't close the game.
 */
public class ClientGUIApp {
	public static void main(String[] args) {
		new Thread(new ClientGUI()).start();
	}
}
