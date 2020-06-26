package it.polimi.ingsw.ui.gui.viewModel;

import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.game.NetBuild;
import it.polimi.ingsw.network.game.NetMap;
import it.polimi.ingsw.network.game.NetMove;
import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetGaming;
import it.polimi.ingsw.util.Color;

import java.util.*;

/**
 * This is a class for the GUI which holds all the information about the current game with the appropriate setters and getters.
 */
public class GameState {
	private String player;
	private String activePlayer;
	private int playerNumber;
	private List<String> players;
	private List<String> godsName;
	private Map<String,String> gods;
	private Map<String,Color> colors;
	private Turn turn;
	private NetMap map;
	private List<NetMove> possibleMoves;
	private List<NetBuild> possibleBuilds;

	public GameState() {
		refresh();
	}
	/**
	 * Initialize the game state.
	 */
	public void refresh() {
		player = null;
		activePlayer = null;
		playerNumber = 0;
		players = new ArrayList<>();
		godsName = new ArrayList<>();
		gods = new LinkedHashMap<>();
		colors = new LinkedHashMap<>();
		turn = new Turn();
		map = null;
		possibleMoves = new ArrayList<>();
		possibleBuilds = new ArrayList<>();
	}

	/* **********************************************
	 *												*
	 *			GETTERS FOR THIS CLASS				*
	 * 												*
	 ************************************************/
	/**
	 * Gets the parameter {@link #player}.
	 * @return value of {@link #player}
	 */
	public String getPlayer() {
		return player;
	}
	/**
	 * Gets the parameter {@link #activePlayer}.
	 * @return value of {@link #activePlayer}
	 */
	public String getActivePlayer() {
		return activePlayer;
	}
	/**
	 * Gets the parameter {@link #playerNumber}.
	 * @return value of {@link #playerNumber}
	 */
	public int getPlayerNumber() {
		return playerNumber;
	}
	/**
	 * Gets a clone of the parameter {@link #players}.
	 * @return value of {@link #players}
	 */
	public List<String> getPlayers() {
		return new ArrayList<>(players);
	}
	/**
	 * Gets a clone of the parameter {@link #godsName}.
	 * @return value of {@link #godsName}
	 */
	public List<String> getGodsName() {
		return new ArrayList<>(godsName);
	}
	/**
	 * Gets a clone of the parameter {@link #gods}.
	 * @return value of {@link #gods}
	 */
	public Map<String, String> getGods() {
		return new LinkedHashMap<>(gods);
	}
	/**
	 * Gets a clone of the parameter {@link #colors}.
	 * @return value of {@link #colors}
	 */
	public Map<String, Color> getColors() {
		return new LinkedHashMap<>(colors);
	}
	/**
	 * Gets a clone of the parameter {@link #turn}.
	 * @return value of {@link #turn}
	 */
	public Turn getTurn() {
		return turn.clone();
	}
	/**
	 * Gets the parameter {@link #map}.
	 * @return value of {@link #map}
	 */
	public NetMap getMap() {
		return map;
	}
	/**
	 * Gets a clone of the parameter {@link #possibleMoves}.
	 * @return value of {@link #possibleMoves}
	 */
	public List<NetMove> getPossibleMoves() {
		return new ArrayList<>(possibleMoves);
	}
	/**
	 * Gets a clone of the parameter {@link #possibleBuilds}.
	 * @return value of {@link #possibleBuilds}
	 */
	public List<NetBuild> getPossibleBuilds() {
		return new ArrayList<>(possibleBuilds);
	}

	/* **********************************************
	 *												*
	 *		ADVANCED GETTERS FOR THIS CLASS			*
	 * 												*
	 ************************************************/
	/**
	 * Says if there is a {@link it.polimi.ingsw.network.game.NetMove} like object in {@link #possibleMoves}.
	 * @param wantedMove a {@link it.polimi.ingsw.network.game.NetMove}
	 * @return true if there is a {@link it.polimi.ingsw.network.game.NetMove} like object
	 */
	public int containsLike(NetMove wantedMove) {
		int counter = 0;
		for (int i = 0; i < possibleMoves.size(); i++) {
			if (possibleMoves.get(i).isLike(wantedMove)) {
				counter++;
			}
		}
		return counter;
	}
	/**
	 * Says if there is a {@link it.polimi.ingsw.network.game.NetBuild} like object in {@link #possibleBuilds}.
	 * @param wantedBuild a {@link it.polimi.ingsw.network.game.NetBuild}
	 * @return true if there is a {@link it.polimi.ingsw.network.game.NetBuild} like object
	 */
	public int containsLike(NetBuild wantedBuild) {
		int counter = 0;
		for (int i = 0; i < possibleBuilds.size(); i++) {
			if (possibleBuilds.get(i).isLike(wantedBuild)) {
				counter++;
			}
		}
		return counter;
	}

