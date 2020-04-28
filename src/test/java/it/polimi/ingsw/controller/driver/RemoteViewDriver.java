package it.polimi.ingsw.controller.driver;

import it.polimi.ingsw.network.RemoteView;
import it.polimi.ingsw.network.ServerClientListenerThread;

public class RemoteViewDriver extends RemoteView {
	public RemoteViewDriver(ServerClientListenerThread handler) throws NullPointerException {
		super(handler);
	}
}
