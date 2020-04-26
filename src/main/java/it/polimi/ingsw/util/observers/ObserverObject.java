package it.polimi.ingsw.util.observers;

// necessary imports from other packages of the project
import it.polimi.ingsw.util.exceptions.WrongPhaseException;

public interface ObserverObject {
	void updateQuit(ObservableObject observed, String playerName);
}
