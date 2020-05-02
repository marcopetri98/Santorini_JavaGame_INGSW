package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.stub.GameStub;
import it.polimi.ingsw.core.Cell;
import it.polimi.ingsw.core.Map;
import it.polimi.ingsw.core.Player;
import it.polimi.ingsw.core.Worker;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetGameSetup;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.util.exceptions.BadRequestException;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class SetupManagerTest {
	private GameStub gameStub;
	private SetupManager setupController;
	private Map gameMap;
	private Player gamePlayer1;
	private Cell before;
	private Cell after;

	public void setVariablesChoice(boolean colorPhase, boolean godPhase) {
		gameStub = new GameStub(new String[]{"Aldo", "Giovanni", "Giacomo"},colorPhase,godPhase);
		setupController = new SetupManager(gameStub);
		gameMap = gameStub.getMap();
		gameStub.setActivePlayer("Aldo");
		gamePlayer1 = gameStub.getPlayerByName("Aldo");
	}

	@Before
	public void setVariables() {
		setVariablesChoice(false,false);
	}

	@Test
	public void generateOrder() {
		// it calls the stub method which instead of execute the command returns true if the list passed is a partition of already present players
		setupController.generateOrder();
		assertTrue("It hasn't called the method of the game to set the order",gameStub.isSetOrderCalledCorrectly());
	}

	@Test (expected = AssertionError.class)
	public void generateOrderWrongPhase() {
		// it calls the order generation in a wrong game phase
		gameStub.setPhase(Phase.COLORS);
		setupController.generateOrder();
		assertFalse("The method executes also with a wrong phase",gameStub.isSetOrderCalledCorrectly());
	}

	@Test
	public void changeColorCorrectly() throws BadRequestException, WrongPhaseException {
		// test if it calls the set color method without chosen colors by players
		NetColorPreparation message = new NetColorPreparation(Constants.COLOR_IN_CHOICE,"Aldo",Constants.COLOR_COLORS.get(0));
		gameStub.setPhase(Phase.COLORS);

		setupController.changeColor(message);
		assertTrue(gameStub.isSetPlayerColorCalled());

		gameStub.resetCounters();
		message = new NetColorPreparation(Constants.COLOR_IN_CHOICE,"Aldo",Constants.COLOR_COLORS.get(1));
		setupController.changeColor(message);
		assertTrue("It hasn't called the method of the game to set the colors",gameStub.isSetPlayerColorCalled());
	}

	@Test (expected = BadRequestException.class)
	public void changeColorAlreadyPresent() throws BadRequestException, WrongPhaseException {
		// test if it calls the set color method without chosen colors by players
		NetColorPreparation message = new NetColorPreparation(Constants.COLOR_IN_CHOICE,"Aldo",Constants.COLOR_COLORS.get(0));
		gameStub.setPhase(Phase.COLORS);

		gameStub.setAColor();
		setupController.changeColor(message);
		fail("It hasn't thrown an exception an executed also if is impossible to");
	}

	@Test (expected = BadRequestException.class)
	public void changeColorWrongPhase() throws BadRequestException, WrongPhaseException {
		// test if it calls the set color method without chosen colors by players
		NetColorPreparation message = new NetColorPreparation(Constants.COLOR_IN_CHOICE,"Aldo",Constants.COLOR_COLORS.get(0));
		gameStub.setPhase(Phase.GODS);

		gameStub.setAColor();
		setupController.changeColor(message);
		fail("The method executes also with a wrong phase");
	}

	@Test
	public void changeColorStability() throws BadRequestException, WrongPhaseException {
		// test if it calls the set color method with a not well formatted message
		int exceptionsThrown = 0;
		NetColorPreparation message = new NetColorPreparation("Frodo Baggins","Aldo",Constants.COLOR_COLORS.get(0));
		gameStub.setPhase(Phase.COLORS);

		try { setupController.changeColor(message); }
		catch (BadRequestException e) { exceptionsThrown++;}

		message = new NetColorPreparation(Constants.COLOR_IN_CHOICE,"Saruman",Constants.COLOR_COLORS.get(0));
		try { setupController.changeColor(message); }
		catch (BadRequestException e) { exceptionsThrown++;}

		message = new NetColorPreparation(Constants.COLOR_IN_CHOICE,"Aldo",new Color(0,0,0));
		try { setupController.changeColor(message); }
		catch (BadRequestException e) { exceptionsThrown++;}

		gameStub.setActivePlayer("Giacomo");
		message = new NetColorPreparation(Constants.COLOR_IN_CHOICE,"Aldo",Constants.COLOR_COLORS.get(0));
		try { setupController.changeColor(message); }
		catch (BadRequestException e) { exceptionsThrown++;}

		assertEquals("Method called only some of the exceptions",exceptionsThrown,4);
	}

	@Test
	public void handleGodChallengerCorrectlySetGods() throws BadRequestException, WrongPhaseException {
		setVariablesChoice(true,false);
		gameStub.setPhase(Phase.GODS);
		gameStub.setPhase(GodsPhase.CHALLENGER_CHOICE);
		NetDivinityChoice godsMessage = new NetDivinityChoice(Constants.GODS_IN_GAME_GODS,"Aldo",Constants.GODS_GOD_NAMES.get(0),false);
		godsMessage = new NetDivinityChoice(Constants.GODS_IN_GAME_GODS,"Aldo",Constants.GODS_GOD_NAMES.get(1),godsMessage);
		godsMessage = new NetDivinityChoice(Constants.GODS_IN_GAME_GODS,"Aldo",Constants.GODS_GOD_NAMES.get(2),godsMessage);

		setupController.handleGodMessage(godsMessage);
		assertTrue(gameStub.isSetGameGodsCalled());
	}

	@Test
	public void handleGodChallengerCorrectlySetStarter() throws BadRequestException, WrongPhaseException {
		setVariablesChoice(true,false);
		gameStub.setPhase(GodsPhase.STARTER_CHOICE);
		NetDivinityChoice godsMessage = new NetDivinityChoice(Constants.GODS_IN_START_PLAYER,"Aldo","Aldo",true);

		setupController.handleGodMessage(godsMessage);
		assertTrue(gameStub.isSetStarterCalled());
	}

	@Test
	public void handleGodPlayerCorrectlyChoose() throws BadRequestException, WrongPhaseException {
		setVariablesChoice(true,false);
		gameStub.setPhase(GodsPhase.GODS_CHOICE);
		NetDivinityChoice godsMessage = new NetDivinityChoice(Constants.GODS_IN_CHOICE,"Aldo",Constants.GODS_GOD_NAMES.get(0),false);

		setupController.handleGodMessage(godsMessage);
		assertTrue(gameStub.isSetPlayerGodCalled());
	}

	@Test (expected = BadRequestException.class)
	public void handleGodChallengerIncorrectlySetGods() throws BadRequestException, WrongPhaseException {
		setVariablesChoice(true,false);
		gameStub.setPhase(GodsPhase.CHALLENGER_CHOICE);
		NetDivinityChoice godsMessage = new NetDivinityChoice(Constants.GODS_IN_GAME_GODS,"Aldo",Constants.GODS_GOD_NAMES.get(0),false);
		godsMessage = new NetDivinityChoice(Constants.GODS_IN_GAME_GODS,"Aldo",Constants.GODS_GOD_NAMES.get(1),godsMessage);
		godsMessage = new NetDivinityChoice(Constants.GODS_IN_GAME_GODS,"Aldo","PaperelleGialle",godsMessage);

		setupController.handleGodMessage(godsMessage);
		fail();
	}

	@Test (expected = BadRequestException.class)
	public void hangleGodChallengerIncorrectlySetStarter() throws BadRequestException, WrongPhaseException {
		setVariablesChoice(true,false);
		gameStub.setPhase(GodsPhase.STARTER_CHOICE);
		NetDivinityChoice godsMessage = new NetDivinityChoice(Constants.GODS_IN_CHOICE,"Gandalf",Constants.GODS_GOD_NAMES.get(0),false);

		setupController.handleGodMessage(godsMessage);
		fail();
	}

	@Test (expected = BadRequestException.class)
	public void handleGodPlayerAlreadyChosen() throws BadRequestException, WrongPhaseException {
		setVariablesChoice(true,false);
		gameStub.setPhase(GodsPhase.GODS_CHOICE);
		gameStub.setAGod();
		NetDivinityChoice godsMessage = new NetDivinityChoice(Constants.GODS_IN_CHOICE,"Giovanni",Constants.GODS_GOD_NAMES.get(0),false);

		setupController.handleGodMessage(godsMessage);
		fail();
	}

	@Test (expected = BadRequestException.class)
	public void handleGodPlayerNotExisting() throws BadRequestException, WrongPhaseException {
		setVariablesChoice(true,false);
		gameStub.setPhase(GodsPhase.GODS_CHOICE);
		NetDivinityChoice godsMessage = new NetDivinityChoice(Constants.GODS_IN_CHOICE,"Aragorn",Constants.GODS_GOD_NAMES.get(0),false);

		setupController.handleGodMessage(godsMessage);
		fail();
	}

	@Test (expected = BadRequestException.class)
	public void handleGodWrongPhaseRequest() throws BadRequestException, WrongPhaseException {
		setVariablesChoice(true,false);
		gameStub.setPhase(Phase.COLORS);
		NetDivinityChoice godsMessage = new NetDivinityChoice(Constants.GODS_IN_CHOICE,"Aldo",Constants.GODS_GOD_NAMES.get(0),false);

		setupController.handleGodMessage(godsMessage);
		fail();
	}

	@Test
	public void positionWorkersCorrect() throws BadRequestException {
		setVariablesChoice(true,true);
		gameStub.setPhase(Phase.SETUP);
		NetGameSetup message = new NetGameSetup(Constants.GAMESETUP_IN_PLACE,"Aldo",new Pair<Integer, Integer>(1,1),new Pair<Integer, Integer>(2,2));

		setupController.positionWorkers(message);
		assertTrue(gameStub.isSetWorkerPositionsCalled());
	}

	@Test (expected = BadRequestException.class)
	public void positionWorkersOnOtherWorker() throws BadRequestException {
		setVariablesChoice(true,true);
		gameStub.setPhase(Phase.SETUP);
		try {
			Method setPos = Worker.class.getDeclaredMethod("setPos", Cell.class);
			setPos.setAccessible(true);
			setPos.invoke(gamePlayer1.getWorker1(),gameMap.getCell(1, 1));
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new AssertionError("Design error");
		}
		NetGameSetup message = new NetGameSetup(Constants.GAMESETUP_IN_PLACE,"Giovanni",new Pair<Integer, Integer>(1,1),new Pair<Integer, Integer>(2,2));

		setupController.positionWorkers(message);
		fail();
	}

	@Test (expected = BadRequestException.class)
	public void positionWorkersOnSamePos() throws BadRequestException {
		setVariablesChoice(true,true);
		gameStub.setPhase(Phase.SETUP);
		NetGameSetup message = new NetGameSetup(Constants.GAMESETUP_IN_PLACE,"Aldo",new Pair<Integer, Integer>(1,1),new Pair<Integer, Integer>(1,1));

		setupController.positionWorkers(message);
		fail();
	}

	@Test (expected = BadRequestException.class)
	public void positionWorkersOutOfMap() throws BadRequestException, NoSuchFieldException {
		setVariablesChoice(true,true);
		gameStub.setPhase(Phase.SETUP);
		NetGameSetup message = new NetGameSetup(Constants.GAMESETUP_IN_PLACE,"Aldo",new Pair<Integer, Integer>(1,1),new Pair<Integer, Integer>(2,2));
		message.worker2.setFirst(10);
		message.worker2.setSecond(10);

		setupController.positionWorkers(message);
		fail();
	}

	@Test (expected = BadRequestException.class)
	public void positionWorkersOnOtherTurn() throws BadRequestException {
		setVariablesChoice(true,true);
		gameStub.setPhase(Phase.SETUP);
		gameStub.setActivePlayer("Giovanni");
		NetGameSetup message = new NetGameSetup(Constants.GAMESETUP_IN_PLACE,"Aldo",new Pair<Integer, Integer>(1,1),new Pair<Integer, Integer>(2,2));

		setupController.positionWorkers(message);
		fail();
	}

	@Test (expected = BadRequestException.class)
	public void positionWorkersWrongPhase() throws BadRequestException {
		setVariablesChoice(true,true);
		gameStub.setPhase(Phase.COLORS);
		NetGameSetup message = new NetGameSetup(Constants.GAMESETUP_IN_PLACE,"Aldo",new Pair<Integer, Integer>(1,1),new Pair<Integer, Integer>(2,2));

		setupController.positionWorkers(message);
		fail();
	}
}