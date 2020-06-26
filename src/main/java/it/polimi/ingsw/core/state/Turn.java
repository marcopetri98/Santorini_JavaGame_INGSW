package it.polimi.ingsw.core.state;

/**
 * This class represent the phase and the turn of the game, it has a phase and for some of them a subphase indicating how the game is going.
 */
public class Turn implements Cloneable {
	private Phase phase;
	private GamePhase gamePhase;
	private GodsPhase godsPhase;

	/**
	 * Creates a turn in the start phase, the lobby one.
	 */
	public Turn() {
		phase = Phase.LOBBY;
		gamePhase = GamePhase.BEFOREMOVE;
		godsPhase = GodsPhase.CHALLENGER_CHOICE;
	}
	/**
	 * Creates a game with the given phase
	 * @param p a {@link it.polimi.ingsw.core.state.Phase}
	 * @param game a {@link it.polimi.ingsw.core.state.GamePhase}
	 * @param gods a {@link it.polimi.ingsw.core.state.GodsPhase}
	 */
	private Turn(Phase p, GamePhase game, GodsPhase gods) {
		phase = p;
		gamePhase = game;
		godsPhase = gods;
	}

	// modifiers
	/**
	 * It advanced the game to the next phase of the game
	 */
	public void advance() {
		if (phase != Phase.PLAYERTURN) {
			if (phase != Phase.GODS) {
				phase = phase.advance(phase);
			} else {
				try {
					godsPhase = godsPhase.advance(godsPhase);
				} catch (IllegalStateException e) {
					phase = phase.advance(phase);
				}
			}
		} else {
			gamePhase = gamePhase.advance(gamePhase);
		}
	}

	// getters
	/**
	 * Gets the current {@link it.polimi.ingsw.core.state.Phase}
	 * @return current {@link it.polimi.ingsw.core.state.Phase}
	 */
	public Phase getPhase() {
		return phase;
	}
	/**
	 * Gets the current {@link it.polimi.ingsw.core.state.GamePhase}
	 * @return current {@link it.polimi.ingsw.core.state.GamePhase}
	 */
	public GamePhase getGamePhase() {
		return gamePhase;
	}
	/**
	 * Gets the current {@link it.polimi.ingsw.core.state.GodsPhase}
	 * @return current {@link it.polimi.ingsw.core.state.GodsPhase}
	 */
	public GodsPhase getGodsPhase() {
		return godsPhase;
	}

	// methods overriding
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Turn) {
			Turn other = (Turn)obj;
			return phase == other.phase && gamePhase == other.gamePhase && godsPhase == other.godsPhase;
		}
		return false;
	}
	@Override
	public Turn clone() {
		return new Turn(phase,gamePhase,godsPhase);
	}
}
