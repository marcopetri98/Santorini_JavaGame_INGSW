package it.polimi.ingsw.controller;

import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.core.Move;
import it.polimi.ingsw.core.TypeMove;
import it.polimi.ingsw.network.game.NetMove;

import java.util.ArrayList;
import java.util.List;

public class Mover {
	private Game observedModel;

	// constructor for this class
	public Mover(Game g) {
		observedModel = g;
	}


	/**
	 * The function checks if the NetMove returned by the client is indeed possible, and if it is, it applies it
	 * @param netmove the move returned by the client
	 * @param possibilities the possible moves of the active player
	 * @return true if the NetMove is contained in the possibilities list
	 * @throws NullPointerException if the parameter is null
	 */
	public boolean move(NetMove netmove, List<Move> possibilities) throws NullPointerException {
		if (netmove == null || possibilities == null) {
			throw new NullPointerException();
		}

		for(Move move : possibilities){
			if(move.isSameAs(netmove)) {
				if(move.typeMove == TypeMove.SIMPLE_MOVE || move.typeMove == TypeMove.CONDITIONED_MOVE || move.typeMove == TypeMove.VICTORY_MOVE){
					observedModel.applyMove(move);
					return true;
				}
			}
		}
		return false;
	}

	private List<Move> filterMoves(List<Move> possibilities) {
		List<Move> newList = new ArrayList<>(possibilities);
		for (int i = 0; i < possibilities.size(); i++) {
			for (int j = 0; j < possibilities.size(); j++) {
				if (possibilities.get(i).typeMove == TypeMove.FORBIDDEN_MOVE && possibilities.get(j).typeMove == TypeMove.VICTORY_MOVE && possibilities.get(i).getCellNext() == possibilities.get(j).getCellNext() && possibilities.get(i).getCellPrev() == possibilities.get(j).getCellPrev() && possibilities.get(i).getWorker() == possibilities.get(j).getWorker()) {
					if (newList.contains(possibilities.get(i))) {
						newList.remove(possibilities.get(i));
					}
				}
			}
		}
		return newList;
	}
}
