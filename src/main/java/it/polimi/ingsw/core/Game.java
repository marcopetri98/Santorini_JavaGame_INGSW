package it.polimi.ingsw.core;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.gods.*;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.util.observers.ObservableGame;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;

// necessary imports of Java SE
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class Game extends ObservableGame {
	private Player activePlayer; //the player who has to move and build in the turn considered.
	private List<Player> players;
	private List<GodCard> godCards;
	private Turn turn;
	private final Map map;
	private Player winner;

	// constructors
	public Game(String[] names) {
		players = new ArrayList<>();
		godCards = new ArrayList<>();
		map = new Map();
		turn = new Turn();
		for (String name : names) {
			players.add(new Player(name));
		}
		winner = null;
	}

	// setters and methods which changes the state of the game
	public synchronized void applyMove(Move move) {
		//TODO: check if the model is completely updated
		move.prev.setWorker(null);
		move.next.setWorker(move.worker);
		move.worker.setPos(move.next);
		if(move.getOther() != null){
			move.getOther().prev.setWorker(null);
			move.getOther().next.setWorker(move.worker);
			move.getOther().worker.setPos(move.next);
		}
	}

	/**
	 * This function applies the construction in the map.
	 * @param build is the construction checked by the controller.
	 */
	public synchronized void applyBuild(Build build) {
		if(build.dome) {
			map.getCell(map.getX(build.cell), map.getY(build.cell)).building.setDome();
		} else {
			map.getCell(map.getX(build.cell), map.getY(build.cell)).building.incrementLevel(); //or == "build.level"; should work in this way though.
		}
	}

	/**
	 * If this function is called, there is a winner!
	 * @param player is the winner
	 */
	public synchronized void applyWin(Player player) {
		winner = player.clone();
		notifyWinner(player.playerName);
	}

	/**
	 * The function manages the defeat of a player
	 * @param player is the looser
	 */
	public synchronized void applyDefeat(Player player) {
		//TODO: check if applywin is correctly called
		this.players.remove(player);
		if(this.players.size() == 1){
			try {
				applyWin(players.get(0));
			}
			catch(IndexOutOfBoundsException ex){
				ex.printStackTrace();
			}
		}
	}

	public synchronized void applyDisconnection(String playerName) {
	}
	public synchronized void moveWorker(Worker w, Cell c){
		w.getPos().setWorker(null);
		w.setPos(c);
		c.setWorker(w);
	}
	public synchronized void changeTurn() {  //active Player become the next one
		if (turn.getPhase() == Phase.COLORS && players.indexOf(activePlayer) != players.size()-1) {
			changeActivePlayer();
			turn.advance();
		} else if ((turn.getPhase() == Phase.GODS && turn.getGodsPhase() == GodsPhase.CHALLENGER_CHOICE) || (turn.getPhase() == Phase.GODS && turn.getGodsPhase() == GodsPhase.GODS_CHOICE && players.indexOf(activePlayer) == 0) || (turn.getPhase() == Phase.GODS && turn.getGodsPhase() == GodsPhase.STARTER_CHOICE)) {
			if (turn.getPhase() == Phase.GODS && turn.getGodsPhase() != GodsPhase.STARTER_CHOICE) {
				changeActivePlayer();
			} else if (turn.getPhase() == Phase.GODS && turn.getGodsPhase() == GodsPhase.STARTER_CHOICE) {
				notifyActivePlayer(activePlayer.getPlayerName());
			}
			turn.advance();
		} else if (turn.getPhase() == Phase.SETUP) {
			if (players.indexOf(activePlayer) == players.size()-1) {
				changeActivePlayer();
				turn.advance();
			} else {
				changeActivePlayer();
			}
		} else {
			if (turn.getGamePhase() == GamePhase.END) {
				activePlayer.resetLocking();
				changeActivePlayer();
			}
			turn.advance();
			notifyPhaseChange(turn);
		}
	}
	/**
	 * This function is called whenever there is a change of game phase
	 */
	public synchronized void changeActivePlayer() {
		if (players.size() == 2) {
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
		notifyActivePlayer(activePlayer.getPlayerName());
	}
	/**
	 * This function is called during the starter selection in god selection phase
	 * @param playerName
	 * @throws IllegalArgumentException
	 * @throws WrongPhaseException
	 */
	public synchronized void changeActivePlayer(String playerName) throws IllegalArgumentException, WrongPhaseException {
		boolean found = false;
		if (playerName == null) {
			throw new IllegalArgumentException();
		} else if (turn.getPhase() != Phase.GODS && turn.getGodsPhase() != GodsPhase.STARTER_CHOICE) {
			throw new WrongPhaseException();
		}

		// it searches the player and change the active player for the turn
		for (int i = 0; i < players.size() && !found; i++) {
			if (players.get(i).getPlayerName().equals(playerName)) {
				activePlayer = players.get(i);
				found = true;
			}
		}
		// the player doesn't exist and it throws an exception
		if (!found) {
			throw new IllegalArgumentException();
		} else {
			notifyGods(playerName);
		}
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
	public synchronized Player getPlayerByName(String name) throws IllegalArgumentException {
		for (Player p : players) {
			if (p.getPlayerName().equals(name)) {
				return p;
			}
		}
		throw new IllegalArgumentException();
	}
	public synchronized List<Player> getPlayers() {
		return new ArrayList<>(players);
	}
	public synchronized Player getPlayerTurn() {
		return activePlayer;
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
		notifyOrder((String[])playerOrder.toArray());
		// once all clients are notified the phase advance to color selection
		turn.advance();
	}
	/**
	 * Sets the player's color indicated and updates the remote views about the colors actually chosen by the players sending an HashMap<String,Color> with player names and color chosen
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
	 * @param playerName
	 * @param god
	 * @throws IllegalArgumentException
	 * @throws WrongPhaseException
	 */
	public synchronized void setPlayerGod(String playerName, String god) throws IllegalArgumentException, WrongPhaseException {
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

			if (!godFound) {
				throw new IllegalArgumentException();
			}
		}

		// it search for the player inside the list of players
		int i;
		boolean found = false;
		for (i = 0; i < players.size() && !found; i++) {
			if (players.get(i).getPlayerName().equals(playerName)) {
				found = true;
			}
		}
		// if present it sets the godCard, if not it throws the exception
		if (i == players.size()) {
			throw new IllegalArgumentException();
		}

		players.get(i).setGodCard(playerGod);
		HashMap<String,GodCard> godsInfo = new HashMap<>();
		for (Player player : players) {
			godsInfo.put(player.getPlayerName(),player.getCard());
		}
		notifyGods(godsInfo);
	}
	public synchronized void setGameGods(List<String> godNames) throws IllegalArgumentException, WrongPhaseException {
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

		for (String godName : godNames) {
			GodCard godCreated = GodCardFactory.createGodCard(godName);
			godCards.add(godCreated);
		}
		notifyGods(godCards);
	}
	public synchronized void setStarter(String starterName) throws IllegalStateException, WrongPhaseException {
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

		if (players.indexOf(starter) != 0) {
			List<Player> temp = new ArrayList<>();
			for (int i = players.indexOf(starter); i < players.size(); i++) {
				temp.add(players.get(i));
			}
			for (int i = 0; i < players.indexOf(starter); i++) {
				temp.add(players.get(i));
			}
			players = temp;
		}
		activePlayer = starter;
		notifyActivePlayer(starterName);
	}
	public synchronized void setWorkerPositions(String playerName, Pair<Integer,Integer> worker1, Pair<Integer,Integer> worker2) throws NullPointerException {
		if (worker1 == null || worker2 == null) {
			throw new NullPointerException();
		}

		boolean finished = true;
		Player player = getPlayerByName(playerName);
		player.getWorker1().setPos(map.getCell(worker1.getFirst(),worker1.getSecond()));
		player.getWorker2().setPos(map.getCell(worker1.getFirst(),worker1.getSecond()));
		for (int i = 0; i < players.size() && finished; i++) {
			if (players.get(i).getWorker1().getPos() == null) {
				finished = false;
			}
		}
		notifyPositions(map,finished);
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
