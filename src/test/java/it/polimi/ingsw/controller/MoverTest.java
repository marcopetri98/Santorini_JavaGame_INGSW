package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.stub.GameStub;
import it.polimi.ingsw.core.*;
import it.polimi.ingsw.network.game.NetMove;
import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
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

	@Before
	public void setVariables() {
		gameStub = new GameStub(new String[]{"Aldo", "Giovanni", "Giacomo"},true,true);
		mover = new Mover(gameStub);
		gameMap = gameStub.getMap();
		gamePlayer1 = gameStub.getPlayerByName("Aldo");
	}

	@Test
	public void correctMove() {
		gamePlayer1.getWorker1().setPos(gameMap.getCell(0,0));
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
		gamePlayer1.getWorker1().setPos(gameMap.getCell(0,0));
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
		gamePlayer1.getWorker1().setPos(gameMap.getCell(0,0));
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
		gamePlayer1.getWorker1().setPos(gameMap.getCell(0,0));
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
		gamePlayer1.getWorker1().setPos(gameMap.getCell(0,0));
		Move move1 = new Move(TypeMove.FORBIDDEN_MOVE, gameMap.getCell(0,0), gameMap.getCell(0,1), gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.FORBIDDEN_MOVE, gameMap.getCell(0,0), gameMap.getCell(1,0), gamePlayer1.getWorker1());
		NetMove netMove = new NetMove(move2);
		List<Move> movePossibilities = new ArrayList<>();
		movePossibilities.add(move1);
		movePossibilities.add(move2);

		assertFalse(mover.move(netMove,movePossibilities));
		assertFalse(gameStub.isApplyBuildCalled());
	}

	@Test
	public void filterMoves() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		/*Move move1 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(0,1), gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(1,0), gamePlayer1.getWorker1());
		Move move3 = new Move(TypeMove.FORBIDDEN_MOVE, gameMap.getCell(0,0), gameMap.getCell(2,2), gamePlayer1.getWorker1());
		Move move4 = new Move(TypeMove.VICTORY_MOVE, gameMap.getCell(0,0), gameMap.getCell(2,2), gamePlayer1.getWorker1());
		List<Move> movePossibilities = new ArrayList<>();
		movePossibilities.add(move1);
		movePossibilities.add(move2);
		movePossibilities.add(move3);
		movePossibilities.add(move4);
		// TODO: ask to the tutor how to test that PRIVATE METHOD! PRIVATE!!!!!!!!!!
		Method method = mover.getClass().getDeclaredMethod("filterMoves",movePossibilities.getClass());
		method.setAccessible(true);
		List<Move> returnList = (List<Move>) method.invoke(mover,movePossibilities);
		assertTrue(movePossibilities.containsAll(returnList));*/
	}
}