package it.polimi.ingsw.core;

// necessary imports of Java SE
import it.polimi.ingsw.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the map of the game
 */
public class Map {
	private final List<List<Cell>> cells;


	/**
	 * Empty constructor of this class
	 */
	public Map() {
		cells = new ArrayList<>();
		for (int i = 0; i < Constants.MAP_SIDE; i++) {
			cells.add(new ArrayList<Cell>());
			for (int j = 0; j < Constants.MAP_SIDE; j++) {
				cells.get(i).add(new Cell(this,i,j));
			}
		}
	}

	// GETTERS OF THE CLASS

	/**
	 * Getter of the {@link Cell} given the coordinates
	 * @param X,Y the coordinates of the {@link Cell}
	 * @return the {@link Cell} with these coordinates
	 */
	public Cell getCell(int X, int Y) {
		return cells.get(X).get(Y);
	}

	/**
	 * Getter of the X coordinate of a {@link Cell}
	 * @param c the {@link Cell}
	 * @return the X coordinate of the {@link Cell}
	 * @throws IllegalArgumentException
	 */
	public int getX(Cell c) throws IllegalArgumentException {
		for (int i = 0; i < Constants.MAP_SIDE; i++) {
			for (int j = 0; j < Constants.MAP_SIDE; j++) {
				if (c == cells.get(i).get(j)) {
					return i;
				}
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Getter of the Y coordinate of a {@link Cell}
	 * @param c the {@link Cell}
	 * @return the Y coordinate of a {@link Cell}
	 * @throws IllegalArgumentException
	 */
	public int getY(Cell c) throws IllegalArgumentException  {
		for (int i = 0; i < Constants.MAP_SIDE; i++) {
			for (int j = 0; j < Constants.MAP_SIDE; j++) {
				if (c == cells.get(i).get(j)) {
					return j;
				}
			}
		}
		throw new IllegalArgumentException();
	}

	// OVERRIDDEN METHODS

	/**
	 * Overridden equals method
	 * @param obj the object to check
	 * @return true if they are the same
	 */
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