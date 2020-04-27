package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.stub.GameStub;
import it.polimi.ingsw.core.*;
import it.polimi.ingsw.network.game.NetMove;
import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
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
		gameStub = new GameStub(new String[]{"Aldo", "Giovanni", "Giacomo"});
		mover = new Mover(gameStub);
		gameMap = gameStub.getMap();
		gamePlayer1 = new Player("Aldo");
		gamePlayer1.setPlayerColor(Color.BLACK);
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
		assertTrue(gameStub.isApplyBuildCalled());
	}

	@Test
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

	@Test
	public void secondNull() {
		gamePlayer1.getWorker1().setPos(gameMap.getCell(0,0));
		Move move1 = new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(0,0), gameMap.getCell(0,1), gamePlayer1.getWorker1());
		NetMove netMove = new NetMove(move1);

		assertFalse(mover.move(netMove,null));
		assertFalse(gameStub.isApplyBuildCalled());
	}

	@Test
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
}