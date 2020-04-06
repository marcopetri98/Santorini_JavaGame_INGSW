package it.polimi.ingsw.core;

public class Move {
	private int typeMove;
	private Cell prev;
	private Cell next;
	private Worker worker;
	private Move other;

	public Move(int t, Cell p, Cell n, Worker w){
		typeMove = t;
		prev = p;
		next = n;
		worker = w;
	}

	private Cell getCellPrev(){
		return prev;
	}

	private Cell getCellNext(){
		return next;
	}

	private Worker getWorker(){
		return worker;
	}

	private Move getOther(){
		return other;
	}

	public void setCondition(Move o){
		other = o;
	}

	public int getType(){
		return typeMove;
	}

	//TODO: controllare se le condizioni sono corrette!!!
	public boolean equals(Move m){
		if(this.prev == m.getCellPrev() && this.next == m.getCellNext() && this.worker == m.getWorker()) return true;
		return false;
	}

}