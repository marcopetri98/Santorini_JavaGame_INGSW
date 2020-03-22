package it.polimi.ingsw.network;

// necessary imports from other packages of the project
import it.polimi.ingsw.controller.ServerController;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.ui.CliMenu;
import it.polimi.ingsw.ui.GraphicInterface;
import it.polimi.ingsw.ui.GraphicMenu;

// necessary imports of Java SE
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ClientThread implements Runnable {
	private ServerController clientController;
	private Game clientGame;
	private GraphicInterface clientView;
	private Socket serverSocket;
	private GraphicMenu mainMenu;

	public void run() {
		Scanner receivedInput, input = new Scanner(System.in);
		PrintWriter sendOutput;
		String received, userInput;

		boolean continueConnection = true;

		System.out.print("Welcome user, which mode do you prefer (CLI [Command line interface] or GUI [Graphical interface])? ");
		userInput = input.nextLine().toUpperCase();
		if (userInput.equals("CLI")) {
			// client starts the CLI mode of interacting
			mainMenu = new CliMenu();
			while (continueConnection) {
				userInput = mainMenu.start();
				if (userInput.equals("1")) {
					try {
						continueConnection = !connectToServer(mainMenu.getServerAddress(), mainMenu.getNickname(), mainMenu.getPlayerNumber());
					} catch (IOException e) {
						// TODO: change this catch
						System.out.print("Socket connection error!");
						System.exit(1);
					}
				} else {
					System.out.print("See you the next time gamer!");
					continueConnection = false;
				}
			}

			// now the client is connected and need to wait to server's message to start the game
			try {
				receivedInput = new Scanner(serverSocket.getInputStream());
				sendOutput = new PrintWriter(serverSocket.getOutputStream());
				received = receivedInput.nextLine();
				if (received.equals("setup-connection-finished")) {
					// TODO: implements game creation
					System.out.println("Game is starting...");
				}
			} catch (IOException e) {
				// TODO: change this catch
				System.exit(1);
			}
		} else {
			// TODO: create GUI way of interacting of the player
		}
	}

	private boolean connectToServer(String server, String nickname, int numPlayer) throws IOException {
		String received;
		Scanner receivedInput;
		PrintWriter sendOutput;

		serverSocket = new Socket(server,21005);
		receivedInput = new Scanner(serverSocket.getInputStream());
		sendOutput = new PrintWriter(serverSocket.getOutputStream());
		sendOutput.println("setup-partecipate "+numPlayer+" "+nickname);
		sendOutput.flush();
		mainMenu.handleConnection(1);

		received = receivedInput.nextLine();
		if (received.equals("setup-connection-worked")) {
			mainMenu.handleConnection(2);
			return true;
		} else if (received.equals("setup-connection-failed")) {
			mainMenu.handleConnection(3);
			return false;
		} else {
			// TODO: insert and exception for communication error on the line
			mainMenu.handleConnection(4);
			return false;
		}
	}
}
