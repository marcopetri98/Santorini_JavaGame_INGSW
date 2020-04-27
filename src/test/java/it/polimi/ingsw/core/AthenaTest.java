package it.polimi.ingsw.core;

import it.polimi.ingsw.core.gods.Athena;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.util.exceptions.NoMoveException;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class AthenaTest {
	private Turn turn;
	private Map map;
	private TypeMove type;
	private Athena athena;
	private Player player;
	private Player opponent;
	int x,y,x1,y1; //player's workers positions
	int h,k,h1,k1; //opponent's workers positions
	int i,j; //simple move position

	@Before
	public void testSetup(){
		map = new Map();
		player = new Player("Pippo");
		player.setPlayerColor(Color.RED);
		opponent = new Player("Pluto");
		opponent.setPlayerColor(Color.BLACK);
		athena = new Athena(player);
		turn = new Turn();
		while(turn.getGamePhase() != GamePhase.MOVE){
			turn.advance();
		}

		athena.setWentUp(true);
	}

	/**
	 * Worker1 in (1,1) (level 2) and Worker2 in (1,2), opponent worker1 in (2,2) (level 3) and worker2 in (2,1) (level 1), building with dome in (2,0), building level 3 in (1,0), building level 1 in (0,0), dome only in (0,1): it should return only 3 cells which I compare "manually" with the returned arrayList of the checkMove.
	 * The cells returned are the cells where the level is >1, without checking if there is a worker or a dome, because it's useless.
	 * Return cells only if the boolean flag wentUp is true (it means my worker moved up in the last turn)
	 * the opponent worker won't move to these cells returned.
	 */
	@Test
	public void checkMoveTestGeneral() throws NoMoveException {
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

		assertEquals(3, athena.checkMove(map, player.getWorker1(), turn).size());

		i=1; j=0;
		Move newMove3 = new Move(TypeMove.FORBIDDEN_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		assertTrue(athena.checkMove(map, player.getWorker1(), turn).contains(newMove3));

		i=2; j=0;
		Move newMove4 = new Move(TypeMove.FORBIDDEN_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		assertTrue(athena.checkMove(map, player.getWorker1(), turn).contains(newMove4));

		i=2; j=2;
		Move newMove5 = new Move(TypeMove.FORBIDDEN_MOVE, map.getCell(x, y), map.getCell(i, j), player.getWorker1());
		assertTrue(athena.checkMove(map, player.getWorker1(), turn).contains(newMove5));

	}
}