package it.polimi.ingsw.network.game;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.Move;

import java.io.Serializable;

/**
 * This class is used to communicate from the client to the server a position where it wants to move a specified worker which is owned by it.
 */
public class NetMove implements Serializable {
	public final int workerID;
	public final int cellX;
	public final int cellY;
	public final NetMove other;

	public NetMove(NetWorker worker, NetCell cell, NetMap map) {
		workerID = worker.workerID;
		cellX = map.getX(cell);
		cellY = map.getY(cell);
		other = null;
	}
	public NetMove(Move move) {
		workerID = move.worker.workerID;
		cellX = move.next.map.getX(move.next);
		cellY = move.next.map.getY(move.next);
		if (move.getOther() == null) {
			other = null;
		} else {
			other = new NetMove(move.getOther());
		}
	}
}
