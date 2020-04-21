package it.polimi.ingsw.core;

public class Cell {
	// FIXME: the map isn't part of the cell state
	public final Map map;
	public final Building building;
	private Worker worker;

	public Cell(Map m) {
		this.building = new Building();
		this.map = m;
	}

	// SETTERS
	public void setWorker(Worker w){
		this.worker=w;
	}

	// CLASSES GETTERS
	public Building getBuilding() {
		return building;
	}
	public Worker getWorker(){ return worker; }

	// OVERRIDDEN METHODS
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Cell) {
			Cell other = (Cell)obj;
			return building.equals(other.building) && worker.equals(other.worker);
		}
		return false;
	}
}