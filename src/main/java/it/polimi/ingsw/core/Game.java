package it.polimi.ingsw.core;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.gods.*;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.objects.NetGameSetup;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.observers.ObservableGame;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;

// necessary imports of Java SE
import it.polimi.ingsw.util.Color;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class Game extends ObservableGame {
	private Player activePlayer; //the player who has to move and build in the turn considered.
	private List<Player> players;
	private final List<GodCard> godCards;
	private final Turn turn;
	private final Map map;
	private final List<Player> defeatedPlayers;
	private Player winner;
	private boolean isFinished;

	public Game(String[] names) {
		players = new ArrayList<>();
		godCards = new ArrayList<>();
		defeatedPlayers = new ArrayList<>();
		map = new Map();
		turn = new Turn();
		for (String name : names) {
			players.add(new Player(name));
		}
		winner = null;
		isFinished = false;
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *		MODIFIERS FOR THE GAME USED AND			*
	 * 		INTERFACES EXPOSED TO OTHER PACKS		*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	public synchronized void applyMove(Move move) throws NullPointerException {
		if (move == null) {
			throw new NullPointerException();
		}

		moveWorkers(move);
		notifyMove(getMap());
	}
	/**
	 * This function applies the construction in the map.
	 * @param build is the construction checked by the controller.
	 */
	public synchronized void applyBuild(Build build) throws NullPointerException {
		if (build == null) {
			throw new NullPointerException();
		}

		if(build.dome) {
			map.getCell(map.getX(build.cell), map.getY(build.cell)).building.setDome();
		} else {
			map.getCell(map.getX(build.cell), map.getY(build.cell)).building.incrementLevel(); //or == "build.level"; should work in this way though.
		}
		build.worker.setLastBuildPos(build.cell);
		notifyBuild(getMap());
	}
	/**
	 * If this function is called, there is a winner!
	 * @param player is the winner
	 */
	public synchronized void applyWin(Player player) throws IllegalArgumentException {
		if (!players.contains(player)) {
			throw new IllegalArgumentException();
		}

		// sets the winner and close the game
		winner = player;
		isFinished = true;
		notifyWinner(player.playerName);
	}
	/**
	 * The function manages the defeat of a player
	 * @param player is the looser
	 */
	public synchronized void applyDefeat(Player player) throws IllegalArgumentException {
		if (!players.contains(player)) {
			throw new IllegalArgumentException();
		}

		// removes the player and his workers
		removePlayer(player);
		if(this.players.size() == 1){
			applyWin(players.get(0));
		}

		notifyDefeat(player.getPlayerName());
	}
	public synchronized void applyDisconnection(String playerName) throws IllegalArgumentException {
		if (playerName == null) {
			throw new IllegalArgumentException();
		}

		// if someone disconnects during the setup the game finished
		if (turn.getPhase() != Phase.PLAYERTURN) {
			isFinished = true;
			notifyEndForDisconnection();
			removeAllObservers();
		} else {
			// applies the disconnection, if the player doesn't exist this line throws IllegalArgumentException
			Player player = getPlayerByName(playerName);
			int playerIndex = players.indexOf(player);

			if (players.size() == 2) {
				notifyQuit(playerName);
				applyWin(players.get(0));
			} else {
				// distinguish if the disconnecting player is the active player or not because the game isn't finished
				if (player == activePlayer) {
					activePlayer = players.get(playerIndex == players.size() - 1 ? 0 : playerIndex + 1);
					removePlayer(player);
					notifyQuit(playerName);
					notifyActivePlayer(activePlayer.getPlayerName());
				} else {
					removePlayer(player);
					notifyQuit(playerName);
				}
			}
		}
	}
	public synchronized void applyWorkerLock(Player player, int worker) throws IllegalArgumentException {
		if (player == null || !players.contains(player) || (worker != 1 && worker != 2)) {
			throw new IllegalArgumentException();
		}
		player.chooseWorker(worker);
	}
	/**
	 * This function handles the advancing of the turn, it means that it checks if it is needed only to advance the subphase (for god setup: challenger choice, gods choice and starter choice, for game turn: before move (which is also the start of the turn), move, before build, build, end turn) of the current phase or if is needed also to change the active player.
	 */
	public synchronized void changeTurn() {  //active Player become the next one
		if (turn.getPhase() == Phase.LOBBY) {
			turn.advance();
			notifyPhaseChange(turn.clone());
		} else if (turn.getPhase() == Phase.COLORS) {
			if (players.indexOf(activePlayer) != players.size()-1) {
				turn.advance();
				notifyPhaseChange(turn.clone());
			}
			changeActivePlayer();
		} else if (turn.getPhase() == Phase.GODS) {
			if (turn.getGodsPhase() == GodsPhase.CHALLENGER_CHOICE || (turn.getGodsPhase() == GodsPhase.GODS_CHOICE && players.indexOf(activePlayer) == 0) || (turn.getGodsPhase() == GodsPhase.STARTER_CHOICE)) {
				turn.advance();
				notifyPhaseChange(turn.clone());
			}
			changeActivePlayer();
		} else if (turn.getPhase() == Phase.SETUP) {
			if (players.indexOf(activePlayer) == players.size()-1) {
				turn.advance();
				notifyPhaseChange(turn.clone());
			}
			changeActivePlayer();
		} else {
			// the turn is finished and this if resets the player turn values
			if (turn.getGamePhase() == GamePhase.BUILD) {
				activePlayer.resetLocking();
				activePlayer.getWorker1().resetBuilding();
				activePlayer.getWorker2().resetBuilding();
				activePlayer.resetLocking();
				changeActivePlayer();
			}
			turn.advance();
			notifyPhaseChange(turn.clone());
		}
	}
	/**
	 * This function is called whenever there is a change of game phase
	 */
	private synchronized void changeActivePlayer() {
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
	private synchronized void removePlayer(Player player) {
		players.remove(player);
		defeatedPlayers.add(player);
		player.getWorker1().getPos().setWorker(null);
		player.getWorker1().setPos(null);
		player.getWorker2().getPos().setWorker(null);
		player.getWorker2().setPos(null);
	}
	private synchronized void moveWorkers(Move move) {
		move.prev.setWorker(null);
		move.next.setWorker(move.worker);
		move.worker.setPos(move.next);
		if(move.getOther() != null){
			moveWorkers(move.getOther());
		}
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 *		GETTERS FOR THE GAME USED				*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	public synchronized Map getMap() {
		return map;
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
	public synchronized Player getWinner() {
		return winner;
	}
	public synchronized boolean isFinished() {
		return isFinished;
	}

	/* **********************************************
	 *												*
	 *												*
	 *												*
	 * METHODS USED AT THE BEGINNING OF THE GAME	*
	 * 												*
	 * 												*
	 * 												*
	 ************************************************/
	// TODO: in all methods call the notify active player when the active player change and REFACTOR the communication of the active players on the RemoteView
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
		notifyOrder((String[])playerOrder.toArray(new String[0]));
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
		}

		HashMap<String,Color> colorInfo = new HashMap<>();
		for (int j = 0; j < i; j++) {
			colorInfo.put(players.get(i).getPlayerName(),players.get(i).getWorker1().color);
		}
		notifyColors(colorInfo);
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

		if (playerName == null || god == null || !Constants.GODS_GOD_NAMES.contains(god.toUpperCase())) {
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
		if (!found) {
			throw new IllegalArgumentException();
		}

		GodCard newGod = GodCardFactory.createGodCard(playerGod.getName());
		godCards.set(godCards.indexOf(playerGod),newGod);
		players.get(i).setGodCard(newGod);
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
				if (!Constants.GODS_GOD_NAMES.contains(godName.toUpperCase())) {
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
	/**
	 * It sets the starting player positioning it on the first position of the arrayList
	 * @param starterName
	 * @throws IllegalStateException
	 * @throws WrongPhaseException
	 */
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
		activePlayer = players.get(players.size()-1);
		notifyGods(starterName);
	}
	public synchronized void setWorkerPositions(NetGameSetup req) throws IllegalArgumentException, WrongPhaseException {
		if (req == null || !req.isWellFormed()) {
			throw new IllegalArgumentException();
		} else if (turn.getPhase() != Phase.SETUP) {
			throw new WrongPhaseException();
		} else if (map.getCell(req.worker1.getFirst(),req.worker1.getSecond()).getWorker() != null || map.getCell(req.worker2.getFirst(),req.worker2.getSecond()).getWorker() != null) {
			throw new IllegalArgumentException();
		}

		// this row throws Illegal argument exception if the player doesn't exist
		Player player = getPlayerByName(req.player);
		boolean finished = true;
		player.getWorker1().setPos(map.getCell(req.worker1.getFirst(),req.worker1.getSecond()));
		player.getWorker2().setPos(map.getCell(req.worker2.getFirst(),req.worker2.getSecond()));
		for (int i = 0; i < players.size() && finished; i++) {
			if (players.get(i).getWorker1().getPos() == null) {
				finished = false;
			}
		}
		notifyPositions(map,finished);
	}

	// GETTERS USED ON THE BEGINNING
	public synchronized Turn getPhase() {
		return turn.clone();
	}
}
