package it.polimi.ingsw.util.observers;

// necessary imports from other packages of the project
import it.polimi.ingsw.util.exceptions.WrongPhaseException;

public interface ObserverObject {
	void updateColors(Object playerColors);
	void updateGods(Object playerGods);
	void updatePositions(Object netObject, boolean finished);
	void updateMove(Object netMap);
	void updateBuild(Object netMap);
	void updateQuit(String playerName);
}
