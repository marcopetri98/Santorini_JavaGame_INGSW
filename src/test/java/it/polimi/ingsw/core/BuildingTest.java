package it.polimi.ingsw.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class BuildingTest {
	private Map map = new Map();
	private int i,j=0;
	private Cell cell; //TODO: update cell fix

	@Test
	public void incrementLevelTest() {
		cell.getBuilding().incrementLevel();
		cell.getBuilding().incrementLevel();
		cell.getBuilding().incrementLevel();
		assertEquals(3, cell.getBuilding().getLevel());
		assertNotEquals(2, cell.getBuilding().getLevel());
	}
}