package it.polimi.ingsw.core.gods;
import it.polimi.ingsw.core.*;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.util.exceptions.NoBuildException;
import it.polimi.ingsw.util.exceptions.NoMoveException;

import java.util.List;
import java.util.ArrayList;

public class Pan implements GodCard {

	//CODICE PAN
	private Player owner;
	public final TypeGod typeGod = TypeGod.SIMPLE_GOD;
	public final List<Integer> numPlayer = List.of(2,3,4);
	public final String name = "Pan";
	public final String description = "Win Condition: You also win if your Worker moves down two or more levels.";

	public Pan(Player player){
		this.owner = player;
	}

	public Pan(){
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
	 * @throws NoBuildException so that controller knows it must use the default action
	 */
	public List<Build> checkBuild(Map m, Worker w, Turn turn) throws NoBuildException {
		throw new NoBuildException();
	}

	/**
	 * @param m represents the map
	 * @param w represents the worker moved by the player during this turn
	 * @return the cells where the Player's Worker may move according to general game rules and his GodCard power
	 */
	public List<Move> checkMove(Map m, Worker w, Turn turn) throws NoMoveException {   //worker->activeworker
		// if the phase isn't the move phase it throws a move exception
		if (turn.getGamePhase() != GamePhase.MOVE) {
			throw new NoMoveException();
		}

		int y = m.getY(w.getPos());
		int x = m.getX(w.getPos());
		List<Move> moves = new ArrayList<>();
		for(int i = -1; i <= 1; i++){   //i->x   j->y     x1, y1 all the cells where I MAY move
			int x1 = x + i;
			for(int j = -1; j <= 1; j++){
				int y1 = y + j;

				if(x != x1 || y != y1){ //I shall not move where I am already
					if(0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4){   //Check that I am inside the map
						if(-1 <= (x1-x) && (x1-x) <= 1 && -1 <= (y1-y) && (y1-y) <=1){  //Check that distance from original is cell <= 1: useless?
							if(!m.getCell(x1, y1).getBuilding().getDome()){   //Check there is NO dome
								if (m.getCell(x1, y1).getWorker() == null) {   //Check there isn't any worker on the cell
									if (m.getCell(x, y).getBuilding().getLevel() - m.getCell(x1, y1).getBuilding().getLevel() >= 2) {
										//Check height difference is at least 2 levels (moving down) and adds type 3 move
										moves.add(new Move(TypeMove.VICTORY_MOVE, m.getCell(x, y), m.getCell(x1, y1), w));
									} else {
										//else adds type 0 [simple] move, still doable
										moves.add(new Move(TypeMove.SIMPLE_MOVE, m.getCell(x, y), m.getCell(x1, y1), w));
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