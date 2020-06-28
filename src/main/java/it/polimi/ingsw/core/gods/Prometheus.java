package it.polimi.ingsw.core.gods;
import it.polimi.ingsw.core.*;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.util.exceptions.NoBuildException;
import it.polimi.ingsw.util.exceptions.NoMoveException;

import java.util.List;
import java.util.ArrayList;

/**
 * This is Prometheus GodCard, it has the specific implementation of the methods to calculate the moves and builds that each worker can do
 */
public class Prometheus extends GodCard {

	//PROMETHEUS CODE
	private Player owner;
	public final TypeGod typeGod = TypeGod.CHANGE_FLOW_GOD;
	public final List<Integer> numPlayer = List.of(2,3,4);
	public final String name = "Prometheus";
	public final String description = "Your Turn: If your Worker does not move up, it may build both before and after moving.";

	/**
	 * Constructor of the class
	 * @param player the owner of the card
	 */
	public Prometheus(Player player){
		this.owner = player;
	}

	/**
	 * Empty constructor of the class
	 */
	public Prometheus(){
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



	//CARD-SPECIFIC IMPLEMENTATION OF CHECKBUILD AND CHECKMOVE
	/**
	 * This is the "default" building option, overridden because also needed in "BEFOREMOVE" GamePhase
	 * @param m represents the map
	 * @param w represents the worker moved by the player during this turn
	 * @param turn the phase of the game
	 * @return the cells where the Player's Worker may build according to general game rules and his GodCard power
	 * @throws NoBuildException if you can't build because of a wrong phase
	 */
	@Override
	public List<Build> checkBuild(Map m, Worker w, Turn turn) throws NoBuildException{
		// if it isn't before the moving phase this god has no power on building and throws an exception
		if (turn.getGamePhase() != GamePhase.BEFOREMOVE && turn.getGamePhase() != GamePhase.BUILD) {
			throw new NoBuildException();
		}

		int y = m.getY(w.getPos());
		int x = m.getX(w.getPos());
		List<Build> builds = new ArrayList<>();
		for(int i = -1; i <= 1; i++) {   //i->x   j->y     x1, y1 all the cells where I MAY build
			int x1 = x + i;
			for (int j = -1; j <= 1; j++) {
				int y1 = y + j;

				if (x != x1 || y != y1) { //I shall not build where I am
					if (0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4) {   //Check that I am inside the map
						if (-1 <= (x1 - x) && (x1 - x) <= 1 && -1 <= (y1 - y) && (y1 - y) <= 1) {  //Check that distance from original cell is <= 1
							if (m.getCell(x1, y1).getWorker() == null) {   //Check there isn't any worker on the cell
								if (!m.getCell(x1, y1).getBuilding().getDome()) {   //Check there is NO dome
									if(m.getCell(x1, y1).getBuilding().getLevel() <= 2) {
										builds.add(new Build(w, m.getCell(x1, y1), false, TypeBuild.SIMPLE_BUILD));	//adds possible build: one block build
									} else if(m.getCell(x1, y1).getBuilding().getLevel() == 3) {
										builds.add(new Build(w, m.getCell(x1, y1), true, TypeBuild.SIMPLE_BUILD)); //adds possible build: single dome on top of three level building
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
	 * This is the specific implementation of the movement option for this GodCard
	 * @param m represents the map
	 * @param w represents the worker moved by the player during this turn
	 * @param turn the phase of the game
	 * @return the cells where the Player's Worker may move according to general game rules and his GodCard power
	 * @throws NoMoveException if the phase is wrong
	 */
	@Override
	public List<Move> checkMove(Map m, Worker w, Turn turn) throws NoMoveException {   //worker->activeworker
		// if the phase isn't the move phase it throws a move exception
		if (turn.getGamePhase() == GamePhase.BUILD) {
			throw new NoMoveException();
		}

		// if the player has built before moving it should not move up
		List<Move> moves = new ArrayList<>();
		if (w.itHasBuilt()) {
			int x = m.getX(w.getPos());
			int y = m.getY(w.getPos());

			for(int i = -1; i <= 1; i++) {   //i->x   j->y     x1, y1 all the cells where I MAY move
				int x1 = x + i;
				for(int j = -1; j <= 1; j++){
					int y1 = y + j;

					if(x != x1 || y != y1){ //I shall not move where I am already
						if(0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4){   //Check that I am inside the map
							if(m.getCell(x1, y1).getBuilding().getLevel() - m.getCell(x, y).getBuilding().getLevel() <= 0) { // it can't move up because has built
								if(!m.getCell(x1, y1).getBuilding().getDome()){   //Check there is NO dome
									if (m.getCell(x1, y1).getWorker() == null) {   //Check there isn't any worker on the cell
										moves.add(new Move(TypeMove.SIMPLE_MOVE, w.getPos(), m.getCell(x1, y1), w));
									}
								}
							}
						}
					}
				}
			}
		} else {
			if (w == owner.getWorker1()) {
				if (owner.getWorker2().itHasBuilt()) {
					return moves;
				}
			} else {
				if (owner.getWorker1().itHasBuilt()) {
					return moves;
				}
			}
			// the player hasn't built before moving and can move up
			int x = m.getX(w.getPos());
			int y = m.getY(w.getPos());

			for(int i = -1; i <= 1; i++) {   //i->x   j->y     x1, y1 all the cells where I MAY move
				int x1 = x + i;
				for(int j = -1; j <= 1; j++){
					int y1 = y + j;

					if(x != x1 || y != y1){ //I shall not move where I am already
						if(0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4){   //Check that I am inside the map
							if(-1 <= (x1-x) && (x1-x) <= 1 && -1 <= (y1-y) && (y1-y) <=1){  //Check that distance from original is cell <= 1: useless?
								if(m.getCell(x1, y1).getBuilding().getLevel() - m.getCell(x, y).getBuilding().getLevel() <= 1){
									if(!m.getCell(x1, y1).getBuilding().getDome()){   //Check there is NO dome
										if (m.getCell(x1, y1).getWorker() == null) {   //Check there isn't any worker on the cell
											moves.add(new Move(TypeMove.SIMPLE_MOVE, m.getCell(x, y), m.getCell(x1, y1), w));
										}
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