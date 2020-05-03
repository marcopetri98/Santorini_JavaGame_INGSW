package it.polimi.ingsw.util.observers;

import it.polimi.ingsw.network.game.NetAvailableBuildings;
import it.polimi.ingsw.network.game.NetAvailablePositions;
import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetGameSetup;
import it.polimi.ingsw.network.objects.NetGaming;

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
	@Override
	public void removeAllObservers() {
		super.removeAllObservers();
		ctrObs = null;
	}
	public void notifyPositions(NetGameSetup netMap) throws NullPointerException {
		if (netMap == null) {
			throw new NullPointerException();
		}
		if (ctrObs != null) {
			ctrObs.updatePositions(this,netMap);
		}
	}
	public void notifyColors(NetColorPreparation playerColors) throws NullPointerException {
		if (playerColors == null) {
			throw new NullPointerException();
		}
		if (ctrObs != null) {
			ctrObs.updateColors(this,playerColors);
		}
	}
	public void notifyGods(NetDivinityChoice playerGods) throws NullPointerException {
		if (playerGods == null) {
			throw new NullPointerException();
		}
		if (ctrObs != null) {
			ctrObs.updateGods(this,playerGods);
		}
	}
	public void notifyMove(NetGaming netMap) throws NullPointerException {
		if (netMap == null) {
			throw new NullPointerException();
		}
		if (ctrObs != null) {
			ctrObs.updateMove(this,netMap);
		}
	}
	public void notifyBuild(NetGaming netMap) throws NullPointerException {
		if (netMap == null) {
			throw new NullPointerException();
		}
		if (ctrObs != null) {
			ctrObs.updateBuild(this,netMap);
		}
	}
	public void notifyObserverQuit() {
		if (ctrObs != null) {
			ctrObs.observerQuit(this);
		}
	}

	// TODO: maybe askPhase isn't necessary
	/*public Turn askPhase() {
		return ctrObs.givePhase();
	}*/
	public NetAvailablePositions askPositions() {
		if (ctrObs != null) {
			return ctrObs.giveAvailablePositions();
		}
		return null;
	}
	public NetAvailableBuildings askBuildings() {
		if (ctrObs != null) {
			return ctrObs.giveAvailableBuildings();
		}
		return null;
	}
}
