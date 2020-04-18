package it.polimi.ingsw.util.observers;

import it.polimi.ingsw.util.exceptions.WrongPhaseException;

public interface ObserverObjectRemoteView extends ObserverObject {
	void updateOrder(Object[] order) throws NullPointerException, WrongPhaseException;
	void updateDefeat(Object playerDefeated) throws NullPointerException, WrongPhaseException;
	void updateWinner(Object playerWinner) throws NullPointerException, WrongPhaseException;
}
