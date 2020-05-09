package it.polimi.ingsw.network;

import it.polimi.ingsw.network.driver.ServerListenerDriver;
import it.polimi.ingsw.util.exceptions.AlreadyStartedException;
import it.polimi.ingsw.util.exceptions.FirstPlayerException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServerTest {
	private Server server;

	public ServerTest() {
		this.server = new Server();
	}

	@Before
	public void resetThreads() {
		server = new Server();
	}

	/**
	 * This test tests that the first player has to create the game and that other threads enter in wait if the creator hasn't chosen the size of the game already
	 */
	@Test
	public void gameCreationWithoutRemove() throws InterruptedException {
		ServerClientListenerThread[] threads = new ServerClientListenerThread[2];
		threads[0] = new ServerListenerDriver();
		threads[1] = new ServerListenerDriver();
		ServerListenerDriver[] drivers = new ServerListenerDriver[]{(ServerListenerDriver)threads[0],(ServerListenerDriver)threads[1]};

		try {
			server.addPlayer("Gandalf",drivers[0]);
			fail("On the first player the exception isn't thrown");
		} catch (FirstPlayerException e) {
			assertTrue(server.getToBeCreated());
			assertEquals(server.getClientPosition(threads[0]),0);
			server.isNowPrepared(drivers[0]);
			Thread second = new Thread(() -> drivers[1].callAddPlayer("Gimli",server));
			second.start();
			Thread.sleep(200);
			assertFalse(drivers[1].isFirstPlayerThrown());
			assertEquals(second.getState(),Thread.State.WAITING);
			second.stop();

			// sets the number of players for the lobby and create the game using the driver
			server.setPlayerNumber(2,drivers[0]);
			assertFalse(server.getToBeCreated());
			try {
				server.addPlayer("Gimli", drivers[1]);
				server.isNowPrepared(drivers[1]);
			} catch (FirstPlayerException e1) {
				fail("It has been thrown first player exception by the second player");
			}

			// it controls that is possible to create another lobby (lobby has been reset)
			assertFalse(server.getToBeCreated());
			try {
				server.addPlayer("Gandalf",drivers[0]);
				fail("After the first match it can't be created another one");
			} catch (FirstPlayerException e1) {
				assertTrue(server.getToBeCreated());
			}
		}
	}

	/**
	 * It test if the creator can be remove successfully
	 * @throws AlreadyStartedException
	 */
	@Test
	public void removeCreator() throws AlreadyStartedException {
		ServerClientListenerThread[] threads = new ServerClientListenerThread[2];
		threads[0] = new ServerListenerDriver();
		threads[1] = new ServerListenerDriver();
		ServerListenerDriver[] drivers = new ServerListenerDriver[]{(ServerListenerDriver)threads[0],(ServerListenerDriver)threads[1]};

		try {
			server.addPlayer("Saruman",drivers[0]);
			fail("On the first player the exception isn't thrown");
		} catch (FirstPlayerException e) {
			assertTrue(server.getToBeCreated());
			server.removePlayer("Saruman",drivers[0]);
			assertFalse(server.getToBeCreated());
		}
	}

	/**
	 * It tests if the removePlayer function removes a player that isn't the creator of the lobby.
	 * @throws FirstPlayerException
	 * @throws AlreadyStartedException
	 */
	@Test
	public void removePlayer() throws FirstPlayerException, AlreadyStartedException {
		ServerClientListenerThread[] threads = new ServerClientListenerThread[3];
		threads[0] = new ServerListenerDriver();
		threads[1] = new ServerListenerDriver();
		threads[2] = new ServerListenerDriver();
		ServerListenerDriver[] drivers = new ServerListenerDriver[]{(ServerListenerDriver)threads[0],(ServerListenerDriver)threads[1],(ServerListenerDriver)threads[2]};

		try {
			server.addPlayer("Sauron",drivers[0]);
			fail("On the first player the exception isn't thrown");
		} catch (FirstPlayerException e) {
			assertTrue(server.getToBeCreated());
			server.setPlayerNumber(3,drivers[0]);
			assertFalse(server.getToBeCreated());
			server.addPlayer("Legolas",drivers[1]);

			// now that there are 2 players in a lobby for 3 we try to remove a player and to see if it does this correctly
			server.removePlayer("Legolas",drivers[1]);
			try {
				server.getClientPosition(drivers[1]);
				fail();
			} catch (IllegalCallerException e1) {
				server.getClientPosition(drivers[0]);
			}
		}
	}

	/**
	 * It tests that function doesn't add a player with a null name or a name that is already present in the lobby
	 * @throws FirstPlayerException
	 */
	@Test
	public void addImpossiblePlayer() throws FirstPlayerException {
		ServerClientListenerThread[] threads = new ServerClientListenerThread[2];
		threads[0] = new ServerListenerDriver();
		threads[1] = new ServerListenerDriver();
		ServerListenerDriver[] drivers = new ServerListenerDriver[]{(ServerListenerDriver)threads[0],(ServerListenerDriver)threads[1]};

		try {
			server.addPlayer("Frodo",drivers[0]);
			fail("On the first player the exception isn't thrown");
		} catch (FirstPlayerException e) {
			server.setPlayerNumber(2,drivers[0]);
			assertEquals(server.addPlayer(null,drivers[1]),0);
			assertEquals(server.addPlayer("Frodo",drivers[1]),0);
			try {
				server.getClientPosition(drivers[1]);
				fail();
			} catch (IllegalCallerException e1) {
				server.getClientPosition(drivers[0]);
			}
		}
	}

	@Test
	public void correctSetPlayerNum() {
		ServerClientListenerThread[] threads = new ServerClientListenerThread[2];
		threads[0] = new ServerListenerDriver();
		threads[1] = new ServerListenerDriver();
		ServerListenerDriver[] drivers = new ServerListenerDriver[]{(ServerListenerDriver)threads[0],(ServerListenerDriver)threads[1]};

		try {
			server.addPlayer("Frodo",drivers[0]);
			fail("On the first player the exception isn't thrown");
		} catch (FirstPlayerException e) {
			try {
				server.setPlayerNumber(2,drivers[1]);
				fail();
			} catch (IllegalCallerException e1) {
				server.getClientPosition(drivers[0]);
				try {
					server.setPlayerNumber(5,drivers[0]);
					fail();
				} catch (IllegalArgumentException e2) {
					try {
						server.setPlayerNumber(2,drivers[0]);
						fail();
					} catch (IllegalCallerException e3) {
						assertFalse(server.getToBeCreated());
					}
				}
			}
		}
	}
}