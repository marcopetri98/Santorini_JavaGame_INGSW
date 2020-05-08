package it.polimi.ingsw.ui.cli.view;

import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.game.*;
import it.polimi.ingsw.network.objects.*;
import it.polimi.ingsw.ui.cli.controller.UserInputController;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.exceptions.UserInputTimeoutException;

import it.polimi.ingsw.util.Color;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CliGame {
	// other view object and attributes relative to the connection with server
	private Deque<NetObject> messages;
	private CliInput inputGetter;
	private UserInputController inputController;
	// state attributes that are used to represent the view
	boolean challenger;
	private Turn phase;
	private List<String> players;
	private List<Color> playerColors;
	private List<String> gods;
	private NetMap netMap;
	private List<NetMove> netMoves;
	private List<NetBuild> netBuilds;
	private boolean drawPoss = false;
	// attributes used for functioning
	private boolean functioning;
	private final Object inputLock;

	public CliGame(){
		messages = new ArrayDeque<>();
		functioning = true;
		inputLock = new Object();
		players = new ArrayList<>();
		playerColors = new ArrayList<>();
		gods = new ArrayList<>();
		challenger = false;
		inputGetter = new CliInput(); //TODO: necessary??
		phase = new Turn();
	}

	// start method which is the core of game cli class
	public void start() {
		Command currentCommand;

		// functioning is set to false by parseSyntax if the user wants to quit the game
		while (functioning) {
			try {
				// it tries to read user input without interrupting and to be interrupted
				parseMessages();
				//typeInputPrint(); //directly done in parseMessages
				currentCommand = inputGetter.getInput();
				if (parseSyntax(currentCommand)) {
					// the user wrote a correct message that can be wrote in the current phase, so this is sent to the view controller
					inputController.getCommand(currentCommand);
					synchronized (inputLock) {
						try {
							inputLock.wait();
						} catch (InterruptedException e){
							throw new AssertionError(e);
						}
					}
					//parseMessages();
				} else {
					printError();
				}
			} catch (IOException | UserInputTimeoutException e) {
				// the user input read has been interrupted because server has sent a message to the player, this message must be handled
				parseMessages();
			}
		}
	}

	// SETTERS
	public void setInputController(UserInputController inputController) {
		this.inputController = inputController;
	}
	public void setNetMap(NetMap map){
		netMap = map;
	}
	public void addToQueue(String message){
		messages.add(new NetObject(message));
	}
	public void wakeUp(){
		inputLock.notifyAll();
	}

	// INPUT PARSING FUNCTIONS
	/*private void typeInputPrint() {

	}*/
	private boolean parseSyntax(Command command) {
		switch (phase.getPhase()){
			case COLORS :	//syntax: color colorname
				if(command.commandType.equals("colore") && command.getNumParameters() == 1 && (command.getParameter(0).equals("blu") || command.getParameter(0).equals("rosso") || command.getParameter(0).equals("verde"))){
					return true;
				}
				break;

			case GODS :	//syntax: gods god1 god2 god3 OR god mygod
				if(phase.getGodsPhase().equals(GodsPhase.CHALLENGER_CHOICE) && challenger){
					if(command.commandType.equals("gods") && (command.getNumParameters() == 2 || command.getNumParameters() == 3)){
						int j = 0;
						for(int x = 0; x < 3; x++){
							if( command.getParameter(x).equals("apollo") || command.getParameter(x).equals("artemis") || command.getParameter(x).equals("athena") || command.getParameter(x).equals("atlas") || command.getParameter(x).equals("demeter") || command.getParameter(x).equals("hephestus") || command.getParameter(x).equals("minotaur") || command.getParameter(x).equals("pan") || command.getParameter(x).equals("prometheus") ){
								j++;
							}
						}
						if(j == 3){
							return true;
						}
					}
				}
				else if(phase.getGodsPhase().equals(GodsPhase.GODS_CHOICE) || phase.getGodsPhase().equals(GodsPhase.STARTER_CHOICE)){
					if(command.commandType.equals("god") && command.getNumParameters() == 1 && (command.getParameter(0).equals("apollo") || command.getParameter(0).equals("artemis") || command.getParameter(0).equals("athena") || command.getParameter(0).equals("atlas") || command.getParameter(0).equals("demeter") || command.getParameter(0).equals("hephestus") || command.getParameter(0).equals("minotaur") || command.getParameter(0).equals("pan") || command.getParameter(0).equals("prometheus"))){
						return true;
					}
				}
				else if(command.commandType.equals("player") && command.getNumParameters() == 1){
					return true;
				}

				break;

			case SETUP :	//syntax check and something more: worker worker 1 x_coord1 y_coord1 worker2 x_coord2 y_coord2
				if(command.commandType.equals("worker") && command.getNumParameters() == 6 && command.getParameter(0).equals("worker1") && command.getParameter(3).equals("worker2") ){
					boolean flag = true;
					for(int x = 1; x < 6; x++){
						if(x != 3){
							if(!(0 <= Integer.parseInt(command.getParameter(x)) && Integer.parseInt(command.getParameter(x)) <= 4) || netMap.getCell(Integer.parseInt(command.getParameter(1)), Integer.parseInt(command.getParameter(2))).worker != null || netMap.getCell(Integer.parseInt(command.getParameter(4)), Integer.parseInt(command.getParameter(5))).worker != null){
								//checks for an already present worker, but the netMap always has to be up to date!
								flag = false;
							}
						}
					}
					return flag;
				}
				break;

			case PLAYERTURN :	//TODO: where does the player change turn??
				switch (phase.getGamePhase()){
					case BEFOREMOVE :	//only syntax: beforebuild workerX dome/building x_coord y_coord
						if(command.commandType.equals("beforebuild")){
							if(command.getNumParameters() == 4 && (command.getParameter(0).equals("worker1") || command.getParameter(0).equals("worker2")) && (command.getParameter(1).equals("dome") || command.getParameter(1).equals("building"))){
								if(0 <= Integer.parseInt(command.getParameter(2)) && Integer.parseInt(command.getParameter(2)) <= 4 && 0 <= Integer.parseInt(command.getParameter(3)) && Integer.parseInt(command.getParameter(3)) <= 4 ){
									return true;
								}
							}
						}
						break;

					case MOVE :		//only syntax: move workerX x_coord y_coord
						if(command.commandType.equals("move")){
							if(command.getNumParameters() == 3 && (command.getParameter(0).equals("worker1") || command.getParameter(0).equals("worker2"))){
								if(0 <= Integer.parseInt(command.getParameter(1)) && Integer.parseInt(command.getParameter(1)) <= 4 && 0 <= Integer.parseInt(command.getParameter(2)) && Integer.parseInt(command.getParameter(2)) <= 4 ){
									return true;
								}
							}
						}
						break;

					case BUILD :	//only syntax: build workerX dome/building x_coord y_coord
						if(command.commandType.equals("build")){
							if(command.getNumParameters() == 4 && (command.getParameter(0).equals("worker1") || command.getParameter(0).equals("worker2")) && (command.getParameter(1).equals("dome") || command.getParameter(1).equals("building"))){
								if(0 <= Integer.parseInt(command.getParameter(2)) && Integer.parseInt(command.getParameter(2)) <= 4 && 0 <= Integer.parseInt(command.getParameter(3)) && Integer.parseInt(command.getParameter(3)) <= 4 ){
									return true;
								}
							}
						}
						break;
				}
		}
		return false;
	}
	/*private boolean parseCorrect(Command command) {

		return false;
	}*/
	private void parseMessages(){
		while(messages.size() != 0){
			parseMessage(messages.pop());
		}
	}
	private void parseMessage(NetObject obj){
		NetColorPreparation ncp;
		NetDivinityChoice ndc;
		NetGaming ng;
		switch (obj.message){
			//COLORS
			case Constants.COLOR_OTHER :
				System.out.println("Other players are now chosing the colors. Hang on.");
				break;

			case Constants.COLOR_YOU :
				while (phase.getPhase() != Phase.COLORS) {
					phase.advance();
				}
				System.out.println("Insert the color you want to use with the following syntax: color red/green/blue");
				System.out.print("Insert the color: ");	//check color's ok in parsesyntax
				break;

			case Constants.COLOR_ERROR :
				System.out.println("The color is not available or the syntax was wrong.");
				break;

			case Constants.COLOR_CHOICES :
				ncp = (NetColorPreparation) obj;
				players.add(ncp.player);
				playerColors.add(ncp.color);
				if(ncp.next != null){
					players.add(ncp.next.player);
					playerColors.add(ncp.next.color);
				}
				break;

			//GODS
			case Constants.GODS_CHALLENGER :
				phase.advance();
				challenger = true;
				System.out.print("Insert the gods you want to use with the following syntax: gods nomedivinità1 nomedivinità2 nomedivinità3\nScegli tra le seguenti divinità: apollo, artemis, athena, atlas, demeter, hephestus, minotaur, pan, prometheus.\n");
				System.out.print("Insert the gods: ");	//check gods are ok in parsesyntax
				break;

			case Constants.GODS_CHOOSE_STARTER :
				System.out.println("Choose the player that has to start as the first one in the following list. Write with this syntax: player playername");
				for(String p : players){
					System.out.print(p + "\n");
				}
				System.out.print("Insert the player name: ");	//check name is ok in parsesyntax
				break;

			case Constants.GODS_STARTER :
				ndc = (NetDivinityChoice) obj;
				phase.advance();
				System.out.println("Questo è il giocatore che inizierà il turno: " + ndc.player);
				break;

			case Constants.GODS_YOU :
				System.out.print("Insert the god power you want to use with the following syntax: god nomedivinità\nScegli tra le seguenti divinità: apollo, artemis, athena, atlas, demeter, hephestus, minotaur, pan, prometheus.\n");
				System.out.print("Insert the god: ");	//check god is ok in parsesyntax
				break;

			case Constants.GODS_OTHER :
				System.out.println("Other players are now chosing the god. Hang on.");
				break;

			case Constants.GODS_ERROR :
				System.out.println("An error occurred while choosing the god.");
				break;

			case Constants.GODS_CHOICES :
				phase.advance();
				ndc = (NetDivinityChoice) obj;
				gods.add(ndc.divinity);
				if(ndc.next != null){
					gods.add(ndc.next.divinity);
				}
				break;

			//SETUP [WORKERS ON MAP]
			case Constants.GAMESETUP_PLACE :
				phase.advance();
				NetGameSetup ntg = (NetGameSetup) obj;
				this.netMap = ntg.gameMap;
				System.out.println("Place the workers with the following syntax: worker worker1 x_coord y_coord worker2 x_coord y_coord");
				System.out.print("Now place the workers on the map: ");	//check workers are ok in parsesyntax
				break;

			case Constants.GAMESETUP_ERROR :
				System.out.println("An error occurred while positioning the workers.");
				break;

			//ACTUAL GAME
			case Constants.PLAYER_ERROR :
				System.out.println("The message sent is not correct.");
				break;

			case Constants.PLAYER_MOVE :
				phase.advance();
				System.out.println("Now it's your turn! Move one of your workers. Use this syntax: move workerX x_coord y_coord");
				System.out.println("Here is the map with the positions where you can move, marked with @:");
				ng = (NetGaming) obj;
				netMoves = ng.availablePositions.moves; //TODO: check - as well as case PLAYER_BUILD
				drawPossibilities();
				System.out.print("Move your worker: ");	//check the move is correct in parsesyntax
				break;

			case Constants.PLAYER_BUILD :
				phase.advance();
				System.out.println("Now you have to build a buildingor a dome near a worker. Use this syntax: build workerX x_coord y_coord or, if you haven't moved any worker yet, the syntax: beforebuild workerX x_coord y_coord");
				System.out.println("Here is the map with the position where you can build:");
				ng = (NetGaming) obj;
				netBuilds = ng.availableBuildings.builds;
				drawPossibilities();
				System.out.print("Build: ");	//check the build is correct in parsesyntax
				break;

			case Constants.PLAYER_FINISHED_TURN :
				ng = (NetGaming) obj;
				System.out.println(ng.player + " has just finished the turn.");
				phase.advance();
				break;

			case Constants.OTHERS_TURN :
				phase.advance();
				System.out.println("A player has just finished his turn.");
				ng = (NetGaming) obj;
				netMap = ng.gameMap;
				System.out.println("This is the new map:");
				drawMap();
				break;

			case Constants.OTHERS_ERROR :
				System.out.println("An error occurred while running another player's turn.");
				break;

			//SUPPORT
			case Constants.CHECK :	//TODO: check if the ping sending to the server is indeed correct!!! It may not be!
				System.out.println("The server just pinged this client. Responding to the ping...");
				messages.push(new NetObject(Constants.CHECK));
				System.out.println("Ping is now queued.");
				break;

			//GENERAL SIGNALS
			case Constants.GENERAL_ERROR :
				System.out.println("An error occurred while inserting the data.");
				break;

			case Constants.GENERAL_SETUP_DISCONNECT :
				NetGameSetup ngs = (NetGameSetup) obj;
				System.out.println(ngs.player + " just disconnected. The game is shutting off.");
				break;

			case Constants.GENERAL_FATAL_ERROR :
				System.out.println("Sorry, a fatal error has occurred and the server shut down.");
				break;

			case Constants.GENERAL_PLAYER_DISCONNECTED :
				ng = (NetGaming) obj;
				System.out.println(ng.player + " just disconnected.");
				break;

			case Constants.GENERAL_WINNER :
				ng = (NetGaming) obj;
				for(String p : players) {
					if(ng.player != null && ng.player.equals(p)){
						System.out.println(ng.player + " just won the game!");
						break;
					}
				}
				System.out.println("You won! Good job!");
				break;

			case Constants.GENERAL_DEFEATED :
				ng = (NetGaming) obj;
				for(String p : players) {
					if(ng.player != null && ng.player.equals(p)){
						System.out.println(ng.player + " just lost.");
						break;
					}
				}
				System.out.println("You lost the game.");
				break;

			case Constants.GENERAL_GAMEMAP_UPDATE :
				System.out.println("The map has changed, take a look:");
				drawMap();
				break;

			case Constants.GENERAL_PHASE_UPDATE :
				phase.advance();
				System.out.println("The game phase just changed!");
				break;
		}
	}

	// PRINTING FUNCTIONS
	private void printError() {

	}

	// DRAWING FUNCTIONS
	private void drawPossibilities(){
		drawPoss = true;
		drawMap();
	}
	public void drawMap(){
		System.out.println("+-------------+-------------+-------------+-------------+-------------+");
		//System.out.println("|" + drawSpaces(0) + "|" + drawSpaces(0) + "|" + drawSpaces(0) + "|" + drawSpaces(0) + "|" + drawSpaces(0) + "|");
		System.out.println("|" + drawSpaces(1, netMap.getCell(0,0)) + drawWorker(netMap.getCell(0,0)) + drawSpaces(1, netMap.getCell(0,0)) + "|" + drawSpaces(1, netMap.getCell(1,0)) + drawWorker(netMap.getCell(1,0)) + drawSpaces(1, netMap.getCell(1,0)) + "|" + drawSpaces(1, netMap.getCell(2,0)) + drawWorker(netMap.getCell(2,0)) + drawSpaces(1, netMap.getCell(2,0)) + "|" + drawSpaces(1, netMap.getCell(3,0)) + drawWorker(netMap.getCell(3,0)) + drawSpaces(1, netMap.getCell(3,0)) + "|" + drawSpaces(1, netMap.getCell(4,0)) + drawWorker(netMap.getCell(4,0)) + drawSpaces(1, netMap.getCell(4,0)) + "|");
		System.out.println("|" + drawSpaces(1, netMap.getCell(0,0)) + drawDome(netMap.getCell(0,0)) + drawSpaces(1, netMap.getCell(0,0)) + "|" + drawSpaces(1, netMap.getCell(1,0)) + drawDome(netMap.getCell(1,0)) + drawSpaces(1, netMap.getCell(1,0)) + "|" + drawSpaces(1, netMap.getCell(2,0)) + drawDome(netMap.getCell(2,0)) + drawSpaces(1, netMap.getCell(2,0)) + "|" + drawSpaces(1, netMap.getCell(3,0)) + drawDome(netMap.getCell(3,0)) + drawSpaces(1, netMap.getCell(3,0)) + "|" + drawSpaces(1, netMap.getCell(4,0)) + drawDome(netMap.getCell(4,0)) + drawSpaces(1, netMap.getCell(4,0)) + "|");
		System.out.println("|" + drawSpaces(2, netMap.getCell(0,0)) + drawBuilding(netMap.getCell(0,0)) + drawSpaces(2, netMap.getCell(0,0)) + "|" + drawSpaces(2, netMap.getCell(1,0)) + drawBuilding(netMap.getCell(1,0)) + drawSpaces(2, netMap.getCell(1,0)) + "|" + drawSpaces(2, netMap.getCell(2,0)) + drawBuilding(netMap.getCell(2,0)) + drawSpaces(2, netMap.getCell(2,0)) + "|" + drawSpaces(2, netMap.getCell(3,0)) + drawBuilding(netMap.getCell(3,0)) + drawSpaces(2, netMap.getCell(3,0)) + "|" + drawSpaces(2, netMap.getCell(4,0)) + drawBuilding(netMap.getCell(4,0)) + drawSpaces(2, netMap.getCell(4,0)) + "|");
		System.out.println("+-------------+-------------+-------------+-------------+-------------+");
		System.out.println("|" + drawSpaces(1, netMap.getCell(0,1)) + drawWorker(netMap.getCell(0,1)) + drawSpaces(1, netMap.getCell(0,1)) + "|" + drawSpaces(1, netMap.getCell(1,1)) + drawWorker(netMap.getCell(1,1)) + drawSpaces(1, netMap.getCell(1,1)) + "|" + drawSpaces(1, netMap.getCell(2,1)) + drawWorker(netMap.getCell(2,1)) + drawSpaces(1, netMap.getCell(2,1)) + "|" + drawSpaces(1, netMap.getCell(3,1)) + drawWorker(netMap.getCell(3,1)) + drawSpaces(1, netMap.getCell(3,1)) + "|" + drawSpaces(1, netMap.getCell(4,1)) + drawWorker(netMap.getCell(4,1)) + drawSpaces(1, netMap.getCell(4,1)) + "|");
		System.out.println("|" + drawSpaces(1, netMap.getCell(0,1)) + drawDome(netMap.getCell(0,1)) + drawSpaces(1, netMap.getCell(0,1)) + "|" + drawSpaces(1, netMap.getCell(1,1)) + drawDome(netMap.getCell(1,1)) + drawSpaces(1, netMap.getCell(1,1)) + "|" + drawSpaces(1, netMap.getCell(2,1)) + drawDome(netMap.getCell(2,1)) + drawSpaces(1, netMap.getCell(2,1)) + "|" + drawSpaces(1, netMap.getCell(3,1)) + drawDome(netMap.getCell(3,1)) + drawSpaces(1, netMap.getCell(3,1)) + "|" + drawSpaces(1, netMap.getCell(4,1)) + drawDome(netMap.getCell(4,1)) + drawSpaces(1, netMap.getCell(4,1)) + "|");
		System.out.println("|" + drawSpaces(2, netMap.getCell(0,1)) + drawBuilding(netMap.getCell(0,1)) + drawSpaces(2, netMap.getCell(0,1)) + "|" + drawSpaces(2, netMap.getCell(1,1)) + drawBuilding(netMap.getCell(1,1)) + drawSpaces(2, netMap.getCell(1,1)) + "|" + drawSpaces(2, netMap.getCell(2,1)) + drawBuilding(netMap.getCell(2,1)) + drawSpaces(2, netMap.getCell(2,1)) + "|" + drawSpaces(2, netMap.getCell(3,1)) + drawBuilding(netMap.getCell(3,1)) + drawSpaces(2, netMap.getCell(3,1)) + "|" + drawSpaces(2, netMap.getCell(4,1)) + drawBuilding(netMap.getCell(4,1)) + drawSpaces(2, netMap.getCell(4,1)) + "|");
		System.out.println("+-------------+-------------+-------------+-------------+-------------+");
		System.out.println("|" + drawSpaces(1, netMap.getCell(0,2)) + drawWorker(netMap.getCell(0,2)) + drawSpaces(1, netMap.getCell(0,2)) + "|" + drawSpaces(1, netMap.getCell(1,2)) + drawWorker(netMap.getCell(1,2)) + drawSpaces(1, netMap.getCell(1,2)) + "|" + drawSpaces(1, netMap.getCell(2,2)) + drawWorker(netMap.getCell(2,2)) + drawSpaces(1, netMap.getCell(2,2)) + "|" + drawSpaces(1, netMap.getCell(3,2)) + drawWorker(netMap.getCell(3,2)) + drawSpaces(1, netMap.getCell(3,2)) + "|" + drawSpaces(1, netMap.getCell(4,2)) + drawWorker(netMap.getCell(4,2)) + drawSpaces(1, netMap.getCell(4,2)) + "|");
		System.out.println("|" + drawSpaces(1, netMap.getCell(0,2)) + drawDome(netMap.getCell(0,2)) + drawSpaces(1, netMap.getCell(0,2)) + "|" + drawSpaces(1, netMap.getCell(1,2)) + drawDome(netMap.getCell(1,2)) + drawSpaces(1, netMap.getCell(1,2)) + "|" + drawSpaces(1, netMap.getCell(2,2)) + drawDome(netMap.getCell(2,2)) + drawSpaces(1, netMap.getCell(2,2)) + "|" + drawSpaces(1, netMap.getCell(3,2)) + drawDome(netMap.getCell(3,2)) + drawSpaces(1, netMap.getCell(3,2)) + "|" + drawSpaces(1, netMap.getCell(4,2)) + drawDome(netMap.getCell(4,2)) + drawSpaces(1, netMap.getCell(4,2)) + "|");
		System.out.println("|" + drawSpaces(2, netMap.getCell(0,2)) + drawBuilding(netMap.getCell(0,2)) + drawSpaces(2, netMap.getCell(0,2)) + "|" + drawSpaces(2, netMap.getCell(1,2)) + drawBuilding(netMap.getCell(1,2)) + drawSpaces(2, netMap.getCell(1,2)) + "|" + drawSpaces(2, netMap.getCell(2,2)) + drawBuilding(netMap.getCell(2,2)) + drawSpaces(2, netMap.getCell(2,2)) + "|" + drawSpaces(2, netMap.getCell(3,2)) + drawBuilding(netMap.getCell(3,2)) + drawSpaces(2, netMap.getCell(3,2)) + "|" + drawSpaces(2, netMap.getCell(4,2)) + drawBuilding(netMap.getCell(4,2)) + drawSpaces(2, netMap.getCell(4,2)) + "|");
		System.out.println("+-------------+-------------+-------------+-------------+-------------+");
		System.out.println("|" + drawSpaces(1, netMap.getCell(0,3)) + drawWorker(netMap.getCell(0,3)) + drawSpaces(1, netMap.getCell(0,3)) + "|" + drawSpaces(1, netMap.getCell(1,3)) + drawWorker(netMap.getCell(1,3)) + drawSpaces(1, netMap.getCell(1,3)) + "|" + drawSpaces(1, netMap.getCell(2,3)) + drawWorker(netMap.getCell(2,3)) + drawSpaces(1, netMap.getCell(2,3)) + "|" + drawSpaces(1, netMap.getCell(3,3)) + drawWorker(netMap.getCell(3,3)) + drawSpaces(1, netMap.getCell(3,3)) + "|" + drawSpaces(1, netMap.getCell(4,3)) + drawWorker(netMap.getCell(4,3)) + drawSpaces(1, netMap.getCell(4,3)) + "|");
		System.out.println("|" + drawSpaces(1, netMap.getCell(0,3)) + drawDome(netMap.getCell(0,3)) + drawSpaces(1, netMap.getCell(0,3)) + "|" + drawSpaces(1, netMap.getCell(1,3)) + drawDome(netMap.getCell(1,3)) + drawSpaces(1, netMap.getCell(1,3)) + "|" + drawSpaces(1, netMap.getCell(2,3)) + drawDome(netMap.getCell(2,3)) + drawSpaces(1, netMap.getCell(2,3)) + "|" + drawSpaces(1, netMap.getCell(3,3)) + drawDome(netMap.getCell(3,3)) + drawSpaces(1, netMap.getCell(3,3)) + "|" + drawSpaces(1, netMap.getCell(4,3)) + drawDome(netMap.getCell(4,3)) + drawSpaces(1, netMap.getCell(4,3)) + "|");
		System.out.println("|" + drawSpaces(2, netMap.getCell(0,3)) + drawBuilding(netMap.getCell(0,3)) + drawSpaces(2, netMap.getCell(0,3)) + "|" + drawSpaces(2, netMap.getCell(1,3)) + drawBuilding(netMap.getCell(1,3)) + drawSpaces(2, netMap.getCell(1,3)) + "|" + drawSpaces(2, netMap.getCell(2,3)) + drawBuilding(netMap.getCell(2,3)) + drawSpaces(2, netMap.getCell(2,3)) + "|" + drawSpaces(2, netMap.getCell(3,3)) + drawBuilding(netMap.getCell(3,3)) + drawSpaces(2, netMap.getCell(3,3)) + "|" + drawSpaces(2, netMap.getCell(4,3)) + drawBuilding(netMap.getCell(4,3)) + drawSpaces(2, netMap.getCell(4,3)) + "|");
		System.out.println("+-------------+-------------+-------------+-------------+-------------+");
		System.out.println("|" + drawSpaces(1, netMap.getCell(0,4)) + drawWorker(netMap.getCell(0,4)) + drawSpaces(1, netMap.getCell(0,4)) + "|" + drawSpaces(1, netMap.getCell(1,4)) + drawWorker(netMap.getCell(1,4)) + drawSpaces(1, netMap.getCell(1,4)) + "|" + drawSpaces(1, netMap.getCell(2,4)) + drawWorker(netMap.getCell(2,4)) + drawSpaces(1, netMap.getCell(2,4)) + "|" + drawSpaces(1, netMap.getCell(3,4)) + drawWorker(netMap.getCell(3,4)) + drawSpaces(1, netMap.getCell(3,4)) + "|" + drawSpaces(1, netMap.getCell(4,4)) + drawWorker(netMap.getCell(4,4)) + drawSpaces(1, netMap.getCell(4,4)) + "|");
		System.out.println("|" + drawSpaces(1, netMap.getCell(0,4)) + drawDome(netMap.getCell(0,4)) + drawSpaces(1, netMap.getCell(0,4)) + "|" + drawSpaces(1, netMap.getCell(1,4)) + drawDome(netMap.getCell(1,4)) + drawSpaces(1, netMap.getCell(1,4)) + "|" + drawSpaces(1, netMap.getCell(2,4)) + drawDome(netMap.getCell(2,4)) + drawSpaces(1, netMap.getCell(2,4)) + "|" + drawSpaces(1, netMap.getCell(3,4)) + drawDome(netMap.getCell(3,4)) + drawSpaces(1, netMap.getCell(3,4)) + "|" + drawSpaces(1, netMap.getCell(4,4)) + drawDome(netMap.getCell(4,4)) + drawSpaces(1, netMap.getCell(4,4)) + "|");
		System.out.println("|" + drawSpaces(2, netMap.getCell(0,4)) + drawBuilding(netMap.getCell(0,4)) + drawSpaces(2, netMap.getCell(0,4)) + "|" + drawSpaces(2, netMap.getCell(1,4)) + drawBuilding(netMap.getCell(1,4)) + drawSpaces(2, netMap.getCell(1,4)) + "|" + drawSpaces(2, netMap.getCell(2,4)) + drawBuilding(netMap.getCell(2,4)) + drawSpaces(2, netMap.getCell(2,4)) + "|" + drawSpaces(2, netMap.getCell(3,4)) + drawBuilding(netMap.getCell(3,4)) + drawSpaces(2, netMap.getCell(3,4)) + "|" + drawSpaces(2, netMap.getCell(4,4)) + drawBuilding(netMap.getCell(4,4)) + drawSpaces(2, netMap.getCell(4,4)) + "|");
		System.out.println("+-------------+-------------+-------------+-------------+-------------+");

		drawPoss = false;
		//System.out.println('\u0905');
	}
	public String drawSpaces(int type, NetCell netC){
		if(drawPoss){
			if(phase.getGamePhase() == GamePhase.MOVE){
				for(NetMove netM : netMoves){
					if(netM.cellX == netMap.getX(netC) && netM.cellY == netMap.getX(netC)){
						if (type == 0) {
							return "@@@@@@@@@@@@@";
						}
						else if (type == 1) {
							return "@@@@@@";
						}
						else if (type == 2) {
							return "@@@@@";
						}
					}
					else{
						if (type == 0) {
							return "             ";
						}
						else if (type == 1) {
							return "      ";
						}
						else if (type == 2) {
							return "     ";
						}
					}
				}
			}
			else if(phase.getGamePhase() == GamePhase.BEFOREMOVE || phase.getGamePhase() == GamePhase.BUILD){
				for(NetBuild netB : netBuilds){
					if(netB.cellX == netMap.getX(netC) && netB.cellY == netMap.getX(netC)){
						if (type == 0) {
							return "@@@@@@@@@@@@@";
						}
						else if (type == 1) {
							return "@@@@@@";
						}
						else if (type == 2) {
							return "@@@@@";
						}
					}
					else{
						if (type == 0) {
							return "             ";
						}
						else if (type == 1) {
							return "      ";
						}
						else if (type == 2) {
							return "     ";
						}
					}
				}
			}
		}
		else{
			if (type == 0) {
				return "             ";
			}
			else if (type == 1) {
				return "      ";
			}
			else if (type == 2) {
				return "     ";
			}
		}
		return "ERROR";
	}
	public char drawWorker(NetCell netC){
		if(netC.worker != null){
			return 'W';
		}
		return ' ';
	}
	public char drawDome(NetCell netC){
		if (netC.building.dome) {
			return 'D';
		}
		return ' ';
	}
	public String drawBuilding(NetCell netC){
		if (netC.building.level == 3) {
			return "B:3";
		}
		else if (netC.building.level == 2) {
			return "B:2";
		}
		else if (netC.building.level == 1) {
			return "B:1";
		}
		return "   ";
	}

}