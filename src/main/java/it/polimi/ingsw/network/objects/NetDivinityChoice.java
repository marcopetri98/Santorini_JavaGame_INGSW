package it.polimi.ingsw.network.objects;

// necessary imports of Java SE
import java.util.ArrayList;
import java.util.List;

public class NetDivinityChoice extends NetObject {
	public final String divinity;
	public final String starter;
	public final NetDivinityChoice next;

	public NetDivinityChoice(String msg) throws NullPointerException {
		super(msg);
		divinity = null;
		starter = null;
		next = null;
	}
	public NetDivinityChoice(String msg, String name, boolean divinity) throws NullPointerException {
		super(msg);
		if (name == null) {
			throw new NullPointerException();
		}
		if (!divinity) {
			this.divinity = null;
			starter = name;
		} else {
			this.divinity = name;
			starter = null;
		}
		next = null;
	}
	public NetDivinityChoice(String msg, String name, NetDivinityChoice next) throws NullPointerException {
		super(msg);
		if (name == null) {
			throw new NullPointerException();
		}
		divinity = name;
		starter = null;
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
		starter = null;
	}

	// getters
	public String getDivinity() {
		return divinity;
	}
	public String getStarter() {
		return starter;
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