	/* **********************************************
	 *												*
	 *			SETTERS FOR THIS CLASS				*
	 * 												*
	 ************************************************/
	/**
	 * Sets {@link #player} value.
	 * @param value player's name
	 * @throws NullPointerException if {@code value} is null
	 */
	public void setPlayer(String value) throws NullPointerException {
		if (value == null) {
			throw new NullPointerException();
		}
		player = value;
	}
	/**
	 * Sets {@link #activePlayer} value.
	 * @param name player's name
	 * @throws IllegalArgumentException if {@code value} is null or not contained in players' list
	 */
	public void setActivePlayer(String name) throws IllegalArgumentException {
		if (!players.contains(name) || name == null) {
			throw new IllegalArgumentException();
		}
		activePlayer = name;
	}
	/**
	 * Sets {@link #playerNumber} value.
	 * @param value number of players
	 * @throws IllegalArgumentException if {@code value} is different from 2 or 3
	 */
	public void setPlayerNumber(int value) throws IllegalArgumentException {
		if (value != 2 && value != 3) {
			throw new IllegalArgumentException();
		}
		playerNumber = value;
	}
	/**
	 * Sets {@link #players} value.
	 * @param names list of players' names
	 * @throws IllegalArgumentException if {@code names} is null or there are duplicated
	 */
	public void setPlayers(List<String> names) throws IllegalArgumentException {
		if (names == null) {
			throw new IllegalArgumentException();
		} else {
			for (int i = 0; i < names.size(); i++) {
				for (int j = i+1; j < names.size(); j++) {
					if (names.get(i).equals(names.get(j))) {
						throw new IllegalArgumentException();
					}
				}
			}
		}
		players = new ArrayList<>(names);
	}
	/**
	 * Sets {@link #players} value.
	 * @param names an array of players' names
	 * @throws IllegalArgumentException if {@code names} is null or there are duplicated
	 */
	public void setPlayers(String[] names) throws IllegalArgumentException {
		if (names == null) {
			throw new IllegalArgumentException();
		} else {
			for (int i = 0; i < names.length; i++) {
				for (int j = i+1; j < names.length; j++) {
					if (names[i].equals(names[j])) {
						throw new IllegalArgumentException();
					}
				}
			}
		}
		players = new ArrayList<>(Arrays.asList(names));
	}
	/**
	 * Sets {@link #activePlayer} value.
	 * @param starter sets the starter ad active player
	 * @throws IllegalArgumentException if {@code starter} is null or not contained in players' list
	 */
	public void setStarter(String starter) throws IllegalArgumentException {
		if (starter == null || !players.contains(starter)) {
			throw new IllegalArgumentException();
		}
		if (players.indexOf(starter) != 0) {
			List<String> temp = new ArrayList<>();
			for (int i = players.indexOf(starter); i < players.size(); i++) {
				temp.add(players.get(i));
			}
			for (int i = 0; i < players.indexOf(starter); i++) {
				temp.add(players.get(i));
			}
			players = temp;
		}
	}
	/**
	 * Sets {@link #godsName} value.
	 * @param names names of the gods to set
	 * @throws IllegalArgumentException if {@code names} is null or contains duplicates
	 */
	public void setGodsName(String[] names) throws IllegalArgumentException {
		if (names == null) {
			throw new IllegalArgumentException();
		} else {
			for (int i = 0; i < names.length; i++) {
				for (int j = i+1; j < names.length; j++) {
					if (names[i].equals(names[j])) {
						throw new IllegalArgumentException();
					}
				}
			}
		}
		godsName = new ArrayList<>(Arrays.asList(names));
	}
	/**
	 * Sets {@link #player} value.
	 * @param names a list of gods' names
	 * @throws IllegalArgumentException if {@code names} is null or contains duplicates
	 */
	public void setGodsName(List<String> names) throws IllegalArgumentException {
		if (names == null) {
			throw new IllegalArgumentException();
		} else {
			for (int i = 0; i < names.size(); i++) {
				for (int j = i+1; j < names.size(); j++) {
					if (names.get(i).equals(names.get(j))) {
						throw new IllegalArgumentException();
					}
				}
			}
		}
		godsName = new ArrayList<>(names);
	}
	/**
	 * Sets {@link #gods} value.
	 * @param mapping players' choices of gods map
	 * @throws IllegalArgumentException if {@code mapping} is null
	 */
	public void setGods(Map<String,String> mapping) throws IllegalArgumentException {
		if (mapping == null) {
			throw new IllegalArgumentException();
		}
		gods = new LinkedHashMap<>(mapping);
	}
	/**
	 * Sets {@link #gods} value.
	 * @param divinityMessage players network message with gods choices
	 * @throws IllegalArgumentException if {@code divinityMessage} is null
	 */
	public void setGods(NetDivinityChoice divinityMessage) throws IllegalArgumentException {
		if (divinityMessage == null) {
			throw new IllegalArgumentException();
		}
		gods = new LinkedHashMap<>(divinityMessage.getPlayerGodMap());
	}
	/**
	 * Sets {@link #colors} value.
	 * @param mapping players' choices of colors map
	 * @throws IllegalArgumentException if {@code mapping} is null
	 */
	public void setColors(Map<String,Color> mapping) throws IllegalArgumentException {
		if (mapping == null) {
			throw new IllegalArgumentException();
		}
		colors = new LinkedHashMap<>(mapping);
	}
	/**
	 * Sets {@link #colors} value.
	 * @param colorMessage players network message with color choices
	 * @throws IllegalArgumentException if {@code colorMessage} is null
	 */
	public void setColors(NetColorPreparation colorMessage) throws IllegalArgumentException {
		if (colorMessage == null) {
			throw new IllegalArgumentException();
		}
		colors = new LinkedHashMap<>(colorMessage.getPlayerColorsMap());
	}
	/**
	 * Sets {@link #map} value.
	 * @param map a network map
	 * @throws IllegalArgumentException if {@code map} is null
	 */
	public void setMap(NetMap map) throws IllegalArgumentException {
		if (map == null) {
			throw new IllegalArgumentException();
		}
		this.map = map;
	}
	/**
	 * Sets {@link #possibleMoves} value.
	 * @param gamingMessage gaming network message
	 * @throws IllegalArgumentException if {@code gamingMessage} is null or if its {@code availablePositions} field is null
	 */
	public void setPossibleMoves(NetGaming gamingMessage) throws IllegalArgumentException {
		if (gamingMessage == null || gamingMessage.availablePositions == null) {
			throw new IllegalArgumentException();
		}
		possibleMoves = new ArrayList<>();
		possibleMoves.addAll(gamingMessage.availablePositions.moves);
	}
	/**
	 * Sets {@link #possibleMoves} value.
	 * @param moveList a list of {@link it.polimi.ingsw.network.game.NetMove}
	 * @throws NullPointerException if {@code moveList} is null
	 */
	public void setPossibleMoves(List<NetMove> moveList) throws NullPointerException {
		if (moveList == null) {
			throw new NullPointerException();
		}
		possibleMoves = new ArrayList<>();
		possibleMoves.addAll(moveList);
	}
	/**
	 * Sets {@link #possibleBuilds} value.
	 * @param gamingMessage gaming network message
	 * @throws IllegalArgumentException if {@code gamingMessage} is null or if its {@code availableBuildings} field is null
	 */
	public void setPossibleBuilds(NetGaming gamingMessage) throws IllegalArgumentException {
		if (gamingMessage == null || gamingMessage.availableBuildings == null) {
			throw new IllegalArgumentException();
		}
		possibleBuilds = new ArrayList<>();
		possibleBuilds.addAll(gamingMessage.availableBuildings.builds);
	}
	/**
	 * Sets {@link #possibleBuilds} value.
	 * @param buildList a list of {@link it.polimi.ingsw.network.game.NetBuild}
	 * @throws NullPointerException if {@code buildList} is null
	 */
	public void setPossibleBuilds(List<NetBuild> buildList) throws NullPointerException {
		if (buildList == null) {
			throw new NullPointerException();
		}
		possibleBuilds = new ArrayList<>();
		possibleBuilds.addAll(buildList);
	}

	/* **********************************************
	 *												*
	 *			MODIFIERS FOR THIS CLASS			*
	 * 												*
	 ************************************************/
	/**
	 * It removes the given player from the players' list.
	 * @param name player's name to be removed
	 * @throws IllegalArgumentException if {@code name} is null or not contained in the players' list
	 */
	public void removePlayer(String name) throws IllegalArgumentException {
		if (!players.contains(name) || name == null) {
			throw new IllegalArgumentException();
		}
		players.remove(name);
	}
	/**
	 * Advance the turn to the next game phase.
	 */
	public void advancePhase() {
		turn.advance();
	}
}
