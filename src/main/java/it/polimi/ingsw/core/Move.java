package it.polimi.ingsw.core;

// necessary imports from other packages of the project
import it.polimi.ingsw.network.game.NetMove;

/**
 * This class represents a move of the workers in the game
 */
public class Move {
	public final TypeMove typeMove;
	public final Cell prev;
	public final Cell next;
	public final Worker worker;
	private Move other;

	/**
	 * Constructor of the class
	 * @param t the {@link TypeMove}
	 * @param p the previous {@link Cell}
	 * @param n the following {@link Cell}
	 * @param w the {@link Worker} involved in the move
	 */
	public Move(TypeMove t, Cell p, Cell n, Worker w){
		typeMove = t;
		prev = p;
		next = n;
		worker = w;
	}

	// SETTERS

	/**
	 * Setter of the {@code other} {@link Move}
	 * @param o the {@code other} {@link Move}
	 */
	public void setCondition(Move o){
		other = o;
	}

	// GETTERS

	/**
	 * Getter of {@code typeMove}
	 * @return the {@code typeMove}
	 */
	public TypeMove getType(){
		return typeMove;
	}

	/**
	 * Getter of previous cell
	 * @return the previous cell
	 */
	public Cell getCellPrev(){
		return prev;
	}

	/**
	 * Getter of following cell
	 * @return the following cell
	 */
	public Cell getCellNext(){
		return next;
	}

	/**
	 * Getter of the {@link Worker} of the {@link Move}
	 * @return the {@link Worker}
	 */
	public Worker getWorker(){
		return worker;
	}

	/**
	 * Getter of the {@code other} {@link Move}
	 * @return the {@code other} {@link Move}
	 */
	public Move getOther(){
		return other;
	}

	/**
	 * Check if a {@link Move} and a {@link NetMove} are the same
	 * @param playerMove the {@link NetMove}
	 * @return true if they are the same
	 */
	public boolean isSameAs(NetMove playerMove) {
		return next.map.getX(next) == playerMove.cellX && next.map.getY(next) == playerMove.cellY && worker.workerID == playerMove.workerID && ((other == null && playerMove.other == null) || (other != null && playerMove.other != null && other.isSameAs(playerMove.other)));
	}

	// OVERRIDDEN METHODS

	/**
	 * Overridden equals method
	 * @param obj the object to check
	 * @return true if they are the same
	 */
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Move){
			Move m = (Move) obj;
			if (this.typeMove == m.getType() && this.prev == m.getCellPrev() && this.next == m.getCellNext() && this.worker == m.getWorker() && ((this.other == null && this.other == m.getOther()) || (this.other != null && m.getOther() != null && this.other.equals(m.getOther())))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}