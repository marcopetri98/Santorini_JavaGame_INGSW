package it.polimi.ingsw.core;

// necessary imports of Java SE
import java.awt.Color;
import java.util.ArrayList;
import java.util.Observable;
import java.util.List;

public class Worker extends Observable {
    private Cell previousPosition;
    private Cell position;
    private Cell lastBuild;
    public final Color color;
    public final Player owner;
    public final int workerID;

    public Worker(Color color, Player owner, int num){
        this.color = color;
        this.owner = owner;
        workerID = owner.getPlayerID()+num;
    }

    // getter of position
    public Cell getPos() {
        return position;
    }

    //Setter of position, Implements the observable object
    public void setPos(Cell c){
        previousPosition = this.position;
        Cell[] positions = new Cell[2];
        positions[0] = this.position;   //Old position
        positions[1] = c;               //New position
        this.position = c;
        setChanged();
        notifyObservers(positions);
    }   //ADD TO OBSERVER ONLY THE ONES CREATED BY THE PLAYER WITH ATHENA
}