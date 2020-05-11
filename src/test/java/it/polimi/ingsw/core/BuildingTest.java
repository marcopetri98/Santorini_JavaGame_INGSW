package it.polimi.ingsw.core;

import org.junit.Before;
import org.junit.Test;

import it.polimi.ingsw.util.Color;

import static org.junit.Assert.*;

public class BuildingTest {
	private Building building;

	@Before
	public void testSetup(){
		building = new Building();
	}

	@Test
	public void incrementLevelTest() {
		building.incrementLevel();
		assertEquals(1,building.getLevel());

		building.incrementLevel();
		assertEquals(2,building.getLevel());

		building.incrementLevel();
		assertEquals(3,building.getLevel());
	}

	@Test (expected = IllegalStateException.class)
	public void illegalIncrement() {
		building.incrementLevel();
		building.incrementLevel();
		building.incrementLevel();
		building.incrementLevel();
	}

	@Test
	public void setDomeTest() {
		building.setDome();

		assertTrue(building.getDome());
	}

	@Test (expected = IllegalStateException.class)
	public void illegalSetDome() {
		building.setDome();
		building.setDome();
	}

	@Test
	public void equalsCorrect() {
		Building b1 = new Building();
		Building b2 = new Building();
		Building b3 = new Building();

		// reflexive property
		assertTrue(b1.equals(b1));
		// symmetric property
		assertTrue(b1.equals(b2));
		assertTrue(b2.equals(b1));
		// transitive property
		assertTrue(b2.equals(b3));
		assertTrue(b1.equals(b3));
		// compare with null return false
		assertFalse(b1.equals(null));

		b1.incrementLevel();
		assertFalse(b1.equals(b2));

		b2.incrementLevel();
		assertTrue(b1.equals(b2));

		b1.setDome();
		assertFalse(b1.equals(b2));

		b2.setDome();
		assertTrue(b1.equals(b2));
	}

	@Test
	public void cloneCorrect() {
		Building copy = building.clone();

		assertEquals(copy,building);
	}
}