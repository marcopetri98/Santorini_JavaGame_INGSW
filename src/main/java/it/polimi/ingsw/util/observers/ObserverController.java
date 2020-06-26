package it.polimi.ingsw.util.observers;

import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetGameSetup;
import it.polimi.ingsw.network.objects.NetGaming;

/**
 * This class is the observer class for the controller class in the Distributed MVC that observes {@link it.polimi.ingsw.util.observers.ObservableRemoteView}. This object is the observer object which will be inherited by a controller for the game.
 */
public interface ObserverController extends ObserverObject {
	/**
	 * Updates the observer when the observer calls this method wants to perform an action during the gods phase.
	 * @param observed is the {@link it.polimi.ingsw.util.observers.ObserverObject} which called this method
	 * @param playerGods is the {@link it.polimi.ingsw.network.objects.NetDivinityChoice} message sent from the player
	 */
	void updateGods(ObservableObject observed, NetDivinityChoice playerGods);
	/**
	 * Updates the observer when the observer calls this method because a player wants to choose a color.
	 * @param observed is the {@link it.polimi.ingsw.util.observers.ObserverObject} which called this method
	 * @param playerColors is the {@link it.polimi.ingsw.network.objects.NetColorPreparation} message sent from the player
	 */
	void updateColors(ObservableObject observed, NetColorPreparation playerColors);
	/**
	 * Updates the observer when the observer calls this method because a player wants to position its worker on the game table.
	 * @param observed is the {@link it.polimi.ingsw.util.observers.ObserverObject} which called this method
	 * @param netObject is the {@link it.polimi.ingsw.network.objects.NetGameSetup} message sent from the player
	 */
	void updatePositions(ObservableObject observed, NetGameSetup netObject);
	/**
	 * Updates the observer when the observer calls this method because someone wants to move.
	 * @param observed is the {@link it.polimi.ingsw.util.observers.ObserverObject} which called this method
	 * @param netMap is the {@link it.polimi.ingsw.network.objects.NetGaming} message sent from the player
	 */
	void updateMove(ObservableObject observed, NetGaming netMap);
	/**
	 * Updates the observer when the observer calls this method because someone wants to build.
	 * @param observed is the {@link it.polimi.ingsw.util.observers.ObserverObject} which called this method
	 * @param netMap is the {@link it.polimi.ingsw.network.objects.NetGaming} message sent from the player
	 */
	void updateBuild(ObservableObject observed, NetGaming netMap);
	/**
	 * Updates the observer when the observer calls this method because a player has quit.
	 * @param observed is the {@link it.polimi.ingsw.util.observers.ObservableRemoteView} which called this method
	 */
	void observerQuit(ObservableRemoteView observed);
}
