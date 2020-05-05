package it.polimi.ingsw.core;

import it.polimi.ingsw.core.gods.Apollo;
import it.polimi.ingsw.core.gods.GodCard;
import org.junit.Before;
import org.junit.Test;

import it.polimi.ingsw.util.Color;

import static org.junit.Assert.*;

public class PlayerTest {
	private Player player1;

	@Before
	public void testSetup(){
		Map map = new Map();
		player1 = new Player("Pippo");
		player1.setPlayerColor(Color.RED);
		GodCard carta = new Apollo();
		player1.setGodCard(carta);
		player1.getWorker1().setPos(map.getCell(0,0));
	}

	@Test
	public void chooseWorkerTest() {

		player1.chooseWorker(1);
		assertTrue(player1.getWorker1().equals(player1.getActiveWorker()));
		assertEquals(player1.getWorker1(), player1.getActiveWorker());
		assertNotEquals(player1.getWorker2(), player1.getActiveWorker());

		player1.chooseWorker(2);
		assertEquals(player1.getWorker2(), player1.getActiveWorker());
		assertNotEquals(player1.getWorker1(), player1.getActiveWorker());

	}

	@Test
	public void copyTest(){
		Player player2 = player1.clone();
		assertEquals(player2, player1);
	}

	@Test
	public void equalsTest(){
		Player player2 = new Player("Pluto");
		player2.setPlayerColor(Color.BLACK);
		assertNotEquals(player2, player1);

	}
}