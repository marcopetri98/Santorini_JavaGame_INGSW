package it.polimi.ingsw.core;

public class Cell {
	public final Map map;
	public final Building building;
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
}