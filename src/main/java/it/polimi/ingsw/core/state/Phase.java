package it.polimi.ingsw.core.state;

/**
 * This enum has the information about the game phase of the match that is being played by Santorini's video gamers.
 */
public enum Phase {
	PRELOBBY, LOBBY, COLORS, GODS, SETUP, PLAYERTURN;

	/**
	 * This methods advance to the immediately after {@link it.polimi.ingsw.core.state.Phase}.
	 * @param ph a {@link it.polimi.ingsw.core.state.Phase}
	 * @return the {@link it.polimi.ingsw.core.state.Phase} after the {@code ph}
	 * @throws IllegalStateException is {@code ph} is a {@code PLAYERTURN}
	 */
	public Phase advance(Phase ph) throws IllegalStateException {
		switch (ph) {
			case PRELOBBY:
				return LOBBY;

			case LOBBY:
				return COLORS;

			case COLORS:
				return GODS;

			case GODS:
				return SETUP;

			case SETUP:
				return PLAYERTURN;

			default:
				throw new IllegalStateException();
		}
	}

	/**
	 * This method returns true if {@code other} is a phase which comes before the this phase
	 * @param other a {@link it.polimi.ingsw.core.state.Phase}
	 * @return true if {@code other} is a phase which comes before the this phase
	 */
	public boolean lessThan(Phase other) {
		if (this == PRELOBBY && (other.equals(LOBBY) || other.equals(COLORS) || other.equals(GODS) || other.equals(SETUP) || other.equals(PLAYERTURN))) {
			return true;
		} else if (this == LOBBY && (other.equals(COLORS) || other.equals(GODS) || other.equals(SETUP) || other.equals(PLAYERTURN))) {
			return true;
		} else if (this == COLORS && (other.equals(GODS) || other.equals(SETUP) || other.equals(PLAYERTURN))) {
			return true;
		} else if (this == GODS && (other.equals(SETUP) || other.equals(PLAYERTURN))) {
			return true;
		} else if (this == SETUP && (other.equals(PLAYERTURN))) {
			return true;
		} else {
			return false;
		}
	}
}
