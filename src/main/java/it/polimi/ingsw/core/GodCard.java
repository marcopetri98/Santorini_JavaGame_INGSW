package it.polimi.ingsw.core;

public interface GodCard {
    int getNumPlayer();
    int getOwnerPlayer();
    boolean getOwned();
    boolean isPassive();
    void check();
    void apply();
}