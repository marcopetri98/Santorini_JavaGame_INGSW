package it.polimi.ingsw.util.exceptions;

/**
 * The {@code BadRequestException} is an exception used by the package controller which has the information that the request sent by the user can't be completed for different reasons, it is possible that is not well formed as well as it is possible that it is impossible with the current game state.
 */
public class BadRequestException extends Exception {
	/**
	 * It creates and empty {@code BadRequestException}.
	 */
	public BadRequestException() {
		super();
	}
	/**
	 * It creates a {@code BadRequestException} with msg message.
	 * @param msg the message of the exception
	 */
	public BadRequestException(String msg) {
		super(msg);
	}
}
