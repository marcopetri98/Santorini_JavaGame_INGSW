package it.polimi.ingsw.controller;

// other project's classes needed here
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.core.Player;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// necessary imports of Java SE

public class SetupManager {
	private Game observedModel;

	// constructor for this class
	public SetupManager(Game g) {
		observedModel = g;
	}

	// actions to effectuate on the game
	/**
	 * This method generate random order of play in the game choosing the first player randomly, at the end it updates the game
	 */
	public void generateOrder() {
		int playerNumber, random;
		List<String> temp = new ArrayList<>(), gamers = new ArrayList<>();
		List<Player> players = observedModel.getPlayers();

		for (Player player : players) {
			temp.add(player.getPlayerName());
		}
		playerNumber = temp.size();
		// generate random order
		while (gamers.size() < playerNumber) {
			if (temp.size() > 1) {
				// find a random player to add to the gamers list
				random = (int) (Math.random() * (double) temp.size()-1);
				gamers.add(temp.get(random));
				temp.remove(random);
			} else {
				// adds the last player
				gamers.add(temp.get(0));
			}
		}
		try {
			observedModel.setOrder(gamers);
		} catch (IllegalArgumentException | WrongPhaseException e) {
			throw new AssertionError("Generate order called in a phase different from the setup");
		}
	}
}
