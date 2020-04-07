package it.polimi.ingsw.core;

public class Cell {
	private Building building;
	private Worker worker;

	public Cell() {
		this.building = new Building();
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