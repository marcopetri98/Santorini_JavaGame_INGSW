package it.polimi.ingsw.controller.stub;

import it.polimi.ingsw.core.*;
import it.polimi.ingsw.core.gods.Apollo;
import it.polimi.ingsw.core.gods.GodCard;
import it.polimi.ingsw.core.gods.GodCardFactory;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.objects.NetGameSetup;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;

import javax.print.DocFlavor;
import it.polimi.ingsw.util.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GameStub extends Game {
	private boolean applyMoveCalled;
	private boolean applyBuildCalled;
	private boolean applyWinCalled;
	private boolean applyDefeatCalled;
	private boolean setOrderCalledCorrectly;
	private boolean setPlayerColorCalled;
	private boolean setGameGodsCalled;
	private boolean setPlayerGodCalled;
	private boolean setStarterCalled;
	private boolean setWorkerPositionsCalled;
	private Player activePlayer;
	private List<Player> players;
	private List<GodCard> godCards;
	private Map map;
	private List<Player> defeatedPlayers;
	private Player winner;
	private Turn turn;

	public GameStub(String[] names, boolean setColors, boolean setGods) {
		super(names);
		applyMoveCalled = false;
		applyBuildCalled = false;
		applyWinCalled = false;
		applyDefeatCalled = false;
		setOrderCalledCorrectly = false;
		setPlayerColorCalled = false;
		setGameGodsCalled = false;
		setPlayerGodCalled = false;
		setStarterCalled = false;
		setWorkerPositionsCalled = false;
		players = new ArrayList<>();
		godCards = new ArrayList<>();
		map = new Map();
		defeatedPlayers = new ArrayList<>();
		turn = new Turn();
		try {
			Method playerColor = Player.class.getDeclaredMethod("setPlayerColor", Color.class);
			playerColor.setAccessible(true);
			Method playerGod = Player.class.getDeclaredMethod("setGodCard", GodCard.class);
			playerGod.setAccessible(true);
			for (int i = 0; i < names.length; i++) {
				players.add(new Player(names[i]));
				if (setColors) {
					playerColor.invoke(players.get(i), Constants.COLOR_COLORS.get(i));
				}
				if (setGods) {
					godCards.add(GodCardFactory.createGodCard(Constants.GODS_GOD_NAMES.get(i)));
					playerGod.invoke(players.get(i),godCards.get(i));
				}
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new AssertionError("Design error");
		}
	}

	// stub methods
	public void applyMove(Move move) {
		applyMoveCalled = true;
	}
	public void applyBuild(Build build) {
		applyBuildCalled = true;
	}
	public void applyWin(Player player) {
		applyWinCalled = true;
	}
	public void applyDefeat(Player player) {
		applyDefeatCalled = true;
	}
	public void setOrder(List<String> playerOrder) throws IllegalArgumentException, WrongPhaseException {
		if (playerOrder == null || playerOrder.size() != players.size()) {
			throw new IllegalArgumentException();
		} else if (turn.getPhase() != Phase.LOBBY) {
			throw new WrongPhaseException();
		} else {
			for (Player player : players) {
				if (!playerOrder.contains(player.getPlayerName())) {
					throw new IllegalArgumentException();
				}
			}
		}
		setOrderCalledCorrectly = true;
	}
	public void setPlayerColor(String player, Color color) throws IllegalArgumentException, WrongPhaseException {
		if (player == null || color == null) {
			throw new IllegalArgumentException();
		} else if (turn.getPhase() != Phase.COLORS) {
			throw new WrongPhaseException();
		}
		setPlayerColorCalled = true;
	}
	public void setPlayerGod(String playerName, String god) throws IllegalArgumentException, WrongPhaseException {
		GodCard playerGod = null;
		if (playerName == null || god == null || !Constants.GODS_GOD_NAMES.contains(god)) {
			throw new IllegalArgumentException();
		} else if (turn.getPhase() != Phase.GODS) {
			throw new WrongPhaseException();
		} else {
			boolean godFound = false;
			for (GodCard card : godCards) {
				if (card.getName().equals(god)) {
					godFound = true;
					playerGod = card;
				}
			}

			if (godFound) {
				throw new IllegalArgumentException();
			}
		}
		setPlayerGodCalled = true;
	}
	public void setGameGods(List<String> godNames) throws IllegalArgumentException, WrongPhaseException {
		if (godNames == null || godNames.size() != players.size()) {
			throw new IllegalArgumentException();
		} else if (turn.getPhase() != Phase.GODS && turn.getGodsPhase() != GodsPhase.CHALLENGER_CHOICE) {
			throw new WrongPhaseException();
		} else {
			for (String godName : godNames) {
				if (!Constants.GODS_GOD_NAMES.contains(godName)) {
					throw new IllegalArgumentException();
				}
			}
		}
		setGameGodsCalled = true;
	}
	public void setStarter(String starterName) throws IllegalStateException, WrongPhaseException {
		Player starter = null;
		if (starterName == null) {
			throw new IllegalStateException();
		} else if (turn.getPhase() != Phase.GODS || (turn.getPhase() == Phase.GODS && turn.getGodsPhase() != GodsPhase.STARTER_CHOICE)) {
			throw new WrongPhaseException();
		} else {
			boolean found = false;
			for (int i = 0; i < players.size() && !found; i++) {
				if (players.get(i).getPlayerName().equals(starterName)) {
					found = true;
					starter = players.get(i);
				}
			}
			if (!found) {
				throw new IllegalStateException();
			}
		}
		setStarterCalled = true;
	}
	public void setWorkerPositions(NetGameSetup req) throws IllegalArgumentException, WrongPhaseException {
		if (req == null || !req.isWellFormed()) {
			throw new IllegalArgumentException();
		} else if (turn.getPhase() != Phase.SETUP) {
			throw new WrongPhaseException();
		} else if (map.getCell(req.worker1.getFirst(),req.worker1.getSecond()).getWorker() != null || map.getCell(req.worker2.getFirst(),req.worker2.getSecond()).getWorker() != null) {
			throw new IllegalArgumentException();
		}
		setWorkerPositionsCalled = true;
	}

	// stub getters
	public synchronized Map getMap() {
		return map;
	}
	public synchronized int getPlayerNum() {
		return players.size();
	}
	public synchronized Player getPlayerByName(String name) throws IllegalArgumentException {
		for (Player p : players) {
			if (p.getPlayerName().equals(name)) {
				return p;
			}
		}
		throw new IllegalArgumentException();
	}
	public synchronized List<Player> getPlayers() {
		return players;
	}
	public synchronized Player getPlayerTurn() {
		return activePlayer;
	}
	public synchronized Color getPlayerColor(String player) throws IllegalArgumentException, IllegalStateException{
		if (player == null) {
			throw new IllegalArgumentException();
		}
		int i;
		boolean found = false;
		for (i = 0; i < players.size() && !found; i++) {
			if (players.get(i).getPlayerName().equals(player)) {
				found = true;
			}
		}
		if (i == players.size()) {
			throw new IllegalArgumentException();
		} else {
			return players.get(i).getWorker1().color;
		}
	}
	public synchronized GodCard getPlayerGodCard(String player) throws IllegalArgumentException, IllegalStateException  {
		int i;
		boolean found = false;
		for (i = 0; i < players.size() && !found; i++) {
			if (players.get(i).getPlayerName().equals(player)) {
				found = true;
			}
		}
		if (i == players.size()) {
			throw new IllegalArgumentException();
		} else {
			return players.get(i).getCard();
		}
	}
	public synchronized Turn getPhase() {
		return turn;
	}

	// check methods
	public void resetCounters() {
		applyMoveCalled = false;
		applyBuildCalled = false;
		applyWinCalled = false;
		applyDefeatCalled = false;
		setOrderCalledCorrectly = false;
		setPlayerColorCalled = false;
		setGameGodsCalled = false;
		setPlayerGodCalled = false;
		setStarterCalled = false;
		setWorkerPositionsCalled = false;
	}
	public void setPhase(Phase ph) {
		while (turn.getPhase() != ph) {
			turn.advance();
		}
	}
	public void setPhase(GodsPhase ph) {
		while (turn.getGodsPhase() != ph) {
			turn.advance();
		}
	}
	public void setPhase(GamePhase ph) {
		while (turn.getGamePhase() != ph) {
			turn.advance();
		}
	}
	public void setActivePlayer(Player player) {
		activePlayer = players.get(players.indexOf(player));
	}
	public void setActivePlayer(String player) {
		activePlayer = getPlayerByName(player);
	}
	public void setAColor() {
		try {
			Method playerColor = Player.class.getDeclaredMethod("setPlayerColor", Color.class);
			playerColor.setAccessible(true);
			for (int i = 0; i < players.size(); i++) {
				try {
					players.get(i).getWorker1();
				} catch (IllegalStateException e) {
					playerColor.invoke(players.get(i), Constants.COLOR_COLORS.get(i));
				}
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new AssertionError("Design error");
		}
	}
	public void setAGod() {
		try {
			Method playerGod = Player.class.getDeclaredMethod("setGodCard", GodCard.class);
			playerGod.setAccessible(true);
			for (int i = 0; i < players.size(); i++) {
				try {
					players.get(i).getCard();
				} catch (IllegalStateException e) {
					godCards.add(GodCardFactory.createGodCard(Constants.GODS_GOD_NAMES.get(i)));
					playerGod.invoke(players.get(i),godCards.get(godCards.size()-1));
				}
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new AssertionError("Design error");
		}
	}
	public boolean isApplyMoveCalled() {
		return applyMoveCalled;
	}
	public boolean isApplyBuildCalled() {
		return applyBuildCalled;
	}
	public boolean isApplyWinCalled() {
		return applyWinCalled;
	}
	public boolean isApplyDefeatCalled() {
		return applyDefeatCalled;
	}
	public boolean isSetOrderCalledCorrectly() {
		return setOrderCalledCorrectly;
	}
	public boolean isSetPlayerColorCalled() {
		return setPlayerColorCalled;
	}
	public boolean isSetGameGodsCalled() {
		return setGameGodsCalled;
	}
	public boolean isSetPlayerGodCalled() {
		return setPlayerGodCalled;
	}
	public boolean isSetStarterCalled() {
		return setStarterCalled;
	}
	public boolean isSetWorkerPositionsCalled() {
		return setWorkerPositionsCalled;
	}
}
