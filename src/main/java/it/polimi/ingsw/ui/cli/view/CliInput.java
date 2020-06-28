package it.polimi.ingsw.ui.cli.view;

import it.polimi.ingsw.util.exceptions.UserInputTimeoutException;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * This class is a class used to get the input of the client with a interruptible style which permits to the application to be more responsive when a server message arrives and to reduce unwanted graphical bugs.
 */
public class CliInput {
	private int timePassed;
	private final int timeout = 1000;
	private final int timeoutUndo = 5000;
	private boolean timeoutActive;
	private final Object timeoutLock;

	/**
	 * It creates a Cli input getter without a timeout active.
	 */
	public CliInput() {
		timePassed = 0;
		timeoutActive = false;
		timeoutLock = new Object();
	}

	// setters
	/**
	 * It sets the timeout.
	 */
	public void setTimeout() {
		synchronized (timeoutLock) {
			timeoutActive = true;
		}
	}

	//eventually setters and getters; constructor
	/**
	 * This method gets the input of the user when it writes it and enter \n
	 * @return the command inserted by the user
	 * @throws UserInputTimeoutException if the user hasn't written something when there is a server message to read
	 * @throws IOException if there has been an error to access stdin
	 */
	public Command getInput() throws UserInputTimeoutException, IOException {
		String userInput = null;
		boolean foundSomething = false;
		resetInsertedInput();
		dropTimeout();

		ExecutorService myService = Executors.newSingleThreadExecutor();
		Future<String> executingThread = myService.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				Scanner reader = new Scanner(System.in);
				return reader.nextLine();
			}
		});

		// waits until the user hasn't wrote something
		while (timePassed < timeout && !foundSomething) {
			if (executingThread.isDone()) {
				try {
					userInput = executingThread.get();
					foundSomething = true;
				} catch (InterruptedException e) {
					throw new AssertionError("Someone interrupted the executing thread");
				} catch (ExecutionException e) {
					throw new IOException();
				}
			} else {
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
			}
		}

		if (userInput == null) {
			// if there was a timeout it throws an exception
			myService.shutdownNow();
			dropTimeout();
			System.out.print("\n");
			throw new UserInputTimeoutException();
		} else {
			// it resets the time passed for the next input call
			dropTimeout();
			return new Command(userInput.split(" "));
		}
	}

	/**
	 * This method gets the user undo it writes it and enter \n
	 * @return the command inserted by the user
	 * @throws IOException if there has been an error to access stdin
	 */
	public boolean getUndo() throws IOException {
		String userInput = null;
		boolean foundSomething = false;
		resetInsertedInput();

		ExecutorService myService = Executors.newSingleThreadExecutor();
		Future<String> executingThread = myService.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				Scanner reader = new Scanner(System.in);
				return reader.nextLine();
			}
		});

		// waits until the user hasn't wrote something
		while (timePassed < timeoutUndo && !foundSomething) {
			if (executingThread.isDone()) {
				try {
					userInput = executingThread.get();
					foundSomething = true;
				} catch (InterruptedException e) {
					throw new AssertionError("Someone interrupted the executing thread");
				} catch (ExecutionException e) {
					throw new IOException();
				}
			} else {
				try {
					timePassed += 200;
					Thread.sleep(200);
				} catch (InterruptedException e) {
					throw new AssertionError("Someone interrupted the executing thread");
				}
			}
		}

		dropTimeout();
		if (userInput == null) {
			// if there was a timeout it throws an exception
			System.out.print("\n");
			myService.shutdownNow();
			return false;
		} else {
			// it resets the time passed for the next input call
			if((new Command(userInput.split(" "))).commandType.toUpperCase().equals("UNDO")) {
				return true;
			} else {
				return false;
			}
		}
	}

	// support methods
	/**
	 * It deletes the current timeout active for this input getter.
	 */
	private void dropTimeout() {
		synchronized (timeoutLock) {
			timePassed = 0;
			timeoutActive = false;
		}
	}
	/**
	 * It deletes every input sent by the user to the Cli when he shouldn't do it.
	 * @throws IOException if there has been an error to access stdin
	 */
	private void resetInsertedInput() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while (reader.ready()) {
			// TODO: maybe the last line isn't deleted
			reader.readLine();
		}
	}
}
