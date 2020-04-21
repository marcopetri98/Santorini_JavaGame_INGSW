package it.polimi.ingsw.util.observers;

// necessary imports of Java SE
import java.util.ArrayList;
import java.util.List;

public class ObservableGame extends ObservableObject {
	private List<ObserverRemoteView> rvObs;

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
			obs.updateOrder(order);
		}
	}
	public void notifyDefeat(Object playerDefeated) throws NullPointerException {
		if (playerDefeated == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateDefeat(playerDefeated);
		}
	}
	public void notifyWinner(Object playerWinner) throws NullPointerException {
		if (playerWinner == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateWinner(playerWinner);
		}
	}
}
