package it.polimi.ingsw.network.game;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.Map;
import it.polimi.ingsw.core.Worker;
import it.polimi.ingsw.util.Constants;

// necessary imports of Java SE
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NetMap implements Serializable {
	public final List<List<NetCell>> cells;

	public NetMap(Map map) {
		cells = new ArrayList<>();
		for (int x = 0; x <= 4; x++) {
			cells.add(new ArrayList<NetCell>());
			for (int y = 0; y <= 4; y++) {
				cells.get(x).add(new NetCell(map.getCell(x,y)));
			}
		}
	}
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
	public NetMap changeCell(NetCell newCell, int x, int y) {
		return new NetMap(this,newCell,x,y);
	}

	/* **********************************************
	 *												*
	 * GETTERS AND METHODS WHICH DON'T CHANGE STATE	*
	 * 												*
	 ************************************************/
	public NetCell getCell(int x, int y) throws IllegalArgumentException {
		if (x < 0 || y < 0 || x >= Constants.MAP_SIDE || y >= Constants.MAP_SIDE) {
			throw new IllegalArgumentException();
		}
		return cells.get(x).get(y);
	}
	public int getX(NetCell c) throws IllegalArgumentException {
		for (int i = 0; i < cells.size(); i++) {
			if (cells.get(i).contains(c)) {
				return i;
			}
		}
		throw new IllegalArgumentException();
	}
	public int getY(NetCell c) throws IllegalArgumentException  {
		for (int i = 0; i < cells.size(); i++) {
			if (cells.get(i).contains(c)) {
				return cells.get(i).indexOf(c);
			}
		}
		throw new IllegalArgumentException();
	}
	public boolean contains(NetCell cell) {
		for (int x = 0; x < cells.size(); x++) {
			if (cells.get(x).contains(cell)) {
				return true;
			}
		}
		return false;
	}

	// TODO: are these really necessary?
	// security check methods
	public boolean containWorker(NetWorker worker) {
		for (int i = 0; i < cells.size(); i++) {
			for (int j = 0; j < cells.get(i).size(); j++) {
				if (cells.get(i).get(j).worker == worker) {
					return true;
				}
			}
		}
		return false;
	}
	private int numberOfCell(NetCell cell) {
		int number = 0;
		for (int x = 0; x < cells.size(); x++) {
			for (int y = 0; y < cells.get(x).size(); y++) {
				if (cells.get(x).get(y) == cell) {
					number++;
				}
			}
		}
		return number;
	}
}
