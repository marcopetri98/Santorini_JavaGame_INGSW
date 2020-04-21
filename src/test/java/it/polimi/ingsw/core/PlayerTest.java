package it.polimi.ingsw.core;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class PlayerTest {
	private Player player1;

	@Before
	public void testSetup(){
		player1 = new Player("Pippo");
		player1.setPlayerColor(Color.RED);
	}

	@Test
	public void chooseWorkerTest() {

		player1.chooseWorker(1);
		assertEquals(player1.getWorker1(), player1.getActiveWorker());
		assertNotEquals(player1.getWorker2(), player1.getActiveWorker());

		player1.chooseWorker(2);
		assertEquals(player1.getWorker2(), player1.getActiveWorker());
		assertNotEquals(player1.getWorker1(), player1.getActiveWorker());

	}

	@Test
	public void copyTest(){
		Player player2 = player1.copy();
		assertEquals(player2, player1);
	}

	@Test
	public void equalsTest(){
		Player player2 = new Player("Pluto");
		player2.setPlayerColor(Color.BLACK);
		assertNotEquals(player2, player1);

	}
}