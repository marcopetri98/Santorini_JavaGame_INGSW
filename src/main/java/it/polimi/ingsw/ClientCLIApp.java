package it.polimi.ingsw;

// necessary imports from other packages of the project
import it.polimi.ingsw.network.ClientMessageListener;
import it.polimi.ingsw.ui.cli.controller.MainCliController;
import it.polimi.ingsw.ui.cli.view.CliGame;
import it.polimi.ingsw.ui.cli.view.CliInitial;
import it.polimi.ingsw.ui.cli.view.CliInput;

/**
 * This class is the base App class for CLI clients, it starts the CLI client, the client lives until the player doesn't close the game. It loops for an execution until the game does not quit the game.
 */
public class ClientCLIApp {
	public static void main(String[] args) {
		MainCliController controller;
		ClientMessageListener serverListener;
		CliInitial pregame;
		CliGame game;
		boolean functioning;
		int menuResult;
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

			menuResult = pregame.menu();
			if (menuResult == 0) {
				lobbyResult = pregame.lobbyCli();
				if (lobbyResult == 0) {
					controller.setPregameStage(false);
					game.start();
					serverListener.setActive(false);
				}
			} else if (menuResult == 1) {
				functioning = false;
			}
		}
	}
}
