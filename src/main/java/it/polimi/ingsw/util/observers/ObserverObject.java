package it.polimi.ingsw.util.observers;

// necessary imports from other packages of the project
import it.polimi.ingsw.util.exceptions.WrongPhaseException;

public interface ObserverObject {
	void updateColors(ObservableObject observed, Object playerColors);
	void updateGods(ObservableObject observed, Object playerGods);
	void updateMove(ObservableObject observed, Object netMap);
	void updateBuild(ObservableObject observed, Object netMap);
	void updateQuit(ObservableObject observed, String playerName);
}
