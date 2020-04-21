package it.polimi.ingsw.core;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.gods.*;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.game.NetBuild;
import it.polimi.ingsw.network.game.NetMove;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.observers.ObservableGame;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;

// necessary imports of Java SE
import java.awt.*;
import java.util.List;
import java.util.ArrayList;


public class Game extends ObservableGame {
	private Player activePlayer; //the player who has to move and build in the turn considered.
	private List<Player> players;
	private List<GodCard> godCards;
	private Turn turn;
	private final Map map;

	// constructors
	public Game(String[] names) {
		players = new ArrayList<>();
		godCards = new ArrayList<>();
		map = new Map();
		turn = new Turn();
		for (String name : names) {
			players.add(new Player(name));
		}
	}

	// setters and methods which changes the state of the game
	public synchronized void applyMove(Move move) {
	}
	public synchronized void applyBuild(Build build) {
	}
	public synchronized void moveWorker(Worker w, Cell c){
		w.getPos().setWorker(null);
		w.setPos(c);
		c.setWorker(w);
	}
	public synchronized void changeTurn() {  //active Player become the next one
		if(players.size() == 2) {
			if (players.indexOf(activePlayer) == 0) {
				activePlayer = players.get(1);
			} else {
				activePlayer = players.get(0);
			}
		} else {  //players.size() == 3
			if (players.indexOf(activePlayer) < 2) {
				activePlayer = players.get(players.indexOf(activePlayer) + 1);
			} else {
				activePlayer = players.get(0);
			}
		}
		turn.advance();
	}

	// getters and other functions which doesn't change the structure of the object
	public synchronized Map getMap() {
		// creates a copy of the player's list
		List<Player> tempPlayers = getPlayers();
		return map;
	}
	public synchronized int getPlayerNum() {
		return players.size();
	}
	public synchronized List<Player> getPlayers() {
		List<Player> temp = new ArrayList<>();
		for (Player player : players) {
			Player current = player.clone();
			temp.add(player.clone());
		}
		return temp;
	}
	public synchronized Player getPlayerTurn() {
		return activePlayer.clone();
	}

	// support method
	private Worker findWorker(int id) throws IllegalArgumentException {
		for (Player player : players) {
			if (player.getPlayerID()-1 == id) {
				return player.getWorker1();
			} else if (player.getPlayerID()-2 == id) {
				return player.getWorker2();
			}
		}
		throw new IllegalArgumentException();
	}

	// METHODS USED AT THE BEGINNING OF THE GAME
	// SETTERS USED ON THE BEGINNING
	/**
	 * This method receives a list of player's names and set the order sorting the players arrayList
	 * @param playerOrder is the ordered list of players turn sequence
	 * @throws IllegalArgumentException it is thrown if playerOrder is null or if it doesn't represent a permutation of players arrayList
	 * @throws WrongPhaseException is thrown if this method is called on a different phase from the start
	 */
	public synchronized void setOrder(List<String> playerOrder) throws IllegalArgumentException, WrongPhaseException {
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

		List<Player> temp = new ArrayList<>();
		for (int i = 0; i < playerOrder.size(); i++) {
			boolean found = false;
			for (int j = 0; j < players.size() && !found; j++) {
				if (players.get(j).getPlayerName().equals(playerOrder.get(i))) {
					found = true;
					temp.add(players.get(j));
				}
			}
		}
		players = temp;
		activePlayer = players.get(0);
		// notifies the remote view of a change
		notifyOrder(playerOrder.toArray());
		// once all clients are notified the phase advance to color selection
		turn.advance();
	}
	/**
	 * Sets the player's color indicated
	 * @param player is the player which the color has to be set
	 * @param color the color chosen by the player
	 * @throws IllegalArgumentException if color or player is null or if it is trying to set the color of a player which isn't the active player
	 * @throws WrongPhaseException if the phase isn't the color selection phase
	 */
	public synchronized void setPlayerColor(String player, Color color) throws IllegalArgumentException, WrongPhaseException {
		if (player == null || color == null) {
			throw new IllegalArgumentException();
		} else if (turn.getPhase() != Phase.COLORS) {
			throw new WrongPhaseException();
		}
		int i;
		boolean found = false;
		for (i = 0; i < players.size() && !found; i++) {
			if (players.get(i).getPlayerName().equals(player)) {
				found = true;
			}
		}
		if (i == players.size() || players.get(i) != activePlayer) {
			throw new IllegalArgumentException();
		} else {
			players.get(i).setPlayerColor(color);
		}
	}
	/**
	 * Sets the player's godCard
	 * @param player
	 * @param god
	 * @throws IllegalArgumentException
	 * @throws WrongPhaseException
	 */
	public synchronized void setPlayerGod(String player, String god) throws IllegalArgumentException, WrongPhaseException {
		if (player == null || god == null || !Constants.GODS_GOD_NAMES.contains(god)) {
			throw new IllegalArgumentException();
		} else if (turn.getPhase() != Phase.GODS) {
			throw new WrongPhaseException();
		}
		// it search for the player inside the list of players
		int i;
		boolean found = false;
		for (i = 0; i < players.size() && !found; i++) {
			if (players.get(i).getPlayerName().equals(player)) {
				found = true;
			}
		}

		// if present it sets the godCard, if not it throws the exception
		if (i == players.size()) {
			throw new IllegalArgumentException();
		} else {
			GodCard godCreated = GodCardFactory.createGodCard(god,players.get(i));
			players.get(i).setGodCard(godCreated);
			godCards.add(godCreated);
		}
	}

	// GETTERS USED ON THE BEGINNING
	public synchronized Color getPlayerColor(String player) throws IllegalArgumentException, IllegalStateException {
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
		return turn.clone();
	}
}
