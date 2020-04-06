package it.polimi.ingsw.network.objects;

// necessary imports of Java SE
import java.awt.Color;
import java.io.Serializable;

public class NetColorPreparation implements Serializable {
	private String message;
	private String player;
	private Color color;
	private NetColorPreparation next;

	public NetColorPreparation(String s) {
		message = s;
	}
	public NetColorPreparation(String msg, String player, Color c) {
		this.message = msg;
		this.player = player;
		this.color = c;
		this.next = null;
	}
	public NetColorPreparation(String msg, String player, Color c, NetColorPreparation next) {
		this.message = msg;
		this.player = player;
		this.color = c;
		this.next = next;
	}

	public String getMessage() {
		return message;
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
}
