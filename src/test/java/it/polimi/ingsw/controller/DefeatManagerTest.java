package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.stub.GameStub;
import it.polimi.ingsw.core.*;
import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DefeatManagerTest {
	private GameStub gameStub;
	private DefeatManager defeatController;
	private Map gameMap;
	private Player gamePlayer1;

	@Before
	public void setVariables() {
		gameStub = new GameStub(new String[]{"Aldo", "Giovanni", "Giacomo"});
		defeatController = new DefeatManager(gameStub);
		gameMap = new Map();
		gamePlayer1 = new Player("Aldo");
		gamePlayer1.setPlayerColor(Color.BLACK);
	}

	@Test
	public void moveDefeatNoMoves() {
		gamePlayer1.getWorker1().setPos(gameMap.getCell(0,0));
		List<Move> list1 = new ArrayList<>();
		List<Move> list2 = new ArrayList<>();
		Move move1 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(0,1), gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(1,0), gamePlayer1.getWorker1());

		defeatController.moveDefeat(null,null);
		assertTrue(gameStub.isApplyDefeatCalled());

		gameStub.resetCounters();
		defeatController.moveDefeat(null,list2);
		assertTrue(gameStub.isApplyDefeatCalled());

		gameStub.resetCounters();
		list2.add(move2);
		defeatController.moveDefeat(null,list2);
		assertFalse(gameStub.isApplyDefeatCalled());

		gameStub.resetCounters();
		defeatController.moveDefeat(list1,null);
		assertTrue(gameStub.isApplyDefeatCalled());

		gameStub.resetCounters();
		list2.clear();
		defeatController.moveDefeat(list1,list2);
		assertTrue(gameStub.isApplyDefeatCalled());

		gameStub.resetCounters();
		list2.add(move2);
		defeatController.moveDefeat(list1,list2);
		assertFalse(gameStub.isApplyDefeatCalled());

		gameStub.resetCounters();
		list1.add(move1);
		list2.clear();
		defeatController.moveDefeat(list1,null);
		assertFalse(gameStub.isApplyDefeatCalled());

		gameStub.resetCounters();
		defeatController.moveDefeat(list1,list2);
		assertFalse(gameStub.isApplyDefeatCalled());

		gameStub.resetCounters();
		list2.add(move2);
		defeatController.moveDefeat(list1,list2);
		assertFalse(gameStub.isApplyDefeatCalled());
	}

	@Test
	public void moveDefeatAllForbidden() {
		gamePlayer1.getWorker1().setPos(gameMap.getCell(0,0));
		List<Move> list1 = new ArrayList<>();
		List<Move> list2 = new ArrayList<>();
		Move move1 = new Move(TypeMove.FORBIDDEN_MOVE, gameMap.getCell(0,0), gameMap.getCell(0,1), gamePlayer1.getWorker1());
		Move move2 = new Move(TypeMove.FORBIDDEN_MOVE, gameMap.getCell(0,0), gameMap.getCell(1,0), gamePlayer1.getWorker1());
		list1.add(move1);
		list2.add(move2);

		defeatController.moveDefeat(list1,list2);
		assertFalse(gameStub.isApplyDefeatCalled());

		gameStub.resetCounters();
		defeatController.moveDefeat(list1,null);
		assertFalse(gameStub.isApplyDefeatCalled());

		gameStub.resetCounters();
		defeatController.moveDefeat(null,list2);
		assertFalse(gameStub.isApplyDefeatCalled());
	}

	@Test
	public void buildDefeat() {
		gamePlayer1.getWorker1().setPos(gameMap.getCell(0,0));
		List<Build> list = new ArrayList<>();
		Build build = new Build(gamePlayer1.getWorker1(),gameMap.getCell(1,1), false, TypeBuild.SIMPLE_BUILD);

		defeatController.buildDefeat(null);
		assertTrue(gameStub.isApplyDefeatCalled());

		gameStub.resetCounters();
		defeatController.buildDefeat(list);
		assertTrue(gameStub.isApplyDefeatCalled());

		gameStub.resetCounters();
		list.add(build);
		defeatController.buildDefeat(list);
		assertFalse(gameStub.isApplyDefeatCalled());
	}
}