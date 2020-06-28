package it.polimi.ingsw.core;

/**
 * This is the building class, which stores the information about the building on the map, for each cell
 */
public class Building {
	private int level;
	private boolean dome;

	/**
	 * Empty constructor of the class
	 */
	public Building() {
		level = 0;
		dome = false;
	}

	/**
	 * Constructor of the class
	 * @param level the level of the building
	 * @param dome true if a dome is present
	 */
	private Building(int level, boolean dome) {
		this.level = level;
		this.dome = dome;
	}

	// SETTERS AND CHANGERS

	/**
	 * Method used to increment the height of a building (from 0 to 3 in steps of 1)
	 * @throws IllegalStateException
	 */
	void incrementLevel() throws IllegalStateException {
		if (level <= 2) {
			level++;
		} else {
			throw new IllegalStateException();
		}
	}

	/**
	 * Method used to set a dome
	 * @throws IllegalStateException if there was already a dome
	 */
	void setDome() throws IllegalStateException{
		if (!dome) {
			dome = true;
		} else {
			throw new IllegalStateException();
		}
	}

	// GETTERS OF THE CLASS

	/**
	 * Getter of the {@code level} of the {@link Building}
	 * @return the {@code level} of this {@link Building}
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Getter of the {@code dome}
	 * @return true if a {@code dome} is present
	 */
	public boolean getDome(){ return dome; }

	// OVERRIDDEN METHODS

	/**
	 * Overridden equals method
	 * @param obj the object to check
	 * @return true if they are the same
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Building) {
			Building other = (Building)obj;
			return level == other.getLevel() && dome == other.getDome();
		}
		return false;
	}

	/**
	 * Overridden clone method
	 * @return the newly cloned {@link Building}
	 */
	@Override
	public Building clone() {
		return new Building(level,dome);
	}
}