package it.polimi.ingsw;

// necessary imports from other packages of the project
import it.polimi.ingsw.network.ClientCLI;

/**
 * This class is the base App class for CLI clients, it starts the CLI client as
 * an object, the client lives until the player doesn't close the game.
 */
public class ClientCLIApp {
	public static void main(String[] args) {
		new Thread(new ClientCLI(),"ClientCliMainThread").start();
	}
}
