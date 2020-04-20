package it.polimi.ingsw.core;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class MoveTest {
	private Player playerA = new Player("Pippo");
	private int typeMove=0;
	private Cell prev; //TODO: update cell fix
	private Cell next;
	private Worker workerA = playerA.getWorker1();
	private Move move = new Move(typeMove, prev, next, workerA);

	private Player playerB = new Player("Pluto");
	//same typeMove, for ex.
	private Cell prev2;
	private Cell next2;
	private Worker workerB = playerB.getWorker1();
	private Move other = new Move(typeMove, prev2, next2, workerB);

	@Test
	public void testEquals() {
		assertFalse(move.equals(other));
	}
}