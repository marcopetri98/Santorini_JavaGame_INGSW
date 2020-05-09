package it.polimi.ingsw.network;

// necessary imports from other packages of the project
import it.polimi.ingsw.ui.cli.controller.MainCliController;
import it.polimi.ingsw.ui.cli.view.CliGame;
import it.polimi.ingsw.ui.cli.view.CliInitial;
import it.polimi.ingsw.ui.cli.view.CliInput;

public class ClientCLI implements Runnable {
	private MainCliController controller;
	private ClientMessageListener serverListener;
	private CliInitial pregame;
	private CliGame game;
	private boolean functioning;

	public ClientCLI() {
		functioning = true;
	}

	@Override
	public void run() {
		int lobbyResult;

		while (functioning) {
			CliInput inputGetter = new CliInput();
			pregame = new CliInitial(inputGetter);
			controller = new MainCliController(pregame);
			serverListener = new ClientMessageListener(controller);
			controller.setListener(serverListener);
			controller.setInputHandler(inputGetter);
			serverListener.setDaemon(true);
			serverListener.start();

			if (pregame.menu() == 0) {
				lobbyResult = pregame.lobbyCli();
				if (lobbyResult == 0) {
					game = new CliGame();
					controller.setGameView(game);
					game.start();
				}
			} else {
				functioning = false;
			}
		}
	}
}
