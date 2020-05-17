package it.polimi.ingsw.network.objects;

// necessary imports of Java SE
import it.polimi.ingsw.core.gods.GodCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetDivinityChoice extends NetObject {
	public final String divinity;
	public final String challenger;
	public final String player;
	public final NetDivinityChoice next;
	public final boolean godsEnd;

	public NetDivinityChoice(String msg) throws NullPointerException {
		super(msg);
		divinity = null;
		challenger = null;
		player = null;
		next = null;
		godsEnd = false;
	}
	public NetDivinityChoice(String msg, boolean godsEnd) throws NullPointerException {
		super(msg);
		divinity = null;
		challenger = null;
		player = null;
		next = null;
		this.godsEnd = godsEnd;
	}
	public NetDivinityChoice(String msg, String starter) throws NullPointerException {
		super(msg);
		challenger = null;
		player = starter;
		next = null;
		divinity = null;
		godsEnd = false;
	}
	public NetDivinityChoice(String msg, String player, String other, boolean start) throws NullPointerException {
		super(msg);
		if (player == null || other == null) {
			throw new NullPointerException();
		}
		challenger = start ? player : null;
		this.player = start ? other : player;
		next = null;
		divinity = start ? null : other.toUpperCase();
		godsEnd = false;
	}
	public NetDivinityChoice(String msg, String name, String god, NetDivinityChoice next) throws NullPointerException {
		super(msg);
		if (name == null) {
			throw new NullPointerException();
		}
		challenger = null;
		divinity = god.toUpperCase();
		player = name;
		this.next = next;
		godsEnd = false;
	}
	public NetDivinityChoice(String msg, List<String> divinities) throws NullPointerException {
		super(msg);
		if (divinities == null) {
			throw new NullPointerException();
		} else {
			divinity = divinities.get(0).toUpperCase();
			divinities.remove(0);
			if (divinities.size() >= 1) {
				next = new NetDivinityChoice(msg,divinities);
			} else {
				next = null;
			}
		}
		player = null;
		challenger = null;
		godsEnd = false;
	}
	public NetDivinityChoice(String msg, String player, List<String> divinities) throws NullPointerException {
		super(msg);
		if (divinities == null) {
			throw new NullPointerException();
		} else {
			divinity = divinities.get(0).toUpperCase();
			divinities.remove(0);
			if (divinities.size() >= 1) {
				next = new NetDivinityChoice(msg,divinities);
			} else {
				next = null;
			}
		}
		this.player = player;
		challenger = null;
		godsEnd = false;
	}

	// getters
	public String getDivinity() {
		return divinity;
	}
	public String getPlayer() {
		return player;
	}
	public Map<String,String> getPlayerGodMap() {
		Map<String,String> list = new HashMap<>();
		if (player != null && divinity != null) {
			list.put(player,divinity);
			if (next != null) {
				list.putAll(next.getPlayerGodMap());
			}
		}
		return list;
	}
	/*public List<String> getDivinities() {
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
	}*/

	public List<String> getDivinities() {
		if (divinity == null) {
			return null;
		} else {
			List<String> divinityNames = new ArrayList<>();
			divinityNames.add(divinity);
			NetDivinityChoice x;
			x = next;
			while(x != null){
				divinityNames.add(x.divinity);
				x = x.next;
			}
			return divinityNames;
		}
	}
}
