package it.polimi.ingsw.network.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a class used to exchange messages between clients and server when the players are inside a lobby.
 */
public class NetLobbyPreparation extends NetObject {
	public final String player;
	public final int order;
	public final NetLobbyPreparation next;

	/**
	 * Creates a standard message and only calls the super constructor.
	 * @param msg is the message to be sent
	 * @throws NullPointerException is {@code msg} is null
	 */
	public NetLobbyPreparation(String msg) throws NullPointerException {
		super(msg);
		player = null;
		order = 0;
		next = null;
	}

	/**
	 * It creates a lobby message with a player's name and its order on the lobby.
	 * @param msg is the message to be sent
	 * @param player is the player's name
	 * @param order is the player's order in the lobby
	 * @throws NullPointerException if {@code msg} is null or if {@code player} is null
	 */
	public NetLobbyPreparation(String msg, String player, int order) throws NullPointerException {
		super(msg);
		if (player == null) {
			throw new NullPointerException();
		}
		this.player = player;
		this.order = order;
		this.next = null;
	}

	/**
	 * It creates a lobby message with a player's name and its order on the lobby concatenating this player information with the given information in {@code next} field.
	 * @param msg is the message to be sent
	 * @param player is the player's name
	 * @param order is the player's order in the lobby
	 * @param next is the next lobby message to connect
	 * @throws NullPointerException if {@code msg} is null or if {@code player} is null
	 */
	public NetLobbyPreparation(String msg, String player, int order, NetLobbyPreparation next) throws NullPointerException {
		super(msg);
		if (player == null) {
			throw new NullPointerException();
		}
		this.player = player;
		this.order = order;
		this.next = next;
	}

	/**
	 * It creates a lobby message with the information of the first player inside the lobby and concatenates this player information with the given information in {@code next} field.
	 * @param msg is the message to be sent
	 * @param player is the player's name
	 * @param next is the next lobby message to connect
	 * @throws NullPointerException if {@code msg} is null or if {@code player} is null
	 */
	public NetLobbyPreparation(String msg, String player, NetLobbyPreparation next) throws NullPointerException {
		super(msg);
		if (player == null) {
			throw new NullPointerException();
		}
		this.player = player;
		this.order = 0;
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
	 * Gets the parameter {@link #order}.
	 * @return value of {@link #order}
	 */
	public int getOrder() {
		return order;
	}
	/**
	 * Gets the parameter {@link #next}.
	 * @return value of {@link #next}
	 */
	public NetLobbyPreparation getNext() {
		return next;
	}
	/**
	 * It builds a list of all players inside this dynamic list iterating it.
	 * @return a list of all the players
	 */
	public List<String> getPlayersList() {
		List<String> list = new ArrayList<>();
		if (player != null) {
			list.add(player);
			if (next != null) {
				list.addAll(next.getPlayersList());
			}
		}
		return list;
	}
}
