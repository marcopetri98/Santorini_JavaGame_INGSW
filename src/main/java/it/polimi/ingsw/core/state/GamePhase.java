package it.polimi.ingsw.core.state;

public enum GamePhase {
	BEFOREMOVE, MOVE, BEFOREBUILD, BUILD, END;

	public GamePhase advance(GamePhase ph) throws IllegalStateException {
		switch (ph) {
			case BEFOREMOVE:
				return MOVE;

			case MOVE:
				return BEFOREBUILD;

			case BEFOREBUILD:
				return BUILD;

			case BUILD:
				return END;

			default:
				return BEFOREMOVE;
		}
	}
}
