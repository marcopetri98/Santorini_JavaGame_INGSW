package it.polimi.ingsw.core.state;

public enum Phase {
	PRELOBBY, LOBBY, COLORS, GODS, SETUP, PLAYERTURN;

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
