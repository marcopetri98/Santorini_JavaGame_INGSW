package it.polimi.ingsw.network.objects;

// necessary imports from other packages of the project
import it.polimi.ingsw.util.Constants;

// necessary imports of Java SE
import it.polimi.ingsw.util.Color;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class NetColorPreparation extends NetObject {
	public final String player;
	public final Color color;
	public final NetColorPreparation next;

	public NetColorPreparation(String msg) throws NullPointerException {
		super(msg);
		player = null;
		color = null;
		next = null;
	}
	public NetColorPreparation(String msg, String player) throws NullPointerException {
		super(msg);
		this.player = player;
		color = null;
		next = null;
	}
	public NetColorPreparation(String msg, String player, Color c) throws NullPointerException {
		super(msg);
		if (player == null || c == null) {
			throw new NullPointerException();
		}
		this.player = player;
		this.color = c;
		this.next = null;
	}
	public NetColorPreparation(String msg, String player, Color c, NetColorPreparation next) throws NullPointerException {
		super(msg);
		if (player == null || c == null) {
			throw new NullPointerException();
		}
		this.player = player;
		this.color = c;
		this.next = next;
	}

	public String getPlayer() {
		return player;
	}
	public Color getColor() {
		return color;
	}
	public NetColorPreparation getNext() {
		return next;
	}
	public Map<String,Color> getPlayerColorsMap() {
		Map<String,Color> list = new LinkedHashMap<>();
		if (player != null && color != null) {
			list.put(player,color);
			if (next != null) {
				list.putAll(next.getPlayerColorsMap());
			}
		}
		return list;
	}
}
