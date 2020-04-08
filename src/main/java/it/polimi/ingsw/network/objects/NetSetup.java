package it.polimi.ingsw.network.objects;

// necessary imports from other packages of the project
import it.polimi.ingsw.util.Constants;

// necessary imports of Java SE
import java.io.Serializable;

public class NetSetup extends NetObject {
	private String player;
	private int number;

	public NetSetup(String msg) {
		super(msg);
	}
	public NetSetup(String msg, String name) {
		super(msg);
		player = name;
	}
	public NetSetup(String msg, int number) {
		super(msg);
		this.number = number;
	}

	public String getPlayer() {
		return player;
	}
	public int getNumber() {
		return number;
	}
}
