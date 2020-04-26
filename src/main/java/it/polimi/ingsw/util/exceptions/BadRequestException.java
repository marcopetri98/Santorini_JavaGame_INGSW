package it.polimi.ingsw.util.exceptions;

public class BadRequestException extends Exception {
	public BadRequestException() {
		super();
	}
	public BadRequestException(String msg) {
		super(msg);
	}
}
