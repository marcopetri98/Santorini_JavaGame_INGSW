package it.polimi.ingsw.core.gods;

import it.polimi.ingsw.core.Player;
import it.polimi.ingsw.util.Constants;

/**
 * This class is a class which implements the Factory design pattern, it is a class which aim is to create GodCards types given some attributes without that the owner knows how it creates the cards.
 */
public class GodCardFactory {
	/**
	 * This method creates a {@link it.polimi.ingsw.core.gods.GodCard} without an owner which dynamic type is chosen by the {@code godName} parameter
	 * @param godName god's name
	 * @return a {@link it.polimi.ingsw.core.gods.GodCard}
	 * @throws IllegalArgumentException if {@code godName} is not a name of a game god or if {@code godName} is null
	 */
	public static GodCard createGodCard(String godName) throws IllegalArgumentException {
		if (godName == null || !Constants.GODS_GOD_NAMES.contains(godName.toUpperCase())) {
			throw new IllegalArgumentException();
		}
		return switch (godName) {
			case "APOLLO" -> new Apollo();
			case "ARTEMIS" -> new Artemis();
			case "ATHENA" -> new Athena();
			case "ATLAS" -> new Atlas();
			case "DEMETER" -> new Demeter();
			case "HEPHAESTUS" -> new Hephaestus();
			case "MINOTAUR" -> new Minotaur();
			case "PAN" -> new Pan();
			case "PROMETHEUS" -> new Prometheus();
			default -> throw new AssertionError("The god that is wanted to be created is correct and not handled");
		};
	}

	/**
	 * This method creates a {@link it.polimi.ingsw.core.gods.GodCard} with an owner which dynamic type is chosen by the {@code godName} parameter and owner specified by the {@code owner}.
	 * @param godName god's name
	 * @param owner card's owner
	 * @return a {@link it.polimi.ingsw.core.gods.GodCard}
	 * @throws IllegalArgumentException if {@code godName} is not a name of a game god, if {@code godName} is null or if {@code owner} is null
	 */
	public static GodCard createGodCard(String godName, Player owner) throws IllegalArgumentException {
		if (godName == null || owner == null || !Constants.GODS_GOD_NAMES.contains(godName.toUpperCase())) {
			throw new IllegalArgumentException();
		}
		return switch (godName) {
			case "APOLLO" -> new Apollo(owner);
			case "ARTEMIS" -> new Artemis(owner);
			case "ATHENA" -> new Athena(owner);
			case "ATLAS" -> new Atlas(owner);
			case "DEMETER" -> new Demeter(owner);
			case "HEPHAESTUS" -> new Hephaestus(owner);
			case "MINOTAUR" -> new Minotaur(owner);
			case "PAN" -> new Pan(owner);
			case "PROMETHEUS" -> new Prometheus(owner);
			default -> throw new AssertionError("The god that is wanted to be created is correct and not handled");
		};
	}
}
