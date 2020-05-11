package it.polimi.ingsw.core;

import it.polimi.ingsw.util.Color;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CellTest {
	private Map map1;
	private Map map2;
	private Map map3;

	@Before
	public void setupTest() {
		map1 = new Map();
		map2 = new Map();
		map3 = new Map();
	}

	@Test
	public void setWorker() {
		Cell cell = map1.getCell(0,0);
		Player p = new Player("Sheldon Cooper");
		Worker w = new Worker(Color.RED,p,1);

		assertNull(cell.getWorker());
		cell.setWorker(w);
		assertNotNull(cell.getWorker());
	}

	@Test
	public void testEquals() {
		Player p = new Player("Sheldon Cooper");
		Worker w1 = new Worker(Color.RED,p,1);
		Worker w2 = new Worker(Color.RED,p,2);

		// reflexive property
		assertTrue(map1.getCell(0,0).equals(map1.getCell(0,0)));
		// symmetric property
		assertTrue(map1.getCell(0,0).equals(map2.getCell(0,0)));
		assertTrue(map2.getCell(0,0).equals(map1.getCell(0,0)));
		// transitive property
		assertTrue(map2.getCell(0,0).equals(map3.getCell(0,0)));
		assertTrue(map1.getCell(0,0).equals(map3.getCell(0,0)));
		// compare with null return false
		assertFalse(map1.getCell(0,0).equals(null));

		assertFalse(map1.getCell(0,0).equals(map2.getCell(0,1)));

		map2.getCell(0,0).getBuilding().incrementLevel();
		assertFalse(map1.getCell(0,0).equals(map2.getCell(0,0)));

		assertTrue(map1.getCell(1,1).equals(map2.getCell(1,1)));

		map2.getCell(1,1).getBuilding().setDome();
		assertFalse(map1.getCell(1,1).equals(map2.getCell(1,1)));

		assertTrue(map1.getCell(2,2).equals(map2.getCell(2,2)));

		map2.getCell(2,2).setWorker(w1);
		assertFalse(map1.getCell(2,2).equals(map2.getCell(2,2)));
		map1.getCell(2,2).setWorker(w2);
		assertTrue(map1.getCell(2,2).equals(map2.getCell(2,2)));
	}
}