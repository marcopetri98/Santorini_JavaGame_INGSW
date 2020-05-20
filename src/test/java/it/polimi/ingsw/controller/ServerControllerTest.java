package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.driver.RemoteViewDriver;
import it.polimi.ingsw.controller.stub.*;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.network.driver.ServerListenerDriver;
import it.polimi.ingsw.network.game.NetBuild;
import it.polimi.ingsw.network.game.NetMove;
import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetGameSetup;
import it.polimi.ingsw.network.objects.NetGaming;
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
		remoteViewDriver = new RemoteViewDriver(new ServerListenerDriver());

		setControllerStubs();
	}
	private void resetWithRealGame(boolean prometheus, boolean completelyTrue) throws NoSuchFieldException, IllegalAccessException, WrongPhaseException {
		game = new Game(playerNames);
		serverController = new ServerController(game);
		remoteViewDriver = new RemoteViewDriver(new ServerListenerDriver());
		if (!completelyTrue) {
			setControllerStubs();
		}

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
		godNames.add("ATLAS");
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
	private void setControllerStubs() throws NoSuchFieldException, IllegalAccessException {
		setupStub = new SetupStub(gameStub);
		builderStub = new BuilderStub(gameStub);
		defeatStub = new DefeatStub(gameStub);
		moverStub = new MoverStub(gameStub);
		victoryStub = new VictoryStub(gameStub);

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

		// TODO: update, the stub does not have compute action method
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
		gameStub.setPhase(GamePhase.BEFOREMOVE);
		gameStub.setActivePlayer("Ezio Auditore");

		serverController.updatePass(remoteViewDriver,"Ezio Auditore");
		assertTrue(gameStub.isChangeTurnCalled());
		assertFalse(remoteViewDriver.isCommunicateErrorCalled());
		gameStub.resetCounters();
		remoteViewDriver.resetCalls();

		serverController.updatePass(remoteViewDriver,"Connor");
		assertFalse(gameStub.isChangeTurnCalled());
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		gameStub.resetCounters();
		remoteViewDriver.resetCalls();
	}

	@Test
	public void updateMove() throws IllegalAccessException, NoSuchFieldException, WrongPhaseException {
		NetGaming gamingMsg;
		NetMove move;

		resetWithRealGame(false, true);

		// send the player to move phase
		game.changeTurn();
		// for first it test that a player can send a move only if is its turn
		// it is Ezio Auditore's turn
		move = new NetMove(game.getPlayerByName("Connor").getWorker1().workerID,2,4);
		gamingMsg = new NetGaming(Constants.PLAYER_IN_MOVE,"Connor",move);
		serverController.updateMove(remoteViewDriver,gamingMsg);
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		assertEquals(game.getPhase().getGamePhase(),GamePhase.MOVE);
		remoteViewDriver.resetCalls();

		// arrives a wrong request
		move = new NetMove(game.getPlayerTurn().getWorker1().workerID,2,4);
		gamingMsg = new NetGaming(Constants.PLAYER_IN_MOVE,"Connor",move);
		serverController.updateMove(remoteViewDriver,gamingMsg);
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		assertEquals(game.getPhase().getGamePhase(),GamePhase.MOVE);
		remoteViewDriver.resetCalls();

		// test if the player try to do an impossible move
		move = new NetMove(game.getPlayerTurn().getWorker1().workerID,4,4);
		gamingMsg = new NetGaming(Constants.PLAYER_IN_MOVE,"Ezio Auditore",move);
		serverController.updateMove(remoteViewDriver,gamingMsg);
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		assertEquals(game.getPhase().getGamePhase(),GamePhase.MOVE);
		remoteViewDriver.resetCalls();

		// tests if a player can move on the same cell where he is
		move = new NetMove(game.getPlayerTurn().getWorker1().workerID,0,1);
		gamingMsg = new NetGaming(Constants.PLAYER_IN_MOVE,"Ezio Auditore",move);
		serverController.updateMove(remoteViewDriver,gamingMsg);
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		assertEquals(game.getPhase().getGamePhase(),GamePhase.MOVE);
		remoteViewDriver.resetCalls();

		// tests if a possible move can be done
		move = new NetMove(game.getPlayerTurn().getWorker1().workerID,0,2);
		gamingMsg = new NetGaming(Constants.PLAYER_IN_MOVE,"Ezio Auditore",move);
		serverController.updateMove(remoteViewDriver,gamingMsg);
		assertFalse(remoteViewDriver.isCommunicateErrorCalled());
		assertEquals(game.getPhase().getGamePhase(),GamePhase.BUILD);
		remoteViewDriver.resetCalls();

		// tests if a player can move in the build phase
		move = new NetMove(game.getPlayerTurn().getWorker1().workerID,0,0);
		gamingMsg = new NetGaming(Constants.PLAYER_IN_MOVE,"Ezio Auditore",move);
		serverController.updateMove(remoteViewDriver,gamingMsg);
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		assertEquals(game.getPhase().getGamePhase(),GamePhase.BUILD);
		remoteViewDriver.resetCalls();
	}

	@Test
	public void updateMoveWithBuildingGod() throws IllegalAccessException, NoSuchFieldException, WrongPhaseException {
		NetGaming gamingMsg;
		NetMove move;

		resetWithRealGame(false, true);

		// goes to Connor's turn
		game.changeTurn();
		game.changeTurn();
		game.changeTurn();
		// goes to Connor's move phase
		game.changeTurn();

		// try to move in a correct place
		move = new NetMove(game.getPlayerTurn().getWorker1().workerID,3,1);
		gamingMsg = new NetGaming(Constants.PLAYER_IN_MOVE,"Connor",move);
		serverController.updateMove(remoteViewDriver,gamingMsg);
		assertFalse(remoteViewDriver.isCommunicateErrorCalled());
		assertEquals(game.getPhase().getGamePhase(),GamePhase.BUILD);
		remoteViewDriver.resetCalls();
	}

	@Test
	public void updateBuild() throws IllegalAccessException, NoSuchFieldException, WrongPhaseException {
		NetGaming gamingMsg;
		NetBuild build;

		resetWithRealGame(false,true);

		// send the player to build phase
		game.changeTurn();
		game.changeTurn();
		// test if a player can build when it's not its turn with a valid build for its god (now is Ezio Auditore's turn)
		build = new NetBuild(game.getPlayerByName("Connor").getWorker1().workerID,1,3,1,true);
		gamingMsg = new NetGaming(Constants.PLAYER_IN_BUILD,"Connor",build);
		serverController.updateBuild(remoteViewDriver,gamingMsg);
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		assertEquals(game.getPhase().getGamePhase(),GamePhase.BUILD);
		remoteViewDriver.resetCalls();

		// tests if a wrong request is canceled
		build = new NetBuild(game.getPlayerTurn().getWorker1().workerID,1,3,1,true);
		gamingMsg = new NetGaming(Constants.PLAYER_IN_BUILD,"Connor",build);
		serverController.updateBuild(remoteViewDriver,gamingMsg);
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		assertEquals(game.getPhase().getGamePhase(),GamePhase.BUILD);
		remoteViewDriver.resetCalls();

		// tests if the player tries to do an impossible build
		build = new NetBuild(game.getPlayerTurn().getWorker1().workerID,3,4,3,true);
		gamingMsg = new NetGaming(Constants.PLAYER_IN_BUILD,"Ezio Auditore",build);
		serverController.updateBuild(remoteViewDriver,gamingMsg);
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		assertEquals(game.getPhase().getGamePhase(),GamePhase.BUILD);
		remoteViewDriver.resetCalls();

		// tests if a player can build correctly in the build phase
		build = new NetBuild(game.getPlayerTurn().getWorker1().workerID,1,1,1,false);
		gamingMsg = new NetGaming(Constants.PLAYER_IN_BUILD,"Ezio Auditore",build);
		serverController.updateBuild(remoteViewDriver,gamingMsg);
		assertFalse(remoteViewDriver.isCommunicateErrorCalled());
		assertEquals(game.getPhase().getGamePhase(),GamePhase.BEFOREMOVE);
		assertEquals(game.getPlayerTurn().getPlayerName(),"Connor");
		remoteViewDriver.resetCalls();
	}

	@Test
	public void updateMoveAfterPrometheusBuildBeforeMove() throws IllegalAccessException, NoSuchFieldException, WrongPhaseException {
		NetGaming gamingMsg;
		NetMove move;
		NetBuild build;

		resetWithRealGame(true, true);

		// goes to prometheus player owner turn
		game.changeTurn();
		game.changeTurn();
		game.changeTurn();
		game.changeTurn();
		game.changeTurn();
		game.changeTurn();

		// effectuate a build before moving
		build = new NetBuild(game.getPlayerTurn().getWorker1().workerID,4,0,1,false);
		gamingMsg = new NetGaming(Constants.PLAYER_IN_BUILD,"Altair",build);
		serverController.updateBuild(remoteViewDriver,gamingMsg);
		assertFalse(remoteViewDriver.isCommunicateErrorCalled());
		assertEquals(game.getPhase().getGamePhase(),GamePhase.MOVE);
		assertEquals(game.getPlayerTurn().getPlayerName(),"Altair");
		remoteViewDriver.resetCalls();

		// try to move up after building (is impossible for prometheus)
		move = new NetMove(game.getPlayerTurn().getWorker1().workerID,4,0);
		gamingMsg = new NetGaming(Constants.PLAYER_IN_MOVE,"Altair",move);
		serverController.updateMove(remoteViewDriver,gamingMsg);
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		assertEquals(game.getPhase().getGamePhase(),GamePhase.MOVE);
		remoteViewDriver.resetCalls();

		// effectuate a valid move
		move = new NetMove(game.getPlayerTurn().getWorker1().workerID,3,1);
		gamingMsg = new NetGaming(Constants.PLAYER_IN_MOVE,"Altair",move);
		serverController.updateMove(remoteViewDriver,gamingMsg);
		assertFalse(remoteViewDriver.isCommunicateErrorCalled());
		assertEquals(game.getPhase().getGamePhase(),GamePhase.BUILD);
		remoteViewDriver.resetCalls();

		// effectuate the second build
		build = new NetBuild(game.getPlayerTurn().getWorker1().workerID,4,0,1,false);
		gamingMsg = new NetGaming(Constants.PLAYER_IN_BUILD,"Altair",build);
		serverController.updateBuild(remoteViewDriver,gamingMsg);
		assertFalse(remoteViewDriver.isCommunicateErrorCalled());
		assertEquals(game.getPhase().getGamePhase(),GamePhase.BEFOREMOVE);
		assertEquals(game.getPlayerTurn().getPlayerName(),"Ezio Auditore");
		remoteViewDriver.resetCalls();
	}

	@Test
	public void updateQuit() {
		gameStub.setPhase(Phase.COLORS);
		serverController.updateQuit(remoteViewDriver,"Ezio Auditore");
		assertTrue(gameStub.isApplyDisconnectionCalled());
		assertFalse(remoteViewDriver.isCommunicateErrorCalled());
		gameStub.resetCounters();
		remoteViewDriver.resetCalls();

		serverController.updateQuit(remoteViewDriver,"Non existing player");
		assertFalse(gameStub.isApplyDisconnectionCalled());
		assertTrue(remoteViewDriver.isCommunicateErrorCalled());
		gameStub.resetCounters();
		remoteViewDriver.resetCalls();

		gameStub.setPhase(Phase.GODS);
		serverController.updateQuit(remoteViewDriver,"Ezio Auditore");
		assertTrue(gameStub.isApplyDisconnectionCalled());
		assertFalse(remoteViewDriver.isCommunicateErrorCalled());
		gameStub.resetCounters();
		remoteViewDriver.resetCalls();

		gameStub.setPhase(Phase.SETUP);
		serverController.updateQuit(remoteViewDriver,"Ezio Auditore");
		assertTrue(gameStub.isApplyDisconnectionCalled());
		assertFalse(remoteViewDriver.isCommunicateErrorCalled());
		gameStub.resetCounters();
		remoteViewDriver.resetCalls();

		gameStub.setPhase(Phase.PLAYERTURN);
		serverController.updateQuit(remoteViewDriver,"Ezio Auditore");
		assertTrue(gameStub.isApplyDisconnectionCalled());
		assertFalse(remoteViewDriver.isCommunicateErrorCalled());
		gameStub.resetCounters();
		remoteViewDriver.resetCalls();
	}

	@Test
	public void observerQuit() {
		gameStub.addObserver(remoteViewDriver);
		serverController.observerQuit(remoteViewDriver);
		// now if it is deleted and i try to add it as observer i do not generate exceptions
		gameStub.addObserver(remoteViewDriver);
	}
}