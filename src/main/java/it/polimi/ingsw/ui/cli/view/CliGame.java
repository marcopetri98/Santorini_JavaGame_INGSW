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
	private int activeWorkerID;
	// attributes used for functioning
	private boolean functioning;
	private final Object inputLock;
	private boolean godsGoAlreadyCalled = false;
	private boolean alreadyPrintedPlay = false;
	private boolean drawPoss = false;
	private boolean sentCorrectMessage = false;
	private boolean chooseStarter = false;
	private boolean printedGuide = false;
	private boolean cellToFill = false;
	private boolean hasBuiltBefore = false;	//for prometheus

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
			printedGuide = true;
			return false;
		}

		if(Constants.GODS_GOD_NAMES.contains(command.commandType.toUpperCase())) {
			printGodGuide(command.commandType.toUpperCase());
			printedGuide = true;
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
						case BEFOREMOVE, BUILD -> {

							switch(chosenGods.get(player)) {

								case Constants.PROMETHEUS :
									if(phase.getGamePhase() == GamePhase.BEFOREMOVE) {
										if (command.commandType.equals(Constants.COMMAND_BUILD) && command.getNumParameters() == 4 && (command.getParameter(0).equals("worker1") || command.getParameter(0).equals("worker2")) && (command.getParameter(1).equals("dome") || command.getParameter(1).equals("building"))) {
											try {
												if (0 <= Integer.parseInt(command.getParameter(2)) && Integer.parseInt(command.getParameter(2)) <= 4 && 0 <= Integer.parseInt(command.getParameter(3)) && Integer.parseInt(command.getParameter(3)) <= 4) {
													int x1 = Integer.parseInt(command.getParameter(2));
													int y1 = Integer.parseInt(command.getParameter(3));

													if(command.getParameter(0).equals("worker1")) {
														activeWorkerID = player.hashCode() + 1;
													} else {
														activeWorkerID = player.hashCode() + 2;
													}

													if(command.getParameter(1).equals("dome")){
														selectedBuild = new NetBuild(activeWorkerID, x1, y1, netMap.getCell(x1,y1).building.level, true);
													} else if(command.getParameter(1).equals("building")) {
														selectedBuild = new NetBuild(activeWorkerID, x1, y1, netMap.getCell(x1,y1).building.level, false);
													}
													if(netBuilds.contains(selectedBuild)) {
														hasBuiltBefore = true;
														return true;
													}
												}
											} catch (NumberFormatException nfe) {
												System.out.println("You didn't insert a correct number");
												return false;
											}
										} else if (command.commandType.equals(Constants.COMMAND_MOVE) && command.getNumParameters() == 3 && (command.getParameter(0).equals("worker1") || command.getParameter(0).equals("worker2"))) {
											try {
												if (0 <= Integer.parseInt(command.getParameter(1)) && Integer.parseInt(command.getParameter(1)) <= 4 && 0 <= Integer.parseInt(command.getParameter(2)) && Integer.parseInt(command.getParameter(2)) <= 4) {
													if(command.getParameter(0).equals("worker1")){
														selectedMove = new NetMove(player.hashCode()+1, Integer.parseInt(command.getParameter(1)), Integer.parseInt(command.getParameter(2)));
													} else if (command.getParameter(0).equals("worker2")){
														selectedMove = new NetMove(player.hashCode()+2, Integer.parseInt(command.getParameter(1)), Integer.parseInt(command.getParameter(2)));
													}
													if(netMoves.contains(selectedMove)) {
														activeWorkerID = selectedMove.workerID;
														return true;
													}
												}
											} catch (NumberFormatException nfe) {
												System.out.println("You didn't insert a correct number");
												return false;
											}
										}
										return false;
									} else if(phase.getGamePhase() == GamePhase.BUILD) {
										if (command.commandType.equals(Constants.COMMAND_BUILD) && command.getNumParameters() == 3 && (command.getParameter(0).equals("dome") || command.getParameter(0).equals("building"))) {
											try {
												if (0 <= Integer.parseInt(command.getParameter(1)) && Integer.parseInt(command.getParameter(1)) <= 4 && 0 <= Integer.parseInt(command.getParameter(2)) && Integer.parseInt(command.getParameter(2)) <= 4) {
													int x1 = Integer.parseInt(command.getParameter(1));
													int y1 = Integer.parseInt(command.getParameter(2));
													if(command.getParameter(0).equals("dome")){
														selectedBuild = new NetBuild(activeWorkerID, x1, y1, netMap.getCell(x1,y1).building.level, true);
													} else if(command.getParameter(0).equals("building")) {
														selectedBuild = new NetBuild(activeWorkerID, x1, y1, netMap.getCell(x1,y1).building.level, false);
													}
													if(netBuilds.contains(selectedBuild)) {
														return true;
													}
												}
											} catch (NumberFormatException nfe) {
												System.out.println("You didn't insert a correct number");
												return false;
											}
										}
										return false;
									}

								case Constants.DEMETER :	//only syntax: build dome/building x_coord y_coord (dome/building x_coord y_coord)
									if (command.commandType.equals(Constants.COMMAND_BUILD)) {
										if (command.getNumParameters() == 6 && (command.getParameter(0).equals("dome") || command.getParameter(0).equals("building")) && (command.getParameter(3).equals("dome") || command.getParameter(3).equals("building"))) {
											try {
												if (0 <= Integer.parseInt(command.getParameter(1)) && Integer.parseInt(command.getParameter(1)) <= 4 && 0 <= Integer.parseInt(command.getParameter(2)) && Integer.parseInt(command.getParameter(2)) <= 4) {
													if(0 <= Integer.parseInt(command.getParameter(4)) && Integer.parseInt(command.getParameter(4)) <= 4 && 0 <= Integer.parseInt(command.getParameter(5)) && Integer.parseInt(command.getParameter(5)) <= 4) {
														int x1 = Integer.parseInt(command.getParameter(1));
														int y1 = Integer.parseInt(command.getParameter(2));
														int x2 = Integer.parseInt(command.getParameter(4));
														int y2 = Integer.parseInt(command.getParameter(5));

														if(command.getParameter(0).equals("dome")){
															if(command.getParameter(3).equals("dome")) {
																selectedBuild = new NetBuild(activeWorkerID, x2, y2, netMap.getCell(x2,y2).building.level, true);	//second Build
															} else {
																selectedBuild = new NetBuild(activeWorkerID, x2, y2, netMap.getCell(x2,y2).building.level, false);	//second Build
															}
															selectedBuild = new NetBuild(activeWorkerID, x1, y1, netMap.getCell(x1,y1).building.level, true, selectedBuild);	//first Build
														} else if(command.getParameter(0).equals("building")) {
															if(command.getParameter(3).equals("dome")) {
																selectedBuild = new NetBuild(activeWorkerID, x2, y2, netMap.getCell(x2,y2).building.level, true);	//second Build
															} else {
																selectedBuild = new NetBuild(activeWorkerID, x2, y2, netMap.getCell(x2,y2).building.level, false);	//second Build
															}
															selectedBuild = new NetBuild(activeWorkerID, x1, y1, netMap.getCell(x1,y1).building.level, false, selectedBuild);	//first Build
														}
														if(netBuilds.contains(selectedBuild)) {
															return true;
														}
													}
												}
											} catch (NumberFormatException nfe) {
												System.out.println("You didn't insert a correct number");
												return false;
											}
										} else if (command.getNumParameters() == 3 && (command.getParameter(0).equals("dome") || command.getParameter(0).equals("building"))) {
											try {
												if (0 <= Integer.parseInt(command.getParameter(1)) && Integer.parseInt(command.getParameter(1)) <= 4 && 0 <= Integer.parseInt(command.getParameter(2)) && Integer.parseInt(command.getParameter(2)) <= 4) {
													int x1 = Integer.parseInt(command.getParameter(1));
													int y1 = Integer.parseInt(command.getParameter(2));
													if(command.getParameter(0).equals("dome")){
														selectedBuild = new NetBuild(activeWorkerID, x1, y1, netMap.getCell(x1,y1).building.level, true);
													} else if(command.getParameter(0).equals("building")) {
														selectedBuild = new NetBuild(activeWorkerID, x1, y1, netMap.getCell(x1,y1).building.level, false);
													}
													if(netBuilds.contains(selectedBuild)) {
														return true;
													}
												}
											} catch (NumberFormatException nfe) {
												System.out.println("You didn't insert a correct number");
												return false;
											}
										}
									}
									return false;

								case Constants.HEPHAESTUS :	//only syntax: build dome/building x_coord y_coord (double)
									if(command.commandType.equals(Constants.COMMAND_BUILD)) {
										if (command.getNumParameters() == 4 && (command.getParameter(0).equals("dome") || command.getParameter(0).equals("building")) && (command.getParameter(3).equals("double"))) {
											try {
												if (0 <= Integer.parseInt(command.getParameter(1)) && Integer.parseInt(command.getParameter(1)) <= 4 && 0 <= Integer.parseInt(command.getParameter(2)) && Integer.parseInt(command.getParameter(2)) <= 4) {
													int x1 = Integer.parseInt(command.getParameter(1));
													int y1 = Integer.parseInt(command.getParameter(2));

													if(command.getParameter(0).equals("dome")){
														selectedBuild = new NetBuild(activeWorkerID, x1, y1, netMap.getCell(x1,y1).building.level + 1, true);	//second Build
													} else if(command.getParameter(0).equals("building")) {
														selectedBuild = new NetBuild(activeWorkerID, x1, y1, netMap.getCell(x1,y1).building.level + 1, false);	//second Build
													}
													selectedBuild = new NetBuild(activeWorkerID, x1, y1, netMap.getCell(x1,y1).building.level, false, selectedBuild);	//first Build

													if(netBuilds.contains(selectedBuild)) {
														return true;
													}
												}
											} catch (NumberFormatException nfe) {
												System.out.println("You didn't insert a correct number");
												return false;
											}
										} else if (command.getNumParameters() == 3 && (command.getParameter(0).equals("dome") || command.getParameter(0).equals("building"))) {
											try {
												if (0 <= Integer.parseInt(command.getParameter(1)) && Integer.parseInt(command.getParameter(1)) <= 4 && 0 <= Integer.parseInt(command.getParameter(2)) && Integer.parseInt(command.getParameter(2)) <= 4) {
													int x1 = Integer.parseInt(command.getParameter(1));
													int y1 = Integer.parseInt(command.getParameter(2));
													if(command.getParameter(0).equals("dome")){
														selectedBuild = new NetBuild(activeWorkerID, x1, y1, netMap.getCell(x1,y1).building.level, true);
													} else if(command.getParameter(0).equals("building")) {
														selectedBuild = new NetBuild(activeWorkerID, x1, y1, netMap.getCell(x1,y1).building.level, false);
													}
													if(netBuilds.contains(selectedBuild)) {
														return true;
													}
												}
											} catch (NumberFormatException nfe) {
												System.out.println("You didn't insert a correct number");
												return false;
											}
										}
									}
									return false;

								default :	//only syntax: build dome/building x_coord y_coord   (previous: build workerX dome/building level x_coord y_coord)
									if (command.commandType.equals(Constants.COMMAND_BUILD) && command.getNumParameters() == 3 && (command.getParameter(0).equals("dome") || command.getParameter(0).equals("building"))) {
										try {
											if (0 <= Integer.parseInt(command.getParameter(1)) && Integer.parseInt(command.getParameter(1)) <= 4 && 0 <= Integer.parseInt(command.getParameter(2)) && Integer.parseInt(command.getParameter(2)) <= 4) {
												int x1 = Integer.parseInt(command.getParameter(1));
												int y1 = Integer.parseInt(command.getParameter(2));
												if(command.getParameter(0).equals("dome")){
													selectedBuild = new NetBuild(activeWorkerID, x1, y1, netMap.getCell(x1,y1).building.level, true);
												} else if(command.getParameter(0).equals("building")) {
													selectedBuild = new NetBuild(activeWorkerID, x1, y1, netMap.getCell(x1,y1).building.level, false);
												}
												if(netBuilds.contains(selectedBuild)) {
													return true;
												}
											}
										} catch (NumberFormatException nfe) {
											System.out.println("You didn't insert a correct number");
											return false;
										}
									}
									return false;
							}
						}

						//only syntax: move workerX x_coord y_coord
						case MOVE -> {
							if (command.commandType.equals(Constants.COMMAND_MOVE)) {

								switch(chosenGods.get(player)) {

									case Constants.APOLLO, Constants.MINOTAUR :
										if (command.getNumParameters() == 3 && (command.getParameter(0).equals("worker1") || command.getParameter(0).equals("worker2"))) {
											try {
												if (0 <= Integer.parseInt(command.getParameter(1)) && Integer.parseInt(command.getParameter(1)) <= 4 && 0 <= Integer.parseInt(command.getParameter(2)) && Integer.parseInt(command.getParameter(2)) <= 4) {
													if(command.getParameter(0).equals("worker1")){
														selectedMove = new NetMove(player.hashCode()+1, Integer.parseInt(command.getParameter(1)), Integer.parseInt(command.getParameter(2)));
													} else if (command.getParameter(0).equals("worker2")){
														selectedMove = new NetMove(player.hashCode()+2, Integer.parseInt(command.getParameter(1)), Integer.parseInt(command.getParameter(2)));
													}
													for(NetMove netM : netMoves) {
														if(netM.isLike(selectedMove)){
															selectedMove = netM;
															activeWorkerID = netM.workerID;
															return true;
														}
													}
												}
											} catch (NumberFormatException nfe) {
												System.out.println("You didn't insert a correct number");
												return false;
											}
										}
										return false;

									default :
										if (command.getNumParameters() == 3 && (command.getParameter(0).equals("worker1") || command.getParameter(0).equals("worker2"))) {
											try {
												if (0 <= Integer.parseInt(command.getParameter(1)) && Integer.parseInt(command.getParameter(1)) <= 4 && 0 <= Integer.parseInt(command.getParameter(2)) && Integer.parseInt(command.getParameter(2)) <= 4) {
													if(!(chosenGods.get(player).equals(Constants.PROMETHEUS) && hasBuiltBefore)) {	//used for prometheus
														if(command.getParameter(0).equals("worker1")){
															activeWorkerID = player.hashCode() + 1;
														} else if (command.getParameter(0).equals("worker2")){
															activeWorkerID = player.hashCode() + 2;
														}
													}
													hasBuiltBefore = false;
													selectedMove = new NetMove(activeWorkerID, Integer.parseInt(command.getParameter(1)), Integer.parseInt(command.getParameter(2)));
													if(netMoves.contains(selectedMove)) {
														activeWorkerID = selectedMove.workerID;
														return true;
													}
												}
											} catch (NumberFormatException nfe) {
												System.out.println("You didn't insert a correct number");
												return false;
											}
										}
										return false;

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
			if(functioning == false)	break;
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
				if(player.equals(ndc.player)){
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

					switch(phase.getGodsPhase()) {
						case CHALLENGER_CHOICE :
							System.out.println("The challenger "+activePlayer+" is choosing the gods for the game");
							break;

						case GODS_CHOICE :
							System.out.println("The other player "+activePlayer+" is choosing his god");
							break;

						case STARTER_CHOICE :
							System.out.println("The challenger "+activePlayer+" is now choosing the starter player");
							break;
					}

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
									System.out.println("Now you have to first build a building and then move. Or you can just move. Use the syntax \"build workerX building/dome x_coord y_coord\" or \"move workerX x_coord y_coord\"");
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
							System.out.println("Now you have to build a building or a dome near the worker. Use the syntax \"build building/dome x_coord y_coord\"");
							System.out.print("Now build: ");    //check the build is correct in parsesyntax
						}
					}
				} else {
					if(phase.getGamePhase() == GamePhase.BEFOREMOVE) {	//if I'm in beforemove I show the message only if someone has prometheus
						if(chosenGods.values().contains(Constants.PROMETHEUS)) {
							System.out.println("You can only disconnect, it's "+ng.player+"'s turn.");
						}
					} else if(phase.getGamePhase() == GamePhase.MOVE) {	//if I'm in move and the activePlayer has prometheus, I don't show it
						if(chosenGods.get(activePlayer).equals(Constants.PROMETHEUS)) {
							System.out.println("You can only disconnect, it's "+ng.player+"'s turn.");
						}
					} else {
						System.out.println("You can only disconnect, it's "+ng.player+"'s turn.");
					}
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
					if (ng.player != null && ng.player.equals(p) && !player.equals(ng.player)) {
						System.out.println(ng.player + " just won the game!");
						break;
					}
				}
				if(player.equals(ng.player)){
					printVictory();
				}
				functioning = false;
				break;

			case Constants.GENERAL_DEFEATED:
				ng = (NetGaming) obj;
				for (String p : players) {
					if (ng.player != null && ng.player.equals(p) && !player.equals(ng.player)) {
						System.out.println(ng.player + " just lost");
						if(players.size() == 2) {
							printVictory();
						}
						break;
					}
				}
				if(player.equals(ng.player)){
					System.out.println("You lost the game:(");
				}
				if(players.size() == 3) {
					playerColors.remove(ng.player);
					gods.remove(chosenGods.get(ng.player));
					chosenGods.remove(ng.player);
					players.remove(ng.player);
				}

				//functioning = false;
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
				//System.out.println("\n"+Constants.BG_CYAN+phase.getPhase()+" "+phase.getGodsPhase()+" "+phase.getGamePhase()+Constants.RESET+"\n");	//Only for debug purposes
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

	private void printVictory() {
		System.out.println("``   ```.\"^riyyv*}y\\`      -    `:,.  `_:~^T>`=:_-_:\".'       `^Ywlx}cr^=^xxiv^:::,-~xYREOgPq9#@#QZd0#@@@@@@@@@@@@#B$RgQDQ$dEIT;:~*?x*\"'~  `!            ':~!,:,,:=-`*!`                             .`           \n" +
				"```     `_~^*\\uVw3I*`       `          `   `<v,_::_``        `!xT}xL?l|u3OdGV*~^rv)*|(vZ$QbE8######BBBQQ$8gQ##@@#Q0d5M0QQ8EMDWyx>'``-!==*  ,=                      -!`               `              ``            \n" +
				"```       '_=ruIIVY*_``\"__'              ```:rv>-`       .___:^)*!:!rTxuTLx)^*rTWT^-^|vv}r_rk8QQg$8OIx^\"`.!^u5gQQQQ8G*''<xVmPhcwVcv^,.     `*`                    `-`                  \"_``     `   ``            \n" +
				"`        `::!^rr*:-'`   ``:-         `-_,,,,-'``..-:~. `:;^^rvx*!`  '}cccccVVuuuyycVcuuuuulVwTxr~:.           `_=*v}kIyccccuuuuucVVVVzVcuuuuy'              `_-`                      ,,  ``   `.  `       `````` \n" +
				"``.-`   .,_``-::``         ``  `  `_^)r~!,'   -VB#gr.  '!**rvL}r:`                                                                              `            `=xucr`             `` `-`    `   ``     ````````````\n" +
				"__=,'`      .~}^`            `  ```-_,:\"'   -w###I_     `*Vzeeu*:`                                                                              `-:~rvxr:`      }6QQV`          `` `.`     ``''``   ````````````  \n" +
				"r=::::.    =dBQr`              ``:^,-.`    *#@@#~                              ```````````````````````      ```````````````  ``                                 `y#@@8:     `'-___,_-'``````',^:'````````````     \n" +
				"L*:..,\". `)Q@#R~``._,`        `-,--!`     =@@@#^                                ```````` `````````````````````'```````  ` `                                       u#@@#~  `_:=;^**<=:_.````'=oEP;`                \n" +
				"~_'` _^*iZ#@#6~`` '-`:~=`    -~='   `    _B@@#c                                                                                                                    8#@@B.`_!<*rrr*<!\"_.``\"?TYvL::=\".              \n" +
				"    `!*l#@@@$!   `_!~=-_~!_`_>!.         z#BQ#.         -*xvv).     `*vvr, `*vv*`    `^xiii}xr  *vvvvvvvvvvx\"    :(iLYYx;`   .*vvvvvi)_  .^xvvv:    .\\xvr.         }#$QQT`-,::!::,_-'`-^x?!)}i**=_`:!!:-````    ``\n" +
				"` `!!--}@@@#Y`       `=^\"`~x*`          `M3vP0           `MQB8=     `ZQD'   ^QQr   `zBBs)=~vK8  y*==*QQZ==~)v  )R#P*=^i6#B]`  ~QQx!~VQ#M-  xQB8z    vQQY`          =#})VP`  ````````,)v:\")Tv,`  `!<=:-.-__\"-```.=<\n" +
				"v~_-' `a@@@Q=`        '\"ik}:-_'        `*gDX$I            :Q##Q`    3##?    \\##x  .6##~      *  `   :##0    ` m##x     `O##z  |##|   P##T   x##@z` (##I            `0MIdgr        .<r\"!?}^:-``````.-:!:_.''``-\"!~:\n" +
				"^_-`  !Q@@#M!`     .!(VzGmur-  `    `,;^d@@BBc             >B88U`  }QQT     v88L  lQ83              :B8O     -B8B       =B8Q, \\$gu:=]0QR_    xQ88Gc8Qv              6Q#@@M      `-.`,vVxYu}vrr<.``````'.-_,\":\"_---\n" +
				")!_  `m@@@Di_   `.\"=xzci]}x!.       -~;=Q@@##T              r^~)r _i<|      ^>;^  *~;?              -v=v     -)<L`      _T=v' ^!:*^^r}:       !r~>^*,               M#@@@B    ._.``<cycY(>!!-'v~\"_---__\"\":::,_...-\n" +
				"0h*'`*B@@#E]` `!!~r}56O3\\<_        `__.!UcT*sQ*              :GMM)IM(       :WM:   xM3?`    `;-     `UqY      ~WMV\"    `uGV`  :MM: ,qMv        -KMm_              `cB}xhQM  !~:..~xYx)?xvr>>irrr*<;>><<<^;=!:,--,\"\n" +
				"QRwr*b#@@#M:`,^}YkGdd5}=          \"rv(\\uI}v` =0E,             V5MM5M.       xPGx   `TZZGwYYwdd-     !dHZ`      ^KOdy}YVZOy-   xMMx  }OZj-      :OqOr             ^DP! _]! -=-`-!i}vrvx?^~===??)rrr)rrr*^^==!::\":~^\n" +
				"QBwUm#@@@B^_^(}a$BQEk(-      ```.!)xuy}}oTv*` `vQV`           `eixI:       `^^^*`    `!^)r)>\"      `~^^r:  `     \"^rr(*=`    `^^^*`  ~^**`     >}xVx            !z*  '.  =^`.,=rv^<^***)\\)rr)??|\\()r*^<=!::!!:!~\\x\n" +
				"MT!=K#@@@k<x}WgBQ#8y~-       `!~vT}Tcu}r^~!=:,r,*De`           \"*;~                             ``       ``````                               `*~~~r            ~:,!__-'_!._:~****^vv?iT}xx|)rrrr)vxv|*>===!!!=^}y\n" +
				"x-.\"GQ@@a*T3Q8Eza0Mx,`      `-*u3PKKuv<:__,._=iDqMI-                                         ```````` ````````````                                            `iKKMvrr^!!;^*)vx}T}xyVcIUwuYv)r)viTVyc}x*<=====~rys\n" +
				"x!,cg@@R}M0$8jmB#Rd|.````'..-~vjMqWHVv^!\"__\"!~*V#$O>                                       ``` ````````````````````   `                                     ``}ZD8}YYLv|]YTcyzIeKIcoaIMZMHmzyVwImmehy}v^~!!=~<^\\Kd\n" +
				"(<mB@QdEBB0quKR8Q$3]=:::!!=^?}cmPmIj}(^!::::;)r)9BDP:                                            ```````````````                                             =MOBXVVc}}lcVVywkjky}iiyy5ZMM5H3sUIzkVuYx)*>==~^*vc$B\n" +
				"ZQ#89$$OZmV}rvTuY}uLr)|x}uVo56R6dZHUu}x)r^<!:<|)LQmwc       `_;xVXI33zkkT}x))^>:\".`                      ``                    '-:=>*(x}ukIh3Mmyu}Lx^\".``   `IIGEXXzVucuuTT}Y}TYY}T}]useKaamXylYxvrrrr)vvvxLTckGB#\n" +
				"#$6sbbyjlk3Guuyw}xxxVXmPWmsZ9R6OdMKhy}x*~^)?^^}ycPIVV`'<xumMEDMjq666OMyL**||L}VzPqdg$RMKViv^!-                      -=<rxuXPZ$Q06PokTTvvr**^^!:!=!*x}ixv*!_`^dMZM5GsIokcxvv?|\\vxxxvxTkXseXkVuYxv|)r?x}uyzIsKmmmM##\n" +
				"Q3ke3wPd6$D06MRIYYVHOMOOMmKdOOOdM3IkV}x?vxv)*<umMbqIPM8QQQQ8g$DmuTcOR6OdUx:`       .zBB8KDggQ#Q$qw(!`        -^Lkdg88E96h}WQBV:      ```'.-_\"\"\"\"!!rvxL}TuywyXGMZMMWmyVT}L]xv|vvv(r\\}ucVyyyVVcTTTTuVyIKHMdO6OdZMR#@\n" +
				"D33VXRBBQdV)xIGmwzaGMMZqhzmZdbMMMHUojXjXhsmezx^r3E$$Z3R8QQg$0DEERRKrxPqqMbMIY.   `v\\~VVBBk~~mDd60QB##Es^`:xPB@@##gOWZ}_'x$BoG*k}~\",:-..--_,,-`-!^r)?\\vxxYTuyzIUUjir?lVu}xx\\)rr*<<r\\xLTVkzkkkkXhUsm3GZdOR0$$$$0Eg@@\n" +
				"gIz3ZZMMwivvL]uwVIqMORDDdMMddZZZddddZZMddbZdOO5V*rk8QDqO0DOMMd699R96h*e6OOZG3P):-!*rvZ=e0#8}Q#Q98B0RDB#@@@@@@####BgQB$LD@8Go6jXVKd]. `-,:\":\",_:!=~;;>^*r|vxYlcT*~ruVuu}Lx?r*;=!;*vYuykIma3G5MMZMZZZddORD00$gg$Dg@@\n" +
				"gd6WPoTYThkyTcmmM9RE000Eg$EddddOED6dZM5Md6REEE09eIVyjUhwu}LYxr*?|iTedM;L5M3UzlwcmV!:rRdDRR#88$MPWdQQ89O@@@@#@##B8Ddqg8B#8Dg8$E6QE_  `-,::_,_,-..```'.-,:!rYVux*)}}}}}L]vr^~!=^)x}uVkIKHMZddZZM5WGGGHGWMMMMZZZbM9#@");
		System.out.println("\n\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  You won! Good job!");
	}

	private void printGuide() {
		System.out.println(Constants.FG_RED + "\n\n\n\t\t\t\t\t\t\t\t\t\t\t\t\t**************************** ~ Welcome to Santorini's guide! ~ ****************************" + Constants.RESET);
		System.out.println("Here you'll be told how to play, and what commands to use to do it properly.\n");
		System.out.println("The game is divided into several stages, the first of which is to determine the colors you want to use. To do this, the command is \"color colorName\", where colorName can be a color of choice between red, green, and blue.");
		System.out.println("In the second phase, the choice of divinities takes place: firstly, the \"challenger\" will choose two or three deities, depending on the number of players, thanks to the command \"gods godName1 godName2 godName3\".\nClearly, in a game with two players, godName3 will not be inserted.\n");
		System.out.println("Next, each player chooses the god he wants to use from those chosen by the challenger. The command to use to do this, when required by the game, is \"god godName\". Remember: you can only choose one of the gods chosen by the challenger!");
		System.out.println("A player will now be asked to choose who will start the game: the choice can fall on any player in the game, even themselves! Beware though: not necessarily starting first is an advantage.");
		System.out.println("The command to choose who should start the game is \"starter playerName\"\n");
		System.out.println("The next step is the setup of the game: you have to place the workers! The command is \"position worker1 x_coord y_coord worker2 x_coord y_coord\". Instead of x_coord and y_coord put the coordinates to which you want to place your worker.");
		System.out.println("Warning: You can't place a worker where another worker already has another worker, even if it's another player! Pay attention to syntax, it's very important.\n");
		System.out.println("And now, the phase of the game begins! At each turn, you are forced to move a worker and build, after defeat! To move, the command is \"move workerX x_coord y_coord\": replace workerX as the worker you want to move.");
		System.out.println("To build, the standard command is \"build dome/building x_coord y_coord\": choose whether to build a dome or building, then enter the coordinates. Remember: you can only build with the worker you moved!");
		System.out.println("Also remember that you can only build a dome if there is already a level 3 building, unless your god has a particular power!\n");
		System.out.println("If you want to learn the special commands for your god, or his/her power, write the name whenever you want. Otherwise write the right command for the gamephase and continue playing! See you soon!:)");
		System.out.println(Constants.FG_RED + "\t\t\t\t\t\t\t\t\t\t\t\t\t*******************************************************************************************\n\n" + Constants.RESET);
		System.out.println("\n\n");
	}

	private void printGodGuide(String godName) {
		System.out.println(Constants.FG_RED + "\n\n\n\t\t\t\t\t\t\t\t\t\t\t\t\t************************* ~ Welcome to the Gods info paragraph! ~ *************************" + Constants.RESET);
		System.out.print("You selected ");
		switch(godName) {
			case Constants.APOLLO :
				System.out.println(Constants.FG_RED + "Apollo\n" + Constants.RESET);
				System.out.println("Here is the power:\n");
				System.out.println("Phase\t\t\t->\tYour Move");
				System.out.println("Power\t\t\t->\tYour Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated");
				System.out.println("Move command\t->\t\"move workerX x_coord y_coord\"");
				System.out.println("Build command\t->\t\"build building/dome x_coord y_coord\"");
				System.out.println(Constants.FG_RED + "\t\t\t\t\t\t\t\t\t\t\t\t\t*******************************************************************************************\n\n" + Constants.RESET);
				break;

			case Constants.ARTEMIS :
				System.out.println(Constants.FG_RED + "Artemis\n" + Constants.RESET);
				System.out.println("Here is the power:\n");
				System.out.println("Phase\t\t\t->\tYour Move");
				System.out.println("Power\t\t\t->\tYour Worker may move one additional time, but not back to its initial space");
				System.out.println("Move command\t->\t\"move workerX x_coord y_coord\"");
				System.out.println("Build command\t->\t\"build building/dome x_coord y_coord\"");
				System.out.println(Constants.FG_RED + "\t\t\t\t\t\t\t\t\t\t\t\t\t*******************************************************************************************\n\n" + Constants.RESET);
				break;

			case Constants.ATHENA :
				System.out.println(Constants.FG_RED + "Athena\n" + Constants.RESET);
				System.out.println("Here is the power:\n");
				System.out.println("Phase\t\t\t->\tOpponent’s Turn");
				System.out.println("Power\t\t\t->\tIf one of your Workers moved up on your last turn, opponent Workers cannot move up this turn");
				System.out.println("Move command\t->\t\"move workerX x_coord y_coord\"");
				System.out.println("Build command\t->\t\"build building/dome x_coord y_coord\"");
				System.out.println(Constants.FG_RED + "\t\t\t\t\t\t\t\t\t\t\t\t\t*******************************************************************************************\n\n" + Constants.RESET);
				break;

			case Constants.ATLAS :
				System.out.println(Constants.FG_RED + "Atlas\n" + Constants.RESET);
				System.out.println("Here is the power:\n");
				System.out.println("Phase\t\t\t->\tYour Build");
				System.out.println("Power\t\t\t->\tYour Worker may build a dome at any level");
				System.out.println("Move command\t->\t\"move workerX x_coord y_coord\"");
				System.out.println("Build command\t->\t\"build building/dome x_coord y_coord\"");
				System.out.println(Constants.FG_RED + "\t\t\t\t\t\t\t\t\t\t\t\t\t*******************************************************************************************\n\n" + Constants.RESET);
				break;

			case Constants.DEMETER :
				System.out.println(Constants.FG_RED + "Demeter\n" + Constants.RESET);
				System.out.println("Here is the power:\n");
				System.out.println("Phase\t\t\t->\tYour Build");
				System.out.println("Power\t\t\t->\tYour Worker may build one additional time, but not on the same space");
				System.out.println("Move command\t->\t\"move workerX x_coord y_coord\"");
				System.out.println("Build command\t->\t\"build building/dome x_coord y_coord (building/dome x_coord y_coord)\"");
				System.out.println(Constants.FG_RED + "\t\t\t\t\t\t\t\t\t\t\t\t\t*******************************************************************************************\n\n" + Constants.RESET);
				break;

			case Constants.HEPHAESTUS :
				System.out.println(Constants.FG_RED + "Hephaestus\n" + Constants.RESET);
				System.out.println("Here is the power:\n");
				System.out.println("Phase\t\t\t->\tYour Build");
				System.out.println("Power\t\t\t->\tYour Worker may build one additional block (not dome) on top of your first block");
				System.out.println("Move command\t->\t\"move workerX x_coord y_coord\"");
				System.out.println("Build command\t->\t\"build building/dome x_coord y_coord (double)\"");
				System.out.println("Additional notes: only write double if you want to build two buildings in the same spot!");
				System.out.println(Constants.FG_RED + "\t\t\t\t\t\t\t\t\t\t\t\t\t*******************************************************************************************\n\n" + Constants.RESET);
				break;

			case Constants.MINOTAUR :
				System.out.println(Constants.FG_RED + "Minotaur\n" + Constants.RESET);
				System.out.println("Here is the power:\n");
				System.out.println("Phase\t\t\t->\tYour Move");
				System.out.println("Power\t\t\t->\tYour Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight backwards to an unoccupied space at any level");
				System.out.println("Move command\t->\t\"move workerX x_coord y_coord\"");
				System.out.println("Build command\t->\t\"build building/dome x_coord y_coord\"");
				System.out.println(Constants.FG_RED + "\t\t\t\t\t\t\t\t\t\t\t\t\t*******************************************************************************************\n\n" + Constants.RESET);
				break;

			case Constants.PAN :
				System.out.println(Constants.FG_RED + "Pan\n" + Constants.RESET);
				System.out.println("Here is the power:\n");
				System.out.println("Phase\t\t\t->\tWin Condition");
				System.out.println("Power\t\t\t->\tYou also win if your Worker moves down two or more levels");
				System.out.println("Move command\t->\t\"move workerX x_coord y_coord\"");
				System.out.println("Build command\t->\t\"build building/dome x_coord y_coord\"");
				System.out.println(Constants.FG_RED + "\t\t\t\t\t\t\t\t\t\t\t\t\t*******************************************************************************************\n\n" + Constants.RESET);
				break;

			case Constants.PROMETHEUS :
				System.out.println(Constants.FG_RED + "Prometheus\n" + Constants.RESET);
				System.out.println("Here is the power:\n");
				System.out.println("Phase\t\t\t->\tYour Turn");
				System.out.println("Power\t\t\t->\tIf your Worker does not move up, it may build both before and after moving");
				System.out.println("Move command\t->\t\"move workerX x_coord y_coord\"");
				System.out.println("Build command\t->\t\"build building/dome x_coord y_coord\"");
				System.out.println(Constants.FG_RED + "\t\t\t\t\t\t\t\t\t\t\t\t\t*******************************************************************************************\n\n" + Constants.RESET);
				break;
		}
	}

	private void printError() {
		if(printedGuide) {
			printedGuide = false;
		} else {
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
				System.out.println("You can only disconnect or visualize the guide during turn of other players.");
			}
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
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(1, netMap.getCell(0,0)) + drawDome(netMap.getCell(0,0)) + drawSpaces(1, netMap.getCell(0,0)) + "|" + drawSpaces(1, netMap.getCell(1,0)) + drawDome(netMap.getCell(1,0)) + drawSpaces(1, netMap.getCell(1,0)) + "|" + drawSpaces(1, netMap.getCell(2,0)) + drawDome(netMap.getCell(2,0)) + drawSpaces(1, netMap.getCell(2,0)) + "|" + drawSpaces(1, netMap.getCell(3,0)) + drawDome(netMap.getCell(3,0)) + drawSpaces(1, netMap.getCell(3,0)) + "|" + drawSpaces(1, netMap.getCell(4,0)) + drawDome(netMap.getCell(4,0)) + drawSpaces(1, netMap.getCell(4,0)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t0\t" + "|" + drawSpaces(1, netMap.getCell(0,0)) + drawWorker(netMap.getCell(0,0)) + drawSpaces(1, netMap.getCell(0,0)) + "|" + drawSpaces(1, netMap.getCell(1,0)) + drawWorker(netMap.getCell(1,0)) + drawSpaces(1, netMap.getCell(1,0)) + "|" + drawSpaces(1, netMap.getCell(2,0)) + drawWorker(netMap.getCell(2,0)) + drawSpaces(1, netMap.getCell(2,0)) + "|" + drawSpaces(1, netMap.getCell(3,0)) + drawWorker(netMap.getCell(3,0)) + drawSpaces(1, netMap.getCell(3,0)) + "|" + drawSpaces(1, netMap.getCell(4,0)) + drawWorker(netMap.getCell(4,0)) + drawSpaces(1, netMap.getCell(4,0)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(2, netMap.getCell(0,0)) + drawBuilding(netMap.getCell(0,0)) + drawSpaces(2, netMap.getCell(0,0)) + "|" + drawSpaces(2, netMap.getCell(1,0)) + drawBuilding(netMap.getCell(1,0)) + drawSpaces(2, netMap.getCell(1,0)) + "|" + drawSpaces(2, netMap.getCell(2,0)) + drawBuilding(netMap.getCell(2,0)) + drawSpaces(2, netMap.getCell(2,0)) + "|" + drawSpaces(2, netMap.getCell(3,0)) + drawBuilding(netMap.getCell(3,0)) + drawSpaces(2, netMap.getCell(3,0)) + "|" + drawSpaces(2, netMap.getCell(4,0)) + drawBuilding(netMap.getCell(4,0)) + drawSpaces(2, netMap.getCell(4,0)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t-\t" + "+-------------+-------------+-------------+-------------+-------------+");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(1, netMap.getCell(0,1)) + drawDome(netMap.getCell(0,1)) + drawSpaces(1, netMap.getCell(0,1)) + "|" + drawSpaces(1, netMap.getCell(1,1)) + drawDome(netMap.getCell(1,1)) + drawSpaces(1, netMap.getCell(1,1)) + "|" + drawSpaces(1, netMap.getCell(2,1)) + drawDome(netMap.getCell(2,1)) + drawSpaces(1, netMap.getCell(2,1)) + "|" + drawSpaces(1, netMap.getCell(3,1)) + drawDome(netMap.getCell(3,1)) + drawSpaces(1, netMap.getCell(3,1)) + "|" + drawSpaces(1, netMap.getCell(4,1)) + drawDome(netMap.getCell(4,1)) + drawSpaces(1, netMap.getCell(4,1)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t1\t" + "|" + drawSpaces(1, netMap.getCell(0,1)) + drawWorker(netMap.getCell(0,1)) + drawSpaces(1, netMap.getCell(0,1)) + "|" + drawSpaces(1, netMap.getCell(1,1)) + drawWorker(netMap.getCell(1,1)) + drawSpaces(1, netMap.getCell(1,1)) + "|" + drawSpaces(1, netMap.getCell(2,1)) + drawWorker(netMap.getCell(2,1)) + drawSpaces(1, netMap.getCell(2,1)) + "|" + drawSpaces(1, netMap.getCell(3,1)) + drawWorker(netMap.getCell(3,1)) + drawSpaces(1, netMap.getCell(3,1)) + "|" + drawSpaces(1, netMap.getCell(4,1)) + drawWorker(netMap.getCell(4,1)) + drawSpaces(1, netMap.getCell(4,1)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(2, netMap.getCell(0,1)) + drawBuilding(netMap.getCell(0,1)) + drawSpaces(2, netMap.getCell(0,1)) + "|" + drawSpaces(2, netMap.getCell(1,1)) + drawBuilding(netMap.getCell(1,1)) + drawSpaces(2, netMap.getCell(1,1)) + "|" + drawSpaces(2, netMap.getCell(2,1)) + drawBuilding(netMap.getCell(2,1)) + drawSpaces(2, netMap.getCell(2,1)) + "|" + drawSpaces(2, netMap.getCell(3,1)) + drawBuilding(netMap.getCell(3,1)) + drawSpaces(2, netMap.getCell(3,1)) + "|" + drawSpaces(2, netMap.getCell(4,1)) + drawBuilding(netMap.getCell(4,1)) + drawSpaces(2, netMap.getCell(4,1)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t-\t" + "+-------------+-------------+-------------+-------------+-------------+");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(1, netMap.getCell(0,2)) + drawDome(netMap.getCell(0,2)) + drawSpaces(1, netMap.getCell(0,2)) + "|" + drawSpaces(1, netMap.getCell(1,2)) + drawDome(netMap.getCell(1,2)) + drawSpaces(1, netMap.getCell(1,2)) + "|" + drawSpaces(1, netMap.getCell(2,2)) + drawDome(netMap.getCell(2,2)) + drawSpaces(1, netMap.getCell(2,2)) + "|" + drawSpaces(1, netMap.getCell(3,2)) + drawDome(netMap.getCell(3,2)) + drawSpaces(1, netMap.getCell(3,2)) + "|" + drawSpaces(1, netMap.getCell(4,2)) + drawDome(netMap.getCell(4,2)) + drawSpaces(1, netMap.getCell(4,2)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t2\t" + "|" + drawSpaces(1, netMap.getCell(0,2)) + drawWorker(netMap.getCell(0,2)) + drawSpaces(1, netMap.getCell(0,2)) + "|" + drawSpaces(1, netMap.getCell(1,2)) + drawWorker(netMap.getCell(1,2)) + drawSpaces(1, netMap.getCell(1,2)) + "|" + drawSpaces(1, netMap.getCell(2,2)) + drawWorker(netMap.getCell(2,2)) + drawSpaces(1, netMap.getCell(2,2)) + "|" + drawSpaces(1, netMap.getCell(3,2)) + drawWorker(netMap.getCell(3,2)) + drawSpaces(1, netMap.getCell(3,2)) + "|" + drawSpaces(1, netMap.getCell(4,2)) + drawWorker(netMap.getCell(4,2)) + drawSpaces(1, netMap.getCell(4,2)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(2, netMap.getCell(0,2)) + drawBuilding(netMap.getCell(0,2)) + drawSpaces(2, netMap.getCell(0,2)) + "|" + drawSpaces(2, netMap.getCell(1,2)) + drawBuilding(netMap.getCell(1,2)) + drawSpaces(2, netMap.getCell(1,2)) + "|" + drawSpaces(2, netMap.getCell(2,2)) + drawBuilding(netMap.getCell(2,2)) + drawSpaces(2, netMap.getCell(2,2)) + "|" + drawSpaces(2, netMap.getCell(3,2)) + drawBuilding(netMap.getCell(3,2)) + drawSpaces(2, netMap.getCell(3,2)) + "|" + drawSpaces(2, netMap.getCell(4,2)) + drawBuilding(netMap.getCell(4,2)) + drawSpaces(2, netMap.getCell(4,2)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t-\t" + "+-------------+-------------+-------------+-------------+-------------+");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(1, netMap.getCell(0,3)) + drawDome(netMap.getCell(0,3)) + drawSpaces(1, netMap.getCell(0,3)) + "|" + drawSpaces(1, netMap.getCell(1,3)) + drawDome(netMap.getCell(1,3)) + drawSpaces(1, netMap.getCell(1,3)) + "|" + drawSpaces(1, netMap.getCell(2,3)) + drawDome(netMap.getCell(2,3)) + drawSpaces(1, netMap.getCell(2,3)) + "|" + drawSpaces(1, netMap.getCell(3,3)) + drawDome(netMap.getCell(3,3)) + drawSpaces(1, netMap.getCell(3,3)) + "|" + drawSpaces(1, netMap.getCell(4,3)) + drawDome(netMap.getCell(4,3)) + drawSpaces(1, netMap.getCell(4,3)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t3\t" + "|" + drawSpaces(1, netMap.getCell(0,3)) + drawWorker(netMap.getCell(0,3)) + drawSpaces(1, netMap.getCell(0,3)) + "|" + drawSpaces(1, netMap.getCell(1,3)) + drawWorker(netMap.getCell(1,3)) + drawSpaces(1, netMap.getCell(1,3)) + "|" + drawSpaces(1, netMap.getCell(2,3)) + drawWorker(netMap.getCell(2,3)) + drawSpaces(1, netMap.getCell(2,3)) + "|" + drawSpaces(1, netMap.getCell(3,3)) + drawWorker(netMap.getCell(3,3)) + drawSpaces(1, netMap.getCell(3,3)) + "|" + drawSpaces(1, netMap.getCell(4,3)) + drawWorker(netMap.getCell(4,3)) + drawSpaces(1, netMap.getCell(4,3)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(2, netMap.getCell(0,3)) + drawBuilding(netMap.getCell(0,3)) + drawSpaces(2, netMap.getCell(0,3)) + "|" + drawSpaces(2, netMap.getCell(1,3)) + drawBuilding(netMap.getCell(1,3)) + drawSpaces(2, netMap.getCell(1,3)) + "|" + drawSpaces(2, netMap.getCell(2,3)) + drawBuilding(netMap.getCell(2,3)) + drawSpaces(2, netMap.getCell(2,3)) + "|" + drawSpaces(2, netMap.getCell(3,3)) + drawBuilding(netMap.getCell(3,3)) + drawSpaces(2, netMap.getCell(3,3)) + "|" + drawSpaces(2, netMap.getCell(4,3)) + drawBuilding(netMap.getCell(4,3)) + drawSpaces(2, netMap.getCell(4,3)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t-\t" + "+-------------+-------------+-------------+-------------+-------------+");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t \t" + "|" + drawSpaces(1, netMap.getCell(0,4)) + drawDome(netMap.getCell(0,4)) + drawSpaces(1, netMap.getCell(0,4)) + "|" + drawSpaces(1, netMap.getCell(1,4)) + drawDome(netMap.getCell(1,4)) + drawSpaces(1, netMap.getCell(1,4)) + "|" + drawSpaces(1, netMap.getCell(2,4)) + drawDome(netMap.getCell(2,4)) + drawSpaces(1, netMap.getCell(2,4)) + "|" + drawSpaces(1, netMap.getCell(3,4)) + drawDome(netMap.getCell(3,4)) + drawSpaces(1, netMap.getCell(3,4)) + "|" + drawSpaces(1, netMap.getCell(4,4)) + drawDome(netMap.getCell(4,4)) + drawSpaces(1, netMap.getCell(4,4)) + "|");
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t4\t" + "|" + drawSpaces(1, netMap.getCell(0,4)) + drawWorker(netMap.getCell(0,4)) + drawSpaces(1, netMap.getCell(0,4)) + "|" + drawSpaces(1, netMap.getCell(1,4)) + drawWorker(netMap.getCell(1,4)) + drawSpaces(1, netMap.getCell(1,4)) + "|" + drawSpaces(1, netMap.getCell(2,4)) + drawWorker(netMap.getCell(2,4)) + drawSpaces(1, netMap.getCell(2,4)) + "|" + drawSpaces(1, netMap.getCell(3,4)) + drawWorker(netMap.getCell(3,4)) + drawSpaces(1, netMap.getCell(3,4)) + "|" + drawSpaces(1, netMap.getCell(4,4)) + drawWorker(netMap.getCell(4,4)) + drawSpaces(1, netMap.getCell(4,4)) + "|");
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
					for(NetMove netM : netMoves){
						NetMove x = netM;
						while(x != null) {
							if(x.cellX == netMap.getX(netC) && x.cellY == netMap.getY(netC)){
								if (type == 0) {
									cellToFill = true;
									return "@@@@@@@@@@@@@";
								}
								else if (type == 1) {
									cellToFill = true;
									return "@@@@@@";
								}
								else if (type == 2) {
									cellToFill = true;
									return "@@@@@";
								}
							}
							if(chosenGods.get(player).equals(Constants.APOLLO) || chosenGods.get(player).equals(Constants.MINOTAUR)) {
								x = null;
							} else {
								x = x.other;
							}
						}

					}
				}
				if (type == 0) {
					cellToFill = false;
					return "             ";
				}
				else if (type == 1) {
					cellToFill = false;
					return "      ";
				}
				else if (type == 2) {
					cellToFill = false;
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
										cellToFill = true;
										return "@@@@@@@@@@@@@";
									}
									else if (type == 1) {
										cellToFill = true;
										return "@@@@@@";
									}
									else if (type == 2) {
										cellToFill = true;
										return "@@@@@";
									}
								}
								x = x.other;
							}

						}
					}
				}
				if (type == 0) {
					cellToFill = false;
					return "             ";
				}
				else if (type == 1) {
					cellToFill = false;
					return "      ";
				}
				else if (type == 2) {
					cellToFill = false;
					return "     ";
				}
			}

		}
		else{
			if (type == 0) {
				cellToFill = false;
				return "             ";
			}
			else if (type == 1) {
				cellToFill = false;
				return "      ";
			}
			else if (type == 2) {
				cellToFill = false;
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
		if(drawPoss && cellToFill) {
			return "@";
		} else {
			return " ";
		}
	}
	public char drawDome(NetCell netC){
		if (netC.building.dome) {
			return 'D';
		}
		if(drawPoss && cellToFill) {
			return '@';
		} else {
			return ' ';
		}
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
		if(drawPoss && cellToFill) {
			return "@@@";
		} else {
			return "   ";
		}
	}

	public void writeAllInfo(){
		for (int y = 0; y < 5; y++) {
			for(int x = 0; x < 5; x++){
				NetWorker myWorker = netMap.getCell(x,y).worker;
				if(myWorker != null){
					System.out.print("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + "The ");
					if(myWorker.workerID == myWorker.owner.hashCode() + 1) {
						System.out.print("worker1");
					} else if(myWorker.workerID == myWorker.owner.hashCode() + 2) {
						System.out.print("worker2");
					} else {
						System.out.print(myWorker.workerID);
					}
					System.out.print(" of " + myWorker.owner + " is in position:  x = " + x + "; y = " + y + ".");
					System.out.print("\n");
				}
			}
		}
		System.out.print("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + "**************************************************************\n");
		for(String player : players) {
			System.out.print("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + "The player ");
			System.out.print(player);
			System.out.print(" has the god ");
			System.out.print(chosenGods.get(player) + ".");
			System.out.print("\n");
		}
		System.out.print("\n");
	}
}