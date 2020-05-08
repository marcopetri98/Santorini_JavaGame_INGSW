package it.polimi.ingsw.ui.cli.view;

import it.polimi.ingsw.network.objects.NetLobbyPreparation;
import it.polimi.ingsw.network.objects.NetSetup;
import it.polimi.ingsw.ui.cli.controller.UserInputController;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.exceptions.UserInputTimeoutException;

//import javax.management.MBeanRegistration;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class CliInitial {
	private final Deque<NetLobbyPreparation> messagesLobby;
	private final Deque<NetSetup> messagesMenu;
	private Command input;
	private NetLobbyPreparation netLobbyPreparation;
	private NetSetup netSetup;
	private CliInput cliInput;
	private UserInputController userInputController;
	private boolean flag;
	private int menuReturn;
	private int lobbyReturn;
	private int parseLobbyValue;
	private int parseMenuValue;

	public CliInitial(CliInput cliInput1) {
		messagesLobby = new ArrayDeque<>();
		messagesMenu = new ArrayDeque<>();
		this.cliInput = cliInput1;
		flag = true;
	}

	//setter
	public void setUserInputController(UserInputController userInputController) {
		this.userInputController = userInputController;
	}

	/**
	 * This function shows the CLI-logo of the game and the initial menu.
	 * @return 0 when client insert a valid option and there is a right communication with the server
	 */
	public int menu() throws IOException, UserInputTimeoutException {
		drawMenu();
		while(flag) {
			input = cliInput.getInput();
			if (input.commandType.equals("1")) {
				userInputController.connect(input);
				synchronized (messagesMenu) {
					try {
						messagesMenu.wait(); //waiting MainCLiController gives here the message from the server. It will notify this
					} catch (InterruptedException e) {
						throw new AssertionError(e);
					}
					if (parseMenuMessage() != 6 ) flag = false;
					boolean flag1 = true;
					while(flag){ //asking ip address
						System.out.println("Please insert the server ip address:");
						input = cliInput.getInput(); //TODO handle with a regex if the syntax is correct, else call printError(2);
						userInputController.getCommand(input);
						if (parseMenuMessage() != 6 ) flag1 = false;
						//if ip address is legit, let's ask the nickname:
						boolean flag2 = true;
						while (flag1) { //asking number of players
							System.out.println("Please insert the number of players:");
							input = cliInput.getInput();
							if(input.commandType.equals("2") || input.commandType.equals("3")) {
								userInputController.getCommand(input);
								if (parseMenuMessage() != 7 ) flag2 = false;
							} else {
								printError(4);
								flag2 = false;
							}
							boolean flag3 = true;
							while (flag2 && flag3) {
								System.out.println("Please insert your nickname:");
								input = cliInput.getInput();
								userInputController.getCommand(input);
								if (parseMenuMessage() != 4) {
									if (!checkNickname(input, netLobbyPreparation)) {
										printError(1);
										//stay in this while
									} else { //legit ip address and legit nickname
										menuReturn = 0;
										flag = false;
										flag3 = false;
										flag1 = false;
										//expected to enter in the lobby...
									}
								}
							}
						}
					}

				}
			} else if (input.commandType.equals("2")) {
				userInputController.connect(input);
				synchronized (messagesMenu) {
					try {
						messagesMenu.wait();
					} catch (InterruptedException e) {
						throw new AssertionError(e);
					}
					if(parseMenuMessage() == 8) {
						menuReturn = -1;
						flag = false;
					} else {
						printError(3);
					}
				}
			} else {
				printError(0); //Insert a valid number

			}
		}

		return menuReturn; //if it is '0' client choose "1" option; if it is '-1' client choose "2" option.
	}

	/**
	 *
	 * @return 0 if the game can start; else -1
	 */
	public int lobbyCli() throws IOException, UserInputTimeoutException {
		//TODO: cliLobby graphic: I'll handle it when the others "TODO" will be fixed.
		boolean lob = true;
		while (lob) {
			input = cliInput.getInput();  //if a player wanted to disconnect...
			userInputController.getCommand(input);
			if (input.commandType.toLowerCase().equals("quit")) {
				if (parseLobbyMessage() == 2) {
					lobbyReturn = -1;
					lob = false;
				} else {
					printError(3);
				}
			} else {
				if(parseLobbyMessage() == 0 || parseLobbyMessage() == 3){
					printError(3);
				} else if(parseLobbyMessage() == 1){
					printGameStarting();
					lobbyReturn = 0;
				}
			}
		}

		return lobbyReturn;
	}

	public void queueMessageLobby(NetLobbyPreparation msg){
		synchronized (messagesLobby) {
			messagesLobby.add(msg);
		}
	}

	public void queueMessageMenu(NetSetup msg){
		synchronized (messagesMenu){
			messagesMenu.add(msg);
		}
	}

	/**
	 *
	 * @return false if there is an error message, else returns true.
	 */
	private int parseLobbyMessage() {
		netLobbyPreparation = messagesLobby.pop();
		switch (netLobbyPreparation.message){
			case Constants.LOBBY_ERROR :
				//System.out.println(Constants.LOBBY_ERROR);
				parseLobbyValue = 0;
				break;

			case Constants.LOBBY_TURN :
				//System.out.println(Constants.LOBBY_TURN);
				parseLobbyValue = 1;
				break;

			case Constants.GENERAL_PLAYER_DISCONNECTED :
				//System.out.println(Constants.GENERAL_PLAYER_DISCONNECTED);
				parseLobbyValue = 2;
				break;

			case Constants.GENERAL_FATAL_ERROR :
				//System.out.println(Constants.GENERAL_FATAL_ERROR);
				parseLobbyValue = 3;
				break;

			default:
				printError(3);
				break;
		}
		return parseLobbyValue;
	}

	/**
	 *
	 * @return false if there is an error message, else return true.
	 */
	private int parseMenuMessage(){
		netSetup = messagesMenu.pop();
		switch (netSetup.message){
			case Constants.SETUP_CREATE :
				//System.out.println(Constants.SETUP_CREATE);
				parseMenuValue = 0;
				break;

			case Constants.SETUP_CREATE_WORKED :
				//System.out.println(Constants.SETUP_CREATE_WORKED);
				parseMenuValue = 1;
				break;

			case Constants.SETUP_ERROR :
				//System.out.println(Constants.SETUP_ERROR);
				parseMenuValue = 2;
				break;

			case Constants.SETUP_OUT_CONNWORKED :
				//System.out.println(Constants.SETUP_OUT_CONNWORKED);
				parseMenuValue = 3;
				break;

			case Constants.SETUP_OUT_CONNFAILED :
				//System.out.println(Constants.SETUP_OUT_CONNFAILED);
				parseMenuValue = 4;
				break;

			case Constants.SETUP_OUT_CONNFINISH :
				//System.out.println(Constants.SETUP_OUT_CONNFINISH);
				parseMenuValue = 5;
				break;

			case Constants.SETUP_IN_PARTICIPATE :
				//System.out.println(Constants.SETUP_IN_PARTICIPATE);
				parseMenuValue = 6;
				break;

			case Constants.SETUP_IN_SETUPNUM :
				//System.out.println(Constants.SETUP_OUT_CONNFINISH);
				parseMenuValue = 7;
				break;

			case Constants.GENERAL_SETUP_DISCONNECT :
				//System.out.println(Constants.GENERAL_SETUP_DISCONNECT);
				parseMenuValue = 8;
				break;


			default:
				printError(3);
				break;
		}

		return parseMenuValue;
	}

	private void printPlayers(NetLobbyPreparation netLobby){ //inside printGame...
		System.out.println("Players and their order turn:");
		while(netLobby != null){
			System.out.println(" " + netLobby.player + " - " + netLobby.order);
			netLobby = netLobby.next;
		}
	}

	private void printGameStarting(){
		System.out.println("The game is starting...");
		printPlayers(netLobbyPreparation);
	}

	/**
	 *
	 * @param value indicates a certain error: depending on it, it will print a certain error message.
	 */
	private void printError(int value){
		switch (value){
			case '0' :
				System.out.println("Please insert a valid number:");
				break;

			case '1' :
				System.out.println("Another player online has this nickname: please change it.");
				break;

			case '2' :
				System.out.println("Invalid IP address.");
				break;

			case '3' :
				System.out.println("Unexpected server message.");
				break;

			case '4' :
				System.out.println("Invalid number of players.");
				break;

			default:
				System.out.println("Undefined error");
				break;
		}
	}

	/**
	 *
	 * @param inp nickname just inserted
	 * @param netL to check players' nickname already connected
	 * @return true if there isn't already a player with the same nickname just inserted
	 */
	private boolean checkNickname(Command inp, NetLobbyPreparation netL ){ //TODO check if parameter netL is useful
		boolean value = true;
		while(netL != null && value){
			if(inp.commandType.equals(netL.player)){
				value = false;
			}
			netL = netL.next;
		}
		return value;
	}

	public void notifyCliMenu(){
		synchronized (messagesMenu){
			messagesMenu.notify();
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
		System.out.println("\t\tINSERT A NUMBER:\n");

	}

}
