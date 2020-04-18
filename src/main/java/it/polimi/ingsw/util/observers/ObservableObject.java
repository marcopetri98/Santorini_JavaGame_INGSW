package it.polimi.ingsw.util.observers;

// necessary imports from other packages of the project

// necessary imports of Java SE
import it.polimi.ingsw.util.exceptions.WrongPhaseException;

import java.util.ArrayList;
import java.util.List;

public class ObservableObject {
	private List<ObserverObject> observers;

	// constructors
	public ObservableObject() {
		observers = new ArrayList<>();
	}

	// modifiers and updaters
	public void addObserver(ObserverObject obs) throws IllegalArgumentException {
		if (observers.contains(obs)) {
			throw new IllegalArgumentException();
		}

		observers.add(obs);
	}
	public void removeObserver(ObserverObject obs) throws IllegalArgumentException {
		if (!observers.contains(obs)) {
			throw new IllegalArgumentException();
		}

		observers.remove(obs);
	}
	public void notifyOrder(Object[] order) throws NullPointerException, IllegalStateException, WrongPhaseException {
		if (order == null) {
			throw new NullPointerException();
		} else if (checkIfAlsoRemoteObserver()) {
			throw new IllegalStateException();
		}
		ObserverObjectRemoteView temp;
		for (ObserverObject obs : observers) {
			if (obs instanceof ObserverObjectRemoteView) {
				temp = (ObserverObjectRemoteView) obs;
				temp.updateOrder(order);
			}
		}
	}
	public void notifyColors(Object playerColors) throws NullPointerException, WrongPhaseException {
		if (playerColors == null) {
			throw new NullPointerException();
		}
		for (ObserverObject obs : observers) {
			obs.updateColors(playerColors);
		}
	}
	public void notifyGods(Object playerGods) throws NullPointerException, WrongPhaseException {
		if (playerGods == null) {
			throw new NullPointerException();
		}
		for (ObserverObject obs : observers) {
			obs.updateGods(playerGods);
		}
	}
	public void notifyPositions(Object netMap, boolean finished) throws NullPointerException, WrongPhaseException {
		if (netMap == null) {
			throw new NullPointerException();
		}
		for (ObserverObject obs : observers) {
			obs.updatePositions(netMap,finished);
		}
	}
	public void notifyMove(Object netMap) throws NullPointerException, WrongPhaseException {
		if (netMap == null) {
			throw new NullPointerException();
		}
		for (ObserverObject obs : observers) {
			obs.updateMove(netMap);
		}
	}
	public void notifyBuild(Object netMap) throws NullPointerException, WrongPhaseException {
		if (netMap == null) {
			throw new NullPointerException();
		}
		for (ObserverObject obs : observers) {
			obs.updateBuild(netMap);
		}
	}
	public void notifyDefeat(Object playerDefeated) throws IllegalStateException, IllegalArgumentException, WrongPhaseException {
		if (playerDefeated == null) {
			throw new NullPointerException();
		} else if (checkIfAlsoRemoteObserver()) {
			throw new IllegalStateException();
		}
		ObserverObjectRemoteView temp;
		for (ObserverObject obs : observers) {
			if (obs instanceof ObserverObjectRemoteView) {
				temp = (ObserverObjectRemoteView) obs;
				temp.updateDefeat(playerDefeated);
			}
		}
	}
	public void notifyWinner(Object playerWinner) throws IllegalStateException, IllegalArgumentException, WrongPhaseException {
		if (playerWinner == null) {
			throw new NullPointerException();
		} else if (checkIfAlsoRemoteObserver()) {
			throw new IllegalStateException();
		}
		ObserverObjectRemoteView temp;
		for (ObserverObject obs : observers) {
			if (obs instanceof ObserverObjectRemoteView) {
				temp = (ObserverObjectRemoteView) obs;
				temp.updateWinner(playerWinner);
			}
		}
	}
	public void notifyQuit(String playerName) throws NullPointerException {
		if (playerName == null) {
			throw new NullPointerException();
		}
		ObserverObjectController temp;
		for (ObserverObject obs : observers) {
			if (obs instanceof ObserverObjectController) {
				temp = (ObserverObjectController) obs;
				temp.updateQuit(playerName);
			}
		}
	}

	// private methods
	private boolean checkIfAlsoRemoteObserver() {
		boolean found = false;
		for (int i = 0; i < observers.size() && !found; i++) {
			if (observers.get(i) instanceof ObserverObjectRemoteView) {
				found = true;
			}
		}
		return found;
	}
}
