package it.polimi.ingsw.core.gods;

import it.polimi.ingsw.core.Player;
import it.polimi.ingsw.util.Constants;

public class GodCardFactory {
	public static GodCard createGodCard(String godName) throws IllegalArgumentException {
		if (godName == null || !Constants.GODS_GOD_NAMES.contains(godName.toUpperCase())) {
			throw new IllegalArgumentException();
		}
		switch (godName) {
			case "Apollo":
				return new Apollo();

			case "Artemis":
				return new Artemis();

			case "Athena":
				return new Athena();

			case "Atlas":
				return new Atlas();

			case "Demeter":
				return new Demeter();

			case "Hephaestus":
				return new Hephaestus();

			case "Minotaur":
				return new Minotaur();

			case "Pan":
				return new Pan();

			case "Prometheus":
				return new Prometheus();

			default:
				throw new AssertionError("The god that is wanted to be created is correct and not handled");
		}
	}
}
