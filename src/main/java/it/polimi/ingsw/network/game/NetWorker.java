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

	public NetWorker(Worker worker, NetCell pos) {
		color = worker.color;
		workerID = worker.workerID;
		owner = worker.owner.getPlayerName();
		position = pos;
	}
	public NetWorker(NetWorker worker, NetCell pos) {
		color = worker.color;
		workerID = worker.workerID;
		owner = worker.owner;
		position = pos;
	}
	private NetWorker(NetWorker worker, Color newColor) {
		color = newColor;
		workerID = worker.workerID;
		owner = worker.owner;
		position = worker.position;
	}
	private NetWorker(NetWorker worker, int id) {
		color = worker.color;
		workerID = id;
		owner = worker.owner;
		position = worker.position;
	}
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
	public NetWorker setColor(Color color) {
		return new NetWorker(this,color);
	}
	public NetWorker setOwner(String name) {
		return new NetWorker(this,name);
	}
	public NetWorker setWorkerId(int id) {
		return new NetWorker(this,id);
	}
	public NetWorker setCell(NetCell cell) {
		return new NetWorker(this,cell);
	}

	/* **********************************************
	 *												*
	 * GETTERS AND METHODS WHICH DON'T CHANGE STATE	*
	 * 												*
	 ************************************************/
	public Color getColor() {
		return color;
	}
	public String getOwner() {
		return owner;
	}
	public int getWorkerID() {
		return workerID;
	}
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
