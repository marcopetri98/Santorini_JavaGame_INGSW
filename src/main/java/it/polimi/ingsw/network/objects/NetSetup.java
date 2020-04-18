package it.polimi.ingsw.network.objects;

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
