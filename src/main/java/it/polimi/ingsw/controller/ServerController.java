package it.polimi.ingsw.controller;

// necessary imports of Java SE

// other project's classes needed here
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.game.NetAvailableBuildings;
import it.polimi.ingsw.network.game.NetAvailablePositions;
import it.polimi.ingsw.util.observers.ObservableController;
import it.polimi.ingsw.util.observers.ObservableObject;
import it.polimi.ingsw.util.observers.ObserverController;

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
		moveController = new Mover(g);
		buildController = new Builder(g);
		defeatController = new DefeatManager(g);
		victoryController = new VictoryManager(g);
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
	public void updateColors(ObservableObject observed, Object playerColors) {
	}
	@Override
	public void updateGods(ObservableObject observed, Object playerGods) {

	}
	@Override
	public void updatePositions(ObservableObject observed, Object netObject) {

	}
	@Override
	public void updateMove(ObservableObject observed, Object netMap) {

	}
	@Override
	public void updateBuild(ObservableObject observed, Object netMap) {

	}
	@Override
	public void updateQuit(ObservableObject observed, String playerName) {

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
