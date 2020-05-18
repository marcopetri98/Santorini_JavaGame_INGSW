package it.polimi.ingsw.controller;

// other project's classes needed here
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.core.Map;
import it.polimi.ingsw.core.Player;
import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetGameSetup;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.exceptions.BadRequestException;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;

// necessary imports of Java SE
import it.polimi.ingsw.util.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetupManager {
	private final Game observedModel;

	// constructor for this class
	public SetupManager(Game g) {
		observedModel = g;
	}

	// actions to effectuate on the game
	/**
	 * This method generate random order of play in the game choosing the first player randomly, at the end it updates the game
	 */
	public void generateOrder() {
		int playerNumber, random;
		List<String> temp = new ArrayList<>(), gamers = new ArrayList<>();
		List<Player> players = observedModel.getPlayers();

		for (Player player : players) {
			temp.add(player.getPlayerName());
		}
		playerNumber = temp.size();
		// generate random order
		while (gamers.size() < playerNumber) {
			if (temp.size() > 1) {
				// find a random player to add to the gamers list
				random = (int) (Math.random() * (double) temp.size()-1);
				gamers.add(temp.get(random));
				temp.remove(random);
			} else {
				// adds the last player
				gamers.add(temp.get(0));
			}
		}
		try {
			observedModel.setOrder(gamers);
			observedModel.changeTurn();
		} catch (IllegalArgumentException | WrongPhaseException e) {
			throw new AssertionError("Generate order called in a phase different from the setup");
		}
	}
	/**
	 *
	 * @param playerColors
	 * @throws BadRequestException
	 * @throws WrongPhaseException
	 */
	public void changeColor(NetColorPreparation playerColors) throws BadRequestException, WrongPhaseException {
		List<Color> chosenColors = observedModel.getPlayers().stream().filter((player) -> {try { player.getWorker1(); return true; } catch (IllegalStateException e) { return false; }}).map((player) -> player.getWorker1().color).collect(Collectors.toList());
		if (chosenColors.contains(playerColors.color) || !observedModel.getPlayerTurn().getPlayerName().equals(playerColors.player) || !playerColors.message.equals(Constants.COLOR_IN_CHOICE) || !observedModel.getPlayers().contains(observedModel.getPlayerByName(playerColors.player)) || !Constants.COLOR_COLORS.contains(playerColors.color)) {
			throw new BadRequestException();
		} else {
			try {
				observedModel.setPlayerColor(playerColors.player,playerColors.color);
			} catch (WrongPhaseException | IllegalArgumentException e) {
				throw new BadRequestException();
			}
		}
	}
	/**
	 *
	 * @param request
	 * @return is true if the god selection phase is ended, false if not
	 * @throws BadRequestException
	 * @throws WrongPhaseException
	 */
	public void handleGodMessage(NetDivinityChoice request) throws BadRequestException, WrongPhaseException {
		if (request == null || observedModel.getPhase().getPhase() != Phase.GODS) {
			throw new BadRequestException();
		} else {
			try {
				observedModel.getPlayerByName(request.player);
			} catch (IllegalArgumentException e) {
				throw new BadRequestException();
			}
		}
		if (Constants.GODS_IN_GAME_GODS.equals(request.message)) {
			// if the player is the challenger it changes the gods for this game
			if (!observedModel.getPlayers().get(0).getPlayerName().equals(request.player) || observedModel.getPhase().getGodsPhase() != GodsPhase.CHALLENGER_CHOICE) {
				throw new BadRequestException();
			} else {
				try {
					observedModel.setGameGods(request.getDivinities());
				} catch (IllegalArgumentException e) {
					throw new BadRequestException();
				}
			}
		} else if (Constants.GODS_IN_CHOICE.equals(request.message)) {
			// if the god is already chosen it throws an exception, if not it sets the god
			List<String> cardsChosen = observedModel.getPlayers().stream().filter((player) -> { try { player.getCard(); return true; } catch (IllegalStateException e) { return false; } }).map((player) -> player.getCard().getName().toUpperCase()).collect(Collectors.toList());
			if (cardsChosen.contains(request.divinity) || observedModel.getPhase().getGodsPhase() != GodsPhase.GODS_CHOICE) {
				throw new BadRequestException();
			} else {
				observedModel.setPlayerGod(request.player,request.divinity);
			}
		} else {
			// if the player is the challenger it chooses the starter player
			if (!observedModel.getPlayers().get(0).getPlayerName().equals(request.challenger) || observedModel.getPhase().getGodsPhase() != GodsPhase.STARTER_CHOICE) {
				throw new BadRequestException();
			} else {
				try {
					observedModel.setStarter(request.player);
				} catch (IllegalStateException e) {
					throw new BadRequestException();
				}
			}
		}
	}
	/**
	 *
	 * @param positions
	 * @throws BadRequestException
	 */
	public void positionWorkers(NetGameSetup positions) throws BadRequestException {
		Map gameMap = observedModel.getMap();
		// if there aren't workers in that position it set the workers in that position
		if (!positions.isWellFormed() || !observedModel.getPlayerTurn().equals(observedModel.getPlayerByName(positions.player))) {
			throw new BadRequestException();
		} else if (gameMap.getCell(positions.worker1.getFirst(),positions.worker1.getSecond()).getWorker() != null || gameMap.getCell(positions.worker2.getFirst(),positions.worker2.getSecond()).getWorker() != null || positions.worker1.equals(positions.worker2)) {
			throw new BadRequestException();
		} else {
			try {
				observedModel.setWorkerPositions(positions);
			} catch (IllegalArgumentException | WrongPhaseException e) {
				throw new BadRequestException();
			}
		}
	}
}
