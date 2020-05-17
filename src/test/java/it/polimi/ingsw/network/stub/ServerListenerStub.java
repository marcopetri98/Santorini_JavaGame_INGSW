package it.polimi.ingsw.network.stub;

import it.polimi.ingsw.network.NetworkPhase;
import it.polimi.ingsw.network.ServerClientListenerThread;
import it.polimi.ingsw.network.objects.NetObject;

public class ServerListenerStub extends ServerClientListenerThread {
	private boolean fatalErrorCalled;
	private boolean closeSocketCalled;
	private boolean sendMessageCalled;
	private boolean setPhaseCalled;
	private String playerName;
	private NetworkPhase netPhase;
	private NetObject messageReceived;

	public ServerListenerStub() {
		super();
		fatalErrorCalled = false;
		closeSocketCalled = false;
		sendMessageCalled = false;
		setPhaseCalled = false;
	}

	public void setPlayerName(String name) {
		playerName = name;
	}
	public void resetCalls() {
		fatalErrorCalled = false;
		closeSocketCalled = false;
		sendMessageCalled = false;
		setPhaseCalled = false;
	}
	public boolean isFatalErrorCalled() {
		return fatalErrorCalled;
	}
	public boolean isCloseSocketCalled() {
		return closeSocketCalled;
	}
	public boolean isSendMessageCalled() {
		return sendMessageCalled;
	}
	public boolean isSetPhaseCalled() {
		return setPhaseCalled;
	}
	public NetObject getMessageReceived() {
		return messageReceived;
	}

	@Override
	public void setGamePhase(NetworkPhase gamePhase) {
		netPhase = gamePhase;
		setPhaseCalled = true;
	}
	@Override
	public void sendMessage(NetObject object) {
		sendMessageCalled = true;
		messageReceived = object;
	}
	@Override
	public void closeSocketAndTerminate() {
		closeSocketCalled = true;
	}
	@Override
	public NetworkPhase getGamePhase() {
		return netPhase;
	}
	@Override
	public String getPlayerName() {
		return playerName;
	}
	@Override
	public void fatalError(String info) {
		fatalErrorCalled = true;
	}

}
