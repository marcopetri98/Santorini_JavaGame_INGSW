package it.polimi.ingsw.core;

public class Building {
	private int level;
	private boolean dome;

	public Building() {
		level = 0;
		dome = false;
	}

	// SETTERS AND CHANGERS
	public void incrementLevel() throws IllegalStateException {
		if (level <= 2) {
			level++;
		} else {
			throw new IllegalStateException();
		}
	}
	public void setDome() throws IllegalStateException {
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
			return level == other.level && dome == other.dome;
		}
		return false;
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}