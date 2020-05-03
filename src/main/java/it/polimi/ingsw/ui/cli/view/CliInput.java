package it.polimi.ingsw.ui.cli.view;

import java.util.Scanner;

public class CliInput {
	private int timeout;
	private boolean timeoutActive;

	public CliInput() {
		timeout = 0;
		timeoutActive = false;
	}

	// setters
	public void setTimeout() {
		timeout = 1000;
		timeoutActive = true;
	}
	public void dropTimeout() {
		timeout = 0;
		timeoutActive = false;
	}

	//eventually setters and getters; constructor
	public Command getInput(){
		//TODO: implement the buffered way of retrieving input
		Scanner reader = new Scanner(System.in);
		String input = reader.nextLine();
		String[] words = input.split(" ");
		return new Command(words);
	}
}
