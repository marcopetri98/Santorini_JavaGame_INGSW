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
	public void updatePass(ObservableRemoteView observed, String playerName) {
		updatePassCalled = true;
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
	@Override
	public Turn givePhase() {
		return turn.clone();
	}
	@Override
	public NetAvailablePositions giveAvailablePositions() {
		List<Move> testList = new ArrayList<>();
		Map board = new Map();
		testList.add(new Move(TypeMove.SIMPLE_MOVE,board.getCell(1,1),board.getCell(1,2),new Worker(Color.RED,new Player("Er test"),1)));
		return new NetAvailablePositions(testList);
	}
	@Override
	public NetAvailableBuildings giveAvailableBuildings() {
		List<Build> testList = new ArrayList<>();
		Map board = new Map();
		testList.add(new Build(new Worker(Color.RED,new Player("Er test"),1),board.getCell(1,1),false,TypeBuild.SIMPLE_BUILD));
		return new NetAvailableBuildings(testList);
	}
}
