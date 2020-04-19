package it.polimi.ingsw.network.game;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.Build;

// necessary imports of Java SE
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to send to the user the positions where a specified worker can build
 */
public class NetAvailableBuildings {
	public final List<NetBuild> builds;

	public NetAvailableBuildings(List<Build> possibleBuilds) {
		builds = new ArrayList<>();
		for (Build b : possibleBuilds) {
			builds.add(new NetBuild(b));
		}
	}
}
