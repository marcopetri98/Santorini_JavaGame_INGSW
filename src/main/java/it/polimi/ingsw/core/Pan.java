package it.polimi.ingsw.core;
import it.polimi.ingsw.util.exceptions.NoBuildException;

import java.util.List;
import java.util.ArrayList;

public class Pan implements GodCard{
	private int typeGod = 0;
	private Player owner;
	int numPlayer = 4;
	String name = "Pan";
	String description = "Win Condition: You also win if your Worker moves down two or more levels.";
	List<Move> moves;
	List<Build> builds;

	public Pan(Player player){
		this.owner = player;
	}

	public Pan(){
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

	/**
	 * @throws NoBuildException so that controller knows it must use the default action
	 */
	public List<Build> checkBuild(Map m, Worker w, int type) throws NoBuildException {
		throw new NoBuildException();
	}

	/**
	 * @param m represents the map
	 * @param w represents the worker moved by the player during this turn
	 * @param type represents the typeMove of this particular GodCard: 0 stands for a "simple move", 1 for a "conditioned move", 2 for a "defeat move", 3 for a "victory move"
	 * @return the cells where the Player's Worker may move according to general game rules and his GodCard power
	 */
	public List<Move> checkMove(Map m, Worker w, int type){   //worker->activeworker
		int y = m.getY(w.getPos());
		int x = m.getX(w.getPos());
		moves = new ArrayList<>();
		for(int i = -1; i <= 1; i++){   //i->x   j->y     x1, y1 all the cells where I MAY move
			int x1 = x + i;
			for(int j = -1; j <= 1; j++){
				int y1 = y + j;

				if(x != x1 || y != y1){ //I shall not move where I am already
					if(0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4){   //Check that I am inside the map
						if(-1 <= (x1-x) && (x1-x) <= 1 && -1 <= (y1-y) && (y1-y) <=1){  //Check that distance from original is cell <= 1: useless?
							if(!m.getCell(x1, y1).getBuilding().getDome()){   //Check there is NO dome
								if (m.getCell(x1, y1).getWorker() == null) {   //Check there isn't any worker on the cell
									if(m.getCell(x, y).getBuilding().getLevel() - m.getCell(x1, y1).getBuilding().getLevel() >= 2) moves.add(new Move(3, m.getCell(x, y), m.getCell(x1, y1), w));	//Check height difference is at least 2 levels (moving down) and adds type 3 move
									else moves.add(new Move(0, m.getCell(x, y), m.getCell(x1, y1), w));	//else adds type 0 [simple] move, still doable
								}
							}
						}
					}
				}
			}
		}

		return moves;
	}
}