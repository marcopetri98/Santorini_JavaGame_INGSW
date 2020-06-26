package it.polimi.ingsw.core.state;

/**
 * This enum represent the phase of the gaming phase. The gaming phase is the phase where player can build and move, this enum is subdivided in the stages that can be used by the gods of the game (some ones have the possibility to perform actions before a move)
 */
public enum GamePhase {
	BEFOREMOVE, MOVE, BUILD;

	/**
	 * This methods advance to the immediately after {@link it.polimi.ingsw.core.state.GamePhase}.
	 * @param ph a {@link it.polimi.ingsw.core.state.GamePhase}
	 * @return the {@link it.polimi.ingsw.core.state.GamePhase} after the {@code ph}
	 * @throws NullPointerException if {@code ph} is null
	 */
	public GamePhase advance(GamePhase ph) throws NullPointerException {
		if (ph == null) {
			throw new NullPointerException();
		}
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
