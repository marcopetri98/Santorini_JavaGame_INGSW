package it.polimi.ingsw.controller.stub;

import it.polimi.ingsw.core.Build;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.core.Move;
import it.polimi.ingsw.core.Player;
import it.polimi.ingsw.util.Pair;

import java.awt.*;
import java.util.List;

public class GameStub extends Game {
	private boolean applyMoveCalled;
	private boolean applyBuildCalled;
	private boolean applyWinCalled;
	private boolean applyDefeatCalled;
	private boolean setOrderCalled;
	private boolean setPlayerColorCalled;
	private boolean setGameGodsCalled;
	private boolean setPlayerGodCalled;
	private boolean setStarterCalled;
	private boolean setWorkerPositionsCalled;

	public GameStub(String[] names) {
		super(names);
		applyMoveCalled = false;
		applyBuildCalled = false;
		applyWinCalled = false;
		applyDefeatCalled = false;
		setOrderCalled = false;
		setPlayerColorCalled = false;
		setGameGodsCalled = false;
		setPlayerGodCalled = false;
		setStarterCalled = false;
		setWorkerPositionsCalled = false;
	}

	// stub methods
	public void applyMove(Move move) {
		applyMoveCalled = true;
	}
	public void applyBuild(Build build) {
		applyBuildCalled = true;
	}
	public void applyWin(Player player) {
		applyWinCalled = true;
	}
	public void applyDefeat(Player player) {
		applyDefeatCalled = true;
	}
	public void setOrder(List<String> playerOrder) {
		setOrderCalled = true;
	}
	public void setPlayerColor(String player, Color color) {
		setPlayerColorCalled = true;
	}
	public void setPlayerGod(String playerName, String god) {
		setPlayerGodCalled = true;
	}
	public void setGameGods(List<String> godNames) {
		setGameGodsCalled = true;
	}
	public void setStarter(String starterName) {
		setStarterCalled = true;
	}
	public void setWorkerPositions(String playerName, Pair<Integer,Integer> worker1, Pair<Integer,Integer> worker2) {
		setWorkerPositionsCalled = true;
	}

	// check methods
	public void resetCounters() {
		applyMoveCalled = false;
		applyBuildCalled = false;
		applyWinCalled = false;
		applyDefeatCalled = false;
		setOrderCalled = false;
		setPlayerColorCalled = false;
		setGameGodsCalled = false;
		setPlayerGodCalled = false;
		setStarterCalled = false;
		setWorkerPositionsCalled = false;
	}
	public boolean isApplyMoveCalled() {
		return applyMoveCalled;
	}
	public boolean isApplyBuildCalled() {
		return applyBuildCalled;
	}
	public boolean isApplyWinCalled() {
		return applyWinCalled;
	}
	public boolean isApplyDefeatCalled() {
		return applyDefeatCalled;
	}
	public boolean isSetOrderCalled() {
		return setOrderCalled;
	}
	public boolean isSetPlayerColorCalled() {
		return setPlayerColorCalled;
	}
	public boolean isSetGameGodsCalled() {
		return setGameGodsCalled;
	}
	public boolean isSetPlayerGodCalled() {
		return setPlayerGodCalled;
	}
	public boolean isSetStarterCalled() {
		return setStarterCalled;
	}
	public boolean isSetWorkerPositionsCalled() {
		return setWorkerPositionsCalled;
	}
}
