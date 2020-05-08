package it.polimi.ingsw.core.gods;

import it.polimi.ingsw.core.Player;
import it.polimi.ingsw.util.Constants;

public class GodCardFactory {
	public static GodCard createGodCard(String godName) throws IllegalArgumentException {
		if (godName == null || !Constants.GODS_GOD_NAMES.contains(godName.toUpperCase())) {
			throw new IllegalArgumentException();
		}
		switch (godName) {
			case "APOLLO":
				return new Apollo();

			case "ARTEMIS":
				return new Artemis();

			case "ATHENA":
				return new Athena();

			case "ATLAS":
				return new Atlas();

			case "DEMETER":
				return new Demeter();

			case "HEPHAESTUS":
				return new Hephaestus();

			case "MINOTAUR":
				return new Minotaur();

			case "PAN":
				return new Pan();

			case "PROMETHEUS":
				return new Prometheus();

			default:
				throw new AssertionError("The god that is wanted to be created is correct and not handled");
		}
	}
}
