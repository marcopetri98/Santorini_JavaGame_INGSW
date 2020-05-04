package it.polimi.ingsw.ui.cli.view;

import it.polimi.ingsw.util.exceptions.UserInputTimeoutException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CliInput {
	private int timePassed;
	private final int timeout = 1000;
	private boolean timeoutActive;
	private final Object timeoutLock;

	public CliInput() {
		timePassed = 0;
		timeoutActive = false;
		timeoutLock = new Object();
	}

	// setters
	public void setTimeout() {
		synchronized (timeoutLock) {
			timeoutActive = true;
		}
	}

	//eventually setters and getters; constructor
	/**
	 * It resets the input inserted when the used shouldn't write on the cli and gets the next input without interrupting the thread forever
	 * @return
	 */
	public Command getInput() throws UserInputTimeoutException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String userInput = null;
		boolean foundSomething = false;
		resetInsertedInput();

		// waits until the user hasn't wrote something
		while (timePassed < 1000 && !foundSomething) {
			if (!reader.ready()) {
				try {
					synchronized (timeoutLock) {
						if (timeoutActive) {
							timePassed += 500;
						}
					}
					Thread.sleep(500);
				} catch (InterruptedException e) {
					throw new AssertionError("Someone interrupted the executing thread");
				}
			} else {
				userInput = reader.readLine();
				foundSomething = true;
			}
		}

		if (userInput == null) {
			// if there was a timeout it throws an exception
			dropTimeout();
			System.out.print("\n");
			throw new UserInputTimeoutException();
		} else {
			// it resets the time passed for the next input call
			dropTimeout();
			return new Command(userInput.split(" "));
		}
	}

	// support methods
	private void dropTimeout() {
		synchronized (timeoutLock) {
			timePassed = 0;
			timeoutActive = false;
		}
	}
	private void resetInsertedInput() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while (reader.ready()) {
			// TODO: maybe the last line isn't deleted
			reader.readLine();
		}
	}
}
