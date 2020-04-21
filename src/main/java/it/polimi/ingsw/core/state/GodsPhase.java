package it.polimi.ingsw.core.state;

public enum GodsPhase {
	CHALLENGER_CHOICE, GODS_CHOICE, STARTER_CHOICE;

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
