package it.polimi.ingsw.core;

// necessary imports of Java SE
import java.awt.Color;

public class Worker {
    private int level;
    private Cell position;
    private Color color;

    public Worker(Color color){
        this.color = color;
    }

    // CLASSES GETTERS
    public int getLevel() {
        return level;
    }
    public Cell getPos() {
        return position;
    }
}