package it.polimi.ingsw.util.exceptions;

/**
 * The {@code NoMoveException} is an exception which says that a certain god is god which power does not provide any move action for the specified game phase.
 */
public class NoMoveException extends Exception {
	/**
	 * It creates and empty {@code NoMoveException}.
	 */
	public NoMoveException() {super();}
	/**
	 * It creates a {@code NoMoveException} with msg message.
	 * @param msg the message of the exception
	 */
	public NoMoveException(String msg) {super(msg);}
}
