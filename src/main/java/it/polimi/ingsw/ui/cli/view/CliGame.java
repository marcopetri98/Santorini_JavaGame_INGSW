package it.polimi.ingsw.ui.cli.view;

import it.polimi.ingsw.core.Move;
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
	private final Deque<NetObject> messages;
	private CliInput cliInput;
	private UserInputController inputController;
	// state attributes that are used to represent the view
	boolean challenger;
	private Turn phase;
	private List<String> players;
	private List<Color> playerColors;
	private List<String> gods;
	private String activePlayer;
	private String starterPlayer;
	private NetMap netMap;
	private List<NetMove> netMoves;
	private List<NetBuild> netBuilds;
	private boolean drawPoss = false;
	private String code = "null";
	private String others;
	// attributes used for functioning
	private boolean functioning;
	private final Object inputLock;

	public CliGame(CliInput inputGetter) {
		messages = new ArrayDeque<>();
		cliInput = inputGetter;
		inputController = null;
		// game attributes
		challenger = false;
		phase = new Turn();
		players = new ArrayList<>();
		playerColors = new ArrayList<>();
		gods = new ArrayList<>();
		activePlayer = null;
		netMap = null;
		netMoves = null;
		netBuilds = null;
		// functioning attributes
		functioning = true;
		inputLock = new Object();
	}

	// start method which is the core of game cli class
	public void start() {
		Command currentCommand;

		printInitialPhase();
		// functioning is set to false by parseSyntax if the user wants to quit the game
		while (functioning) {
			try {
				// it tries to read user input without interrupting and to be interrupted
				parseMessages();
				typeInputPrint();
				currentCommand = cliInput.getInput();
				if (parseSyntax(currentCommand)) {
					// the user wrote a correct message that can be wrote in the current phase, so this is sent to the view controller
					inputController.getCommand(currentCommand,phase.clone());
				} else {
					printError();
				}

			} catch (IOException | UserInputTimeoutException e) {
				// the user input read has been interrupted because server has sent a message to the player, this message must be handled
				parseMessages();
			} catch (IllegalStateException e) {
				throw new AssertionError("Fatal error: design problem, the get command is called with a command that does not match game phase");
			}
		}
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *			GETTERS OF THIS CLASS				*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	public Turn getPhase(){
		return phase.clone();
	}
	public Turn getPhasePointer() {
		return phase;
	}
	public List<String> getPlayers() {
		return new ArrayList<>(players);
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *			SETTERS OF THIS CLASS				*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	// SETTERS
	public void setInputController(UserInputController inputController) {
		this.inputController = inputController;
	}
	public void setNetMap(NetMap map){
		netMap = map;
	}
	public void setPlayers(List<String> players) {
		this.players = new ArrayList<>(players);
		activePlayer = this.players.get(0);
	}
	public void addNetMove(Move m){
		netMoves.add(new NetMove(m));
	}
	public void addToQueue(NetObject message) {
		synchronized (messages) {
			messages.add(message);
		}
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *				PARSING FUNCTIONS				*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/

	private boolean parseSyntax(Command command) {
		switch (phase.getPhase()) {
			//syntax: color colorname
			case COLORS -> {
				if (command.commandType.equals(Constants.COMMAND_COLOR_CHOICE) && command.getNumParameters() == 1 && Constants.COMMAND_COLOR_COLORS.contains(command.getParameter(0))) {
					return true;
				}
			}

			//syntax: gods god1 god2 god3 OR god mygod
			case GODS -> {
				if (phase.getGodsPhase().equals(GodsPhase.CHALLENGER_CHOICE) && challenger) {
					if (command.commandType.equals(Constants.COMMAND_GODS_CHOICES) && (command.getNumParameters() == 2 || command.getNumParameters() == 3)) {
						int j = 0;
						if(command.getNumParameters() == 2){
							for (int x = 0; x < 2; x++) {
								if (Constants.GODS_GOD_NAMES.contains(command.getParameter(x).toUpperCase())) {
									j++;
								}
							}
							if (j == 2) {
								return true;
							}
						}
						else if(command.getNumParameters() == 3){
							for (int x = 0; x < 3; x++) {
								if (Constants.GODS_GOD_NAMES.contains(command.getParameter(x).toUpperCase())) {
									j++;
								}
							}
							if (j == 3) {
								return true;
							}
						}
					}
				} else if (phase.getGodsPhase().equals(GodsPhase.GODS_CHOICE) || phase.getGodsPhase().equals(GodsPhase.STARTER_CHOICE)) {
					if (command.commandType.equals(Constants.COMMAND_GODS_CHOOSE) && command.getNumParameters() == 1 && Constants.GODS_GOD_NAMES.contains(command.getParameter(0).toUpperCase())) {
						return true;
					}
				} else if (command.commandType.equals(Constants.COMMAND_GODS_STARTER) && command.getNumParameters() == 1 && players.contains(command.getParameter(0)) && challenger) {
					return true;
				}
			}

			//syntax check and something more: worker worker1 x_coord1 y_coord1 worker2 x_coord2 y_coord2
			case SETUP -> {
				if (command.commandType.equals(Constants.COMMAND_GAMESETUP_POSITION) && command.getNumParameters() == 6 && command.getParameter(0).equals("worker1") && command.getParameter(3).equals("worker2")) {
					for (int i = 2; i < 6; i++) {
						if (i != 3) {
							if (0 < Integer.parseInt(command.getParameter(i)) || Integer.parseInt(command.getParameter(i)) > 4) {
								return false;
							}
						}
					}
					if (netMap.getCell(Integer.parseInt(command.getParameter(1)), Integer.parseInt(command.getParameter(2))).worker != null || netMap.getCell(Integer.parseInt(command.getParameter(4)), Integer.parseInt(command.getParameter(5))).worker != null) {
						return false;
					}
				}
			}

			case PLAYERTURN -> {
				switch (phase.getGamePhase()) {
					//only syntax: build workerX dome/building level x_coord y_coord
					case BEFOREMOVE, BUILD -> {
						if (command.commandType.equals(Constants.COMMAND_BUILD)) {
							// FIXME 1: if the user is trying to build in a cell with a dome or another worker this must return an error, here it returns true
							// FIXME 2: if the user is trying to build in a position that isn't present in netbuild list is forbidden
							if (command.getNumParameters() == 4 && (command.getParameter(0).equals("worker1") || command.getParameter(0).equals("worker2")) && (command.getParameter(1).equals("dome") || command.getParameter(1).equals("building"))) {
								if (0 <= Integer.parseInt(command.getParameter(2)) && Integer.parseInt(command.getParameter(2)) <= 4 && 0 <= Integer.parseInt(command.getParameter(3)) && Integer.parseInt(command.getParameter(3)) <= 4) {
									return true;
								}
							}
						}
					}

					//only syntax: move workerX x_coord y_coord
					case MOVE -> {
						if (command.commandType.equals(Constants.COMMAND_MOVE)) {
							if (command.getNumParameters() == 3 && (command.getParameter(0).equals("worker1") || command.getParameter(0).equals("worker2"))) {
								if (0 <= Integer.parseInt(command.getParameter(1)) && Integer.parseInt(command.getParameter(1)) <= 4 && 0 <= Integer.parseInt(command.getParameter(2)) && Integer.parseInt(command.getParameter(2)) <= 4) {
									return true;
								}
							}
						}
					}
				}
			}

			default -> {
				if (command.commandType.equals(Constants.COMMAND_DISCONNECT)) {
					return true;
				}
			}
		}
		return false;
	}

	private void parseMessages(){
		if(messages.size() != 0){
			parseMessage(messages.getFirst());
		}
		/*while(messages.size() != 0){
			parseMessage(messages.getFirst());
		}*/
	}
	// FIXME: this is a parsing function, this isn't a drawing or printing function, it should only parse the message

	private void parseMessage(NetObject obj){
		switch (phase.getPhase()) {
			case COLORS:
				parseColors(obj);
				messages.remove();
				break;

			case GODS:
				parseGods(obj);
				messages.remove();
				break;

			case SETUP:
				parseSetup(obj);
				messages.remove();
				break;

			case PLAYERTURN:
				parsePlayerTurn(obj);
				messages.remove();
				break;
		}

		parseOther(obj);
	}

	private void parseColors(NetObject obj) {
		NetColorPreparation ncp;
		switch (obj.message) {
			//COLORS
			case Constants.COLOR_YOU :
				//System.out.println("Insert the color you want to use with the following syntax: color red/green/blue");
				//System.out.print("Insert the color: ");    //check color's ok in parsesyntax
				code = Constants.COLOR_YOU;
				break;

			case Constants.COLOR_ERROR :
				//System.out.println("The color is not available or the syntax was wrong.");
				code = Constants.COLOR_ERROR;
				break;

			case Constants.COLOR_CHOICES :
				ncp = (NetColorPreparation) obj;
				players.add(ncp.player);
				playerColors.add(ncp.color);
				if (ncp.next != null) {
					players.add(ncp.next.player);
					playerColors.add(ncp.next.color);
				}
				break;
		}

	}

	private void parseGods(NetObject obj){
		NetDivinityChoice ndc;
		switch (obj.message) {
			//GODS
			case Constants.GODS_CHALLENGER:
				challenger = true;
				//System.out.print("Insert the gods you want to use with the following syntax: gods godname1 godname2 godname3\nChoose among the following gods: apollo, artemis, athena, atlas, demeter, hephaestus, minotaur, pan, prometheus.\n");
				//System.out.print("Insert the gods: ");    //check gods are ok in parsesyntax
				code = Constants.GODS_CHALLENGER;
				break;

			case Constants.GODS_CHOOSE_STARTER:
				/*System.out.println("Choose the player that has to start as the first one in the following list. Write with this syntax: player playername");
				for (String p : players) {
					System.out.print(p + "\n");
				}
				System.out.print("Insert the player name: ");*/    //check name is ok in parsesyntax
				code = Constants.GODS_CHOOSE_STARTER;
				break;

			case Constants.GODS_STARTER :
				ndc = (NetDivinityChoice) obj;
				activePlayer = players.get(players.indexOf(ndc.player));
				//System.out.println("This is the player who is going to start the game: " + ndc.player);
				code = Constants.GODS_STARTER;
				break;

			case Constants.GODS_YOU :
				//System.out.print("Insert the god power you want to use with the following syntax: god godname\nChoose among the following gods: apollo, artemis, athena, atlas, demeter, hephaestus, minotaur, pan, prometheus.\n");
				//System.out.print("Insert the god: ");    //check god is ok in parsesyntax
				code = Constants.GODS_YOU;
				break;

			case Constants.GODS_OTHER :
				//System.out.println("Other players are now chosing the god. Hang on.");
				code = Constants.GODS_OTHER;
				break;

			case Constants.GODS_ERROR :
				//System.out.println("An error occurred while choosing the god.");
				code = Constants.GODS_ERROR;
				break;

			case Constants.GODS_CHOICES :
				ndc = (NetDivinityChoice) obj;
				gods.add(ndc.divinity);
				if (ndc.next != null) {
					gods.add(ndc.next.divinity);
				}
				break;
		}

	}

	private void parseSetup(NetObject obj){
		switch (obj.message) {
			//SETUP [WORKERS ON MAP]
			case Constants.GAMESETUP_PLACE:
				NetGameSetup ntg = (NetGameSetup) obj;
				this.netMap = ntg.gameMap;
				//System.out.println("Place the workers with the following syntax: worker worker1 x_coord y_coord worker2 x_coord y_coord");
				//System.out.print("Now place the workers on the map: ");    //check workers are ok in parsesyntax
				code = Constants.GAMESETUP_PLACE;
				break;

			case Constants.GAMESETUP_ERROR:
				//System.out.println("An error occurred while positioning the workers.");
				code = Constants.GAMESETUP_ERROR;
				break;
		}
	}

	private void parsePlayerTurn(NetObject obj){
		NetGaming ng;
		switch (obj.message) {
			//ACTUAL GAME
			case Constants.PLAYER_ERROR :
				//System.out.println("The message sent is not correct.");
				code = Constants.PLAYER_ERROR;
				break;

			case Constants.PLAYER_MOVE :
				//System.out.println("Now it's your turn! Move one of your workers. Use this syntax: move workerX x_coord y_coord");
				//System.out.println("Here is the map with the positions where you can move, marked with @:");
				ng = (NetGaming) obj;
				netMoves = ng.availablePositions.moves; //TODO: check - as well as case PLAYER_BUILD
				//drawPossibilities();
				//System.out.print("Move your worker: ");    //check the move is correct in parsesyntax
				code = Constants.PLAYER_MOVE;
				break;

			case Constants.PLAYER_BUILD :
				//System.out.println("Now you have to build a building or a dome near a worker. Use this syntax: build workerX x_coord y_coord or, if you haven't moved any worker yet, the syntax: beforebuild workerX x_coord y_coord");
				//System.out.println("Here is the map with the position where you can build:");
				ng = (NetGaming) obj;
				netBuilds = ng.availableBuildings.builds;
				//drawPossibilities();
				//System.out.print("Now build: ");    //check the build is correct in parsesyntax
				code = Constants.PLAYER_BUILD;
				break;

			case Constants.PLAYER_FINISHED_TURN :
				ng = (NetGaming) obj;
				others = ng.player;
				//System.out.println(ng.player + " has just finished the turn.");
				code = Constants.PLAYER_FINISHED_TURN;
				break;

			case Constants.OTHERS_TURN :
				//System.out.println("A player has just finished his turn.");
				ng = (NetGaming) obj;
				netMap = ng.gameMap;
				//System.out.println("This is the new map:");
				//drawMap();
				code = Constants.OTHERS_TURN;
				break;

			case Constants.OTHERS_ERROR :
				//System.out.println("An error occurred while running another player's turn.");
				code = Constants.OTHERS_ERROR;
				break;
		}
	}

	private void parseOther(NetObject obj){
		NetGaming ng;
		switch (obj.message) {
			//SUPPORT
			case Constants.CHECK :	//TODO: check if the ping sending to the server is indeed correct!!! It may not be!
				messages.push(new NetObject(Constants.CHECK));
				//System.out.println("The server just pinged this client. Responding to the ping.");
				messages.remove();
				code = Constants.CHECK;
				break;

			//GENERAL SIGNALS
			case Constants.GENERAL_ERROR :
				//System.out.println("An error occurred while inserting the data.");
				messages.remove();
				code = Constants.GENERAL_ERROR;
				break;

			case Constants.GENERAL_SETUP_DISCONNECT :
				NetGameSetup ngs = (NetGameSetup) obj;
				others = ngs.player;
				//System.out.println(ngs.player + " just disconnected. The game is shutting off.");
				messages.remove();
				code = Constants.GENERAL_SETUP_DISCONNECT;
				break;

			case Constants.GENERAL_FATAL_ERROR :
				//System.out.println("Sorry, a fatal error has occurred and the server shut down.");
				messages.remove();
				code = Constants.GENERAL_FATAL_ERROR;
				break;

			case Constants.GENERAL_PLAYER_DISCONNECTED :
				ng = (NetGaming) obj;
				others = ng.player;
				//System.out.println(ng.player + " just disconnected.");
				messages.remove();
				code = Constants.GENERAL_PLAYER_DISCONNECTED;
				break;

			case Constants.GENERAL_WINNER :
				ng = (NetGaming) obj;
				for(String p : players) {
					if(ng.player != null && ng.player.equals(p)){
						others = ng.player;
						//System.out.println(ng.player + " just won the game!");
						break;
					}
				}
				System.out.println("You won! Good job!");
				messages.remove();
				break;

			case Constants.GENERAL_DEFEATED :
				ng = (NetGaming) obj;
				for(String p : players) {
					if(ng.player != null && ng.player.equals(p)){
						others = ng.player;
						//System.out.println(ng.player + " just lost.");
						break;
					}
				}
				others = "You";
				//System.out.println("You lost the game.");
				messages.remove();
				code = Constants.GENERAL_DEFEATED;
				break;

			case Constants.GENERAL_GAMEMAP_UPDATE :
				//System.out.println("The map has changed, take a look:");
				//drawMap();
				messages.remove();
				code = Constants.GENERAL_GAMEMAP_UPDATE;
				break;

			case Constants.GENERAL_PHASE_UPDATE :
				if (phase.getGamePhase() == GamePhase.BUILD) {
					activePlayer = players.get(players.indexOf(activePlayer) == players.size()-1 ? 0 : players.indexOf(activePlayer)+1);
				}
				phase.advance();
				//System.out.println("The game phase just changed! New phase: " + phase.getPhase());
				messages.remove();
				code = Constants.GENERAL_PHASE_UPDATE;
				break;
		}
	}


	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *		PRINTING/DRAWING OF THIS CLASS			*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	private void printInitialPhase() {
		switch (phase.getPhase()) {
			case COLORS -> {
				System.out.print("\n\n");
				System.out.print("\t\t--------------------------------------------------------------------------\n" +
						"\t\t|                                                                        |\n" +
						"\t\t|      _____ ____  _      ____  _____     _____ ______ _      ______ _____ _______ _____ ____  _   _    |\n" +
						"\t\t|     / ____/ __ \\| |    / __ \\|  __ \\   / ____|  ____| |    |  ____/ ____|__   __|_   _/ __ \\| \\ | |   |\n" +
						"\t\t|    | |   | |  | | |   | |  | | |__) | | (___ | |__  | |    | |__ | |       | |    | || |  | |  \\| |   |\n" +
						"\t\t|    | |   | |  | | |   | |  | |  _  /   \\___ \\|  __| | |    |  __|| |       | |    | || |  | | . ` |   |\n" +
						"\t\t|    | |___| |__| | |___| |__| | | \\ \\   ____) | |____| |____| |___| |____   | |   _| || |__| | |\\  |   |\n" +
						"\t\t|     \\_____\\____/|______\\____/|_|  \\_\\ |_____/|______|______|______\\_____|  |_|  |_____\\____/|_| \\_|   |\n" +
						"\t\t|                                                                        |\n" +
						"\t\t--------------------------------------------------------------------------\n\n\n");
			}
		}
	}

	// PRINTING FUNCTIONS
	private void printError() {

	}

	private void typeInputPrint() {
		switch (phase.getPhase()) {
			case COLORS:
				typeColorsPrint();
				break;

			case GODS:
				typeGodsPrint();
				break;

			case SETUP:
				typeSetupPrint();
				break;

			case PLAYERTURN:
				typePlayerTurnPrint();
				break;
		}

		typeOtherPrint();
	}

	private void typeColorsPrint(){
		switch(code){
			case Constants.COLOR_YOU :
				System.out.println("Insert the color you want to use with the following syntax: color red/green/blue");
				System.out.print("Insert the color: ");    //check color's ok in parsesyntax
				break;

			case Constants.COLOR_ERROR :
				System.out.println("The color is not available or the syntax was wrong.");
				break;

		}

	}

	private void typeGodsPrint(){
		switch(code){
			case Constants.GODS_CHALLENGER :
				System.out.print("Insert the gods you want to use with the following syntax: gods godname1 godname2 godname3\nChoose among the following gods: apollo, artemis, athena, atlas, demeter, hephaestus, minotaur, pan, prometheus.\n");
				System.out.print("Insert the gods: ");    //check gods are ok in parsesyntax
				break;

			case Constants.GODS_CHOOSE_STARTER :
				System.out.println("Choose the player that has to start as the first one in the following list. Write with this syntax: player playername");
				for (String p : players) {
					System.out.print(p + "\n");
				}
				System.out.print("Insert the player name: ");    //check name is ok in parsesyntax
				break;

			case Constants.GODS_STARTER :
				System.out.println("This is the player who is going to start the game: " + activePlayer); //TODO : May choose the wrong player to print?
				break;

			case Constants.GODS_YOU :
				System.out.print("Insert the god power you want to use with the following syntax: god godname\nChoose among the following gods: apollo, artemis, athena, atlas, demeter, hephaestus, minotaur, pan, prometheus.\n");
				System.out.print("Insert the god: ");    //check god is ok in parsesyntax
				break;

			case Constants.GODS_OTHER :
				System.out.println("Other players are now chosing the god. Hang on.");
				break;

			case Constants.GODS_ERROR :
				System.out.println("An error occurred while choosing the god.");
				break;
		}
	}

	private void typeSetupPrint(){
		switch(code){
			case Constants.GAMESETUP_PLACE :
				System.out.println("Place the workers with the following syntax: worker worker1 x_coord y_coord worker2 x_coord y_coord");
				System.out.print("Now place the workers on the map: ");    //check workers are ok in parsesyntax
				break;

			case Constants.GAMESETUP_ERROR :
				System.out.println("An error occurred while positioning the workers.");
				break;

		}
	}

	private void typePlayerTurnPrint(){
		switch(code){
			case Constants.PLAYER_ERROR :
				System.out.println("The message sent is not correct.");
				break;

			case Constants.PLAYER_MOVE :
				System.out.println("Now it's your turn! Move one of your workers. Use this syntax: move workerX x_coord y_coord");
				System.out.println("Here is the map with the positions where you can move, marked with @:");
				drawPossibilities();
				System.out.print("Move your worker: ");    //check the move is correct in parsesyntax
				break;

			case Constants.PLAYER_BUILD :
				System.out.println("Now you have to build a building or a dome near a worker. Use this syntax: build workerX x_coord y_coord or, if you haven't moved any worker yet, the syntax: beforebuild workerX x_coord y_coord");
				System.out.println("Here is the map with the position where you can build:");
				drawPossibilities();
				System.out.print("Now build: ");    //check the build is correct in parsesyntax
				break;

			case Constants.PLAYER_FINISHED_TURN :
				System.out.println(others + " has just finished the turn.");
				break;

			case Constants.OTHERS_TURN :
				System.out.println("A player has just finished his turn.");
				System.out.println("This is the new map:");
				drawMap();
				break;

			case Constants.OTHERS_ERROR :
				System.out.println("An error occurred while running another player's turn.");
				break;

		}
	}

	private void typeOtherPrint(){
		switch(code){
			case Constants.CHECK :
				System.out.println("The server just pinged this client. Responding to the ping.");
				break;

			case Constants.GENERAL_ERROR :
				System.out.println("An error occurred while inserting the data.");
				break;

			case Constants.GENERAL_SETUP_DISCONNECT :
				System.out.println(others + " just disconnected. The game is shutting off.");
				break;

			case Constants.GENERAL_FATAL_ERROR :
				System.out.println("Sorry, a fatal error has occurred and the server shut down.");
				break;

			case Constants.GENERAL_PLAYER_DISCONNECTED :
				System.out.println(others + " just disconnected.");
				break;

			case Constants.GENERAL_WINNER :
				System.out.println(others + " just won the game!");
				break;

			case Constants.GENERAL_DEFEATED :
				System.out.println(others + " just lost.");
				break;

			case Constants.GENERAL_GAMEMAP_UPDATE :
				System.out.println("The map has changed, take a look:");
				drawMap();
				break;

			case Constants.GENERAL_PHASE_UPDATE :
				System.out.println("The game phase just changed! New phase: " + phase.getPhase());
				break;

		}
	}

	// DRAWING FUNCTIONS
	public void drawPossibilities(){
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

		writeAllInfo();

		drawPoss = false;
		//System.out.println('\u0905');
	}
	public String drawSpaces(int type, NetCell netC){
		if(drawPoss){
			if(phase.getGamePhase() == GamePhase.MOVE){
				if(netMoves != null){
					for(NetMove netM : netMoves){
						if(netM.cellX == netMap.getX(netC) && netM.cellY == netMap.getY(netC)){
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
					}
				}
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
			else if(phase.getGamePhase() == GamePhase.BEFOREMOVE || phase.getGamePhase() == GamePhase.BUILD){
				if(netBuilds != null){
					for(NetBuild netB : netBuilds){
						if(netB.cellX == netMap.getX(netC) && netB.cellY == netMap.getY(netC)){
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
					}
				}
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

	public void writeAllInfo(){
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 5; x++){
				NetWorker myWorker = netMap.getCell(x,y).worker;
				if(myWorker != null){
					System.out.println("Il worker " + myWorker.workerID + " del player " + myWorker.owner + " è in posizione:  x = " + x + "; y = " + y + ".");
				}
			}
		}
	}

}