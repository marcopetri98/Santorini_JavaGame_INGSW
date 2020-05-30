package it.polimi.ingsw.network.objects;

// necessary imports from other packages of the project
import it.polimi.ingsw.util.Constants;

// necessary imports of Java SE
import java.io.Serializable;

public class NetObject implements Serializable {
	public static final int serialUID = Constants.ACTUAL_VERSION;
	public final String message;

	public NetObject(String msg) throws NullPointerException {
		if (msg == null) {
			throw new NullPointerException();
		}
		message = msg;
	}
}
