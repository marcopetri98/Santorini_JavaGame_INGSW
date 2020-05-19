package it.polimi.ingsw.core.gods;
import it.polimi.ingsw.core.*;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.util.exceptions.NoBuildException;
import it.polimi.ingsw.util.exceptions.NoMoveException;

import java.util.List;
import java.util.ArrayList;

public class Atlas extends GodCard {

	//CODICE ATLAS
	private Player owner;
	public final TypeGod typeGod = TypeGod.SIMPLE_GOD;
	public final List<Integer> numPlayer = List.of(2,3,4);
	public final String name = "Atlas";
	public final String description = "Your Build: Your Worker may build a dome at any level.";

	public Atlas(Player player){
		this.owner = player;
	}

	public Atlas(){
		this.owner = null;
	}

	//GETTERS
	public List<Integer> getNumPlayer(){
		return numPlayer;
	}

	public Player getOwner(){
		return owner;
	}

	public TypeGod getTypeGod(){
		return typeGod;
	}

	public String getName(){
		return name;
	}

	public String getDescription(){
		return description;
	}



	//CARD-SPECIFIC IMPLEMENTATION OF CHECKBUILD AND CHECKMOVE
	/**
	 * @param m represents the map
	 * @param w represents the worker moved by the player during this turn
	 * @return the cells where the Player's Worker may build according to general game rules and his GodCard power
	 */
	@Override
	public List<Build> checkBuild(Map m, Worker w, Turn turn) throws NoBuildException {
		// if it isn't during the building phase this god has no power and throws an exception
		if (turn.getGamePhase() != GamePhase.BUILD) {
			throw new NoBuildException();
		}

		int y = m.getY(w.getPos());
		int x = m.getX(w.getPos());
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
									if (m.getCell(x1, y1).getBuilding().getLevel() <= 3) { //Check height building is <=3
										//don't need to check else, because Atlas can build Dome at any level
										builds.add(new Build(w, m.getCell(x1, y1), true, TypeBuild.SIMPLE_BUILD));
										builds.add(new Build(w, m.getCell(x1, y1), false, TypeBuild.SIMPLE_BUILD));	//adds the possibility to build another generic building [no dome]
									}
								}
							}
						}
					}
				}
			}
		}
		return builds;
	}
}