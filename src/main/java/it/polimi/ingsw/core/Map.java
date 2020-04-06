package it.polimi.ingsw.core;

// necessary imports of Java SE
import java.util.ArrayList;
import java.util.List;

public class Map {
	private List<List<Cell>> cells;

	// constructors and setters for this class
	public Map() {
		cells = new ArrayList<>();
		for (int i = 0; i <= 4; i++) {
			cells.add(new ArrayList<Cell>());
			for (int j = 0; j <= 4; j++) {
				cells.get(i).add(new Cell(j,i));
			}
		}
	}

	// getters and other functions which doesn't change the structure of the class
	public Cell getCell(int X, int Y){
		return cells.get(X).get(Y);
	}

	// checks if is possible to move a worker in a certain cell
	public boolean moveable(Cell c, Worker w) {
		Cell workerCell = w.getPos();

		if (c.getX() <= workerCell.getX()+1 && c.getX() >= workerCell.getX()-1 && c.getY() <= workerCell.getY()+1 && c.getY() >= workerCell.getY()-1) {
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