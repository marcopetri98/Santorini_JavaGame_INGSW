package it.polimi.ingsw.ui.cli.view;

import it.polimi.ingsw.network.objects.NetLobbyPreparation;
import it.polimi.ingsw.ui.cli.controller.UserInputController;
import it.polimi.ingsw.util.Constants;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

public class CliInitial {
	private final Deque<NetLobbyPreparation> messages;
	private Command input;
	private CliInput cliInput;
	private UserInputController userInputController;

	public CliInitial() {
		messages = new ArrayDeque<>();
	}

	//setters,getters,constructor...

	/**
	 * This function shows the CLI-logo of the game and the initial menu.
	 * @return 0 when client insert a valid option and there is a right communication with the server
	 */
	public int menu() throws InterruptedException /*throws IOException*/ { //exception or handling the problem???...
		//logo:
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


		int value = cliInput.getInput().getNumParameters(); //TODO: the call of getNumParameters() is temporary: waiting for a getInput() implementation (probably an access to "otherValues" of CliInput)
		int flag = 0;
		int ret = 1;
		while(flag == 0) {
			if (value == 1) {
				synchronized (messages) {
					wait(); //waiting MainCLiController gives here the message from the server. It will notify this thread
					//when it's awake, it has to parse the NetLobby message sent from the server:
					parseLobbyMessage();
					if(input.commandType == Constants.SETUP_OUT_CONNWORKED){
						ret = 0;
						flag = 1;
					} else if(input.commandType == Constants.SETUP_OUT_CONNFAILED){//TODO: temporary implementation: if connection failed, it will be asked again client to insert a valid number
						flag = 0;
					}
				}
			} else if (flag == 2) {
				synchronized (messages) {
					wait();
					parseLobbyMessage();
					if(input.commandType == Constants.SETUP_OUT_CONNFINISH){ //TODO: temporary implementation.
						ret = -1;
						flag = 1;
					} else if(input.commandType == Constants.SETUP_OUT_CONNFAILED){//TODO: temporary implementation: if connection failed, it will be asked again client to insert a valid number
						flag = 0;
					}
				}
			} else {
				System.out.println("Please insert a valid number:");
				value = cliInput.getInput().getNumParameters();
			}
		}

		return ret; //if ret == 0 client choose "1" option; if ret == -1 client choose "2" option.
	}

	public void queueMessage(NetLobbyPreparation msg){
		messages.add(msg);
	}

	private void parseLobbyMessage() { //TODO: probably the aim of this function was something else; this should work though.
		input.commandType = messages.getLast().message;
	}

	public int lobbyCli() {
		//TODO: cliLobby graphic: I'll handle it when the others "TODO" will be fixed.
		int value = 1;
		if(messages.getLast().message == Constants.SETUP_OUT_CONNFINISH){
			printPlayers();
			if(userInputController.getCommand(input) == true) { //if the server received the command //TODO: parameter of getCommand...
				printGameStarting();
				value = 0;            //the game starts
			}
		} else {
			//TODO...
			return value; //==1, means nothing at this moment
		}

		return value;
	}

	private void printPlayers(){ //TODO: probably I should iterate the first element of "messages", not all messages... there isn't yet a useful constant...
		for(NetLobbyPreparation n : messages){
			System.out.println(" " + messages.getLast().player);
		}
	}
	private void printGameStarting(){
		System.out.println("The game is starting...");
	}

}
