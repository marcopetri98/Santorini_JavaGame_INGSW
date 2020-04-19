package it.polimi.ingsw.core;

import it.polimi.ingsw.util.observers.ObservableObject;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;


public class Game extends ObservableObject {
	private Player activePlayer; //the player who has to move and build in the turn considered.
	private List<Player> players;
	// TODO: implement factory method for godCards
	private List<GodCard> godCards;
	private int phase; // 0 = setup order, 1 = game color selection, 2 = divinity selection, 3 = worker position on board (game setup), 4 = your turn, 5 = others turn
	private int subPhase; // 1 if the player has to move, 2 if the player has to build, -1 if the game isn't started
	private Map map;

	// constructors
	public Game(String[] names) {
		activePlayer = null;
		players = new ArrayList<>();
		godCards = new ArrayList<>();
		map = new Map();
		phase = 0;
		subPhase = -1;
		for (String name : names) {
			players.add(new Player(name));
		}
		createGodCards();
	}

	// setters and methods which changes the state of the object
	public void moveWorker(Worker w, Cell c){  //server controlled if the move was legit
		w.getPos().setWorker(null);
		w.setPos(c);
		c.setWorker(w);
	}
	public void changeTurn() {  //active Player become the next one
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
	}

	// getters and other functions which doesn't change the structure of the object
	public Map getMap() throws CloneNotSupportedException {
		return (Map)map.clone();
	}
	public int getPlayerNum() {
		return players.size();
	}
	public List<Player> getPlayers() {
		return new ArrayList<Player>(players);
	}
	public Player getPlayerTurn() {
		return activePlayer.copy();
	}

	// METHODS USED AT THE BEGINNING OF THE GAME
	private void createGodCards() {
		int i = 0;
		godCards.add(new Apollo());
		godCards.add(new Artemis());
		godCards.add(new Minotaur());
		godCards.add(new Atlas());
		godCards.add(new Demeter());
		godCards.add(new Hephaestus());
		godCards.add(new Athena());
		godCards.add(new Pan());
		godCards.add(new Prometheus());
	}

	// SETTERS USED ON THE BEGINNING
	/**
	 * This method receives a list of player's names and set the order sorting the players arrayList
	 * @param playerOrder is the ordered list of players turn sequence
	 * @throws IllegalArgumentException it is thrown if playerOrder is null or if it doesn't represent a permutation of players arrayList
	 * @throws WrongPhaseException is thrown if this method is called on a different phase from the start
	 */
	public void setOrder(List<String> playerOrder) throws IllegalArgumentException, WrongPhaseException {
		if (playerOrder == null || playerOrder.size() != players.size()) {
			throw new IllegalArgumentException();
		} else if (phase != 0) {
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
		phase++;
	}
	/**
	 * Sets the player's color indicated
	 * @param player is the player which the color has to be set
	 * @param color the color chosen by the player
	 * @throws IllegalArgumentException if color or player is null or if it is trying to set the color of a player which isn't the active player
	 * @throws WrongPhaseException if the phase isn't the color selection phase
	 */
	public void setPlayerColor(String player, Color color) throws IllegalArgumentException, WrongPhaseException {
		if (player == null || color == null) {
			throw new IllegalArgumentException();
		} else if (phase != 1) {
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
	public void setPlayerGod(String player, GodCard god) throws IllegalArgumentException, WrongPhaseException {
		if (player == null || god == null || !godCards.contains(god)) {
			throw new IllegalArgumentException();
		} else if (phase != 2) {
			throw new WrongPhaseException();
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
			players.get(i).setGodCard(god);
		}
	}
	/**
	 * Sets the current phase to the new phase
	 * @param phase the new phase
	 * @throws IllegalArgumentException if the parameter is less than 0 or greater than 5
	 */
	public void setPhase(int phase) throws IllegalArgumentException {
		if (subPhase < 0 || subPhase > 5) {
			throw new IllegalArgumentException();
		}
		this.phase = phase;
	}
	/**
	 * Sets the current subPhase to a new value specified in the parameters
	 * @param subPhase the new sub phase
	 * @throws IllegalArgumentException if the parameter isn't 1 or 2
	 */
	public void setSubPhase(int subPhase) throws IllegalArgumentException {
		if (subPhase != 1 && subPhase != 2) {
			throw new IllegalArgumentException();
		}
		this.subPhase = subPhase;
	}

	// GETTERS USED ON THE BEGINNING
	public Color getPlayerColor(String player) throws IllegalArgumentException, IllegalStateException {
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
	public GodCard getPlayerGodCard(String player) throws IllegalArgumentException, IllegalStateException  {
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
	public int getPhase() {
		return phase;
	}
	public int getSubPhase() {
		return subPhase;
	}
}
