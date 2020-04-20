package it.polimi.ingsw.core;
import it.polimi.ingsw.util.exceptions.NoBuildException;
import it.polimi.ingsw.util.exceptions.NoMoveException;

import java.util.List;

public interface GodCard {
    /*TypeGod typeGod;
    Player owner;
    int numPlayer;  //Maximum number of possible players supported by the card
    String name;
    String description;*/
    int getNumPlayer();
    Player getOwner();
    TypeGod getTypeGod();
    String getName();
    String getDescription();
    List<Move> checkMove(Map m, Worker w, TypeMove type) throws NoMoveException;
    List<Build> checkBuild(Map m, Worker w, TypeBuild type) throws NoBuildException;
}