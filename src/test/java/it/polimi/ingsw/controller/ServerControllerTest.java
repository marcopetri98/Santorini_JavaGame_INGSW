package it.polimi.ingsw.controller;

import com.sun.source.tree.AssertTree;
import it.polimi.ingsw.controller.driver.RemoteViewDriver;
import it.polimi.ingsw.controller.stub.*;
import it.polimi.ingsw.core.Build;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.network.ServerClientListenerThread;
import it.polimi.ingsw.network.driver.ServerListenerDriver;
import it.polimi.ingsw.network.game.NetAvailablePositions;
import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetGameSetup;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ServerControllerTest {
	private ServerController serverController;
	private RemoteViewDriver remoteViewDriver;
	private SetupStub setupStub;
	private BuilderStub builderStub;
	private DefeatStub defeatStub;
	private MoverStub moverStub;
	private VictoryStub victoryStub;
	private GameStub gameStub;
	private Game game;
	private String[] playerNames;

	@Before
	public void prepareTest() throws NoSuchFieldException, IllegalAccessException {
		playerNames = new String[]{"Ezio Auditore","Connor","Altair"};
		resetTest(false,false);
	}

	private void resetTest(boolean colorPhase, boolean godsPhase) throws NoSuchFieldException, IllegalAccessException {
		gameStub = new GameStub(playerNames,colorPhase,godsPhase);
		serverController = new ServerController(gameStub);
		setupStub = new SetupStub(gameStub);
		builderStub = new BuilderStub(gameStub);
		defeatStub = new DefeatStub(gameStub);
		moverStub = new MoverStub(gameStub);
		victoryStub = new VictoryStub(gameStub);
		remoteViewDriver = new RemoteViewDriver(new ServerListenerDriver());

		Field serverControllerField = ServerController.class.getDeclaredField("moveController");
		serverControllerField.setAccessible(true);
		serverControllerField.set(serverController,moverStub);
		serverControllerField = ServerController.class.getDeclaredField("buildController");
		serverControllerField.setAccessible(true);
		serverControllerField.set(serverController,builderStub);
		serverControllerField = ServerController.class.getDeclaredField("defeatController");
		serverControllerField.setAccessible(true);
		serverControllerField.set(serverController,defeatStub);
		serverControllerField = ServerController.class.getDeclaredField("victoryController");
		serverControllerField.setAccessible(true);
		serverControllerField.set(serverController,victoryStub);
		serverControllerField = ServerController.class.getDeclaredField("setupController");
		serverControllerField.setAccessible(true);
		serverControllerField.set(serverController,setupStub);
	}
	private void resetWithRealGame(boolean prometheus) throws NoSuchFieldException, IllegalAccessException, WrongPhaseException {
		game = new Game(playerNames);
		serverController = new ServerController(game);
		setupStub = new SetupStub(gameStub);
		builderStub = new BuilderStub(gameStub);
		defeatStub = new DefeatStub(gameStub);
		moverStub = new MoverStub(gameStub);
		victoryStub = new VictoryStub(gameStub);
		remoteViewDriver = new RemoteViewDriver(new ServerListenerDriver());

		Field serverControllerField = ServerController.class.getDeclaredField("moveController");
		serverControllerField.setAccessible(true);
		serverControllerField.set(serverController,moverStub);
		serverControllerField = ServerController.class.getDeclaredField("buildController");
		serverControllerField.setAccessible(true);
		serverControllerField.set(serverController,builderStub);
		serverControllerField = ServerController.class.getDeclaredField("defeatController");
		serverControllerField.setAccessible(true);
		serverControllerField.set(serverController,defeatStub);
		serverControllerField = ServerController.class.getDeclaredField("victoryController");
		serverControllerField.setAccessible(true);
		serverControllerField.set(serverController,victoryStub);
		serverControllerField = ServerController.class.getDeclaredField("setupController");
		serverControllerField.setAccessible(true);
		serverControllerField.set(serverController,setupStub);

		// lobby phase
		game.changeTurn();
		// color phase setup
		game.setPlayerColor(playerNames[0],Constants.COLOR_COLORS.get(0));
		game.changeTurn();
		game.setPlayerColor(playerNames[1],Constants.COLOR_COLORS.get(1));
		game.changeTurn();
		game.setPlayerColor(playerNames[2],Constants.COLOR_COLORS.get(2));
		game.changeTurn();
		// gods phase setup
		// challenger chooses
		List<String> godNames = new ArrayList<>();
		godNames.add("APOLLO");
		godNames.add("ARTEMIS");
		if (!prometheus) {
			godNames.add("ATHENA");
		} else {
			godNames.add("PROMETHEUS");
		}
		game.setGameGods(godNames);
		game.changeTurn();
		// players choose the gods
		game.setPlayerGod(playerNames[0],godNames.get(0));
		game.changeTurn();
		game.setPlayerGod(playerNames[1],godNames.get(1));
		game.changeTurn();
		game.setPlayerGod(playerNames[2],godNames.get(2));
		game.changeTurn();
		// challenger select the starter
		game.setStarter("Ezio Auditore");
		game.changeTurn();
		// setup workers phase
		NetGameSetup gameSetupMsg;
		gameSetupMsg = new NetGameSetup(Constants.GAMESETUP_IN_PLACE,"Ezio Auditore",new Pair<Integer, Integer>(0,1),new Pair<Integer, Integer>(0,3));
		game.setWorkerPositions(gameSetupMsg);
		game.changeTurn();
		gameSetupMsg = new NetGameSetup(Constants.GAMESETUP_IN_PLACE,"Connor",new Pair<Integer, Integer>(2,1),new Pair<Integer, Integer>(2,3));
		game.setWorkerPositions(gameSetupMsg);
		game.changeTurn();
		gameSetupMsg = new NetGameSetup(Constants.GAMESETUP_IN_PLACE,"Altair",new Pair<Integer, Integer>(4,1),new Pair<Integer, Integer>(4,3));
		game.setWorkerPositions(gameSetupMsg);
		game.changeTurn();
		// now the starter is in the before move phase
	}

	@Test
	public void generateOrderTest() {
		serverController.generateOrder();
		assertTrue(setupStub.isGenerateOrderCalled());
	}

	@Test
	public void updateColors() {
		gameStub.setPhase(Phase.COLORS);
		gameStub.setActivePlayer("Ezio Auditore");

		NetColorPreparation colorMsg = new NetColorPreparation(Constants.COLOR_IN_CHOICE,"Ezio Auditore",Constants.COLOR_COLORS.get(0));
		serverController.updateColors(remoteViewDriver,colorMsg);
		assertFalse(remoteViewDriver.isCommunicateErrorCalled());
		assertTrue(setupStub.isChangeColorCalled());
		assertTrue(gameStub.isChangeTurnCalled());
		gameStub.resetCounters();
		setupStub.resetCalls();
		remoteViewDriver.resetCalls();

		gameStub.setActivePlayer("Connor");
		serverController.updateColors(remoteViewDriver,colorMsg);
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		assertFalse(setupStub.isChangeColorCalled());
		setupStub.resetCalls();
		remoteViewDriver.resetCalls();

		gameStub.setPhase(Phase.PLAYERTURN);
		gameStub.setActivePlayer("Ezio Auditore");
		serverController.updateColors(remoteViewDriver,colorMsg);
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		assertFalse(setupStub.isChangeColorCalled());
		setupStub.resetCalls();
		remoteViewDriver.resetCalls();
	}

	@Test
	public void updateGods() {
		gameStub.setPhase(GodsPhase.CHALLENGER_CHOICE);
		gameStub.setActivePlayer("Ezio Auditore");
		List<String> godsChosen = new ArrayList<>();
		godsChosen.add(Constants.GODS_GOD_NAMES.get(0));
		godsChosen.add(Constants.GODS_GOD_NAMES.get(1));
		godsChosen.add(Constants.GODS_GOD_NAMES.get(2));

		NetDivinityChoice godsMsg = new NetDivinityChoice(Constants.GODS_IN_GAME_GODS,"Ezio Auditore", godsChosen);
		serverController.updateGods(remoteViewDriver,godsMsg);
		assertFalse(remoteViewDriver.isCommunicateErrorCalled());
		assertTrue(setupStub.isHandleGodMessageCalled());
		assertTrue(gameStub.isChangeTurnCalled());
		gameStub.resetCounters();
		setupStub.resetCalls();
		remoteViewDriver.resetCalls();

		gameStub.setPhase(GodsPhase.GODS_CHOICE);
		godsMsg = new NetDivinityChoice(Constants.GODS_IN_CHOICE,"Ezio Auditore", Constants.GODS_GOD_NAMES.get(0),false);
		serverController.updateGods(remoteViewDriver,godsMsg);
		assertFalse(remoteViewDriver.isCommunicateErrorCalled());
		assertTrue(setupStub.isHandleGodMessageCalled());
		assertTrue(gameStub.isChangeTurnCalled());
		gameStub.resetCounters();
		setupStub.resetCalls();
		remoteViewDriver.resetCalls();

		gameStub.setPhase(GodsPhase.STARTER_CHOICE);
		godsMsg = new NetDivinityChoice(Constants.GODS_IN_START_PLAYER,"Ezio Auditore", "Altair",true);
		serverController.updateGods(remoteViewDriver,godsMsg);
		assertFalse(remoteViewDriver.isCommunicateErrorCalled());
		assertTrue(setupStub.isHandleGodMessageCalled());
		assertTrue(gameStub.isChangeTurnCalled());
		gameStub.resetCounters();
		setupStub.resetCalls();
		remoteViewDriver.resetCalls();

		gameStub.setActivePlayer("Connor");
		serverController.updateGods(remoteViewDriver,godsMsg);
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		assertFalse(setupStub.isChangeColorCalled());
		setupStub.resetCalls();
		remoteViewDriver.resetCalls();

		gameStub.setPhase(Phase.PLAYERTURN);
		gameStub.setActivePlayer("Ezio Auditore");
		serverController.updateGods(remoteViewDriver,godsMsg);
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		assertFalse(setupStub.isChangeColorCalled());
		setupStub.resetCalls();
		remoteViewDriver.resetCalls();
	}

	@Test
	public void updatePositions() {
		gameStub.setPhase(Phase.SETUP);
		gameStub.setActivePlayer("Ezio Auditore");

		NetGameSetup gameSetupMsg = new NetGameSetup(Constants.GAMESETUP_IN_PLACE,"Ezio Auditore",new Pair<Integer, Integer>(0,0),new Pair<Integer, Integer>(1,1));

		serverController.updatePositions(remoteViewDriver,gameSetupMsg);
		assertFalse(remoteViewDriver.isCommunicateErrorCalled());
		assertTrue(setupStub.isPositionWorkersCalled());
		assertTrue(gameStub.isChangeTurnCalled());
		remoteViewDriver.resetCalls();
		setupStub.resetCalls();
		gameStub.resetCounters();

		gameStub.setActivePlayer("Connor");
		serverController.updatePositions(remoteViewDriver,gameSetupMsg);
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		assertFalse(setupStub.isChangeColorCalled());
		setupStub.resetCalls();
		remoteViewDriver.resetCalls();

		gameStub.setPhase(Phase.PLAYERTURN);
		gameStub.setActivePlayer("Ezio Auditore");
		serverController.updatePositions(remoteViewDriver,gameSetupMsg);
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		assertFalse(setupStub.isChangeColorCalled());
		setupStub.resetCalls();
		remoteViewDriver.resetCalls();
	}

	@Test
	public void updatePass() {
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
	public void observerQuit() {
		gameStub.addObserver(remoteViewDriver);
		serverController.observerQuit(remoteViewDriver);
		// now if it is deleted and i try to add it as observer i do not generate exceptions
		gameStub.addObserver(remoteViewDriver);
	}

	@Test
	public void giveAvailablePositionsFlow() throws IllegalAccessException, NoSuchFieldException, WrongPhaseException {
		resetWithRealGame(false);

		// asks in before move builds which are not possible and asks for moves after it entered in move phase
		assertNull(serverController.giveAvailableBuildings());
		assertNotNull(serverController.giveAvailablePositions());

		// advance to Connor turn
		game.changeTurn();
		game.changeTurn();
		// advance to Altair turn
		game.changeTurn();
		game.changeTurn();
		game.changeTurn();

		// moves to move phase
		game.changeTurn();

		// tests if it return positions if the player has athena
		assertNotNull(serverController.giveAvailablePositions());
	}

	@Test
	public void giveAvailableBuildings() throws IllegalAccessException, NoSuchFieldException, WrongPhaseException {
		resetWithRealGame(true);

		// advance to the Connor turn
		game.changeTurn();
		game.changeTurn();
		game.changeTurn();
		// advance to the Altair turn
		game.changeTurn();
		game.changeTurn();
		game.changeTurn();

		// it simulate that the player first moved with worker 2
		game.applyWorkerLock(game.getPlayerByName("Altair"),1);
		// asks in before move builds which are not possible and asks for moves after it entered in move phase
		assertNotNull(serverController.giveAvailableBuildings());
		game.changeTurn();
		assertNotNull(serverController.giveAvailablePositions());
		game.changeTurn();
		assertNotNull(serverController.giveAvailableBuildings());
	}
}