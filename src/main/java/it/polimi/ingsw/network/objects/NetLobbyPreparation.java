package it.polimi.ingsw.network.objects;

// necessary imports from other packages of the project

// necessary imports of Java SE


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

	public String getPlayer() {
		return player;
	}
	public int getOrder() {
		return order;
	}
	public NetLobbyPreparation getNext() {
		return next;
	}
}
