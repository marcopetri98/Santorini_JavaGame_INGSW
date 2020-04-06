package it.polimi.ingsw.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Athena implements GodCard, Observer {
	private int typeGod = 1;
	private Player owner;
	int numPlayer = 4;
	String name = "Athena";
	String description = "Opponent’s Turn: If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn.";
	List<Move> moves;
	List<Build> builds;

	public Athena(Player player) { this.owner = player; }

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

	private boolean wentUp = false;

	public void setActive(){
		wentUp = true;
	}
	public void setInactive(){
		wentUp = false;
	}

	/**
	 *
	 * @param m The map situation of the match
	 * @param w the worker the player of this turn choose to move
	 * @param type the typeMove of Prometheus is 2. We choose this means that she performs a "losing move"
	 * @return the cells where the Player's Worker moved up
	 */
	public List<Move> checkMove(Map m, Worker w, int type){
		int y = w.getPos().getY();
		int x = w.getPos().getX();
		moves = new ArrayList<>();
		for(int i = -1; i <= 1; i++){   //i->x   j->y     x1, y1 all the cells where I MAY move
			int x1 = x + i;
			for(int j = -1; j <= 1; j++){
				int y1 = y + j;

				if(x != x1 || y != y1){ //I shall not move where I am already
					if(0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4){   //Check that I am inside the map
						if(-1 <= (x1-x) && (x1-x) <= 1 && -1 <= (y1-y) && (y1-y) <=1){  //Check that distance from original is cell <= 1: useless?
							if(m.getCell(x1, y1).getBuilding().getLevel() - m.getCell(x, y).getBuilding().getLevel() == 1){ //Check height difference is 1 (moving up)
								//TODO: how does controlorre recognizes it is a voluntary move?
								if(!m.getCell(x1, y1).getBuilding().getDome()){   //Check there is NO dome
									if (m.getCell(x1, y1).getWorker() == null) {   //Check there isn't any worker on the cell
										moves.add(new Move(3, m.getCell(x, y), m.getCell(x1, y1), w));
									}
								}
							}
						}
					}
				}
			}
		}

		return moves;
	}


	public List<Build> checkBuild(Map m, Worker w, int type) {
		//TODO: oppure lista vuota???
		return null;
	}

	@Override
	public void update(Observable o, Object arg) {
		//setting wentup..?
	}
}