package it.polimi.ingsw.core;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class MapTest {

	private Player player = new Player("Pippo");
	private Map map = new Map();
	private int i,j=0;
	private Cell cell; //TODO: update cell fix
	private Cell cell2;


	@Test
	public void moveableTestFreeCell() {
		//cell free: worker can move to it
		cell.setWorker(player.getWorker1());
		player.getWorker1().setPos(cell);
		assertTrue(map.moveable(cell, player.getWorker1()));
	}

	@Test
	public void moveableTestMoveUp() {
		////building in cell (0,0) level 1, worker in cell2(1,1) level 0: he can move to it
		cell.getBuilding().incrementLevel(); // building level == 1
		cell2.setWorker(player.getWorker1());
		player.getWorker1().setPos(cell2);
		assertTrue(map.moveable(cell, player.getWorker1()));
	}

	@Test
	public void moveableTestCannotMoveUp() {
		//building in cell (0,0) level 2, worker in cell2(1,1) level 0: he cannot move to it
		cell.getBuilding().incrementLevel(); // building level == 1
		cell.getBuilding().incrementLevel();
		cell2.setWorker(player.getWorker1());
		player.getWorker1().setPos(cell2);
		assertFalse(map.moveable(cell, player.getWorker1()));
	}

	@Test
	public void moveableTestMoveDown() {
		//building in cell2(1,1) level 1, worker in cell2: he can move to cell(0,0) (level==0)
		cell2.getBuilding().incrementLevel(); // building level == 1
		cell2.setWorker(player.getWorker1());
		player.getWorker1().setPos(cell2);
		assertTrue(map.moveable(cell, player.getWorker1()));
	}

	@Test
	public void moveableTestMoveDown2() {
		//building in cell2(1,1) level 2, worker in cell2: he can move to cell(0,0) (level==0)
		cell2.getBuilding().incrementLevel();
		cell2.getBuilding().incrementLevel(); // building level == 2
		cell2.setWorker(player.getWorker1());
		player.getWorker1().setPos(cell2);
		assertTrue(map.moveable(cell, player.getWorker1()));
		//TODO: ERROR
	}

	@Test
	public void moveableTestMoveToDome() {
		//building in cell2(1,1) level 3, worker in cell2; building in cell(0,0) level3+dome: worker cannot move to it
		cell2.getBuilding().incrementLevel();
		cell2.getBuilding().incrementLevel();
		cell2.getBuilding().incrementLevel(); // building level == 3
		cell2.setWorker(player.getWorker1());
		player.getWorker1().setPos(cell2);
		cell.getBuilding().incrementLevel();
		cell.getBuilding().incrementLevel();
		cell.getBuilding().incrementLevel();
		cell.getBuilding().setDome();
		assertFalse(map.moveable(cell, player.getWorker1()));
		//TODO: ERROR
	}

}