package it.polimi.ingsw.ui;

public interface GraphicMenu {
	public String start();
	public void handleConnection(int param);
	public void handlePreparation(int param);

	// getters of the structure set by the client while interacting
	public String getServerAddress();
	public String getNickname();
	public int getPlayerNumber();
}
