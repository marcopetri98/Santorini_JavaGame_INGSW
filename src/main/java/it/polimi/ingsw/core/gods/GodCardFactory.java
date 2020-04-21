package it.polimi.ingsw.core.gods;

import it.polimi.ingsw.core.Player;
import it.polimi.ingsw.util.Constants;

public class GodCardFactory {
	public static GodCard createGodCard(String godName, Player owner) throws IllegalArgumentException {
		if (godName == null || owner == null || !Constants.GODS_GOD_NAMES.contains(godName)) {
			throw new IllegalArgumentException();
		}
		switch (godName) {
			case "Apollo":
				return new Apollo(owner);

			case "Artemis":
				return new Artemis(owner);

			case "Athena":
				return new Athena(owner);

			case "Atlas":
				return new Atlas(owner);

			case "Demeter":
				return new Demeter(owner);

			case "Hephaestus":
				return new Hephaestus(owner);

			case "Minotaur":
				return new Minotaur(owner);

			case "Pan":
				return new Pan(owner);

			case "Prometheus":
				return new Prometheus(owner);

			default:
				throw new AssertionError("The god that is wanted to be created is correct and not handled");
		}
	}
}
