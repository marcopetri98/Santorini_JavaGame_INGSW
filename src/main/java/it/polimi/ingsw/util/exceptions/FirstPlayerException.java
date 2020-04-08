package it.polimi.ingsw.util.exceptions;

public class FirstPlayerException extends Exception {
	private String message;

	public FirstPlayerException() {
		super();
	}
	public FirstPlayerException(String msg) {
		message = msg;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
