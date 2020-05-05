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
	private List<List<Cell>> cells;
	int x,y;

	@Before
	public void testSetup(){
		map = new Map();
		cells = new ArrayList<>();
		for (int i = 0; i < Constants.MAP_SIDE; i++) {
			cells.add(new ArrayList<Cell>());
			for (int j = 0; j < Constants.MAP_SIDE; j++) {
				cells.get(i).add(new Cell(map));
			}
		}

	}

	@Test
	public void getCellTest(){
		x=0; y=0;
		Cell cella = map.getCell(x, y);
		assertEquals(map.getX(cella), 0);

		//assertEquals(cells.get(x).get(y), map.getCell(x,y));
	}
}