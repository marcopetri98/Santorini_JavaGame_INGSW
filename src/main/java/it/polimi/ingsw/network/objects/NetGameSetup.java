package it.polimi.ingsw.network.objects;

// necessary imports from other packages of the project
import it.polimi.ingsw.network.game.NetMap;
import it.polimi.ingsw.util.Pair;

public class NetGameSetup extends NetObject {
	public final String player;
	public final NetMap gameMap;
	public final Pair<Integer,Integer> worker1;
	public final Pair<Integer,Integer> worker2;

	public NetGameSetup(String msg) {
		super(msg);
		player = null;
		gameMap = null;
		worker1 = null;
		worker2 = null;
	}
	public NetGameSetup(String msg, NetMap map) throws NullPointerException {
		super(msg);
		if (map == null) {
			throw new NullPointerException();
		}
		player = null;
		gameMap = map;
		worker1 = null;
		worker2 = null;
	}
	public NetGameSetup(String msg, String player, Pair<Integer,Integer> worker1, Pair<Integer,Integer> worker2) throws NullPointerException, IllegalArgumentException {
		super(msg);
		if (worker1 == null || worker2 == null) {
			throw new NullPointerException();
		} else if (worker1.getFirst() <= 4 && worker1.getFirst() >= 0 && worker1.getSecond() <= 4 && worker1.getSecond() >= 0 && worker2.getFirst() <= 4 && worker2.getFirst() >= 0 && worker2.getSecond() <= 4 && worker2.getSecond() >= 0) {
			throw new IllegalArgumentException();
		}
		this.player = player;
		gameMap = null;
		this.worker1 = worker1;
		this.worker2 = worker2;
	}
}
