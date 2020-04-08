package it.polimi.ingsw.controller;

// other project's classes needed here
import it.polimi.ingsw.core.PreGame;

// necessary imports of Java SE
import java.util.Observable;
import java.util.Observer;

public class PreGameController implements Observer {
	private PreGame preGame;

	// constructor for this class
	public PreGameController(PreGame preGame) {
		this.preGame = preGame;
	}

	@Override
	public void update(Observable o, Object arg) {

	}
}
