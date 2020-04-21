package it.polimi.ingsw.core;

import java.awt.Color;
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
	public final Color color;
	public final int workerID;
	// FIXME: player owner isn't part of the state of a worker
	public final Player owner;

	public Worker(Color color, Player owner, int num){
		this.color = color;
		this.owner = owner;
		workerID = owner.getPlayerID()+num;
		previousPositions = new ArrayDeque<>();
	}

	//GETTERS OF POSITION
	public Cell getPos() {
		return position;
	}
	public Cell getLastPos(){
		return previousPositions.getLast();
	}
	public Cell getLastBuildPos(){
		return this.lastBuild;
	}

	//SETTERS OF POSITION [Implements the observable object specifically for Athena]
	public void setPos(Cell c){
		if (this.position != null) {
			previousPositions.add(this.position);
		}
		Cell[] positions = new Cell[2];
		positions[0] = this.position;   //Old position
		positions[1] = c;               //New position
		this.position = c;
		setChanged();
		notifyObservers(positions);
	}   //ADD TO OBSERVER ONLY THE ONES CREATED BY THE PLAYER WITH ATHENA
	public void setLastBuildPos(Cell c){
		this.lastBuild = c;
	}

	// OVERRIDDEN METHODS
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Worker) {
			Worker other = (Worker) obj;
			return color.equals(other.color) && workerID == other.workerID && position.map.getX(position) == other.position.map.getX(other.position) && position.map.getY(position) == other.position.map.getY(other.position) && position.equals(other.position);
		}
		return false;
	}
}