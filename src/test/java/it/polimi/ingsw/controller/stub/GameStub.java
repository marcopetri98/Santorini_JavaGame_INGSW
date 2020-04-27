package it.polimi.ingsw.controller.stub;

import it.polimi.ingsw.core.*;
import it.polimi.ingsw.core.gods.GodCard;
import it.polimi.ingsw.core.gods.GodCardFactory;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameStub extends Game {
	private boolean applyMoveCalled;
	private boolean applyBuildCalled;
	private boolean applyWinCalled;
	private boolean applyDefeatCalled;
	private boolean setOrderCalled;
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

	public GameStub(String[] names) {
		super(names);
		applyMoveCalled = false;
		applyBuildCalled = false;
		applyWinCalled = false;
		applyDefeatCalled = false;
		setOrderCalled = false;
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
		for (int i = 0; i < names.length; i++) {
			players.add(new Player(names[i]));
			players.get(i).setPlayerColor(new Color(255,255,255-i));
			godCards.add(GodCardFactory.createGodCard(Constants.GODS_GOD_NAMES.get(i)));
			players.get(i).setGodCard(godCards.get(i));
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
	public void setOrder(List<String> playerOrder) {
		setOrderCalled = true;
	}
	public void setPlayerColor(String player, Color color) {
		setPlayerColorCalled = true;
	}
	public void setPlayerGod(String playerName, String god) {
		setPlayerGodCalled = true;
	}
	public void setGameGods(List<String> godNames) {
		setGameGodsCalled = true;
	}
	public void setStarter(String starterName) {
		setStarterCalled = true;
	}
	public void setWorkerPositions(String playerName, Pair<Integer,Integer> worker1, Pair<Integer,Integer> worker2) {
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
		setOrderCalled = false;
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
	public boolean isSetOrderCalled() {
		return setOrderCalled;
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
