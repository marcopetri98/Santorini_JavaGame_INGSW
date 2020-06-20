package it.polimi.ingsw.core;

import it.polimi.ingsw.core.driver.RemoteViewGameDriver;
import it.polimi.ingsw.core.gods.GodCardFactory;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.driver.ServerListenerDriver;
import it.polimi.ingsw.network.objects.NetGameSetup;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GameTest {
	private Game game;
	private RemoteViewGameDriver remoteViewDriver;

	@Before
	public void reset() throws IOException {
		String[] playerNames = new String[]{"Price", "Ghost", "Soap"};
		game = new Game(playerNames);
		remoteViewDriver = new RemoteViewGameDriver(new ServerListenerDriver());
		game.addObserver(remoteViewDriver);
	}

	private void setPhase(Phase phase) throws NoSuchFieldException, IllegalAccessException {
		Field turn = Game.class.getDeclaredField("turn");
		turn.setAccessible(true);
		Turn wantedTurn = new Turn();
		while (wantedTurn.getPhase() != phase) {
			wantedTurn.advance();
		}
		turn.set(game,wantedTurn);
	}
	private void setPhase(GodsPhase phase) throws NoSuchFieldException, IllegalAccessException {
		Field turn = Game.class.getDeclaredField("turn");
		turn.setAccessible(true);
		Turn wantedTurn = new Turn();
		while (wantedTurn.getGodsPhase() != phase) {
			wantedTurn.advance();
		}
		turn.set(game,wantedTurn);
	}
	private void setPhase(GamePhase phase) throws NoSuchFieldException, IllegalAccessException {
		Field turn = Game.class.getDeclaredField("turn");
		turn.setAccessible(true);
		Turn wantedTurn = new Turn();
		while (wantedTurn.getGamePhase() != phase) {
			wantedTurn.advance();
		}
		turn.set(game,wantedTurn);
	}
	private void setActivePlayer(String player) throws NoSuchFieldException, IllegalAccessException {
		Field active = Game.class.getDeclaredField("activePlayer");
		active.setAccessible(true);
		Player wantedPlayer = game.getPlayerByName(player);
		active.set(game,wantedPlayer);
	}
	private void completeSetup() throws NoSuchFieldException, IllegalAccessException, WrongPhaseException, IOException {
		List<String> gods = new ArrayList<>();
		gods.add("apollo");
		gods.add("artemis");
		gods.add("atlas");

		reset();
		setPhase(Phase.COLORS);
		setActivePlayer("Price");
		game.setPlayerColor("Price",new Color(255,0,0));
		setActivePlayer("Ghost");
		game.setPlayerColor("Ghost",new Color(0,255,0));
		setActivePlayer("Soap");
		game.setPlayerColor("Soap",new Color(0,0,255));
		setPhase(GodsPhase.CHALLENGER_CHOICE);
		game.setGameGods(gods);
		setPhase(GodsPhase.GODS_CHOICE);
		game.setPlayerGod("Price","APOLLO");
		game.setPlayerGod("Ghost","ARTEMIS");
		game.setPlayerGod("Soap","ATLAS");
		setPhase(Phase.SETUP);
		setActivePlayer("Price");
		NetGameSetup setupMsg = new NetGameSetup(Constants.GAMESETUP_IN_PLACE,"Price",new Pair<Integer,Integer>(0,0),new Pair<Integer,Integer>(1,0));
		game.setWorkerPositions(setupMsg);
		setActivePlayer("Ghost");
		setupMsg = new NetGameSetup(Constants.GAMESETUP_IN_PLACE,"Ghost",new Pair<Integer,Integer>(0,1),new Pair<Integer,Integer>(1,1));
		game.setWorkerPositions(setupMsg);
		setActivePlayer("Soap");
		setupMsg = new NetGameSetup(Constants.GAMESETUP_IN_PLACE,"Soap",new Pair<Integer,Integer>(0,2),new Pair<Integer,Integer>(1,2));
		game.setWorkerPositions(setupMsg);
		setPhase(GamePhase.MOVE);
		remoteViewDriver.resetCalled();
	}

	@Test
	public void setOrder() throws WrongPhaseException {
		List<String> differentOrder = new ArrayList<>();
		differentOrder.add("Price");
		differentOrder.add("Soap");
		differentOrder.add("Ghost");

		game.setOrder(differentOrder);
		for (int i = 0; i < differentOrder.size(); i++) {
			assertEquals(game.getPlayers().get(i).getPlayerName(),differentOrder.get(i));
		}
		assertTrue(remoteViewDriver.isUpdateOrderCalled());
		remoteViewDriver.resetCalled();

		differentOrder.clear();
		differentOrder.add("Ghost");
		differentOrder.add("Soap");
		differentOrder.add("Price");
		game.setOrder(differentOrder);
		for (int i = 0; i < differentOrder.size(); i++) {
			assertEquals(game.getPlayers().get(i).getPlayerName(),differentOrder.get(i));
		}
		assertTrue(remoteViewDriver.isUpdateOrderCalled());
	}

	@Test
	public void setPlayerColor() throws NoSuchFieldException, IllegalAccessException, WrongPhaseException {
		setPhase(Phase.COLORS);

		setActivePlayer("Price");
		game.setPlayerColor("Price",Color.RED);
		assertTrue(remoteViewDriver.isUpdateColorsCalled());
		remoteViewDriver.resetCalled();
		setActivePlayer("Ghost");
		game.setPlayerColor("Ghost",Color.GREEN);
		assertTrue(remoteViewDriver.isUpdateColorsCalled());
		remoteViewDriver.resetCalled();
		setActivePlayer("Soap");
		game.setPlayerColor("Soap",Color.BLUE);
		assertTrue(remoteViewDriver.isUpdateColorsCalled());
		remoteViewDriver.resetCalled();
		for (int i = 0; i < game.getPlayers().size(); i++) {
			if (game.getPlayers().get(i).getPlayerName().equals("Price")) {
				assertEquals(game.getPlayers().get(i).getWorker1().color,Color.RED);
			} else if (game.getPlayers().get(i).getPlayerName().equals("Ghost")) {
				assertEquals(game.getPlayers().get(i).getWorker1().color,Color.GREEN);
			} else {
				assertEquals(game.getPlayers().get(i).getWorker1().color,Color.BLUE);
			}
		}
	}

	@Test
	public void setGameGods() throws NoSuchFieldException, IllegalAccessException, WrongPhaseException {
		List<String> gods = new ArrayList<>();
		gods.add("apollo");
		gods.add("artemis");
		gods.add("atlas");

		setPhase(GodsPhase.CHALLENGER_CHOICE);
		setActivePlayer("Price");
		game.setGameGods(gods);
		assertNotEquals(game.getGods().size(),0);
		assertEquals(game.getGods().size(),3);
		assertEquals(game.getGods().get(0).getName().toUpperCase(),"APOLLO");
		assertEquals(game.getGods().get(1).getName().toUpperCase(),"ARTEMIS");
		assertEquals(game.getGods().get(2).getName().toUpperCase(),"ATLAS");
		assertTrue(remoteViewDriver.isUpdateGodsChallengerCalled());
		remoteViewDriver.resetCalled();

		assertEquals(game.getGods().get(0).getName().toUpperCase(),"APOLLO");
		assertEquals(game.getGods().get(1).getName().toUpperCase(),"ARTEMIS");
		assertEquals(game.getGods().get(2).getName().toUpperCase(),"ATLAS");
	}

	@Test
	public void setPlayerGod() throws NoSuchFieldException, IllegalAccessException, WrongPhaseException {
		List<String> gods = new ArrayList<>();
		gods.add("apollo");
		gods.add("artemis");
		gods.add("atlas");

		setPhase(GodsPhase.GODS_CHOICE);
		try {
			game.getPlayerByName("Price").getCard();
			fail("The player already has a god and no one has been set");
		} catch (IllegalStateException e) {
			setActivePlayer("Price");
			game.setGameGods(gods);
			remoteViewDriver.resetCalled();
			game.setPlayerGod("Price", "Apollo");
			assertNotNull(game.getPlayerByName("Price").getCard());
			assertEquals(game.getPlayerByName("Price").getCard().getName().toUpperCase(), "APOLLO");
			assertTrue(remoteViewDriver.isUpdateGodsChoiceCalled());
			remoteViewDriver.resetCalled();
		}
	}

	@Test
	public void setStarter() throws NoSuchFieldException, IllegalAccessException, WrongPhaseException {
		List<Player> beforeStarter = game.getPlayers();
		List<Player> afterStarter;
		Player starter = game.getPlayerByName("Ghost");

		setPhase(GodsPhase.STARTER_CHOICE);
		setActivePlayer("Price");

		game.setStarter("Ghost");
		afterStarter = game.getPlayers();
		assertEquals(afterStarter.indexOf(starter),0);
		// it contains all previous player and ordered in the right clockwise order
		for (Player player : beforeStarter) {
			int playerIndex = beforeStarter.indexOf(player)-beforeStarter.indexOf(starter);
			int beforeNewIndex = beforeStarter.size()-beforeStarter.indexOf(starter)+beforeStarter.indexOf(player);
			assertTrue(afterStarter.contains(player));
			assertTrue(playerIndex >= 0 ? afterStarter.get(playerIndex).equals(player) : afterStarter.get(beforeNewIndex).equals(player));
		}
		assertEquals(game.getPlayers().get(0).getPlayerName(),"Ghost");
	}

	@Test
	public void setWorkerPositions() throws NoSuchFieldException, IllegalAccessException, WrongPhaseException {
		List<String> gods = new ArrayList<>();
		gods.add("apollo");
		gods.add("artemis");
		gods.add("atlas");

		// setting up the game
		setPhase(Phase.COLORS);
		setActivePlayer("Price");
		game.setPlayerColor("Price",new Color(255,0,0));
		setActivePlayer("Ghost");
		game.setPlayerColor("Ghost",new Color(0,255,0));
		setActivePlayer("Soap");
		game.setPlayerColor("Soap",new Color(0,0,255));
		setPhase(GodsPhase.CHALLENGER_CHOICE);
		game.setGameGods(gods);
		setPhase(GodsPhase.GODS_CHOICE);
		game.setPlayerGod("Price","APOLLO");
		game.setPlayerGod("Ghost","ARTEMIS");
		game.setPlayerGod("Soap","ATLAS");

		// setting up the map
		setActivePlayer("Price");
		setPhase(Phase.SETUP);
		Integer x1 = 0, y1 = 0, x2 = 1, y2 = 1;
		NetGameSetup setupMsg = new NetGameSetup(Constants.GAMESETUP_IN_PLACE,"Price",new Pair<Integer,Integer>(x1,y1),new Pair<Integer,Integer>(x2,y2));
		remoteViewDriver.resetCalled();
		game.setWorkerPositions(setupMsg);
		assertTrue(remoteViewDriver.isUpdatePositionsCalled());
		assertNotNull(game.getMap().getCell(0,0).getWorker());
		assertEquals(game.getPlayerByName("Price").getWorker1().getPos(),game.getMap().getCell(0,0));
		assertEquals(game.getPlayerByName("Price").getWorker2().getPos(),game.getMap().getCell(1,1));
		assertEquals(game.getMap().getCell(0,0).getWorker(),game.getPlayerByName("Price").getWorker1());
		assertEquals(game.getMap().getCell(1,1).getWorker(),game.getPlayerByName("Price").getWorker2());
	}

	@Test
	public void changeTurn() throws WrongPhaseException {
		game.changeTurn();
		assertEquals(game.getPhase().getPhase(),Phase.COLORS);
		assertTrue(remoteViewDriver.updatePhaseChangeCalled);
		assertTrue(remoteViewDriver.updateActivePlayerCalled);
		remoteViewDriver.resetCalled();

		// all three players has chosen the color
		game.changeTurn();
		assertEquals(game.getPhase().getPhase(),Phase.COLORS);
		assertTrue(remoteViewDriver.isUpdateActivePlayerCalled());
		remoteViewDriver.resetCalled();

		game.changeTurn();
		assertEquals(game.getPhase().getPhase(),Phase.COLORS);
		assertTrue(remoteViewDriver.isUpdateActivePlayerCalled());
		remoteViewDriver.resetCalled();

		game.changeTurn();
		assertEquals(game.getPhase().getPhase(),Phase.GODS);
		assertEquals(game.getPhase().getGodsPhase(),GodsPhase.CHALLENGER_CHOICE);
		assertTrue(remoteViewDriver.updatePhaseChangeCalled);
		assertTrue(remoteViewDriver.updateActivePlayerCalled);
		remoteViewDriver.resetCalled();

		// the challenger chooses 3 gods
		game.changeTurn();
		assertEquals(game.getPhase().getPhase(),Phase.GODS);
		assertEquals(game.getPhase().getGodsPhase(),GodsPhase.GODS_CHOICE);
		assertTrue(remoteViewDriver.updatePhaseChangeCalled);
		assertTrue(remoteViewDriver.updateActivePlayerCalled);
		remoteViewDriver.resetCalled();
		// all 3 player choose a god
		game.changeTurn();
		assertEquals(game.getPhase().getPhase(),Phase.GODS);
		assertEquals(game.getPhase().getGodsPhase(),GodsPhase.GODS_CHOICE);
		assertTrue(remoteViewDriver.isUpdateActivePlayerCalled());
		remoteViewDriver.resetCalled();

		game.changeTurn();
		assertEquals(game.getPhase().getPhase(),Phase.GODS);
		assertEquals(game.getPhase().getGodsPhase(),GodsPhase.GODS_CHOICE);
		assertTrue(remoteViewDriver.isUpdateActivePlayerCalled());
		remoteViewDriver.resetCalled();

		game.changeTurn();
		assertEquals(game.getPhase().getPhase(),Phase.GODS);
		assertEquals(game.getPhase().getGodsPhase(),GodsPhase.STARTER_CHOICE);
		assertTrue(remoteViewDriver.updatePhaseChangeCalled);
		assertTrue(remoteViewDriver.updateActivePlayerCalled);
		remoteViewDriver.resetCalled();

		game.setStarter("Ghost");
		assertEquals(game.getPlayers().get(0).getPlayerName(),"Ghost");
		// the challenger chooses the starter
		game.changeTurn();
		assertEquals(game.getPhase().getPhase(),Phase.SETUP);
		assertTrue(remoteViewDriver.updatePhaseChangeCalled);
		assertTrue(remoteViewDriver.updateActivePlayerCalled);
		remoteViewDriver.resetCalled();

		// all 3 players set up the their workers on the table
		game.changeTurn();
		assertEquals(game.getPhase().getPhase(),Phase.SETUP);
		assertTrue(remoteViewDriver.isUpdateActivePlayerCalled());
		remoteViewDriver.resetCalled();

		game.changeTurn();
		assertEquals(game.getPhase().getPhase(),Phase.SETUP);
		assertTrue(remoteViewDriver.isUpdateActivePlayerCalled());
		remoteViewDriver.resetCalled();

		game.changeTurn();
		assertEquals(game.getPhase().getPhase(),Phase.PLAYERTURN);
		assertEquals(game.getPhase().getGamePhase(),GamePhase.BEFOREMOVE);
		assertTrue(remoteViewDriver.updatePhaseChangeCalled);
		assertTrue(remoteViewDriver.updateActivePlayerCalled);
		remoteViewDriver.resetCalled();

		// sets the player workers and worker position to pass the turn
		game.getPlayers().get(0).setPlayerColor(Color.RED);
		game.getPlayers().get(0).setPlayerColor(Color.GREEN);
		game.getPlayers().get(0).setPlayerColor(Color.BLUE);
		game.getPlayers().get(0).setGodCard(GodCardFactory.createGodCard("APOLLO"));
		game.getPlayers().get(0).setGodCard(GodCardFactory.createGodCard("ARTEMIS"));
		game.getPlayers().get(0).setGodCard(GodCardFactory.createGodCard("ATHENA"));

		// a player execute a complete turn
		game.changeTurn();
		assertEquals(game.getPhase().getPhase(),Phase.PLAYERTURN);
		assertEquals(game.getPhase().getGamePhase(),GamePhase.MOVE);
		assertTrue(remoteViewDriver.isUpdatePhaseChangeCalled());
		remoteViewDriver.resetCalled();

		game.changeTurn();
		assertEquals(game.getPhase().getPhase(),Phase.PLAYERTURN);
		assertEquals(game.getPhase().getGamePhase(),GamePhase.BUILD);
		assertTrue(remoteViewDriver.isUpdatePhaseChangeCalled());
		remoteViewDriver.resetCalled();

		game.changeTurn();
		assertEquals(game.getPhase().getPhase(),Phase.PLAYERTURN);
		assertEquals(game.getPhase().getGamePhase(),GamePhase.BEFOREMOVE);
		assertTrue(remoteViewDriver.updatePhaseChangeCalled);
		assertTrue(remoteViewDriver.updateActivePlayerCalled);
		remoteViewDriver.resetCalled();
	}

	@Test
	public void applyMove() throws IOException, WrongPhaseException, IllegalAccessException, NoSuchFieldException {
		completeSetup();

		// effectuate a simple move (moves a worker from 1,0 to 2,0)
		setActivePlayer("Price");
		Move myMove = new Move(TypeMove.SIMPLE_MOVE,game.getPlayerByName("Price").getWorker2().getPos(),game.getMap().getCell(2,0),game.getPlayerByName("Price").getWorker2());
		game.applyMove(myMove);
		assertNull(myMove.getCellPrev().getWorker());
		assertEquals(game.getMap().getCell(2,0).getWorker(),game.getPlayerByName("Price").getWorker2());
		assertEquals(game.getPlayerByName("Price").getWorker2().getPos(),game.getMap().getCell(2,0));
		assertTrue(remoteViewDriver.isUpdateMoveCalled());
		remoteViewDriver.resetCalled();

		// effectuate a conditioned move (moves a worker from 0,0 to 1,0 when there is another worker on 1,0)
		setActivePlayer("Price");
		myMove = new Move(TypeMove.CONDITIONED_MOVE,game.getPlayerByName("Price").getWorker1().getPos(),game.getMap().getCell(0,1),game.getPlayerByName("Price").getWorker1());
		myMove.setCondition(new Move(TypeMove.SIMPLE_MOVE,game.getPlayerByName("Ghost").getWorker1().getPos(),game.getMap().getCell(0,0),game.getPlayerByName("Ghost").getWorker1()));
		Worker conditionedWorker = game.getMap().getCell(0,1).getWorker();
		game.applyMove(myMove);
		assertEquals(game.getMap().getCell(0,1).getWorker(),game.getPlayerByName("Price").getWorker1());
		assertEquals(game.getPlayerByName("Price").getWorker1().getPos(),game.getMap().getCell(0,1));
		assertEquals(game.getMap().getCell(0,0).getWorker(),conditionedWorker);
		assertEquals(conditionedWorker.getPos(),game.getMap().getCell(0,0));
		assertTrue(remoteViewDriver.isUpdateMoveCalled());
		remoteViewDriver.resetCalled();
	}

	@Test
	public void applyBuild() throws IOException, WrongPhaseException, IllegalAccessException, NoSuchFieldException {
		completeSetup();

		// effectuate a simple build (simply increase height of the building)
		setActivePlayer("Price");
		Build myBuild = new Build(game.getPlayerByName("Price").getWorker2(),game.getMap().getCell(2,0),false,TypeBuild.SIMPLE_BUILD);
		Building building = game.getMap().getCell(2,0).getBuilding();
		int levelBefore = building.getLevel();
		game.applyBuild(myBuild);
		assertEquals(levelBefore+1,building.getLevel());
		assertTrue(remoteViewDriver.isUpdateBuildCalled());
		remoteViewDriver.resetCalled();

		// make the building be a dome
		game.applyBuild(myBuild);
		game.applyBuild(myBuild);
		myBuild = new Build(game.getPlayerByName("Price").getWorker2(),game.getMap().getCell(2,0),true,TypeBuild.SIMPLE_BUILD);
		building = game.getMap().getCell(2,0).getBuilding();
		boolean domeBefore = building.getDome();
		game.applyBuild(myBuild);
		assertNotEquals(domeBefore,building.getDome());
		assertTrue(remoteViewDriver.isUpdateBuildCalled());
		remoteViewDriver.resetCalled();

		// effectuate a conditioned build (builds two times in different places)
		setActivePlayer("Soap");
		myBuild = new Build(game.getPlayerByName("Soap").getWorker2(),game.getMap().getCell(2,2),false,TypeBuild.CONDITIONED_BUILD);
		myBuild.setCondition(new Build(game.getPlayerByName("Soap").getWorker2(),game.getMap().getCell(1,3),false,TypeBuild.SIMPLE_BUILD));
		Building building1 = game.getMap().getCell(2,2).getBuilding();
		Building building2 = game.getMap().getCell(1,3).getBuilding();
		int levelBefore1 = building1.getLevel();
		int levelBefore2 = building2.getLevel();
		game.applyBuild(myBuild);
		assertEquals(levelBefore1+1,building1.getLevel());
		assertEquals(levelBefore2+1,building2.getLevel());
		assertTrue(remoteViewDriver.isUpdateBuildCalled());
		remoteViewDriver.resetCalled();

		// effectuate a conditioned build (two times in the same place)
		setActivePlayer("Soap");
		myBuild = new Build(game.getPlayerByName("Soap").getWorker2(),game.getMap().getCell(2,3),false,TypeBuild.CONDITIONED_BUILD);
		myBuild.setCondition(new Build(game.getPlayerByName("Soap").getWorker2(),game.getMap().getCell(2,3),false,TypeBuild.SIMPLE_BUILD));
		building = game.getMap().getCell(2,3).getBuilding();
		levelBefore = building.getLevel();
		game.applyBuild(myBuild);
		assertEquals(levelBefore+2,building.getLevel());
		assertTrue(remoteViewDriver.isUpdateBuildCalled());
		remoteViewDriver.resetCalled();
	}

	@Test
	public void applyWin() throws IOException, WrongPhaseException, IllegalAccessException, NoSuchFieldException {
		completeSetup();
		game.applyWin(game.getPlayerByName("Price"));
		assertTrue(game.isFinished());
		assertEquals(game.getWinner(),game.getPlayerByName("Price"));
		assertTrue(remoteViewDriver.isUpdateWinnerCalled());
	}

	@Test
	public void applyDefeat() throws IOException, WrongPhaseException, IllegalAccessException, NoSuchFieldException {
		Player defeatedPlayer;
		Worker defeatedWorker1, defeatedWorker2;
		Cell cellWorker1, cellWorker2;

		// defeat of a playing player without terminating the game
		completeSetup();
		setActivePlayer("Price");
		defeatedPlayer = game.getPlayerByName("Price");
		defeatedWorker1 = defeatedPlayer.getWorker1();
		defeatedWorker2 = defeatedPlayer.getWorker2();
		cellWorker1 = defeatedWorker1.getPos();
		cellWorker2 = defeatedWorker2.getPos();
		game.applyDefeat(game.getPlayerByName("Price"));
		assertFalse(game.isFinished());
		assertFalse(game.getPlayers().contains(defeatedPlayer));
		assertEquals(game.getPlayers().size(),2);
		assertEquals(game.getPlayerTurn(),game.getPlayerByName("Ghost"));
		assertNull(defeatedWorker1.getPos());
		assertNull(defeatedWorker2.getPos());
		assertNull(cellWorker1.getWorker());
		assertNull(cellWorker2.getWorker());
		assertTrue(remoteViewDriver.updateDefeatCalled);
		assertTrue(remoteViewDriver.updateActivePlayerCalled);
		remoteViewDriver.resetCalled();

		// defeat of a waiting player (it isn't its turn) without terminating the game
		completeSetup();
		setActivePlayer("Price");
		defeatedPlayer = game.getPlayerByName("Ghost");
		defeatedWorker1 = defeatedPlayer.getWorker1();
		defeatedWorker2 = defeatedPlayer.getWorker2();
		cellWorker1 = defeatedWorker1.getPos();
		cellWorker2 = defeatedWorker2.getPos();
		game.applyDefeat(game.getPlayerByName("Ghost"));
		assertFalse(game.isFinished());
		//assertFalse(game.getPlayers().contains(defeatedPlayer));
		//assertEquals(game.getPlayers().size(),2);
		assertEquals(game.getPlayerTurn(),game.getPlayerByName("Price"));
		//assertNull(defeatedWorker1.getPos());
		//assertNull(defeatedWorker2.getPos());
		//assertNull(cellWorker1.getWorker());
		//assertNull(cellWorker2.getWorker());
		assertTrue(remoteViewDriver.isUpdateDefeatCalled());
		remoteViewDriver.resetCalled();

		// someone has been defeated and there is a winner for this reason
		setActivePlayer("Soap");
		defeatedPlayer = game.getPlayerByName("Soap");
		defeatedWorker1 = defeatedPlayer.getWorker1();
		defeatedWorker2 = defeatedPlayer.getWorker2();
		cellWorker1 = defeatedWorker1.getPos();
		cellWorker2 = defeatedWorker2.getPos();
		game.applyDefeat(game.getPlayerByName("Soap"));
//		assertEquals(game.getWinner().getPlayerName(),"Price");
//		//assertTrue(game.isFinished());
//		assertFalse(game.getPlayers().contains(defeatedPlayer));
//		assertNull(defeatedWorker1.getPos());
//		assertNull(defeatedWorker2.getPos());
//		assertNull(cellWorker1.getWorker());
//		assertNull(cellWorker2.getWorker());
//		assertTrue(remoteViewDriver.updateDefeatCalled);
//		assertTrue(remoteViewDriver.updateWinnerCalled);
		remoteViewDriver.resetCalled();
	}

	@Test
	public void applyDisconnection() throws NoSuchFieldException, IllegalAccessException, IOException, WrongPhaseException {
		Player removedPlayer;
		Worker removedWorker1, removedWorker2;
		Cell cellWorker1, cellWorker2;

		setPhase(Phase.LOBBY);
		game.applyDisconnection("Price");
		assertTrue(game.isFinished());
		assertTrue(remoteViewDriver.isUpdateGameFinishedCalled());

		reset();
		setPhase(Phase.COLORS);
		game.applyDisconnection("Ghost");
		assertTrue(game.isFinished());
		assertTrue(remoteViewDriver.isUpdateGameFinishedCalled());

		reset();
		setPhase(Phase.GODS);
		game.applyDisconnection("Soap");
		assertTrue(game.isFinished());
		assertTrue(remoteViewDriver.isUpdateGameFinishedCalled());

		reset();
		setPhase(Phase.SETUP);
		game.applyDisconnection("Price");
		assertTrue(game.isFinished());
		assertTrue(remoteViewDriver.isUpdateGameFinishedCalled());

		// call the setup
		completeSetup();

		// now that if the players are 3 we can continue playing if one has disconnected
		setPhase(Phase.PLAYERTURN);
		setActivePlayer("Price");
		removedPlayer = game.getPlayerByName("Price");
		removedWorker1 = removedPlayer.getWorker1();
		removedWorker2 = removedPlayer.getWorker2();
		cellWorker1 = removedWorker1.getPos();
		cellWorker2 = removedWorker2.getPos();
		game.applyDisconnection("Price");
		assertFalse(game.getPlayers().contains(removedPlayer));
		//assertFalse(game.isFinished());
		//assertEquals(game.getPlayerByName("Ghost"),game.getPlayerTurn());
		assertEquals(game.getPlayers().size(),2);
		assertNull(removedWorker1.getPos());
		assertNull(removedWorker2.getPos());
		assertNull(cellWorker1.getWorker());
		assertNull(cellWorker2.getWorker());
		assertTrue(remoteViewDriver.updateQuitCalled);
		//assertTrue(remoteViewDriver.updateActivePlayerCalled);
		remoteViewDriver.resetCalled();

		// call the setup
		completeSetup();

		// now a player which is not in the active turn disconnect
		setPhase(Phase.PLAYERTURN);
		setActivePlayer("Price");
		removedPlayer = game.getPlayerByName("Ghost");
		removedWorker1 = removedPlayer.getWorker1();
		removedWorker2 = removedPlayer.getWorker2();
		cellWorker1 = removedWorker1.getPos();
		cellWorker2 = removedWorker2.getPos();
		game.applyDisconnection("Ghost");
		assertFalse(game.getPlayers().contains(removedPlayer));
		//assertFalse(game.isFinished());
		assertEquals(game.getPlayerByName("Price"),game.getPlayerTurn());
		assertEquals(game.getPlayers().size(),2);
		assertNull(removedWorker1.getPos());
		assertNull(removedWorker2.getPos());
		assertNull(cellWorker1.getWorker());
		assertNull(cellWorker2.getWorker());
		assertTrue(remoteViewDriver.isUpdateQuitCalled());
		remoteViewDriver.resetCalled();

		// now a player disconnect when there are only 2 players
		setActivePlayer("Price");
		removedPlayer = game.getPlayerByName("Soap");
		removedWorker1 = removedPlayer.getWorker1();
		removedWorker2 = removedPlayer.getWorker2();
		cellWorker1 = removedWorker1.getPos();
		cellWorker2 = removedWorker2.getPos();
		game.applyDisconnection("Soap");
		assertFalse(game.getPlayers().contains(removedPlayer));
		assertTrue(game.isFinished());
		assertNull(removedWorker1.getPos());
		assertNull(removedWorker2.getPos());
		assertNull(cellWorker1.getWorker());
		assertNull(cellWorker2.getWorker());
		//assertTrue(remoteViewDriver.updateQuitCalled);
		//assertTrue(remoteViewDriver.updateWinnerCalled);
		remoteViewDriver.resetCalled();
	}

	@Test
	public void applyWorkerLock() throws NoSuchFieldException, IllegalAccessException, WrongPhaseException {
		setPhase(Phase.COLORS);

		game.setPlayerColor("Price",Color.RED);
		game.applyWorkerLock(game.getPlayerByName("Price"),1);
		assertEquals(game.getPlayerByName("Price").getWorker1(), game.getPlayerByName("Price").getActiveWorker());

		game.getPlayerByName("Price").resetLocking();
		game.applyWorkerLock(game.getPlayerByName("Price"),2);
		assertEquals(game.getPlayerByName("Price").getWorker2(), game.getPlayerByName("Price").getActiveWorker());
	}
}