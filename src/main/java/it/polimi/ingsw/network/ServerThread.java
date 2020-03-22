package it.polimi.ingsw.network;

// necessary imports from other packages of the project
import it.polimi.ingsw.controller.ServerController;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.ui.Cli;
import it.polimi.ingsw.ui.GraphicInterface;

// necessary imports of Java SE
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServerThread extends Thread implements Observer{
	private ServerController serverController;
	private Game serverGame;
	private GraphicInterface serverView;
	private ServerSocket serverSocket;
	private List<Socket> clientConncetions;
	private List<String> clientNicknames;
	// RFC: maybe this is not needed cause (should server print messages?)
	// private ResourceBundle language = ResourceBundle.getBundle("it.polimi.ingsw.language_",new Locale("en","GB"));

	public void run() {
		System.out.println("Server has started...");
		// here the server builds the pre-game lobby where players wait to start the game
		try {
			// we assign a port to the server not registered to other services: https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.txt
			serverSocket = new ServerSocket(21005);
			System.out.println("Server has opened the socket...");
			setupMatch();
		} catch (IOException e) {
			// TODO: change this catch
			System.exit(1);
		}
		System.out.println("Server has prepared a game with sufficient players and is going to start...");

		// the first thing to do for the server it to set up the lobby when there are enough players to start
		serverGame = new Game(clientNicknames);
		serverController = new ServerController(serverGame);
		serverView = new Cli();
		serverView.addObserver(serverController);
		serverGame.addObserver(serverView);

		// now the server need to update players about the start choosing the first player
		serverView.run();

		// TODO: how to implement the action of being turned off of the server?
	}

	private void setupMatch() throws IOException {
		String[] commandReceived;
		Scanner tempIn;
		PrintWriter tempOut;
		Scanner[] in;
		PrintWriter[] out;
		int playerNumber, playerWaiting, actualPlayer;

		// server waits for a well formed request of start of a new match
		clientNicknames = new ArrayList<String>();
		clientConncetions = new ArrayList<Socket>();
		clientConncetions.add(new Socket());
		do {
			clientConncetions.set(0, serverSocket.accept());
			tempIn = new Scanner(clientConncetions.get(0).getInputStream());
			// server parses the input sent by the client and process it to verify it is well formed, if not it waits for a new client to reconnect after informing the client
			commandReceived = tempIn.nextLine().split(" ");
			playerNumber = Integer.parseInt(commandReceived[1]);
			if (!commandReceived[0].equals("setup-partecipate") || commandReceived.length != 3 || playerNumber < 2 || playerNumber > 3) {
				tempOut = new PrintWriter(clientConncetions.get(0).getOutputStream());
				tempOut.println("setup-connection-failed");
				tempOut.flush();
				tempOut.close();
			}
		} while (!commandReceived[0].equals("setup-partecipate") || commandReceived.length != 3 || playerNumber < 2 || playerNumber > 3);
		in = new Scanner[playerNumber];
		in[0] = tempIn;
		out = new PrintWriter[playerNumber];
		out[0] = new PrintWriter(clientConncetions.get(0).getOutputStream());
		out[0].println("setup-connection-worked");
		out[0].flush();
		clientNicknames.add(commandReceived[2]);
		playerWaiting = 1;
		System.out.println("Server received player number 1");

		// server creates the match and waits for other partecipants to join to start the game
		while (clientConncetions.size() < playerNumber) {
			if (clientConncetions.size() == playerWaiting) {
				clientConncetions.add(new Socket());
			}
			actualPlayer = clientConncetions.size()-1;
			clientConncetions.set(actualPlayer,serverSocket.accept());
			in[actualPlayer] = new Scanner(clientConncetions.get(actualPlayer).getInputStream());
			commandReceived = in[actualPlayer].nextLine().split(" ");
			if (commandReceived[0].equals("setup-partecipate")) {
				// TODO: inform other players of the new number of player in the lobby
				playerWaiting++;
				out[actualPlayer] = new PrintWriter(clientConncetions.get(actualPlayer).getOutputStream());
				out[actualPlayer].println("setup-connection-worked");
				out[actualPlayer].flush();
				clientNicknames.add(commandReceived[2]);
				System.out.println("Server received player number "+playerNumber);
			} else {
				out[actualPlayer] = new PrintWriter(clientConncetions.get(actualPlayer).getOutputStream());
				out[actualPlayer].println("setup-connection-failed");
				out[actualPlayer].flush();
				out[actualPlayer].close();
			}
		}

		// now the server ha all the player needed to start the game, it says that to the clients
		for (PrintWriter client : out) {
			client.println("setup-connection-finished");
			client.flush();
		}
	}

	@Override
	public void update(Observable o, Object arg) {

	}
}