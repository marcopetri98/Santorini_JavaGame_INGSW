package it.polimi.ingsw.core;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ApolloTest {
	private Map map;
	private TypeMove type;
	private Apollo apollo;
	private Player player;
	private Player opponent;
	int x,y,x1,y1; //player's workers positions
	int h,k,h1,k1; //opponent's workers positions
	int i,j; //simple move position

	@Before
	public void testSetup(){
		map = new Map();
		type = TypeMove.CONDITIONED_MOVE; //typeMove of Apollo according to our implementation
		player = new Player("Pippo");
		player.setPlayerColor(Color.RED);
		opponent = new Player("Pluto");
		opponent.setPlayerColor(Color.BLACK);
		apollo = new Apollo(player);
	}

	/**
	 * Worker1 in (0,0) (level 0), opponent worker in (1,1) (level 0): it should return only the 3 cells adjacent (0,0) which I compare "manually" with the returned arrayList of the checkMove
	 */
	@Test
	public void checkMoveTestCorner() {
		x=0; y=0; x1=3; y1=3;
		h=1; k=1; h1=4; k1=4;
		map.getCell(x, y).setWorker(player.getWorker1());
		player.getWorker1().setPos(map.getCell(x, y));
		map.getCell(x1, y1).setWorker(player.getWorker2());
		player.getWorker2().setPos(map.getCell(x1, y1));
		map.getCell(h, k).setWorker(opponent.getWorker1());
		opponent.getWorker1().setPos(map.getCell(h, k));
		map.getCell(h1, k1).setWorker(opponent.getWorker2());
		opponent.getWorker2().setPos(map.getCell(h1, k1));

		assertEquals(3, apollo.checkMove(map, player.getWorker1(), type).size());

		Move newMove = new Move(TypeMove.CONDITIONED_MOVE, map.getCell(x, y), map.getCell(h, k), player.getWorker1()); //move due to Apollo's power
		newMove.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(h,k), map.getCell(x,y), map.getCell(h, k).getWorker())); //opponent's worker bounded to swipe with player's worker1
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove));

		i=0; j=1;
		Move newMove1 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove1));

		i=1; j=0;
		Move newMove2 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove2));

	}

	/**
	 * Worker1 in (1,1) (level 0), opponent worker in (2,2) (level 0): it should return only the 8 cells adjacent (1,1) which I compare "manually" with the returned arrayList of the checkMove.
	 *
	 */
	@Test
	public void checkMoveTestNotCorner() {
		x=1; y=1; x1=1; y1=4;
		h=2; k=2; h1=2; k1=4;
		map.getCell(x, y).setWorker(player.getWorker1());
		player.getWorker1().setPos(map.getCell(x, y));
		map.getCell(x1, y1).setWorker(player.getWorker2());
		player.getWorker2().setPos(map.getCell(x1, y1));
		map.getCell(h, k).setWorker(opponent.getWorker1());
		opponent.getWorker1().setPos(map.getCell(h, k));
		map.getCell(h1, k1).setWorker(opponent.getWorker2());
		opponent.getWorker2().setPos(map.getCell(h1, k1));

		assertEquals(8, apollo.checkMove(map, player.getWorker1(), type).size());

		Move newMove = new Move(TypeMove.CONDITIONED_MOVE, map.getCell(x, y), map.getCell(h, k), player.getWorker1()); //move due to Apollo's power
		newMove.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(h,k), map.getCell(x,y), map.getCell(h, k).getWorker())); //opponent's worker bounded to swipe with player's worker1
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove));

		i=0; j=0;
		Move newMove1 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove1));

		i=0; j=1;
		Move newMove2 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove2));

		i=0; j=2;
		Move newMove3 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove3));

		i=1; j=0;
		Move newMove4 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove4));

		i=1; j=2;
		Move newMove5 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove5));

		i=2; j=0;
		Move newMove6 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove6));

		i=2; j=1;
		Move newMove7 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove7));

	}

	/**
	 * Worker1 in (1,1) (level 0), opponent worker in (2,2) (level 3): it should return only the 7 cells adjacent (1,1) which I compare "manually" with the returned arrayList of the checkMove.
	 * Worker1 cannot move up in a building with a difference in height more than 1.
	 */
	@Test
	public void checkMoveTestNotCornerDifferenceInHeight() {
		x=1; y=1; x1=1; y1=4;
		h=2; k=2; h1=2; k1=4;
		map.getCell(x, y).setWorker(player.getWorker1());
		player.getWorker1().setPos(map.getCell(x, y));
		map.getCell(x1, y1).setWorker(player.getWorker2());
		player.getWorker2().setPos(map.getCell(x1, y1));
		map.getCell(h, k).getBuilding().incrementLevel();
		map.getCell(h, k).getBuilding().incrementLevel();
		map.getCell(h, k).getBuilding().incrementLevel(); //(h,k) level 3
		map.getCell(h, k).setWorker(opponent.getWorker1());
		opponent.getWorker1().setPos(map.getCell(h, k));
		map.getCell(h1, k1).setWorker(opponent.getWorker2());
		opponent.getWorker2().setPos(map.getCell(h1, k1));

		assertEquals(7, apollo.checkMove(map, player.getWorker1(), type).size());

		Move newMove = new Move(TypeMove.CONDITIONED_MOVE, map.getCell(x, y), map.getCell(h, k), player.getWorker1()); //move due to Apollo's power
		newMove.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(h,k), map.getCell(x,y), map.getCell(h, k).getWorker())); //opponent's worker bounded to swipe with player's worker1
		assertFalse(apollo.checkMove(map, player.getWorker1(), type).contains(newMove)); //Worker1 cannot move up in a building with a difference in height more than 1.

		i=0; j=0;
		Move newMove1 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove1));

		i=0; j=1;
		Move newMove2 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove2));

		i=0; j=2;
		Move newMove3 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove3));

		i=1; j=0;
		Move newMove4 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove4));

		i=1; j=2;
		Move newMove5 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove5));

		i=2; j=0;
		Move newMove6 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove6));

		i=2; j=1;
		Move newMove7 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove7));

	}

	/**
	 * Worker1 in (1,1) (level 3), opponent worker in (2,2) (level 0): it should return only the 8 cells adjacent (1,1) which I compare "manually" with the returned arrayList of the checkMove
	 */
	@Test
	public void checkMoveTestNotCornerDifferenceInHeight2() {
		x=1; y=1; x1=1; y1=4;
		h=2; k=2; h1=2; k1=4;
		map.getCell(x, y).getBuilding().incrementLevel();
		map.getCell(x, y).getBuilding().incrementLevel();
		map.getCell(x, y).getBuilding().incrementLevel(); //(x,y) level 3
		map.getCell(x, y).setWorker(player.getWorker1());
		player.getWorker1().setPos(map.getCell(x, y));
		map.getCell(x1, y1).setWorker(player.getWorker2());
		player.getWorker2().setPos(map.getCell(x1, y1));
		map.getCell(h, k).setWorker(opponent.getWorker1());
		opponent.getWorker1().setPos(map.getCell(h, k));
		map.getCell(h1, k1).setWorker(opponent.getWorker2());
		opponent.getWorker2().setPos(map.getCell(h1, k1));

		assertEquals(8, apollo.checkMove(map, player.getWorker1(), type).size());

		Move newMove = new Move(TypeMove.CONDITIONED_MOVE, map.getCell(x, y), map.getCell(h, k), player.getWorker1()); //move due to Apollo's power
		newMove.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(h,k), map.getCell(x,y), map.getCell(h, k).getWorker())); //opponent's worker bounded to swipe with player's worker1
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove));

		i=0; j=0;
		Move newMove1 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove1));

		i=0; j=1;
		Move newMove2 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove2));

		i=0; j=2;
		Move newMove3 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove3));

		i=1; j=0;
		Move newMove4 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove4));

		i=1; j=2;
		Move newMove5 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove5));

		i=2; j=0;
		Move newMove6 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove6));

		i=2; j=1;
		Move newMove7 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove7));

	}

	/**
	 * Worker1 in (1,1) (level 2) and Worker2 in (1,2), opponent worker1 in (2,2) (level 3) and worker2 in (2,1) (level 1), building with dome in (2,0), building level 3 in (1,0), building level 1 in (0,0), dome only in (0,1): it should return only the 5 cells adjacent (1,1) which I compare "manually" with the returned arrayList of the checkMove
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

		assertEquals(5, apollo.checkMove(map, player.getWorker1(), type).size());

		Move newMove = new Move(TypeMove.CONDITIONED_MOVE, map.getCell(x, y), map.getCell(h, k), player.getWorker1()); //move due to Apollo's power
		newMove.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(h,k), map.getCell(x,y), map.getCell(h, k).getWorker())); //opponent's worker1 bounded to swipe with player's worker1
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove));

		i=0; j=0;
		Move newMove1 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove1));

		i=0; j=1;
		Move newMove2 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertFalse(apollo.checkMove(map, player.getWorker1(), type).contains(newMove2));

		i=0; j=2;
		Move newMove3 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove3));

		i=1; j=0;
		Move newMove4 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove4));

		i=1; j=2;
		Move newMove5 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertFalse(apollo.checkMove(map, player.getWorker1(), type).contains(newMove5));

		i=2; j=0;
		Move newMove6 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertFalse(apollo.checkMove(map, player.getWorker1(), type).contains(newMove6));

		i=2; j=1;
		Move newMove7 = new Move(TypeMove.CONDITIONED_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		newMove7.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(i,j), map.getCell(x,y), map.getCell(i, j).getWorker())); //opponent's worker2 bounded to swipe with player's worker1
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove7));

	}

	@Test
	public void checkMoveTestCorner2() {
		x=4; y=4; x1=0; y1=0;
		h=3; k=3; h1=1; k1=1;
		map.getCell(x, y).setWorker(player.getWorker1());
		player.getWorker1().setPos(map.getCell(x, y));
		map.getCell(x1, y1).setWorker(player.getWorker2());
		player.getWorker2().setPos(map.getCell(x1, y1));
		map.getCell(h, k).setWorker(opponent.getWorker1());
		opponent.getWorker1().setPos(map.getCell(h, k));
		map.getCell(h1, k1).setWorker(opponent.getWorker2());
		opponent.getWorker2().setPos(map.getCell(h1, k1));

		assertEquals(3, apollo.checkMove(map, player.getWorker1(), type).size());

		Move newMove = new Move(TypeMove.CONDITIONED_MOVE, map.getCell(x, y), map.getCell(h, k), player.getWorker1()); //move due to Apollo's power
		newMove.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(h,k), map.getCell(x,y), map.getCell(h, k).getWorker())); //opponent's worker bounded to swipe with player's worker1
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove));

		i=4; j=3;
		Move newMove1 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove1));

		i=3; j=4;
		Move newMove2 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove2));

	}

	@Test
	public void checkMoveTestCorner3() {
		x=4; y=0; x1=0; y1=0;
		h=3; k=1; h1=1; k1=1;
		map.getCell(x, y).setWorker(player.getWorker1());
		player.getWorker1().setPos(map.getCell(x, y));
		map.getCell(x1, y1).setWorker(player.getWorker2());
		player.getWorker2().setPos(map.getCell(x1, y1));
		map.getCell(h, k).setWorker(opponent.getWorker1());
		opponent.getWorker1().setPos(map.getCell(h, k));
		map.getCell(h1, k1).setWorker(opponent.getWorker2());
		opponent.getWorker2().setPos(map.getCell(h1, k1));

		assertEquals(3, apollo.checkMove(map, player.getWorker1(), type).size());

		Move newMove = new Move(TypeMove.CONDITIONED_MOVE, map.getCell(x, y), map.getCell(h, k), player.getWorker1()); //move due to Apollo's power
		newMove.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(h,k), map.getCell(x,y), map.getCell(h, k).getWorker())); //opponent's worker bounded to swipe with player's worker1
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove));

		i=3; j=0;
		Move newMove1 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove1));

		i=4; j=1;
		Move newMove2 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove2));

	}

	@Test
	public void checkMoveTestCorner4() {
		x=0; y=4; x1=0; y1=0;
		h=1; k=3; h1=1; k1=1;
		map.getCell(x, y).setWorker(player.getWorker1());
		player.getWorker1().setPos(map.getCell(x, y));
		map.getCell(x1, y1).setWorker(player.getWorker2());
		player.getWorker2().setPos(map.getCell(x1, y1));
		map.getCell(h, k).setWorker(opponent.getWorker1());
		opponent.getWorker1().setPos(map.getCell(h, k));
		map.getCell(h1, k1).setWorker(opponent.getWorker2());
		opponent.getWorker2().setPos(map.getCell(h1, k1));

		assertEquals(3, apollo.checkMove(map, player.getWorker1(), type).size());

		Move newMove = new Move(TypeMove.CONDITIONED_MOVE, map.getCell(x, y), map.getCell(h, k), player.getWorker1()); //move due to Apollo's power
		newMove.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(h,k), map.getCell(x,y), map.getCell(h, k).getWorker())); //opponent's worker bounded to swipe with player's worker1
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove));

		i=0; j=3;
		Move newMove1 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove1));

		i=1; j=4;
		Move newMove2 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x,y), map.getCell(i,j), player.getWorker1());
		assertTrue(apollo.checkMove(map, player.getWorker1(), type).contains(newMove2));

	}

}