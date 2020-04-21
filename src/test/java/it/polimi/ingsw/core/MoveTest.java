package it.polimi.ingsw.core;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class MoveTest {
	private Move move1;
	private Map map;
	private Player player1;
	private TypeMove typeMove;


	@Before
	public void testSetup() {
		typeMove = TypeMove.SIMPLE_MOVE;
		map = new Map();
		player1 = new Player("Pippo");
		player1.setPlayerColor(Color.RED);
		player1.getWorker1().setPos(map.getCell(0, 0));
		map.getCell(0, 0).setWorker(player1.getWorker1());
		move1 = new Move(typeMove, map.getCell(0, 0), map.getCell(1, 1), player1.getWorker1());
	}

	/**
	 * Testing if two moves are the same.
	 */
	@Test
	public void testEquals() {
		Move move2 = new Move(typeMove, map.getCell(0, 0), map.getCell(1, 1), player1.getWorker1());
		assertEquals(move2, move1); //same moves

		TypeMove typeMove2 = TypeMove.CONDITIONED_MOVE;
		Move move3 = new Move(typeMove2, map.getCell(0, 0), map.getCell(1, 1), player1.getWorker1());
		assertNotEquals(move3, move1); //different typeMove

		Move move4 = new Move(typeMove, map.getCell(0, 0), map.getCell(2, 2), player1.getWorker1());
		assertNotEquals(move4, move1); //different cells

		Move move5 = new Move(typeMove, map.getCell(0, 0), map.getCell(1, 1), player1.getWorker2());
		assertNotEquals(move5, move1); //different worker

	}
}