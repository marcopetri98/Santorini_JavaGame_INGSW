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
	private final Color color;
	private final Player owner;
	public final int workerID;

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
	public Color getColor() {
		return color;
	}
	public Player getOwner() {
		return owner;
	}

	//SETTERS OF POSITION [Implements the observable object specifically for Athena]
	public void setPos(Cell c){
		if(this.position != null)   previousPositions.add(this.position);
		Cell[] positions = new Cell[2];
		positions[0] = this.position;   //Old position
		positions[1] = c;               //New position
		//if(this.position != null) this.position.setWorker(null);  //updates worker field in old cell
		this.position = c;
		//this.position.setWorker(this);  //updates worker field in new cell
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
			return color.equals(other.color) && workerID == other.workerID && position.getMap().getX(position) == other.position.getMap().getX(other.position) && position.getMap().getY(position) == other.position.getMap().getY(other.position) && position.equals(other.position);
		}
		return false;
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}