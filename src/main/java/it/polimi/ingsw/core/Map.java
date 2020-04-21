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

	// here there are methods which must be overridden
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Map) {
			Map other = (Map)obj;
			for (int x = 0; x < Constants.MAP_SIDE; x++) {
				for (int y = 0; y < Constants.MAP_SIDE; y++) {
					if (!cells.get(x).get(y).equals(other.cells.get(x).get(y))) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}
}