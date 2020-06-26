package it.polimi.ingsw.network.objects;

import it.polimi.ingsw.util.Color;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is a class used to exchange messages between clients and server in the color phase of the game when players have to choose a color. More information about game phase can be found on {@link it.polimi.ingsw.core.state} package.
 */
public class NetColorPreparation extends NetObject {
	public final String player;
	public final Color color;
	public final NetColorPreparation next;

	/**
	 * Creates a standard message and only calls the super constructor.
	 * @param msg is the message to be sent
	 * @throws NullPointerException if {@code msg} is null
	 */
	public NetColorPreparation(String msg) throws NullPointerException {
		super(msg);
		player = null;
		color = null;
		next = null;
	}
	/**
	 * Creates a message with a message and a player's name.
	 * @param msg is the message to be sent
	 * @param player is the player's name
	 * @throws NullPointerException if {@code msg} is null
	 */
	public NetColorPreparation(String msg, String player) throws NullPointerException {
		super(msg);
		this.player = player;
		color = null;
		next = null;
	}
	/**
	 * Creates a message sent from the client to the server indicating the player's color choice.
	 * @param msg is the message to be sent
	 * @param player is the player's name
	 * @param c is the color chosen by the player
	 * @throws NullPointerException if {@code msg} is null or {@code player} is null or {@code c} is null
	 */
	public NetColorPreparation(String msg, String player, Color c) throws NullPointerException {
		super(msg);
		if (player == null || c == null) {
			throw new NullPointerException();
		}
		this.player = player;
		this.color = c;
		this.next = null;
	}
	/**
	 * Creates a message from the server to the client with a player's color choice concatenating to other {@code NetColorPreparation} objects.
	 * @param msg is the message to be sent
	 * @param player is the player's name
	 * @param c is the color chosen by the player
	 * @param next is the next element to concatenated to the dynamic list
	 * @throws NullPointerException if {@code msg} is null or {@code player} is null or {@code c} is null
	 */
	public NetColorPreparation(String msg, String player, Color c, NetColorPreparation next) throws NullPointerException {
		super(msg);
		if (player == null || c == null) {
			throw new NullPointerException();
		}
		this.player = player;
		this.color = c;
		this.next = next;
	}

	/**
	 * Gets the parameter {@link #player}.
	 * @return value of {@link #player}
	 */
	public String getPlayer() {
		return player;
	}
	/**
	 * Gets the parameter {@link #color}.
	 * @return value of {@link #color}
	 */
	public Color getColor() {
		return color;
	}
	/**
	 * Gets the parameter {@link #next}.
	 * @return value of {@link #next}
	 */
	public NetColorPreparation getNext() {
		return next;
	}
	/**
	 * Builds a map with the association between the players (keys) and the colors they have chosen (values) inside this structure.
	 * @return a map of colors players' choice
	 */
	public Map<String,Color> getPlayerColorsMap() {
		Map<String,Color> list = new LinkedHashMap<>();
		if (player != null && color != null) {
			list.put(player,color);
			if (next != null) {
				list.putAll(next.getPlayerColorsMap());
			}
		}
		return list;
	}
}
