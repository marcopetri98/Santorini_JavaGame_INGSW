package it.polimi.ingsw.core;
import it.polimi.ingsw.exceptions.NoBuildException;

import java.util.List;
import java.util.ArrayList;

public class Artemis implements GodCard{

	//CODICE APOLLO
	private int typeGod = 0;
	private Player owner;
	int numPlayer = 4;
	String name = "Artemis";
	String description = "Your Move: Your Worker may move one additional time, but not back to its initial space.";
	List<Move> moves;
	List<Build> builds;

	public Artemis(Player player){
		this.owner = player;
	}

	public Artemis(){
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
					if(0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4){	//Check that I am inside the map
						if(-1 <= (x1-x) && (x1-x) <= 1 && -1 <= (y1-y) && (y1-y) <=1){  //Check that distance from original is cell <= 1: useless?
							if(m.getCell(x1, y1).getBuilding().getLevel() - m.getCell(x, y).getBuilding().getLevel() <= 1){ //Check height difference
								if(!m.getCell(x1, y1).getBuilding().getDome()){   //Check there is NO dome
									if(m.getCell(x1, y1).getWorker() == null){   //Check there is no worker [of ANY player] on cell
										//Demando tutto il check dell'ulteriore cella e l'aggiunta di tutte le celle a un altro metodo addCell
										addCell(m, w, type, x1, y1, x, y);
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

	/**
	 * @param m represents the map
	 * @param w represents the worker moved by the player during this turn
	 * @param type represents the typeMove of this particular GodCard
	 * @param x1,y1 represent the coordinates of the cell where the player may move
	 * @param x,y represent the coordinates of the cell where the worker is
	 */
	private void addCell(Map m, Worker w, int type, int x1, int y1, int x, int y){

		moves.add(new Move(0, m.getCell(x, y), m.getCell(x1, y1), w));	//Adds default move: Artemis can indeed move without using her power

		for(int i = -1; i <= 1; i++){
			int x2 = x1 + i;
			for(int j = -1; j <= 1; j++){
				int y2 = y1 + j;

				if(x2 != x1 || y2 != y1){ //I shall not move where I am already
					if(x2 != x || y2 != y){	//I shall not move where I was at the beginning

						if(0 <= x2 && x2 <= 4 && 0 <= y2 && y2 <= 4){	//Check I am inside the boundaries of the map
							if(-1 <= (x2-x1) && (x2-x1) <= 1 && -1 <= (y2-y1) && (y2-y1) <=1){  //Check that distance from original is cell <= 1: useless?
								if(m.getCell(x2, y2).getBuilding().getLevel() - m.getCell(x1, y1).getBuilding().getLevel() <= 1){ //Check height difference
									if(!m.getCell(x2, y2).getBuilding().getDome()){   //Check there is NO dome
										if(m.getCell(x2, y2).getWorker() == null){   //Check there is no worker on cell
											//Adds a move with another linked move
											Move firstMove = new Move(0, m.getCell(x, y), m.getCell(x1, y1), w);
											firstMove.setCondition(new Move(0, m.getCell(x1, y1), m.getCell(x2, y2), w));
											moves.add(firstMove);
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