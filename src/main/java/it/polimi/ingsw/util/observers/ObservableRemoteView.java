package it.polimi.ingsw.util.observers;

import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.game.NetAvailableBuildings;
import it.polimi.ingsw.network.game.NetAvailablePositions;
import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;

public class ObservableRemoteView extends ObservableObject {
	private ObserverController ctrObs;

	public ObservableRemoteView() {
		ctrObs = null;
	}

	@Override
	public void addObserver(ObserverObject obs) throws IllegalArgumentException {
		if (obs instanceof ObserverController) {
			if (ctrObs != null) {
				ctrObs = (ObserverController)obs;
			}
		} else {
			super.addObserver(obs);
		}
	}
	public void notifyPositions(Object netMap) throws NullPointerException {
		if (netMap == null) {
			throw new NullPointerException();
		}
		ctrObs.updatePositions(this,netMap);
	}
	public void notifyColors(NetColorPreparation playerColors) throws NullPointerException {
		if (playerColors == null) {
			throw new NullPointerException();
		}
		ctrObs.updateColors(this,playerColors);
	}
	public void notifyGods(NetDivinityChoice playerGods) throws NullPointerException {
		if (playerGods == null) {
			throw new NullPointerException();
		}
		ctrObs.updateGods(this,playerGods);
	}

	public Turn askPhase() {
		return ctrObs.givePhase();
	}
	public NetAvailablePositions askPositions() {
		return ctrObs.giveAvailablePositions();
	}
	public NetAvailableBuildings askBuildings() {
		return ctrObs.giveAvailableBuildings();
	}
}
