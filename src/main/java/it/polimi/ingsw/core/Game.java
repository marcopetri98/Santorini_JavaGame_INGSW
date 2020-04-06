package it.polimi.ingsw.core;

import it.polimi.ingsw.util.Pair;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


public class Game extends Observable implements Observer {
	private Player activePlayer; //the player who has to move and build in the turn considered.
	private List<Player> players;
	private List<GodCard> godCards;
	private List<Pair<String,Integer>> godPositions;
	private Map map;

	// constructors and setters for this class
	public Game(String[] names, Color[] colors) {   //through nicknames I'm able to know how many players are playing (2 or 3).
		players = new ArrayList<Player>();
		map = new Map();
		for(int i=0; i<names.length; i++){
			players.add(new Player(names[i], colors[i]));
		}
		activePlayer = players.get(0);
		createGodCards();
	}
	public void moveWorker(Worker w, Cell c){  //server controlled if the move was legit
		w.getPos().setWorker(null);
		w.setPos(c);
		c.setWorker(w);
	}
	public void changeTurn() {  //active Player become the next one
		if(players.size() == 2) {
			if (players.indexOf(activePlayer) == 0) {
				activePlayer = players.get(1);
			} else {
				activePlayer = players.get(0);
			}
		} else {  //players.size() == 3
			if (players.indexOf(activePlayer) < 2) {
				activePlayer = players.get(players.indexOf(activePlayer) + 1);
			} else {
				activePlayer = players.get(0);
			}
		}
	}

	// getters and other functions which doesn't change the structure of the class
	public Map getMap() throws CloneNotSupportedException {
		return (Map)map.clone();
	}
	public List<Player> getPlayers() {
		return new ArrayList<Player>(players);
	}

	// private methods needed only for this class
	private void createGodCards() {
		int i = 0;
		godCards = new ArrayList<>();
		godPositions = new ArrayList<>();
		godCards.add(new Apollo());
		godPositions.add(new Pair<String, Integer>(godCards.get(i).getName(),i++));
		godCards.add(new Artemis());
		godPositions.add(new Pair<String, Integer>(godCards.get(i).getName(),i++));
		godCards.add(new Minotaur());
		godPositions.add(new Pair<String, Integer>(godCards.get(i).getName(),i++));
		godCards.add(new Atlas());
		godPositions.add(new Pair<String, Integer>(godCards.get(i).getName(),i++));
		godCards.add(new Demeter());
		godPositions.add(new Pair<String, Integer>(godCards.get(i).getName(),i++));
		godCards.add(new Hephaestus());
		godPositions.add(new Pair<String, Integer>(godCards.get(i).getName(),i++));
		godCards.add(new Athena());
		godPositions.add(new Pair<String, Integer>(godCards.get(i).getName(),i++));
		godCards.add(new Pan());
		godPositions.add(new Pair<String, Integer>(godCards.get(i).getName(),i++));
		godCards.add(new Prometheus());
		godPositions.add(new Pair<String, Integer>(godCards.get(i).getName(),i));
	}

	// here there are methods which must be overridden
	@Override
	public void update(Observable o, Object arg) {

	}
}
