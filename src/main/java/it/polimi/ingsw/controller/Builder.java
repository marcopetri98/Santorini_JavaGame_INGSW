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
	public boolean build(NetBuild netBuild, List<Build> possibilities) {
		boolean value = false;
		for(Build b : possibilities){
			if(netBuild.workerID == b.worker.workerID && netBuild.dome == b.dome && netBuild.cellX == observedModel.getMap().getX(b.cell) && netBuild.cellY == observedModel.getMap().getY(b.cell) && netBuild.level == b.cell.building.getLevel() && netBuild.other != null && netBuild.other.workerID == b.getOther().worker.workerID && netBuild.other.dome == b.getOther().dome && netBuild.other.cellX == observedModel.getMap().getX(b.getOther().cell) && netBuild.other.cellY == observedModel.getMap().getY(b.getOther().cell) && netBuild.other.level == b.getOther().cell.building.getLevel() ) {
				observedModel.applyBuild(b);
				value = true;
			} else {
				value = false;
			}
		}
		return value;
	}
}