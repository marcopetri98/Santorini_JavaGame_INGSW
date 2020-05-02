package it.polimi.ingsw.network.game;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.Move;

// necessary imports of Java SE
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to send to the user the positions where a specified worker can move
 */
public class NetAvailablePositions implements Serializable {
	public final List<NetMove> moves;

	public NetAvailablePositions(List<Move> possibleMoves) {
		moves = new ArrayList<>();
		for (Move m : possibleMoves) {
			moves.add(new NetMove(m));
		}
	}

	public void appendMoves(List<Move> movesList) {
		for (Move m : movesList) {
			moves.add(new NetMove(m));
		}
	}
}
