package it.polimi.ingsw.core;

// necessary imports of Java SE
import it.polimi.ingsw.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class Map {
	private final List<List<Cell>> cells;

	// constructors and setters for this class
	public Map() {
		cells = new ArrayList<>();
		for (int i = 0; i < Constants.MAP_SIDE; i++) {
			cells.add(new ArrayList<Cell>());
			for (int j = 0; j < Constants.MAP_SIDE; j++) {
				cells.get(i).add(new Cell(this));
			}
		}
	}

	// getters and other functions which doesn't change the structure of the class
	public Cell getCell(int X, int Y){
		return cells.get(X).get(Y);
	}
	public int getX(Cell c) throws IllegalArgumentException {
		for (int i = 0; i < cells.size(); i++) {
			if (cells.get(i).contains(c)) {
				return i;
			}
		}
		throw new IllegalArgumentException();
	}
	public int getY(Cell c) throws IllegalArgumentException  {
		for (int i = 0; i < cells.size(); i++) {
			if (cells.get(i).contains(c)) {
				return cells.get(i).indexOf(c);
			}
		}
		throw new IllegalArgumentException();
	}

	// checks if is possible to move a worker in a certain cell
	public boolean moveable(Cell c, Worker w) {
		Cell workerCell = w.getPos();

		if (getX(c) <= getX(workerCell)+1 && getX(c) >= getX(workerCell)-1 && getY(c) <= getY(workerCell)+1 && getY(c) >= getY(workerCell)-1) {
			return c.getBuilding().getLevel() == w.getPos().getBuilding().getLevel() || c.getBuilding().getLevel() == w.getPos().getBuilding().getLevel() + 1 || c.getBuilding().getLevel() == w.getPos().getBuilding().getLevel() - 1;
		}
		return false;
	}

	// here there are methods which must be overridden
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}