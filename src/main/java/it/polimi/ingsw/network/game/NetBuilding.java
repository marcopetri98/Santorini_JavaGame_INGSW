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
	public NetBuilding(NetBuilding building) {
		level = building.getLevel();
		dome = building.isDome();
	}
	public NetBuilding() {
		level = 0;
		dome = false;
	}
	private NetBuilding(NetBuilding building, int newLevel) {
		level = newLevel;
		dome = building.isDome();
	}
	private NetBuilding(NetBuilding building, boolean newDome) {
		level = building.getLevel();
		dome = newDome;
	}

	/* **********************************************
	 *												*
	 *		MODIFIERS FOR USER IMMUTABLE OBJECT		*
	 * 												*
	 ************************************************/
	public NetBuilding setLevel(int level) {
		return new NetBuilding(this,level);
	}
	public NetBuilding setDome(boolean dome) {
		return new NetBuilding(this,dome);
	}

	/* **********************************************
	 *												*
	 * GETTERS AND METHODS WHICH DON'T CHANGE STATE	*
	 * 												*
	 ************************************************/
	public int getLevel() {
		return level;
	}
	public boolean isDome() {
		return dome;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NetBuilding) {
			NetBuilding other = (NetBuilding) obj;
			if (level == other.level && dome == other.dome) {
				return true;
			}
		}
		return false;
	}
}
