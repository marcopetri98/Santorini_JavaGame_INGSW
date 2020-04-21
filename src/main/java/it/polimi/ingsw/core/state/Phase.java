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
}
