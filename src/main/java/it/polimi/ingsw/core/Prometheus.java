package it.polimi.ingsw.core;
import java.util.List;
import java.util.ArrayList;

public class Prometheus implements GodCard{
	private int typeGod = 2;
	private Player owner;
	int numPlayer = 4;
	String name = "Prometheus";
	String description = "Your Turn: If your Worker does not move up, it may build both before and after moving.";
	List<Move> moves;
	List<Build> builds;

	public Prometheus(Player player){
		this.owner = player;
	}

	public Prometheus(){
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
	 * @param m represents the map
	 * @param w represents the worker moved by the player during this turn
	 * @param type represents the typeBuild of this particular GodCard: 0 stands for a "simple construction", 1 for a "conditioned construction"
	 * @return the cells where the Player's Worker may build according to general game rules and his GodCard power
	 */
	//This is the "default" building option
	public List<Build> checkBuild(Map m, Worker w, int type){
		int y = m.getY(w.getPos());
		int x = m.getX(w.getPos());
		builds = new ArrayList<>();
		for(int i = -1; i <= 1; i++) {   //i->x   j->y     x1, y1 all the cells where I MAY build
			int x1 = x + i;
			for (int j = -1; j <= 1; j++) {
				int y1 = y + j;

				if (x != x1 || y != y1) { //I shall not build where I am
					if (0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4) {   //Check that I am inside the map
						if (-1 <= (x1 - x) && (x1 - x) <= 1 && -1 <= (y1 - y) && (y1 - y) <= 1) {  //Check that distance from original cell is <= 1
							if (m.getCell(x1, y1).getWorker() == null) {   //Check there isn't any worker on the cell
								if (!m.getCell(x1, y1).getBuilding().getDome()) {   //Check there is NO dome
									if(m.getCell(x1, y1).getBuilding().getLevel() <= 2)	builds.add(new Build(w, m.getCell(x1, y1), false, 0));		//adds possible build: one block build
									else if(m.getCell(x1, y1).getBuilding().getLevel() == 3) builds.add(new Build(w, m.getCell(x1, y1), true, 0));	//adds possible build: single dome on top of three level building
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
	 * @param m represents the map
	 * @param w represents the worker moved by the player during this turn
	 * @param type represents the typeMove of this particular GodCard: 0 stands for a "simple move", 1 for a "conditioned move", 2 for a "defeat move", 3 for a "victory move"
	 * @return the cells where the Player's Worker may move according to general game rules and his GodCard power
	 */
	//This is the "default" movement option
	public List<Move> checkMove(Map m, Worker w, int type){   //worker->activeworker
		int y = m.getX(w.getPos());
		int x = m.getY(w.getPos());
		moves = new ArrayList<>();
		for(int i = -1; i <= 1; i++){   //i->x   j->y     x1, y1 all the cells where I MAY move
			int x1 = x + i;
			for(int j = -1; j <= 1; j++){
				int y1 = y + j;

				if(x != x1 || y != y1){ //I shall not move where I am already
					if(0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4){   //Check that I am inside the map
						if(-1 <= (x1-x) && (x1-x) <= 1 && -1 <= (y1-y) && (y1-y) <=1){  //Check that distance from original is cell <= 1: useless?
							if(m.getCell(x1, y1).getBuilding().getLevel() - m.getCell(x, y).getBuilding().getLevel() == 0){ //Check worker doesn't move up
								if(!m.getCell(x1, y1).getBuilding().getDome()){   //Check there is NO dome
									if (m.getCell(x1, y1).getWorker() == null) {   //Check there isn't any worker on the cell
										moves.add(new Move(0, m.getCell(x, y), m.getCell(x1, y1), w));
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
}