package it.polimi.ingsw.network.game;

import it.polimi.ingsw.core.Building;

import java.io.Serializable;

public class NetBuilding implements Serializable {
	public final int level;
	public final boolean dome;

	public NetBuilding(Building building) {
		level = building.getLevel();
		dome = building.getDome();
	}

	public int getLevel() {
		return level;
	}
	public boolean isDome() {
		return dome;
	}

	// TODO: is this really necessary?
	// security check methods
	public boolean trueBuilding() {
		return level <= 3 && level >= 0;
	}
}
