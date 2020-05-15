package it.polimi.ingsw.controller.stub;

import it.polimi.ingsw.controller.VictoryManager;
import it.polimi.ingsw.core.Cell;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.core.Move;

import java.util.List;

public class VictoryStub extends VictoryManager {
	private boolean checkVictoryCalled;

	public VictoryStub(Game g) {
		super(g);
		checkVictoryCalled = false;
	}

	// stub methods
	@Override
	public void checkVictory(Cell before, Cell after, List<Move> possibleCells) {
		checkVictoryCalled = true;
	}

	// check if called
	public boolean isCheckVictoryCalled() {
		return checkVictoryCalled;
	}
}
