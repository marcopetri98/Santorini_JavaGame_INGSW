package it.polimi.ingsw.core;

import it.polimi.ingsw.core.gods.Apollo;
import it.polimi.ingsw.core.gods.GodCard;
import it.polimi.ingsw.core.gods.Prometheus;
import org.junit.Before;
import org.junit.Test;

import it.polimi.ingsw.util.Color;

import static org.junit.Assert.*;

public class PlayerTest {
	private Player player1;
	private Player player2;
	private Player player3;

	@Before
	public void testSetup(){
		player1 = new Player("Topolino");
		player1.setPlayerColor(Color.RED);
		GodCard card = new Apollo();
		player1.setGodCard(card);
	}
	private void resetVariables() {
		player1 = null;
		player2 = null;
		player3 = null;
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
	public void setGodCardTest() {
		player1 = new Player("Aldo");
		player1.setGodCard(new Apollo());
		assertNotNull(player1.getCard());
	}

	@Test
	public void setPlayerColor() {
		player1 = new Player("Aldo");
		player1.setPlayerColor(Color.RED);
		assertNotNull(player1.getWorker1());
	}

	@Test
	public void resetLocking() {
		player1 = new Player("Aldo");
		player1.resetLocking();
		assertFalse(player1.isWorkerLocked());
	}

	@Test
	public void equalsTest(){
		player1 = new Player("Aldo");
		player1.setPlayerColor(Color.GREEN);
		player1.setGodCard(new Apollo());
		player2 = new Player("Aldo");
		player2.setPlayerColor(Color.GREEN);
		player2.setGodCard(new Apollo());
		player3 = new Player("Aldo");
		player3.setPlayerColor(Color.GREEN);
		player3.setGodCard(new Apollo());

		// reflexive property
		assertTrue(player1.equals(player1));
		// symmetric property
		assertTrue(player1.equals(player2));
		assertTrue(player2.equals(player1));
		// transitive property
		assertTrue(player2.equals(player3));
		assertTrue(player1.equals(player3));
		// compare with null return false
		assertFalse(player1.equals(null));

		player2 = new Player("Giovanni");
		player2.setPlayerColor(Color.GREEN);
		assertFalse(player1.equals(player2));
		player2 = new Player("Aldo");
		player2.setPlayerColor(Color.RED);
		assertFalse(player1.equals(player2));
		player2 = new Player("Aldo");
		player2.setPlayerColor(Color.GREEN);
		player2.setGodCard(new Prometheus());
		assertFalse(player1.equals(player2));
	}
}