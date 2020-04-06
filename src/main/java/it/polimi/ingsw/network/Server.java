package it.polimi.ingsw.network;

// necessary imports from other packages of the project
import it.polimi.ingsw.util.Constants;

// necessary imports of Java SE
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is the server class and it never ends, it manages the lobby creation and
 * creation of threads which duty is to manage games, each one of these objects (ServerGamingThread)
 * manages a game, so it uses a Game, Controller and a RemoteView.
 */
public class Server implements Runnable {
	private ExecutorService gamingThreads = Executors.newFixedThreadPool(64);
	private ServerSocket serverSocket;
	private List<Pair<Socket,String>> clients;

	@Override
	public void run() {
		boolean functioning = true;

		System.out.println("Server has started...");
		// here the server builds the pre-game lobby where players wait to start the game
		try {
			// we assign a port to the server not registered to other services: https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.txt
			serverSocket = new ServerSocket(21005);
			System.out.println("Server has opened the socket...");
		} catch (IOException e) {
			// TODO: change this catch
			System.exit(1);
		}

		// TODO: remove the int
		int n = 1;
		while (functioning) {
			// server creates the lobby passing to the gaming thread connections and client nicknames
			clients = new ArrayList<>();
			try {
				setupMatch();
			} catch(IOException e) {
				// TODO: change this exception
				System.exit(1);
			}
			gamingThreads.submit(new ServerGamingThread(clients));
			// TODO: remove the System.out.println
			System.out.println("Server has prepared a game with sufficient players and is going to start game "+n+++"...");
		}
	}

	// TODO: DO THAT!!!!! Eliminate every System.out.println, this is not UI function!!!!!!!!!!!!!!!!!!!!!!!!
	private void setupMatch() throws IOException {
		String[] commandReceived;
		List<Scanner> in = new ArrayList<>();
		List<PrintWriter> out = new ArrayList<>();
		int playerNumber = 3, playerWaiting = 0, actualPlayer = 0;

		while (playerWaiting <= playerNumber) {
			// server manages the lobby while waiting for needed player to start the game, otherwise it controls that all players are still connected, if not it decrease the number of lobby player and restart waiting other players to join
			if (playerWaiting < playerNumber) {
				if (clients.size() <= playerWaiting) {
					clients.add(new Pair<Socket,String>(new Socket(), null));
				}
				actualPlayer = clients.size() - 1;
				clients.get(actualPlayer).setFirst(serverSocket.accept());
				if (in.size() == playerWaiting) {
					in.add(new Scanner(clients.get(actualPlayer).getFirst().getInputStream()));
					out.add(new PrintWriter(clients.get(actualPlayer).getFirst().getOutputStream()));
				} else {
					in.set(actualPlayer, new Scanner(clients.get(actualPlayer).getFirst().getInputStream()));
					out.set(actualPlayer,new PrintWriter(clients.get(actualPlayer).getFirst().getOutputStream()));
				}
				commandReceived = in.get(actualPlayer).nextLine().split(" ");
				if (playerWaiting == 0) {
					try {
						playerNumber = Integer.parseInt(commandReceived[1]);
						if (!commandReceived[0].equals(Constants.SETUP_IN_PARTECIPATE) || commandReceived.length != 3 || playerNumber < 2 || playerNumber > 3) {
							out.get(actualPlayer).println(Constants.SETUP_OUT_CONNFAILED);
							out.get(actualPlayer).flush();
							out.get(actualPlayer).close();
							playerNumber = 3;
						} else {
							out.get(actualPlayer).println(Constants.SETUP_OUT_CONNWORKED);
							out.get(actualPlayer).flush();
							clients.get(actualPlayer).setSecond(commandReceived[2]);
							playerWaiting++;
							// TODO: remove
							System.out.println("Server received player number "+playerWaiting);
						}
					} catch (NumberFormatException e) {
						out.get(actualPlayer).println(Constants.SETUP_OUT_CONNFAILED);
						out.get(actualPlayer).flush();
						out.get(actualPlayer).close();
						playerNumber = 3;
					}
				} else {
					// TODO: verify that there aren't players with the same nickname
					if (commandReceived[0].equals(Constants.SETUP_IN_PARTECIPATE)) {
						// TODO: inform other players of the new number of player in the lobby
						playerWaiting++;
						out.get(actualPlayer).println(Constants.SETUP_OUT_CONNWORKED);
						out.get(actualPlayer).flush();
						clients.get(actualPlayer).setSecond(commandReceived[2]);
						// TODO: remove
						System.out.println("Server received player number "+playerWaiting);
					} else {
						out.get(actualPlayer).println(Constants.SETUP_OUT_CONNFAILED);
						out.get(actualPlayer).flush();
						out.get(actualPlayer).close();
					}
				}
			} else {
				for (int i = 0; i < clients.size(); i++) {
					if (!Constants.verifyConnected(clients.get(i).getFirst())) {
						in.remove(i);
						out.remove(i);
						clients.remove(i);
						i--;
						playerWaiting--;
					}
				}
				if (playerWaiting == playerNumber) {
					for (PrintWriter client : out) {
						client.println(Constants.SETUP_OUT_CONNFINISH);
						client.flush();
					}
					playerWaiting++;
				}
			}
		}
	}
}