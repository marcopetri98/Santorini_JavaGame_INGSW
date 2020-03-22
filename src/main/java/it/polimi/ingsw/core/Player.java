package it.polimi.ingsw.Core;

public class Player {
    private int playerID;
    private String playerName;
    //private int color;    //Moved in Worker class
    private Worker worker1;
    private Worker worker2;
    private int activeWorker;
    private GodCard card;

    public Player(String playerName, String color) {  //CHECK IN-GAME IF THERE IS ALREADY ANY PLAYER WITH SPECIFIED NAME && COLOR!!!!
        this.playerName = playerName;
        this.playerID = playerName.hashCode();
        worker1 = new Worker(color);
        worker2 = new Worker(color);
    }

    public int getPlayerID() {
        return playerID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Worker getWorker1() {
        return worker1;
    }

    public Worker getWorker2() {
        return worker2;
    }

    public int getActiveWorker() {
        return activeWorker;
    }

    public GodCard getCard() {
        return card;
    }

    public void chooseWorker(int chosen){
        if (chosen == 1) this.activeWorker = 1;
        else if (chosen == 2) this.activeWorker = 2;
        else System.out.println("Error");
    }
}