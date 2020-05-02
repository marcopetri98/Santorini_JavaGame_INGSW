package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.stub.GameStub;
import it.polimi.ingsw.core.*;
import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class VictoryManagerTest {
	private GameStub gameStub;
	private VictoryManager victoryController;
	private Map gameMap;
	private Player gamePlayer1;
	private Cell before;
	private Cell after;

	// support methods
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
		victoryController = new VictoryManager(gameStub);
		gameMap = gameStub.getMap();
		gamePlayer1 = gameStub.getPlayerByName("Aldo");
		setWorkerPosition(1,0,0);
		before = gameMap.getCell(0,0);
	}

	@Test
	public void checkVictoryStandard() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		after = gameMap.getCell(0,1);
		Building nextCellBuilding = after.building;
		List<Move> list = new ArrayList<>();
		Move move1 = new Move(TypeMove.SIMPLE_MOVE,before,after,gamePlayer1.getWorker1());
		list.add(move1);

		// set accessible the increment method for this building object and the height of the arrive to 3 and the before height to 2
		gameStub.resetCounters();
		Method method = after.building.getClass().getDeclaredMethod("incrementLevel");
		method.setAccessible(true);
		method.invoke(before.building);
		method.invoke(before.building);
		method.invoke(after.building);
		method.invoke(after.building);
		method.invoke(after.building);

		victoryController.checkVictory(before,after,list);
		assertTrue(gameStub.isApplyWinCalled());
	}

	@Test
	public void checkVictoryGodPower() {
		after = gameMap.getCell(0,1);
		Building nextCellBuilding = after.building;
		List<Move> list = new ArrayList<>();
		Move move1 = new Move(TypeMove.VICTORY_MOVE,before,after,gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.FORBIDDEN_MOVE,before,after,gamePlayer1.getWorker1());
		list.add(move1);

		victoryController.checkVictory(before,after,list);
		assertTrue(gameStub.isApplyWinCalled());

		gameStub.resetCounters();
		list.add(move2);
		victoryController.checkVictory(before,after,list);
		assertTrue(gameStub.isApplyWinCalled());
	}

	@Test (expected = NullPointerException.class)
	public void firstNull() {
		after = gameMap.getCell(0,1);
		Building nextCellBuilding = after.building;
		List<Move> list = new ArrayList<>();
		Move move1 = new Move(TypeMove.SIMPLE_MOVE,before,after,gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.FORBIDDEN_MOVE,before,after,gamePlayer1.getWorker1());

		// it not throws exceptions ==> it can have a null parameter
		victoryController.checkVictory(null,after,list);
		assertFalse(gameStub.isApplyWinCalled());
	}

	@Test (expected = NullPointerException.class)
	public void secondNull() {
		after = gameMap.getCell(0,1);
		Building nextCellBuilding = after.building;
		List<Move> list = new ArrayList<>();
		Move move1 = new Move(TypeMove.SIMPLE_MOVE,before,after,gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.FORBIDDEN_MOVE,before,after,gamePlayer1.getWorker1());

		// it not throws exceptions ==> it can have a null parameter
		victoryController.checkVictory(before,null,list);
		assertFalse(gameStub.isApplyWinCalled());
	}

	@Test (expected = NullPointerException.class)
	public void thirdNull() {
		after = gameMap.getCell(0,1);
		Building nextCellBuilding = after.building;
		List<Move> list = new ArrayList<>();
		Move move1 = new Move(TypeMove.SIMPLE_MOVE,before,after,gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.FORBIDDEN_MOVE,before,after,gamePlayer1.getWorker1());

		// it not throws exceptions ==> it can have a null parameter
		victoryController.checkVictory(before,after,null);
		assertFalse(gameStub.isApplyWinCalled());
	}

	@Test
	public void checkNotVictoryStandard() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		after = gameMap.getCell(0,1);
		Building nextCellBuilding = after.building;
		List<Move> list = new ArrayList<>();
		Move move1 = new Move(TypeMove.SIMPLE_MOVE,before,after,gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.FORBIDDEN_MOVE,before,after,gamePlayer1.getWorker1());

		// same high == no victory
		victoryController.checkVictory(before,after,list);
		assertFalse(gameStub.isApplyWinCalled());

		// same high and simple move == no victory
		gameStub.resetCounters();
		list.add(move1);
		victoryController.checkVictory(before,after,list);
		assertFalse(gameStub.isApplyWinCalled());

		// forbidden move == no victory
		gameStub.resetCounters();
		list.add(move2);
		victoryController.checkVictory(before,after,list);
		assertFalse(gameStub.isApplyWinCalled());

		// sets the starting cell to be higher than the arrive cell
		gameStub.resetCounters();
		Method method = after.building.getClass().getDeclaredMethod("incrementLevel");
		method.setAccessible(true);
		method.invoke(before.building);
		victoryController.checkVictory(before,after,list);
		assertFalse(gameStub.isApplyWinCalled());

		// 2 ==> 0
		gameStub.resetCounters();
		method.invoke(before.building);
		victoryController.checkVictory(before,after,list);
		assertFalse(gameStub.isApplyWinCalled());

		// 3 ==> 0
		gameStub.resetCounters();
		method.invoke(before.building);
		victoryController.checkVictory(before,after,list);
		assertFalse(gameStub.isApplyWinCalled());
	}
}