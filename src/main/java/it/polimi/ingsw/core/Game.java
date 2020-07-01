package it.polimi.ingsw.core;

// necessary imports from other packages of the project
import it.polimi.ingsw.controller.DefeatManager;
import it.polimi.ingsw.core.gods.*;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.objects.NetGameSetup;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.exceptions.NoBuildException;
import it.polimi.ingsw.util.exceptions.NoMoveException;
import it.polimi.ingsw.util.observers.ObservableGame;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;

// necessary imports of Java SE
import it.polimi.ingsw.util.Color;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * This class is the main class for the model in the Distributed MVC pattern, this class is one of the few accessible classes of the core (the model) package, it expose some methods accessible from the outside classes which can be used to change the values of the game.
 */
public class Game extends ObservableGame {
	private Player activePlayer; //the player who has to move and build in the turn considered.
	private List<Player> players;
	private final List<GodCard> godCards;
	private final Turn turn;
	private final Map map;
	private final List<Player> defeatedPlayers;
	private Player winner;
	private boolean isFinished;
	private List<Move> playerPossibleMoves;
	private List<Build> playerPossibleBuilds;

	/**
	 * Creates the game given an array of players' names.
	 * @param names players' names
	 */
	public Game(String[] names) {
		players = new ArrayList<>();
		godCards = new ArrayList<>();
		defeatedPlayers = new ArrayList<>();
		map = new Map();
		turn = new Turn();
		for (int i = 0; i < names.length; i++) {
			players.add(new Player(names[i],i*50));
		}
		activePlayer = players.get(0);
		winner = null;
		isFinished = false;
		playerPossibleMoves = new ArrayList<>();
		playerPossibleBuilds = new ArrayList<>();
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

	/**
	 * This method applies a move moving the workers on the map using the all information inside the {@link it.polimi.ingsw.core.Move} object.
	 * @param move the object representing the move
	 * @throws NullPointerException if {@code move} is null
	 */
	public synchronized void applyMove(Move move) throws NullPointerException {
		if (move == null) {
			throw new NullPointerException();
		}

		moveWorkers(move, false);
		notifyMove(getMap());
	}
	/**
	 * This method applies the construction on the map performed by the player the information all present in the {@link it.polimi.ingsw.core.Build} object.
	 * @param build is the build to be performed
	 * @throws NullPointerException if {@code build} is null
	 */
	public synchronized void applyBuild(Build build) throws NullPointerException {
		if (build == null) {
			throw new NullPointerException();
		}

		buildBuildings(build);
		build.worker.setLastBuildPos(build.cell);
		notifyBuild(getMap());
	}
	/**
	 * This method creates a winner and notifies it to all observers.
	 * @param player is the winner
	 * @throws IllegalArgumentException if {@code player} is not a player of the game
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
	 * This method creates a loser and notifies it to all observers.
	 * @param player is the loser
	 * @throws IllegalArgumentException if {@code player} is not a player of the game
	 */
	public synchronized void applyDefeat(Player player) throws IllegalArgumentException {
		if (!players.contains(player)) {
			throw new IllegalArgumentException();
		}

		// removes the player and his workers
		notifyDefeat(player.getPlayerName());
		if(this.players.size() == 2) {
			removePlayer(player);
			applyWin(players.get(0));
		} else {
			if (activePlayer == player) {
				int playerIndex = players.indexOf(player);
				activePlayer = players.get(playerIndex == players.size() - 1 ? 0 : playerIndex + 1);
				removePlayer(player);
				notifyMove(getMap());

				notifyActivePlayer(activePlayer.getPlayerName());
				while (turn.getGamePhase() != GamePhase.BEFOREMOVE) {
					turn.advance();
					notifyPhaseChange(turn.clone());
				}
				computeActions();

				if (getPlayerPossibleMoves().size() == 0 && getPlayerPossibleBuilds().size() == 0) {
					applyDefeat(getPlayerTurn());
				} else if (getPlayerPossibleBuilds().size() == 0) {
					changeTurn();
					computeActions();
				}
			} else {
				removePlayer(player);
				notifyMove(getMap());
			}
		}
	}
	/**
	 * This method close the game because a player has disconnected.
	 * @param playerName the disconnected player
	 * @throws IllegalArgumentException if {@code playerName} is null
	 */
	public synchronized void applyDisconnection(String playerName) throws IllegalArgumentException {
		if (playerName == null) {
			throw new IllegalArgumentException();
		}
		Player player = getPlayerByName(playerName);

		// if someone disconnects during the setup the game finished
		if (turn.getPhase() != Phase.PLAYERTURN) {
			isFinished = true;
			notifyEndForDisconnection();
			removeAllObservers();
		} else {
			isFinished = true;
			removePlayer(player);
			notifyQuit(playerName);
			removeAllObservers();
		}
	}
	/**
	 * This method lock the worker for the active player.
	 * @param player the player to lock the worker
	 * @param worker worker to lock
	 * @throws IllegalArgumentException if {@code player} is null or isn't a player of the game or if {@code worker} isn't 2 or 3
	 */
	public synchronized void applyWorkerLock(Player player, int worker) throws IllegalArgumentException {
		if (player == null || !players.contains(player) || (worker != 1 && worker != 2)) {
			throw new IllegalArgumentException();
		}
		player.chooseWorker(worker);
	}
	/**
	 * This method handles the advancing of the turn, it means that it checks if it is needed only to advance the subphase (for god setup: challenger choice, gods choice and starter choice, for game turn: before move (which is also the start of the turn), move, before build, build, end turn) of the current phase or if is needed also to change the active player.
	 */
	public synchronized void changeTurn() {  //active Player become the next one
		if (turn.getPhase() == Phase.LOBBY) {
			turn.advance();
			notifyPhaseChange(turn.clone());
			notifyActivePlayer(players.get(0).getPlayerName());
		} else if (turn.getPhase() == Phase.COLORS) {
			if (players.indexOf(activePlayer) == players.size()-1) {
				turn.advance();
				notifyPhaseChange(turn.clone());
			}
			changeActivePlayer();
		} else if (turn.getPhase() == Phase.GODS) {
			GodsPhase beforePhase = turn.getGodsPhase();
			if (turn.getGodsPhase() == GodsPhase.CHALLENGER_CHOICE || (turn.getGodsPhase() == GodsPhase.GODS_CHOICE && players.indexOf(activePlayer) == 0) || (turn.getGodsPhase() == GodsPhase.STARTER_CHOICE)) {
				turn.advance();
				notifyPhaseChange(turn.clone());
			}
			if (beforePhase == GodsPhase.GODS_CHOICE && turn.getGodsPhase() == GodsPhase.STARTER_CHOICE) {
				notifyActivePlayer(activePlayer.getPlayerName());
			} else {
				changeActivePlayer();
			}
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
	 * This method is called whenever there is a change of game phase where the active player should change, it changes the active player and notifies all observers.
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
	/**
	 * This methods removes a player from the player list.
	 * @param player the {@link it.polimi.ingsw.core.Player} to be removed
	 */
	private synchronized void removePlayer(Player player) {
		players.remove(player);
		defeatedPlayers.add(player);
		player.getWorker1().getPos().setWorker(null);
		player.getWorker1().setPos(null);
		player.getWorker2().getPos().setWorker(null);
		player.getWorker2().setPos(null);
	}
	/**
	 * This method moves worker on the map from a given {@link it.polimi.ingsw.core.Move}.
	 * @param move a {@link it.polimi.ingsw.core.Move} saying where to move workers
	 * @param conditioned says if the move is conditioned
	 */
	private synchronized void moveWorkers(Move move, boolean conditioned) {
		if (!conditioned) {
			move.prev.setWorker(null);
		}
		move.next.setWorker(move.worker);
		move.worker.setPos(move.next);
		if(move.getOther() != null){
			moveWorkers(move.getOther(), true);
		}
	}
	/**
	 * This method builds on the map from a given {@link it.polimi.ingsw.core.Build}.
	 * @param build a {@link it.polimi.ingsw.core.Build} saying where to build
	 */
	private synchronized void buildBuildings(Build build) {
		if (build.dome) {
			build.getCell().getBuilding().setDome();
		} else {
			build.getCell().getBuilding().incrementLevel();
		}
		if (build.getOther() != null && build.getTypeBuild() == TypeBuild.CONDITIONED_BUILD) {
			buildBuildings(build.getOther());
		}
	}
	/**
	 * This method computes only the moves and the builds which the player can perform in its turn.
	 */
	public synchronized void computeActions() {
		playerPossibleMoves = new ArrayList<>();
		playerPossibleBuilds = new ArrayList<>();

		if (turn.getGamePhase() == GamePhase.BEFOREMOVE) {
			computeMoves(getPlayerTurn().getWorker1(),false);
			computeMoves(getPlayerTurn().getWorker2(),false);
			computeBuilds(getPlayerTurn().getWorker1());
			computeBuilds(getPlayerTurn().getWorker2());
		} else if (turn.getGamePhase() == GamePhase.MOVE) {
			computeMoves(getPlayerTurn().getWorker1(),true);
			computeMoves(getPlayerTurn().getWorker2(),true);
		} else {
			computeBuilds(getPlayerTurn().getActiveWorker());
		}
		notifyPossibleActions(playerPossibleMoves,playerPossibleBuilds);
	}
	/**
	 * Computes the workers moves.
	 * @param w the {@link it.polimi.ingsw.core.Worker} which moves are computed
	 * @param move a boolean which says if the phase is move of beforemove
	 */
	private synchronized void computeMoves(Worker w, boolean move) {
		try {
			if (getPlayerTurn().getCard().getTypeGod() != TypeGod.OTHER_TURN_GOD) {
				playerPossibleMoves.addAll(getPlayerTurn().getCard().checkMove(map,w,turn));
			} else {
				if (move) {
					if (GodCard.standardMoves(map,w,turn) != null) {
						playerPossibleMoves.addAll(GodCard.standardMoves(map,w,turn));
					}
				} else {
					Turn moveTurn = turn.clone();
					moveTurn.advance();
					if (GodCard.standardMoves(map, w, moveTurn) != null) {
						playerPossibleMoves.addAll(GodCard.standardMoves(map, w, moveTurn));
					}
				}
			}
		} catch (NoMoveException e) {
			if (move) {
				if (GodCard.standardMoves(map,w,turn) != null) {
					playerPossibleMoves.addAll(GodCard.standardMoves(map,w,turn));
				}
			} else {
				Turn moveTurn = turn.clone();
				moveTurn.advance();
				if (GodCard.standardMoves(map, w, moveTurn) != null) {
					playerPossibleMoves.addAll(GodCard.standardMoves(map, w, moveTurn));
				}
			}
		}

		try {
			for (Player player : players) {
				if (player != getPlayerTurn() && player.getCard().getTypeGod() == TypeGod.OTHER_TURN_GOD) {
					if (move) {
						playerPossibleMoves.addAll(player.getCard().checkMove(map, w, turn));
					} else {
						Turn moveTurn = turn.clone();
						moveTurn.advance();
						playerPossibleMoves.addAll(player.getCard().checkMove(map, w, moveTurn));
					}
				}
			}
		} catch (NoMoveException e) {
			throw new AssertionError("Fatal error: Athena has been called with check move in a phase different from move");
		}
		playerPossibleMoves = DefeatManager.filterMoves(playerPossibleMoves);
	}
	/**
	 * Computes the workers builds.
	 * @param w the {@link it.polimi.ingsw.core.Worker} which builds are computed
	 */
	private synchronized void computeBuilds(Worker w) {
		try {
			playerPossibleBuilds.addAll(getPlayerTurn().getCard().checkBuild(map,w,turn));
		} catch (NoBuildException ignore) {
			// TODO: verify why we can ignore this exception
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
	/**
	 * Gets the parameter {@link #map}.
	 * @return value of {@link #map}
	 */
	public synchronized Map getMap() {
		return map;
	}
	/**
	 * Get a player inside the game which the name is specified by the parameter.
	 * @param name player's name
	 * @return a {@link it.polimi.ingsw.core.Player} whose name is {@code name}
	 * @throws IllegalArgumentException if the player doesn't exist
	 */
	public synchronized Player getPlayerByName(String name) throws IllegalArgumentException {
		for (Player p : players) {
			if (p.getPlayerName().equals(name)) {
				return p;
			}
		}
		throw new IllegalArgumentException();
	}
	/**
	 * Gets the parameter {@link #players}.
	 * @return value of {@link #players}
	 */
	public synchronized List<Player> getPlayers() {
		return new ArrayList<>(players);
	}
	/**
	 * Gets the parameter {@link #godCards}.
	 * @return value of {@link #godCards}
	 */
	public synchronized List<GodCard> getGods() {
		return new ArrayList<>(godCards);
	}
	/**
	 * Gets the parameter {@link #activePlayer}.
	 * @return value of {@link #activePlayer}
	 */
	public synchronized Player getPlayerTurn() {
		return activePlayer;
	}
	/**
	 * Gets the parameter {@link #winner}.
	 * @return value of {@link #winner}
	 */
	public synchronized Player getWinner() {
		return winner;
	}
	/**
	 * Gets the parameter {@link #isFinished}.
	 * @return value of {@link #isFinished}
	 */
	public synchronized boolean isFinished() {
		return isFinished;
	}
	/**
	 * Gets the parameter {@link #playerPossibleMoves}.
	 * @return value of {@link #playerPossibleMoves}
	 */
	public synchronized List<Move> getPlayerPossibleMoves() {
		return new ArrayList<>(playerPossibleMoves);
	}
	/**
	 * Gets the parameter {@link #playerPossibleBuilds}.
	 * @return value of {@link #playerPossibleBuilds}
	 */
	public synchronized List<Build> getPlayerPossibleBuilds() {
		return new ArrayList<>(playerPossibleBuilds);
	}
	/**
	 * Gets a copy of the attribute {@link #playerPossibleMoves} for the second worker.
	 * @return value of {@link #playerPossibleMoves} where worker is 1
	 */
	public synchronized List<Move> getPlayerPossibleMovesWorker1() {
		List<Move> worker1Moves = new ArrayList<>();

		for (int i = 0; i < playerPossibleMoves.size(); i++) {
			if (playerPossibleMoves.get(i).worker.workerID == getPlayerTurn().getWorker1().workerID) {
				worker1Moves.add(playerPossibleMoves.get(i));
			}
		}
		return worker1Moves;
	}
	/**
	 * Gets a copy of the attribute {@link #playerPossibleMoves} for the first worker.
	 * @return value of {@link #playerPossibleMoves} where worker is 2
	 */
	public synchronized List<Move> getPlayerPossibleMovesWorker2() {
		List<Move> worker2Moves = new ArrayList<>();

		for (int i = 0; i < playerPossibleMoves.size(); i++) {
			if (playerPossibleMoves.get(i).worker.workerID == getPlayerTurn().getWorker2().workerID) {
				worker2Moves.add(playerPossibleMoves.get(i));
			}
		}
		return worker2Moves;
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
	/**
	 * This method receives a list of player's names and set the order sorting the players arrayList.
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
	 * Sets the player's color indicated and updates the remote views about the colors actually chosen by the players sending an HashMap&lt;String,Color&gt; with player names and color chosen.
	 * @param player is the player which the color has to be set
	 * @param color the color chosen by the player
	 * @throws IllegalArgumentException if color or player is null or if it is trying to set the color of a player which isn't the active player
	 * @throws WrongPhaseException if the phase isn't the color selection phase
	 */
	public synchronized void setPlayerColor(String player, Color color) throws IllegalArgumentException, WrongPhaseException {
		if (player == null || color == null || !activePlayer.equals(getPlayerByName(player))) {
			throw new IllegalArgumentException();
		} else if (turn.getPhase() != Phase.COLORS) {
			throw new WrongPhaseException();
		} else {
			boolean found = false;
			for (int i = 0; i < players.size() && !found; i++) {
				if (players.get(i).getPlayerName().equals(player)) {
					found = true;
				}
			}
			if (!found) {
				throw new IllegalArgumentException();
			}
		}

		// now that I'm sure that the player is present it sets the color
		activePlayer.setPlayerColor(color);

		// it builds an hashmap to send to the clients
		HashMap<String,Color> colorInfo = new HashMap<>();
		for (int j = 0; j <= players.indexOf(activePlayer); j++) {
			colorInfo.put(players.get(j).getPlayerName(),players.get(j).getWorker1().color);
		}
		notifyColors(colorInfo);
	}
	/**
	 * The methods sets the game gods passed in the list using the {@link it.polimi.ingsw.core.gods.GodCardFactory} class.
	 * @param godNames a list of the gods' names
	 * @throws IllegalArgumentException if {@code godNames} is null or its size is different from the number of players
	 * @throws WrongPhaseException if it isn't the gods phase
	 */
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

		for (int i = 0; i < godNames.size(); i++) {
			GodCard godCreated = GodCardFactory.createGodCard(godNames.get(i).toUpperCase());
			godCards.add(godCreated);
		}
		notifyGods(godCards);
	}
	/**
	 * Sets the player's godCard.
	 * @param playerName player's name
	 * @param god god's name
	 * @throws IllegalArgumentException if {@code playerName} or {@code god} are null or if {@code god} is not a game god or is already selected by another player
	 * @throws WrongPhaseException if it isn't the gods selection phase
	 */
	public synchronized void setPlayerGod(String playerName, String god) throws IllegalArgumentException, WrongPhaseException {
		int x, godIndex = 0;
		if (playerName == null || god == null || !Constants.GODS_GOD_NAMES.contains(god.toUpperCase())) {
			throw new IllegalArgumentException();
		} else if (turn.getPhase() != Phase.GODS) {
			throw new WrongPhaseException();
		} else {
			boolean godFound = false;
			for (x = 0; x < godCards.size(); x++) {
				if (godCards.get(x).getName().toUpperCase().equals(god.toUpperCase())) {
					godFound = true;
					godIndex = x;
				}
			}

			if (!godFound) {
				throw new IllegalArgumentException();
			}
		}
		Player playerTurn;
		playerTurn = getPlayerByName(playerName);

		godCards.set(godIndex,GodCardFactory.createGodCard(god.toUpperCase(),playerTurn));
		playerTurn.setGodCard(godCards.get(godIndex));
		HashMap<String,GodCard> godsInfo = new HashMap<>();
		for (Player player : players) {
			try {
				GodCard card = player.getCard();
				godsInfo.put(player.getPlayerName(),card);
			} catch (IllegalStateException e) {}
		}
		notifyGods(godsInfo);
	}
	/**
	 * It sets the starting player positioning it on the first position of the arrayList.
	 * @param starterName starter's name
	 * @throws IllegalArgumentException if {@code starterName} is null or the player doesn't exist
	 * @throws WrongPhaseException if it isn't the starter phase
	 */
	public synchronized void setStarter(String starterName) throws IllegalArgumentException, WrongPhaseException {
		Player starter = null;
		if (starterName == null) {
			throw new IllegalArgumentException();
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
				throw new IllegalArgumentException();
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
	/**
	 * Sets the player's workers on the game map if the cells specified are free.
	 * @param req is the player's request
	 * @throws IllegalArgumentException it {@code req} is null or the positions aren't valid
	 * @throws WrongPhaseException if it isn't the worker position phase
	 */
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
		map.getCell(req.worker1.getFirst(),req.worker1.getSecond()).setWorker(player.getWorker1());
		map.getCell(req.worker2.getFirst(),req.worker2.getSecond()).setWorker(player.getWorker2());
		for (int i = 0; i < players.size() && finished; i++) {
			try {
				players.get(i).getWorker1();
			} catch (IllegalStateException e) {
				finished = false;
			}
		}
		notifyPositions(map,finished);
	}

	// GETTERS USED ON THE BEGINNING
	/**
	 * Gets a copy of the current turn.
	 * @return copy of {@link #turn}
	 */
	public synchronized Turn getPhase() {
		return turn.clone();
	}
}
