package it.polimi.ingsw.controller;

// necessary imports of Java SE

// other project's classes needed here
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.game.NetAvailableBuildings;
import it.polimi.ingsw.network.game.NetAvailablePositions;
import it.polimi.ingsw.util.observers.ObservableController;
import it.polimi.ingsw.util.observers.ObserverController;
import it.polimi.ingsw.util.observers.ObserverObject;

public class ServerController extends ObservableController implements ObserverController {
	private final Game observedModel;
	private final Mover moveController;
	private final Builder buildController;
	private final DefeatManager defeatController;
	private final VictoryManager victoryController;
	private final SetupManager setupController;

	// constructors and setters for this class
	public ServerController(Game g) throws NullPointerException {
		if (g == null) throw new NullPointerException();
		moveController = new Mover();
		buildController = new Builder();
		defeatController = new DefeatManager();
		victoryController = new VictoryManager();
		setupController = new SetupManager(g);
		observedModel = g;
	}

	// actions called by the players or the server
	public void generateOrder() {
		setupController.generateOrder();
	}

	// this is the function which passes the turn
	private void passTurn() {}

	@Override
	public void updateColors(Object playerColors) {
	}
	@Override
	public void updateGods(Object playerGods) {

	}
	@Override
	public void updatePositions(Object netObject, boolean finished) {

	}
	@Override
	public void updateMove(Object netMap) {

	}
	@Override
	public void updateBuild(Object netMap) {

	}
	@Override
	public void updateQuit(String playerName) {

	}

	@Override
	public Turn givePhase() {
		return null;
	}
	@Override
	public NetAvailablePositions giveAvailablePositions() {
		return null;
	}
	@Override
	public NetAvailableBuildings giveAvailableBuildings() {
		return null;
	}
}
