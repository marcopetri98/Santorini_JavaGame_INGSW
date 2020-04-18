package it.polimi.ingsw.util.observers;

public interface ObserverObjectController extends ObserverObject {
	void updateQuit(String playerName) throws NullPointerException;
}