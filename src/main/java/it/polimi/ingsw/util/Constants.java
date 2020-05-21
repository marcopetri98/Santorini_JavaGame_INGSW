package it.polimi.ingsw.util;

import it.polimi.ingsw.util.Color;
import java.util.*;

/**
 * This is a class used to store programs constants
 */
public final class Constants {
	/**
	 * A Constants class shouldn't be created, it's only a class used to store variables
	 */
	private Constants() {}

	/* Client CLI game constants */

	/* Game constants */
	public static final int MAP_SIDE = 5;

	/* Version constants */
	public static final int ACTUAL_VERSION = 1;

	/* GOD NAMES */
	public static final String APOLLO = "APOLLO";
	public static final String ARTEMIS = "ARTEMIS";
	public static final String ATHENA = "ATHENA";
	public static final String ATLAS = "ATLAS";
	public static final String DEMETER = "DEMETER";
	public static final String HEPHAESTUS = "HEPHAESTUS";
	public static final String MINOTAUR = "MINOTAUR";
	public static final String PAN = "PAN";
	public static final String PROMETHEUS = "PROMETHEUS";

	/* General messages between client and server */
	public static final String GENERAL_ERROR = "error";									// sent to the user to indicate general error about user's message
	public static final String GENERAL_SETUP_DISCONNECT = "setup-disconnect";			// sent to the user to indicate that someone disconnected on setup and game will end
	public static final String GENERAL_FATAL_ERROR = "error-fatal";						// sent to the user to indicate that server has occurred in a fatal error and crashed
	public static final String GENERAL_DISCONNECT = "disconnect";						// sent to the server to indicate that the player wants to disconnect
	public static final String GENERAL_PLAYER_DISCONNECTED = "player-disconnected";		// sent to the user to indicate that a player has disconnected
	public static final String GENERAL_WINNER = "player-winner";						// sent to the user to indicate that there is a winner
	public static final String GENERAL_DEFEATED = "player-defeated";					// sent to the user to indicate that there is a loser
	public static final String GENERAL_GAMEMAP_UPDATE = "gamemap-update";				// sent to the user to indicate that the board situation changed
	public static final String GENERAL_PHASE_UPDATE = "phase-advanced";					// sent to the user to indicate that the game phase has changed
	public static final String GENERAL_NOT_EXIST_SERVER = "not-exist-server";			// an error message used by the client for the connection phase
	/* Setup messages between client and server */
	public static final String SETUP_IN_PARTICIPATE = "setup-participate";				// sent to the server to indicate that the client want to participate to a match
	public static final String SETUP_IN_SETUPNUM = "setup-choosing-num";				// sent to the server to indicate that the client has chosen the number of players
	public static final String SETUP_CREATE = "setup-create";							// sent to the user to indicate that it must choose the number of player
	public static final String SETUP_CREATE_WORKED = "setup-create-worked";				// sent to the user to indicate that the lobby has been created
	public static final String SETUP_ERROR = "setup-error";								// sent to the user to indicate that the message sent was wrong
	public static final String SETUP_OUT_CONNWORKED = "setup-connection-worked";		// sent to the user to indicate that it is inside the lobby
	public static final String SETUP_OUT_CONNFAILED = "setup-connection-failed";		// sent to the user to indicate that he cannot participate because the name is already chosen
	public static final String SETUP_OUT_CONNFINISH = "setup-connection-finished";		// sent to the user to indicate to the user that lobby is finished and game is starting
	/* Lobby messages between client and server */
	public static final String LOBBY_ERROR = "lobby-error";								// sent to the user that the message sent was wrong
	public static final String LOBBY_INFO = "lobby-info";								// sent to the user to indicate information about the lobby (has players names)
	public static final String LOBBY_TURN = "lobby-turn-order";							// sent to the user to indicate information about the lobby (has the order of players)
	/* Color messages between client and server */
	public static final String COLOR_ERROR = "color-error";								// sent to the user to indicate that the message he inserted was wrong
	public static final String COLOR_CHOICES = "color-other-choice";					// sent to the user to indicate other players' color choices
	public static final String COLOR_IN_CHOICE = "color-choice";						// sent to the server to indicate the chosen color by the player
	public static final List<Color> COLOR_COLORS = List.of(new Color(0, 0, 255), new Color(255, 0, 0), new Color(0, 255, 0));
	/* Divinity choice messages between client and server */
	public static final String GODS_ERROR = "gods-error";								// sent to the user to indicate that its message was wrong
	public static final String GODS_CHALLENGER = "gods-challenger";						// sent to the user to indicate that he must choose gods for the current game
	public static final String GODS_GODS = "gods-gods";									// sent to the user to indicate that
	public static final String GODS_STARTER = "gods-starter";							// sent to the user to indicate which player will start placing workers and will start playing
	public static final String GODS_CHOICES = "gods-choices";							// sent to the user to indicate other players' god choices
	public static final String GODS_IN_GAME_GODS = "gods-game-gods";					// sent to the server by the challenger to indicate the gods chosen
	public static final String GODS_IN_CHOICE = "gods-choice";							// sent to the server to indicate the god choice
	public static final String GODS_IN_START_PLAYER = "gods-starter";					// sent to the server by the challenger to indicate the starter chosen
	public static final List<String> GODS_GOD_NAMES = List.of(APOLLO,ARTEMIS,ATHENA,ATLAS,DEMETER,HEPHAESTUS,MINOTAUR,PAN,PROMETHEUS);
	/* Game setup messages between client and server */
	public static final String GAMESETUP_ERROR = "gamesetup-error";						// sent to the user to indicate that its message was wrong
	public static final String GAMESETUP_IN_PLACE = "gamesetup-player-positions";		// sent to the server to indicate the position chosen by the player for its workers
	/* Player's turn messages between client and server */
	public static final String PLAYER_ERROR = "player-error";							// sent to the user to indicate that its message was wrong
	public static final String PLAYER_ACTIONS = "player-actions";						// sent to the user to indicate what he can do in this turn phase
	public static final String PLAYER_IN_PASS = "player-pass";							// sent to the server to indicate that a player with prometheus wants to pass turn in before move phase
	public static final String PLAYER_IN_MOVE = "player-move";							// sent to the server to indicate the move it wants to perform
	public static final String PLAYER_IN_BUILD = "player-build";						// sent to the server to indicate the build it wants to perform
	/* Other player's turn messages between client and server */
	public static final String OTHERS_ERROR = "others-error";							// sent to the user to indicate that the message sent was wrong
	/* General messages about the turn */
	public static final String TURN_PLAYERTURN = "turn-of";								// sent to the user to indicate the current player turn

