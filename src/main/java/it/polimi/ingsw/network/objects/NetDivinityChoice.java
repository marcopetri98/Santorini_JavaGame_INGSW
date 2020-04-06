package it.polimi.ingsw.network.objects;

// necessary imports from other packages of the project
import it.polimi.ingsw.util.Constants;

// necessary imports of Java SE
import java.io.Serializable;

public class NetDivinityChoice implements Serializable {
	public static int serialUID = Constants.ACTUAL_VERSION;
	private String message;
	private String divinity;
}
