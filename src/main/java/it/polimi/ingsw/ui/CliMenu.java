package it.polimi.ingsw.ui;

// necessary imports of Java SE
import java.util.Scanner;

public class CliMenu implements GraphicMenu {
	private String serverAddress;
	private String nameChosen;
	private int playerNumber;

	// METHODS INTENDED TO MANAGE CLI
	/* this method starts the main menu and returns
	* 	1: if the player wants to start a new game
	* 	2: if the player wants to close the game*/
	public String start() {
		Scanner input = new Scanner(System.in);
		String userInput;

		do {
			System.out.print("Do you want to play a new game? (Y/N): ");
			userInput = input.nextLine().toUpperCase();
		} while (!userInput.equals("Y") && !userInput.equals("N"));
		if (userInput.equals("Y")) {
			do {
				System.out.print("Which nickname do you want to use? (exit to close the game): ");
				userInput = input.nextLine().toUpperCase();
				if (userInput.contains(" ")) {
					userInput = "INVALID";
				}
			} while (!userInput.equals("EXIT") && userInput.equals("INVALID"));
			nameChosen = new String(userInput);
			if (!userInput.equals("EXIT")) {
				do {
					System.out.print("Do you want to play a 2 or 3 players game? (exit to close the game): ");
					userInput = input.nextLine();
				} while (!userInput.equals("EXIT") && Integer.parseInt(userInput) != 2 && Integer.parseInt(userInput) != 3);
			}
			playerNumber = Integer.parseInt(userInput);
			if (!userInput.equals("EXIT")) {
				do {
					System.out.print("Which is the server IP address? (exit to close the game): ");
					userInput = input.nextLine();
					if (userInput.contains(" ") || userInput.split("\\.").length != 4) {
						userInput = "INVALID";
					}
				} while (!userInput.equals("EXIT") && userInput.equals("INVALID"));
			}
			serverAddress = new String(userInput);
			if (userInput.equals("EXIT")) {
				nameChosen = null;
				playerNumber = 0;
				serverAddress = null;
				return "2";
			} else {
				return "1";
			}
		} else {
			return "2";
		}
	}
	public void handleConnection(int param) {
		switch (param) {
			case 1:
				System.out.println("Connecting to the server...");
				break;
			case 2:
				System.out.println("Connected to the server and the lobby...");
				break;
			case 3:
				System.out.println("Parameters sent to the server was wrong, failed to connect, coming back to main menu...");
				break;
			case 4:
				System.out.println("There was an error on the communication line, coming back to main menu...");
				break;
		}
	}

	// getters of the structure set by the client while interacting
	public String getServerAddress() {
		return new String(serverAddress);
	}
	public String getNickname() {
		return new String(nameChosen);
	}
	public int getPlayerNumber() {
		return playerNumber;
	}
}
