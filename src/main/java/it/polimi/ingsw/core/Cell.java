package it.polimi.ingsw.core;

public class Cell {
	private int x;
	private int y;
	private Building building;
	private Worker worker;

	public Cell(int i, int j) {
		x = i;
		y = j;
		this.building = new Building();
	}

	public void setWorker(Worker w){
		this.worker=w;
	}

	// CLASSES GETTERS
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public Building getBuilding() {
		return building;
	}
	public Worker getWorker(){ return worker; }

}