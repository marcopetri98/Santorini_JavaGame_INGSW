package it.polimi.ingsw.core;
import it.polimi.ingsw.exceptions.NoBuildException;
import it.polimi.ingsw.exceptions.NoMoveException;

import java.util.List;

public interface GodCard {
    /*int typeGod;
    Player owner;
    int numPlayer;  //Maximum number of possible players supported by the card
    String name;
    String description;*/
    int getNumPlayer();
    Player getOwner();
    int getTypeGod();
    String getName();
    String getDescription();
    List<Move> checkMove(Map m, Worker w, int type) throws NoMoveException;
    List<Build> checkBuild(Map m, Worker w, int type) throws NoBuildException;
}