package it.polimi.ingsw.controller;

import it.polimi.ingsw.core.Build;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.network.game.NetBuild;

import java.net.Inet4Address;
import java.util.List;

public class Builder {
	private Game observedModel;
	private NetBuild netBuild;

	// constructor for this class
	public Builder(Game g) {
		observedModel = g;
	}

	/**
	 *
	 * @param netBuild the build communicated to the server.
	 * @param possibilities the list of possible builds the player could do.
	 * @return true if the netBuild is contained in the possibilities list and call applyBuild in Game class; else return false.
	 */
	public boolean Build(NetBuild netBuild, List<Build> possibilities) {
		boolean value = false;
		for(Build b : possibilities){
			if(netBuild.workerID == b.worker.workerID && netBuild.dome == b.dome && netBuild.cellX == b.cell.map.getX(b.cell) && netBuild.cellY == b.cell.map.getY(b.cell) && netBuild.level == b.cell.building.getLevel() && netBuild.other.equals(b.getOther()) ) {
				observedModel.applyBuild(b);
				value = true;
			} else {
				value = false;
			}
		}
		return value;
	}

}
