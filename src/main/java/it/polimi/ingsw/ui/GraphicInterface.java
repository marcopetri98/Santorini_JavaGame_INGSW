package it.polimi.ingsw.ui;

// necessary imports of Java SE for ServerController class
import it.polimi.ingsw.network.game.NetMap;
import it.polimi.ingsw.network.objects.NetSetup;

public interface GraphicInterface {
	void retrieveError();
	void retrieveConnectionError();
	void retrieveConnectionMsg(NetSetup connMsg);
	void retrieveClientTurn();
	void retrievePhaseChange();
	void retrieveGameStart();
	void retrieveOtherColor();
	void retrieveChallenger();
	void retrieveGodsSelection();
	void retrieveOtherGods();
	void retrieveStarter();
	void retrieveOtherWorkers(NetMap map);
	void retrieveAction();
	void retrieveOtherAction();
	void retrieveWinner();
	void retrieveDefeated();
	void retrieveDisconnection();
}