	/* Commands given from the player to do a specific command */
	public static final String COMMAND_DISCONNECT = "disconnect";
	/* Commands given on color phase */
	public static final String COMMAND_COLOR_CHOICE = "color";
	public static final List<String> COMMAND_COLOR_COLORS = List.of("BLUE","GREEN","RED");
	/* Commands given on gods phase */
	public static final String COMMAND_GODS_CHOICES = "gods";
	public static final String COMMAND_GODS_CHOOSE = "god";
	public static final String COMMAND_GODS_STARTER = "starter";
	/* Commands given on game setup phase */
	public static final String COMMAND_GAMESETUP_POSITION = "position";
	/* Commands given on gaming phase */
	public static final String COMMAND_MOVE = "move";
	public static final String COMMAND_BUILD = "build";
	public static final String COMMAND_BUILD_DOME = "dome";
	public static final String COMMAND_BUILD_BUILDING = "building";
	public static final String COMMAND_PASS = "pass";

	/* General connection messages */
	public static final String CHECK = "ping";

	/* Color scape variables */
	public static final String RESET = "\u001B[0m";
	public static final String FG_RED = "\u001B[31m";
	public static final String FG_GREEN = "\u001B[32m";
	public static final String FG_BLUE = "\u001B[34m";
	public static final String BG_GREEN = "\u001B[42m";
	public static final String BG_CYAN = "\u001B[46m";

	/* Constants used by static methods */
	public static final int MAX_NICKNAME_LEN = 20;
	private static final List<Character> number_chars = List.of('0','1','2','3','4','5','6','7','8','9');

	/* Useful methods */
	public static boolean isNumber(String string) {
		for (int i = 0; i < string.length(); i++) {
			if (!number_chars.contains(string.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
