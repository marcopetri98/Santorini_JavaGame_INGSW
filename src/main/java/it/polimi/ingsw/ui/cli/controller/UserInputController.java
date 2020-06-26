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

/**
 * This class is used by the Cli to send the input that the client insert to the server, it receives a command and translate it in a network message to pass to the {@link it.polimi.ingsw.network.ClientMessageListener} to send to the server.
 */
public class UserInputController {
	private CliGame gameView;
	private ClientMessageListener listener;
	private NetMap gameMap;
	private String playerName;

	/**
	 * It creates a user input controller with the given listener ad argument.
	 * @param listener a {@link it.polimi.ingsw.network.ClientMessageListener}
	 */
	public UserInputController(ClientMessageListener listener) {
		this.listener = listener;
	}

	/**
	 * Sets the current map.
	 * @param map a {@link it.polimi.ingsw.network.game.NetMap}
	 */
	public void setMap(NetMap map) {
		gameMap = map;
	}
	/**
	 * Sets the name of the player.
	 * @param name the player's name
	 */
	public void setPlayerName(String name) {
		playerName = name;
	}
	/**
	 * Sets the game view where to get commands.
	 * @param view a {@link it.polimi.ingsw.ui.cli.view.CliGame}
	 */
	public void setGameView(CliGame view) {
		gameView = view;
	}

	/**
	 * This method builds a network message from the command given by the users and uses the listener to send it invoking its methods, it is called only from lobby to the next phases of the game and it consider the command given correct because controlled by the caller.
	 * @param command the command inserted by the client
	 * @param turn the current phase of the game
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
			// TODO: delete because of refactoring
			case Constants.COMMAND_PASS -> {
				if (turn.getPhase() != Phase.PLAYERTURN || turn.getGamePhase() != GamePhase.BEFOREMOVE) {
					throw new IllegalStateException();
				} else {
					listener.sendMessage(new NetGaming(Constants.PLAYER_IN_PASS,playerName));
				}
			}
		}
	}
	/**
	 * This method builds a network message of move from the given command.
	 * @param command the command inserted by the client
	 * @param turn the current phase of the game
	 * @param move the {@link it.polimi.ingsw.network.game.NetMove} that the player wants to perform
	 * @throws IllegalStateException if the phase is wrong of if it isn't the player's turn
	 * @throws IllegalArgumentException if the command is wrong or if the parameter is not well formed
	 */
	public void getCommand(Command command, Turn turn, NetMove move) throws IllegalStateException, IllegalArgumentException {
		if (!command.commandType.equals(Constants.COMMAND_MOVE)) {
			throw new IllegalArgumentException();
		} else if (turn.getPhase() != Phase.PLAYERTURN || (turn.getPhase() == Phase.PLAYERTURN && turn.getGamePhase() == GamePhase.BUILD)) {
			throw new IllegalStateException();
		} else if (command.getNumParameters() != 3 || move == null) {
			throw new IllegalArgumentException();
		} else if (!Constants.isNumber(command.getParameter(1)) || !Constants.isNumber(command.getParameter(2))) {
			throw new IllegalArgumentException();
		} else {
			listener.sendMessage(new NetGaming(Constants.PLAYER_IN_MOVE,playerName,move));
		}
	}
	/**
	 * This method builds a network message of build from the given command.
	 * @param command the command inserted by the client
	 * @param turn the current phase of the game
	 * @param build the {@link it.polimi.ingsw.network.game.NetBuild} that the player wants to perform
	 * @throws IllegalStateException if the phase is wrong of if it isn't the player's turn
	 * @throws IllegalArgumentException if the command is wrong or if the parameter is null
	 */
	public void getCommand(Command command, Turn turn, NetBuild build) throws IllegalStateException, IllegalArgumentException {
		if (!command.commandType.equals(Constants.COMMAND_BUILD)) {
			throw new IllegalArgumentException();
		} else if (turn.getPhase() != Phase.PLAYERTURN || (turn.getPhase() == Phase.PLAYERTURN && turn.getGamePhase() == GamePhase.MOVE)) {
			throw new IllegalStateException();
		} else if (build == null) {
			throw new IllegalArgumentException();
		} else {
			listener.sendMessage(new NetGaming(Constants.PLAYER_IN_BUILD,playerName,build));
		}
	}
	/**
	 * It sends to the server the wanted dimension for the lobby because the player is the first player of the game.
	 * @param num an integer representing the number of players of the lobby
	 * @throws IllegalArgumentException if {@code num} isn't 2 or 3
	 */
	public void getCommand(int num) throws IllegalArgumentException {
		if (num != 2 && num != 3) {
			throw new IllegalArgumentException();
		}
		listener.sendMessage(new NetSetup(Constants.SETUP_IN_SETUPNUM,playerName,num));
	}
	/**
	 * It disconnects the user.
	 */
	public void disconnect() {
		listener.resetListening();
		listener.sendMessage(new NetSetup(Constants.GENERAL_DISCONNECT));
	}
	/**
	 * It tries to connect to the game server with the specified nickname.
	 * @param playerName the player's name
	 * @param serverAddress the server's address
	 * @throws IllegalArgumentException is {@code playerName} or {@code serverAddress} is null
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
	/**
	 * It sends another message to the server trying to gain this other name because the previous wasn't possible to use.
	 * @param name new player's name
	 * @throws IllegalArgumentException if {@code name} is null
	 * @throws IllegalStateException if the message listener thinks the player doesn't want to play
	 */
	public void tryAnotherName(String name) throws IllegalArgumentException, IllegalStateException {
		if (playerName == null) {
			throw new IllegalArgumentException();
		} else if (!listener.getWantsToPlay()) {
			throw new IllegalStateException();
		}
		gameView.setPlayerName(name);
		this.playerName = name;
		listener.sendMessage(new NetSetup(Constants.SETUP_IN_PARTICIPATE,playerName));
	}
}
