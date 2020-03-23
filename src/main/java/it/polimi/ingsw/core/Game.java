package it.polimi.ingsw.core;

import java.util.List;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class Game extends Observable implements Observer {

    private Player activePlayer; //the player who has to move and build in the turn considered.
    private int numberPlayers;
    private List<Player> players;
    private Map map;

    public Game(String[] names) {
        players = new ArrayList<Player>();
        map = new Map();
    }

    public List<Player> getPlayers() {
        return new ArrayList<Player>(players);
    }

    public void moveWorker(Worker w, Cell c){
        //
    }

    public void changeTurn() {
        //change the activePlayer
    }

    public Map getMap() {
        return map; //or new Map(map);
    }

    @Override
    public void update(Observable o, Object arg) {

    }

}
