package it.polimi.ingsw.core;
import it.polimi.ingsw.exceptions.NoMoveException;

import java.util.List;
import java.util.ArrayList;

public class Demeter implements GodCard{
	private int typeGod = 0;
	private Player owner;
	int numPlayer = 4;
	String name = "Demeter";
	String description = "Your Build: Your Worker may build one additional time, but not on the same space.\n";
	List<Move> moves;
	List<Build> builds;

	public Demeter(Player player){
		this.owner = player;
	}

	public Demeter(){
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
	 *
	 * @param m The map situation of the match
	 * @param w the worker the player of this turn choose to move
	 * @param type the typeBuild of Demeter is 1. We choose this means that she performs a "conditioned build"
	 * @return the cells where the Player's Worker could move according to general game rules and his God card Power
	 */
	public List<Build> checkBuild(Map m, Worker w, int type){
		int y = w.getPos().getY();
		int x = w.getPos().getX();
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
										addCell(m, w, type, x1, y1, x, y);
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
	 *
	 * @param x1,y1 cell where the player is going to build the first time
	 * @param x,y cell where the worker is
	 */
	private void addCell(Map m, Worker w, int type, int x1, int y1, int x, int y){

		//first build
		if (m.getCell(x1, y1).getBuilding().getLevel() == 3){
			builds.add(new Build(w, m.getCell(x1, y1), true, 0)); //can build a dome
		} else {
			builds.add(new Build(w, m.getCell(x1, y1), false, 0)); //cannot build a dome
		}

		//second build
		for(int i = -1; i <= 1; i++){    //x1,y1 is where i have already build in this turn
			int x2 = x1 + i;
			for(int j = -1; j <= 1; j++){
				int y2 = y1 + j;         //x2,y2 is where i want to build the second time

				if(x2 != x1 || y2 != y1){ //I shall not build where I built already
					if(x2 != x || y2 != y){	//I shall not build where I am
						if(0 <= x2 && x2 <= 4 && 0 <= y2 && y2 <= 4){	//Check I am inside the boundaries of the map
							if(-1 <= (x2-x) && (x2-x) <= 1 && -1 <= (y2-y) && (y2-y) <=1){  //Check that distance from my position is however <= 1
								if (m.getCell(x2, y2).getWorker() == null) {   //Check there isn't any worker on the cell
									if (!m.getCell(x2, y2).getBuilding().getDome()) {   //Check there is NO dome
										if (m.getCell(x2, y2).getBuilding().getLevel() == 3) { //can build dome in x2y2
											if(m.getCell(x1, y1).getBuilding().getDome()) { //x1y1 built a dome
												Build firstBuild = new Build(w, m.getCell(x1, y1), true, 0);
												firstBuild.setCondition(new Build(w, m.getCell(x2, y2), true, 0));
												builds.add(firstBuild);
											} else {
												Build firstBuild = new Build(w, m.getCell(x1, y1), false, 0);
												firstBuild.setCondition(new Build(w, m.getCell(x2, y2), true, 0));
												builds.add(firstBuild);
											}
										} else {
											if(m.getCell(x1, y1).getBuilding().getDome()) {
												Build firstBuild = new Build(w, m.getCell(x1, y1), true, 0);
												firstBuild.setCondition(new Build(w, m.getCell(x2, y2), false, 0));
												builds.add(firstBuild);
											} else {
												Build firstBuild = new Build(w, m.getCell(x1, y1), false, 0);
												firstBuild.setCondition(new Build(w, m.getCell(x2, y2), false, 0));
												builds.add(firstBuild);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @return null, because Demeter power isn't about moves.
	 */
	public List<Move> checkMove(Map m, Worker w, int type) throws NoMoveException {
		throw new NoMoveException();
	}

}

