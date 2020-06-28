package it.polimi.ingsw.core;

import org.junit.Before;
import org.junit.Test;

import it.polimi.ingsw.util.Color;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class WorkerTest {
	private Player player;
	private Map map;

	@Before
	public void testSetup(){
		map = new Map();
		player = new Player("Erwin",0);
		player.setPlayerColor(Color.RED);
	}

	@Test
	public void setPosTest() {
		player.getWorker1().setPos(map.getCell(0,0));
		assertEquals(map.getCell(0,0), player.getWorker1().getPos());

		player.getWorker1().setPos(map.getCell(1,0));
		player.getWorker1().setPos(map.getCell(2,0));
		player.getWorker1().setPos(map.getCell(3,0));
		assertEquals(map.getCell(3,0), player.getWorker1().getPos());
	}

	@Test
	public void getLastPos() {
		player.getWorker1().setPos(map.getCell(0,0));
		player.getWorker1().setPos(map.getCell(1,0));
		player.getWorker1().setPos(map.getCell(2,0));
		player.getWorker1().setPos(map.getCell(3,0));
		assertEquals(map.getCell(2,0), player.getWorker1().getLastPos());
	}

	@Test
	public void setLastBuildPos() {
		player.getWorker1().setLastBuildPos(map.getCell(0,0));
		assertEquals(map.getCell(0,0), player.getWorker1().getLastBuildPos());
	}

	@Test
	public void equalsCorrect() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Player erwin = new Player("Erwin",0);
		erwin.setPlayerColor(Color.RED);
		Player erwin2 = new Player("Erwin",0);
		erwin2.setPlayerColor(Color.RED);
		Player paolo = new Player("Paolo",20);
		paolo.setPlayerColor(Color.RED);
		Player blue = new Player("BLUE",30);
		blue.setPlayerColor(Color.BLUE);
		Worker worker1 = player.getWorker1();
		Worker erwinWorker1 = erwin.getWorker1();
		Worker erwin2Worker1 = erwin2.getWorker1();
		Worker paoloWorker1 = paolo.getWorker1();
		Worker blueWorker1 = blue.getWorker1();

		worker1.setPos(map.getCell(0,0));
		erwinWorker1.setPos(map.getCell(0,0));
		erwin2Worker1.setPos(map.getCell(0,0));

		// reflexive property
		assertTrue(erwinWorker1.equals(erwinWorker1));
		// symmetric property
		assertTrue(erwinWorker1.equals(worker1));
		assertTrue(worker1.equals(erwinWorker1));
		// transitive property
		assertTrue(worker1.equals(erwin2Worker1));
		assertTrue(erwinWorker1.equals(erwin2Worker1));
		// compare with null return false
		assertFalse(erwinWorker1.equals(null));

		assertFalse(erwinWorker1.equals(paoloWorker1));
		assertFalse(erwinWorker1.equals(blueWorker1));

		worker1.setPos(map.getCell(0,1));
		assertFalse(erwinWorker1.equals(worker1));
	}
}