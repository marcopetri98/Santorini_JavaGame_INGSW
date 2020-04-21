package it.polimi.ingsw.util.observers;

// necessary imports of Java SE
import java.util.ArrayList;
import java.util.List;

public class ObservableGame extends ObservableObject {
	private final List<ObserverRemoteView> rvObs;

	public ObservableGame() {
		rvObs = new ArrayList<>();
	}

	@Override
	public void addObserver(ObserverObject obs) throws IllegalArgumentException {
		super.addObserver(obs);
		if (obs instanceof ObserverRemoteView) {
			rvObs.add((ObserverRemoteView)obs);
		}
	}
	public void notifyOrder(Object[] order) throws NullPointerException {
		if (order == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateOrder(this,order);
		}
	}
	public void notifyDefeat(Object playerDefeated) throws NullPointerException {
		if (playerDefeated == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateDefeat(this,playerDefeated);
		}
	}
	public void notifyWinner(Object playerWinner) throws NullPointerException {
		if (playerWinner == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateWinner(this,playerWinner);
		}
	}
	public void notifyPositions(Object netMap, boolean finished) throws NullPointerException {
		if (netMap == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updatePositions(this,netMap,finished);
		}
	}
}
