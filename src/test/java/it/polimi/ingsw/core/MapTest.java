package it.polimi.ingsw.core;

import it.polimi.ingsw.util.Constants;
import org.junit.Before;
import org.junit.Test;

import it.polimi.ingsw.util.Color;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MapTest {
	private Map map;

	@Before
	public void testSetup(){
		map = new Map();
	}

	@Test
	public void getCellTest(){
		int x=0, y=0;
		Cell cell = map.getCell(x, y);
		assertEquals(map.getX(cell), 0);
		assertEquals(map.getY(cell), 0);
	}

	@Test
	public void equals() {
		Map map2 = new Map();
		Map map3 = new Map();

		// reflexive property
		assertTrue(map.equals(map));
		// symmetric property
		assertTrue(map.equals(map2));
		assertTrue(map2.equals(map2));
		// transitive property
		assertTrue(map2.equals(map3));
		assertTrue(map.equals(map3));

		// check inequality
		map2.getCell(0,0).getBuilding().incrementLevel();
		assertFalse(map.equals(map2));

		// check on the same type
		assertFalse(map.equals("I'm an ugly and sad string :("));
	}

	@Test (expected = IllegalArgumentException.class)
	public void illegalArgumentToGet() {
		Map map2 = new Map();

		try {
			map.getX(map2.getCell(0,0));
			fail();
		} catch (IllegalArgumentException e) {
			map.getY(map2.getCell(0,0));
			fail();
		}
	}
}