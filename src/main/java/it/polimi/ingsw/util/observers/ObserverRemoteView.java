package it.polimi.ingsw.util.observers;

import it.polimi.ingsw.core.Map;
import it.polimi.ingsw.core.gods.GodCard;
import it.polimi.ingsw.core.state.Turn;

import java.awt.Color;
import java.util.List;
import java.util.HashMap;

public interface ObserverRemoteView extends ObserverObject {
	void updateGods(ObservableObject observed, List<GodCard> godsInfo);
	void updateGods(ObservableObject observed, HashMap<String,GodCard> godsInfo);
	void updateGods(ObservableObject observed, String godsInfo);
	void updateColors(ObservableGame observed, HashMap<String,Color> playerColors);
	void updateOrder(ObservableGame observed, String[] order);
	void updatePositions(ObservableGame observed, Map gameMap, boolean finished);
	void updateMove(ObservableObject observed, Map netMap);
	void updateBuild(ObservableObject observed, Map netMap);
	void updateDefeat(ObservableGame observed, String playerDefeated);
	void updateWinner(ObservableGame observed, String playerWinner);
	void updateActivePlayer(ObservableGame observed, String playerName);
	void updatePhaseChange(ObservableGame observed, Turn turn);
}
