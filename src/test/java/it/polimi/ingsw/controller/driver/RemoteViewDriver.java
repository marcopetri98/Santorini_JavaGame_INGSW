package it.polimi.ingsw.controller.driver;

import it.polimi.ingsw.network.RemoteView;
import it.polimi.ingsw.network.ServerClientListenerThread;

public class RemoteViewDriver extends RemoteView {
	private boolean communicateErrorCalled;

	public RemoteViewDriver(ServerClientListenerThread handler) throws NullPointerException {
		super(handler);
		communicateErrorCalled = false;
	}

	public boolean isCommunicateErrorCalled() {
		return communicateErrorCalled;
	}
	public void resetCalls() {
		communicateErrorCalled = false;
	}

	@Override
	public void communicateError() {
		communicateErrorCalled = true;
	}
}
