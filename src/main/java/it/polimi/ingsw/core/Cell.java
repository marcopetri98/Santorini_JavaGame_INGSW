package it.polimi.ingsw.core;

/**
 * This is the Cell class, which implement a single cell on the map.
 */
public class Cell {
	public final Map map;
	private final int x;
	private final int y;
	public final Building building;
	// worker isn't part of the state of a cell, it only
	private Worker worker;

	/**
	 * Constructor of this class
	 * @param m a {@link Map}
	 * @param x the x coordinate of the {@link Cell}
	 * @param y the y coordinate of the {@link Cell}
	 */
	public Cell(Map m, int x, int y) {
		this.building = new Building();
		this.map = m;
		this.x = x;
		this.y = y;
	}

	// SETTERS

	/**
	 * Setter of the {@link Cell}
	 * @param w the {@link Worker} on this {@link Cell}
	 */
	void setWorker(Worker w){
		this.worker=w;
	}

	// CLASSES GETTERS

	/**
	 * Getter of the {@link Building} on the cell
	 * @return the {@link Building} on this cell
	 */
	public Building getBuilding() {
		return building;
	}

	/**
	 * Getter of the {@link Cell}
	 * @return the {@link Worker} on this {@link Cell}
	 */
	public Worker getWorker(){ return worker; }

	// OVERRIDDEN METHODS

	/**
	 * Overridden equals method
	 * @param obj the object to check
	 * @return true if they are the same
	 */
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