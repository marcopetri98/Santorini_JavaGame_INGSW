package it.polimi.ingsw.network.game;

// necessary imports from other packages of the project
import it.polimi.ingsw.core.Move;
import it.polimi.ingsw.core.TypeMove;

// necessary imports of Java SE
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to send to the user the positions where a specified worker can move.
 */
public class NetAvailablePositions implements Serializable {
	public final List<NetMove> moves;

	/**
	 * Creates an object with the possible moves.
	 * @param possibleMoves is the list of possible moves
	 */
	public NetAvailablePositions(List<Move> possibleMoves) {
		moves = new ArrayList<>();
		for (Move m : possibleMoves) {
			if (m.typeMove != TypeMove.FORBIDDEN_MOVE) {
				moves.add(new NetMove(m));
			}
		}
	}
}
