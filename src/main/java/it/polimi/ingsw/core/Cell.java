package it.polimi.ingsw.core;

public class Cell {
	public final Map map;
	private final int x;
	private final int y;
	public final Building building;
	// worker isn't part of the state of a cell, it only
	private Worker worker;

	public Cell(Map m, int x, int y) {
		this.building = new Building();
		this.map = m;
		this.x = x;
		this.y = y;
	}

	// SETTERS
	void setWorker(Worker w){
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
			if (map.getX(this) == other.map.getX(other) && map.getY(this) == other.map.getY(other)) {
				if (this.getBuilding().equals(other.getBuilding())) {
					if (this.worker == null && other.worker == null) {
						return true;
					} else if (this.worker != null && other.getWorker() != null) {
						return true;
					}
				}
			}
		}
		return false;
	}
}