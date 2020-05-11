package it.polimi.ingsw.ui.cli.controller;

import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.ClientMessageListener;
import it.polimi.ingsw.network.game.NetBuild;
import it.polimi.ingsw.network.game.NetMap;
import it.polimi.ingsw.network.game.NetMove;
import it.polimi.ingsw.network.objects.*;
import it.polimi.ingsw.ui.cli.view.CliGame;
import it.polimi.ingsw.ui.cli.view.Command;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.Pair;

public class UserInputController {
	private CliGame gameView;
	private ClientMessageListener listener;
	private NetMap gameMap;
	private String playerName;

	public UserInputController(ClientMessageListener listener) {
		this.listener = listener;
	}

	public void setMap(NetMap map) {
		gameMap = map;
	}
	public void setPlayerName(String name) {
		playerName = name;
	}
	public void setGameView(CliGame view) {
		gameView = view;
	}

	/**
	 * This class builds a network message from the command given by the users and uses the listener to send it invoking its methods, it is called only from lobby to the next phases of the game and it consider the command given correct because controlled by the caller.
	 * @param command which will be translated in a massage the server can read
	 * @throws IllegalStateException when it is called with a phase that isn't a message of the phase considered
	 */
	public void getCommand(Command command, Turn turn) throws IllegalStateException, IllegalArgumentException {
		switch (command.commandType) {
			case Constants.COMMAND_DISCONNECT -> {
				switch (turn.getPhase()) {
					case LOBBY -> {
						listener.resetListening();
						listener.sendMessage(new NetLobbyPreparation(Constants.GENERAL_DISCONNECT));
					}
					case COLORS -> {
						listener.resetListening();
						listener.sendMessage(new NetColorPreparation(Constants.GENERAL_DISCONNECT));
					}
					case GODS -> {
						listener.resetListening();
						listener.sendMessage(new NetDivinityChoice(Constants.GENERAL_DISCONNECT));
					}
					case SETUP -> {
						listener.resetListening();
						listener.sendMessage(new NetGameSetup(Constants.GENERAL_DISCONNECT));
					}
					case PLAYERTURN -> {
						listener.resetListening();
						listener.sendMessage(new NetGaming(Constants.GENERAL_DISCONNECT));
					}
					default -> throw new IllegalStateException();
				}
			}
			case Constants.COMMAND_COLOR_CHOICE -> {
				if (turn.getPhase() != Phase.COLORS) {
					throw new IllegalStateException();
				} else if (command.getNumParameters() == 0) {
					throw new IllegalArgumentException();
				} else {
					Color colorChosen = new Color(command.getParameter(0));
					listener.sendMessage(new NetColorPreparation(Constants.COLOR_IN_CHOICE,playerName,colorChosen));
				}
			}
			case Constants.COMMAND_GODS_CHOICES -> {
				if (turn.getPhase() != Phase.GODS || turn.getGodsPhase() != GodsPhase.CHALLENGER_CHOICE) {
					throw new IllegalStateException();
				} else if (command.getNumParameters() != 2 && command.getNumParameters() != 3) {
					throw new IllegalArgumentException();
				} else {
					for (int i = 0; i < command.getNumParameters(); i++) {
						if (!Constants.GODS_GOD_NAMES.contains(command.getParameter(i).toUpperCase())) {
							throw new IllegalArgumentException();
						}
					}
					listener.sendMessage(new NetDivinityChoice(Constants.GODS_IN_GAME_GODS,playerName,command.getParameterList()));
				}
			}
			case Constants.COMMAND_GODS_CHOOSE -> {
				if (turn.getPhase() != Phase.GODS || turn.getGodsPhase() != GodsPhase.GODS_CHOICE) {
					throw new IllegalStateException();
				} else if (command.getNumParameters() == 0) {
					throw new IllegalArgumentException();
				} else {
					if (!Constants.GODS_GOD_NAMES.contains(command.getParameter(0).toUpperCase())) {
						throw new IllegalArgumentException();
					} else {
						listener.sendMessage(new NetDivinityChoice(Constants.GODS_IN_CHOICE,playerName,command.getParameter(0),false));
					}
				}
			}
			case Constants.COMMAND_GODS_STARTER -> {
				if (turn.getPhase() != Phase.GODS || turn.getGodsPhase() != GodsPhase.STARTER_CHOICE) {
					throw new IllegalStateException();
				} else if (command.getNumParameters() == 0) {
					throw new IllegalArgumentException();
				} else {
					if (!gameView.getPlayers().contains(command.getParameter(0))) {
						throw new IllegalArgumentException();
					}
					listener.sendMessage(new NetDivinityChoice(Constants.GODS_IN_START_PLAYER,playerName,command.getParameter(0),true));
				}
			}
			case Constants.COMMAND_GAMESETUP_POSITION -> {
				if (turn.getPhase() != Phase.SETUP) {
					throw new IllegalStateException();
				} else if (command.getNumParameters() != 6) {
					throw new IllegalArgumentException();
				} else if (!Constants.isNumber(command.getParameter(1)) || !Constants.isNumber(command.getParameter(2)) || !Constants.isNumber(command.getParameter(4)) || !Constants.isNumber(command.getParameter(5))) {
					throw new IllegalArgumentException();
				} else if (Integer.parseInt(command.getParameter(1)) < 0 || Integer.parseInt(command.getParameter(1)) > 4 || Integer.parseInt(command.getParameter(2)) < 0  || Integer.parseInt(command.getParameter(2)) > 4 || Integer.parseInt(command.getParameter(4)) < 0 || Integer.parseInt(command.getParameter(4)) > 4 || Integer.parseInt(command.getParameter(5)) < 0  || Integer.parseInt(command.getParameter(5)) > 4) {
					throw new IllegalArgumentException();
				} else {
					listener.sendMessage(new NetGameSetup(Constants.GAMESETUP_IN_PLACE,playerName,new Pair<Integer,Integer>(Integer.parseInt(command.getParameter(1)),Integer.parseInt(command.getParameter(2))),new Pair<Integer,Integer>(Integer.parseInt(command.getParameter(4)),Integer.parseInt(command.getParameter(5)))));
				}
			}
			case Constants.COMMAND_PASS -> {
				if (turn.getPhase() != Phase.PLAYERTURN || turn.getGamePhase() != GamePhase.BEFOREMOVE) {
					throw new IllegalStateException();
				} else {
					listener.sendMessage(new NetGaming(Constants.PLAYER_IN_PASS,playerName));
				}
			}
		}
	}
	public void getCommand(Command command, Turn turn, NetMove move) throws IllegalStateException, IllegalArgumentException {
		if (!command.commandType.equals(Constants.COMMAND_MOVE)) {
			throw new IllegalArgumentException();
		} else if (turn.getPhase() != Phase.PLAYERTURN || turn.getGamePhase() != GamePhase.MOVE) {
			throw new IllegalStateException();
		} else if (command.getNumParameters() != 3 || move == null) {
			throw new IllegalArgumentException();
		} else if (!Constants.isNumber(command.getParameter(1)) || !Constants.isNumber(command.getParameter(2))) {
			throw new IllegalArgumentException();
		} else {
			// TODO: "worker1" should be changed with a constant as well as in the CliGame
			NetMove playerMove = new NetMove(command.getParameter(0).equals("worker1") ? playerName.hashCode()+1 : playerName.hashCode()+2,Integer.parseInt(command.getParameter(1)),Integer.parseInt(command.getParameter(2)));
			listener.sendMessage(new NetGaming(Constants.PLAYER_IN_MOVE,playerName,playerMove));
		}
	}
	public void getCommand(Command command, Turn turn, NetBuild build) throws IllegalStateException, IllegalArgumentException {
		if (!command.commandType.equals(Constants.COMMAND_BUILD)) {
			throw new IllegalArgumentException();
		} else if (turn.getPhase() != Phase.PLAYERTURN || turn.getGamePhase() != GamePhase.BUILD) {
			throw new IllegalStateException();
		} else if (command.getNumParameters() != 5 || build == null) {
			throw new IllegalArgumentException();
		} else if (!Constants.isNumber(command.getParameter(2)) || !Constants.isNumber(command.getParameter(3)) || !Constants.isNumber(command.getParameter(4)) || (!command.getParameter(1).equals(Constants.COMMAND_BUILD_DOME) && !command.getParameter(1).equals(Constants.COMMAND_BUILD_BUILDING))) {
			throw new IllegalArgumentException();
		} else {
			// TODO: "worker1" should be changed with a constant as well as in the CliGame
			NetBuild playerBuild = new NetBuild(command.getParameter(0).equals("worker1") ? playerName.hashCode()+1 : playerName.hashCode()+2,Integer.parseInt(command.getParameter(3)),Integer.parseInt(command.getParameter(4)),Integer.parseInt(command.getParameter(2)), command.getParameter(1).equals(Constants.COMMAND_BUILD_DOME));
			listener.sendMessage(new NetGaming(Constants.PLAYER_IN_BUILD,playerName,playerBuild));
		}
	}
	public void getCommand(int num) throws IllegalArgumentException {
		if (num != 2 && num != 3) {
			throw new IllegalArgumentException();
		}
		listener.sendMessage(new NetSetup(Constants.SETUP_IN_SETUPNUM,playerName,num));
	}
	public void disconnect() {
		listener.resetListening();
		listener.sendMessage(new NetSetup(Constants.GENERAL_DISCONNECT));
	}
	/**
	 *
	 * @param playerName
	 * @param serverAddress
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public void connect(String playerName, String serverAddress) throws IllegalArgumentException {
		if (playerName == null || serverAddress == null) {
			throw new IllegalArgumentException();
		}
		if (listener.connectToServer(serverAddress)) {
			gameView.setPlayerName(playerName);
			this.playerName = playerName;
			listener.setWantsToPlay(true);
			listener.sendMessage(new NetSetup(Constants.SETUP_IN_PARTICIPATE,playerName));
		}
	}
}
