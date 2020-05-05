package it.polimi.ingsw.core;

import org.junit.Before;
import org.junit.Test;

import it.polimi.ingsw.util.Color;

import static org.junit.Assert.*;

public class BuildingTest {
	private Map map;
	private Player player1;
	private TypeBuild typeBuild;
	private Build build1;

	@Before
	public void testSetup(){
		map = new Map();
		player1 = new Player("Pippo");
		player1.setPlayerColor(Color.RED);
		typeBuild = TypeBuild.SIMPLE_BUILD;
		build1 = new Build(player1.getWorker1(), map.getCell(0,0), false, typeBuild);

	}

	@Test(expected = IllegalStateException.class)
	public void incrementLevelTest() throws Exception {
		assertEquals(0, map.getCell(0,0).getBuilding().getLevel());

		map.getCell(0,0).getBuilding().incrementLevel();
		map.getCell(0,0).getBuilding().incrementLevel();
		map.getCell(0,0).getBuilding().incrementLevel();
		assertEquals(3, map.getCell(0,0).getBuilding().getLevel());

		map.getCell(0,0).getBuilding().incrementLevel(); //should throw the exception
	}

	@Test(expected = IllegalStateException.class)
	public void setDomeTest() throws Exception {
		assertEquals(false,map.getCell(0,0).getBuilding().getDome());

		map.getCell(0,0).getBuilding().setDome();
		assertEquals(true, map.getCell(0,0).getBuilding().getDome());

		map.getCell(0,0).getBuilding().setDome(); //should throw the exception
	}
}