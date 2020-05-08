package it.polimi.ingsw.network.game;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.Cell;

import java.io.Serializable;

public class NetCell implements Serializable {
	public final NetBuilding building;
	public final NetWorker worker;

	public NetCell(Cell cell) {
		building = new NetBuilding(cell.getBuilding());
		if(cell.getWorker() != null){
			worker = new NetWorker(cell.getWorker(), this);
		}
		else{
			worker = null;
		}
	}

	// TODO: is this really necessary?
	// security check methods
	public boolean trueCell() {
		if (building == null) {
			return false;
		} else {
			if (worker != null) {
				return building.trueBuilding() && worker.position == this;
			} else {
				return building.trueBuilding();
			}
		}
	}
}
