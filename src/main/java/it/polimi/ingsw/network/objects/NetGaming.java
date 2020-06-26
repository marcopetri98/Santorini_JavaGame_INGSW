package it.polimi.ingsw.network.objects;

import it.polimi.ingsw.network.game.*;

/**
 * This class is a class used to exchange messages between clients and server in the gaming phase of the game. More information about game phase can be found on {@link it.polimi.ingsw.core.state} package.
 */
public class NetGaming extends NetObject {
	public final NetMove move;
	public final NetBuild build;
	public final String player;
	public final NetMap gameMap;
	public final NetAvailablePositions availablePositions;
	public final NetAvailableBuildings availableBuildings;

	/**
	 * Creates a standard message and only calls the super constructor.
	 * @param msg is the message to be sent
	 * @throws NullPointerException if {@code msg} is null
	 */
	public NetGaming(String msg) throws NullPointerException {
		super(msg);
		move = null;
		build = null;
		player = null;
		gameMap = null;
		availablePositions = null;
		availableBuildings = null;
	}
	/**
	 * Creates a message with a name of a certain player.
	 * @param msg is the message to be sent
	 * @param player is the player's name
	 * @throws NullPointerException if {@code msg} is null or if {@code player} is null
	 */
	public NetGaming(String msg, String player) throws NullPointerException {
		super(msg);
		if (player == null) {
			throw new NullPointerException();
		}

		move = null;
		build = null;
		this.player = player;
		gameMap = null;
		availablePositions = null;
		availableBuildings = null;
	}
	/**
	 * Creates a message that is sent from the server to the client with the updated map of the game.
	 * @param msg is the message to be sent
	 * @param map is the network object representing the game map
	 * @throws NullPointerException if {@code msg} is null or if {@code map} is null
	 */
	public NetGaming(String msg, NetMap map) throws NullPointerException {
		super(msg);
		if (map == null) {
			throw new NullPointerException();
		}

		move = null;
		build = null;
		player = null;
		gameMap = map;
		availablePositions = null;
		availableBuildings = null;
	}
	/**
	 * Creates a message from the client to the server with the information about the move that the client wants to perform.
	 * @param msg is the message to be sent
	 * @param player is the player's name
	 * @param move is a move the player wants to perform
	 * @throws NullPointerException if {@code msg} is null or if {@code move} is null
	 */
	public NetGaming(String msg, String player, NetMove move) throws NullPointerException {
		super(msg);
		if (move == null) {
			throw new NullPointerException();
		}
		this.move = move;
		build = null;
		this.player = player;
		gameMap = null;
		availablePositions = null;
		availableBuildings = null;
	}
	/**
	 * Creates a message from the client to the server with the information about the build that the client wants to perform.
	 * @param msg is the message to be sent
	 * @param player is the player's name
	 * @param build is a build the player wants to perform
	 * @throws NullPointerException if {@code msg} is null or if {@code build} is null
	 */
	public NetGaming(String msg, String player, NetBuild build) throws NullPointerException {
		super(msg);
		if (build == null) {
			throw new NullPointerException();
		}
		move = null;
		this.build = build;
		this.player = player;
		gameMap = null;
		availablePositions = null;
		availableBuildings = null;
	}
	/**
	 * Creates a message from the server to the client with the information of possible moves that the client can perform on this round.
	 * @param msg is the message to be sent
	 * @param positions are the possible moves that the active player can do
	 * @throws NullPointerException if {@code msg} is null or if {@code positions} is null
	 */
	public NetGaming(String msg, NetAvailablePositions positions) throws NullPointerException {
		super(msg);
		if (positions == null) {
			throw new NullPointerException();
		}
		move = null;
		build = null;
		player = null;
		gameMap = null;
		availablePositions = positions;
		availableBuildings = null;
	}
	/**
	 * Creates a message from the server to the client with the information of possible moves that the client can perform on this round.
	 * @param msg is the message to be sent
	 * @param buildings are the possible builds that the active player can do
	 * @throws NullPointerException if {@code msg} is null or if {@code buildings} is null
	 */
	public NetGaming(String msg, NetAvailableBuildings buildings) throws NullPointerException {
		super(msg);
		if (buildings == null) {
			throw new NullPointerException();
		}
		move = null;
		build = null;
		player = null;
		gameMap = null;
		availablePositions = null;
		availableBuildings = buildings;
	}
	/**
	 * Creates a message from the server to the client with the information of possible moves and builds that the client can perform on this round.
	 * @param msg is the message to be sent
	 * @param player is the player's name
	 * @param buildings are the possible builds that the active player can do
	 * @param positions are the possible moves that the active player can do
	 * @throws NullPointerException if {@code msg} is null or if {@code buildings} is null
	 */
	public NetGaming(String msg, String player, NetAvailableBuildings buildings, NetAvailablePositions positions) throws NullPointerException {
		super(msg);
		if (buildings == null) {
			throw new NullPointerException();
		}
		move = null;
		build = null;
		this.player = player;
		gameMap = null;
		availablePositions = positions;
		availableBuildings = buildings;
	}
}
