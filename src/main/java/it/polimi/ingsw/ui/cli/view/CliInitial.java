package it.polimi.ingsw.ui.cli.view;

import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.objects.NetLobbyPreparation;
import it.polimi.ingsw.network.objects.NetObject;
import it.polimi.ingsw.network.objects.NetSetup;
import it.polimi.ingsw.ui.cli.controller.UserInputController;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.exceptions.UserInputTimeoutException;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This class is the class which creates and manages the lobby and the menu of the Santorini video game.
 */
public class CliInitial {
	/**
	 * This enum represent the phase of input taken by the cli from the player
	 */
	private enum MenuPhase {
		START, NAME, SERVERADDRESS, PLAYERNUMBER, QUITTING
	}

	// other view object and attributes relative to the connection with server
	private final Deque<NetObject> messages;
	private final CliInput cliInput;
	private UserInputController userInputController;
	// state attributes that are used to represent the view
	private boolean menuPhase;
	private boolean active;
	private boolean serverCrashed;
	private MenuPhase stage;
	private String nameChosen;
	private String serverAddress;

	public CliInitial(CliInput cliInput) {
		messages = new ArrayDeque<>();
		this.cliInput = cliInput;
		active = true;
		stage = MenuPhase.START;
		nameChosen = null;
		serverAddress = null;
		serverCrashed = false;
		menuPhase = true;
	}

