package it.polimi.ingsw.util.exceptions;

/**
 * The {@code FirstPlayerException} is an exception that indicates an exceptional flow of the server threads, the player that joined a lobby is the first player and for this reason, he must choose the number of players for this lobby.
 */
public class FirstPlayerException extends Exception {
	/**
	 * It creates and empty {@code FirstPlayerException}.
	 */
	public FirstPlayerException() {
		super();
	}
	/**
	 * It creates a {@code FirstPlayerException} with msg message.
	 */
	public FirstPlayerException(String msg) {
		super(msg);
	}
}
