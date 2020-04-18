package it.polimi.ingsw.util;

// necessary imports of Java SE
import java.util.ArrayList;
import java.util.List;

public class MultipleList<K,V> {
	private List<K> keyList;
	private List<V> valueList;

	public MultipleList() {
		keyList = new ArrayList<>();
		valueList = new ArrayList<>();
	}
	public MultipleList(MultipleList<K,V> other) {
		keyList = other.getKeyList();
		valueList = other.getValueList();
	}

	// modifiers
	public void add(K key, V value) throws IllegalArgumentException {
		if (key == null || value == null || keyList.contains(key)) {
			throw new IllegalArgumentException();
		}
		keyList.add(key);
		valueList.add(value);
	}
	public void removeByKey(K key) throws IllegalArgumentException {
		if (!keyList.contains(key) || key == null) {
			throw new IllegalArgumentException();
		}
		valueList.remove(keyList.indexOf(key));
		keyList.remove(key);
	}
	public void removeByValue(V value) throws IllegalArgumentException {
		if (!valueList.contains(value) || value == null) {
			throw new IllegalArgumentException();
		}
		keyList.remove(valueList.indexOf(value));
		valueList.remove(value);
	}

	// getters
	public int size() {
		return keyList.size();
	}
	public int getIndexByKey(K key) throws IllegalArgumentException {
		if (!keyList.contains(key)) {
			throw new IllegalArgumentException();
		}
		return keyList.indexOf(key);
	}
	public int getIndexByValue(V value) throws IllegalArgumentException {
		if (!valueList.contains(value)) {
			throw new IllegalArgumentException();
		}
		return valueList.indexOf(value);
	}
	public K getKey(int pos) throws IndexOutOfBoundsException {
		if (pos < 0 || pos >= keyList.size()) {
			throw new IndexOutOfBoundsException();
		}
		return keyList.get(pos);
	}
	public V getValue(int pos) throws IndexOutOfBoundsException {
		if (pos < 0 || pos >= keyList.size()) {
			throw new IndexOutOfBoundsException();
		}
		return valueList.get(pos);
	}
	public boolean containsKey(K key) {
		return keyList.contains(key);
	}
	public boolean containsValue(V value) {
		return valueList.contains(value);
	}
	public List<K> getKeyList() {
		return new ArrayList<>(keyList);
	}
	public List<V> getValueList() {
		return new ArrayList<>(valueList);
	}
	public void clear() {
		keyList.clear();
		valueList.clear();
	}
}