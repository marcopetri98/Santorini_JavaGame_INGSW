package it.polimi.ingsw.network.game;

import it.polimi.ingsw.core.Building;

import java.io.Serializable;

/**
 * This is an immutable class that represent a network object of a building of the game map.
 */
public class NetBuilding implements Serializable {
	public final int level;
	public final boolean dome;

	/**
	 * Creates a network building from a {@link it.polimi.ingsw.core.Building}.
	 * @param building a building
	 */
	public NetBuilding(Building building) {
		level = building.getLevel();
		dome = building.getDome();
	}
	/**
	 * Creates a network building from another network building.
	 * @param building a network building
	 */
	public NetBuilding(NetBuilding building) {
		level = building.getLevel();
		dome = building.isDome();
	}
	/**
	 * Creates a network building from a {@link it.polimi.ingsw.network.game.NetBuild}.
	 * @param nB a {@link it.polimi.ingsw.network.game.NetBuild}
	 */
	public NetBuilding(NetBuild nB) {
		if(nB.level < 3) {
			this.level = nB.level + 1;
		} else {
			this.level = 3;
		}
		this.dome = nB.dome;
	}
	/**
	 * Copies a building changing a value.
	 * @param building the building to modify
	 * @param newLevel the new level
	 */
	private NetBuilding(NetBuilding building, int newLevel) {
		level = newLevel;
		dome = building.isDome();
	}
	/**
	 * Copies a building changing a value.
	 * @param building the building to modify
	 * @param newDome the new dome attribute
	 */
	private NetBuilding(NetBuilding building, boolean newDome) {
		level = building.getLevel();
		dome = newDome;
	}

	/* **********************************************
	 *												*
	 *		MODIFIERS FOR USER IMMUTABLE OBJECT		*
	 * 												*
	 ************************************************/
	/**
	 * Creates a new building from the old one modifying the level attribute.
	 * @param level new level
	 * @return a modified {@code NetBuilding}
	 */
	public NetBuilding setLevel(int level) {
		return new NetBuilding(this,level);
	}
	/**
	 * Creates a new building from the old one modifying the dome attribute.
	 * @param dome new dome attribute
	 * @return a modified {@code NetBuilding}
	 */
	public NetBuilding setDome(boolean dome) {
		return new NetBuilding(this,dome);
	}

	/* **********************************************
	 *												*
	 * GETTERS AND METHODS WHICH DON'T CHANGE STATE	*
	 * 												*
	 ************************************************/
	/**
	 * Gets the parameter {@link #level}.
	 * @return value of {@link #level}
	 */
	public int getLevel() {
		return level;
	}
	/**
	 * Gets the parameter {@link #dome}.
	 * @return value of {@link #dome}
	 */
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
