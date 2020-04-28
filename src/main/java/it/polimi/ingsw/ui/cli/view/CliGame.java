package it.polimi.ingsw.ui.cli.view;

import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.network.objects.NetObject;

import java.util.ArrayList;
import java.util.List;

public class CliGame {
	private Phase phase; //Phase? didn't decide yet...
	private List<NetObject> messages;

	public CliGame(){ messages = new ArrayList<>(); }
	//eventually setters and getters...
	public void start(){}
	private void parseMessages(){}
	private void parseColor(){}
	private void parseGod(){}
	private void drawPossibilities(){}
	private void drawMap(){}
}
