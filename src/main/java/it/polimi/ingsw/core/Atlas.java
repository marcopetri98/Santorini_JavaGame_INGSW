package it.polimi.ingsw.core;
import it.polimi.ingsw.util.exceptions.NoMoveException;

import java.util.List;
import java.util.ArrayList;

public class Atlas implements GodCard{
	private int typeGod = 0;
	private Player owner;
	int numPlayer = 4;
	String name = "Atlas";
	String description = "Your Build: Your Worker may build a dome at any level.";
	List<Move> moves;
	List<Build> builds;

	public Atlas(Player player){
		this.owner = player;
	}

	public Atlas(){
		this.owner = null;
		this.moves = null;
		this.builds = null;
	}

	public int getNumPlayer(){
		return numPlayer;
	}
	public Player getOwner(){
		return owner;
	}
	public int getTypeGod(){
		return typeGod;
	}
	public String getName(){
		return name;
	}
	public String getDescription(){
		return description;
	}

	//TODO: o mettiamo direttamente su GodCard, definendo però tutti i type...?
	/**
	 * @param m represents the map
	 * @param w represents the worker moved by the player during this turn
	 * @param type represents the typeBuild of this particular GodCard: 0 stands for a "simple construction", 1 for a "conditioned construction"
	 * @return the cells where the Player's Worker may build according to general game rules and his GodCard power
	 */
	public List<Build> checkBuild(Map m, Worker w, int type){
		int y = m.getY(w.getPos());
		int x = m.getX(w.getPos());
		moves = new ArrayList<>();
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
										builds.add(new Build(w, m.getCell(x1, y1), true, 0));
										builds.add(new Build(w, m.getCell(x1, y1), false, 0));	//adds the possibility to build another generic building [no dome]
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

	/**
	 * @throws NoMoveException so that controller knows it must use the default action
	 */
	public List<Move> checkMove(Map m, Worker w, int type) throws NoMoveException {
		throw new NoMoveException();
	}
}