package it.polimi.ingsw.util.exceptions;

/**
 * The {@code AlreadyStartedException} is an exception that indicates that the game is already started when a {@link it.polimi.ingsw.network.ServerClientListenerThread} tries to perform modification actions on the lobby.
 */
public class AlreadyStartedException extends Exception {
	/**
	 * It creates and empty {@code AlreadyStartedException}.
	 */
	public AlreadyStartedException() {
		super();
	}
	/**
	 * It creates an {@code AlreadyStartedException} with msg message.
	 */
	public AlreadyStartedException(String msg) {
		super(msg);
	}
}

