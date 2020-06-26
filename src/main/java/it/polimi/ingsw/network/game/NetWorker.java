package it.polimi.ingsw.network.game;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.Worker;
import it.polimi.ingsw.util.Constants;

// necessary imports of Java SE
import it.polimi.ingsw.util.Color;
import java.io.Serializable;

/**
 * This class represent a worker on the game map, this worker is obviously owned by a player which is represented by a string that is its name, all clients has a list of all players inside the game and this makes possible to recognize a worker by the owner.
 */
public class NetWorker implements Serializable {
	public final Color color;
	public final String owner;
	public final int workerID;
	public transient final NetCell position;

	/**
	 * Creates a worker from a given {@link it.polimi.ingsw.core.Worker} and a {@link it.polimi.ingsw.network.game.NetCell}.
	 * @param worker the {@link it.polimi.ingsw.core.Worker} to copy
	 * @param pos the position of this worker
	 */
	public NetWorker(Worker worker, NetCell pos) {
		color = worker.color;
		workerID = worker.workerID;
		owner = worker.owner.getPlayerName();
		position = pos;
	}
	/**
	 *Creates a worker from a given network worker and a {@link it.polimi.ingsw.network.game.NetCell}.
	 * @param worker the other network worker
	 * @param pos the position of this worker
	 */
	public NetWorker(NetWorker worker, NetCell pos) {
		color = worker.color;
		workerID = worker.workerID;
		owner = worker.owner;
		position = pos;
	}
	/**
	 * Modify the given worker changing the {@link #color} attribute.
	 * @param worker the worker to modify
	 * @param newColor new worker's color
	 */
	private NetWorker(NetWorker worker, Color newColor) {
		color = newColor;
		workerID = worker.workerID;
		owner = worker.owner;
		position = worker.position;
	}
	/**
	 * Modify the given worker changing the {@link #workerID} attribute.
	 * @param worker the worker to modify
	 * @param id new worker's id
	 */
	private NetWorker(NetWorker worker, int id) {
		color = worker.color;
		workerID = id;
		owner = worker.owner;
		position = worker.position;
	}
	/**
	 * Modify the given worker changing the {@link #owner} attribute.
	 * @param worker the worker to modify
	 * @param ownerName new owner's name
	 */
	private NetWorker(NetWorker worker, String ownerName) {
		color = worker.color;
		workerID = worker.workerID;
		owner = ownerName;
		position = worker.position;
	}

	/* **********************************************
	 *												*
	 *		MODIFIERS FOR USER IMMUTABLE OBJECT		*
	 * 												*
	 ************************************************/
	/**
	 * Creates a new worker from the old one modifying the {@link #color} attribute.
	 * @param color the new worker color
	 * @return a new modified worker
	 */
	public NetWorker setColor(Color color) {
		return new NetWorker(this,color);
	}
	/**
	 * Creates a new worker from the old one modifying the {@link #owner} attribute.
	 * @param name the new owner's name
	 * @return a new modified worker
	 */
	public NetWorker setOwner(String name) {
		return new NetWorker(this,name);
	}
	/**
	 * Creates a new worker from the old one modifying the {@link #workerID} attribute.
	 * @param id the new id for the worker
	 * @return a new modified worker
	 */
	public NetWorker setWorkerId(int id) {
		return new NetWorker(this,id);
	}
	/**
	 * Creates a new worker from the old one modifying the {@link #position} attribute.
	 * @param cell the new {@link it.polimi.ingsw.network.game.NetCell} position
	 * @return a new modified worker
	 */
	public NetWorker setCell(NetCell cell) {
		return new NetWorker(this,cell);
	}

	/* **********************************************
	 *												*
	 * GETTERS AND METHODS WHICH DON'T CHANGE STATE	*
	 * 												*
	 ************************************************/
	/**
	 * Gets the parameter {@link #color}.
	 * @return value of {@link #color}
	 */
	public Color getColor() {
		return color;
	}
	/**
	 * Gets the parameter {@link #owner}.
	 * @return value of {@link #owner}
	 */
	public String getOwner() {
		return owner;
	}
	/**
	 * Gets the parameter {@link #workerID}.
	 * @return value of {@link #workerID}
	 */
	public int getWorkerID() {
		return workerID;
	}
	/**
	 * Gets the parameter {@link #position}.
	 * @return value of {@link #position}
	 */
	public NetCell getPosition() {
		return position;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NetWorker) {
			NetWorker other = (NetWorker) obj;
			if (workerID == other.workerID && owner.equals(other.owner) && color.equals(other.color)) {
				return true;
			}
		}
		return false;
	}
}
