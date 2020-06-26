package it.polimi.ingsw.network.game;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.Move;
import it.polimi.ingsw.util.Constants;

import java.io.Console;
import java.io.Serializable;

/**
 * This class is used to communicate between clients and server the move that the clients want to perform and the possible moves that the active player in the current game phase can perform depending on its god power and workers positions.
 */
public class NetMove implements Serializable {
	public final int workerID;
	public final int cellX;
	public final int cellY;
	public final NetMove other;

	/**
	 * Creates a {@code NetMove} from a given {@link it.polimi.ingsw.core.Move}
	 * @param move a {@link it.polimi.ingsw.core.Move}
	 */
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
	/**
	 * Creates a move with the specified parameters as attributes with {@link #other} null.
	 * @param workerID {@link #workerID} value
	 * @param x {@link #cellX} value
	 * @param y {@link #cellY} value
	 */
	public NetMove(int workerID, int x, int y) {
		this.workerID = workerID;
		cellX = x;
		cellY = y;
		other = null;
	}
	/**
	 * Creates a move with the specified parameters as attributes.
	 * @param workerID {@link #workerID} value
	 * @param x {@link #cellX} value
	 * @param y {@link #cellY} value
	 * @param other {@link #other} value
	 */
	public NetMove(int workerID, int x, int y, NetMove other) {
		this.workerID = workerID;
		cellX = x;
		cellY = y;
		this.other = null;
	}

	/* **********************************************
	 *												*
	 *		MODIFIERS FOR MOVE IMMUTABLE OBJECT		*
	 * 												*
	 ************************************************/
	/**
	 * Change the current {@code NetMove} changing the {@link #workerID} field.
	 * @param id the new {@link #workerID} value
	 * @return the modified {@code NetMove}
	 */
	public NetMove setWorkerId(int id) {
		return new NetMove(id,cellX,cellY,other);
	}
	/**
	 * Change the current {@code NetMove} changing the {@link #cellX} field.
	 * @param x the new {@link #cellX} value
	 * @return the modified {@code NetMove}
	 */
	public NetMove setX(int x) {
		return new NetMove(workerID,x,cellY,other);
	}
	/**
	 * Change the current {@code NetMove} changing the {@link #cellY} field.
	 * @param y the new {@link #cellY} value
	 * @return the modified {@code NetMove}
	 */
	public NetMove setY(int y) {
		return new NetMove(workerID,cellX,y,other);
	}
	/**
	 * Change the current {@code NetMove} changing the {@link #other} field.
	 * @param otherMove the new {@link #other} value
	 * @return the modified {@code NetMove}
	 */
	public NetMove setOther(NetMove otherMove) {
		return new NetMove(workerID,cellX,cellY,otherMove);
	}

	/* **********************************************
	 *												*
	 * GETTERS AND METHODS WHICH DON'T CHANGE STATE	*
	 * 												*
	 ************************************************/
	/**
	 * It checks if the coordinates inside this object are possible coordinates for the game table.
	 * @return true if the object is well formed, false instead
	 */
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
	/**
	 * This method checks if two different {@code NetMove} are similar, this means that they point to the same cell with the same worker selected.
	 * @param obj the other object to compare
	 * @return true if {@code obj} is a {@code NetMove} and is similar to this
	 */
	public boolean isLike(Object obj){
		if(obj instanceof NetMove){
			NetMove m = (NetMove) obj;
			if (this.workerID == m.workerID && this.cellX == m.cellX && this.cellY == m.cellY) {
				return true;
			} else {
				return false;
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
