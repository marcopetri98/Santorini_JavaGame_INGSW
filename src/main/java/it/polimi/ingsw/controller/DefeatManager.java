package it.polimi.ingsw.controller;

import it.polimi.ingsw.core.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the controller part dedicated to checking the defeat of any player
 */
public class DefeatManager {
	private Game observedModel;

	// CONSTRUCTOR

	/**
	 * This is the constructor of the class
	 * @param g the {@link Game}
	 */
	public DefeatManager(Game g) {
		observedModel = g;
	}

	/**
	 * The method checks if the standard condition of defeat (with respect to the move phase) is respected: if it is, it calls applyDefeat method in Game class
	 * @param worker1_possible_moves the possible moves of the first worker of the active player
	 * @param worker2_possible_moves the possible moves of the second worker of the active player
	 * @throws NullPointerException if the parameter is null
	 */
	public boolean moveDefeat(List<Move> worker1_possible_moves, List<Move> worker2_possible_moves) throws NullPointerException {
		if(worker1_possible_moves == null || worker2_possible_moves == null){
			throw new NullPointerException();
		} else {
			List<Move> worker1_filtered = filterMoves(worker1_possible_moves);
			List<Move> worker2_filtered = filterMoves(worker2_possible_moves);

			if (worker1_filtered.size() == 0 && worker2_filtered.size() == 0){
				observedModel.applyDefeat(observedModel.getPlayerTurn());
				return true;
			}
		}
		return false;
	}

	/**
	 * The method checks if the standard condition of defeat (with respect to the build phase) is respected: if it is, it calls applyDefeat method in Game class
	 * @param worker_possible_builds the possible builds of the active worker of the active player
	 * @throws NullPointerException if the parameter is null
	 */
	public boolean buildDefeat(List<Build> worker_possible_builds) throws NullPointerException {
		if(worker_possible_builds == null) {
			throw new NullPointerException();
		} else {
			if (worker_possible_builds.size() == 0) {
				observedModel.applyDefeat(observedModel.getPlayerTurn());
				return true;
			}
		}
		return false;
	}

	/**
	 * It receives a list of moves where can be present only moves that can be applied
	 * @param list
	 * @return
	 */
	public static List<Move> filterMoves(List<Move> list) {
		List<Move> newList = new ArrayList<>();
		boolean toAdd = false;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).typeMove == TypeMove.SIMPLE_MOVE || list.get(i).typeMove == TypeMove.CONDITIONED_MOVE || list.get(i).typeMove == TypeMove.VICTORY_MOVE) {
				toAdd = true;
				for (int j = 0; j < list.size(); j++) {
					if (list.get(j).typeMove == TypeMove.FORBIDDEN_MOVE && list.get(i).getCellNext().equals(list.get(j).getCellNext()) && list.get(i).getCellPrev().equals(list.get(j).getCellPrev()) && list.get(i).getWorker().equals(list.get(j).getWorker())) {
						toAdd = false;
					}
				}
			}
			if (toAdd) {
				newList.add(list.get(i));
			}
		}
		return newList;
	}
}
