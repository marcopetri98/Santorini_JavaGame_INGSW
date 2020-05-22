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
import java.util.*;

public class CliGame {
	// other view object and attributes relative to the connection with server
	private final Deque<NetObject> messages;
	private CliInput cliInput;
	private UserInputController inputController;
	// state attributes that are used to represent the view
	private boolean challenger;
	private Turn phase;
	private List<String> players;
	//private List<Color> playerColors;
	private Map<String, Color> playerColors;
	private List<String> gods;
	private Map<String, String> chosenGods;
	private String player;
	private String activePlayer;
	private NetMap netMap;
	private List<NetMove> netMoves;
	private List<NetBuild> netBuilds;
	private NetMove selectedMove;
	private NetBuild selectedBuild;
	// attributes used for functioning
	private boolean functioning;
	private final Object inputLock;
	private boolean godsGoAlreadyCalled = false;
	private boolean alreadyPrintedPlay = false;
	private boolean drawPoss = false;
	private boolean sentCorrectMessage = false;
	private boolean chooseStarter = false;

	public CliGame(CliInput inputGetter) {
		messages = new ArrayDeque<>();
		cliInput = inputGetter;
		inputController = null;
		// game attributes
		challenger = false;
		phase = new Turn();
		players = new ArrayList<>();
		playerColors = new HashMap<>();
		gods = new ArrayList<>();
		chosenGods = new HashMap<>();
		player = null;
		activePlayer = null;
		netMap = null;
		netMoves = null;
		netBuilds = null;
		selectedMove = null;
		selectedBuild = null;
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
			if(sentCorrectMessage){
				synchronized (inputLock){
					try{
						inputLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace(); //TODO: change action
					}
				}
			}
			try {
				// it tries to read user input without interrupting and to be interrupted
				parseMessages();
				//typeInputPrint();
				//System.out.println("\n"+Constants.BG_CYAN+activePlayer+Constants.RESET+"\n");	//Only for debug purposes
				currentCommand = cliInput.getInput();
				if (parseSyntax(currentCommand)) {
					// the user wrote a correct message that can be wrote in the current phase, so this is sent to the view controller
					if (selectedMove != null) {
						inputController.getCommand(currentCommand,phase.clone(),selectedMove);
					} else if (selectedBuild != null) {
						inputController.getCommand(currentCommand,phase.clone(),selectedBuild);
					} else {
						inputController.getCommand(currentCommand,phase.clone());
					}
					selectedMove = null;
					selectedBuild = null;
					sentCorrectMessage = true;
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
	public void setPlayers(NetLobbyPreparation lobbyMsg) {
		players = new ArrayList<>(players);
		while (lobbyMsg != null) {
			players.add(lobbyMsg.player);
			lobbyMsg = lobbyMsg.next;
		}
	}
	public void setPlayerName(String name) throws NullPointerException {
		if (name == null) {
			throw new NullPointerException();
		}
		player = name;
	}
	public void addToQueue(NetObject message) {
		//System.out.println("\n"+Constants.FG_RED+message.message+Constants.RESET+"\n");	//Only for debug purposes
		synchronized (messages) {
			messages.add(message);
		}
		synchronized (inputLock){
			inputLock.notifyAll();
		}
		sentCorrectMessage = false;
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
		if (command.commandType.equals(Constants.COMMAND_DISCONNECT)) {
			functioning = false;
			return true;
		}

		if(command.commandType.toUpperCase().equals("HELP")){
			printGuide();
			return false;
		}

		if (player.equals(activePlayer)) {
			switch (phase.getPhase()) {
				//syntax: color colorname
				case COLORS -> {
					if (command.commandType.equals(Constants.COMMAND_COLOR_CHOICE) && command.getNumParameters() == 1 && Constants.COMMAND_COLOR_COLORS.contains(command.getParameter(0).toUpperCase())) {
						if(!playerColors.containsValue(new Color(command.getParameter(0)))){
							return true;
						}
					}
					return false;
				}

				//syntax: gods god1 god2 god3 OR god mygod
				case GODS -> {
					if (phase.getGodsPhase().equals(GodsPhase.CHALLENGER_CHOICE) && challenger) {
						if (command.commandType.equals(Constants.COMMAND_GODS_CHOICES) && command.getNumParameters() == players.size()) {
							int j = 0;
							if(command.getNumParameters() == 3) {
								for (int x = 0; x < 3; x++) {
									if (Constants.GODS_GOD_NAMES.contains(command.getParameter(x).toUpperCase())) {
										j++;
									}
								}
							}
							else if(command.getNumParameters() == 2) {
								for (int x = 0; x < 2; x++) {
									if (Constants.GODS_GOD_NAMES.contains(command.getParameter(x).toUpperCase())) {
										j++;
									}
								}
							}
							if (j == command.getNumParameters()) {
								return true;
							}
						}
					} else if (phase.getGodsPhase().equals(GodsPhase.GODS_CHOICE) || phase.getGodsPhase().equals(GodsPhase.STARTER_CHOICE)) {
						// TODO: implement the state of gods chosen locally
						if (command.commandType.equals(Constants.COMMAND_GODS_CHOOSE) && command.getNumParameters() == 1 && Constants.GODS_GOD_NAMES.contains(command.getParameter(0).toUpperCase())) {
							if(gods.contains(command.getParameter(0).toUpperCase()) && !chosenGods.containsValue(command.getParameter(0).toUpperCase())) {
								return true;
							}
						} else if (command.commandType.equals(Constants.COMMAND_GODS_STARTER) && command.getNumParameters() == 1 && players.contains(command.getParameter(0)) && challenger) {
							return true;
						}
					}
					return false;
				}

				//syntax check and something more: worker worker1 x_coord1 y_coord1 worker2 x_coord2 y_coord2
				case SETUP -> {
					if (command.commandType.equals(Constants.COMMAND_GAMESETUP_POSITION) && command.getNumParameters() == 6 && command.getParameter(0).equals("worker1") && command.getParameter(3).equals("worker2")) {
						for (int i = 1; i < 6; i++) {
							if (i != 3) {
								try{
									if (Integer.parseInt(command.getParameter(i)) < 0 || Integer.parseInt(command.getParameter(i)) > 4) {
										return false;
									}
								} catch (NumberFormatException nfe) {
									System.out.println("You didn't insert a correct number");
									return false;
								}
							}
						}
						if(netMap != null){
							try {
								if (netMap.getCell(Integer.parseInt(command.getParameter(1)), Integer.parseInt(command.getParameter(2))).worker != null || netMap.getCell(Integer.parseInt(command.getParameter(4)), Integer.parseInt(command.getParameter(5))).worker != null) {
									return false;
								}
							} catch (NumberFormatException nfe) {
								nfe.printStackTrace();
								return false;
							}

						}
						return true;
					}
					return false;
				}

				case PLAYERTURN -> {
					switch (phase.getGamePhase()) {
						//only syntax: build workerX dome/building level x_coord y_coord
						case BEFOREMOVE, BUILD -> {
							if (command.commandType.equals(Constants.COMMAND_BUILD)) {
								// FIXME 1: if the user is trying to build in a cell with a dome or another worker this must return an error, here it returns true
								// FIXME 2: if the user is trying to build in a position that isn't present in netbuild list is forbidden
								if (command.getNumParameters() == 5 && (command.getParameter(0).equals("worker1") || command.getParameter(0).equals("worker2")) && (command.getParameter(1).equals("dome") || command.getParameter(1).equals("building"))) {
									try {
										if (Integer.parseInt(command.getParameter(2)) >= 0 && Integer.parseInt(command.getParameter(2)) <= 2 && 0 <= Integer.parseInt(command.getParameter(3)) && Integer.parseInt(command.getParameter(3)) <= 4 && 0 <= Integer.parseInt(command.getParameter(4)) && Integer.parseInt(command.getParameter(4)) <= 4) {
											if(command.getParameter(1).equals("dome")){
												if(command.getParameter(0).equals("worker1")){
													selectedBuild = new NetBuild(player.hashCode()+1, Integer.parseInt(command.getParameter(3)), Integer.parseInt(command.getParameter(4)), Integer.parseInt(command.getParameter(2)), true);
												} else if (command.getParameter(0).equals("worker2")){
													selectedBuild = new NetBuild(player.hashCode()+2, Integer.parseInt(command.getParameter(3)), Integer.parseInt(command.getParameter(4)), Integer.parseInt(command.getParameter(2)), true);
												}
											} else if(command.getParameter(1).equals("building")) {
												if(command.getParameter(0).equals("worker1")){
													selectedBuild = new NetBuild(player.hashCode()+1, Integer.parseInt(command.getParameter(3)), Integer.parseInt(command.getParameter(4)), Integer.parseInt(command.getParameter(2)), false);
												} else if (command.getParameter(0).equals("worker2")){
													selectedBuild = new NetBuild(player.hashCode()+2, Integer.parseInt(command.getParameter(3)), Integer.parseInt(command.getParameter(4)), Integer.parseInt(command.getParameter(2)), false);
												}
											}
											/*if(netBuilds.contains(selectedBuild)) {
												return true;
											}*/
											for(NetBuild netB : netBuilds) {
												if(netB.equals(selectedBuild) ||(netB.other != null && netB.other.equals(selectedBuild))){
													return true;
												}
											}
										}
									} catch (NumberFormatException nfe) {
										System.out.println("You didn't insert a correct number");
										return false;
									}

								}
							}
							return false;
						}

						//only syntax: move workerX x_coord y_coord
						case MOVE -> {	//TODO: ammettere anche mosse nella other move
							if (command.commandType.equals(Constants.COMMAND_MOVE)) {
								if (command.getNumParameters() == 3 && (command.getParameter(0).equals("worker1") || command.getParameter(0).equals("worker2"))) {
									try {
										if (0 <= Integer.parseInt(command.getParameter(1)) && Integer.parseInt(command.getParameter(1)) <= 4 && 0 <= Integer.parseInt(command.getParameter(2)) && Integer.parseInt(command.getParameter(2)) <= 4) {
											if(command.getParameter(0).equals("worker1")){
												selectedMove = new NetMove(player.hashCode()+1, Integer.parseInt(command.getParameter(1)), Integer.parseInt(command.getParameter(2)));
											} else if (command.getParameter(0).equals("worker2")){
												selectedMove = new NetMove(player.hashCode()+2, Integer.parseInt(command.getParameter(1)), Integer.parseInt(command.getParameter(2)));
											}
											/*if(netMoves.contains(selectedMove)) {
												return true;
											}*/
											for(NetMove netM : netMoves) {
												if(netM.equals(selectedMove) || (netM.other != null && netM.other.equals(selectedMove))){
													return true;
												}
											}
										}
									} catch (NumberFormatException nfe) {
										System.out.println("You didn't insert a correct number");
										return false;
									}
								}
							}
							return false;
						}
					}
				}
			}
		}
		return false;
	}

	private void parseMessages(){
		while(messages.size() != 0){
			parseMessage(messages.getFirst());
			messages.remove();
		}
	}
	// FIXME: this is a parsing function, this isn't a drawing or printing function, it should only parse the message

	private void parseMessage(NetObject obj){
		if (obj.message.equals(Constants.GENERAL_FATAL_ERROR) || obj.message.equals(Constants.GENERAL_SETUP_DISCONNECT)) {
			printServerError(obj);
			functioning = false;
			messages.remove();
		} else {
			switch (phase.getPhase()) {
				case COLORS:
					parseColors(obj);
					break;

				case GODS:
					parseGods(obj);
					break;

				case SETUP:
					parseSetup(obj);
					break;

				case PLAYERTURN:
					parsePlayerTurn(obj);
					break;
			}
		}

		parseOther(obj);
	}
	private void parseColors(NetObject obj) {
		NetColorPreparation ncp = (NetColorPreparation) obj;
		switch (obj.message) {
			//COLORS
			case Constants.TURN_PLAYERTURN -> {
				if (ncp.player.equals(player)) {
					System.out.println("Insert the color you want with the following syntax \"color colorName\"");
					System.out.print("Choose among: ");
					if(!playerColors.containsValue(new Color("red")))	System.out.print("red ");
					if(!playerColors.containsValue(new Color("green")))	System.out.print("green ");
					if(!playerColors.containsValue(new Color("blue")))	System.out.print("blue");
					System.out.print("\n");
					System.out.print("Insert the color: ");
				} else {
					System.out.println(ncp.player+" is choosing a color.");
				}
				activePlayer = ncp.player;
			}

			case Constants.COLOR_ERROR -> {
				System.out.println("The color is not available or the syntax was wrong.");
			}

			case Constants.COLOR_CHOICES -> {
				while(ncp != null) {
					playerColors.put(ncp.player, ncp.color);
					ncp = ncp.next;
				}
			}

			case Constants.GENERAL_SETUP_DISCONNECT -> {
				functioning = false;
				printServerError(obj);
			}
		}

	}
	private void parseGods(NetObject obj){
		NetDivinityChoice ndc = (NetDivinityChoice) obj;
		switch (ndc.message) {
			//GODS
			case Constants.GODS_CHALLENGER -> {
				if(!player.equals(ndc.player)){	//TODO: challenger player given from server is wrong!!! therefore modify it and this
					challenger = true;
				}
			}

			case Constants.GODS_STARTER -> {
				ndc = (NetDivinityChoice) obj;
				activePlayer = players.get(players.indexOf(ndc.player));
				System.out.println("This is the player who is going to start the game: " + ndc.player);
			}

			case Constants.TURN_PLAYERTURN -> {
				activePlayer = ndc.player;
				if (activePlayer.equals(player)) {
					if (challenger) {
						if (godsGoAlreadyCalled) {
							if(chooseStarter) {
								System.out.println("Insert the starter with this syntax \"starter playerName\"");
								System.out.print("Choose the starter of the game among these: ");
								if(players != null){
									for(String player : players) {
										System.out.print(player + " ");
									}
									System.out.print("\n");
								}
								System.out.print("Insert the starter: ");
							} else {
								System.out.println("Insert the god power you want to use with this syntax \"god godname\"");
								System.out.print("Choose one god among these: ");
								if(gods != null){
									for(String x : gods) {
										if(!chosenGods.containsValue(x))	System.out.print(x.toLowerCase() + " ");
									}
									System.out.print("\n");
								} else {
									System.out.println("Design error, no gods were found");
								}
								System.out.print("Insert the god: ");    //check god is ok in parsesyntax
								chooseStarter = true;
							}
						} else {
							System.out.println("Insert the gods you want to use with the following syntax \"gods godname1 godname2 godname3\"");
							System.out.println("Choose among the following gods: apollo, artemis, athena, atlas, demeter, hephaestus, minotaur, pan, prometheus");
							System.out.print("Insert the gods: ");    //check gods are ok in parsesyntax
							godsGoAlreadyCalled = true;
						}
					} else {
						System.out.println("Insert the god power you want to use with this syntax \"god godname\"");
						System.out.print("Choose one god among these: ");
						if(gods != null){
							for(String x : gods) {
								if(!chosenGods.containsValue(x))	System.out.print(x.toLowerCase() + " ");
							}
							System.out.print("\n");
						} else {
							System.out.println("Design error, no gods were found");
						}
						System.out.print("Insert the god: ");    //check god is ok in parsesyntax
					}
				} else {
					System.out.println("The other player "+activePlayer+" is choosing a god");
				}
			}

			case Constants.GODS_GODS -> {
				gods.addAll(ndc.getDivinities());
				//System.out.println("Just added players' divinities.");
			}

			case Constants.GODS_ERROR -> {
				System.out.println("An error occurred while choosing the god.");
			}

			case Constants.GODS_CHOICES -> {
				while(ndc != null) {
					chosenGods.put(ndc.player, ndc.divinity);
					ndc = ndc.next;
				}
			}

			case Constants.GENERAL_SETUP_DISCONNECT -> {
				functioning = false;
				printServerError(obj);
			}
		}

	}
	private void parseSetup(NetObject obj){
		NetGameSetup ngs = (NetGameSetup)obj;
		switch (obj.message) {
			//SETUP [WORKERS ON MAP]
			case Constants.TURN_PLAYERTURN -> {
				activePlayer = ngs.player;
				if (activePlayer.equals(player)) {
					//drawMap();
					System.out.println("Place the workers with the following syntax: position worker1 x_coord y_coord worker2 x_coord y_coord");
					System.out.print("Now place the workers on the map: ");		//check workers are ok in parsesyntax
				} else {
					System.out.println("The other player "+activePlayer+" is setting up the workers");
				}
			}

			case Constants.GAMESETUP_ERROR -> {
				System.out.println("An error occurred while positioning the workers.");
			}

			case Constants.GENERAL_SETUP_DISCONNECT -> {
				functioning = false;
				printServerError(obj);
			}
		}
	}
	private void parsePlayerTurn(NetObject obj){
		NetGaming ng = (NetGaming) obj;
		switch (obj.message) {
			//ACTUAL GAME
			case Constants.PLAYER_ERROR -> {
				System.out.println("The message sent is not correct.");
			}

			case Constants.TURN_PLAYERTURN -> {
				activePlayer = ng.player;
			}

			case Constants.PLAYER_ACTIONS -> {
				if (activePlayer.equals(player)) {
					switch (phase.getGamePhase()) {
						case BEFOREMOVE -> {
							if(chosenGods.containsValue("PROMETHEUS")) {
								if(chosenGods.get(player).equals("PROMETHEUS")) {
									ng = (NetGaming) obj;
									netBuilds = ng.availableBuildings.builds;
									netMoves = ng.availablePositions.moves;
									drawPossibilities();
									System.out.println("Now you have to first build a building and then move. Or you can just move. Use the syntax \"build workerX x_coord y_coord\" and then \"move workerX x_coord y_coord\"");
									System.out.print("Now it's your turn: ");
								}
							}
						}
						case MOVE -> {
							ng = (NetGaming) obj;
							netMoves = ng.availablePositions.moves;
							drawPossibilities();
							System.out.println("Here is the map with the positions where you can move, marked with @");
							System.out.println("Now it's your turn! Move one of your workers. Use the syntax \"move workerX x_coord y_coord\"");
							System.out.print("Move your worker: ");    //check the move is correct in parsesyntax
						}
						case BUILD -> {
							ng = (NetGaming) obj;
							netBuilds = ng.availableBuildings.builds;
							drawPossibilities();
							System.out.println("Here is the map with the position where you can build");
							System.out.println("Now you have to build a building or a dome near the worker. Use the syntax \"build workerX dome/building level x_coord y_coord\"");
							System.out.print("Now build: ");    //check the build is correct in parsesyntax
						}
					}
				} else {
					System.out.println("You can only disconnect, it's "+ng.player+"'s turn.");
				}
			}

			case Constants.OTHERS_ERROR -> {
				System.out.println("An error occurred while running another player's turn.");
			}
		}
	}
	private void parseOther(NetObject obj) {
		NetGaming ng;
		NetGameSetup ngs;
		switch (obj.message) {
			//GENERAL SIGNALS
			case Constants.GENERAL_ERROR:
				System.out.println("An error occurred while inserting the data.");
				break;

			case Constants.GENERAL_PLAYER_DISCONNECTED:
				ng = (NetGaming) obj;
				System.out.println(ng.player + " just disconnected.");
				break;

			case Constants.GENERAL_WINNER:
				ng = (NetGaming) obj;
				for (String p : players) {
					if (ng.player != null && ng.player.equals(p)) {
						System.out.println(ng.player + " just won the game!");
						break;
					}
				}
				System.out.println("You won! Good job!");
				break;

			case Constants.GENERAL_DEFEATED:
				ng = (NetGaming) obj;
				for (String p : players) {
					if (ng.player != null && ng.player.equals(p)) {
						System.out.println(ng.player + " just lost");
						break;
					}
				}
				System.out.println("You lost the game");
				break;

			case Constants.GENERAL_GAMEMAP_UPDATE:
				if (phase.getPhase() == Phase.SETUP) {
					ngs = (NetGameSetup) obj;
					this.netMap = ngs.gameMap;
					System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + "The map has just changed, take a look\n");
					drawMap();
				} else if (phase.getPhase() == Phase.PLAYERTURN) {
					ng = (NetGaming) obj;
					this.netMap = ng.gameMap;
					System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + "The map has just changed, take a look\n");
					drawMap();
				}
				break;

			case Constants.GENERAL_PHASE_UPDATE:
				phase.advance();
				printInitialPhase();
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
				System.out.print("\t\t\t\t\t\t\t\t\t\t\t\t----------------------------------------------------------------------------------------------------------\n" +
						"\t\t\t\t\t\t\t\t\t\t\t\t|                                                                                                        |\n" +
						"\t\t\t\t\t\t\t\t\t\t\t\t|      _____ ____  _      ____  _____     _____ ______ _      ______ _____ _______ _____ ____  _   _     |\n" +
						"\t\t\t\t\t\t\t\t\t\t\t\t|     / ____/ __ \\| |    / __ \\|  __ \\   / ____|  ____| |    |  ____/ ____|__   __|_   _/ __ \\| \\ | |    |\n" +
						"\t\t\t\t\t\t\t\t\t\t\t\t|    | |   | |  | | |   | |  | | |__) | | (___ | |__  | |    | |__ | |       | |    | || |  | |  \\| |    |\n" +
						"\t\t\t\t\t\t\t\t\t\t\t\t|    | |   | |  | | |   | |  | |  _  /   \\___ \\|  __| | |    |  __|| |       | |    | || |  | | . ` |    |\n" +
						"\t\t\t\t\t\t\t\t\t\t\t\t|    | |___| |__| | |___| |__| | | \\ \\   ____) | |____| |____| |___| |____   | |   _| || |__| | |\\  |    |\n" +
						"\t\t\t\t\t\t\t\t\t\t\t\t|     \\_____\\____/|______\\____/|_|  \\_\\ |_____/|______|______|______\\_____|  |_|  |_____\\____/|_| \\_|    |\n" +
						"\t\t\t\t\t\t\t\t\t\t\t\t|                                                                                                        |\n" +
						"\t\t\t\t\t\t\t\t\t\t\t\t----------------------------------------------------------------------------------------------------------\n\n\n");
			}

			case GODS -> {
				if(phase.getGodsPhase() == GodsPhase.CHALLENGER_CHOICE){
					System.out.print("\n\n");
					System.out.print("\t\t\t\t\t\t\t\t\t\t\t\t\t-----------------------------------------------------------------------------------------------------\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t|                                                                                                   |\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t|      _____  ____  _____   _____    _____ ______ _      ______ _____ _______ _____ ____  _   _     |\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t|     / ____|/ __ \\|  __ \\ / ____|  / ____|  ____| |    |  ____/ ____|__   __|_   _/ __ \\| \\ | |    |\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t|    | |  __| |  | | |  | | (___   | (___ | |__  | |    | |__ | |       | |    | || |  | |  \\| |    |\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t|    | | |_ | |  | | |  | |\\___ \\   \\___ \\|  __| | |    |  __|| |       | |    | || |  | | . ` |    |\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t|    | |__| | |__| | |__| |____) |  ____) | |____| |____| |___| |____   | |   _| || |__| | |\\  |    |\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t|     \\_____|\\____/|_____/|_____/  |_____/|______|______|______\\_____|  |_|  |_____\\____/|_| \\_|    |\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t|                                                                                                   |\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t-----------------------------------------------------------------------------------------------------\n\n\n");

				}
			}

			case SETUP -> {
				System.out.print("\n\n");
				System.out.print("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t-----------------------------------------------\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|                                             |\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|      _____ ______ _______ _    _ _____      |\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|     / ____|  ____|__   __| |  | |  __ \\     |\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|    | (___ | |__     | |  | |  | | |__) |    |\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|     \\___ \\|  __|    | |  | |  | |  ___/     |\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|     ____) | |____   | |  | |__| | |         |\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|    |_____/|______|  |_|   \\____/|_|         |\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|                                             |\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t-----------------------------------------------\n\n\n");
			}

			case PLAYERTURN -> {
				if(!alreadyPrintedPlay){
					System.out.print("\n\n");
					System.out.print("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t-----------------------------------------\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|                                       |\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|     _____  _           __     ___     |\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|    |  __ \\| |        /\\\\ \\   / / |    |\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|    | |__) | |       /  \\\\ \\_/ /| |    |\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|    |  ___/| |      / /\\ \\\\   / | |    |\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|    | |    | |____ / ____ \\| |  |_|    |\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|    |_|    |______/_/    \\_\\_|  (_)    |\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t|                                       |\n"+
							"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t-----------------------------------------\n\n\n");
					alreadyPrintedPlay = true;
				}
			}
		}
	}

