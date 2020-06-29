package it.polimi.ingsw.network.game;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.Build;
import it.polimi.ingsw.util.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by the player to communicate between server and clients the build that the clients want to perform or to communicate from the server to the clients the builds that can be effectuated by the active player of the current phase of the gaming game phase.
 */
public class NetBuild implements Serializable {
	public final int workerID;
	public final int cellX;
	public final int cellY;
	public final int level;
	public final boolean dome;
	public final NetBuild other;

	/**
	 * Creates a {@code NetBuild} given the values of the fields to set with {@link #other} field null.
	 * @param worker {@link #workerID} value
	 * @param x {@link #cellX} value
	 * @param y {@link #cellY} value
	 * @param level {@link #level} value
	 * @param dome {@link #dome} value
	 */
	public NetBuild(int worker, int x, int y, int level, boolean dome) {
		this.workerID = worker;
		this.cellX = x;
		this.cellY = y;
		this.level = level;
		this.dome = dome;
		other = null;
	}
	/**
	 * Creates a {@code NetBuild} given the values of the fields to set.
	 * @param worker {@link #workerID} value
	 * @param x {@link #cellX} value
	 * @param y {@link #cellY} value
	 * @param level {@link #level} value
	 * @param dome {@link #dome} value
	 * @param otherBuild {@link #other} value
	 */
	public NetBuild(int worker, int x, int y, int level, boolean dome, NetBuild otherBuild) {
		this.workerID = worker;
		this.cellX = x;
		this.cellY = y;
		this.level = level;
		this.dome = dome;
		other = otherBuild;
	}
	/**
	 * Creates a {@code NetBuild} from a given {@link it.polimi.ingsw.core.Build} computed from the server.
	 * @param build a {@link it.polimi.ingsw.core.Build}
	 * @throws NullPointerException if {@code build} is null
	 */
	public NetBuild(Build build) throws NullPointerException {
		if (build == null) {
			throw new NullPointerException();
		}
		workerID = build.worker.workerID;
		cellX = build.cell.map.getX(build.cell);
		cellY = build.cell.map.getY(build.cell);
		if (build.dome) {
			dome = true;
			level = build.cell.building.getLevel();
		} else {
			level = build.cell.building.getLevel();
			dome = false;
		}
		if (build.getOther() == null) {
			other = null;
		} else {
			other = new NetBuild(build.getOther());
		}
	}
	/**
	 * Modify the given build changing a value.
	 * @param build the {@code NetBuild} to modify
	 * @param other the new {@link #other} value
	 */
	private NetBuild(NetBuild build, NetBuild other) {
		workerID = build.workerID;
		cellX = build.cellX;
		cellY = build.cellY;
		level = build.level;
		dome = build.dome;
		this.other = other;
	}

	/* **********************************************
	 *												*
	 *		MODIFIERS FOR BUILD IMMUTABLE OBJECT	*
	 * 												*
	 ************************************************/
	/**
	 * Change the current {@code NetBuild} changing the {@link #workerID}
	 * @param id the new {@link #workerID} value
	 * @return the modified {@code NetBuild}
	 */
	public NetBuild setWorkerId(int id) {
		return new NetBuild(id,cellX,cellY,level,dome,other);
	}
	/**
	 * Change the current {@code NetBuild} changing the {@link #workerID}
	 * @param x the new {@link #cellX} value
	 * @return the modified {@code NetBuild}
	 */
	public NetBuild setCellX(int x) {
		return new NetBuild(workerID,x,cellY,level,dome,other);
	}
	/**
	 * Change the current {@code NetBuild} changing the {@link #workerID}
	 * @param y the new {@link #cellY} value
	 * @return the modified {@code NetBuild}
	 */
	public NetBuild setCellY(int y) {
		return new NetBuild(workerID,cellX,y,level,dome,other);
	}
	/**
	 * Change the current {@code NetBuild} changing the {@link #workerID}
	 * @param newLevel the new {@link #level} value
	 * @return the modified {@code NetBuild}
	 */
	public NetBuild setLevel(int newLevel) {
		return new NetBuild(workerID,cellX,cellY,newLevel,dome,other);
	}
	/**
	 * Change the current {@code NetBuild} changing the {@link #workerID}
	 * @param newDome the new {@link #dome} value
	 * @return the modified {@code NetBuild}
	 */
	public NetBuild setDome(boolean newDome) {
		return new NetBuild(workerID,cellX,cellY,level,newDome,other);
	}
	/**
	 * Change the current {@code NetBuild} changing the {@link #workerID}
	 * @param otherBuild the new {@link #other} value
	 * @return the modified {@code NetBuild}
	 */
	public NetBuild setOther(NetBuild otherBuild) {
		return new NetBuild(this,otherBuild);
	}
	/**
	 * Change the current {@code NetBuild} adding another {@code NetBuild} to the dynamic list of concatenated moves
	 * @param elementToAdd the new {@code NetBuild} to add
	 * @return the modified {@code NetBuild}
	 */
	public NetBuild appendOther(NetBuild elementToAdd) {
		List<NetBuild> listOfNetBuilds = getNetBuildsList();
		NetBuild toReturn = elementToAdd;
		for (int i = listOfNetBuilds.size()-1; i >= 0; i--) {
			toReturn = listOfNetBuilds.get(i).setOther(toReturn);
		}

		return toReturn;
	}

	/* **********************************************
	 *												*
	 * GETTERS AND METHODS WHICH DON'T CHANGE STATE	*
	 * 												*
	 ************************************************/
	/**
	 * Gets the list of all concatenated {@code NetBuild} in this structure.
	 * @return a list of {@code NetBuild}
	 */
	public List<NetBuild> getNetBuildsList() {
		List<NetBuild> returnList = new ArrayList<>();
		returnList.add(this);

		NetBuild pointer = other;
		while (pointer != null) {
			returnList.add(pointer);
			pointer = pointer.other;
		}
		return returnList;
	}
	/**
	 * It checks if the coordinates inside this object are possible coordinates for the game table.
	 * @return true if the object is well formed, false instead
	 */
	public boolean isWellFormed() {
		if (cellX >= 0 && cellX < Constants.MAP_SIDE && cellY >= 0 && cellY < Constants.MAP_SIDE && workerID != 0 && level >= 0 && level <= 3) {
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
	 * This method checks if two different {@code NetBuild} are similar, this means that they point to the same cell with same attributes (except level) having the same worker selected.
	 * @param obj the other object to compare
	 * @return true if {@code obj} is a {@code NetBuild} and is similar to this
	 */
	public boolean isLike(Object obj){
		if(obj instanceof NetBuild){
			NetBuild b = (NetBuild) obj;
			if (this.workerID == b.workerID && this.cellX == b.cellX && this.cellY == b.cellY && this.dome == b.dome) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	/**
	 * This method checks if two different {@code NetBuild} are similar, this means that they point to the same cell with same attributes having the same worker selected.
	 * @param obj the other object to compare
	 * @return true if {@code obj} is a {@code NetBuild} and is similar to this
	 */
	public boolean isLikeStrong(Object obj){
		if(obj instanceof NetBuild){
			NetBuild b = (NetBuild) obj;
			if (this.workerID == b.workerID && this.cellX == b.cellX && this.cellY == b.cellY && this.level == b.level && this.dome == b.dome) {
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
		if(obj instanceof NetBuild){
			NetBuild b = (NetBuild) obj;
			if (this.workerID == b.workerID && this.cellX == b.cellX && this.cellY == b.cellY && this.dome == b.dome && ((this.other == null && b.other == null) || (this.other != null && b.other != null && this.other.equals(b.other)))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
