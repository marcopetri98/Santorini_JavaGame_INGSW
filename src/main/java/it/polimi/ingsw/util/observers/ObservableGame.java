package it.polimi.ingsw.util.observers;

// necessary imports of Java SE
import it.polimi.ingsw.core.Build;
import it.polimi.ingsw.core.Map;
import it.polimi.ingsw.core.Move;
import it.polimi.ingsw.core.gods.GodCard;
import it.polimi.ingsw.core.state.Turn;

import it.polimi.ingsw.util.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is the class that represent the Observer pattern used in Distributed MVC pattern used to create Santorini's video game, it is a class that represent a game that can be observer from {@link it.polimi.ingsw.util.observers.ObserverRemoteView} to gain information about the change of the game state using Observer pattern.
 */
public class ObservableGame extends ObservableObject {
	private final List<ObserverRemoteView> rvObs;

	/**
	 * Creates and observable game with a list of {@link it.polimi.ingsw.util.observers.ObserverRemoteView} ready to be changed.
	 */
	public ObservableGame() {
		rvObs = new ArrayList<>();
	}

	/**
	 * Add {@code obs} to the list of the observers for this object.
	 * @param obs is the observer
	 * @throws IllegalArgumentException if the {@code obs} parameter is null or is already an observer of this object
	 */
	@Override
	public void addObserver(ObserverObject obs) throws IllegalArgumentException {
		super.addObserver(obs);
		if (obs instanceof ObserverRemoteView) {
			rvObs.add((ObserverRemoteView)obs);
		}
	}
	/**
	 * Remove {@code obs} to the list of the observers for this object.
	 * @param obs is the observer
	 * @throws IllegalArgumentException if the {@code obs} parameter is null or isn't an observer of this object
	 */
	public void removeObserver(ObserverRemoteView obs) throws IllegalArgumentException {
		if (!rvObs.contains(obs) || obs == null) {
			throw new IllegalArgumentException();
		}
		super.removeObserver(obs);
		rvObs.remove(obs);
	}
	/**
	 * Remove all observers for this object.
	 */
	@Override
	public void removeAllObservers() {
		super.removeAllObservers();
	}
	/**
	 * It notifies all the {@link it.polimi.ingsw.util.observers.ObserverRemoteView} observers that the gaming order changed.
	 * @param order is an array containing the names of the players in gaming order
	 * @throws NullPointerException it order is null
	 */
	public void notifyOrder(String[] order) throws NullPointerException {
		if (order == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateOrder(this,order);
		}
	}
	/**
	 * It notifies all the {@link it.polimi.ingsw.util.observers.ObserverRemoteView} observers about the colors chosen by the players.
	 * @param playerColors is the map of colors chosen by the players
	 * @throws NullPointerException if {@code playerColors} is null
	 */
	public void notifyColors(HashMap<String, Color> playerColors) throws NullPointerException {
		if (playerColors == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateColors(this,playerColors);
		}
	}
	/**
	 * It notifies all the {@link it.polimi.ingsw.util.observers.ObserverRemoteView} observers that the gods have been chosen by the challenger.
	 * @param godsInfo is the list of gods chosen by the challenger
	 * @throws NullPointerException if {@code godsInfo} is null
	 */
	public void notifyGods(List<GodCard> godsInfo) throws NullPointerException {
		if (godsInfo == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateGods(this,godsInfo);
		}
	}
	/**
	 * It notifies all the {@link it.polimi.ingsw.util.observers.ObserverRemoteView} observers that the gods chosen are updated.
	 * @param godsInfo is the map between player and cards chosen
	 * @throws NullPointerException if {@code godsInfo} is null
	 */
	public void notifyGods(HashMap<String, GodCard> godsInfo) throws NullPointerException {
		if (godsInfo == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateGods(this,godsInfo);
		}
	}
	/**
	 * It notifies all the {@link it.polimi.ingsw.util.observers.ObserverRemoteView} observers that the starter player has been chosen by the challenger.
	 * @param godsInfo is the information about the starter player
	 * @throws NullPointerException if {@code godsInfo} is null
	 */
	public void notifyGods(String godsInfo) throws NullPointerException {
		if (godsInfo == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateGods(this,godsInfo);
		}
	}
	/**
	 * It notifies all the {@link it.polimi.ingsw.util.observers.ObserverRemoteView} observers that a player has defeated.
	 * @param playerDefeated is the defeated player
	 * @throws NullPointerException if {@code playerDefeated} is null
	 */
	public void notifyDefeat(String playerDefeated) throws NullPointerException {
		if (playerDefeated == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateDefeat(this,playerDefeated);
		}
	}
	/**
	 * It notifies all the {@link it.polimi.ingsw.util.observers.ObserverRemoteView} observers that a player has won the game.
	 * @param playerWinner is the winner of this game
	 * @throws NullPointerException if {@code playerWinner} is null
	 */
	public void notifyWinner(String playerWinner) throws NullPointerException {
		if (playerWinner == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateWinner(this,playerWinner);
		}
	}
	/**
	 * It notifies all the {@link it.polimi.ingsw.util.observers.ObserverRemoteView} observers that the gaming map changed having 2 more workers on it.
	 * @param netMap is the gaming map
	 * @param finished carries the information if the positioning phase is finished or not
	 * @throws NullPointerException if {@code netMap} is null
	 */
	public void notifyPositions(Map netMap, boolean finished) throws NullPointerException {
		if (netMap == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updatePositions(this,netMap,finished);
		}
	}
	/**
	 * It notifies all the {@link it.polimi.ingsw.util.observers.ObserverRemoteView} observers that the gaming map changed.
	 * @param netMap is the map of the game
	 * @throws NullPointerException if {@code netMap} is null
	 */
	public void notifyMove(Map netMap) throws NullPointerException {
		if (netMap == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateMove(this,netMap);
		}
	}
	/**
	 * It notifies all the {@link it.polimi.ingsw.util.observers.ObserverRemoteView} observers that the gaming map changed.
	 * @param netMap is the map of the game
	 * @throws NullPointerException if {@code netMap} is null
	 */
	public void notifyBuild(Map netMap) throws NullPointerException {
		if (netMap == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateBuild(this,netMap);
		}
	}
	/**
	 * It notifies all the {@link it.polimi.ingsw.util.observers.ObserverRemoteView} observers that the active player can perform moves specified in {@code moves} and builds specified in {@code builds} for the current game phase.
	 * @param moves is a list of the possible moves
	 * @param builds is a list of the possible builds
	 * @throws NullPointerException if {@code moves} or {@code builds} are null
	 */
	public void notifyPossibleActions(List<Move> moves, List<Build> builds) throws NullPointerException {
		if (moves == null || builds == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updatePossibleActions(this,moves,builds);
		}
	}
	/**
	 * It notifies all the {@link it.polimi.ingsw.util.observers.ObserverRemoteView} observers that the gaming order changed.
	 * @param turn if turn is null
	 * @throws NullPointerException if {@code turn} is null
	 */
	public void notifyPhaseChange(Turn turn) throws NullPointerException {
		if (turn == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updatePhaseChange(this,turn);
		}
	}
	/**
	 * It notifies all the {@link it.polimi.ingsw.util.observers.ObserverRemoteView} observers about the new active player for the current turn.
	 * @param player is the active player for the current turn
	 * @throws NullPointerException if {@code player} is null
	 */
	public void notifyActivePlayer(String player) throws NullPointerException {
		if (player == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateActivePlayer(this,player);
		}
	}
	/**
	 * It notifies all the {@link it.polimi.ingsw.util.observers.ObserverRemoteView} observers that a player has disconnected during the setup of the game and for this reason the game is finished.
	 */
	public void notifyEndForDisconnection() {
		for (ObserverRemoteView obs : rvObs) {
			obs.updateGameFinished(this);
		}
	}
}
