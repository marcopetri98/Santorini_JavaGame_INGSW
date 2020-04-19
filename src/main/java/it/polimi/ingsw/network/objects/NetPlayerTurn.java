package it.polimi.ingsw.network.objects;

import it.polimi.ingsw.network.game.NetAvailableBuildings;
import it.polimi.ingsw.network.game.NetAvailablePositions;
import it.polimi.ingsw.network.game.NetBuild;
import it.polimi.ingsw.network.game.NetMove;

public class NetPlayerTurn extends NetObject {
	public final NetMove move;
	public final NetBuild build;
	public final NetAvailablePositions availablePositions;
	public final NetAvailableBuildings availableBuildings;

	public NetPlayerTurn(String msg) throws NullPointerException {
		super(msg);
		move = null;
		build = null;
		availablePositions = null;
		availableBuildings = null;
	}
	public NetPlayerTurn(String msg, NetMove move) throws NullPointerException {
		super(msg);
		if (move == null) {
			throw new NullPointerException();
		}
		this.move = move;
		build = null;
		availablePositions = null;
		availableBuildings = null;
	}
	public NetPlayerTurn(String msg, NetBuild build) throws NullPointerException {
		super(msg);
		if (build == null) {
			throw new NullPointerException();
		}
		move = null;
		this.build = build;
		availablePositions = null;
		availableBuildings = null;
	}
	public NetPlayerTurn(String msg, NetAvailablePositions positions) throws NullPointerException {
		super(msg);
		if (positions == null) {
			throw new NullPointerException();
		}
		move = null;
		build = null;
		availablePositions = positions;
		availableBuildings = null;
	}
	public NetPlayerTurn(String msg, NetAvailableBuildings buildings) throws NullPointerException {
		super(msg);
		if (buildings == null) {
			throw new NullPointerException();
		}
		move = null;
		build = null;
		availablePositions = null;
		availableBuildings = buildings;
	}
}
