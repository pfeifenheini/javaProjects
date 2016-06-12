package trees;

import java.util.Set;

public class BalancedTree<T extends Comparable<T>> implements Set<T> {
	Node<T> root = null;
	
	private boolean isBalanced() {
		return isBalanced(root);
	}
	
	private boolean isBalanced(Node<T> node) { // Testet ob der Teilbaum, der bei node beginnt balanciert ist
		int diff = node.right.getDepth() - node.left.getDepth();
		if((diff <= 1) && (diff >= -1)) {
			return isBalanced(node.left) && isBalanced(node.right); 
		}
		return false;
	}
	
	public int size() {
		return size(root);
	}
	
	private int size(Node<T> node) {
		if(node == null) return 0;
		return size(node.left) + size(node.right) + 1;
	}
	
	public boolean isEmpty() {
		if(root == null) return true;
		return false;
	}
	
	public boolean contains(final T element) {
		return contains(element,root);
	}
	
	private boolean contains(final T element, Node<T> node) {
		if(node == null) return false;
		if(node.getEntry().compareTo(element) == 0) return true;
		if(node.getEntry().compareTo(element) < 0) return contains(element,node.left);
		return contains(element,node.right);
	}
	
	public boolean add(final T element) {
		root = add(element,root);
		assert isBalanced();
		return true;
	}
	
	private Node<T> add(final T element, Node<T> position) {
		if(position == null) return new Node<T>(element);
		if(position.getEntry().compareTo(element) < 0) {
			position.left = add(element,position.left);
			if(position.balance() < -1) { // linker Teilbaum zu tief
				if(position.left.balance() == 1) {
					// doppelrotation um linken sohn
					position = doubleRotateLeftSon(position);
				}
				else {
					// rechtsrotation
					position = rotateRight(position);
				}
			}
		}
		else if(position.getEntry().compareTo(element) > 0) {
			position.right = add(element,position.right);
			if(position.balance() > 1) {
				if(position.right.balance() == -1) {
					// doppelrotation um rechten Sohn
					position = doubleRotateRightSon(position);
				}
				else {
					// linksrotation
					position = rotateLeft(position);
				}
			}
		}
		
		position.depth = max(depth(position.left),depth(position.right)) + 1;
		return position;
	}
	
	private int depth(Node<T> node) {
		return node == null? -1 : node.depth;
	}
	
	private Node<T> rotateLeft(Node<T> node) {
		Node<T> rightSon = node.right;
		node.right = rightSon.left;
		rightSon.left = node;
		
		node.depth = max(node.left.depth, node.right.depth)+1;
		rightSon.depth = max(rightSon.left.depth, rightSon.right.depth)+1;
		
		return rightSon;
	}
	
	private Node<T> rotateRight(Node<T> node) {
		Node<T> leftSon = node.left;
		node.left = leftSon.right;
		leftSon.right = node;
		
		node.depth = max(node.right.depth, node.right.depth)+1;
		leftSon.depth = max(leftSon.right.depth, leftSon.left.depth)+1;
		
		return leftSon;
	}
	
	private Node<T> doubleRotateLeftSon(Node<T> node) {
		node.left = rotateLeft(node.left);
		return rotateRight(node);
	}
	
	private Node<T> doubleRotateRightSon(Node<T> node) {
		node.right = rotateRight(node.right);
		return rotateLeft(node);
	}
	
	private int max(int a, int b) {
		return a > b ? a : b;
	}
	
	public static void main(String[] args) {
		BalancedTree<Integer> a = new BalancedTree<Integer>();
		
		a.add(5);
		a.add(10);
		a.add(7);
		//a.add(8);
		//a.add(4);
		//a.add(9);
		//a.add(1);
		//a.add(2);
		
		if(a.contains(5)) System.out.println("Baum enthält 5");
		System.out.println(a.root.getEntry());
		if(a.contains(6)) System.out.println("Baum enthält 6");
	}
}
