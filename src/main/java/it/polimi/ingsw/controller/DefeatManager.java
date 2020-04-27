package it.polimi.ingsw.controller;

import it.polimi.ingsw.core.Build;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.core.Move;
import it.polimi.ingsw.core.Player;

import java.util.List;

public class DefeatManager {
	private Game observedModel;

	// constructor for this class
	public DefeatManager(Game g) {
		observedModel = g;
	}

	/**
	 * The function checks if the standard condition of defeat (with respect to the move phase) is respected: if it is, it calls applyDefeat method in Game class
	 * @param worker1_possible_moves the possible moves of the first worker of the active player
	 * @param worker2_possible_moves the possible moves of the second worker of the active player
	 */
	public void moveDefeat(List<Move> worker1_possible_moves, List<Move> worker2_possible_moves){
		if(worker1_possible_moves == null && worker2_possible_moves == null){
			observedModel.applyDefeat(observedModel.getPlayerTurn());
		}
		else if(worker1_possible_moves == null && worker2_possible_moves.size() == 0){
			observedModel.applyDefeat(observedModel.getPlayerTurn());
		}
		else if(worker1_possible_moves != null && worker1_possible_moves.size() == 0 && worker2_possible_moves == null){
			observedModel.applyDefeat(observedModel.getPlayerTurn());
		}
		else if(worker1_possible_moves != null && worker1_possible_moves.size() == 0 && worker2_possible_moves.size() == 0){
			observedModel.applyDefeat(observedModel.getPlayerTurn());
		}
	}

	/**
	 * The function checks if the standard condition of defeat (with respect to the build phase) is respected: if it is, it calls applyDefeat method in Game class
	 * @param worker_possible_builds the possible builds of the active worker of the active player
	 */
	public void buildDefeat(List<Build> worker_possible_builds){
		if(worker_possible_builds == null){
			observedModel.applyDefeat(observedModel.getPlayerTurn());
		}
		else if(worker_possible_builds.size() == 0){
			observedModel.applyDefeat(observedModel.getPlayerTurn());
		}
	}
}
