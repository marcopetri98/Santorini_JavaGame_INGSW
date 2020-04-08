package it.polimi.ingsw.core;

// necessary imports of Java SE
import java.awt.Color;

public class Player {
	private int playerID;
	private String playerName;
	//private int color;    //Moved in Worker class
	private Worker worker1;
	private Worker worker2;
	private Worker activeWorker;
	private GodCard card;

	// constructors
	public Player(String playerName, Color color) {  //CHECK IN-GAME IF THERE IS ALREADY ANY PLAYER WITH SPECIFIED NAME && COLOR!!!!
		this.playerName = playerName;
		this.playerID = playerName.hashCode();
		worker1 = new Worker(color);
		worker2 = new Worker(color);
	}

	// STATE CHANGER METHODS
	public void chooseWorker(int chosen){
		if (chosen == 1) this.activeWorker = worker1;
		else if (chosen == 2) this.activeWorker = worker2;
		else System.out.println("Error");
	}

	// CLASSES GETTERS
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
	public Worker getActiveWorker() {
		return activeWorker;
	}
	public GodCard getCard() {
		return card;
	}

}