package it.polimi.ingsw.core;

// necessary imports of Java SE
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PreGame extends Observable implements Observer {
	private String[] playerNames;
	private Color[] playerColors;
	private GodCard[] playerGods;
	private List<GodCard> godCards;

	// constructors
	public PreGame(String[] names) {
		playerNames = names;
		createGodCards();
	}

	// setters
	public void setPlayerColor(String player, Color color) throws IllegalArgumentException {
		int i;
		boolean found = false;
		for (i = 0; i < playerNames.length && !found; i++) {
			if (playerNames[i].equals(player)) {
				found = true;
			}
		}
		if (i == playerNames.length) {
			throw new IllegalArgumentException();
		} else {
			playerColors[i] = color;
		}
	}
	public void setPlayerGod(String player, GodCard god) throws IllegalArgumentException {
		int i;
		boolean found = false;
		for (i = 0; i < playerNames.length && !found; i++) {
			if (playerNames[i].equals(player)) {
				found = true;
			}
		}
		if (i == playerNames.length) {
			throw new IllegalArgumentException();
		} else {
			playerGods[i] = god;
		}
	}

	// getters
	public Color getPlayerColor(String player) throws IllegalArgumentException  {
		int i;
		boolean found = false;
		for (i = 0; i < playerNames.length && !found; i++) {
			if (playerNames[i].equals(player)) {
				found = true;
			}
		}
		if (i == playerNames.length) {
			throw new IllegalArgumentException();
		} else {
			return playerColors[i];
		}
	}
	public GodCard getPlayerGodCard(String player) throws IllegalArgumentException  {
		int i;
		boolean found = false;
		for (i = 0; i < playerNames.length && !found; i++) {
			if (playerNames[i].equals(player)) {
				found = true;
			}
		}
		if (i == playerNames.length) {
			throw new IllegalArgumentException();
		} else {
			return playerGods[i];
		}
	}

	// support methods
	private void createGodCards() {
		int i = 0;
		godCards = new ArrayList<>();
		godCards.add(new Apollo());
		godCards.add(new Artemis());
		godCards.add(new Minotaur());
		godCards.add(new Atlas());
		godCards.add(new Demeter());
		godCards.add(new Hephaestus());
		godCards.add(new Athena());
		godCards.add(new Pan());
		godCards.add(new Prometheus());
	}

	@Override
	public void update(Observable o, Object arg) {

	}
}
