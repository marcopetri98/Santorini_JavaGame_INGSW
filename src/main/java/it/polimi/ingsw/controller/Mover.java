package it.polimi.ingsw.controller;

import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.core.Move;
import it.polimi.ingsw.core.TypeMove;
import it.polimi.ingsw.network.game.NetMove;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the controller part dedicated to applying the moves
 */
public class Mover {
	private Game observedModel;

	// CONSTRUCTOR

	/**
	 * This is the constructor of the class
	 * @param g the {@link Game}
	 */
	public Mover(Game g) {
		observedModel = g;
	}

	/**
	 * The method checks if the {@link NetMove} returned by the client is indeed possible, and if it is, it applies it
	 * @param netmove the move returned by the client
	 * @param possibilities the possible moves of the active player
	 * @return true if the NetMove is contained in the possibilities list
	 * @throws NullPointerException if the parameter is null
	 */
	public boolean move(NetMove netmove, List<Move> possibilities) throws NullPointerException {
		if (netmove == null || possibilities == null) {
			throw new NullPointerException();
		}
		List<Move> filteredMoves = DefeatManager.filterMoves(possibilities);

		for(Move move : filteredMoves) {
			if(move.isSameAs(netmove)) {
				if(move.typeMove == TypeMove.SIMPLE_MOVE || move.typeMove == TypeMove.CONDITIONED_MOVE || move.typeMove == TypeMove.VICTORY_MOVE){
					observedModel.applyWorkerLock(move.worker.owner,move.worker.workerID-move.worker.owner.getPlayerID());
					observedModel.applyMove(move);
					return true;
				}
			}
		}
		return false;
	}
}
