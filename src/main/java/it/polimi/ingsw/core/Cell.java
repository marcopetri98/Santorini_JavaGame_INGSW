package it.polimi.ingsw.core;

public class Cell {
	private int x;
	private int y;
	private Building building;

	public Cell(int i, int j) {
		x = i;
		y = j;
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
}
