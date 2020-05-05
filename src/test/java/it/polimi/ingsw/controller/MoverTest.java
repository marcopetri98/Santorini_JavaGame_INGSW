package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.stub.GameStub;
import it.polimi.ingsw.core.*;
import it.polimi.ingsw.network.game.NetMove;
import org.junit.Before;
import org.junit.Test;

import it.polimi.ingsw.util.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MoverTest {
	private GameStub gameStub;
	private Mover mover;
	private Map gameMap;
	private Player gamePlayer1;

	//support methods
	private void setWorkerPosition(int w, int x, int y) {
		try {
			Method setPos = Worker.class.getDeclaredMethod("setPos", Cell.class);
			setPos.setAccessible(true);
			if (w == 1) {
				setPos.invoke(gamePlayer1.getWorker1(),gameMap.getCell(x, y));
			} else {
				setPos.invoke(gamePlayer1.getWorker2(),gameMap.getCell(x, y));
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new AssertionError("Design error");
		}
	}

	// testing methods
	@Before
	public void setVariables() {
		gameStub = new GameStub(new String[]{"Aldo", "Giovanni", "Giacomo"},true,true);
		mover = new Mover(gameStub);
		gameMap = gameStub.getMap();
		gamePlayer1 = gameStub.getPlayerByName("Aldo");
	}

	@Test
	public void correctMove() {
		setWorkerPosition(1,0,0);
		Move move1 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(0,1), gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(1,0), gamePlayer1.getWorker1());
		NetMove netMove = new NetMove(move1);
		List<Move> movePossibilities = new ArrayList<>();
		movePossibilities.add(move1);
		movePossibilities.add(move2);

		assertTrue(mover.move(netMove,movePossibilities));
		assertTrue(gameStub.isApplyMoveCalled());
	}

	@Test (expected = NullPointerException.class)
	public void firstNull() {
		setWorkerPosition(1,0,0);
		Move move1 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(0,1), gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(1,0), gamePlayer1.getWorker1());
		List<Move> movePossibilities = new ArrayList<>();
		movePossibilities.add(move1);
		movePossibilities.add(move2);

		assertFalse(mover.move(null,movePossibilities));
		assertFalse(gameStub.isApplyBuildCalled());
	}

	@Test (expected = NullPointerException.class)
	public void secondNull() {
		setWorkerPosition(1,0,0);
		Move move1 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(0,1), gamePlayer1.getWorker1());
		NetMove netMove = new NetMove(move1);

		assertFalse(mover.move(netMove,null));
		assertFalse(gameStub.isApplyBuildCalled());
	}

	@Test (expected = NullPointerException.class)
	public void bothNull() {
		assertFalse(mover.move(null,null));
		assertFalse(gameStub.isApplyBuildCalled());
	}

	@Test
	public void notPresentMove() {
		setWorkerPosition(1,0,0);
		Move move1 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(0,1), gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(1,0), gamePlayer1.getWorker1());
		Move move3 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(1,1), gamePlayer1.getWorker1());
		NetMove netMove = new NetMove(move3);
		List<Move> movePossibilities = new ArrayList<>();
		movePossibilities.add(move1);
		movePossibilities.add(move2);

		assertFalse(mover.move(netMove,movePossibilities));
		assertFalse(gameStub.isApplyBuildCalled());
	}

	@Test
	public void forbiddenMove() {
		setWorkerPosition(1,0,0);
		Move move1 = new Move(TypeMove.FORBIDDEN_MOVE, gameMap.getCell(0,0), gameMap.getCell(0,1), gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.FORBIDDEN_MOVE, gameMap.getCell(0,0), gameMap.getCell(1,0), gamePlayer1.getWorker1());
		NetMove netMove = new NetMove(move2);
		List<Move> movePossibilities = new ArrayList<>();
		movePossibilities.add(move1);
		movePossibilities.add(move2);

		assertFalse(mover.move(netMove,movePossibilities));
		assertFalse(gameStub.isApplyBuildCalled());
	}
}