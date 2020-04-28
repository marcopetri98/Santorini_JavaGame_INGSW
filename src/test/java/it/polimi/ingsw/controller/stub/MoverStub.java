package it.polimi.ingsw.controller.stub;

import it.polimi.ingsw.controller.Mover;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.core.Move;
import it.polimi.ingsw.network.game.NetMove;

import java.util.List;

public class MoverStub extends Mover {
	private boolean moveCalled;
	private boolean toMove;

	public MoverStub(Game g) {
		super(g);
		moveCalled = false;
		toMove = false;
	}

	// stub methods
	public boolean move(NetMove netmove, List<Move> possibilities) {
		moveCalled = true;
		return toMove;
	}

	// check if called
	public boolean isMoveCalled() {
		return moveCalled;
	}
	public void setToMove(boolean value) {
		toMove = value;
	}
}
