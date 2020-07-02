package it.polimi.ingsw.util.observers;

import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetGameSetup;
import it.polimi.ingsw.network.objects.NetGaming;

/**
 * This class represent the Observer for remote views inside the Distributed MVC pattern used to design Santorini video game, this class represent a remote view which communicate with a {@link it.polimi.ingsw.util.observers.ObserverController} to update the server about the choices done by the player during a match.
 */
public class ObservableRemoteView extends ObservableObject {
	private ObserverController ctrObs;

	/**
	 * Creates an observable remote view that can be observed by one {@link it.polimi.ingsw.util.observers.ObserverController}.
	 */
	public ObservableRemoteView() {
		ctrObs = null;
	}

	/**
	 * Add {@code obs} to the list of the observers for this object.
	 * @param obs is the observer
	 * @throws IllegalArgumentException if the {@code obs} parameter is null or is already an observer of this object
	 */
	@Override
	public void addObserver(ObserverObject obs) throws IllegalArgumentException {
		super.addObserver(obs);
		if (obs instanceof ObserverController) {
			if (ctrObs == null) {
				ctrObs = (ObserverController)obs;
			}
		}
	}
	/**
	 * Remove {@code obs} to the list of the observers for this object.
	 * @param obs is the observer
	 * @throws IllegalArgumentException if the {@code obs} parameter is null or isn't an observer of this object
	 */
	public void removeObserver(ObserverController obs) throws IllegalArgumentException {
		if (obs == null || !obs.equals(ctrObs)) {
			throw new IllegalArgumentException();
		}
		super.removeObserver(obs);
		ctrObs = null;
	}
	/**
	 * Remove all observers for this object.
	 */
	@Override
	public void removeAllObservers() {
		super.removeAllObservers();
		ctrObs = null;
	}
	/**
	 * This method notifies the {@link it.polimi.ingsw.util.observers.ObserverController} about the message sent by the user during worker positioning phase.
	 * @param netMap is the game setup message for worker positions sent by the user
	 * @throws NullPointerException if {@code netMap} is null
	 */
	public void notifyPositions(NetGameSetup netMap) throws NullPointerException {
		if (netMap == null) {
			throw new NullPointerException();
		}
		if (ctrObs != null) {
			ctrObs.updatePositions(this,netMap);
		}
	}
	/**
	 * This method notifies the {@link it.polimi.ingsw.util.observers.ObserverController} about the message sent by the user during color choice phase.
	 * @param playerColors is the message with the color chosen by the player
	 * @throws NullPointerException if {@code playerColors} is null
	 */
	public void notifyColors(NetColorPreparation playerColors) throws NullPointerException {
		if (playerColors == null) {
			throw new NullPointerException();
		}
		if (ctrObs != null) {
			ctrObs.updateColors(this,playerColors);
		}
	}
	/**
	 * This method notifies the {@link it.polimi.ingsw.util.observers.ObserverController} about the message sent by the user during gods phase.
	 * @param playerGods is the message which contains the player's request
	 * @throws NullPointerException if {@code playerGods} is null
	 */
	public void notifyGods(NetDivinityChoice playerGods) throws NullPointerException {
		if (playerGods == null) {
			throw new NullPointerException();
		}
		if (ctrObs != null) {
			ctrObs.updateGods(this,playerGods);
		}
	}
	/**
	 * This method notifies the {@link it.polimi.ingsw.util.observers.ObserverController} about the message sent by the user during moving phase.
	 * @param netMap is the gaming message sent by the user
	 * @throws NullPointerException if {@code netMap} is null
	 */
	public void notifyMove(NetGaming netMap) throws NullPointerException {
		if (netMap == null) {
			throw new NullPointerException();
		}
		if (ctrObs != null) {
			ctrObs.updateMove(this,netMap);
		}
	}
	/**
	 * This method notifies the {@link it.polimi.ingsw.util.observers.ObserverController} about the message sent by the user during building phase.
	 * @param netMap is the gaming message sent by the user
	 * @throws NullPointerException if {@code netMap} is null
	 */
	public void notifyBuild(NetGaming netMap) throws NullPointerException {
		if (netMap == null) {
			throw new NullPointerException();
		}
		if (ctrObs != null) {
			ctrObs.updateBuild(this,netMap);
		}
	}
	/**
	 * Notifies the {@link it.polimi.ingsw.util.observers.ObserverController} which observes this object that the player that is observing the game is now quit.
	 */
	public void notifyObserverQuit() {
		if (ctrObs != null) {
			ctrObs.observerQuit(this);
		}
	}
}
