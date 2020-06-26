package it.polimi.ingsw.util.observers;

/**
 * This interface is an interface that represent a general observer object for a Santorini video game.
 */
public interface ObserverObject {
	/**
	 * Updated the observer after it has been notifies by the observer of the quit of a player.
	 * @param observed is the observable object
	 * @param playerName is the player's name
	 */
	void updateQuit(ObservableObject observed, String playerName);
}
