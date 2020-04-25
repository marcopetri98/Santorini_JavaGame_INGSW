package it.polimi.ingsw.util.observers;

// necessary imports of Java SE
import it.polimi.ingsw.core.gods.GodCard;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
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
	public void notifyOrder(String[] order) throws NullPointerException {
		if (order == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateOrder(this,order);
		}
	}
	public void notifyColors(HashMap<String, Color> playerColors) throws NullPointerException {
		if (playerColors == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateColors(this,playerColors);
		}
	}
	public void notifyGods(List<GodCard> godsInfo) throws NullPointerException {
		if (godsInfo == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateGods(this,godsInfo);
		}
	}
	public void notifyGods(HashMap<String, GodCard> godsInfo) throws NullPointerException {
		if (godsInfo == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateGods(this,godsInfo);
		}
	}
	public void notifyGods(String godsInfo) throws NullPointerException {
		if (godsInfo == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateGods(this,godsInfo);
		}
	}
	public void notifyDefeat(String playerDefeated) throws NullPointerException {
		if (playerDefeated == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateDefeat(this,playerDefeated);
		}
	}
	public void notifyWinner(String playerWinner) throws NullPointerException {
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
	public void notifyActivePlayer(String player) throws NullPointerException {
		if (player == null) {
			throw new NullPointerException();
		}
		for (ObserverRemoteView obs : rvObs) {
			obs.updateActivePlayer(this,player);
		}
	}
}
