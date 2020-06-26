package it.polimi.ingsw.util.observers;

// necessary imports from other packages of the project

// necessary imports of Java SE
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a general observable object from an arbitrary number of observers of the type of {@link it.polimi.ingsw.util.observers.ObserverObject}.
 */
public class ObservableObject {
	private final List<ObserverObject> observers;

	// constructors
	/**
	 * This constructor creates an observable object with an empty list of observers ready to be changed.
	 */
	public ObservableObject() {
		observers = new ArrayList<>();
	}

	// modifiers and updaters
	/**
	 * Add {@code obs} to the list of the observers for this object.
	 * @param obs is the observer
	 * @throws IllegalArgumentException if the {@code obs} parameter is null or is already an observer of this object
	 */
	public void addObserver(ObserverObject obs) throws IllegalArgumentException {
		if (observers.contains(obs) || obs == null) {
			throw new IllegalArgumentException();
		}

		observers.add(obs);
	}
	/**
	 * Remove {@code obs} to the list of the observers for this object.
	 * @param obs is the observer
	 * @throws IllegalArgumentException if the {@code obs} parameter is null or is already an observer of this object
	 */
	public void removeObserver(ObserverObject obs) throws IllegalArgumentException {
		if (!observers.contains(obs) || obs == null) {
			throw new IllegalArgumentException();
		}

		observers.remove(obs);
	}
	/**
	 * Remove all observers for this object.
	 */
	public void removeAllObservers() {
		observers.clear();
	}
	/**
	 * This function notifies all the observers that the specified player in the {@code playerName} parameter has quit.
	 * @param playerName is the player's name
	 * @throws NullPointerException if {@code playerName} is null
	 */
	public void notifyQuit(String playerName) throws NullPointerException {
		if (playerName == null) {
			throw new NullPointerException();
		}
		for (ObserverObject obs : observers) {
			obs.updateQuit(this,playerName);
		}
	}
}
