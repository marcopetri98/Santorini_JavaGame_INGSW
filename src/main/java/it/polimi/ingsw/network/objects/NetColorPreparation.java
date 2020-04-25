package it.polimi.ingsw.network.objects;

// necessary imports from other packages of the project
import it.polimi.ingsw.util.Constants;

// necessary imports of Java SE
import java.awt.Color;

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
}
