package it.polimi.ingsw.network.objects;

// necessary imports of Java SE
import java.util.ArrayList;
import java.util.List;

public class NetDivinityChoice extends NetObject {
	public final String divinity;
	public final String challenger;
	public final String player;
	public final NetDivinityChoice next;

	public NetDivinityChoice(String msg) throws NullPointerException {
		super(msg);
		divinity = null;
		challenger = null;
		player = null;
		next = null;
	}
	public NetDivinityChoice(String msg, String starter) throws NullPointerException {
		super(msg);
		challenger = null;
		player = starter;
		next = null;
		divinity = null;
	}
	public NetDivinityChoice(String msg, String player, String other, boolean start) throws NullPointerException {
		super(msg);
		if (player == null || other == null) {
			throw new NullPointerException();
		}
		challenger = start ? player : null;
		this.player = start ? other : player;
		next = null;
		divinity = start ? null : other;
	}
	public NetDivinityChoice(String msg, String name, String god, NetDivinityChoice next) throws NullPointerException {
		super(msg);
		if (name == null) {
			throw new NullPointerException();
		}
		challenger = null;
		divinity = god;
		player = name;
		this.next = next;
	}
	public NetDivinityChoice(String msg, List<String> divinities) throws NullPointerException {
		super(msg);
		if (divinities == null) {
			throw new NullPointerException();
		} else {
			divinity = divinities.get(0);
			divinities.remove(0);
			if (divinities.size() >= 1) {
				next = new NetDivinityChoice(msg,divinities);
			} else {
				next = null;
			}
		}
		player = null;
		challenger = null;
	}

	// getters
	public String getDivinity() {
		return divinity;
	}
	public String getPlayer() {
		return player;
	}
	public List<String> getDivinities() {
		if (divinity == null) {
			return null;
		} else {
			List<String> divinityNames = new ArrayList<>();
			if (next != null) {
				divinityNames.addAll(next.getDivinities());
			}
			if (!divinityNames.contains(divinity)) {
				divinityNames.add(divinity);
			}

			return divinityNames;
		}
	}
}
