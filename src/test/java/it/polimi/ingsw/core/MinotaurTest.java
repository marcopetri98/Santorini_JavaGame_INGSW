package it.polimi.ingsw.core;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class MinotaurTest {
	private Map map;
	private TypeMove type;
	private Minotaur minotaur;
	private Player player;
	private Player opponent;
	int x,y,x1,y1; //player's workers positions
	int h,k,h1,k1; //opponent's workers positions
	int i,j; //simple move position
	int s,t; //new position of the opponent worker forced

	@Before
	public void testSetup(){
		map = new Map();
		type = TypeMove.CONDITIONED_MOVE; //typeMove of minotaur according to our implementation
		player = new Player("Pippo");
		player.setPlayerColor(Color.RED);
		opponent = new Player("Pluto");
		opponent.setPlayerColor(Color.BLACK);
		minotaur = new Minotaur(player);
	}

	/**
	 * Worker1 in (1,1) (level 2) and Worker2 in (1,2), opponent worker1 in (2,2) (level 3) and worker2 in (2,1) (level 1), building with dome in (2,0), building level 3 in (1,0), building level 1 in (0,0), dome only in (0,1): it should return only 5 cells which I compare "manually" with the returned arrayList of the checkMove
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

		assertEquals(5, minotaur.checkMove(map, player.getWorker1(), type).size());

		i=0; j=0;
		Move newMove = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		assertTrue(minotaur.checkMove(map, player.getWorker1(), type).contains(newMove));

		i=1; j=0;
		Move newMove1 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(minotaur.checkMove(map, player.getWorker1(), type).contains(newMove1));

		i=0; j=2;
		Move newMove2 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(minotaur.checkMove(map, player.getWorker1(), type).contains(newMove2));

		s=3; t=1;
		Move newMove3 = new Move(TypeMove.CONDITIONED_MOVE, map.getCell(x, y), map.getCell(h1, k1), player.getWorker1()); //moving to position of opponent worker2
		newMove3.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(h1,k1), map.getCell(s,t), map.getCell(h1, k1).getWorker())); //pushing backwards opponent worker2
		assertTrue(minotaur.checkMove(map, player.getWorker1(), type).contains(newMove3));

		s=3; t=3;
		Move newMove4 = new Move(TypeMove.CONDITIONED_MOVE, map.getCell(x, y), map.getCell(h, k), player.getWorker1()); //moving to position of opponent worker1
		newMove4.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(h,k), map.getCell(s,t), map.getCell(h, k).getWorker())); //pushing backwards opponent worker1
		assertTrue(minotaur.checkMove(map, player.getWorker1(), type).contains(newMove4));

	}

	/**
	 * Worker1 in (1,1) (level 1) and Worker2 in (2,2), opponent worker1 in (0,0) (level 0) and worker2 in (1,2) (level 0), building with dome in (2,0), building level 3 in (1,0), building level 2 in (2,1), dome only in (0,1), building level 3 in (1,3): it should return only 3 cells which I compare "manually" with the returned arrayList of the checkMove;
	 * Worker1 moves down and take the position of the opponent worker2 (level 0) which is pushed backwards in a bulding level 3.
	 */
	@Test
	public void checkMoveTestOneOpponentWorkerInCorner() {
		x=1; y=1; x1=2; y1=2;
		h=0; k=0; h1=1; k1=2;
		map.getCell(x, y).setWorker(player.getWorker1());
		player.getWorker1().setPos(map.getCell(x, y));
		map.getCell(x1, y1).setWorker(player.getWorker2());
		player.getWorker2().setPos(map.getCell(x1, y1));
		map.getCell(h, k).setWorker(opponent.getWorker1());
		opponent.getWorker1().setPos(map.getCell(h, k));
		map.getCell(h1, k1).setWorker(opponent.getWorker2());
		opponent.getWorker2().setPos(map.getCell(h1, k1));

		map.getCell(x, y).getBuilding().incrementLevel();

		map.getCell(1, 0).getBuilding().incrementLevel();
		map.getCell(1, 0).getBuilding().incrementLevel();
		map.getCell(1, 0).getBuilding().incrementLevel();

		map.getCell(2, 0).getBuilding().incrementLevel();
		map.getCell(2, 0).getBuilding().incrementLevel();
		map.getCell(2, 0).getBuilding().incrementLevel();
		map.getCell(2, 0).getBuilding().setDome();

		map.getCell(2, 1).getBuilding().incrementLevel();
		map.getCell(2, 1).getBuilding().incrementLevel();

		map.getCell(1, 3).getBuilding().incrementLevel();
		map.getCell(1, 3).getBuilding().incrementLevel();
		map.getCell(1, 3).getBuilding().incrementLevel();

		map.getCell(0,1).getBuilding().setDome(); //dome only: due to Atlas (hypothetically)

		assertEquals(3, minotaur.checkMove(map, player.getWorker1(), type).size());

		i=2; j=1;
		Move newMove = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		assertTrue(minotaur.checkMove(map, player.getWorker1(), type).contains(newMove));

		i=0; j=2;
		Move newMove1 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(minotaur.checkMove(map, player.getWorker1(), type).contains(newMove1));

		s=1; t=3;
		Move newMove2 = new Move(TypeMove.CONDITIONED_MOVE, map.getCell(x,y), map.getCell(h1,k1), player.getWorker1());
		newMove2.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(h1,k1), map.getCell(s,t), map.getCell(h1, k1).getWorker()));
		assertTrue(minotaur.checkMove(map, player.getWorker1(), type).contains(newMove2));

	}

	/**
	 * Worker1 in (1,1) (level 1) and Worker2 in (2,2), opponent worker1 in (0,0) (level 0) and worker2 in (1,2) (level 2), building with dome in (2,0), building level 3 in (1,0), building level 2 in (2,1), dome only in (0,1): it should return only 3 cells which I compare "manually" with the returned arrayList of the checkMove;
	 * Worker1 moves up and take the position of the opponent worker2 (level 2) which is pushed backwards in a cell level 0.
	 */
	@Test
	public void checkMoveTestOneOpponentWorkerInCorner2() {
		x=1; y=1; x1=2; y1=2;
		h=0; k=0; h1=1; k1=2;
		map.getCell(x, y).setWorker(player.getWorker1());
		player.getWorker1().setPos(map.getCell(x, y));
		map.getCell(x1, y1).setWorker(player.getWorker2());
		player.getWorker2().setPos(map.getCell(x1, y1));
		map.getCell(h, k).setWorker(opponent.getWorker1());
		opponent.getWorker1().setPos(map.getCell(h, k));
		map.getCell(h1, k1).setWorker(opponent.getWorker2());
		opponent.getWorker2().setPos(map.getCell(h1, k1));

		map.getCell(x, y).getBuilding().incrementLevel();

		map.getCell(h1, k1).getBuilding().incrementLevel();
		map.getCell(h1, k1).getBuilding().incrementLevel();

		map.getCell(1, 0).getBuilding().incrementLevel();
		map.getCell(1, 0).getBuilding().incrementLevel();
		map.getCell(1, 0).getBuilding().incrementLevel();

		map.getCell(2, 0).getBuilding().incrementLevel();
		map.getCell(2, 0).getBuilding().incrementLevel();
		map.getCell(2, 0).getBuilding().incrementLevel();
		map.getCell(2, 0).getBuilding().setDome();

		map.getCell(2, 1).getBuilding().incrementLevel();
		map.getCell(2, 1).getBuilding().incrementLevel();

		map.getCell(0,1).getBuilding().setDome(); //dome only: due to Atlas (hypothetically)

		assertEquals(3, minotaur.checkMove(map, player.getWorker1(), type).size());

		i=2; j=1;
		Move newMove = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		assertTrue(minotaur.checkMove(map, player.getWorker1(), type).contains(newMove));

		i=0; j=2;
		Move newMove1 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(minotaur.checkMove(map, player.getWorker1(), type).contains(newMove1));

		s=1; t=3;
		Move newMove2 = new Move(TypeMove.CONDITIONED_MOVE, map.getCell(x,y), map.getCell(h1,k1), player.getWorker1());
		newMove2.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(h1,k1), map.getCell(s,t), map.getCell(h1, k1).getWorker()));
		assertTrue(minotaur.checkMove(map, player.getWorker1(), type).contains(newMove2));

	}

}