package it.polimi.ingsw.network.objects;

// necessary imports from other packages of the project
import it.polimi.ingsw.util.Constants;

// necessary imports of Java SE
import java.io.Serializable;

public class NetOrderPreparation extends NetObject {
	private String player;
	private int order;
	private NetOrderPreparation next;

	public NetOrderPreparation(String msg, String player, int order) {
		super(msg);
		this.player = player;
		this.order = order;
		this.next = null;
	}
	public NetOrderPreparation(String msg, String player, int order, NetOrderPreparation next) {
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
	public NetOrderPreparation getNext() {
		return next;
	}
}
