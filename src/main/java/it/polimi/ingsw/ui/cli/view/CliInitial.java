package it.polimi.ingsw.ui.cli.view;

import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.objects.NetLobbyPreparation;
import it.polimi.ingsw.network.objects.NetSetup;
import it.polimi.ingsw.ui.cli.controller.UserInputController;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.exceptions.UserInputTimeoutException;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class CliInitial {
	private enum MenuPhase {
		START, NAME, SERVERADDRESS, PLAYERNUMBER, QUITTING
	}

	// other view object and attributes relative to the connection with server
	private final Deque<NetLobbyPreparation> messagesLobby;
	private final Deque<NetSetup> messagesMenu;
	private final CliInput cliInput;
	private UserInputController userInputController;
	// state attributes that are used to represent the view
	private Command input;
	private boolean active;
	private boolean serverCrashed;
	private MenuPhase stage;
	private String nameChosen;
	private String serverAddress;

	public CliInitial(CliInput cliInput) {
		messagesLobby = new ArrayDeque<>();
		messagesMenu = new ArrayDeque<>();
		this.cliInput = cliInput;
		active = true;
		stage = MenuPhase.START;
		nameChosen = null;
		serverAddress = null;
		serverCrashed = false;
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
				// TODO: implement the support for server disconnection while inserting number of players
				throw new AssertionError("[CliInput - menu()]: Getting input in the menu should never be interrupted");
			}
		}

		return returnValue;
	}
	/**
	 *
	 * @return 0 if the game is starting, 1 if the client wants to quit the lobby, -1 if there was an error
	 */
	public int lobbyCli() {
		stage = MenuPhase.START;
		active = true;
		int returnValue = 0;
		Command command;

		while (active) {
			try {
				printLobbyQuestion();
				command = cliInput.getInput();
				parseLobbyCommands(command);
				if (stage == MenuPhase.QUITTING) {
					returnValue = 1;
					active = false;
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
	 *			MODIFIERS OF THIS CLASS				*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	public void setUserInputController(UserInputController userInputController) {
		this.userInputController = userInputController;
	}
	public void queueMessageLobby(NetLobbyPreparation msg){
		synchronized (messagesLobby) {
			messagesLobby.add(msg);
		}
	}
	public void queueMessageMenu(NetSetup msg){
		synchronized (messagesMenu) {
			messagesMenu.add(msg);
		}
	}
	public void notifyCliMenu() {
		synchronized (messagesMenu){
			messagesMenu.notifyAll();
		}
	}
	public void notifyCliLobby() {
		synchronized (messagesLobby){
			messagesLobby.notify();
		}
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
				if (command.commandType.length() < 5 || command.getNumParameters() != 0) {
					printError(1);
				} else {
					// if the user is trying another name because there is already a player with that name, it immediately tries to connect
					if (serverAddress != null) {
						userInputController.connect(nameChosen,serverAddress);
						synchronized (messagesMenu) {
							try {
								if (messagesMenu.isEmpty()) {
									messagesMenu.wait();
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
							synchronized (messagesMenu) {
								try {
									if (messagesMenu.isEmpty()) {
										messagesMenu.wait();
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
					synchronized (messagesMenu) {
						try {
							if (messagesMenu.isEmpty()) {
								messagesMenu.wait();
							}
						} catch (InterruptedException e) {
							throw new AssertionError("[CliInitial - parseMenuMessage - 3]: Process has been interrupted and no one interrupts it");
						}
					}
					parseMenuMessages();
				} else {
					printError(4);
				}
			}
		}
	}
	private void parseMenuMessages() {
		NetSetup netSetup = messagesMenu.pop();
		switch (netSetup.message) {
			case Constants.SETUP_CREATE -> {
				stage = MenuPhase.PLAYERNUMBER;
			}
			case Constants.SETUP_CREATE_WORKED, Constants.SETUP_OUT_CONNWORKED -> {
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
		}
	}
	private void parseLobbyCommands(Command command) {
		if (command.commandType.equals(Constants.COMMAND_DISCONNECT)) {
			userInputController.getCommand(command,new Turn());
			stage = MenuPhase.QUITTING;
		} else {
			printError(6);
		}
	}
	private void parseLobbyMessages() {
		NetLobbyPreparation netLobbyPreparation;
		synchronized (messagesLobby) {
			while (!messagesLobby.isEmpty()) {
				netLobbyPreparation = messagesLobby.pop();
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
	private void printPlayers(NetLobbyPreparation netLobby) {
		System.out.println("Players and their order turn are displayed below:");
		while(netLobby != null) {
			System.out.println(" " + netLobby.player + " - " + netLobby.order);
			netLobby = netLobby.next;
		}
	}
	private void printGameStarting() {
		System.out.println("The game is starting...");
	}
	private void printMenuMessage(int i) {
		switch (i) {
			case 1 -> System.out.println("You successfully entered in a lobby!");
		}
	}
	private void printMenuQuestion() {
		switch (stage) {
			case START -> System.out.print("Insert a number: ");
			case NAME -> System.out.print("Choose a name for the game: ");
			case SERVERADDRESS -> System.out.print("Insert the address of the server: ");
			case PLAYERNUMBER -> System.out.print("Choose the number of the player for the game (a game can have 2 or 3 players): ");
		}
	}
	private void printLobbyQuestion() {
		System.out.print("If you want you can type disconnect and quit this lobby, if not you can wait the start (do not type nothing if you want to wait): ");
	}
	/**
	 *
	 * @param value indicates a certain error: depending on it, it will print a certain error message.
	 */
	private void printError(int value) {
		switch (value){
			case 0 :
				System.out.println("Please insert a valid number.");
				break;

			case 1:
				System.out.println("You inserted an invalid name, it must not contain spaces and it must be at least 5 characters.");
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

			case 100:
				System.out.println("The server had an error and crashed, you're going back to the main menu");
				break;
		}
	}
	private void drawMenu(){
		System.out.println("\n\n\n\n");
		System.out.println("                     /hmd-            :                                /MMN-              .-::///-        :s+.              mmho:.                smmddddh.`.:.                .   .hmdddddy ");
		System.out.println("                  `/dNMMd.           sNs                               oMMM-  `..-:/+osydmmNNNMMMy      :hNMMd/`           .MMMMMmho:.            hNMMMMMm. :Nmo.            .dmd. .mMMMMMNd ");
		System.out.println("                .+dMMNh:`           :MMMo                              yMMM. smmNNMMMMMMMMmdhyso/.    :hNMMNMMMd+.         .MMMNmNMMMNho:`        `.dMMN-`  /MMMNy:          :MMM-  `-MMMh.`  ");
		System.out.println("              -yNMMms-             .NMMMM+            -s.              hMMM  ymddhyoohMMMs`         :hNMMd/.:hMMMmo.       .MMMs`-/sdNMMNmy+-`      yMMm    /MMMMMNd/`       :MMM-    MMMs   ");
		System.out.println("            :hNMMNo.               yMMMMMN:           -MNo`            mMMN   ```    :MMM+        -hMMMd/`    :hMMMmo`     .MMM+    `.:sdNMMNmh+.   yMMm    /MMMdhNMMmo-     :MMM-    MMMs   ");
		System.out.println("           mmMMMMs                :MMMhdMMm.          .MMMh.           NMMd          -MMM+     `:hNMMd/`        :hMMMm+`   .MMMs        `/NMMMMd:   yMMN    -MMM/`-sNMMNh/`  :MMM-    MMMy  ");
		System.out.println("            .omMMNd/`            `mMMN.`mMMd`          MMMMm+`        `MMMs          `MMMo    :dMMMd/`            :hMMMd:` .MMMs      ./hNMMmy:`    dMMm    :MMM/   .+dMMMmo-/MMM-   `MMMs ");
		System.out.println("              .+dMMMd+.          oMMM/  .NMMd`         NMMMMMh-       `MMM+          `MMMy   -NMMMd`                sMMMMo .MMMs    -smMMNh/.       mMMm    +MMM-     `/hNMMNNMMM-   .MMMs  ");
		System.out.println("                `/hNMMms.       -NMMs    :NMMd`        MMMmdMMNo`     .MMM+          `NMMd    /dMMMy:             .sNMMNs. .MMMo  .yNMMNy-          mMMh    +MMM-        -odMMMMM/   .MMM+");
		System.out.println("                   -yNMMNs-    `dMMm`     +MMMh`       MMMm`+NMMd-    :MMM:           mMMd     `+mMMNy-         .sNMMNs.   :MMM+  /MMMM-            mMMy    /MMM/          `:mMMM+   .MMM/");
		System.out.println("                     -sNMMNy-  oMMM/       +MMMh`      mMMm  .yMMNs`  :MMM-           mMMd       .omMMNy-     .omMMNs-     +MMM+   sMMMo            MMMy    /MMM/            /MMM+   :MMM/ ");
		System.out.println("                      `oMMMMd -NMMd         /NMMh`     dMMm    +mMMm/`+MMM`           mMMN         `+mMMNy-`.omMMNo.       oMMM+   `sNMMo          .MMMy    /MMM/            +MMM:   +MMM/ ");
		System.out.println("                    .+dMMMd+` hMMM:   `.-//` /MMMs     yMMm     .hMMMhdMMN            dMMM           .oNMMNdNMMNs.         /MMM+     +NMMs         -MMMy    /MMM/            /MMM:   oMMM/ ");
		System.out.println("                  .omMMMd+`  /MMMd:+shdmMMM/  +MMM+    yMMm       :dMMMMMh            yMMM             .omMMMNs.           :MMM/      +MMMs        -MMMy    /MMM/            :MMM-   oMMM/  ");
		System.out.println("                -sNMMNh:`   .NMMMMMMMMNmhs/`   oMMM+   yMMm        `sNMMMh            oMMM               .+yo.             -MMM-       +NMMs`      -MMMo    /MMM/            :MMM-   oMMM-  ");
		System.out.println("              -yNMMms-      yMMNNmhs/-.`        sMMM/  yMMm          :hMMh            oMMM.                                :MMM-        /NMMy`     :MMMo    /MMM:            :MMM-   sMMM- ");
		System.out.println("            :hMMMm+.        oo/-.`               hMMN: +ddo           `+my            oMMM-        -----------------       /MMM-         /NMMh`  .smMMMmo.  +MMM-            :MMM- :yNMMMh+` ");
		System.out.println("           dNMMm+`                               `hMMN-                 ..            +MMN.       :NMMMMMMMMMMMMMMMMo      oMMM:          :NMMd` :NMMMMMMy  /MMM-            `yhs` sNMMMMMM/  ");
		System.out.println("           h/o+                                   -dNm/                                +o/         oyyyyyyyyyyyyyyys:      :mNd-           +MMN-  :///+++-  :mMN-                  .////+++-   ");
		System.out.println(" \n\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t(1) PLAY\n");
		System.out.println(" \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t(2) EXIT\n");
	}
}