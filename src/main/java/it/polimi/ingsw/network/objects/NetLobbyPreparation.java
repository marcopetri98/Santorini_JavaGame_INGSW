package it.polimi.ingsw.network.objects;

// necessary imports from other packages of the project

// necessary imports of Java SE


import java.util.ArrayList;
import java.util.List;

public class NetLobbyPreparation extends NetObject {
	public final String player;
	public final int order;
	public final NetLobbyPreparation next;

	public NetLobbyPreparation(String msg) throws NullPointerException {
		super(msg);
		player = null;
		order = 0;
		next = null;
	}
	public NetLobbyPreparation(String msg, String player, int order) throws NullPointerException {
		super(msg);
		if (player == null) {
			throw new NullPointerException();
		}
		this.player = player;
		this.order = order;
		this.next = null;
	}
	public NetLobbyPreparation(String msg, String player, int order, NetLobbyPreparation next) throws NullPointerException {
		super(msg);
		if (player == null) {
			throw new NullPointerException();
		}
		this.player = player;
		this.order = order;
		this.next = next;
	}
	public NetLobbyPreparation(String msg, String player, NetLobbyPreparation next) throws NullPointerException {
		super(msg);
		if (player == null) {
			throw new NullPointerException();
		}
		this.player = player;
		this.order = 0;
		this.next = next;
	}

	public String getPlayer() {
		return player;
	}
	public int getOrder() {
		return order;
	}
	public NetLobbyPreparation getNext() {
		return next;
	}
	public List<String> getPlayersList() {
		List<String> list = new ArrayList<>();
		if (player != null) {
			list.add(player);
			if (next != null) {
				list.addAll(next.getPlayersList());
			}
		}
		return list;
	}
}
