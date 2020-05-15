package it.polimi.ingsw.controller.stub;

import it.polimi.ingsw.controller.Builder;
import it.polimi.ingsw.core.Build;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.network.game.NetBuild;

import java.util.List;

public class BuilderStub extends Builder {
	private boolean buildCalled;
	private boolean toBuild;

	public BuilderStub(Game g) {
		super(g);
		buildCalled = false;
		toBuild = false;
	}

	// stub methods
	@Override
	public boolean build(NetBuild netBuild, List<Build> possibilities) {
		buildCalled = true;
		return toBuild;
	}

	// check if called
	public boolean isBuildCalled() {
		return buildCalled;
	}
	public void setToBuild(boolean value) {
		toBuild = value;
	}
}
