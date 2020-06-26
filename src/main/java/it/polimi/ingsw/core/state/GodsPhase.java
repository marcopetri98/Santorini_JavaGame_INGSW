package it.polimi.ingsw.core.state;

/**
 * This enum represent the phase of a god phase, a god phase is the phase where the challenger chooses the gods for the game, players chooses the god and the challenger choose the starter. This enum contains this information.
 */
public enum GodsPhase {
	CHALLENGER_CHOICE, GODS_CHOICE, STARTER_CHOICE;

	/**
	 * This methods advance to the immediately after {@link it.polimi.ingsw.core.state.GodsPhase}.
	 * @param ph a {@link it.polimi.ingsw.core.state.GodsPhase}
	 * @return the {@link it.polimi.ingsw.core.state.GodsPhase} after the {@code ph}
	 * @throws IllegalStateException is {@code ph} is a {@code STARTER_CHOICE}
	 */
	public GodsPhase advance(GodsPhase ph) throws IllegalStateException {
		switch (ph) {
			case CHALLENGER_CHOICE:
				return GODS_CHOICE;

			case GODS_CHOICE:
				return STARTER_CHOICE;

			default:
				throw new IllegalStateException();
		}
	}
}
