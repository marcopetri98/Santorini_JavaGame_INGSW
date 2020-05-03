package it.polimi.ingsw.network;

// necessary imports from other packages of the project
import it.polimi.ingsw.controller.ServerController;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.ui.CliMenu;
import it.polimi.ingsw.ui.GraphicInterface;
import it.polimi.ingsw.ui.GraphicMenu;
import it.polimi.ingsw.ui.cli.controller.MainCliController;
import it.polimi.ingsw.ui.cli.view.CliGame;
import it.polimi.ingsw.ui.cli.view.CliInitial;
import it.polimi.ingsw.ui.cli.view.CliInput;
import it.polimi.ingsw.util.Constants;
import jdk.jfr.Percentage;

// necessary imports of Java SE
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

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
		while (functioning) {
			CliInput inputGetter = new CliInput();
			// TODO: insert after cli push when parameters are all inserted
			/*pregame = new CliInitial(CliInput);
			controller = new MainCliController(pregame);
			serverListener = new ClientMessageListener(controller);
			serverListener.start();
			if (pregame.menu() == 0) {
				// TODO: shouldn't return a value to indicate that the client hasn't disconnected?
				pregame.lobbyCli();
				game = new CliGame();
				controller.setGameView(game);
				// TODO: shouldn't return a value to indicate that the client hasn't disconnected?
				game.start();
			} else {
				functioning = false;
			}*/
		}
	}
}
