package it.polimi.ingsw.network.objects;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a class used to exchange messages between clients and server in the gods phase of the game where challenger chooses gods and the starter and players choose a god. More information about game phase can be found on {@link it.polimi.ingsw.core.state} package.
 */
public class NetDivinityChoice extends NetObject {
	public final String divinity;
	public final String challenger;
	public final String player;
	public final NetDivinityChoice next;
	public final boolean godsEnd;

	/**
	 * Creates a standard message and only calls the super constructor.
	 * @param msg is the message to be sent
	 * @throws NullPointerException if {@code msg} is null
	 */
	public NetDivinityChoice(String msg) throws NullPointerException {
		super(msg);
		divinity = null;
		challenger = null;
		player = null;
		next = null;
		godsEnd = false;
	}
	/**
	 * Creates a message with a boolean which says that the gods phase ended.
	 * @param msg is the message to be sent
	 * @param godsEnd indicates that the gods phase ended
	 * @throws NullPointerException if {@code msg} is null
	 */
	public NetDivinityChoice(String msg, boolean godsEnd) throws NullPointerException {
		super(msg);
		divinity = null;
		challenger = null;
		player = null;
		next = null;
		this.godsEnd = godsEnd;
	}
	/**
	 * Creates a message with a player's name.
	 * @param msg is the message to be sent
	 * @param value is the player's name
	 * @throws NullPointerException if {@code msg} is null
	 */
	public NetDivinityChoice(String msg, String value) throws NullPointerException {
		super(msg);
		challenger = null;
		player = value;
		next = null;
		divinity = null;
		godsEnd = false;
	}
	/**
	 * Creates a message of a gods choice or the starter choice from the challenger depending on the parameters passed.
	 * @param msg is the message to be sent
	 * @param player is the player's name if the player isn't the challenger while choosing the starter
	 * @param other is the starter to select or the divinity chosen by the player
	 * @param start indicated is the phase is the starter selection phase
	 * @throws NullPointerException if {@code msg} is null or {@code player} is null or {@code other} is null
	 */
	public NetDivinityChoice(String msg, String player, String other, boolean start) throws NullPointerException {
		super(msg);
		if (player == null || other == null) {
			throw new NullPointerException();
		}
		challenger = start ? player : null;
		this.player = start ? other : player;
		next = null;
		divinity = start ? null : other.toUpperCase();
		godsEnd = false;
	}
	/**
	 * Creates a message indicating the choices of gods from the players till now.
	 * @param msg is the message to be sent
	 * @param name is the player's name
	 * @param god is the god's name
	 * @param next is the next message to concatenate
	 * @throws NullPointerException if {@code msg} is null or {@code name} is null
	 */
	public NetDivinityChoice(String msg, String name, String god, NetDivinityChoice next) throws NullPointerException {
		super(msg);
		if (name == null) {
			throw new NullPointerException();
		}
		challenger = null;
		divinity = god.toUpperCase();
		player = name;
		this.next = next;
		godsEnd = false;
	}
	/**
	 * Creates a message from the server to the client with the list of gods chosen by the challenger for this game.
	 * @param msg is the message to be sent
	 * @param divinities is the list of gods
	 * @throws NullPointerException if {@code msg} is null or {@code divinities} is null
	 */
	public NetDivinityChoice(String msg, List<String> divinities) throws NullPointerException {
		super(msg);
		if (divinities == null) {
			throw new NullPointerException();
		} else {
			divinity = divinities.get(0).toUpperCase();
			divinities.remove(0);
			if (divinities.size() >= 1) {
				next = new NetDivinityChoice(msg,divinities);
			} else {
				next = null;
			}
		}
		player = null;
		challenger = null;
		godsEnd = false;
	}
	/**
	 * Creates a message from the client to the server indicating the gods it has chosen for the game.
	 * @param msg is the message to be sent
	 * @param player is the challenger's name
	 * @param divinities is the list of game's gods
	 * @throws NullPointerException if {@code msg} is null or {@code divinities} is null
	 */
	public NetDivinityChoice(String msg, String player, List<String> divinities) throws NullPointerException {
		super(msg);
		if (divinities == null) {
			throw new NullPointerException();
		} else {
			divinity = divinities.get(0).toUpperCase();
			divinities.remove(0);
			if (divinities.size() >= 1) {
				next = new NetDivinityChoice(msg,divinities);
			} else {
				next = null;
			}
		}
		this.player = player;
		challenger = null;
		godsEnd = false;
	}

	// getters
	/**
	 * Gets the parameter {@link #divinity}.
	 * @return value of {@link #divinity}
	 */
	public String getDivinity() {
		return divinity;
	}
	/**
	 * Gets the parameter {@link #player}.
	 * @return value of {@link #player}
	 */
	public String getPlayer() {
		return player;
	}
	/**
	 * Builds a map with the association between the players (keys) and the gods they have chosen (values) inside this structure.
	 * @return a map of god's players' choice
	 */
	public Map<String,String> getPlayerGodMap() {
		Map<String,String> list = new LinkedHashMap<>();
		if (player != null && divinity != null) {
			list.put(player,divinity);
			if (next != null) {
				list.putAll(next.getPlayerGodMap());
			}
		}
		return list;
	}
	/**
	 * Builds a list of the divinities inside this structure.
	 * @return a list of divinities' names
	 */
	public List<String> getDivinities() {
		if (divinity == null) {
			return null;
		} else {
			List<String> divinityNames = new ArrayList<>();
			divinityNames.add(divinity);
			NetDivinityChoice x;
			x = next;
			while(x != null){
				divinityNames.add(x.divinity);
				x = x.next;
			}
			return divinityNames;
		}
	}
}
