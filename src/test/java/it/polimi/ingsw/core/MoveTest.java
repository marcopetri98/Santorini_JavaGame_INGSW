package it.polimi.ingsw.core;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class MoveTest {
	private Player playerA = new Player("Pippo", Color.RED);
	private int typeMove=0;
	private Cell prev = new Cell(0,0);
	private Cell next = new Cell (1,1);
	private Worker workerA = playerA.getWorker1();
	private Move move = new Move(typeMove, prev, next, workerA);

	private Player playerB = new Player("Pluto", Color.yellow);
	//same typeMove, for ex.
	private Cell prev2 = new Cell(1,1);
	private Cell next2 = new Cell(2,2);
	private Worker workerB = playerB.getWorker1();
	private Move other = new Move(typeMove, prev2, next2, workerB);

	@Test
	public void testEquals() {
		assertFalse(move.equals(other));
	}
}