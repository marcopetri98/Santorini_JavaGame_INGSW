package it.polimi.ingsw.core;

// necessary imports of Java SE
import it.polimi.ingsw.core.gods.Athena;
import it.polimi.ingsw.core.gods.GodCard;

import java.awt.Color;

public class Player {
	public final int playerID;
	public final String playerName;
	private Worker worker1;
	private Worker worker2;
	// TODO: delete active worker
	private Worker activeWorker;
	private GodCard card;

	// constructors
	public Player(String playerName) {
		this.playerName = playerName;
		playerID = playerName.hashCode();
		worker1 = null;
		worker2 = null;
		activeWorker = null;
		card = null;
	}

	// STATE CHANGER METHODS
	// TODO: delete the choose worker method
	public void chooseWorker(int chosen) throws IllegalArgumentException {
		if (chosen == 1) {
			this.activeWorker = worker1;
		} else if (chosen==2) {
			this.activeWorker = worker2;
		} else {
			throw new IllegalArgumentException();
		}
	}
	public void setGodCard(GodCard card1) {  //Ties the observable and the observer together TODO: check the parameters and the behavior of the method
		card = card1;
		if(card1 instanceof Athena){
			worker1.addObserver((Athena) card);
			worker2.addObserver((Athena) card);
		}
	}
	public void setPlayerColor(Color color) {
		worker1 = new Worker(color,this,1);
		worker2 = new Worker(color,this,2);
	}

	// CLASSES GETTERS
	public int getPlayerID() {
		return playerID;
	}
	public String getPlayerName() {
		return playerName;
	}
	public Worker getWorker1() throws IllegalStateException {
		if (worker1 == null) {
			throw new IllegalStateException();
		}
		return worker1;
	}
	public Worker getWorker2() throws IllegalStateException {
		if (worker2 == null) {
			throw new IllegalStateException();
		}
		return worker2;
	}
	// TODO: delete active worker get
	public Worker getActiveWorker() throws IllegalStateException {
		if (activeWorker == null) {
			throw new IllegalStateException();
		}
		return activeWorker;
	}
	public GodCard getCard()  throws IllegalStateException {
		if (card == null) {
			throw new IllegalStateException();
		}
		return card;
	}

	// OVERRIDDEN METHODS
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		} else {
			Player other = (Player)obj;
			if (playerID == other.playerID && playerName.equals(other.playerName) && worker1.equals(other.worker1) && worker2.equals(other.worker2) && card.equals(other.card)) {
				return true;
			} else {
				return false;
			}
		}
	}
	@Override
	public Player clone() {
		Player newPlayer = new Player(playerName);
		newPlayer.setPlayerColor(worker1.color);
		newPlayer.setGodCard(card);
		return new Player(playerName);
	}
}