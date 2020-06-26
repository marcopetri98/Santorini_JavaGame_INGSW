package it.polimi.ingsw.network.objects;

/**
 * This class is a network class used to exchange messages between clients and server before joining a lobby or during the preparation to join a lobby.
 */
public class NetSetup extends NetObject {
	public final String player;
	public final int number;

	/**
	 * Creates a standard message and only calls the super constructor.
	 * @param msg is the message to be sent
	 * @throws NullPointerException if {@code msg} is null
	 */
	public NetSetup(String msg)throws NullPointerException {
		super(msg);
		player = null;
		number = 0;
	}
	/**
	 * Creates a setup message with a message and a player's name.
	 * @param msg is the message to be sent
	 * @param name is the player's name
	 * @throws NullPointerException if {@code msg} is null or {@code name} is null
	 */
	public NetSetup(String msg, String name) throws NullPointerException  {
		super(msg);
		if (name == null) {
			throw new NullPointerException();
		}
		player = name;
		number = 0;
	}
	/**
	 * Creates a setup message with a message, a player's name and a number.
	 * @param msg is the message to be sent
	 * @param name is the player's name
	 * @param number is a number
	 * @throws NullPointerException if {@code msg} is null
	 */
	public NetSetup(String msg, String name, int number) throws NullPointerException {
		super(msg);
		player = name;
		this.number = number;
	}
	/**
	 * Creates a message with a message and a number.
	 * @param msg is the message to be sent
	 * @param number is the player's name
	 * @throws NullPointerException if {@code msg} is null
	 */
	public NetSetup(String msg, int number) throws NullPointerException {
		super(msg);
		player = null;
		this.number = number;
	}

	/**
	 * Gets the parameter {@link #player}.
	 * @return value of {@link #player}
	 */
	public String getPlayer() {
		return player;
	}
	/**
	 * Gets the parameter {@link #number}.
	 * @return value of {@link #number}
	 */
	public int getNumber() {
		return number;
	}
}
