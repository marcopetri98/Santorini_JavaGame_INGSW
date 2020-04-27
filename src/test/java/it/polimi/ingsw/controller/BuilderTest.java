package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.stub.GameStub;
import it.polimi.ingsw.core.*;
import it.polimi.ingsw.network.game.NetBuild;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class BuilderTest {
	private GameStub gameStub;
	private Builder builder;
	private Map gameMap;
	private Player gamePlayer1;

	@Before
	public void setVariables() {
		gameStub = new GameStub(new String[]{"Aldo", "Giovanni", "Giacomo"});
		builder = new Builder(gameStub);
		gameMap = new Map();
		gamePlayer1 = new Player("Aldo");
		gamePlayer1.setPlayerColor(Color.BLACK);
	}

	@Test
	public void correctBuild() {
		gamePlayer1.getWorker1().setPos(gameMap.getCell(0,0));
		Build build1 = new Build(gamePlayer1.getWorker1(),gameMap.getCell(1,1), false, TypeBuild.SIMPLE_BUILD);
		Build build2 = new Build(gamePlayer1.getWorker1(),gameMap.getCell(0,1), false, TypeBuild.SIMPLE_BUILD);
		NetBuild netBuild1 = new NetBuild(build1);
		List<Build> constructionPossibilities = new ArrayList<>();
		constructionPossibilities.add(build1);
		constructionPossibilities.add(build2);

		assertTrue(builder.build(netBuild1,constructionPossibilities));
		assertTrue(gameStub.isApplyBuildCalled());
	}

	@Test
	public void firstNull() {
		gamePlayer1.getWorker1().setPos(gameMap.getCell(0,0));
		Build build1 = new Build(gamePlayer1.getWorker1(),gameMap.getCell(1,1), false, TypeBuild.SIMPLE_BUILD);
		Build build2 = new Build(gamePlayer1.getWorker1(),gameMap.getCell(0,1), false, TypeBuild.SIMPLE_BUILD);
		List<Build> constructionPossibilities = new ArrayList<>();
		constructionPossibilities.add(build1);
		constructionPossibilities.add(build2);

		assertFalse(builder.build(null,constructionPossibilities));
		assertFalse(gameStub.isApplyBuildCalled());
	}

	@Test
	public void secondNull() {
		gamePlayer1.getWorker1().setPos(gameMap.getCell(0,0));
		Build build1 = new Build(gamePlayer1.getWorker1(),gameMap.getCell(1,1), false, TypeBuild.SIMPLE_BUILD);
		NetBuild netBuild1 = new NetBuild(build1);

		assertFalse(builder.build(netBuild1,null));
		assertFalse(gameStub.isApplyBuildCalled());
	}

	@Test
	public void bothNull() {
		assertFalse(builder.build(null,null));
		assertFalse(gameStub.isApplyBuildCalled());
	}

	@Test
	public void incorrectBuild() {
		gamePlayer1.getWorker1().setPos(gameMap.getCell(0,0));
		Build build1 = new Build(gamePlayer1.getWorker1(),gameMap.getCell(1,1), false, TypeBuild.SIMPLE_BUILD);
		Build build2 = new Build(gamePlayer1.getWorker1(),gameMap.getCell(0,1), false, TypeBuild.SIMPLE_BUILD);
		Build incorrectBuild = new Build(gamePlayer1.getWorker1(),gameMap.getCell(2,2), false, TypeBuild.SIMPLE_BUILD);
		NetBuild netBuild1 = new NetBuild(incorrectBuild);
		List<Build> constructionPossibilities = new ArrayList<>();
		constructionPossibilities.add(build1);
		constructionPossibilities.add(build2);

		assertFalse(builder.build(netBuild1,constructionPossibilities));
		assertFalse(gameStub.isApplyBuildCalled());
	}
}