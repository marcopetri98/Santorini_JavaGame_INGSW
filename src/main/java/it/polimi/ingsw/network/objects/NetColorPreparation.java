package it.polimi.ingsw.network.objects;

// necessary imports from other packages of the project
import it.polimi.ingsw.util.Constants;

// necessary imports of Java SE
import java.awt.Color;

public class NetColorPreparation extends NetObject {
	public static int serialUID = Constants.ACTUAL_VERSION;
	private String player;
	// TODO: is still necessary this variable?
	private int additionalInfo;
	private Color color;
	private NetColorPreparation next;

	public NetColorPreparation(String msg) {
		super(msg);
	}
	public NetColorPreparation(String msg, int info) {
		super(msg);
		additionalInfo = info;
	}
	public NetColorPreparation(String msg, String player, Color c) {
		super(msg);
		this.player = player;
		this.color = c;
		this.next = null;
	}
	public NetColorPreparation(String msg, String player, Color c, NetColorPreparation next) {
		super(msg);
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
}
