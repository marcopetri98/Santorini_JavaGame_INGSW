package it.polimi.ingsw.network;

// necessary imports from other packages of the project
import it.polimi.ingsw.controller.ServerController;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.ui.CliMenu;
import it.polimi.ingsw.ui.GraphicInterface;
import it.polimi.ingsw.ui.GraphicMenu;
import it.polimi.ingsw.util.Constants;

// necessary imports of Java SE
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientCLI implements Runnable {
	private ServerController clientController;
	private Game clientGame;
	private GraphicInterface clientView;
	private Socket serverSocket;
	private GraphicMenu mainMenu;

	public void run() {
		ServerController clientController;
		Game clientGame;
		GraphicInterface clientView;
		Scanner receivedInput, input = new Scanner(System.in);
		PrintWriter sendOutput;
		String received, userInput;

		boolean continueConnection = true;
		// client starts the CLI mode of interacting
		mainMenu = new CliMenu();
		while (continueConnection) {
			userInput = mainMenu.start();
			continueConnection = parseMainMenuInput(userInput);
		}
		// now the client is connected and need to wait to server's message to start the game
		try {
			gameCreation();
			gamePreparation();
		} catch (IOException e) {
			// TODO: change this catch
			System.exit(1);
		}
	}

	/** This is the function which connects to the server and to the lobby
	 * @param nickname it is the player nickname chosen by the player
	 * @param numPlayer it is the preferred number of player for the lobby
	 * @return it returns true if it has succeeded in connecting to the server
	 * @throws IOException
	 */
	private boolean connectToServer(String nickname, int numPlayer) throws IOException {
		String received;
		Scanner receivedInput;
		PrintWriter sendOutput;

		receivedInput = new Scanner(serverSocket.getInputStream());
		sendOutput = new PrintWriter(serverSocket.getOutputStream());
		sendOutput.println(Constants.SETUP_IN_PARTICIPATE +" "+numPlayer+" "+nickname);
		sendOutput.flush();
		mainMenu.handleConnection(1);

		received = receivedInput.nextLine();
		if (received.equals(Constants.SETUP_OUT_CONNWORKED)) {
			mainMenu.handleConnection(2);
			return true;
		} else if (received.equals(Constants.SETUP_OUT_CONNFAILED)) {
			mainMenu.handleConnection(3);
			return false;
		} else {
			// TODO: insert and exception for communication error on the line
			mainMenu.handleConnection(4);
			return false;
		}
	}
	/** This function waits in the lobby for the finishing of lobby creating and
	 * communicate with the server for choosing first parameters
	 * @throws IOException
	 */
	private void gameCreation() throws IOException {
		Scanner receivedInput = new Scanner(serverSocket.getInputStream());
		PrintWriter sendOutput = new PrintWriter(serverSocket.getOutputStream());
		String received = receivedInput.nextLine();
		while (received.equals(Constants.CHECK)) {
			sendOutput.println(Constants.CHECK);
			sendOutput.flush();
			received = receivedInput.nextLine();
		}
		if (received.equals(Constants.SETUP_OUT_CONNFINISH)) {
			// TODO: remove prints on client
			System.out.println("Game is starting...");
			gamePreparation();
		}
	}
	private void gamePreparation() throws IOException {
		Scanner receivedInput = new Scanner(serverSocket.getInputStream());
		PrintWriter sendOutput = new PrintWriter(serverSocket.getOutputStream());
		String received = receivedInput.nextLine();

	}
	private boolean parseMainMenuInput(String received) {
		if (received.equals("1")) {
			try {
				serverSocket = new Socket(mainMenu.getServerAddress(),21005);
				return !connectToServer(mainMenu.getNickname(), mainMenu.getPlayerNumber());
			} catch (IOException e) {
				// TODO: change this catch
				System.out.print("Socket connection error!");
				System.exit(1);
				return false;
			}
		} else {
			System.exit(1);
			return false;
		}
	}
}
