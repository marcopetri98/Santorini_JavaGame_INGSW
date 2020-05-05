package it.polimi.ingsw.core;

import org.junit.Before;
import org.junit.Test;

import it.polimi.ingsw.util.Color;

import static org.junit.Assert.*;

public class WorkerTest {

	private Player player1;
	private Map map;

	@Before
	public void testSetup(){
		map = new Map();
		player1 = new Player("player1");
		player1.setPlayerColor(Color.RED);
		player1.chooseWorker(1);

	}

	@Test
	public void getPosTest() {
		assertNull(player1.getWorker1().getPos());
	}

	@Test
	public void setPosTest() {
		player1.getWorker1().setPos(map.getCell(0,0));
		assertEquals(map.getCell(0,0), player1.getWorker1().getPos());

		player1.getWorker1().setPos(map.getCell(1,0));
		player1.getWorker1().setPos(map.getCell(2,0));
		player1.getWorker1().setPos(map.getCell(3,0));
		assertEquals(map.getCell(3,0), player1.getWorker1().getPos());
	}

	@Test
	public void getLastPos() {
		player1.getWorker1().setPos(map.getCell(0,0));
		player1.getWorker1().setPos(map.getCell(1,0));
		player1.getWorker1().setPos(map.getCell(2,0));
		player1.getWorker1().setPos(map.getCell(3,0));
		assertEquals(map.getCell(2,0), player1.getWorker1().getLastPos());
	}

	@Test
	public void getLastBuildPos() {
		assertNull(player1.getWorker1().getLastBuildPos());
	}

	@Test
	public void setLastBuildPos() {
		player1.getWorker1().setLastBuildPos(map.getCell(0,0));
		assertEquals(map.getCell(0,0), player1.getWorker1().getLastBuildPos());
	}
}