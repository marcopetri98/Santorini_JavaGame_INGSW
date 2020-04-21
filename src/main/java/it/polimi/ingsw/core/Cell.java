package it.polimi.ingsw.core;

public class Cell {
	private final Map map;
	private final Building building;
	private Worker worker;

	public Cell(Map m) {
		this.building = new Building();
		this.map = m;
	}

	public void setWorker(Worker w){
		this.worker=w;
	}

	// CLASSES GETTERS
	public Building getBuilding() {
		return building;
	}
	public Worker getWorker(){ return worker; }
	public Map getMap() {
		return map;
	}

	// OVERRIDDEN METHODS
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Cell) {
			Cell other = (Cell)obj;
			return building.equals(other.building) && worker.equals(other.worker);
		}
		return false;
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}