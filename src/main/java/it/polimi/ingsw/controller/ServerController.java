package it.polimi.ingsw.controller;

// necessary imports of Java SE for ServerController class
import java.util.Observable;
import java.util.Observer;

// other project's classes needed here
import it.polimi.ingsw.core.Game;

public class ServerController implements Observer {
	private Game observedModel;

	// constructors and setters for this class
	public ServerController(Game g) throws NullPointerException {
		if (g == null) throw new NullPointerException("The argument passed is a null pointer");
		observedModel = g;
	}
	// this is the function which passes the turn
	public void passTurn() {}
	// this is the function which effectuate a move
	public void move() {}
	// this is the function which effectuate a building
	public void build() {}
	// this function checks if there is a winner
	public void checkWin() {}
	// this function check if the player in the turn is going to loose
	public void checkLoose() {}

	// getters and other functions which doesn't change the structure of the class

	// here there are methods which must be overridden
	@Override
	public void update(Observable o, Object arg) {

	}
}
