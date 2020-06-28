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
	// support variables
	private ArrayDeque<Cell> previousPositions;
	private Cell lastBuild;
	private boolean hasBuilt; // always false apart during the turn (at the end of the turn this is set to false)
	public final Player owner;
	// state variables
	private Cell position;
	public final Color color;
	public final int workerID;

	/**
	 * The constructor of the class
	 * @param color the color of the {@link Player}'s workers
	 * @param owner the {@link Player} owner of the card
	 * @param num the number used to calculate the unique ID for every worker
	 */
	public Worker(Color color, Player owner, int num){
		this.color = color;
		this.owner = owner;
		workerID = owner.getPlayerID()+num;
		previousPositions = new ArrayDeque<>();
		hasBuilt = false;
	}

	//SETTERS OF POSITION [Implements the observable object specifically for Athena]
	/**
	 * Setter of the position of this worker to a new {@link Cell} and removes the worker from the previous position. Then it notifies Athena of the change if necessary
	 * @param c the new cell for the worker
	 */
	void setPos(Cell c) {
		if (this.position != null) {
			previousPositions.add(this.position);
		}
		Cell[] positions = new Cell[]{this.position,c};
		this.position = c;

		//ADD TO OBSERVER ONLY THE ONES CREATED BY THE PLAYER WITH ATHENA
		setChanged();
		notifyObservers(positions);
	}

	/**
	 * Setter of the position of the last build
	 * @param c the cell of the last build
	 */
	void setLastBuildPos(Cell c){
		hasBuilt = true;
		this.lastBuild = c;
	}

	/**
	 * This methods resets the {@code hasBuilt} boolean
	 */
	void resetBuilding() {
		hasBuilt = false;
	}

	//GETTERS OF POSITION

	/**
	 * Returns the {@code hasBuilt} boolean
	 * @return the {@code hasBuilt} boolean
	 */
	public boolean itHasBuilt() {
		return hasBuilt;
	}

	/**
	 * Returns the position of the worker
	 * @return the position of the worker
	 */
	public Cell getPos() {
		return position;
	}

	/**
	 * Returns the last position of the worker
	 * @return the last position of the worker
	 */
	public Cell getLastPos(){
		return previousPositions.getLast();
	}

	/**
	 * Returns the position of the last build
	 * @return the position of the last build
	 */
	public Cell getLastBuildPos(){
		return this.lastBuild;
	}

	// OVERRIDDEN METHODS

	/**
	 * The overridden equals method
	 * @param obj the object to check
	 * @return true if the objects are the same
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Worker) {
			Worker other = (Worker) obj;
			return color.equals(other.color) && workerID == other.workerID && ((position == null && other.position == null) || (position != null && other.position != null && position.equals(other.position)));
		}
		return false;
	}
}