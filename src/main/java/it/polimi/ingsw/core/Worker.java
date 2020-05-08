package it.polimi.ingsw.core;

import it.polimi.ingsw.util.Color;
import java.util.Observable;
import java.util.Queue;
import java.util.ArrayDeque;

/**
 * This is the Worker class, which stores the information about each worker of every Player.
 * This class also implements part of the Observer pattern used for the Athena class.
 */
public class Worker extends Observable {
	private ArrayDeque<Cell> previousPositions;
	private Cell position;
	private Cell lastBuild;
	private boolean hasBuilt; // always false apart during the turn (at the end of the turn this is set to false)
	public final Color color;
	public final int workerID;
	public final Player owner;

	public Worker(Color color, Player owner, int num){
		this.color = color;
		this.owner = owner;
		workerID = owner.getPlayerID()+num;
		previousPositions = new ArrayDeque<>();
		hasBuilt = false;
	}

	//SETTERS OF POSITION [Implements the observable object specifically for Athena]
	public void setPos(Cell c) {
		if (this.position != null) {
			previousPositions.add(this.position);
		}
		Cell[] positions = new Cell[]{this.position,c};
		this.position = c;

		//ADD TO OBSERVER ONLY THE ONES CREATED BY THE PLAYER WITH ATHENA
		setChanged();
		notifyObservers(positions);
	}
	void setLastBuildPos(Cell c){
		hasBuilt = true;
		this.lastBuild = c;
	}
	void resetBuilding() {
		hasBuilt = false;
	}

	//GETTERS OF POSITION
	public boolean itHasBuilt() {
		return hasBuilt;
	}
	public Cell getPos() {
		return position;
	}
	public Cell getLastPos(){
		return previousPositions.getLast();
	}
	public Cell getLastBuildPos(){
		return this.lastBuild;
	}

	// OVERRIDDEN METHODS
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Worker) {
			Worker other = (Worker) obj;
			return color.equals(other.color) && workerID == other.workerID && position == other.position; //TODO: may not check everything
		}
		return false;
	}
}