package it.polimi.ingsw;

// necessary imports from other packages of the project
import it.polimi.ingsw.network.ClientMessageListener;
import it.polimi.ingsw.ui.cli.controller.MainCliController;
import it.polimi.ingsw.ui.cli.view.CliGame;
import it.polimi.ingsw.ui.cli.view.CliInitial;
import it.polimi.ingsw.ui.cli.view.CliInput;

/**
 * This class is the base App class for CLI clients, it starts the CLI client, the client lives until the player doesn't close the game.
 */
public class ClientCLIApp {
	public static void main(String[] args) {
		MainCliController controller;
		ClientMessageListener serverListener;
		CliInitial pregame;
		CliGame game;
		boolean functioning;
		int lobbyResult;

		functioning = true;
		while (functioning) {
			CliInput inputGetter = new CliInput();
			pregame = new CliInitial(inputGetter);
			game = new CliGame(inputGetter);
			controller = new MainCliController(pregame);
			serverListener = new ClientMessageListener(controller);
			controller.setListener(serverListener);
			controller.setGameView(game);
			controller.setInputHandler(inputGetter);
			serverListener.setDaemon(true);
			serverListener.start();

			if (pregame.menu() == 0) {
				lobbyResult = pregame.lobbyCli();
				if (lobbyResult == 0) {
					game.start();
				}
			} else {
				functioning = false;
			}
		}
	}
}
