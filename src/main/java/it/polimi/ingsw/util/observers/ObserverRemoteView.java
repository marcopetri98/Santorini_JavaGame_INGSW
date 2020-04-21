package it.polimi.ingsw.util.observers;

public interface ObserverRemoteView extends ObserverObject {
	void updateOrder(ObservableGame observed, Object[] order);
	void updatePositions(ObservableGame observed, Object netObject, boolean finished);
	void updateDefeat(ObservableGame observed, Object playerDefeated);
	void updateWinner(ObservableGame observed, Object playerWinner);
}
