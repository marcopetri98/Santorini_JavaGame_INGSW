package it.polimi.ingsw.core;

// necessary imports of Java SE
import it.polimi.ingsw.core.gods.Athena;
import it.polimi.ingsw.core.gods.GodCard;

import it.polimi.ingsw.util.Color;

public class Player {
	public final int playerID;
	public final String playerName;
	private Worker worker1;
	private Worker worker2;
	private Worker activeWorker;
	private boolean workerLocked;
	private GodCard card;

	// constructors
	public Player(String playerName) {
		this.playerName = playerName;
		playerID = playerName.hashCode();	//TODO: remove hashcode and put something else!
		worker1 = null;
		worker2 = null;
		activeWorker = null;
		card = null;
		workerLocked = false;
	}

	// STATE CHANGER METHODS
	void chooseWorker(int chosen) throws IllegalArgumentException {
		if (chosen == 1) {
			this.activeWorker = worker1;
			workerLocked = true;
		} else if (chosen==2) {
			this.activeWorker = worker2;
			workerLocked = true;
		} else {
			throw new IllegalArgumentException();
		}
	}
	void setGodCard(GodCard card1) throws NullPointerException {
		if (card1 == null) {
			throw new NullPointerException();
		}

		card = card1;
		if(card1 instanceof Athena){
			worker1.addObserver((Athena) card);
			worker2.addObserver((Athena) card);
		}
	}
	void setPlayerColor(Color color) {
		worker1 = new Worker(color,this,1);
		worker2 = new Worker(color,this,2);
	}
	void resetLocking() {
		workerLocked = false;
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
	public boolean isWorkerLocked() {
		return workerLocked;
	}

	// OVERRIDDEN METHODS
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Player) {
			Player other = (Player)obj;
			return playerID == other.playerID && playerName.equals(other.playerName) && ((worker1 == null && other.worker1 == null) || (worker1 != null && other.worker1 != null && worker1.color.equals(other.worker1.color) && worker2.color.equals(other.worker2.color))) && ((card == null && other.card == null) || (card != null && other.card != null && card.getName().equals(other.card.getName())));
		}
		return false;
	}

	// TODO: eliminate if possible
	@Override
	public Player clone() {	//TODO: doesn't make the previuos positions the same!! Should not be a problem though
		Player newPlayer = new Player(playerName);
		newPlayer.setPlayerColor(worker1.color);
		newPlayer.worker1.setPos(this.worker1.getPos());
		newPlayer.worker2.setPos(this.worker2.getPos());
		newPlayer.setGodCard(this.card);
		return newPlayer;
	}
}