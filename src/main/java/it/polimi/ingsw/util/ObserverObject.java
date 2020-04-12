package it.polimi.ingsw.util;

// necessary imports from other packages of the project
import it.polimi.ingsw.util.exceptions.WrongPhaseException;

public interface ObserverObject {
	void updateColors(Object playerColors) throws IllegalArgumentException, WrongPhaseException;
	void updateGods(Object playerGods) throws IllegalArgumentException, WrongPhaseException;
	void updatePositions(Object netObject, boolean finished) throws WrongPhaseException;
	void updateMove(Object netMap) throws NullPointerException, WrongPhaseException;
	void updateBuild(Object netMap) throws NullPointerException, WrongPhaseException;
}
