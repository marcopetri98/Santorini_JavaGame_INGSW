package it.polimi.ingsw.core.gods;
import it.polimi.ingsw.core.*;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.util.exceptions.NoBuildException;
import it.polimi.ingsw.util.exceptions.NoMoveException;

import java.util.ArrayList;
import java.util.List;

public abstract class GodCard {
	public abstract List<Integer> getNumPlayer();
	public abstract Player getOwner();
	public abstract TypeGod getTypeGod();
	public abstract String getName();
	public abstract String getDescription();
	public List<Move> checkMove(Map m, Worker w, Turn turn) throws NoMoveException {
		if (turn.getGamePhase() != GamePhase.MOVE) {
			throw new NoMoveException();
		}
		Map gameMap = m;
		int x = gameMap.getX(w.getPos());
		int y = gameMap.getY(w.getPos());

		List<Move> moves = new ArrayList<>();
		for(int i = -1; i <= 1; i++) {   //i->x   j->y     x1, y1 all the cells where I MAY move
			int x1 = x + i;
			for(int j = -1; j <= 1; j++){
				int y1 = y + j;

				if(x != x1 || y != y1){ //I shall not move where I am already
					if(0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4){   //Check that I am inside the map
						if(gameMap.getCell(x1, y1).getBuilding().getLevel() - gameMap.getCell(x, y).getBuilding().getLevel() <= 1){
							if(!gameMap.getCell(x1, y1).getBuilding().getDome()){   //Check there is NO dome
								if (gameMap.getCell(x1, y1).getWorker() == null) {   //Check there isn't any worker on the cell
									moves.add(new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(x, y), gameMap.getCell(x1, y1), w));
								}
							}
						}
					}
				}
			}
		}
		return moves;
	}
	public List<Build> checkBuild(Map m, Worker w, Turn turn) throws NoBuildException {
		if (turn.getGamePhase() != GamePhase.BUILD) {
			throw new NoBuildException();
		}
		Map gameMap = m;
		int x = gameMap.getX(w.getPos());
		int y = gameMap.getY(w.getPos());

		List<Build> builds = new ArrayList<>();
		for(int i = -1; i <= 1; i++) {   //i->x   j->y     x1, y1 all the cells where I MAY build
			int x1 = x + i;
			for (int j = -1; j <= 1; j++) {
				int y1 = y + j;

				if (x != x1 || y != y1) { //I shall not build where I am
					if (0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4) {   //Check that I am inside the map
						if (gameMap.getCell(x1, y1).getWorker() == null) {   //Check there isn't any worker on the cell
							if (!gameMap.getCell(x1, y1).getBuilding().getDome()) {   //Check there is NO dome
								if(gameMap.getCell(x1, y1).getBuilding().getLevel() <= 2) {
									builds.add(new Build(w, gameMap.getCell(x1, y1), false, TypeBuild.SIMPLE_BUILD)); // simple increment level
								} else if(gameMap.getCell(x1, y1).getBuilding().getLevel() == 3) {
									builds.add(new Build(w, gameMap.getCell(x1, y1), true, TypeBuild.SIMPLE_BUILD)); // dome on a 3 level building
								}
							}
						}
					}
				}
			}
		}
		return builds;
	}
	public static List<Move> standardMoves(Map m, Worker w, Turn turn) {
		if (turn.getGamePhase() != GamePhase.MOVE) {
			return null;
		}
		Map gameMap = m;
		int x = gameMap.getX(w.getPos());
		int y = gameMap.getY(w.getPos());

		List<Move> moves = new ArrayList<>();
		for(int i = -1; i <= 1; i++) {   //i->x   j->y     x1, y1 all the cells where I MAY move
			int x1 = x + i;
			for(int j = -1; j <= 1; j++){
				int y1 = y + j;

				if(x != x1 || y != y1){ //I shall not move where I am already
					if(0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4){   //Check that I am inside the map
						if(gameMap.getCell(x1, y1).getBuilding().getLevel() - gameMap.getCell(x, y).getBuilding().getLevel() <= 1){
							if(!gameMap.getCell(x1, y1).getBuilding().getDome()){   //Check there is NO dome
								if (gameMap.getCell(x1, y1).getWorker() == null) {   //Check there isn't any worker on the cell
									moves.add(new Move(TypeMove.SIMPLE_MOVE, gameMap.getCell(x, y), gameMap.getCell(x1, y1), w));
								}
							}
						}
					}
				}
			}
		}
		return moves;
	}
	public static List<Build> standardBuilds(Map m, Worker w, Turn turn) {
		if (turn.getGamePhase() != GamePhase.BUILD) {
			return null;
		}
		Map gameMap = m;
		int x = gameMap.getX(w.getPos());
		int y = gameMap.getY(w.getPos());

		List<Build> builds = new ArrayList<>();
		for(int i = -1; i <= 1; i++) {   //i->x   j->y     x1, y1 all the cells where I MAY build
			int x1 = x + i;
			for (int j = -1; j <= 1; j++) {
				int y1 = y + j;

				if (x != x1 || y != y1) { //I shall not build where I am
					if (0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4) {   //Check that I am inside the map
						if (gameMap.getCell(x1, y1).getWorker() == null) {   //Check there isn't any worker on the cell
							if (!gameMap.getCell(x1, y1).getBuilding().getDome()) {   //Check there is NO dome
								if(gameMap.getCell(x1, y1).getBuilding().getLevel() <= 2) {
									builds.add(new Build(w, gameMap.getCell(x1, y1), false, TypeBuild.SIMPLE_BUILD)); // simple increment level
								} else if(gameMap.getCell(x1, y1).getBuilding().getLevel() == 3) {
									builds.add(new Build(w, gameMap.getCell(x1, y1), true, TypeBuild.SIMPLE_BUILD)); // dome on a 3 level building
								}
							}
						}
					}
				}
			}
		}
		return builds;
	}
}