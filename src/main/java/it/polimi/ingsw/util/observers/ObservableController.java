package it.polimi.ingsw.util.observers;

// necessary imports of Java SE
import java.util.ArrayList;
import java.util.List;

public class ObservableController extends ObservableObject {
	private List<ObserverRemoteView> rvObs;

	public ObservableController() {
		rvObs = new ArrayList<>();
	}

	@Override
	public void addObserver(ObserverObject obs) throws IllegalArgumentException {
		super.addObserver(obs);
		if (obs instanceof ObserverRemoteView) {
			rvObs.add((ObserverRemoteView)obs);
		}
	}
}
