package it.polimi.ingsw.network.driver;

import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.ServerClientListenerThread;
import it.polimi.ingsw.network.objects.NetObject;
import it.polimi.ingsw.util.exceptions.FirstPlayerException;

import java.io.IOException;
import java.net.Socket;

public class ServerListenerDriver extends ServerClientListenerThread {
	private boolean addPlayerCalled;
	private boolean removePlayerCalled;
	private boolean firstPlayerThrown;
	private boolean sendMessageCalled;
	private int addPlayerReturn;

	/**
	 * The constructor initialize a listener for client's messages in the setup stage, this server listens for messages of setup until the games start, when it starts it changes the gamePhase and listen to other messages, depending on the game phase
	 */
	public ServerListenerDriver() {
		super();
		addPlayerCalled = false;
		removePlayerCalled = false;
		firstPlayerThrown = false;
		addPlayerReturn = -1;
	}

	public void callAddPlayer(String name, Server server) {
		try {
			int value = server.addPlayer(name,this);
		} catch (FirstPlayerException e) {
			firstPlayerThrown = true;
		}
		addPlayerCalled = true;
	}
	public void resetReturns() {
		addPlayerReturn = -1;
	}
	public void resetThrown() {
		firstPlayerThrown = false;
	}
	public void resetCalls() {
		addPlayerCalled = false;
		removePlayerCalled = false;
		firstPlayerThrown = false;
	}
	@Override
	public void sendMessage(NetObject object) {
		sendMessageCalled = true;
	}

	public boolean isAddPlayerCalled() {
		return addPlayerCalled;
	}
	public boolean isRemovePlayerCalled() {
		return removePlayerCalled;
	}
	public boolean isFirstPlayerThrown() {
		return firstPlayerThrown;
	}
	public int getAddPlayerReturn() {
		return addPlayerReturn;
	}
}
