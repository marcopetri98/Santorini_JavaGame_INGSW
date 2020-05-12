package it.polimi.ingsw.core.driver;

import it.polimi.ingsw.core.Map;
import it.polimi.ingsw.core.gods.GodCard;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.RemoteView;
import it.polimi.ingsw.network.ServerClientListenerThread;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.observers.ObservableGame;
import it.polimi.ingsw.util.observers.ObservableObject;

import java.util.HashMap;
import java.util.List;

public class RemoteViewGameDriver extends RemoteView {
	public boolean updateDefeatCalled;
	public boolean updateWinnerCalled;
	public boolean updateOrderCalled;
	public boolean updateColorsCalled;
	public boolean updateGodsChallengerCalled;
	public boolean updateGodsChoiceCalled;
	public boolean updateGodsStarterCalled;
	public boolean updatePositionsCalled;
	public boolean updateMoveCalled;
	public boolean updateBuildCalled;
	public boolean updateQuitCalled;
	public boolean updatePhaseChangeCalled;
	public boolean updateActivePlayerCalled;
	public boolean updateGameFinishedCalled;

	public RemoteViewGameDriver(ServerClientListenerThread handler) throws NullPointerException {
		super(handler);
		resetCalled();
	}

	public void resetCalled() {
		updateDefeatCalled = false;
		updateWinnerCalled = false;
		updateOrderCalled = false;
		updateColorsCalled = false;
		updateGodsChallengerCalled = false;
		updateGodsChoiceCalled = false;
		updateGodsStarterCalled = false;
		updatePositionsCalled = false;
		updateMoveCalled = false;
		updateBuildCalled = false;
		updateQuitCalled = false;
		updatePhaseChangeCalled = false;
		updateActivePlayerCalled = false;
		updateGameFinishedCalled = false;
	}

	@Override
	public synchronized void updateDefeat(ObservableGame observed, String playerDefeated) {
		if (observed == null || playerDefeated == null) {
			throw new AssertionError("Wrong parameters from Game");
		} else {
			updateDefeatCalled = true;
		}
	}
	@Override
	public synchronized void updateWinner(ObservableGame observed, String playerWinner) {
		if (observed == null || playerWinner == null) {
			throw new AssertionError("Wrong parameters from Game");
		} else {
			updateWinnerCalled = true;
		}
	}
	@Override
	public synchronized void updateOrder(ObservableGame observed, String[] order) {
		if (observed == null || order == null) {
			throw new AssertionError("Wrong parameters from Game");
		} else {
			updateOrderCalled = true;
		}
	}
	@Override
	public synchronized void updateColors(ObservableGame observed, HashMap<String, Color> playerColors) {
		if (observed == null || playerColors == null || playerColors.size() == 0) {
			throw new AssertionError("Wrong parameters from Game");
		} else {
			updateColorsCalled = true;
		}
	}
	@Override
	public synchronized void updateGods(ObservableObject observed, List<GodCard> godsInfo) {
		if (observed == null || godsInfo == null) {
			throw new AssertionError("Wrong parameters from Game");
		} else {
			updateGodsChallengerCalled = true;
		}
	}
	@Override
	public synchronized void updateGods(ObservableObject observed, HashMap<String,GodCard> godsInfo) {
		if (observed == null || godsInfo == null) {
			throw new AssertionError("Wrong parameters from Game");
		} else {
			updateGodsChoiceCalled = true;
		}
	}
	@Override
	public synchronized void updateGods(ObservableObject observed, String godsInfo) {
		if (observed == null || godsInfo == null) {
			throw new AssertionError("Wrong parameters from Game");
		} else {
			updateGodsStarterCalled = true;
		}
	}
	@Override
	public synchronized void updatePositions(ObservableGame observed, Map gameMap, boolean finished) {
		if (observed == null || gameMap == null) {
			throw new AssertionError("Wrong parameters from Game");
		} else {
			updatePositionsCalled = true;
		}
	}
	@Override
	public synchronized void updateMove(ObservableObject observed, Map netMap) {
		if (observed == null || netMap != null) {
			throw new AssertionError("Wrong parameters from Game");
		} else {
			updateMoveCalled = true;
		}
	}
	@Override
	public synchronized void updateBuild(ObservableObject observed, Map netMap) {
		if (observed == null || netMap != null) {
			throw new AssertionError("Wrong parameters from Game");
		} else {
			updateBuildCalled = true;
		}
	}
	@Override
	public synchronized void updateQuit(ObservableObject observed, String playerName) {
		if (observed == null || playerName == null) {
			throw new AssertionError("Wrong parameters from Game");
		} else {
			updateQuitCalled = true;
		}
	}
	@Override
	public void updatePhaseChange(ObservableGame observed, Turn turn) {
		if (observed == null || turn == null) {
			throw new AssertionError("Wrong parameters from Game");
		} else {
			updatePhaseChangeCalled = true;
		}
	}
	@Override
	public void updateActivePlayer(ObservableGame observed, String playerName) {
		if (observed == null || playerName == null) {
			throw new AssertionError("Wrong parameters from Game");
		} else {
			updateActivePlayerCalled = true;
		}
	}
	@Override
	public void updateGameFinished(ObservableGame observed) {
		if (observed == null) {
			throw new AssertionError("Wrong parameters from Game");
		} else {
			updateGameFinishedCalled = true;
		}
	}

