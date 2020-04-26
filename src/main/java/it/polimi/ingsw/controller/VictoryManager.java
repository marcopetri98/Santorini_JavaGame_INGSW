package it.polimi.ingsw.controller;

import it.polimi.ingsw.core.Cell;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.core.Move;
import it.polimi.ingsw.core.TypeMove;

import java.util.ArrayList;
import java.util.List;

public class VictoryManager {
	private Game observedModel;

	// constructor for this class
	public VictoryManager(Game g) {
		observedModel = g;
	}

	//Controllare che after è dentro lista move(nb non lìha messo, si è dimenticata..), se no controllo condizione standard (adiacente etc..) e chiama applywin in game (da aggiungere... mette il stringa playerwinner (che inizio è NULL) a true e fo notifywinner passando la stringa del player che ha vinto).

//	/**
//	 *
//	 * @param possibleCells the list of possible moves returned by the God's checkMove function.
//	 * @return a list containing only winning moves due to God's power.
//	 */
//	private List<Move> filterMoves(List<Move> possibleCells) {
//		List<Move> moves = new ArrayList<>();
//		for (Move m : possibleCells) {
//			if (m.typeMove == TypeMove.VICTORY_MOVE) { //due to winning condition of a God's power.
//				moves.add(m);
//			}
//		}
//		return moves;
//	}

	/**
	 * The function checks if the standard condition of winning is respected: if it is respected, it calls applyWin function in Game class.
	 * Also, it checks if the move is a VICTORY_MOVE: if it is true, it calls applyWin function in Game class.
	 * @param before the origin cell of the worker.
	 * @param after the cell where the worker is moving to.
	 * @param possibleCells the list of possible moves returned by the God's checkMove function.
	 */
	public void checkVictory(Cell before, Cell after, List<Move> possibleCells){
		for(Move m: possibleCells){
			if (m.typeMove == TypeMove.SIMPLE_MOVE && m.prev.equals(before) && m.next.equals(after) && m.next.building.getLevel() == 3 && m.prev.building.getLevel() == 2) { //check the standard condition of winning: a Player's worker moved up (only 1 level) to a building level 3 during his own turn.
				observedModel.applyWin(observedModel.getPlayerTurn());
			} else if(m.typeMove == TypeMove.VICTORY_MOVE && m.prev.equals(before) && m.next.equals(after)){ //the move from "before" to "after" is a VICTORY_MOVE
				observedModel.applyWin(observedModel.getPlayerTurn());
			}
		}
	}

}
