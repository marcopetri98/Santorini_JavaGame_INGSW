package it.polimi.ingsw.network.objects;

// necessary imports from other packages of the project
import it.polimi.ingsw.util.Constants;

// necessary imports of Java SE
import java.io.Serializable;

/**
 * This class is the base class for every message exchange between a Santorini server and a Santorini client for the video game version, it implements java {@code Serializable} interface and is used to send a generic information, in fact it only carries the information about the message sent and the current version of the game.
 */
public class NetObject implements Serializable {
	public static final int serialUID = Constants.ACTUAL_VERSION;
	public final String message;

	/**
	 * It creates a {@code NetObject} with the specified message in the parameter.
	 * @param msg is the message to be sent
	 * @throws NullPointerException if {@code msg} is null
	 */
	public NetObject(String msg) throws NullPointerException {
		if (msg == null) {
			throw new NullPointerException();
		}
		message = msg;
	}
}
