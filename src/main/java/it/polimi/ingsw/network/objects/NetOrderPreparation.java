package it.polimi.ingsw.network.objects;

import java.io.Serializable;

public class NetOrderPreparation implements Serializable {
	private String message;
	private String player;
	private int order;
	private NetOrderPreparation next;

	public NetOrderPreparation(String msg, String player, int order) {
		this.message = msg;
		this.player = player;
		this.order = order;
		this.next = null;
	}
	public NetOrderPreparation(String msg, String player, int order, NetOrderPreparation next) {
		this.message = msg;
		this.player = player;
		this.order = order;
		this.next = next;
	}

	public String getMessage() {
		return message;
	}
	public String getPlayer() {
		return player;
	}
	public int getOrder() {
		return order;
	}
	public NetOrderPreparation getNext() {
		return next;
	}
}
