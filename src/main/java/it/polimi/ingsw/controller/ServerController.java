package it.polimi.ingsw.controller;

// necessary imports of Java SE

// other project's classes needed here
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;
import it.polimi.ingsw.util.observers.ObserverObjectController;

public class ServerController implements ObserverObjectController {
	private Game observedModel;
	private Mover moveController;
	private Builder buildController;
	private DefeatManager defeatController;
	private VictoryManager victoryController;
	private SetupManager setupController;
	private boolean setupPhase;
	private boolean phaseMove;
	private boolean phaseBuild;

	// constructors and setters for this class
	public ServerController(Game g) throws NullPointerException {
		if (g == null) throw new NullPointerException();
		moveController = new Mover();
		buildController = new Builder();
		defeatController = new DefeatManager();
		victoryController = new VictoryManager();
		setupController = new SetupManager(g);
		observedModel = g;
		setupPhase = true;
		phaseMove = false;
		phaseBuild = false;
	}

	// actions called by the players or the server
	public void generateOrder() {
		setupController.generateOrder();
	}

	// this is the function which passes the turn
	private void passTurn() {}

	@Override
	public boolean updateColors(Object playerColors) throws IllegalArgumentException, WrongPhaseException {

	}
	@Override
	public boolean updateGods(Object playerGods) throws IllegalArgumentException, WrongPhaseException {

	}
	@Override
	public boolean updatePositions(Object netObject, boolean finished) throws WrongPhaseException {

	}
	@Override
	public boolean updateMove(Object netMap) throws NullPointerException, WrongPhaseException {

	}
	@Override
	public boolean updateBuild(Object netMap) throws NullPointerException, WrongPhaseException {

	}
	@Override
	public void updateQuit(String playerName) throws NullPointerException {

	}
}
