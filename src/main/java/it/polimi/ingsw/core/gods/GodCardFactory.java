package it.polimi.ingsw.core.gods;

import it.polimi.ingsw.core.Player;
import it.polimi.ingsw.util.Constants;

public class GodCardFactory {
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
	public static GodCard createGodCard(String godName, Player owner) throws IllegalArgumentException {
		if (godName == null || !Constants.GODS_GOD_NAMES.contains(godName.toUpperCase())) {
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
