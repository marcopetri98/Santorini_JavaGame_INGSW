package it.polimi.ingsw.core;

import it.polimi.ingsw.core.gods.Prometheus;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class PrometheusTest {
	private Map map;
	private TypeMove typeM;
	private TypeBuild typeB;
	private Prometheus prometheus;
	private Player player;
	private Player opponent;
	int x,y,x1,y1; //player's workers positions
	int h,k,h1,k1; //opponent's workers positions
	int i,j; //simple move/build position

	@Before
	public void testSetup(){
		map = new Map();
		player = new Player("Pippo");
		player.setPlayerColor(Color.RED);
		opponent = new Player("Pluto");
		opponent.setPlayerColor(Color.BLACK);
		prometheus = new Prometheus(player);
	}

	/**
	 * Worker1 in (1,1) (level 2) and Worker2 in (1,2), opponent worker1 in (2,2) (level 3) and worker2 in (2,1) (level 1), building with dome in (2,0), building level 3 in (1,0), building level 1 in (0,0), dome only in (0,1): it should return only 6 cells which I compare "manually" with the returned arrayList of the checkMove
	 */
	@Test
	public void checkMoveTestGeneral() {
		x=1; y=1; x1=1; y1=2;
		h=2; k=2; h1=2; k1=1;
		map.getCell(x, y).getBuilding().incrementLevel();
		map.getCell(x, y).getBuilding().incrementLevel();
		map.getCell(x, y).setWorker(player.getWorker1());
		player.getWorker1().setPos(map.getCell(x, y));
		map.getCell(x1, y1).setWorker(player.getWorker2());
		player.getWorker2().setPos(map.getCell(x1, y1));
		map.getCell(h, k).getBuilding().incrementLevel();
		map.getCell(h, k).getBuilding().incrementLevel();
		map.getCell(h, k).getBuilding().incrementLevel();
		map.getCell(h, k).setWorker(opponent.getWorker1());
		opponent.getWorker1().setPos(map.getCell(h, k));
		map.getCell(h1, k1).setWorker(opponent.getWorker2());
		opponent.getWorker2().setPos(map.getCell(h1, k1));

		map.getCell(2, 0).getBuilding().incrementLevel();
		map.getCell(2, 0).getBuilding().incrementLevel();
		map.getCell(2, 0).getBuilding().incrementLevel();
		map.getCell(2, 0).getBuilding().setDome();

		map.getCell(1, 0).getBuilding().incrementLevel();
		map.getCell(1, 0).getBuilding().incrementLevel();
		map.getCell(1, 0).getBuilding().incrementLevel();

		map.getCell(0, 0).getBuilding().incrementLevel();

		map.getCell(0,1).getBuilding().setDome(); //dome only: due to Atlas (hypothetically)

		assertEquals(3, prometheus.checkMove(map, player.getWorker1()).size());

		i=0; j=0;
		Move newMove3 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		assertTrue(prometheus.checkMove(map, player.getWorker1()).contains(newMove3));

		i=0; j=2;
		Move newMove4 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		assertTrue(prometheus.checkMove(map, player.getWorker1()).contains(newMove4));

		i=1; j=0;
		Move newMove5 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		assertTrue(prometheus.checkMove(map, player.getWorker1()).contains(newMove5));

	}

	/**
	 * Worker1 in (1,1) (level 2) and Worker2 in (1,2), opponent worker1 in (2,2) (level 3) and worker2 in (2,1) (level 1), building with dome in (2,0), building level 3 in (1,0), building level 1 in (0,0), dome only in (0,1): it should return only 3 cells which I compare "manually" with the returned arrayList of the checkBuild
	 */
	@Test
	public void checkBuildTestGeneral() {
		x=1; y=1; x1=1; y1=2;
		h=2; k=2; h1=2; k1=1;
		map.getCell(x, y).getBuilding().incrementLevel();
		map.getCell(x, y).getBuilding().incrementLevel();
		map.getCell(x, y).setWorker(player.getWorker1());
		player.getWorker1().setPos(map.getCell(x, y));
		map.getCell(x1, y1).setWorker(player.getWorker2());
		player.getWorker2().setPos(map.getCell(x1, y1));
		map.getCell(h, k).getBuilding().incrementLevel();
		map.getCell(h, k).getBuilding().incrementLevel();
		map.getCell(h, k).getBuilding().incrementLevel();
		map.getCell(h, k).setWorker(opponent.getWorker1());
		opponent.getWorker1().setPos(map.getCell(h, k));
		map.getCell(h1, k1).setWorker(opponent.getWorker2());
		opponent.getWorker2().setPos(map.getCell(h1, k1));

		map.getCell(2, 0).getBuilding().incrementLevel();
		map.getCell(2, 0).getBuilding().incrementLevel();
		map.getCell(2, 0).getBuilding().incrementLevel();
		map.getCell(2, 0).getBuilding().setDome();

		map.getCell(1, 0).getBuilding().incrementLevel();
		map.getCell(1, 0).getBuilding().incrementLevel();
		map.getCell(1, 0).getBuilding().incrementLevel();

		map.getCell(0, 0).getBuilding().incrementLevel();

		map.getCell(0,1).getBuilding().setDome(); //dome only: due to Atlas (hypothetically)

		assertEquals(3, prometheus.checkBuild(map, player.getWorker1()).size());

		i=0; j=0;
		Build newBuild = new Build(player.getWorker1(), map.getCell(i, j), false, TypeBuild.SIMPLE_BUILD);
		assertTrue(prometheus.checkBuild(map, player.getWorker1()).contains(newBuild));

		i=1; j=0;
		Build newBuild1= new Build(player.getWorker1(), map.getCell(i, j), true, TypeBuild.SIMPLE_BUILD);
		assertTrue(prometheus.checkBuild(map, player.getWorker1()).contains(newBuild1));

		i=0; j=2;
		Build newBuild2 = new Build(player.getWorker1(), map.getCell(i, j), false, TypeBuild.SIMPLE_BUILD);
		assertTrue(prometheus.checkBuild(map, player.getWorker1()).contains(newBuild2));

	}
}