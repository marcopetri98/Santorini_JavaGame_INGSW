package it.polimi.ingsw.core;

public class Cell {
	// FIXME: the map isn't part of the cell state
	public final Map map;
	public final Building building;
	private Worker worker;

	public Cell(Map m) {
		this.building = new Building();
		this.map = m;
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

	// OVERRIDDEN METHODS //TODO: really necessary to override?!?!
	/*@Override
	public boolean equals(Object obj) {
		if (obj instanceof Cell) {
			Cell other = (Cell)obj;
			if(this.getBuilding().equals(other.getBuilding())){
				if(this.worker == null && other.worker == null){
					return true;
				}
				else if(this.worker != null && other.getWorker() != null && this.worker.equals(other.worker)){
					return true;
				}
			}




			//return (worker == null && other.getWorker() == null && building.equals(other.building)) || (other.getWorker() != null && this.worker != null && building.equals(other.building) && worker.equals(other.getWorker()));
		}
		return false;
	}*/
}