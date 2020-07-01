package it.polimi.ingsw.core;

// necessary imports of Java SE
import it.polimi.ingsw.core.gods.Athena;
import it.polimi.ingsw.core.gods.GodCard;

import it.polimi.ingsw.util.Color;

/**
 * This class stores the information about each player in the game
 */
public class Player {
	public final int playerID;
	public final String playerName;
	private Worker worker1;
	private Worker worker2;
	private Worker activeWorker;
	private boolean workerLocked;
	private GodCard card;

	// CONSTRUCTORS

	/**
	 * Constructor of the class
	 * @param playerName the name of the {@link Player}
	 * @param order the order of this player among the others
	 */
	public Player(String playerName, int order) {
		this.playerName = playerName;
		playerID = order;
		worker1 = null;
		worker2 = null;
		activeWorker = null;
		card = null;
		workerLocked = false;
	}

	// STATE CHANGER METHODS

	/**
	 * Sets the correct {@code activeWorker} based on its number
	 * @param chosen the number of the worker
	 * @throws IllegalArgumentException if chosen isn't 2 or 3
	 */
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

	/**
	 * Sets the {@link GodCard} of this player
	 * @param card1 the {@link GodCard}
	 * @throws NullPointerException if card1 is null
	 */
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

	/**
	 * Sets the color chosen by the player
	 * @param color the chosen color
	 */
	void setPlayerColor(Color color) {
		worker1 = new Worker(color,this,1);
		worker2 = new Worker(color,this,2);
	}

	/**
	 * Resets the lock
	 */
	void resetLocking() {
		workerLocked = false;
		activeWorker = null;
	}

	// CLASSES GETTERS

	/**
	 * Getter of the {@code playerID}
	 * @return the {@code playerID}
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * Getter of the {@code playerName}
	 * @return the {@code playerName}
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * Getter of the first {@link Worker} or this player
	 * @return the first {@link Worker}
	 * @throws IllegalStateException if there is no first {@link Worker}
	 */
	public Worker getWorker1() throws IllegalStateException {
		if (worker1 == null) {
			throw new IllegalStateException();
		}
		return worker1;
	}

	/**
	 * Getter of the second {@link Worker} or this player
	 * @return the second {@link Worker}
	 * @throws IllegalStateException if there is no second {@link Worker}
	 */
	public Worker getWorker2() throws IllegalStateException {
		if (worker2 == null) {
			throw new IllegalStateException();
		}
		return worker2;
	}

	/**
	 * Getter of the active {@link Worker} or this player
	 * @return the active {@link Worker}
	 * @throws IllegalStateException if there is no active {@link Worker}
	 */
	public Worker getActiveWorker() throws IllegalStateException {
		if (activeWorker == null) {
			throw new IllegalStateException();
		}
		return activeWorker;
	}

	/**
	 * Getter of the heir of the {@link GodCard} of this player
	 * @return the coorect heir of the {@link GodCard}
	 * @throws IllegalStateException if {@code card} is null
	 */
	public GodCard getCard()  throws IllegalStateException {
		if (card == null) {
			throw new IllegalStateException();
		}
		return card;
	}

	/**
	 * method that checks if the {@link Worker} is locked
	 * @return true if the {@link Worker} is locked
	 */
	public boolean isWorkerLocked() {
		return workerLocked;
	}

	// OVERRIDDEN METHODS

	/**
	 * Overridden equals method
	 * @param obj the object to check
	 * @return true if they are the same
	 */	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Player) {
			Player other = (Player)obj;
			return playerID == other.playerID && playerName.equals(other.playerName) && ((worker1 == null && other.worker1 == null) || (worker1 != null && other.worker1 != null && worker1.color.equals(other.worker1.color) && worker2.color.equals(other.worker2.color))) && ((card == null && other.card == null) || (card != null && other.card != null && card.getName().equals(other.card.getName())));
		}
		return false;
	}
}