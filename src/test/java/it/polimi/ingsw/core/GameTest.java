package it.polimi.ingsw.core;

import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GameTest {
	private Game game;

	@Before
	public void reset() {
		game = new Game(new String[]{"Price", "Ghost", "Soap"});
	}

	private void setGamePhase(Phase phase) throws NoSuchFieldException, IllegalAccessException {
		Field turn = Game.class.getDeclaredField("turn");
		turn.setAccessible(true);
		Turn wantedTurn = new Turn();
		while (wantedTurn.getPhase() != phase) {
			wantedTurn.advance();
		}
		turn.set(game,wantedTurn);
	}
	private void setGodsPhase(GodsPhase phase) throws NoSuchFieldException, IllegalAccessException {
		Field turn = Game.class.getDeclaredField("turn");
		turn.setAccessible(true);
		Turn wantedTurn = new Turn();
		while (wantedTurn.getGodsPhase() != phase) {
			wantedTurn.advance();
		}
		turn.set(game,wantedTurn);
	}
	private void setGamePhase(GamePhase phase) throws NoSuchFieldException, IllegalAccessException {
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

		differentOrder.clear();
		differentOrder.add("Ghost");
		differentOrder.add("Soap");
		differentOrder.add("Price");
		game.setOrder(differentOrder);
		for (int i = 0; i < differentOrder.size(); i++) {
			assertEquals(game.getPlayers().get(i).getPlayerName(),differentOrder.get(i));
		}
	}

	@Test
	public void setPlayerColor() throws NoSuchFieldException, IllegalAccessException, WrongPhaseException {
		setGamePhase(Phase.COLORS);

		setActivePlayer("Price");
		game.setPlayerColor("Price",Color.RED);
		setActivePlayer("Ghost");
		game.setPlayerColor("Ghost",Color.GREEN);
		setActivePlayer("Soap");
		game.setPlayerColor("Soap",Color.BLUE);
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
	public void setPlayerGod() {

	}

	@Test
	public void setGameGods() {
	}

	@Test
	public void setStarter() {
	}

	@Test
	public void setWorkerPositions() {
	}

	@Test
	public void changeTurn() {
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