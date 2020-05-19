package it.polimi.ingsw.util.observers;

import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.game.NetAvailableBuildings;
import it.polimi.ingsw.network.game.NetAvailablePositions;
import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetGameSetup;
import it.polimi.ingsw.network.objects.NetGaming;

public interface ObserverController extends ObserverObject {
	void updateGods(ObservableObject observed, NetDivinityChoice playerGods);
	void updateColors(ObservableObject observed, NetColorPreparation playerColors);
	void updatePositions(ObservableObject observed, NetGameSetup netObject);
	void updatePass(ObservableRemoteView observed, String playerName);
	void updateMove(ObservableObject observed, NetGaming netMap);
	void updateBuild(ObservableObject observed, NetGaming netMap);
	void observerQuit(ObservableRemoteView observed);

	Turn givePhase();
}
