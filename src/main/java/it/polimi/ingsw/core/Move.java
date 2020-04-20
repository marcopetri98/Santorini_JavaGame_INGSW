package it.polimi.ingsw.core;

// necessary imports from other packages of the project
import it.polimi.ingsw.network.game.NetMove;

public class Move {
	public final TypeMove typeMove;
	public final Cell prev;
	public final Cell next;
	public final Worker worker;
	private Move other;

	public Move(TypeMove t, Cell p, Cell n, Worker w){
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
	public TypeMove getType(){
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

	@Override
	public boolean equals(Object obj){
		if(obj instanceof Move){
			Move m = (Move) obj;
			if(this.typeMove == m.getType() && this.prev == m.getCellPrev() && this.next == m.getCellNext() && this.worker == m.getWorker() && ((this.other == null && this.other == m.getOther()) || (this.other != null && m.getOther() != null && this.other.equals(m.getOther())))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}