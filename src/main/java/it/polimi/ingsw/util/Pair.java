package it.polimi.ingsw.util;

public class Pair<P,Q> {
	private P p;
	private Q q;

	public Pair(P p, Q q) {
		this.p = p;
		this.q = q;
	}
	public Pair(Pair<P,Q> other) {
		this.p = other.getFirst();
		this.q = other.getSecond();
	}

	public P getFirst() {
		return p;
	}
	public Q getSecond() {
		return q;
	}
	public void setFirst(P p) {
		this.p = p;
	}
	public void setSecond(Q q) {
		this.q = q;
	}
	public boolean firstEqual(Pair<P,Q> other) {
		return p.equals(other.getFirst());
	}
	public boolean secondEqual(Pair<P,Q> other) {
		return q.equals(other.getSecond());
	}
}
