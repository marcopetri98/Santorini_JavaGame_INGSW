package it.polimi.ingsw.network.game;

import it.polimi.ingsw.core.Building;

public class NetBuilding {
	public final int level;
	public final boolean dome;

	public NetBuilding(Building building) {
		level = building.getLevel();
		dome = building.getDome();
	}

	// TODO: is this really necessary?
	// security check methods
	public boolean trueBuilding() {
		return level <= 3 && level >= 0;
	}
}
