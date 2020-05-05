package it.polimi.ingsw.core;

import org.junit.Before;
import org.junit.Test;

import it.polimi.ingsw.util.Color;

import static org.junit.Assert.*;

public class BuildTest {
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

	@Test
	public void copyTest() {
		Build build2 = build1.copy();
		assertEquals(build2, build1);
	}

	@Test
	public void equalsTest() {
		Build build2 = build1.copy();
		assertTrue(build2.equals(build1)); //same build

		Build build3 = new Build(player1.getWorker1(), map.getCell(1,1), false, typeBuild);
		assertFalse(build3.equals(build1)); //different cell

		Build build4 = new Build(player1.getWorker1(), map.getCell(0,0), true, typeBuild);
		assertFalse(build4.equals(build1)); //in build4 there is the possibility to build  a dome

		TypeBuild typeBuild2 = TypeBuild.CONDITIONED_BUILD;
		Build build5 = new Build(player1.getWorker1(), map.getCell(0,0), false, typeBuild2);
		assertFalse(build5.equals(build1)); //different TypeBuild

		Build build6 = new Build(player1.getWorker2(), map.getCell(1,1), false, typeBuild);
		assertFalse(build6.equals(build1)); //different player worker

	}
}