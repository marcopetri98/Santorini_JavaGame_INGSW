package it.polimi.ingsw.network.game;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.Move;
import it.polimi.ingsw.util.Constants;

import java.io.Console;
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
	public NetMove(int workerID, int x, int y) {
		this.workerID = workerID;
		cellX = x;
		cellY = y;
		other = null;
	}

	public boolean isWellFormed() {
		if (cellX >= 0 && cellX < Constants.MAP_SIDE && cellY >= 0 && cellY < Constants.MAP_SIDE && workerID != 0) {
			if (other != null) {
				return other.isWellFormed();
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof NetMove){
			NetMove m = (NetMove) obj;
			if (this.workerID == m.workerID && this.cellX == m.cellX && this.cellY == m.cellY && ((this.other == null && m.other == null) || (this.other != null && m.other != null && this.other.equals(m.other)))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
