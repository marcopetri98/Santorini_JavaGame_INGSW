package it.polimi.ingsw.core.state;

public enum GamePhase {
	BEFOREMOVE, MOVE, BUILD;

	public GamePhase advance(GamePhase ph) throws IllegalStateException {
		switch (ph) {
			case BEFOREMOVE:
				return MOVE;

			case MOVE:
				return BUILD;

			default:
				return BEFOREMOVE;
		}
	}
}