	public boolean isUpdateDefeatCalled() {
		if (updateWinnerCalled || updateOrderCalled || updateColorsCalled || updateGodsChallengerCalled || updateGodsChoiceCalled || updateGodsStarterCalled || updatePositionsCalled || updateMoveCalled || updateBuildCalled || updateQuitCalled || updatePhaseChangeCalled || updateActivePlayerCalled || updateGameFinishedCalled) {
			return false;
		}
		return updateDefeatCalled;
	}
	public boolean isUpdateWinnerCalled() {
		if (updateDefeatCalled || updateOrderCalled || updateColorsCalled || updateGodsChallengerCalled || updateGodsChoiceCalled || updateGodsStarterCalled || updatePositionsCalled || updateMoveCalled || updateBuildCalled || updateQuitCalled || updatePhaseChangeCalled || updateActivePlayerCalled || updateGameFinishedCalled) {
			return false;
		}
		return updateWinnerCalled;
	}
	public boolean isUpdateOrderCalled() {
		if (updateDefeatCalled || updateWinnerCalled || updateColorsCalled || updateGodsChallengerCalled || updateGodsChoiceCalled || updateGodsStarterCalled || updatePositionsCalled || updateMoveCalled || updateBuildCalled || updateQuitCalled || updatePhaseChangeCalled || updateActivePlayerCalled || updateGameFinishedCalled) {
			return false;
		}
		return updateOrderCalled;
	}
	public boolean isUpdateColorsCalled() {
		if (updateDefeatCalled || updateWinnerCalled || updateOrderCalled || updateGodsChallengerCalled || updateGodsChoiceCalled || updateGodsStarterCalled || updatePositionsCalled || updateMoveCalled || updateBuildCalled || updateQuitCalled || updatePhaseChangeCalled || updateActivePlayerCalled || updateGameFinishedCalled) {
			return false;
		}
		return updateColorsCalled;
	}
	public boolean isUpdateGodsChallengerCalled() {
		if (updateDefeatCalled || updateWinnerCalled || updateOrderCalled || updateColorsCalled || updateGodsChoiceCalled || updateGodsStarterCalled || updatePositionsCalled || updateMoveCalled || updateBuildCalled || updateQuitCalled || updatePhaseChangeCalled || updateActivePlayerCalled || updateGameFinishedCalled) {
			return false;
		}
		return updateGodsChallengerCalled;
	}
	public boolean isUpdateGodsChoiceCalled() {
		if (updateDefeatCalled || updateWinnerCalled || updateOrderCalled || updateColorsCalled || updateGodsChallengerCalled || updateGodsStarterCalled || updatePositionsCalled || updateMoveCalled || updateBuildCalled || updateQuitCalled || updatePhaseChangeCalled || updateActivePlayerCalled || updateGameFinishedCalled) {
			return false;
		}
		return updateGodsChoiceCalled;
	}
	public boolean isUpdateGodsStarterCalled() {
		if (updateDefeatCalled || updateWinnerCalled || updateOrderCalled || updateColorsCalled || updateGodsChallengerCalled || updateGodsChoiceCalled || updatePositionsCalled || updateMoveCalled || updateBuildCalled || updateQuitCalled || updatePhaseChangeCalled || updateActivePlayerCalled || updateGameFinishedCalled) {
			return false;
		}
		return updateGodsStarterCalled;
	}
	public boolean isUpdatePositionsCalled() {
		if (updateDefeatCalled || updateWinnerCalled || updateOrderCalled || updateColorsCalled || updateGodsChallengerCalled || updateGodsChoiceCalled || updateGodsStarterCalled || updateMoveCalled || updateBuildCalled || updateQuitCalled || updatePhaseChangeCalled || updateActivePlayerCalled || updateGameFinishedCalled) {
			return false;
		}
		return updatePositionsCalled;
	}
	public boolean isUpdateMoveCalled() {
		if (updateDefeatCalled || updateWinnerCalled || updateOrderCalled || updateColorsCalled || updateGodsChallengerCalled || updateGodsChoiceCalled || updateGodsStarterCalled || updatePositionsCalled || updateBuildCalled || updateQuitCalled || updatePhaseChangeCalled || updateActivePlayerCalled || updateGameFinishedCalled) {
			return false;
		}
		return updateMoveCalled;
	}
	public boolean isUpdateBuildCalled() {
		if (updateDefeatCalled || updateWinnerCalled || updateOrderCalled || updateColorsCalled || updateGodsChallengerCalled || updateGodsChoiceCalled || updateGodsStarterCalled || updatePositionsCalled || updateMoveCalled || updateQuitCalled || updatePhaseChangeCalled || updateActivePlayerCalled || updateGameFinishedCalled) {
			return false;
		}
		return updateBuildCalled;
	}
	public boolean isUpdateQuitCalled() {
		if (updateDefeatCalled || updateWinnerCalled || updateOrderCalled || updateColorsCalled || updateGodsChallengerCalled || updateGodsChoiceCalled || updateGodsStarterCalled || updatePositionsCalled || updateMoveCalled || updateBuildCalled || updatePhaseChangeCalled || updateActivePlayerCalled || updateGameFinishedCalled) {
			return false;
		}
		return updateQuitCalled;
	}
	public boolean isUpdatePhaseChangeCalled() {
		if (updateDefeatCalled || updateWinnerCalled || updateOrderCalled || updateColorsCalled || updateGodsChallengerCalled || updateGodsChoiceCalled || updateGodsStarterCalled || updatePositionsCalled || updateMoveCalled || updateBuildCalled || updateQuitCalled || updateActivePlayerCalled || updateGameFinishedCalled) {
			return false;
		}
		return updatePhaseChangeCalled;
	}
	public boolean isUpdateActivePlayerCalled() {
		if (updateDefeatCalled || updateWinnerCalled || updateOrderCalled || updateColorsCalled || updateGodsChallengerCalled || updateGodsChoiceCalled || updateGodsStarterCalled || updatePositionsCalled || updateMoveCalled || updateBuildCalled || updateQuitCalled || updatePhaseChangeCalled || updateGameFinishedCalled) {
			return false;
		}
		return updateActivePlayerCalled;
	}
	public boolean isUpdateGameFinishedCalled() {
		if (updateDefeatCalled || updateWinnerCalled || updateOrderCalled || updateColorsCalled || updateGodsChallengerCalled || updateGodsChoiceCalled || updateGodsStarterCalled || updatePositionsCalled || updateMoveCalled || updateBuildCalled || updateQuitCalled || updatePhaseChangeCalled || updateActivePlayerCalled) {
			return false;
		}
		return updateGameFinishedCalled;
	}
}
