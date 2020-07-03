package it.polimi.ingsw.core.gods;
import it.polimi.ingsw.core.*;
import it.polimi.ingsw.core.Map;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.util.exceptions.NoBuildException;
import it.polimi.ingsw.util.exceptions.NoMoveException;

import java.util.*;

/**
 * This is Athena GodCard, it has the specific implementation of the methods to calculate the moves and builds that each worker can do
 */
public class Athena extends GodCard implements Observer {

	//ATHENA CODE
	private Player owner;
	public final TypeGod typeGod = TypeGod.OTHER_TURN_GOD;
	public final List<Integer> numPlayer = List.of(2,3,4);
	public final String name = "Athena";
	public final String description = "Opponent’s Turn: If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn.";

	/**
	 * Constructor of the class
	 * @param player the owner of the card
	 */
	public Athena(Player player) { this.owner = player; }

	/**
	 * Empty constructor of the class
	 */
	public Athena(){
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



	//OBSERVER-RELATED ATTRIBUTES AND METHODS
	private boolean wentUp = false;

	public void setWentUp(boolean wentUp) {
		this.wentUp = wentUp;
	}

	/**
	 * Overridden update method of the Observer, it is the core of Athena's power
	 * @param obs the observable object
	 * @param arg the cells where Athena was and will be
	 */
	public void update(Observable obs, Object arg){
		Worker modifiedWorker = (Worker) obs;
		Cell[] positions = (Cell[]) arg;
		if(modifiedWorker == owner.getWorker1() || modifiedWorker == owner.getWorker2()){	//Then the worker returned as argument of the update method is held by the owner of Athena
			if ((positions[0] == null && positions[1] != null && positions[1].getBuilding().getLevel() >= 1) || (positions[0] != null && positions[1] != null && positions[1].getBuilding().getLevel() - positions[0].getBuilding().getLevel() >= 1)) {
				// athena power is active only if the worker moves up in the player's turn
				if (modifiedWorker.owner.isWorkerLocked()) {
					wentUp = true;
				}
			} else {
				wentUp = false;
			}
		}
	}

	//CARD-SPECIFIC IMPLEMENTATION OF CHECKBUILD AND CHECKMOVE
	/**
	 * This is the specific implementation of the movement option for this GodCard
	 * @param m represents the map
	 * @param w represents the worker moved by the player during this turn
	 * @param turn the phase of the game
	 * @return the cells where the Player's Worker may move according to general game rules and his GodCard power
	 * @throws NoMoveException if the phase is wrong
	 */
	@Override
	public List<Move> checkMove(Map m, Worker w, Turn turn) throws NoMoveException {
		// if the phase isn't the move phase it throws a move exception
		if (turn.getGamePhase() != GamePhase.MOVE) {
			throw new NoMoveException();
		}

		int myHeight = w.getPos().getBuilding().getLevel();
		List<Move> moves = new ArrayList<>();

		if(wentUp){		//Athena's power's applied only if wentUp flag is true
			for(int x1 = 0; x1 <= 4; x1++){   //x1, y1 all the cells where I mustn't move: I have to check the whole map and return all the cells higher than mine for 1 or more blocks
				for(int y1 = 0; y1 <= 4; y1++){
					if(m.getCell(x1, y1).getBuilding().getLevel() - myHeight >= 1) {
						moves.add(new Move(TypeMove.FORBIDDEN_MOVE, w.getPos(), m.getCell(x1, y1), w));
					}
				}
			}

			int y = m.getY(w.getPos());
			int x = m.getX(w.getPos());
			int x3, y3;
			HashMap<Cell,Boolean> cellsPossible = new HashMap<>();

			for(int i = -1; i <= 1; i++){   //i->x   j->y     x1, y1 all the cells where I MAY move
				int x1 = x + i;
				for(int j = -1; j <= 1; j++){
					int y1 = y + j;

					if(x != x1 || y != y1){ //I shall not move where I am already
						if(0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4){	//Check that I am inside the map
							if(-1 <= (x1-x) && (x1-x) <= 1 && -1 <= (y1-y) && (y1-y) <=1){  //Check that distance from original is cell <= 1: useless?
								if(m.getCell(x1, y1).getBuilding().getLevel() - m.getCell(x, y).getBuilding().getLevel() <= 0){ //Check height difference
									if(!m.getCell(x1, y1).getBuilding().getDome()){   //Check there is NO dome
										if(m.getCell(x1, y1).getWorker() == null){   //Check there is no worker [of ANY player] on cell
											//Demando tutto il check dell'ulteriore cella e l'aggiunta di tutte le celle a un altro metodo addCell
											if (cellsPossible.containsKey(m.getCell(x1, y1))) {
												if (cellsPossible.get(m.getCell(x1, y1)) == true) {
													cellsPossible.replace(m.getCell(x1, y1), false);
												}
											} else {
												cellsPossible.put(m.getCell(x1, y1),false);
											}
											addCell(m, w, x1, y1, x, y, cellsPossible);
										}
									}
								} else {
									for (int k = -1; k <= 1; k++) {
										x3 = x1+k;
										for (int h = -1; h <= 1; h++) {
											y3 = y1+h;

											if(0 <= x3 && x3 <= 4 && 0 <= y3 && y3 <= 4) {    //Check that I am inside the map
												if (!m.getCell(x3, y3).getBuilding().getDome()) {   //Check there is NO dome
													if (m.getCell(x3, y3).getWorker() == null) { //Check there is no worker [of ANY player] on cell
														if (!cellsPossible.containsKey(m.getCell(x3, y3))) {
															cellsPossible.put(m.getCell(x3, y3), true);
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
			}
			for (Cell cell : cellsPossible.keySet()) {
				if (cellsPossible.get(cell) == true) {
					moves.add(new Move(TypeMove.FORBIDDEN_MOVE, w.getPos(), cell, w));
				}
			}
		}

		return moves;
	}

	/**
	 * This method checks for cells more distant than a cell and if there isn't a path without moving up the worker can move there
	 * @param m represents the map
	 * @param w represents the worker moved by the player during this turn
	 * @param x1 represent x the coordinate of the cell where the player may move
	 * @param y1 represent y the coordinate of the cell where the player may move
	 * @param x represent x the coordinate of the cell where the worker is
	 * @param y represent y the coordinate of the cell where the worker is
	 * @param cellsPossible the map of the moves found before the addCell is called
	 */
	private void addCell(Map m, Worker w, int x1, int y1, int x, int y, HashMap<Cell,Boolean> cellsPossible) {
		for (int i = -1; i <= 1; i++) {
			int x2 = x1 + i;
			for (int j = -1; j <= 1; j++) {
				int y2 = y1 + j;

				if (x2 != x1 || y2 != y1) { //I shall not move where I am already
					if (x2 != x || y2 != y) {    //I shall not move where I was at the beginning
						if (0 <= x2 && x2 <= 4 && 0 <= y2 && y2 <= 4) {    //Check I am inside the boundaries of the map
							if (-1 <= (x2 - x1) && (x2 - x1) <= 1 && -1 <= (y2 - y1) && (y2 - y1) <= 1) {  //Check that distance from original is cell <= 1: useless?
								if (m.getCell(x2, y2).getBuilding().getLevel() - m.getCell(x1, y1).getBuilding().getLevel() >= 1) { //Check height difference
									if (!m.getCell(x2, y2).getBuilding().getDome()) {   //Check there is NO dome
										if (m.getCell(x2, y2).getWorker() == null) {   //Check there is no worker on cell
											if (!cellsPossible.containsKey(m.getCell(x2, y2))) {
												cellsPossible.put(m.getCell(x2, y2),true);
											}
										}
									}
								} else {
									if (!m.getCell(x2, y2).getBuilding().getDome()) {   //Check there is NO dome
										if (m.getCell(x2, y2).getWorker() == null) {   //Check there is no worker on cell
											if (!cellsPossible.containsKey(m.getCell(x2, y2))) {
												cellsPossible.put(m.getCell(x2, y2),false);
											} else {
												if (cellsPossible.get(m.getCell(x2, y2)) == true) {
													cellsPossible.replace(m.getCell(x2, y2),false);
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
	}
}