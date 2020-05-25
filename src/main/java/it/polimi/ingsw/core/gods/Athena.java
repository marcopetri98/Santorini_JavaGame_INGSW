package it.polimi.ingsw.core.gods;

import it.polimi.ingsw.core.*;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.util.exceptions.NoBuildException;
import it.polimi.ingsw.util.exceptions.NoMoveException;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Athena extends GodCard implements Observer {

	//CODICE ATHENA
	private Player owner;
	public final TypeGod typeGod = TypeGod.OTHER_TURN_GOD;
	public final List<Integer> numPlayer = List.of(2,3,4);
	public final String name = "Athena";
	public final String description = "Opponent’s Turn: If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn.";

	public Athena(Player player) { this.owner = player; }

	public Athena(){
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



	//OBSERVER-RELATED ATTRIBUTES AND METHODS
	private boolean wentUp = false;

	public void setWentUp(boolean wentUp) {
		this.wentUp = wentUp;
	}

	//Overridden update method of the Observer
	public void update(Observable obs, Object arg){
		Worker modifiedWorker = (Worker) obs;
		Cell[] positions = (Cell[]) arg;
		if(modifiedWorker == owner.getWorker1() || modifiedWorker == owner.getWorker2()){	//Then the worker returned as argument of the update method is held by the owner of Athena
			if ((positions[1] != null && positions[1].getBuilding().getLevel() >= 1) || (positions[0] != null && positions[1] != null && positions[1].getBuilding().getLevel() - positions[0].getBuilding().getLevel() >= 1)) {
				wentUp = true;
			} else {
				wentUp = false;
			}
		}
	}

	//CARD-SPECIFIC IMPLEMENTATION OF CHECKBUILD AND CHECKMOVE
	/**
	 * @param m represents the map
	 * @param w represents the worker moved by the player during this turn
	 * @return the cells where the Player's Worker can't move up
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
					if(m.getCell(x1, y1).getBuilding().getLevel() - myHeight >= 1) moves.add(new Move(TypeMove.FORBIDDEN_MOVE, w.getPos(), m.getCell(x1, y1), w));
				}
			}
		}

		return moves;
	}
}