package it.polimi.ingsw.util.exceptions;

/**
 * The {@code NoBuildException} is an exception which says that a certain god is god which power does not provide any build action for the specified game phase.
 */
public class NoBuildException extends Exception {
	/**
	 * It creates and empty {@code NoBuildException}.
	 */
	public NoBuildException() {super();}
	/**
	 * It creates a {@code NoBuildException} with msg message.
	 * @param msg the message of the exception
	 */
	public NoBuildException(String msg) {super(msg);}
}
