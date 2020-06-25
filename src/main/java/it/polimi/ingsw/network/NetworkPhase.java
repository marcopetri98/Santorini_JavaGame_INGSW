package it.polimi.ingsw.network;

/**
 * This class is the class which has the information about the network phase, these are similar to the game phase except of the OTHERTURN and OBSERVER phases because the first one is a normal gaming phase when the client can only disconnect and the observer when it can only disconnect and it has finished to play.
 */
public enum NetworkPhase {
	PRELOBBY, LOBBY, COLORS, GODS, SETUP, PLAYERTURN, OTHERTURN, OBSERVER, END
}
