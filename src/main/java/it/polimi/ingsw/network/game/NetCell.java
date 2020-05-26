package it.polimi.ingsw.network.game;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.Building;
import it.polimi.ingsw.core.Cell;

import java.io.Serializable;

public class NetCell implements Serializable {
	public final NetBuilding building;
	public final NetWorker worker;

	public NetCell(Cell cell) {
		building = new NetBuilding(cell.getBuilding());
		if(cell.getWorker() != null){
			worker = new NetWorker(cell.getWorker(), this);
		} else {
			worker = null;
		}
	}
	public NetCell(NetCell netCell) {
		building = new NetBuilding(netCell.getBuilding());
		if(netCell.getWorker() != null){
			worker = new NetWorker(netCell.getWorker(), this);
		} else {
			worker = null;
		}
	}
	private NetCell(NetCell netCell, NetWorker netWorker) {
		building = new NetBuilding(netCell.getBuilding());
		worker = netWorker;
	}
	private NetCell(NetCell netCell, NetBuilding netBuilding) {
		building = netBuilding;
		if(netCell.getWorker() != null){
			worker = new NetWorker(netCell.getWorker(), this);
		} else {
			worker = null;
		}
	}

	/* **********************************************
	 *												*
	 *		MODIFIERS FOR USER IMMUTABLE OBJECT		*
	 * 												*
	 ************************************************/
	public NetCell setWorker(NetWorker worker) {
		return new NetCell(this,worker);
	}
	public NetCell setBuilding(NetBuilding building) {
		return new NetCell(this,building);
	}

	/* **********************************************
	 *												*
	 * GETTERS AND METHODS WHICH DON'T CHANGE STATE	*
	 * 												*
	 ************************************************/
	public NetBuilding getBuilding() {
		return building;
	}
	public NetWorker getWorker() {
		return worker;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NetCell) {
			NetCell other = (NetCell) obj;
			if (((worker != null && other.worker != null && worker.equals(other.worker)) || (worker == null && other.worker == null)) && building.equals(other.building)) {
				return true;
			}
		}
		return false;
	}
}
