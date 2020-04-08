package it.polimi.ingsw.util.exceptions;

public class AlreadyStartedException extends Exception {
	private String message;

	public AlreadyStartedException() {
		super();
	}
	public AlreadyStartedException(String msg) {
		message = msg;
	}

	@Override
	public String getMessage() {
		return message;
	}
}

