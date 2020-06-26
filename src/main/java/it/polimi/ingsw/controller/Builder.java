package it.polimi.ingsw.controller;

import it.polimi.ingsw.core.Build;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.network.game.NetBuild;

import java.net.Inet4Address;
import java.util.List;

public class Builder {
	private Game observedModel;

	// constructor for this class
	public Builder(Game g) {
		observedModel = g;
	}

	/**
	 *
	 * @param netBuild the build communicated to the server.
	 * @param possibilities the list of possible builds the player could do.
	 * @return true if the netBuild is contained in the possibilities list and call applyBuild in Game class; else return false.
	 * @throws NullPointerException if the parameter is null
	 */
	public boolean build(NetBuild netBuild, List<Build> possibilities) throws NullPointerException {
		if (netBuild == null || possibilities == null) {
			throw new NullPointerException();
		}

		for(Build b : possibilities){
			if (b.isSameAs(netBuild)) {
				observedModel.applyBuild(b);
				return true;
			}
		}
		return false;
	}
}