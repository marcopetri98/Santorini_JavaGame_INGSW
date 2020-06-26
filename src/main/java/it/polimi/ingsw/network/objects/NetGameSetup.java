package it.polimi.ingsw.network.objects;

// necessary imports from other packages of the project
import it.polimi.ingsw.network.game.NetMap;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.Pair;

/**
 * This class is a class used to exchange messages between clients and server in the game setup phase of the game where player positions worker on the game map. More information about game phase can be found on {@link it.polimi.ingsw.core.state} package.
 */
public class NetGameSetup extends NetObject {
	public final String player;
	public final NetMap gameMap;
	public final Pair<Integer,Integer> worker1;
	public final Pair<Integer,Integer> worker2;

	/**
	 * Creates a standard message and only calls the super constructor.
	 * @param msg is the message to be sent
	 * @throws NullPointerException if {@code msg} is null
	 */
	public NetGameSetup(String msg) {
		super(msg);
		player = null;
		gameMap = null;
		worker1 = null;
		worker2 = null;
	}
	/**
	 * Creates a message with a message and a player's name.
	 * @param msg is the message to be sent
	 * @param player is the player's name
	 */
	public NetGameSetup(String msg, String player) {
		super(msg);
		this.player = player;
		gameMap = null;
		worker1 = null;
		worker2 = null;
	}
	/**
	 * Create an update message from the server to the client with the information about the new map.
	 * @param msg is the message to be sent
	 * @param map is the updated game map
	 * @throws NullPointerException if {@code msg} is null or {@code map} is null
	 */
	public NetGameSetup(String msg, NetMap map) throws NullPointerException {
		super(msg);
		if (map == null) {
			throw new NullPointerException();
		}
		player = null;
		gameMap = map;
		worker1 = null;
		worker2 = null;
	}
	/**
	 * Create a message from the client to the server with the information about the position of the workers on the game map that the player wants to have for its own workers.
	 * @param msg is the message to be sent
	 * @param player is the player's name
	 * @param worker1 are the coordinates for the first worker
	 * @param worker2 are the coordinates for the second worker
	 * @throws NullPointerException if {@code msg} is null or {@code worker1} is null or {@code worker2} is null
	 * @throws IllegalArgumentException if coordinates ate not inside the map
	 */
	public NetGameSetup(String msg, String player, Pair<Integer,Integer> worker1, Pair<Integer,Integer> worker2) throws NullPointerException, IllegalArgumentException {
		super(msg);
		if (worker1 == null || worker2 == null) {
			throw new NullPointerException();
		} else if (!(worker1.getFirst() <= 4 && worker1.getFirst() >= 0 && worker1.getSecond() <= 4 && worker1.getSecond() >= 0 && worker2.getFirst() <= 4 && worker2.getFirst() >= 0 && worker2.getSecond() <= 4 && worker2.getSecond() >= 0)) {
			throw new IllegalArgumentException();
		}
		this.player = player;
		gameMap = null;
		this.worker1 = worker1;
		this.worker2 = worker2;
	}

	/**
	 * It checks if the coordinates inside this object are possible coordinates for the game table.
	 * @return true if is well formed, false instead
	 */
	public boolean isWellFormed() {
		if (gameMap == null) {
			if (worker1 == null || worker2 == null) {
				return false;
			} else {
				if (worker1.getFirst() < 0 || worker1.getFirst() >= Constants.MAP_SIDE || worker1.getSecond() < 0 || worker1.getSecond() >= Constants.MAP_SIDE || worker2.getFirst() < 0 || worker2.getFirst() >= Constants.MAP_SIDE || worker2.getSecond() < 0 || worker2.getSecond() >= Constants.MAP_SIDE) {
					return false;
				} else {
					return true;
				}
			}
		} else {
			if (worker1 != null || worker2 != null) {
				return false;
			} else {
				return true;
			}
		}
	}
}
