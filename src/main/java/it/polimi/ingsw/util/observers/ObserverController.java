package it.polimi.ingsw.util.observers;

import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.game.NetAvailableBuildings;
import it.polimi.ingsw.network.game.NetAvailablePositions;
import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;

public interface ObserverController extends ObserverObject {
	void updateGods(ObservableObject observed, NetDivinityChoice playerGods);
	void updateColors(ObservableObject observed, NetColorPreparation playerColors);
	void updatePositions(ObservableObject observed, Object netObject);

	Turn givePhase();
	NetAvailablePositions giveAvailablePositions();
	NetAvailableBuildings giveAvailableBuildings();
}
