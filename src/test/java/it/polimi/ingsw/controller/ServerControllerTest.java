package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.stub.GameStub;
import it.polimi.ingsw.core.Game;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServerControllerTest {
	private ServerController serverController;
	private GameStub gameStub;
	private String[] playerNames;

	@Before
	public void prepareTest() {
		gameStub = new GameStub(new String[]{"Aldo","Giovanni","Giacomo"},false,false);
		serverController = new ServerController(gameStub);
	}

	@Test
	public void generateStandardMoves() {

	}

	@Test
	public void generateStandardBuilds() {
	}

	@Test
	public void updateColors() {
	}

	@Test
	public void updateGods() {
	}

	@Test
	public void updatePositions() {
	}

	@Test
	public void updateMove() {
	}

	@Test
	public void updateBuild() {
	}

	@Test
	public void updateQuit() {
	}

	@Test
	public void givePhase() {
	}

	@Test
	public void giveAvailablePositions() {
	}

	@Test
	public void giveAvailableBuildings() {
	}
}