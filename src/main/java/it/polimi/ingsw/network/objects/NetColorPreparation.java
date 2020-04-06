package it.polimi.ingsw.network.objects;

// necessary imports from other packages of the project
import it.polimi.ingsw.util.Constants;

// necessary imports of Java SE
import java.awt.Color;
import java.io.Serializable;

public class NetColorPreparation implements Serializable {
	public static int serialUID = Constants.ACTUAL_VERSION;
	private String message;
	private String player;
	private int additionalInfo;
	private Color color;
	private NetColorPreparation next;

	public NetColorPreparation(String s) {
		message = s;
	}
	public NetColorPreparation(String s, int info) {
		message = s;
		additionalInfo = info;
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
