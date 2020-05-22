package it.polimi.ingsw.network.game;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.Build;
import it.polimi.ingsw.util.Constants;

import java.io.Serializable;

/**
 * This class is used by the player to communicate to the server if
 */
public class NetBuild implements Serializable {
	public final int workerID;
	public final int cellX;
	public final int cellY;
	public final int level;
	public final boolean dome;
	public final NetBuild other;

	public NetBuild(NetWorker worker, NetCell cell, NetMap map, int level, boolean dome) throws NullPointerException {
		if (worker == null || cell == null || map == null) {
			throw new NullPointerException();
		}
		workerID = worker.workerID;
		cellX = map.getX(cell);
		cellY = map.getY(cell);
		this.level = level;
		this.dome = dome;
		other = null;
	}
	public NetBuild(int worker, int x, int y, int level, boolean dome) {
		this.workerID = worker;
		this.cellX = x;
		this.cellY = y;
		this.level = level;
		this.dome = dome;
		other = null;
	}
	public NetBuild(int worker, int x, int y, int level, boolean dome, NetBuild otherBuild) {
		this.workerID = worker;
		this.cellX = x;
		this.cellY = y;
		this.level = level;
		this.dome = dome;
		other = otherBuild;
	}
	public NetBuild(NetWorker worker, NetCell cell, NetMap map, int level, boolean dome, NetBuild other) throws NullPointerException {
		if (worker == null || cell == null || map == null || other == null) {
			throw new NullPointerException();
		}
		workerID = worker.workerID;
		cellX = map.getX(cell);
		cellY = map.getY(cell);
		this.level = level;
		this.dome = dome;
		this.other = other;
	}
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

	public boolean isWellFormed() {
		if (cellX >= 0 && cellX < Constants.MAP_SIDE && cellY >= 0 && cellY < Constants.MAP_SIDE && workerID != 0 && level >= 0 && level <= 2) {
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
		if(obj instanceof NetBuild){
			NetBuild b = (NetBuild) obj;
			if (this.workerID == b.workerID && this.cellX == b.cellX && this.cellY == b.cellY && this.level == b.level && this.dome == b.dome && ((this.other == null && b.other == null) || (this.other != null && b.other != null && this.other.equals(b.other)))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
