package it.polimi.ingsw.network.game;

import it.polimi.ingsw.core.Cell;

import java.io.Serializable;

/**
 * This class is the network class used to store the information of a game map cell.
 */
public class NetCell implements Serializable {
	public final NetBuilding building;
	public final NetWorker worker;

	/**
	 * Creates a network cell from a {@link it.polimi.ingsw.core.Cell}.
	 * @param cell cell used to create the network cell
	 */
	public NetCell(Cell cell) {
		building = new NetBuilding(cell.getBuilding());
		if(cell.getWorker() != null){
			worker = new NetWorker(cell.getWorker(), this);
		} else {
			worker = null;
		}
	}
	/**
	 * Creates a network cell from another network cell.
	 * @param netCell the network cell used to create this network cell
	 */
	public NetCell(NetCell netCell) {
		building = new NetBuilding(netCell.getBuilding());
		if (netCell.getWorker() != null) {
			worker = new NetWorker(netCell.getWorker(), this);
		} else {
			worker = null;
		}
	}
	/**
	 * Copies a building changing a value.
	 * @param netCell the cell to modify
	 * @param netWorker the new {@link it.polimi.ingsw.network.game.NetWorker}
	 */
	private NetCell(NetCell netCell, NetWorker netWorker) {
		building = new NetBuilding(netCell.getBuilding());
		worker = netWorker;
	}
	/**
	 * Copies a building changing a value.
	 * @param netCell the cell to modify
	 * @param netBuilding the new {@link it.polimi.ingsw.network.game.NetBuilding}
	 */
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
	/**
	 * Creates a new cell from the old one modifying the worker attribute.
	 * @param worker the new worker
	 * @return a modified {@code NetCell}
	 */
	public NetCell setWorker(NetWorker worker) {
		return new NetCell(this,worker);
	}
	/**
	 * Creates a new cell from the old one modifying the building attribute.
	 * @param building the new building
	 * @return a modified {@code NetCell}
	 */
	public NetCell setBuilding(NetBuilding building) {
		return new NetCell(this,building);
	}

	/* **********************************************
	 *												*
	 * GETTERS AND METHODS WHICH DON'T CHANGE STATE	*
	 * 												*
	 ************************************************/
	/**
	 * Gets the parameter {@link #building}.
	 * @return value of {@link #building}
	 */
	public NetBuilding getBuilding() {
		return building;
	}
	/**
	 * Gets the parameter {@link #worker}.
	 * @return value of {@link #worker}
	 */
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
