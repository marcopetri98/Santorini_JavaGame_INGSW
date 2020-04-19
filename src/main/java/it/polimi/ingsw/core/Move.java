package it.polimi.ingsw.core;

// necessary imports from other packages of the project
import it.polimi.ingsw.network.game.NetMove;

public class Move {
	public final int typeMove;
	public final Cell prev;
	public final Cell next;
	public final Worker worker;
	private Move other;

	public Move(int t, Cell p, Cell n, Worker w){
		typeMove = t;
		prev = p;
		next = n;
		worker = w;
	}

	// class setters
	public void setCondition(Move o){
		other = o;
	}

	// class getters
	public int getType(){
		return typeMove;
	}
	public Cell getCellPrev(){
		return prev;
	}
	public Cell getCellNext(){
		return next;
	}
	public Worker getWorker(){
		return worker;
	}
	public Move getOther(){
		return other;
	}
	public boolean isSameAs(NetMove playerMove) {
		return next.map.getX(next) == playerMove.cellX && next.map.getY(next) == playerMove.cellY && worker.workerID == playerMove.workerID;
	}

	// overridden methods
	//TODO: fixare perché sbagliata
	public boolean equals(Move m){
		if(this.prev == m.getCellPrev() && this.next == m.getCellNext() && this.worker == m.getWorker()) return true;
		return false;
	}
}