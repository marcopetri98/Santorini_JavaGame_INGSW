package it.polimi.ingsw.network.objects;

// necessary imports from other packages of the project

// necessary imports of Java SE


public class NetLobbyPreparation extends NetObject {
	private String player;
	private int order;
	private NetLobbyPreparation next;

	public NetLobbyPreparation(String msg) {
		super(msg);
	}
	public NetLobbyPreparation(String msg, String player, int order) {
		super(msg);
		this.player = player;
		this.order = order;
		this.next = null;
	}
	public NetLobbyPreparation(String msg, String player, int order, NetLobbyPreparation next) {
		super(msg);
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