	/**
	 * This function shows the CLI-logo of the game and the initial menu.
	 * @return 0 when the client pressed play and connected to a server, 1 if he wants to close the game, -1 if there was an error
	 */
	public int menu() {
		int returnValue = 0;
		Command command;

		drawMenu();
		while (active) {
			try {
				printMenuQuestion();
				command = cliInput.getInput();
				parseMenuCommands(command);
				if (stage == MenuPhase.QUITTING) {
					returnValue = 1;
					active = false;
				}
			} catch (IOException e) {
				active = false;
				returnValue = -1;
			} catch (UserInputTimeoutException e) {
				active = false;
				returnValue = -1;
				printError(100);
			}
		}

		return returnValue;
	}
	/**
	 * This functions asks to the client if he wants to disconnect of if it wants to wait and when the message of the game start arrives it ends.
	 * @return 0 if the game is starting, 1 if the client wants to quit the lobby, -1 if there was an error
	 */
	public int lobbyCli() {
		stage = MenuPhase.START;
		active = true;
		menuPhase = false;
		int returnValue = 0;
		Command command;

		while (active) {
			try {
				parseLobbyMessages();
				if (active) {
					printLobbyQuestion();
					command = cliInput.getInput();
					parseLobbyCommands(command);
					if (stage == MenuPhase.QUITTING) {
						returnValue = 1;
						active = false;
					}
				}
			} catch (IOException e) {
				active = false;
				returnValue = -1;
			} catch (UserInputTimeoutException e) {
				parseLobbyMessages();
			}
		}

		if (serverCrashed) {
			return -1;
		} else {
			return returnValue;
		}
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *		MODIFIERS/GETTERS OF THIS CLASS			*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	/**
	 * Sets the user input controller to use to send the input to the server when the player insert a certain command.
	 * @param userInputController the {@link it.polimi.ingsw.ui.cli.controller.UserInputController}
	 */
	public void setUserInputController(UserInputController userInputController) {
		this.userInputController = userInputController;
	}
	/**
	 * Add a message to the messages queue.
	 * @param msg the message to queue
	 */
	public void queueMessage(NetObject msg) {
		synchronized (messages) {
			messages.add(msg);
		}
	}
	/**
	 * It notifies all threads waiting on the messages queue.
	 */
	public void notifyPregameCli() {
		synchronized (messages){
			messages.notifyAll();
		}
	}
	/**
	 * Says if the Cli Initial is in the menu phase.
	 * @return true if is the menu phase
	 */
	public boolean isMenuPhase() {
		return menuPhase;
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *		PARSING FUNCTIONS OF THIS CLASS			*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	/**
	 * This method parses the commands given by the user while using the cli version of the game. It checks the command validity and eventually advance phase if it is correct or it can use the {@link it.polimi.ingsw.ui.cli.controller.UserInputController} to send a message to the server indicated by the user.
	 * @param command the command inserted by the user
	 */
	private void parseMenuCommands(Command command) {
		switch (stage) {
			case START -> {
				if (command.commandType.equals("1")) {
					stage = MenuPhase.NAME;
				} else if (command.commandType.equals("2")) {
					stage = MenuPhase.QUITTING;
				} else {
					printError(0);
				}
			}
			case NAME -> {
				if (command.commandType.length() < Constants.MIN_NICKNAME_LEN || command.commandType.length() > Constants.MAX_NICKNAME_LEN || command.getNumParameters() != 0) {
					printError(1);
				} else {
					// if the user is trying another name because there is already a player with that name, it immediately tries to participate to a lobby
					if (serverAddress != null) {
						nameChosen = command.commandType;
						userInputController.tryAnotherName(nameChosen);
						synchronized (messages) {
							try {
								if (messages.isEmpty()) {
									messages.wait();
								}
							} catch (InterruptedException e) {
								throw new AssertionError("[CliInitial - parseMenuMessage - 1]: Process has been interrupted and no one interrupts it");
							}
						}
						parseMenuMessages();
					} else {
						stage = MenuPhase.SERVERADDRESS;
						nameChosen = command.commandType;
					}
				}
			}
			case SERVERADDRESS -> {
				if (command.getNumParameters() != 0) {
					printError(3);
				} else {
					String[] addressNumbers = command.commandType.split("\\.");
					if (addressNumbers.length != 4) {
						printError(3);
					} else if (!Constants.isNumber(addressNumbers[0]) || !Constants.isNumber(addressNumbers[1]) || !Constants.isNumber(addressNumbers[2]) || !Constants.isNumber(addressNumbers[3])) {
						printError(3);
					} else if (Integer.parseInt(addressNumbers[0]) < 0 || Integer.parseInt(addressNumbers[0]) > 255 || Integer.parseInt(addressNumbers[1]) < 0 || Integer.parseInt(addressNumbers[1]) > 255 || Integer.parseInt(addressNumbers[2]) < 0 || Integer.parseInt(addressNumbers[2]) > 255 || Integer.parseInt(addressNumbers[3]) < 0 || Integer.parseInt(addressNumbers[3]) > 255) {
						printError(3);
					} else {
						boolean error = false;
						for (int i = 0; i < addressNumbers.length && !error; i++) {
							if (!Constants.isNumber(addressNumbers[i])) {
								error = true;
							}
						}
						if (error) {
							printError(3);
						} else {
							serverAddress = command.commandType;
							userInputController.connect(nameChosen,serverAddress);
							synchronized (messages) {
								try {
									if (messages.isEmpty()) {
										messages.wait();
									}
								} catch (InterruptedException e) {
									throw new AssertionError("[CliInitial - parseMenuMessage - 2]: Process has been interrupted and no one interrupts it");
								}
							}
							parseMenuMessages();
						}
					}
				}
			}
			case PLAYERNUMBER -> {
				if (command.commandType.equals("2") || command.commandType.equals("3")) {
					userInputController.getCommand(Integer.parseInt(command.commandType));
					synchronized (messages) {
						try {
							if (messages.isEmpty()) {
								messages.wait();
							}
						} catch (InterruptedException e) {
							throw new AssertionError("[CliInitial - parseMenuMessage - 3]: Process has been interrupted and no one interrupts it");
						}
					}
					parseMenuMessages();
				} else if (command.commandType.equals(Constants.COMMAND_DISCONNECT)) {
					userInputController.disconnect();
					stage = MenuPhase.QUITTING;
				} else {
					printError(4);
				}
			}
		}
	}
	/**
	 * This method parses messages arrived from the server.
	 */
	private void parseMenuMessages() {
		NetSetup netSetup = (NetSetup) messages.pop();
		switch (netSetup.message) {
			case Constants.SETUP_CREATE -> {
				stage = MenuPhase.PLAYERNUMBER;
			}
			case Constants.SETUP_CREATE_WORKED, Constants.SETUP_OUT_CONNWORKED, Constants.SETUP_OUT_CONNFINISH -> {
				printMenuMessage(1);
				active = false;
			}
			case Constants.SETUP_OUT_CONNFAILED -> {
				stage = MenuPhase.NAME;
				printError(2);
			}
			case Constants.SETUP_ERROR -> {
				printError(5);
			}
			case Constants.GENERAL_NOT_EXIST_SERVER -> {
				serverAddress = null;
				printError(7);
			}
		}
	}
	/**
	 * This method parses the commands given by the user while using the cli version of the game. It checks the command validity and eventually send a message to the server indicated by the user.
	 * @param command the command inserted by the user
	 */
	private void parseLobbyCommands(Command command) {
		if (command.commandType.equals(Constants.COMMAND_DISCONNECT)) {
			userInputController.getCommand(command,new Turn());
			stage = MenuPhase.QUITTING;
		} else {
			printError(6);
		}
	}
	/**
	 * This method parses messages arrived from the server.
	 */
	private void parseLobbyMessages() {
		NetLobbyPreparation netLobbyPreparation;
		synchronized (messages) {
			while (!messages.isEmpty()) {
				netLobbyPreparation = (NetLobbyPreparation) messages.pop();
				switch (netLobbyPreparation.message) {
					case Constants.LOBBY_ERROR -> {
						printError(6);
					}
					case Constants.GENERAL_FATAL_ERROR -> {
						printError(100);
						serverCrashed = true;
						active = false;
					}
					case Constants.LOBBY_TURN -> {
						active = false;
						printGameStarting();
						printPlayers(netLobbyPreparation);
					}
				}
			}
		}
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *		PRINTING FUNCTIONS OF THIS CLASS		*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	/**
	 * It prints the list of all players inside the lobby.
	 * @param netLobby lobby message with player information
	 */
	private void printPlayers(NetLobbyPreparation netLobby) {
		System.out.println("Players and their order turn are displayed below:");
		while(netLobby != null) {
			System.out.println(" " + netLobby.player + " - " + netLobby.order);
			netLobby = netLobby.next;
		}
	}
	/**
	 * It says to the user that the game is starting.
	 */
	private void printGameStarting() {
		System.out.println("The game is starting...");
	}
	/**
	 * It prints a graphical message to show that the phase changed depending on the parameter.
	 * @param i an integer
	 */
	private void printMenuMessage(int i) {
		switch (i) {
			case 1 -> {
				System.out.print("\n\n");
				System.out.print("---------------------------------------------------------------------------\n" +
						 		 "|                                                                         |\n" +
								 "|      _____          __  __ ______   _      ____  ____  ______     __    |\n" +
								 "|     / ____|   /\\   |  \\/  |  ____| | |    / __ \\|  _ \\|  _ \\ \\   / /    |\n" +
								 "|    | |  __   /  \\  | \\  / | |__    | |   | |  | | |_) | |_) \\ \\_/ /     |\n" +
								 "|    | | |_ | / /\\ \\ | |\\/| |  __|   | |   | |  | |  _ <|  _ < \\   /      |\n" +
								 "|    | |__| |/ ____ \\| |  | | |____  | |___| |__| | |_) | |_) | | |       |\n" +
								 "|     \\_____/_/    \\_\\_|  |_|______| |______\\____/|____/|____/  |_|       |\n" +
								 "|                                                                         |\n" +
								 "---------------------------------------------------------------------------\n\n\n");
			}
		}
	}
	/**
	 * It prints the question that make the client understand what he must write depending on current phase.
	 */
	private void printMenuQuestion() {
		switch (stage) {
			case START -> System.out.print("Insert a number: ");
			case NAME -> System.out.print("Choose a name for the game: ");
			case SERVERADDRESS -> System.out.print("Insert the address of the server: ");
			case PLAYERNUMBER -> System.out.print("Choose the number of the player for the game (a game can have 2 or 3 players): ");
		}
	}
	/**
	 * It prints to the user that he can only disconnect.
	 */
	private void printLobbyQuestion() {
		System.out.println("You can type \"disconnect\" at any time to quit the game. Otherwise wait for the start. You can also type \"help\" in-game, to visualize the English version of the guide");
		System.out.print("Waiting for other players to join...\n\n");
	}
	/**
	 * Prints an error.
	 * @param value indicates a certain error: depending on it, it will print a certain error message.
	 */
	private void printError(int value) {
		switch (value){
			case 0 :
				System.out.println("Please insert a valid number.");
				break;

			case 1:
				System.out.println("You inserted an invalid name, it must not contain spaces and it must be at least 5 characters and no more than 15.");
				break;

			case 2 :
				System.out.println("Another player online has this nickname, try another one.");
				break;

			case 3 :
				System.out.println("Invalid IP address.");
				break;

			case 4 :
				System.out.println("Invalid number of players, a game can have 2 or 3 players.");
				break;

			case 5:
				System.out.println("You inserted a very strange message, try inserting a normal one.");
				break;

			case 6:
				System.out.println("You can only disconnect or wait inside the lobby.");
				break;

			case 7:
				System.out.println("The server isn't a Santorini's game server.");
				break;

			case 100:
				System.out.print("\n\n\n---------------------------------------------------------\n" +
						"-                                                       -\n" +
						"-                                                       -\n" +
						"-        SERVER HAS GONE OFFLINE FOR SOME REASON        -\n" +
						"-                                                       -\n" +
						"-                                                       -\n" +
						"---------------------------------------------------------\n\n\n");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					System.out.println("There has been an interruption problem");
				}
				break;
		}
	}
	/**
	 * Draws the initial menu
	 */
	private void drawMenu(){
		System.out.println("\n\n\n\n");
		System.out.println("          /hmd-            :                                /MMN-              .-::///-        :s+.              mmho:.                smmddddh.`.:.                .   .hmdddddy ");
		System.out.println("       `/dNMMd.           sNs                               oMMM-  `..-:/+osydmmNNNMMMy      :hNMMd/`           .MMMMMmho:.            hNMMMMMm. :Nmo.            .dmd. .mMMMMMNd ");
		System.out.println("     .+dMMNh:`           :MMMo                              yMMM. smmNNMMMMMMMMmdhyso/.    :hNMMNMMMd+.         .MMMNmNMMMNho:`        `.dMMN-`  /MMMNy:          :MMM-  `-MMMh.`  ");
		System.out.println("   -yNMMms-             .NMMMM+            -s.              hMMM  ymddhyoohMMMs`         :hNMMd/.:hMMMmo.       .MMMs`-/sdNMMNmy+-`      yMMm    /MMMMMNd/`       :MMM-    MMMs   ");
		System.out.println(" :hNMMNo.               yMMMMMN:           -MNo`            mMMN   ```    :MMM+        -hMMMd/`    :hMMMmo`     .MMM+    `.:sdNMMNmh+.   yMMm    /MMMdhNMMmo-     :MMM-    MMMs   ");
		System.out.println("mmMMMMs                :MMMhdMMm.          .MMMh.           NMMd          -MMM+     `:hNMMd/`        :hMMMm+`   .MMMs        `/NMMMMd:   yMMN    -MMM/`-sNMMNh/`  :MMM-    MMMy  ");
		System.out.println(" .omMMNd/`            `mMMN.`mMMd`          MMMMm+`        `MMMs          `MMMo    :dMMMd/`            :hMMMd:` .MMMs      ./hNMMmy:`    dMMm    :MMM/   .+dMMMmo-/MMM-   `MMMs ");
		System.out.println("   .+dMMMd+.          oMMM/  .NMMd`         NMMMMMh-       `MMM+          `MMMy   -NMMMd`                sMMMMo .MMMs    -smMMNh/.       mMMm    +MMM-     `/hNMMNNMMM-   .MMMs  ");
		System.out.println("     `/hNMMms.       -NMMs    :NMMd`        MMMmdMMNo`     .MMM+          `NMMd    /dMMMy:             .sNMMNs. .MMMo  .yNMMNy-          mMMh    +MMM-        -odMMMMM/   .MMM+");
		System.out.println("        -yNMMNs-    `dMMm`     +MMMh`       MMMm`+NMMd-    :MMM:           mMMd     `+mMMNy-         .sNMMNs.   :MMM+  /MMMM-            mMMy    /MMM/          `:mMMM+   .MMM/");
		System.out.println("          -sNMMNy-  oMMM/       +MMMh`      mMMm  .yMMNs`  :MMM-           mMMd       .omMMNy-     .omMMNs-     +MMM+   sMMMo            MMMy    /MMM/            /MMM+   :MMM/ ");
		System.out.println("           `oMMMMd -NMMd         /NMMh`     dMMm    +mMMm/`+MMM`           mMMN         `+mMMNy-`.omMMNo.       oMMM+   `sNMMo          .MMMy    /MMM/            +MMM:   +MMM/ ");
		System.out.println("         .+dMMMd+` hMMM:   `.-//` /MMMs     yMMm     .hMMMhdMMN            dMMM           .oNMMNdNMMNs.         /MMM+     +NMMs         -MMMy    /MMM/            /MMM:   oMMM/ ");
		System.out.println("       .omMMMd+`  /MMMd:+shdmMMM/  +MMM+    yMMm       :dMMMMMh            yMMM             .omMMMNs.           :MMM/      +MMMs        -MMMy    /MMM/            :MMM-   oMMM/  ");
		System.out.println("     -sNMMNh:`   .NMMMMMMMMNmhs/`   oMMM+   yMMm        `sNMMMh            oMMM               .+yo.             -MMM-       +NMMs`      -MMMo    /MMM/            :MMM-   oMMM-  ");
		System.out.println("   -yNMMms-      yMMNNmhs/-.`        sMMM/  yMMm          :hMMh            oMMM.                                :MMM-        /NMMy`     :MMMo    /MMM:            :MMM-   sMMM- ");
		System.out.println(" :hMMMm+.        oo/-.`               hMMN: +ddo           `+my            oMMM-        -----------------       /MMM-         /NMMh`  .smMMMmo.  +MMM-            :MMM- :yNMMMh+` ");
		System.out.println("dNMMm+`                               `hMMN-                 ..            +MMN.       :NMMMMMMMMMMMMMMMMo      oMMM:          :NMMd` :NMMMMMMy  /MMM-            `yhs` sNMMMMMM/  ");
		System.out.println("h/o+                                   -dNm/                                +o/         oyyyyyyyyyyyyyyys:      :mNd-           +MMN-  :///+++-  :mMN-                  .////+++-   ");
		System.out.println(" \n\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t(1) PLAY\n");
		System.out.println(" \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t(2) EXIT\n");
	}
}