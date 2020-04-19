package it.polimi.ingsw.util;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

/**
 * This is a class used to store programs constants
 */
public final class Constants {
	/**
	 * A Constants class shouldn't be created, it's only a class used to store variables
	 */
	private Constants() {}

	/* Game constants */
	public static final int MAP_SIDE = 5;

	/* Version constants */
	public static final int ACTUAL_VERSION = 1;

	/* General messages between client and server */
	public static final String GENERAL_ERROR = "error";
	public static final String GENERAL_FATAL_ERROR = "error-fatal";
	public static final String GENERAL_DISCONNECT = "disconnect";
	/* Setup messages between client and server */
	public static final String SETUP_PARTICIPATE = "setup-participate";
	public static final String SETUP_CREATE = "setup-create";
	public static final String SETUP_SETUPNUM = "setup-choosing-num";
	public static final String SETUP_CREATE_WORKED = "setup-create-worked";
	public static final String SETUP_ERROR = "setup-error";
	public static final String SETUP_OUT_CONNWORKED = "setup-connection-worked";
	public static final String SETUP_OUT_CONNFAILED = "setup-connection-failed";
	public static final String SETUP_OUT_CONNFINISH = "setup-connection-finished";
	/* Lobby messages between client and server */
	public static final String LOBBY_ERROR = "lobby-error";
	public static final String LOBBY_TURN = "lobby-info";
	/* Color messages between client and server */
	public static final String COLOR_YOU = "color-go";
	public static final String COLOR_OTHER = "color-wait";
	public static final String COLOR_SUCCESS = "color-success";
	public static final String COLOR_ERROR = "color-error";
	public static final String COLOR_OTHER_CHOICE = "color-other-choice";
	public static final String COLOR_IN_CHOICE = "color-choice";
	public static final List<Color> COLOR_COLORS = List.of(new Color(0, 0, 255), new Color(255, 0, 0), new Color(0, 255, 0));
	/* Divinity choice messages between client and server */
	public static final String GODS_CHALLENGER = "gods-challenger";
	public static final String GODS_CHOOSE_STARTER = "gods-choose-starter";
	public static final String GODS_YOU = "gods-go";
	public static final String GODS_OTHER = "gods-wait";
	public static final String GODS_SUCCESS = "gods-success";
	public static final String GODS_ERROR = "gods-error";
	public static final String GODS_OTHER_CHOICE = "gods-other-choice";
	public static final String GODS_IN_GAME_GODS = "gods-game-gods";
	public static final String GODS_IN_CHOICE = "gods-choice";
	public static final String GODS_IN_START_PLAYER = "gods-starter";
	public static final List<String> GODS_GOD_NAMES = List.of("Apollo","Artemis","Athena","Atlas","Demeter","Hephaestus","Minotaur","Pan","Prometheus");
	/* Game setup messages between client and server */
	public static final String GAMESETUP_PLACE = "gamestup-place";
	public static final String GAMESETUP_SUCCESS = "gamesetup-success";
	public static final String GAMESETUP_ERROR = "gamesetup-error";
	public static final String GAMESETUP_WAIT = "gamesetup-wait";
	public static final String GAMESETUP_OTHER_CHOICE = "gamesetup-other-choice";
	public static final String GAMESETUP_IN_PLACE = "gamesetup-positions";
	/* Player's turn messages between client and server */
	public static final String PLAYER_ERROR = "player-error";
	public static final String PLAYER_IN_MOVE = "player-move";
	public static final String PLAYER_IN_BUILD = "player-build";
	/* Other player's turn messages between client and server */
	public static final String OTHERS_ACTION = "others-action";
	public static final String OTHERS_ERROR = "others-error";

	/* General connection messages */
	public static final String CHECK = "ping";
}
