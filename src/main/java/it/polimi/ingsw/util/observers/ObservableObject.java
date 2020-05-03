package it.polimi.ingsw.util.observers;

// necessary imports from other packages of the project

// necessary imports of Java SE
import java.util.ArrayList;
import java.util.List;

public class ObservableObject {
	private final List<ObserverObject> observers;

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
	public void removeAllObservers() {
		observers.clear();
	}
	public void notifyQuit(String playerName) throws NullPointerException {
		if (playerName == null) {
			throw new NullPointerException();
		}
		for (ObserverObject obs : observers) {
			obs.updateQuit(this,playerName);
		}
	}
}
