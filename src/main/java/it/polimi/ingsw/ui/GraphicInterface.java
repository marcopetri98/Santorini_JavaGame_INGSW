package it.polimi.ingsw.ui;

// necessary imports of Java SE for ServerController class
import it.polimi.ingsw.network.game.NetMap;
import it.polimi.ingsw.network.objects.*;

public interface GraphicInterface {
	void retrieveError();
	void retrieveConnectionError();
	void retrieveConnectionMsg(NetSetup connMsg);
	void retrieveLobbyMsg(NetLobbyPreparation lobbyMsg);
	void retrieveColorMsg(NetColorPreparation colorMsg);
	void retrieveGodsMsg(NetDivinityChoice godsMsg);
	void retrieveGameSetupMsg(NetGameSetup gameSetupMsg);
	void retrieveGamingMsg(NetGaming gamingMsg);
}
