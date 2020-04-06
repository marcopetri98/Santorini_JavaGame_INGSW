package it.polimi.ingsw.core;

// necessary imports of Java SE
import java.awt.Color;
import java.util.ArrayList;
import java.util.Observable;
import java.util.List;

public class Worker extends Observable {
    private Cell position;
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
        List<Cell> positions = new ArrayList<>();
        positions.add(this.position);
        positions.add(c);
        this.position = c;
        setChanged();
        notifyObservers(positions);
    }

    /*public void notifyAthena(){
        //athena update();
    }*/
}