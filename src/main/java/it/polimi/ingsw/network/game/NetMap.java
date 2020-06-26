package it.polimi.ingsw.network.game;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.Map;
import it.polimi.ingsw.core.Worker;
import it.polimi.ingsw.util.Constants;

// necessary imports of Java SE
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the network class that represent the server game map, it is a fully map with all the needed information using other network classes like {@link it.polimi.ingsw.network.game.NetCell} directly and indirectly through the previous cited: {@link it.polimi.ingsw.network.game.NetWorker}, {@link it.polimi.ingsw.network.game.NetBuilding}.
 */
public class NetMap implements Serializable {
	public final List<List<NetCell>> cells;

	/**
	 * Creates a map from a {@link it.polimi.ingsw.core.Cell} copying it in the network version.
	 * @param map a {@link it.polimi.ingsw.core.Map}
	 */
	public NetMap(Map map) {
		cells = new ArrayList<>();
		for (int x = 0; x <= 4; x++) {
			cells.add(new ArrayList<NetCell>());
			for (int y = 0; y <= 4; y++) {
				cells.get(x).add(new NetCell(map.getCell(x,y)));
			}
		}
	}
	/**
	 * Modifies a network map changing a cell on it, without modifying other values.
	 * @param netMap the network map to modify
	 * @param cell cell to insert
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	private NetMap(NetMap netMap, NetCell cell, int x, int y) {
		cells = new ArrayList<>();
		for (int i = 0; i <= 4; i++) {
			cells.add(new ArrayList<NetCell>());
			for (int j = 0; j <= 4; j++) {
				if (i != x || j != y) {
					cells.get(i).add(new NetCell(netMap.getCell(i,j)));
				} else {
					cells.get(i).add(cell);
				}
			}
		}
	}

	/* **********************************************
	 *												*
	 *		MODIFIERS FOR USER IMMUTABLE OBJECT		*
	 * 												*
	 ************************************************/
	/**
	 * Changes the current map changin a cell in the specified cartesian coordinates.
	 * @param newCell the new cell to insert in the map
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return the new modified map
	 */
	public NetMap changeCell(NetCell newCell, int x, int y) {
		return new NetMap(this,newCell,x,y);
	}

	/* **********************************************
	 *												*
	 * GETTERS AND METHODS WHICH DON'T CHANGE STATE	*
	 * 												*
	 ************************************************/
	/**
	 * It gets a cell of the current map given some cartesian coordinates.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return the {@link it.polimi.ingsw.network.game.NetCell} at the given coordinates
	 * @throws IllegalArgumentException if the coordinates aren't inside the map
	 */
	public NetCell getCell(int x, int y) throws IllegalArgumentException {
		if (x < 0 || y < 0 || x >= Constants.MAP_SIDE || y >= Constants.MAP_SIDE) {
			throw new IllegalArgumentException();
		}
		return cells.get(x).get(y);
	}
	/**
	 * Gets the x cartesian coordinate of a given cell of the map.
	 * @param c is the cell to search for
	 * @return an integer representing its x coordinate
	 * @throws IllegalArgumentException if the {@link it.polimi.ingsw.network.game.NetCell} doesn't exists
	 */
	public int getX(NetCell c) throws IllegalArgumentException {
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
	 * Gets the y cartesian coordinate of a given cell of the map.
	 * @param c is the cell to search for
	 * @return an integer representing its y coordinate
	 * @throws IllegalArgumentException if the {@link it.polimi.ingsw.network.game.NetCell} doesn't exists
	 */
	public int getY(NetCell c) throws IllegalArgumentException  {
		for (int i = 0; i < Constants.MAP_SIDE; i++) {
			for (int j = 0; j < Constants.MAP_SIDE; j++) {
				if (c == cells.get(i).get(j)) {
					return j;
				}
			}
		}
		throw new IllegalArgumentException();
	}
	/**
	 * Checks if the given cell is inside the current map.
	 * @param cell a {@link it.polimi.ingsw.network.game.NetCell}
	 * @return true if the cell is contained, false instead
	 */
	public boolean contains(NetCell cell) {
		for (int x = 0; x < cells.size(); x++) {
			if (cells.get(x).contains(cell)) {
				return true;
			}
		}
		return false;
	}
}
