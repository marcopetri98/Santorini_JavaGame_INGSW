package it.polimi.ingsw.core;

public class Building {
	private int level;
	private boolean dome;

	public Building() {
		level = 0;
		dome = false;
	}

	// SETTERS AND CHANGERS
	public void incrementLevel() {
		if (level <= 2) {
			level++;
		} else {
			// TODO: add an exception here
		}
	}
	public void setDome() {
		if (!dome) {
			dome = true;
		} else {
			// TODO: add an exception here
		}
	}

	// GETTERS OF THE CLASS
	public int getLevel() {
		return level;
	}
	public boolean getDome(){ return dome; }
}