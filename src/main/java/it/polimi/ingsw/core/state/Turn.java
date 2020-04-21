package it.polimi.ingsw.core.state;

public class Turn implements Cloneable {
	private Phase phase;
	private GamePhase gamePhase;
	private GodsPhase godsPhase;

	public Turn() {
		phase = Phase.LOBBY;
		gamePhase = GamePhase.BEFOREMOVE;
		godsPhase = GodsPhase.CHALLENGER_CHOICE;
	}
	private Turn(Phase p, GamePhase game, GodsPhase gods) {
		phase = p;
		gamePhase = game;
		godsPhase = gods;
	}

	// modifiers
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
	public Phase getPhase() {
		return phase;
	}
	public GamePhase getGamePhase() {
		return gamePhase;
	}
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
