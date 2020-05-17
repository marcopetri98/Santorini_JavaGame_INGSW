package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.stub.GameStub;
import it.polimi.ingsw.core.Map;
import it.polimi.ingsw.core.Player;
import it.polimi.ingsw.core.gods.GodCard;
import it.polimi.ingsw.network.objects.*;
import it.polimi.ingsw.network.stub.ServerControllerStub;
import it.polimi.ingsw.network.stub.ServerListenerStub;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.Constants;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class RemoteViewTest {
	private RemoteView rv1;
	private RemoteView rv2;
	private RemoteView rv3;
	private ServerListenerStub listenerStub1;
	private ServerListenerStub listenerStub2;
	private ServerListenerStub listenerStub3;
	private ServerControllerStub controllerStub;
	private GameStub gameStub;
	private String[] playerNames;

	@Before
	public void setupTest() {
		playerNames = new String[]{"Desmond Miles","Alexios","Al Mualim"};
		gameStub = new GameStub(playerNames,true,true);
		listenerStub1 = new ServerListenerStub();
		listenerStub1.setPlayerName(playerNames[0]);
		listenerStub2 = new ServerListenerStub();
		listenerStub2.setPlayerName(playerNames[1]);
		listenerStub3 = new ServerListenerStub();
		listenerStub3.setPlayerName(playerNames[2]);
		controllerStub = new ServerControllerStub(gameStub);
		rv1 = new RemoteView(listenerStub1);
		rv2 = new RemoteView(listenerStub2);
		rv3 = new RemoteView(listenerStub3);
		rv1.addObserver(controllerStub);
		rv2.addObserver(controllerStub);
		rv3.addObserver(controllerStub);
		gameStub.addObserver(rv1);
		gameStub.addObserver(rv2);
		gameStub.addObserver(rv3);
	}

	@Test
	public void updateOrder() {
		listenerStub1.setGamePhase(NetworkPhase.LOBBY);
		rv1.updateOrder(gameStub,playerNames);

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.getMessageReceived() instanceof NetLobbyPreparation);
		assertEquals(listenerStub1.getMessageReceived().message, Constants.LOBBY_TURN);
		NetLobbyPreparation lobbyMsg = (NetLobbyPreparation) listenerStub1.getMessageReceived();
		for (int i = 0; i < gameStub.getPlayers().size(); i++) {
			assertEquals(lobbyMsg.player,gameStub.getPlayers().get(i).getPlayerName());
			assertEquals(lobbyMsg.order,i+1);
			lobbyMsg = lobbyMsg.next;
		}
	}

	@Test
	public void updateColors() {
		HashMap<String, Color> matches = new HashMap<>();
		for (int i = 0; i < gameStub.getPlayers().size(); i++) {
			matches.put(gameStub.getPlayers().get(i).getPlayerName(),gameStub.getPlayers().get(i).getWorker1().color);
		}
		listenerStub1.setGamePhase(NetworkPhase.COLORS);
		rv1.updateColors(gameStub,matches);

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.getMessageReceived() instanceof NetColorPreparation);
		assertEquals(listenerStub1.getMessageReceived().message, Constants.COLOR_CHOICES);
		NetColorPreparation message = (NetColorPreparation) listenerStub1.getMessageReceived();
		while (message != null) {
			boolean playerFound = false;
			for (int i = 0; i < gameStub.getPlayers().size(); i++) {
				if (message.player.equals(gameStub.getPlayers().get(gameStub.getPlayers().size() - 1 - i).getPlayerName()) && message.color.equals(gameStub.getPlayers().get(gameStub.getPlayers().size() - 1 - i).getWorker1().color)) {
					playerFound = true;
				}
			}
			assertTrue(playerFound);
			message = message.next;
		}
	}

	@Test
	public void updateGodsChallenger() {
		List<GodCard> gods = gameStub.getPlayers().stream().map(Player::getCard).collect(Collectors.toList());

		listenerStub1.setGamePhase(NetworkPhase.GODS);
		rv1.updateGods(gameStub,gods);

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.getMessageReceived() instanceof NetDivinityChoice);
		assertEquals(listenerStub1.getMessageReceived().message, Constants.GODS_GODS);
	}

	@Test
	public void updateGodsChoice() {
		HashMap<String, GodCard> matches = new HashMap<>();
		for (int i = 0; i < gameStub.getPlayers().size(); i++) {
			matches.put(gameStub.getPlayers().get(i).getPlayerName(),gameStub.getPlayers().get(i).getCard());
		}
		listenerStub1.setGamePhase(NetworkPhase.GODS);
		rv1.updateGods(gameStub,matches);

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.getMessageReceived() instanceof NetDivinityChoice);
		assertEquals(listenerStub1.getMessageReceived().message, Constants.GODS_CHOICES);
		NetDivinityChoice message = (NetDivinityChoice) listenerStub1.getMessageReceived();
		while (message != null) {
			boolean coupleFound = false;
			for (int i = 0; i < gameStub.getPlayers().size(); i++) {
				if (message.player.equals(gameStub.getPlayers().get(i).getPlayerName()) && message.divinity.equals(gameStub.getPlayers().get(i).getCard().getName().toUpperCase())) {
					coupleFound = true;
				}
			}
			assertTrue(coupleFound);
			message = message.next;
		}
	}

	@Test
	public void updateGodsStarter() {
		listenerStub1.setGamePhase(NetworkPhase.GODS);
		rv1.updateGods(gameStub,"Desmond Miles");

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.getMessageReceived() instanceof NetDivinityChoice);
		assertEquals(listenerStub1.getMessageReceived().message, Constants.GODS_STARTER);
	}

	@Test
	public void updatePositions() {
		listenerStub1.setGamePhase(NetworkPhase.SETUP);
		rv1.updatePositions(gameStub,new Map(),false);

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.getMessageReceived() instanceof NetGameSetup);
		assertEquals(listenerStub1.getMessageReceived().message, Constants.GENERAL_GAMEMAP_UPDATE);
	}

	@Test
	public void updateMove() {
		listenerStub1.setGamePhase(NetworkPhase.PLAYERTURN);
		rv1.updateMove(gameStub,new Map());

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.getMessageReceived() instanceof NetGaming);
		assertEquals(listenerStub1.getMessageReceived().message, Constants.GENERAL_GAMEMAP_UPDATE);
	}

	@Test
	public void updateBuild() {
		listenerStub1.setGamePhase(NetworkPhase.PLAYERTURN);
		rv1.updateBuild(gameStub,new Map());

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.getMessageReceived() instanceof NetGaming);
		assertEquals(listenerStub1.getMessageReceived().message, Constants.GENERAL_GAMEMAP_UPDATE);
	}

	@Test
	public void updateDefeat() {
		listenerStub1.setGamePhase(NetworkPhase.PLAYERTURN);
		rv1.updateDefeat(gameStub,"Al Mualim");
		rv2.updateDefeat(gameStub,"Al Mualim");
		rv3.updateDefeat(gameStub,"Al Mualim");

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertFalse(listenerStub2.isFatalErrorCalled());
		assertFalse(listenerStub3.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub2.isSendMessageCalled());
		assertTrue(listenerStub3.isSendMessageCalled());
		assertTrue(listenerStub1.getMessageReceived() instanceof NetGaming);
		assertTrue(listenerStub2.getMessageReceived() instanceof NetGaming);
		assertTrue(listenerStub3.getMessageReceived() instanceof NetGaming);
		assertEquals(listenerStub1.getMessageReceived().message, Constants.GENERAL_DEFEATED);
		assertEquals(listenerStub2.getMessageReceived().message, Constants.GENERAL_DEFEATED);
		assertEquals(listenerStub3.getMessageReceived().message, Constants.GENERAL_DEFEATED);

		assertEquals(listenerStub3.getGamePhase(),NetworkPhase.OBSERVER);
	}

	@Test
	public void updateWinner() {
		listenerStub1.setGamePhase(NetworkPhase.PLAYERTURN);
		rv1.updateWinner(gameStub,"Alexios");

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.isCloseSocketCalled());
		assertTrue(listenerStub1.getMessageReceived() instanceof NetGaming);
		assertEquals(listenerStub1.getMessageReceived().message, Constants.GENERAL_WINNER);
	}

	@Test
	public void updateQuit() {
		rv1.updateQuit(gameStub,"Al Mualim");

		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.getMessageReceived() instanceof NetGaming);
		assertEquals(listenerStub1.getMessageReceived().message, Constants.GENERAL_PLAYER_DISCONNECTED);
	}

	@Test
	public void updatePhaseChange() {
	}

	@Test
	public void updateActivePlayer() {
	}

	@Test
	public void updateGameFinished() {
		listenerStub1.setGamePhase(NetworkPhase.COLORS);
		rv1.updateGameFinished(gameStub);
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.isCloseSocketCalled());
		assertTrue(listenerStub1.getMessageReceived() instanceof NetColorPreparation);
		assertEquals(listenerStub1.getMessageReceived().message, Constants.GENERAL_SETUP_DISCONNECT);

		setupTest();
		listenerStub1.setGamePhase(NetworkPhase.GODS);
		rv1.updateGameFinished(gameStub);
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.isCloseSocketCalled());
		assertTrue(listenerStub1.getMessageReceived() instanceof NetDivinityChoice);
		assertEquals(listenerStub1.getMessageReceived().message, Constants.GENERAL_SETUP_DISCONNECT);

		setupTest();
		listenerStub1.setGamePhase(NetworkPhase.SETUP);
		rv1.updateGameFinished(gameStub);
		assertFalse(listenerStub1.isFatalErrorCalled());
		assertTrue(listenerStub1.isSendMessageCalled());
		assertTrue(listenerStub1.isCloseSocketCalled());
		assertTrue(listenerStub1.getMessageReceived() instanceof NetGameSetup);
		assertEquals(listenerStub1.getMessageReceived().message, Constants.GENERAL_SETUP_DISCONNECT);
	}
}