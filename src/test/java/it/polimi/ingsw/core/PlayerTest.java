package it.polimi.ingsw.core;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class PlayerTest {
	private Player player = new Player("Pippo");

	@Test
	public void chooseWorkerTest() {

		player.chooseWorker(1);
		assertEquals(player.getWorker1(), player.getActiveWorker());
		assertNotEquals(player.getWorker2(), player.getActiveWorker());

		player.chooseWorker(2);
		assertEquals(player.getWorker2(), player.getActiveWorker());
		assertNotEquals(player.getWorker1(), player.getActiveWorker());

	}
}