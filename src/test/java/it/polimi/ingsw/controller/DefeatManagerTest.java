package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.stub.GameStub;
import it.polimi.ingsw.core.*;
import org.junit.Before;
import org.junit.Test;

import it.polimi.ingsw.util.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DefeatManagerTest {
	private GameStub gameStub;
	private DefeatManager defeatController;
	private Map gameMap;
	private Player gamePlayer1;

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
		defeatController = new DefeatManager(gameStub);
		gameMap = gameStub.getMap();
		gamePlayer1 = gameStub.getPlayerByName("Aldo");
	}

	@Test (expected = NullPointerException.class)
	public void movefirstNull() {
		setWorkerPosition(1,0,0);
		List<Move> list1 = new ArrayList<>();
		List<Move> list2 = new ArrayList<>();
		Move move1 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(0,1), gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(1,0), gamePlayer1.getWorker1());

		defeatController.moveDefeat(null,list2);
		assertTrue(gameStub.isApplyDefeatCalled());
	}

	@Test (expected = NullPointerException.class)
	public void movesecondNull() {
		setWorkerPosition(1,0,0);
		List<Move> list1 = new ArrayList<>();
		List<Move> list2 = new ArrayList<>();
		Move move1 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(0,1), gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(1,0), gamePlayer1.getWorker1());

		gameStub.resetCounters();
		defeatController.moveDefeat(list1,null);
		assertTrue(gameStub.isApplyDefeatCalled());
	}

	@Test
	public void moveDefeatNoMoves() {
		setWorkerPosition(1,0,0);
		List<Move> list1 = new ArrayList<>();
		List<Move> list2 = new ArrayList<>();
		Move move1 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(0,1), gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(1,0), gamePlayer1.getWorker1());

		defeatController.moveDefeat(list1,list2);
		assertTrue(gameStub.isApplyDefeatCalled());

		gameStub.resetCounters();
		list2.add(move2);
		defeatController.moveDefeat(list1,list2);
		assertFalse(gameStub.isApplyDefeatCalled());

		gameStub.resetCounters();
		list1.add(move1);
		list2.clear();
		defeatController.moveDefeat(list1,list2);
		assertFalse(gameStub.isApplyDefeatCalled());

		gameStub.resetCounters();
		list2.add(move2);
		defeatController.moveDefeat(list1,list2);
		assertFalse(gameStub.isApplyDefeatCalled());
	}

	@Test
	public void moveDefeatAllForbidden() {
		setWorkerPosition(1,0,0);
		List<Move> list1 = new ArrayList<>();
		List<Move> list2 = new ArrayList<>();
		Move move1 = new Move(TypeMove.FORBIDDEN_MOVE, gameMap.getCell(0,0), gameMap.getCell(0,1), gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.FORBIDDEN_MOVE, gameMap.getCell(0,0), gameMap.getCell(1,0), gamePlayer1.getWorker1());
		Move move3 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(1,0), gamePlayer1.getWorker1());
		list1.add(move1);
		list2.add(move2);

		defeatController.moveDefeat(list1,list2);
		assertTrue(gameStub.isApplyDefeatCalled());
	}

	@Test (expected = NullPointerException.class)
	public void buildNullValue() {
		defeatController.buildDefeat(null);
		assertTrue(gameStub.isApplyDefeatCalled());
	}

	@Test
	public void buildDefeat() {
		setWorkerPosition(1,0,0);
		List<Build> list = new ArrayList<>();
		Build build = new Build(gamePlayer1.getWorker1(),gameMap.getCell(1,1), false, TypeBuild.SIMPLE_BUILD);

		defeatController.buildDefeat(list);
		assertTrue(gameStub.isApplyDefeatCalled());

		gameStub.resetCounters();
		list.add(build);
		defeatController.buildDefeat(list);
		assertFalse(gameStub.isApplyDefeatCalled());
	}
}