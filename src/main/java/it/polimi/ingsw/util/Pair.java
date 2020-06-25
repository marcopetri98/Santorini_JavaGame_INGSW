package it.polimi.ingsw.util;

import java.io.Serializable;

/**
 * This is a class which represent a pair.
 * @param <P> first element of the pair
 * @param <Q> second element of the pair
 */
public class Pair<P,Q> implements Serializable {
	private P p;
	private Q q;

	/**
	 * Creates a pair with first element of {@code p} and second element of {@code q}
	 * @param p first element
	 * @param q second element
	 */
	public Pair(P p, Q q) {
		this.p = p;
		this.q = q;
	}
	/**
	 * Creates a new pair from another one.
	 * @param other a pair
	 */
	public Pair(Pair<P,Q> other) {
		this.p = other.getFirst();
		this.q = other.getSecond();
	}

	/**
	 * Gets the first element.
	 * @return first element
	 */
	public P getFirst() {
		return p;
	}
	/**
	 * Gets the second element.
	 * @return second element
	 */
	public Q getSecond() {
		return q;
	}
	/**
	 * Sets the first element
	 * @param p is an element with the same type of the first element
	 */
	public void setFirst(P p) {
		this.p = p;
	}
	/**
	 * Sets the second element
	 * @param q is an element with the same type of the second element
	 */
	public void setSecond(Q q) {
		this.q = q;
	}
	/**
	 * It controls if the two pairs have the first element equal.
	 * @param other is a pair
	 * @return true if the first elements are equal
	 */
	public boolean firstEqual(Pair<P,Q> other) {
		return p.equals(other.getFirst());
	}
	/**
	 * It controls if the two pairs have the second element equal.
	 * @param other is a pair
	 * @return true if the second elements are equal
	 */
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
