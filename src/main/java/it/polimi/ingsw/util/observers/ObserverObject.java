package it.polimi.ingsw.util.observers;

// necessary imports from other packages of the project
import it.polimi.ingsw.util.exceptions.WrongPhaseException;

public interface ObserverObject {
	boolean updateColors(Object playerColors) throws IllegalArgumentException, WrongPhaseException;
	boolean updateGods(Object playerGods) throws IllegalArgumentException, WrongPhaseException;
	boolean updatePositions(Object netObject, boolean finished) throws WrongPhaseException;
	boolean updateMove(Object netMap) throws NullPointerException, WrongPhaseException;
	boolean updateBuild(Object netMap) throws NullPointerException, WrongPhaseException;
}
