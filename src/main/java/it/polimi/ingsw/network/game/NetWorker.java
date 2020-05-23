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
}
