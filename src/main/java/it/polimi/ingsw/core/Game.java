package it.polimi.ingsw.core;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


public class Game extends Observable implements Observer {

    private Player activePlayer; //the player who has to move and build in the turn considered.
    //private int numberPlayers; (names.length, then players.size)
    private List<Player> players;
    private Map map;

    public Game(String[] names, Color[] colors) {   //through nicknames I'm able to know how many players are playing (2 or 3).
        players = new ArrayList<Player>();
        map = new Map();
		for(int i=0; i<names.length; i++){
			players.add(new Player(names[i], colors[i]));
		}
    }

    public List<Player> getPlayers() {
        return new ArrayList<Player>(players);
    }

    public void moveWorker(Worker w, Cell c){  //server controlled if the move was legit
		w.getPos().setWorker(null);
    	w.setPos(c);
    	c.setWorker(w);
    }

    public void changeTurn() {  //active Player become the next one
    	if(players.size() == 2) {
    		if(players.indexOf(players.equals(activePlayer))==0)
				activePlayer = players.get(1);
    		else activePlayer = players.get(0);
		} else {  //players.size() == 3
			if (players.indexOf(players.equals(activePlayer)) < 2)
				activePlayer = players.get(players.indexOf(players.equals(activePlayer)) + 1);
			else
				activePlayer = players.get(0);
		}
    }

    public Map getMap() {
        return map; //or new Map(map);
    }

    @Override
    public void update(Observable o, Object arg) {

    }

}
