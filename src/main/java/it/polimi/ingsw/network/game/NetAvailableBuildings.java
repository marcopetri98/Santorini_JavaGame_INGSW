package it.polimi.ingsw.network.game;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.Build;
import it.polimi.ingsw.core.Move;

// necessary imports of Java SE
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to send to the user the positions where a specified worker can build.
 */
public class NetAvailableBuildings implements Serializable {
	public final List<NetBuild> builds;

	/**
	 * Creates an object with the possible builds.
	 * @param possibleBuilds is the list of possible builds
	 */
	public NetAvailableBuildings(List<Build> possibleBuilds) {
		builds = new ArrayList<>();
		for (Build b : possibleBuilds) {
			builds.add(new NetBuild(b));
		}
	}
}
