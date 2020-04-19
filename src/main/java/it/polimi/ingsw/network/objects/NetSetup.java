package it.polimi.ingsw.network.objects;

public class NetSetup extends NetObject {
	public final String player;
	public final int number;

	public NetSetup(String msg)throws NullPointerException {
		super(msg);
		player = null;
		number = 0;
	}
	public NetSetup(String msg, String name) throws NullPointerException  {
		super(msg);
		if (name == null) {
			throw new NullPointerException();
		}
		player = name;
		number = 0;
	}
	public NetSetup(String msg, int number) throws NullPointerException {
		super(msg);
		player = null;
		this.number = number;
	}

	public String getPlayer() {
		return player;
	}
	public int getNumber() {
		return number;
	}
}
