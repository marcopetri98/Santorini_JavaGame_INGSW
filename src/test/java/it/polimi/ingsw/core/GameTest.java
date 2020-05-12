package it.polimi.ingsw.core;

import it.polimi.ingsw.core.driver.RemoteViewGameDriver;
import it.polimi.ingsw.core.gods.GodCard;
import it.polimi.ingsw.core.gods.GodCardFactory;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.driver.ServerListenerDriver;
import it.polimi.ingsw.util.Color;
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
	public void setPlayerGod() throws NoSuchFieldException, IllegalAccessException, WrongPhaseException {
		setPhase(GodsPhase.GODS_CHOICE);

		try {
			game.getPlayerByName("Price").getCard();
			fail("The player already has a god and no one has been set");
		} catch (IllegalStateException e) {
			setActivePlayer("Price");
			game.setPlayerGod("Price", "Apollo");
			assertNotNull(game.getPlayerByName("Price").getCard());
			assertEquals(game.getPlayerByName("Price").getCard().getName().toUpperCase(), "APOLLO");
			assertTrue(remoteViewDriver.isUpdateGodsChoiceCalled());
			remoteViewDriver.resetCalled();
		}
	}

	@Test
	public void setGameGods() throws NoSuchFieldException, IllegalAccessException, WrongPhaseException {
		List<String> gods = new ArrayList<>();
		gods.add("apollo");
		gods.add("artemis");
		gods.add("athena");

		setPhase(GodsPhase.CHALLENGER_CHOICE);
		setActivePlayer("Price");
		game.setGameGods(gods);
		assertNotEquals(game.getGods().size(),0);
		assertEquals(game.getGods().size(),3);
		assertEquals(game.getGods().get(0).getName().toUpperCase(),"APOLLO");
		assertEquals(game.getGods().get(1).getName().toUpperCase(),"ARTEMIS");
		assertEquals(game.getGods().get(2).getName().toUpperCase(),"ATHENA");
		assertTrue(remoteViewDriver.isUpdateGodsChallengerCalled());
		remoteViewDriver.resetCalled();
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
	public void setWorkerPositions() {
	}

	@Test
	public void changeTurn() throws WrongPhaseException {
		game.changeTurn();
		assertEquals(game.getPhase().getPhase(),Phase.COLORS);
		assertTrue(remoteViewDriver.isUpdatePhaseChangeCalled());
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
	public void applyMove() {
	}

	@Test
	public void applyBuild() {
	}

	@Test
	public void applyWin() {
	}

	@Test
	public void applyDefeat() {
	}

	@Test
	public void applyDisconnection() {
	}

	@Test
	public void applyWorkerLock() {
	}
}