package it.polimi.ingsw.network.objects;

import it.polimi.ingsw.network.game.NetMap;

public class NetOtherTurn extends NetObject {
	public final String player;
	public final NetMap gameMap;

	public NetOtherTurn(String msg) throws NullPointerException {
		super(msg);
		player = null;
		gameMap = null;
	}
	public NetOtherTurn(String msg, String player) throws NullPointerException {
		super(msg);
		if (player == null) {
			throw new NullPointerException();
		}

		this.player = player;
		gameMap = null;
	}
	public NetOtherTurn(String msg, NetMap map) throws NullPointerException {
		super(msg);
		if (map == null) {
			throw new NullPointerException();
		}

		player = null;
		gameMap = map;
	}
}
