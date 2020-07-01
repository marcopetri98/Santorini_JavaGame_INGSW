package it.polimi.ingsw.util.exceptions;

/**
 * The {@code WrongPhaseException} is an exception thrown by model (classes inside {@link it.polimi.ingsw.core} package) methods to say that this specific method cannot be called in such game phase.
 */
public class WrongPhaseException extends Exception {
	/**
	 * It creates and empty {@code WrongPhaseException}.
	 */
	public WrongPhaseException() {
		super();
	}
	/**
	 * It creates a {@code WrongPhaseException} with msg message.
	 * @param msg the message of the exception
	 */
	public WrongPhaseException(String msg) {
		super(msg);
	}
}
