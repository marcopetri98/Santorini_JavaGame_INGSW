package it.polimi.ingsw.util.exceptions;

public class UserInputTimeoutException extends Exception {
	public UserInputTimeoutException() {
		super();
	}

	public UserInputTimeoutException(String msg) {
		super(msg);
	}
}
