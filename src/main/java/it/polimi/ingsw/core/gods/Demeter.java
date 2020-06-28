package it.polimi.ingsw.core.gods;
import it.polimi.ingsw.core.*;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.util.exceptions.NoBuildException;
import it.polimi.ingsw.util.exceptions.NoMoveException;

import java.util.List;
import java.util.ArrayList;

/**
 * This is Demeter GodCard, it has the specific implementation of the methods to calculate the moves and builds that each worker can do
 */
public class Demeter extends GodCard {

	//DEMETER CODE
	private Player owner;
	public final TypeGod typeGod = TypeGod.SIMPLE_GOD;
	public final List<Integer> numPlayer = List.of(2,3,4);
	public final String name = "Demeter";
	public final String description = "Your Build: Your Worker may build one additional time, but not on the same space.";

	/**
	 * Constructor of the class
	 * @param player the owner of the card
	 */
	public Demeter(Player player){
		this.owner = player;
	}

	/**
	 * Empty constructor of the class
	 */
	public Demeter(){
		this.owner = null;
	}

	//GETTERS
	/**
	 * Getter of the number of players that can play if this card is used
	 * @return the number of players
	 */
	public List<Integer> getNumPlayer(){
		return numPlayer;
	}

	/**
	 * Getter of the owner of the card
	 * @return the owner of the card
	 */
	public Player getOwner(){
		return owner;
	}

	/**
	 * Getter of the type of god:
	 * @return the typeGod
	 */
	public TypeGod getTypeGod(){
		return typeGod;
	}

	/**
	 * Getter of the name of the card
	 * @return the name of the GodCard
	 */
	public String getName(){
		return name;
	}

	/**
	 * Getter of the description
	 * @return the GodCard description
	 */
	public String getDescription(){
		return description;
	}



	//CARD-SPECIFIC IMPLEMENTATION OF CHECKBUILD AND CHECKMOVE
	/**
	 * This is the specific implementation of the building option for this GodCard
	 * @param m represents the map
	 * @param w represents the worker moved by the player during this turn
	 * @param turn the phase of the game
	 * @return the cells where the Player's Worker may build according to general game rules and his GodCard power
	 * @throws NoBuildException if you can't build because of a wrong phase
	 */
	@Override
	public List<Build> checkBuild(Map m, Worker w, Turn turn) throws NoBuildException {
		// if it isn't during the building phase this god has no power and throws an exception
		if (turn.getGamePhase() != GamePhase.BUILD) {
			throw new NoBuildException();
		}

		int y = m.getY(w.getPos());
		int x = m.getX(w.getPos());
		List<Build> tempBuilds = new ArrayList<>();
		List<Build> builds = new ArrayList<>();
		for(int i = -1; i <= 1; i++) {   //i->x   j->y     x1, y1 all the cells where I MAY build
			int x1 = x + i;
			for (int j = -1; j <= 1; j++) {
				int y1 = y + j;

				if (x != x1 || y != y1) { //I shall not build where I am
					if (0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4) {   //Check that I am inside the map
						if (-1 <= (x1 - x) && (x1 - x) <= 1 && -1 <= (y1 - y) && (y1 - y) <= 1) {  //Check that distance from original cell is <= 1
							if (m.getCell(x1, y1).getWorker() == null) {   //Check there isn't any worker on the cell
								if (!m.getCell(x1, y1).getBuilding().getDome()) {   //Check there is NO dome
									if(m.getCell(x1, y1).getBuilding().getLevel() <= 2) {
										builds.add(new Build(w, m.getCell(x1, y1), false, TypeBuild.SIMPLE_BUILD));        //adds possible build: only one block
										tempBuilds.add(new Build(w, m.getCell(x1, y1), false, TypeBuild.SIMPLE_BUILD));
									}
									else if(m.getCell(x1, y1).getBuilding().getLevel() == 3) {
										builds.add(new Build(w, m.getCell(x1, y1), true, TypeBuild.SIMPLE_BUILD));    //adds possible build: only dome
										tempBuilds.add(new Build(w, m.getCell(x1, y1), true, TypeBuild.SIMPLE_BUILD));
									}
								}
							}
						}
					}
				}
			}
		}
		//adds every single allowed permutation of build construction to "builds" arraylist;
		//tempBuilds is just a temporary list in order to cycle through the elements of the original arraylist
		for(Build b : tempBuilds){
			for(Build b1 : tempBuilds) {
				if(b.getCell() != b1.getCell()) {
					Build secondBuild = b.copy();
					secondBuild.setCondition(b1.copy());
					secondBuild.setTypeBuild(TypeBuild.CONDITIONED_BUILD);
					builds.add(secondBuild);
				}
			}
		}

		return builds;
	}
}