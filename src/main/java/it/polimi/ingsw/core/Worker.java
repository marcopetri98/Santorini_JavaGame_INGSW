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
    private Color color;

    public Worker(Color color){
        this.color = color;
    }

    // getter of position
    public Cell getPos() {
        return position;
    }

    //setter of position
    public void setPos(Cell c){
        previousPosition = this.position;
        Cell[] positions = new Cell[2];
        positions[0] = this.position;
        positions[1] = c;
        this.position = c;
        setChanged();
        notifyObservers(positions);
    }

}