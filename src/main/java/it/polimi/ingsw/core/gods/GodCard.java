package it.polimi.ingsw.core.gods;
import it.polimi.ingsw.core.*;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.util.exceptions.NoBuildException;
import it.polimi.ingsw.util.exceptions.NoMoveException;

import java.util.List;

public interface GodCard {
    int getNumPlayer();
    Player getOwner();
    TypeGod getTypeGod();
    String getName();
    String getDescription();
    List<Move> checkMove(Map m, Worker w, Turn turn) throws NoMoveException;
    List<Build> checkBuild(Map m, Worker w, Turn turn) throws NoBuildException;
}