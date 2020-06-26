package it.polimi.ingsw.util.observers;

import it.polimi.ingsw.core.Build;
import it.polimi.ingsw.core.Map;
import it.polimi.ingsw.core.Move;
import it.polimi.ingsw.core.gods.GodCard;
import it.polimi.ingsw.core.state.Turn;

import it.polimi.ingsw.util.Color;
import java.util.List;
import java.util.HashMap;

/**
 * This is the class that represent the Observer pattern for remote view inside the Distributed MVC pattern, this object will be overridden by the remote view of the server that implements the Distributed MVC remote view pattern.
 */
public interface ObserverRemoteView extends ObserverObject {
	/**
	 * This method is called when game state changed and gods have been chosen by the challenger.
	 * @param observed is the observable object which called this method
	 * @param godsInfo is the list of gods chosen by the challenger
	 */
	void updateGods(ObservableObject observed, List<GodCard> godsInfo);
	/**
	 * This method is called when game state changed because a player has chosen a god, the player will be informed about current gods chosen.
	 * @param observed is the observable object which called this method
	 * @param godsInfo is the map of gods chosen by the players at the moment
	 */
	void updateGods(ObservableObject observed, HashMap<String,GodCard> godsInfo);
	/**
	 * This method is called when game state changed because the challenger chose the starter and the player must be informed.
	 * @param observed is the observable object which called this method
	 * @param godsInfo is the name of the starter
	 */
	void updateGods(ObservableObject observed, String godsInfo);
	/**
	 * This method is called when game state changed because a player has chosen a color and the player will be informed about the chosen colors.
	 * @param observed is the observable game which called this method
	 * @param playerColors is the map of the color chosen by the players for the moment
	 */
	void updateColors(ObservableGame observed, HashMap<String,Color> playerColors);
	/**
	 * This method is called when game state changed updating the gaming order, the player will be notified about the new order.
	 * @param observed is the observable game which called this method
	 * @param order is the order of gaming of the player in this game
	 */
	void updateOrder(ObservableGame observed, String[] order);
	/**
	 * This method is called when game state changed because a player positioned its worker on the game table, the player will be informed.
	 * @param observed is the observable game which called this method
	 * @param gameMap is the current game map
	 * @param finished if the worker positioning phase is finished
	 */
	void updatePositions(ObservableGame observed, Map gameMap, boolean finished);
	/**
	 * This method is called when game state changed and has calculated the possible action that the active player can perform, the player will be informed about them.
	 * @param observed is the observable game which called this method
	 * @param moves is the list of possible moves
	 * @param builds is the list of possible builds
	 */
	void updatePossibleActions(ObservableGame observed, List<Move> moves, List<Build> builds);
	/**
	 * This method is called when game state changed because someone moved a worker and the player will be notified.
	 * @param observed is the observable object which called this method
	 * @param netMap is the current game map
	 */
	void updateMove(ObservableObject observed, Map netMap);
	/**
	 * This method is called when game state changed because someone has built on the map and the player will be notified.
	 * @param observed is the observable object which called this method
	 * @param netMap is the current game map
	 */
	void updateBuild(ObservableObject observed, Map netMap);
	/**
	 * This method is called when game state changed and a player has been defeated and this information will be sent to the client.
	 * @param observed is the observable game which called this method
	 * @param playerDefeated is the player defeated
	 */
	void updateDefeat(ObservableGame observed, String playerDefeated);
	/**
	 * This method is called when game state changed because there is a winner and this information will be sent to the client.
	 * @param observed is the observable game which called this method
	 * @param playerWinner is the winner
	 */
	void updateWinner(ObservableGame observed, String playerWinner);
	/**
	 * This method is called when game state changed because the turn changed and there is a new active player and the client must be notified.
	 * @param observed is the observable game which called this method
	 * @param playerName is the player's name
	 */
	void updateActivePlayer(ObservableGame observed, String playerName);
	/**
	 * This method is called when game state changed because it entered in a different phase and this information will be sent to the user.
	 * @param observed is the observable game which called this method
	 * @param turn the current game phase
	 */
	void updatePhaseChange(ObservableGame observed, Turn turn);
	/**
	 * This method is called when game state changed because it is finished and the player will be notified.
	 * @param observed is the observable game which called this method
	 */
	void updateGameFinished(ObservableGame observed);
}
