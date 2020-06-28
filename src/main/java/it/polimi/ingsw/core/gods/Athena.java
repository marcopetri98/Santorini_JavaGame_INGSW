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
					if(m.getCell(x1, y1).getBuilding().getLevel() - myHeight >= 1) moves.add(new Move(TypeMove.FORBIDDEN_MOVE, w.getPos(), m.getCell(x1, y1), w));
				}
			}
		}

		return moves;
	}
}