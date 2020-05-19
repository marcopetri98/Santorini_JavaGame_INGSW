package it.polimi.ingsw.network.objects;

import it.polimi.ingsw.network.game.*;

public class NetGaming extends NetObject {
	public final NetMove move;
	public final NetBuild build;
	public final String player;
	public final NetMap gameMap;
	public final NetAvailablePositions availablePositions;
	public final NetAvailableBuildings availableBuildings;

	public NetGaming(String msg) throws NullPointerException {
		super(msg);
		move = null;
		build = null;
		player = null;
		gameMap = null;
		availablePositions = null;
		availableBuildings = null;
	}
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
