package it.polimi.ingsw.core;

public class Building {
	private int level;
	private boolean dome;

	public Building() {
		level = 0;
		dome = false;
	}
	private Building(int level, boolean dome) {
		this.level = level;
		this.dome = dome;
	}

	// SETTERS AND CHANGERS
	void incrementLevel() throws IllegalStateException {
		if (level <= 2) {
			level++;
		} else {
			throw new IllegalStateException();
		}
	}
	void setDome() {
		if (!dome) {
			dome = true;
		} else {
			throw new IllegalStateException();
		}
	}

	// GETTERS OF THE CLASS
	public int getLevel() {
		return level;
	}
	public boolean getDome(){ return dome; }

	// OVERRIDDEN METHODS
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Building) {
			Building other = (Building)obj;
			return level == other.getLevel() && dome == other.getDome();
		}
		return false;
	}
	@Override
	public Building clone() {
		return new Building(level,dome);
	}
}