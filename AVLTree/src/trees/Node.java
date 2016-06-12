package trees;

public class Node<T extends Comparable<T>> {
	private final T entry;
	Node<T> left;
	Node<T> right;
	int depth;
	
	public Node() {
		entry = null;
		left = null;
		right = null;
		depth = 0;
	}
	
	public Node(T e) {
		entry = e;
		left = null;
		right = null;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public T getEntry() {
		return entry;
	}
	
	public int balance() { // gibt zurück um wie viel der rechte Teilbaum tiefer ist, als der linke
		if((left == null) && (right == null)) return 0;
		if(left == null) return right.depth;
		if(right == null) return -left.depth;
		return right.depth - left.depth;
	}
}
