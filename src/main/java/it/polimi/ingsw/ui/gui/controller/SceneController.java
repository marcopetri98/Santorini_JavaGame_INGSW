package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.network.objects.NetObject;

public interface SceneController {
	void fatalError();
	void deposeMessage(NetObject message);
}
