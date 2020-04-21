package it.polimi.ingsw.util.observers;

import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.game.NetAvailableBuildings;
import it.polimi.ingsw.network.game.NetAvailablePositions;

public interface ObserverController extends ObserverObject {
	void updatePositions(ObservableObject observed, Object netObject);

	Turn givePhase();
	NetAvailablePositions giveAvailablePositions();
	NetAvailableBuildings giveAvailableBuildings();
}
