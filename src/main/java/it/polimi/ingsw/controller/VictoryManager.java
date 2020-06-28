package it.polimi.ingsw.controller;

import it.polimi.ingsw.core.Cell;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.core.Move;
import it.polimi.ingsw.core.TypeMove;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the controller part dedicated to checking the victory of any player
 */
public class VictoryManager {
	private Game observedModel;

	// CONSTRUCTOR

	/**
	 * This is the constructor of the class
	 * @param g the {@link Game}
	 */
	public VictoryManager(Game g) {
		observedModel = g;
	}


	/**
	 * The method checks if the standard condition of winning is respected: if it is respected, it calls applyWin function in Game class.
	 * Also, it checks if the move is a VICTORY_MOVE: if it is true, it calls applyWin function in Game class.
	 * @param before the origin cell of the worker.
	 * @param after the cell where the worker is moving to.
	 * @param possibleCells the list of possible moves returned by the God's checkMove function.
	 * @throws NullPointerException if the parameter is null
	 */
	public void checkVictory(Cell before, Cell after, List<Move> possibleCells) throws NullPointerException {
		if (before == null || after == null || possibleCells == null) {
			throw new NullPointerException();
		}

		for(Move m: possibleCells){
			if (m.prev.equals(before) && m.next.equals(after) && m.next.building.getLevel() == 3 && before.getBuilding().getLevel() != 3) { //check the standard condition of winning: a Player's worker moved up (only 1 level) to a building level 3 during his own turn.
				observedModel.applyWin(observedModel.getPlayerTurn());
			} else if(m.typeMove == TypeMove.VICTORY_MOVE && m.prev.equals(before) && m.next.equals(after)){ //the move from "before" to "after" is a VICTORY_MOVE
				observedModel.applyWin(observedModel.getPlayerTurn());
			}
		}
	}

}
