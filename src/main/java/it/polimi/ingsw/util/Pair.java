package it.polimi.ingsw.util;

import java.io.Serializable;

public class Pair<P,Q> implements Serializable {
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair) {
			Pair<P,Q> other = (Pair<P,Q>)obj;
			return p.equals(other.getFirst()) && q.equals(other.getSecond());
		} else {
			return false;
		}
	}
}
