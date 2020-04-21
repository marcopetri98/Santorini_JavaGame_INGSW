package it.polimi.ingsw.core;

import it.polimi.ingsw.core.gods.Artemis;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class ArtemisTest {
	private Map map;
	private TypeMove type;
	private Artemis artemis;
	private Player player;
	private Player opponent;
	int x,y,x1,y1; //player's workers positions
	int h,k,h1,k1; //opponent's workers positions
	int i,j; //simple move position
	int s,t; //second move

	@Before
	public void testSetup(){
		map = new Map();
		type = TypeMove.SIMPLE_MOVE; //typeMove of Artemis according to our implementation
		player = new Player("Pippo");
		player.setPlayerColor(Color.RED);
		opponent = new Player("Pluto");
		opponent.setPlayerColor(Color.BLACK);
		artemis = new Artemis(player);
	}

	/**
	 * Worker1 in (0,0) (level 0), opponent worker in (1,1) (level 0): it should return 8 cells ( (i,j) & (s,t) ), which I compare "manually" with the returned arrayList of the checkMove
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

		assertEquals(8, artemis.checkMove(map, player.getWorker1(), type).size());

		i=0; j=1; s=0; t=2;
		Move newMove = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		newMove.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(i,j), map.getCell(s,t), map.getCell(x, y).getWorker()));
		assertTrue(artemis.checkMove(map, player.getWorker1(), type).contains(newMove));

		i=0; j=1; s=1; t=2;
		Move newMove1 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		newMove1.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(i,j), map.getCell(s,t), map.getCell(x, y).getWorker()));
		assertTrue(artemis.checkMove(map, player.getWorker1(), type).contains(newMove1));

		i=0; j=1; s=1; t=0;
		Move newMove2 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		newMove2.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(i,j), map.getCell(s,t), map.getCell(x, y).getWorker()));
		assertTrue(artemis.checkMove(map, player.getWorker1(), type).contains(newMove2));

		i=1; j=0; s=0; t=1;
		Move newMove3 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		newMove3.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(i,j), map.getCell(s,t), map.getCell(x, y).getWorker()));
		assertTrue(artemis.checkMove(map, player.getWorker1(), type).contains(newMove3));

		i=1; j=0; s=2; t=0;
		Move newMove4 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		newMove4.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(i,j), map.getCell(s,t), map.getCell(x, y).getWorker()));
		assertTrue(artemis.checkMove(map, player.getWorker1(), type).contains(newMove4));

		i=1; j=0; s=2; t=1;
		Move newMove5 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		newMove5.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(i,j), map.getCell(s,t), map.getCell(x, y).getWorker()));
		assertTrue(artemis.checkMove(map, player.getWorker1(), type).contains(newMove5));

		i=1; j=0;
		Move newMove6 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		assertTrue(artemis.checkMove(map, player.getWorker1(), type).contains(newMove6));

		i=0; j=1;
		Move newMove7 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		assertTrue(artemis.checkMove(map, player.getWorker1(), type).contains(newMove7));

	}

	/**
	 * Worker1 in (1,1) (level 2) and Worker2 in (1,2), opponent worker1 in (2,2) (level 3) and worker2 in (2,1) (level 1), building with dome in (2,0), building level 3 in (1,0), building level 1 in (0,0), dome only in (0,1): it should return only 6 cells ( (i,j) & (s,t) ) which I compare "manually" with the returned arrayList of the checkMove
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

		assertEquals(6, artemis.checkMove(map, player.getWorker1(), type).size());

		i=1; j=0; s=0; t=0;
		Move newMove = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		newMove.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(i,j), map.getCell(s,t), map.getCell(x, y).getWorker()));
		assertTrue(artemis.checkMove(map, player.getWorker1(), type).contains(newMove));

		i=0; j=2; s=0; t=3;
		Move newMove1 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		newMove1.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(i,j), map.getCell(s,t), map.getCell(x, y).getWorker()));
		assertTrue(artemis.checkMove(map, player.getWorker1(), type).contains(newMove1));

		i=0; j=2; s=1; t=3;
		Move newMove2 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		newMove2.setCondition(new Move(TypeMove.SIMPLE_MOVE, map.getCell(i,j), map.getCell(s,t), map.getCell(x, y).getWorker()));
		assertTrue(artemis.checkMove(map, player.getWorker1(), type).contains(newMove2));

		i=0; j=0;
		Move newMove3 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		assertTrue(artemis.checkMove(map, player.getWorker1(), type).contains(newMove3));

		i=0; j=2;
		Move newMove4 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		assertTrue(artemis.checkMove(map, player.getWorker1(), type).contains(newMove4));

		i=1; j=0;
		Move newMove5 = new Move(TypeMove.SIMPLE_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		assertTrue(artemis.checkMove(map, player.getWorker1(), type).contains(newMove5));

	}
}