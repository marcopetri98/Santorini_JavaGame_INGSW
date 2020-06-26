package it.polimi.ingsw.util.exceptions;

/**
 * The {@code FirstPlayerException} is an exception used by the CLI version of the game in getting input phase, this exception says that a message arrived from the server and the user hasn't wrote commands within a certain amount of time given to it.
 */
public class UserInputTimeoutException extends Exception {
	/**
	 * It creates and empty {@code UserInputTimeoutException}.
	 */
	public UserInputTimeoutException() {
		super();
	}
	/**
	 * It creates a {@code UserInputTimeoutException} with msg message.
	 */
	public UserInputTimeoutException(String msg) {
		super(msg);
	}
}
