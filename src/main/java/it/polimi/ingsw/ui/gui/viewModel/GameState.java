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
	public String getPlayer() {
		return player;
	}
	public String getActivePlayer() {
		return activePlayer;
	}
	public int getPlayerNumber() {
		return playerNumber;
	}
	public List<String> getPlayers() {
		return new ArrayList<>(players);
	}
	public List<String> getGodsName() {
		return new ArrayList<>(godsName);
	}
	public Map<String, String> getGods() {
		return new LinkedHashMap<>(gods);
	}
	public Map<String, Color> getColors() {
		return new LinkedHashMap<>(colors);
	}
	public Turn getTurn() {
		return turn.clone();
	}
	public NetMap getMap() {
		return map;
	}
	public List<NetMove> getPossibleMoves() {
		return new ArrayList<>(possibleMoves);
	}
	public List<NetBuild> getPossibleBuilds() {
		return new ArrayList<>(possibleBuilds);
	}

	/* **********************************************
	 *												*
	 *		ADVANCED GETTERS FOR THIS CLASS			*
	 * 												*
	 ************************************************/
	public int containsLike(NetMove wantedMove) {
		int counter = 0;
		for (int i = 0; i < possibleMoves.size(); i++) {
			if (possibleMoves.get(i).isLike(wantedMove)) {
				counter++;
			}
		}
		return counter;
	}
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
	public void setPlayer(String value) throws NullPointerException {
		if (value == null) {
			throw new NullPointerException();
		}
		player = value;
	}
	public void setActivePlayer(String name) throws IllegalArgumentException {
		if (!players.contains(name) || name == null) {
			throw new IllegalArgumentException();
		}
		activePlayer = name;
	}
	public void setPlayerNumber(int value) throws IllegalArgumentException {
		if (value != 2 && value != 3) {
			throw new IllegalArgumentException();
		}
		playerNumber = value;
	}
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
	public void setGods(Map<String,String> mapping) throws IllegalArgumentException {
		if (mapping == null) {
			throw new IllegalArgumentException();
		}
		gods = new LinkedHashMap<>(mapping);
	}
	public void setGods(NetDivinityChoice divinityMessage) throws IllegalArgumentException {
		if (divinityMessage == null) {
			throw new IllegalArgumentException();
		}
		gods = new LinkedHashMap<>(divinityMessage.getPlayerGodMap());
	}
	public void setColors(Map<String,Color> mapping) throws IllegalArgumentException {
		if (mapping == null) {
			throw new IllegalArgumentException();
		}
		colors = new LinkedHashMap<>(mapping);
	}
	public void setColors(NetColorPreparation colorMessage) throws IllegalArgumentException {
		if (colorMessage == null) {
			throw new IllegalArgumentException();
		}
		colors = new LinkedHashMap<>(colorMessage.getPlayerColorsMap());
	}
	public void setMap(NetMap map) throws IllegalArgumentException {
		if (map == null) {
			throw new IllegalArgumentException();
		}
		this.map = map;
	}
	public void setPossibleMoves(NetGaming gamingMessage) throws IllegalArgumentException {
		if (gamingMessage == null || gamingMessage.availablePositions == null) {
			throw new IllegalArgumentException();
		}
		possibleMoves = new ArrayList<>();
		possibleMoves.addAll(gamingMessage.availablePositions.moves);
	}
	public void setPossibleMoves(List<NetMove> moveList) throws NullPointerException {
		if (moveList == null) {
			throw new NullPointerException();
		}
		possibleMoves = new ArrayList<>();
		possibleMoves.addAll(moveList);
	}
	public void setPossibleBuilds(NetGaming gamingMessage) throws IllegalArgumentException {
		if (gamingMessage == null || gamingMessage.availableBuildings == null) {
			throw new IllegalArgumentException();
		}
		possibleBuilds = new ArrayList<>();
		possibleBuilds.addAll(gamingMessage.availableBuildings.builds);
	}
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
	public void removePlayer(String name) throws IllegalArgumentException {
		if (!players.contains(name) || name == null) {
			throw new IllegalArgumentException();
		}
		players.remove(name);
	}
	public void advancePhase() {
		turn.advance();
	}
	public void goToPhase(Phase phase) throws IllegalArgumentException {
		if (!turn.getPhase().lessThan(phase)) {
			throw new IllegalArgumentException();
		}
		while (turn.getPhase() != phase) {
			turn.advance();
		}
	}
	public void goToPhase(GodsPhase phase) throws IllegalArgumentException {
		if (turn.getPhase() == Phase.SETUP && turn.getPhase() == Phase.PLAYERTURN) {
			throw new IllegalArgumentException();
		}
		while (turn.getGodsPhase() != phase) {
			turn.advance();
		}
	}
	public void goToPhase(GamePhase phase) {
		while (turn.getGamePhase() != phase) {
			turn.advance();
		}
	}
}
