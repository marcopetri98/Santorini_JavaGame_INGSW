package it.polimi.ingsw.controller;

// necessary imports of Java SE
import java.util.Observable;
import java.util.Observer;

// other project's classes needed here
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.core.Worker;
import it.polimi.ingsw.core.Cell;

public class ServerController implements Observer {
	private Game observedModel;
	private boolean phaseMove;
	private boolean phaseBuild;

	// constructors and setters for this class
	public ServerController(Game g) throws NullPointerException {
		if (g == null) throw new NullPointerException("The argument passed is a null pointer");
		observedModel = g;
		phaseMove = false;
		phaseBuild = false;
	}

	// this is the function which passes the turn
	public void passTurn() {}

	// this is the function which effectuate a move
	public boolean move(Worker w, Cell c) {
		// TODO: inser the code that check that for this worker is possible to move in the cell 'c'
		return true;
	}

	// this is the function which effectuate a building
	public boolean build(Worker w, Cell c) {
		// TODO: insert the code that check that is possible to build
		return true;
	}

	// this function checks if there is a winner
	/*public Pair<Player, Boolean> checkWin() {
		// TODO: insert the code which check if there is a winner at the end of the current turn
	}
	// this function check if the player in the turn is going to loose
	public Pair<Player, Boolean> checkLoose() {
		// TODO: insert the code which check if the player of the current turn lost the match
	}*/

	// getters and other functions which doesn't change the structure of the class

	// here there are methods which must be overridden
	@Override
	public void update(Observable o, Object arg) {

	}
}
