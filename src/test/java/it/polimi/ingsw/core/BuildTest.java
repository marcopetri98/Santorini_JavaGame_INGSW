package it.polimi.ingsw.core;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class BuildTest {
	private Player playerA = new Player("Pippo");
	private TypeBuild typeBuild = TypeBuild.SIMPLE_BUILD;
	private Cell cell; //TODO: update cell fix
	private Worker workerA = playerA.getWorker1();
	private boolean dome = false;
	private Build build = new Build(workerA, cell, dome, typeBuild);

	private Player playerB = new Player("Pluto");
	//same typeBuild, for ex.
	private Cell cell2;
	private Worker workerB = playerB.getWorker1();
	//same value of dome, for ex
	private Build other = new Build(workerB, cell2, dome, typeBuild);

	@Test
	public void testEquals() {
		assertFalse(build.equals(other));
	}
}