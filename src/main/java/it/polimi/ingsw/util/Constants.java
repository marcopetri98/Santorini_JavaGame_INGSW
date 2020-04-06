package it.polimi.ingsw.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class Constants {
	/**
	 * A Constants class shouldn't be created, it's only a class used to store variables
	 */
	private Constants() {}

	/* Setup messages from client to server */
	public static final String SETUP_IN_PARTECIPATE = "setup-partecipate";
	public static final String SETUP_OUT_CONNWORKED = "setup-connection-worked";
	public static final String SETUP_OUT_CONNFAILED = "setup-connection-failed";
	public static final String SETUP_OUT_CONNFINISH = "setup-connection-finished";

	/* Game preparation messages */
	public static final String PREP_TURN = "preparation-info";
	public static final String PREP_FIRST = "preparation-first";
	public static final String PREP_COLOR_YOU = "preparation-color-go";
	public static final String PREP_COLOR_OTHER = "preparation-color-wait";
	public static final String PREP_COLOR_ERROR = "preparation-color-error";

	/* General connection messages */
	public static final String CHECK = "ping";

	/* Functions useful for all program */
	public static boolean verifyConnected(Socket socket) {
		try {
			PrintWriter printer = new PrintWriter(socket.getOutputStream());
			printer.println(Constants.CHECK);
			printer.flush();
			Thread.sleep(100);
			Scanner reader = new Scanner(socket.getInputStream());
			reader.nextLine();
			return true;
		} catch (NoSuchElementException | IOException | InterruptedException e) {
			return false;
		}
	}
}
