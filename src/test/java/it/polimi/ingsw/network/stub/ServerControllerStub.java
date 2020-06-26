package it.polimi.ingsw.network.stub;

import it.polimi.ingsw.controller.ServerController;
import it.polimi.ingsw.core.*;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.game.NetAvailableBuildings;
import it.polimi.ingsw.network.game.NetAvailablePositions;
import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetGameSetup;
import it.polimi.ingsw.network.objects.NetGaming;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.observers.ObservableObject;
import it.polimi.ingsw.util.observers.ObservableRemoteView;

import java.util.ArrayList;
import java.util.List;

public class ServerControllerStub extends ServerController {
	private final Game game;
	private boolean updateColorsCalled;
	private boolean updateGodsCalled;
	private boolean updatePositionsCalled;
	private boolean updatePassCalled;
	private boolean updateMoveCalled;
	private boolean updateBuildCalled;
	private boolean updateQuitCalled;
	private boolean observerQuitCalled;
	private final Turn turn;

	public ServerControllerStub(Game g) throws NullPointerException {
		super(g);
		game = g;
		updateColorsCalled = false;
		updateGodsCalled = false;
		updatePositionsCalled = false;
		updatePassCalled = false;
		updateMoveCalled = false;
		updateBuildCalled = false;
		updateQuitCalled = false;
		observerQuitCalled = false;
		turn = new Turn();
	}

	public void resetCalls() {
		updateColorsCalled = false;
		updateGodsCalled = false;
		updatePositionsCalled = false;
		updatePassCalled = false;
		updateMoveCalled = false;
		updateBuildCalled = false;
		observerQuitCalled = false;
	}
	public boolean isUpdateColorsCalled() {
		return updateColorsCalled;
	}
	public void setUpdateColorsCalled(boolean updateColorsCalled) {
		this.updateColorsCalled = updateColorsCalled;
	}
	public boolean isUpdateGodsCalled() {
		return updateGodsCalled;
	}
	public boolean isUpdatePositionsCalled() {
		return updatePositionsCalled;
	}
	public boolean isUpdatePassCalled() {
		return updatePassCalled;
	}
	public boolean isUpdateMoveCalled() {
		return updateMoveCalled;
	}
	public boolean isUpdateBuildCalled() {
		return updateBuildCalled;
	}
	public boolean isUpdateQuitCalled() {
		return updateQuitCalled;
	}
	public boolean isObserverQuitCalled() {
		return observerQuitCalled;
	}
	public void setPhase(Phase ph) {
		while (turn.getPhase() != ph) {
			turn.advance();
		}
	}
	public void setPhase(GodsPhase ph) {
		setPhase(Phase.GODS);
		while (turn.getGodsPhase() != ph) {
			turn.advance();
		}
	}
	public void setPhase(GamePhase ph) {
		setPhase(Phase.PLAYERTURN);
		while (turn.getGamePhase() != ph) {
			turn.advance();
		}
	}

	@Override
	public synchronized void updateColors(ObservableObject observed, NetColorPreparation playerColors) {
		updateColorsCalled = true;
	}
	@Override
	public synchronized void updateGods(ObservableObject observed, NetDivinityChoice playerGods) {
		updateGodsCalled = true;
	}
	@Override
	public synchronized void updatePositions(ObservableObject observed, NetGameSetup netObject) {
		updatePositionsCalled = true;
	}
	@Override
	public synchronized void updateMove(ObservableObject observed, NetGaming moveMessage) {
		updateMoveCalled = true;
	}
	@Override
	public synchronized void updateBuild(ObservableObject observed, NetGaming buildMessage) {
		updateBuildCalled = true;
	}
	@Override
	public synchronized void updateQuit(ObservableObject observed, String playerName) {
		updateQuitCalled = true;
	}
	@Override
	public synchronized void observerQuit(ObservableRemoteView observed) {
		observerQuitCalled = true;
	}
}