	private void printGuide() {
		System.out.println(Constants.FG_RED + "\n\n\n\t\t\t\t\t\t\t\t\t\t\t\t\t************************* ~ Benvenuto nella guida di Santorini! ~ *************************" + Constants.RESET);
		System.out.println("Qui ti verrà spiegato come giocare, e quali comandi utilizzare per poterlo fare correttamente.\n");
		System.out.println("Il gioco è articolato in varie fasi, la prima delle quali consiste nel determinare i colori che vuoi utilizzare. Per fare ciò, il comando è \"color colorName\", in cui colorName può essere un colore a scelta tra red, green e blue.");
		System.out.println("Nella seconda fase, avviene la scelta delle divinità: innanzitutto il \"challenger\" sceglierà due o tre divinità, a seconda del numero di giocatori, grazie al comando \"gods godName1 godName2 godName3\".\nChiaramente, in un gioco con due player, godName3 non verrà inserito.\n");
		System.out.println("Successivamente, ogni giocatore provvede a scegliere il dio che vuole utilizzare tra quelli scelti dal challenger.Il comando da utilizzare per fare ciò, quando richiesto dal gioco, è \"god godName\". Ricorda: puoi scegliere solo uno tra gli dei scelti dal challenger!");
		System.out.println("Ad un giocatore verrà ora chiesto di scegliere chi dovrà iniziare la partita: la scelta può ricadere su qualunque giocatore nel gioco, anche se stessi! Attenzione però: non per forza cominciare per primo rappresenta un vantaggio.");
		System.out.println("Il comando per scegliere chi dovrà iniziare la partita è \"starter playerName\"\n");
		System.out.println("La prossima fase è il setup del gioco: bisogna posizionare i worker! Il comando è \"position worker1 x_coord y_coord worker2 x_coord y_coord\". Al posto di x_coord e y_coord metti le coordinate alle quali vuoi posizionare il tuo worker.");
		System.out.println("Attenzione: non puoi posizionare un worker dove già è presente un altro worker, anche se di un altro giocatore! Presta attenzione alla sintassi, è molto importante.\n");
		System.out.println("E ora, ha inizio la fase di gioco! Ad ogni turno, sei costretto a muovere un worker e costruire, pena la sconfitta! Per muovere, il comando è \"move workerX x_coord y_coord\": sostituisci a workerX il worker che vuoi muovere.");
		System.out.println("Per costruire, scrivi \"build workerX dome/building level x_coord y_coord\": scegli se costruire una dome o un building, dopodiché inserisci il livello al quale vuoi costruire (0, 1 ,2) e le coordinate. Ricorda: puoi costruire SOLO con il worker che hai mosso!");
		System.out.println("Ricorda anche che puoi costruire una dome solo se è già presente un edificio di livello 3, a meno di poteri particolari del tuo dio!");
		System.out.println(Constants.FG_RED + "\t\t\t\t\t\t\t\t\t\t\t\t\t*******************************************************************************************" + Constants.RESET);
		System.out.println("\n\n");
	}

