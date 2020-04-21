package it.polimi.ingsw.util.observers;

public interface ObserverRemoteView extends ObserverObject {
	void updateOrder(Object[] order);
	void updateDefeat(Object playerDefeated);
	void updateWinner(Object playerWinner);
	void getControllerResult(boolean result);
}