	private void printError() {
		if (player.equals(activePlayer)) {
			switch (phase.getPhase()) {
				case COLORS -> System.out.println("You must choose a color that is red, blue or green and that is not taken by another player");
				case GODS -> {
					if (player.equals(players.get(0))) {
						switch (phase.getGodsPhase()) {
							case CHALLENGER_CHOICE -> System.out.println("You must choose "+players.size()+" gods from the previously showed");
							case GODS_CHOICE -> System.out.println("You must choose one of the god chosen by the challenger that is not taken by another player");
							case STARTER_CHOICE -> System.out.println("You must choose a player that is inside the game");
						}
					} else {
						System.out.println("You must choose one of the god chosen by the challenger that is not taken by another player.");
					}
				}
				case SETUP -> System.out.println("You must place the worker in a valid cell, not occupied by other workers");
				case PLAYERTURN -> {
					switch (phase.getGamePhase()) {
						case BEFOREMOVE -> System.out.println("You can build only in the cells showed before");
						case MOVE -> System.out.println("You must move in the cells showed before");
						case BUILD -> System.out.println("You must build only in one of the cells showed before");
					}
				}
			}
		} else {
			System.out.println("You can only disconnect during turn of other players.");
		}
	}
	private void printServerError(NetObject obj) {
		switch (obj.message) {
			case Constants.GENERAL_SETUP_DISCONNECT -> {
				System.out.print("\n\n\n---------------------------------------------------------\n" +
						"-                                                       -\n" +
						"-                                                       -\n" +
						"-     SOMEONE HAS DISCONNECTED AND GAME IS FINISHED     -\n" +
						"-                                                       -\n" +
						"-                                                       -\n" +
						"---------------------------------------------------------\n\n\n");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					System.out.println("There has been an interruption problem");
				}
			}
			case Constants.GENERAL_FATAL_ERROR -> {
				System.out.print("\n\n\n---------------------------------------------------------\n" +
						"-                                                       -\n" +
						"-                                                       -\n" +
						"-        SERVER HAS GONE OFFLINE FOR SOME REASON        -\n" +
						"-                                                       -\n" +
						"-                                                       -\n" +
						"---------------------------------------------------------\n\n\n");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					System.out.println("There has been an interruption problem");
				}
			}
		}
	}
	private void typeInputPrint() {
		if (player.equals(activePlayer)) {
			switch (phase.getPhase()) {
				case COLORS -> System.out.print("Type the color you want to use: ");
				case GODS -> {
					if (player.equals(players.get(0))) {
						switch (phase.getGodsPhase()) {
							case CHALLENGER_CHOICE -> System.out.print("Choose the gods for the game: ");
							case GODS_CHOICE -> System.out.print("Choose your god for the game: ");
							case STARTER_CHOICE -> System.out.print("Choose the starter player: ");
						}
					} else {
						System.out.print("Choose your god for the game: ");
					}
				}
				case SETUP -> System.out.print("Place a worker on the map: ");
				case PLAYERTURN -> {
					switch (phase.getGamePhase()) {
						case BEFOREMOVE -> System.out.print("You can build before moving: ");
						case MOVE -> System.out.print("Move a worker: ");
						case BUILD -> System.out.print("Build in a place with the worker you used before: ");
					}
				}
			}
		} else {
			System.out.print("You can disconnect if you want, otherwise you can wait: ");
		}
	}

	// DRAWING FUNCTIONS
	public void drawPossibilities(){
		drawPoss = true;
		drawMap();
	}
	public void drawMap(){
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "       0      |      1      |      2      |      3      |      4       ");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "+-------------+-------------+-------------+-------------+-------------+");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(1, netMap.getCell(0,0)) + drawWorker(netMap.getCell(0,0)) + drawSpaces(1, netMap.getCell(0,0)) + "|" + drawSpaces(1, netMap.getCell(1,0)) + drawWorker(netMap.getCell(1,0)) + drawSpaces(1, netMap.getCell(1,0)) + "|" + drawSpaces(1, netMap.getCell(2,0)) + drawWorker(netMap.getCell(2,0)) + drawSpaces(1, netMap.getCell(2,0)) + "|" + drawSpaces(1, netMap.getCell(3,0)) + drawWorker(netMap.getCell(3,0)) + drawSpaces(1, netMap.getCell(3,0)) + "|" + drawSpaces(1, netMap.getCell(4,0)) + drawWorker(netMap.getCell(4,0)) + drawSpaces(1, netMap.getCell(4,0)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t0\t" + "|" + drawSpaces(1, netMap.getCell(0,0)) + drawDome(netMap.getCell(0,0)) + drawSpaces(1, netMap.getCell(0,0)) + "|" + drawSpaces(1, netMap.getCell(1,0)) + drawDome(netMap.getCell(1,0)) + drawSpaces(1, netMap.getCell(1,0)) + "|" + drawSpaces(1, netMap.getCell(2,0)) + drawDome(netMap.getCell(2,0)) + drawSpaces(1, netMap.getCell(2,0)) + "|" + drawSpaces(1, netMap.getCell(3,0)) + drawDome(netMap.getCell(3,0)) + drawSpaces(1, netMap.getCell(3,0)) + "|" + drawSpaces(1, netMap.getCell(4,0)) + drawDome(netMap.getCell(4,0)) + drawSpaces(1, netMap.getCell(4,0)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(2, netMap.getCell(0,0)) + drawBuilding(netMap.getCell(0,0)) + drawSpaces(2, netMap.getCell(0,0)) + "|" + drawSpaces(2, netMap.getCell(1,0)) + drawBuilding(netMap.getCell(1,0)) + drawSpaces(2, netMap.getCell(1,0)) + "|" + drawSpaces(2, netMap.getCell(2,0)) + drawBuilding(netMap.getCell(2,0)) + drawSpaces(2, netMap.getCell(2,0)) + "|" + drawSpaces(2, netMap.getCell(3,0)) + drawBuilding(netMap.getCell(3,0)) + drawSpaces(2, netMap.getCell(3,0)) + "|" + drawSpaces(2, netMap.getCell(4,0)) + drawBuilding(netMap.getCell(4,0)) + drawSpaces(2, netMap.getCell(4,0)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t-\t" + "+-------------+-------------+-------------+-------------+-------------+");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(1, netMap.getCell(0,1)) + drawWorker(netMap.getCell(0,1)) + drawSpaces(1, netMap.getCell(0,1)) + "|" + drawSpaces(1, netMap.getCell(1,1)) + drawWorker(netMap.getCell(1,1)) + drawSpaces(1, netMap.getCell(1,1)) + "|" + drawSpaces(1, netMap.getCell(2,1)) + drawWorker(netMap.getCell(2,1)) + drawSpaces(1, netMap.getCell(2,1)) + "|" + drawSpaces(1, netMap.getCell(3,1)) + drawWorker(netMap.getCell(3,1)) + drawSpaces(1, netMap.getCell(3,1)) + "|" + drawSpaces(1, netMap.getCell(4,1)) + drawWorker(netMap.getCell(4,1)) + drawSpaces(1, netMap.getCell(4,1)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t1\t" + "|" + drawSpaces(1, netMap.getCell(0,1)) + drawDome(netMap.getCell(0,1)) + drawSpaces(1, netMap.getCell(0,1)) + "|" + drawSpaces(1, netMap.getCell(1,1)) + drawDome(netMap.getCell(1,1)) + drawSpaces(1, netMap.getCell(1,1)) + "|" + drawSpaces(1, netMap.getCell(2,1)) + drawDome(netMap.getCell(2,1)) + drawSpaces(1, netMap.getCell(2,1)) + "|" + drawSpaces(1, netMap.getCell(3,1)) + drawDome(netMap.getCell(3,1)) + drawSpaces(1, netMap.getCell(3,1)) + "|" + drawSpaces(1, netMap.getCell(4,1)) + drawDome(netMap.getCell(4,1)) + drawSpaces(1, netMap.getCell(4,1)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(2, netMap.getCell(0,1)) + drawBuilding(netMap.getCell(0,1)) + drawSpaces(2, netMap.getCell(0,1)) + "|" + drawSpaces(2, netMap.getCell(1,1)) + drawBuilding(netMap.getCell(1,1)) + drawSpaces(2, netMap.getCell(1,1)) + "|" + drawSpaces(2, netMap.getCell(2,1)) + drawBuilding(netMap.getCell(2,1)) + drawSpaces(2, netMap.getCell(2,1)) + "|" + drawSpaces(2, netMap.getCell(3,1)) + drawBuilding(netMap.getCell(3,1)) + drawSpaces(2, netMap.getCell(3,1)) + "|" + drawSpaces(2, netMap.getCell(4,1)) + drawBuilding(netMap.getCell(4,1)) + drawSpaces(2, netMap.getCell(4,1)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t-\t" + "+-------------+-------------+-------------+-------------+-------------+");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(1, netMap.getCell(0,2)) + drawWorker(netMap.getCell(0,2)) + drawSpaces(1, netMap.getCell(0,2)) + "|" + drawSpaces(1, netMap.getCell(1,2)) + drawWorker(netMap.getCell(1,2)) + drawSpaces(1, netMap.getCell(1,2)) + "|" + drawSpaces(1, netMap.getCell(2,2)) + drawWorker(netMap.getCell(2,2)) + drawSpaces(1, netMap.getCell(2,2)) + "|" + drawSpaces(1, netMap.getCell(3,2)) + drawWorker(netMap.getCell(3,2)) + drawSpaces(1, netMap.getCell(3,2)) + "|" + drawSpaces(1, netMap.getCell(4,2)) + drawWorker(netMap.getCell(4,2)) + drawSpaces(1, netMap.getCell(4,2)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t2\t" + "|" + drawSpaces(1, netMap.getCell(0,2)) + drawDome(netMap.getCell(0,2)) + drawSpaces(1, netMap.getCell(0,2)) + "|" + drawSpaces(1, netMap.getCell(1,2)) + drawDome(netMap.getCell(1,2)) + drawSpaces(1, netMap.getCell(1,2)) + "|" + drawSpaces(1, netMap.getCell(2,2)) + drawDome(netMap.getCell(2,2)) + drawSpaces(1, netMap.getCell(2,2)) + "|" + drawSpaces(1, netMap.getCell(3,2)) + drawDome(netMap.getCell(3,2)) + drawSpaces(1, netMap.getCell(3,2)) + "|" + drawSpaces(1, netMap.getCell(4,2)) + drawDome(netMap.getCell(4,2)) + drawSpaces(1, netMap.getCell(4,2)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(2, netMap.getCell(0,2)) + drawBuilding(netMap.getCell(0,2)) + drawSpaces(2, netMap.getCell(0,2)) + "|" + drawSpaces(2, netMap.getCell(1,2)) + drawBuilding(netMap.getCell(1,2)) + drawSpaces(2, netMap.getCell(1,2)) + "|" + drawSpaces(2, netMap.getCell(2,2)) + drawBuilding(netMap.getCell(2,2)) + drawSpaces(2, netMap.getCell(2,2)) + "|" + drawSpaces(2, netMap.getCell(3,2)) + drawBuilding(netMap.getCell(3,2)) + drawSpaces(2, netMap.getCell(3,2)) + "|" + drawSpaces(2, netMap.getCell(4,2)) + drawBuilding(netMap.getCell(4,2)) + drawSpaces(2, netMap.getCell(4,2)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t-\t" + "+-------------+-------------+-------------+-------------+-------------+");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(1, netMap.getCell(0,3)) + drawWorker(netMap.getCell(0,3)) + drawSpaces(1, netMap.getCell(0,3)) + "|" + drawSpaces(1, netMap.getCell(1,3)) + drawWorker(netMap.getCell(1,3)) + drawSpaces(1, netMap.getCell(1,3)) + "|" + drawSpaces(1, netMap.getCell(2,3)) + drawWorker(netMap.getCell(2,3)) + drawSpaces(1, netMap.getCell(2,3)) + "|" + drawSpaces(1, netMap.getCell(3,3)) + drawWorker(netMap.getCell(3,3)) + drawSpaces(1, netMap.getCell(3,3)) + "|" + drawSpaces(1, netMap.getCell(4,3)) + drawWorker(netMap.getCell(4,3)) + drawSpaces(1, netMap.getCell(4,3)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t3\t" + "|" + drawSpaces(1, netMap.getCell(0,3)) + drawDome(netMap.getCell(0,3)) + drawSpaces(1, netMap.getCell(0,3)) + "|" + drawSpaces(1, netMap.getCell(1,3)) + drawDome(netMap.getCell(1,3)) + drawSpaces(1, netMap.getCell(1,3)) + "|" + drawSpaces(1, netMap.getCell(2,3)) + drawDome(netMap.getCell(2,3)) + drawSpaces(1, netMap.getCell(2,3)) + "|" + drawSpaces(1, netMap.getCell(3,3)) + drawDome(netMap.getCell(3,3)) + drawSpaces(1, netMap.getCell(3,3)) + "|" + drawSpaces(1, netMap.getCell(4,3)) + drawDome(netMap.getCell(4,3)) + drawSpaces(1, netMap.getCell(4,3)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(2, netMap.getCell(0,3)) + drawBuilding(netMap.getCell(0,3)) + drawSpaces(2, netMap.getCell(0,3)) + "|" + drawSpaces(2, netMap.getCell(1,3)) + drawBuilding(netMap.getCell(1,3)) + drawSpaces(2, netMap.getCell(1,3)) + "|" + drawSpaces(2, netMap.getCell(2,3)) + drawBuilding(netMap.getCell(2,3)) + drawSpaces(2, netMap.getCell(2,3)) + "|" + drawSpaces(2, netMap.getCell(3,3)) + drawBuilding(netMap.getCell(3,3)) + drawSpaces(2, netMap.getCell(3,3)) + "|" + drawSpaces(2, netMap.getCell(4,3)) + drawBuilding(netMap.getCell(4,3)) + drawSpaces(2, netMap.getCell(4,3)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t-\t" + "+-------------+-------------+-------------+-------------+-------------+");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(1, netMap.getCell(0,4)) + drawWorker(netMap.getCell(0,4)) + drawSpaces(1, netMap.getCell(0,4)) + "|" + drawSpaces(1, netMap.getCell(1,4)) + drawWorker(netMap.getCell(1,4)) + drawSpaces(1, netMap.getCell(1,4)) + "|" + drawSpaces(1, netMap.getCell(2,4)) + drawWorker(netMap.getCell(2,4)) + drawSpaces(1, netMap.getCell(2,4)) + "|" + drawSpaces(1, netMap.getCell(3,4)) + drawWorker(netMap.getCell(3,4)) + drawSpaces(1, netMap.getCell(3,4)) + "|" + drawSpaces(1, netMap.getCell(4,4)) + drawWorker(netMap.getCell(4,4)) + drawSpaces(1, netMap.getCell(4,4)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t4\t" + "|" + drawSpaces(1, netMap.getCell(0,4)) + drawDome(netMap.getCell(0,4)) + drawSpaces(1, netMap.getCell(0,4)) + "|" + drawSpaces(1, netMap.getCell(1,4)) + drawDome(netMap.getCell(1,4)) + drawSpaces(1, netMap.getCell(1,4)) + "|" + drawSpaces(1, netMap.getCell(2,4)) + drawDome(netMap.getCell(2,4)) + drawSpaces(1, netMap.getCell(2,4)) + "|" + drawSpaces(1, netMap.getCell(3,4)) + drawDome(netMap.getCell(3,4)) + drawSpaces(1, netMap.getCell(3,4)) + "|" + drawSpaces(1, netMap.getCell(4,4)) + drawDome(netMap.getCell(4,4)) + drawSpaces(1, netMap.getCell(4,4)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(2, netMap.getCell(0,4)) + drawBuilding(netMap.getCell(0,4)) + drawSpaces(2, netMap.getCell(0,4)) + "|" + drawSpaces(2, netMap.getCell(1,4)) + drawBuilding(netMap.getCell(1,4)) + drawSpaces(2, netMap.getCell(1,4)) + "|" + drawSpaces(2, netMap.getCell(2,4)) + drawBuilding(netMap.getCell(2,4)) + drawSpaces(2, netMap.getCell(2,4)) + "|" + drawSpaces(2, netMap.getCell(3,4)) + drawBuilding(netMap.getCell(3,4)) + drawSpaces(2, netMap.getCell(3,4)) + "|" + drawSpaces(2, netMap.getCell(4,4)) + drawBuilding(netMap.getCell(4,4)) + drawSpaces(2, netMap.getCell(4,4)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "+-------------+-------------+-------------+-------------+-------------+");

		writeAllInfo();

		drawPoss = false;
		//System.out.println('\u0905');
	}
	public String drawSpaces(int type, NetCell netC){
		if(drawPoss){
			if(phase.getGamePhase() == GamePhase.MOVE){
				if(netMoves != null){
					for(NetMove netM : netMoves){	//TODO: ciclare anche sulle move other
						NetMove x = netM;
						while(x != null) {
							if(x.cellX == netMap.getX(netC) && x.cellY == netMap.getY(netC)){
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
							x = x.other;
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
							NetBuild x = netB;
							while(x != null) {
								if(x.cellX == netMap.getX(netC) && x.cellY == netMap.getY(netC)){
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
								x = x.other;
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
	public String drawWorker(NetCell netC){
		if(netC.worker != null){
			if(playerColors.get(netC.worker.owner).equals(new Color("red"))) {
				return Constants.FG_RED + "W" + Constants.RESET;
			} else if(playerColors.get(netC.worker.owner).equals(new Color("green"))) {
				return Constants.FG_GREEN + "W" + Constants.RESET;
			} else if(playerColors.get(netC.worker.owner).equals(new Color("blue"))) {
				return Constants.FG_BLUE + "W" + Constants.RESET;
			} else {
				return "W";
			}
		}
		return " ";
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
					System.out.print("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + "Il ");
					if(myWorker.workerID == myWorker.owner.hashCode() + 1) {
						System.out.print("worker1");
					} else if(myWorker.workerID == myWorker.owner.hashCode() + 2) {
						System.out.print("worker2");
					} else {
						System.out.print(myWorker.workerID);
					}
					System.out.print(" di " + myWorker.owner + " è in posizione:  x = " + x + "; y = " + y + ".");
				}
			}
			System.out.print("\n");
		}
	}
}