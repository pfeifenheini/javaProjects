package unionFind;

// Union Find durch Bäume (inklusive Strategie zur Pfadverkürzung)
// Zu Größen Teil analog zur UnionFindList

class UnionFindTree
{
	AVLTree tree;
	
	UnionFindTree()
	{
		tree = new AVLTree();
	}
	
	UnionFindTree(int n)
	{
		int i;
		tree = new AVLTree();
		
		for(i=0;i<n;i++)
		{
			makeClass(i);
		}
	}
	
	public TreeElement makeClass(int key)
	{
		TreeElement neu = new TreeElement(key);
		tree.root = tree.insert(neu, tree.root);
		return neu;
	}
	
	// Gibt den Schlüssel des Repräsentanten der Klasse wieder, die "key" enthält
	public int findClass(int key)
	{
		return findClass(tree.findNode(key, tree.root)).key;
	}
	
	// Gibt den Repräsentanten der Klasse, die x enthält wieder, und verkürzt dabei den Pfad zur Wurzel
	public TreeElement findClass(TreeElement x)
	{
		if(x.parent == x) // Wurzel gefunden
		{
			return x;
		}
		else // Wurzel nicht gefunden
		{
			x.parent = findClass(x.parent); // parent wird auf den Repräsentanten der Klasse gesetzt, welcher rekursiv ermittelt wird
			//return findClass(x.parent);
		}
		return x.parent;
	}
	
	public void union(int a, int b)
	{
		TreeElement x = tree.findNode(a, tree.root);
		TreeElement y = tree.findNode(b, tree.root);
		
		x = findClass(x);
		y = findClass(y);
		
		if(x.key == y.key) return;
		
		if(x.size >= y.size)
		{
			union(x,y);
		}
		else
		{
			union(y,x);
		}
	}
	
	public void union(TreeElement a, TreeElement b)
	{ // Vereinigund, indem der Vater von b auf a gesetzt wird
		b.parent = a;
		a.size += b.size; // Größevon a anpassen
	}
	
	public String toString()
	{
		String returnS = "";
		returnS = tree.toString();
		return returnS;
	}
	
	class TreeElement
	{
		int key; // Speichert den Schlüssel
		int size; // Enthält die Größe des Teilbaums
		TreeElement parent; // Speichert den Vaterknoten
		
		TreeElement(int key)
		{
			this.key = key;
			parent = this;
			size = 1;
		}
		
		public String toString()
		{
			return (key + " -> " + parent.key);
		}
		
		public String toStringNext()
		{
			String returnS = "";
			returnS += parent.key;
			return returnS;
		}
	}
	
	class AVLTree
	{
		AVLNode root;
		
		public AVLTree()
		{
			root = null;
		}
		
		public TreeElement findNode(int key, AVLNode t)
		{
			if(t.element.key == key)
			{
				return t.element;
			}
			else if((key < t.element.key) && (t.left != null))
			{
				return findNode(key, t.left);
			}
			else if((key > t.element.key) && (t.right != null))
			{
				return findNode(key, t.right);
			}
			else
			{
				return null;
			}
		}
		
		public AVLNode insert(TreeElement element, AVLNode t)
		{
			if(t == null) // Der Schlüssel kann an Stelle t eingetragen werden
			{
				t = new AVLNode(element);
			}
			else if(element.key < t.element.key) // Der Schlüssel muss links vom Aktuellen Knoten eingetragen werden
			{
				t.left = insert(element, t.left);
				if(t.balance() == -2) // Linker Teilbaum ist zu tief
				{
					if(t.left.balance() == 1)
					{
						// Doppeltotation um linken Sohn von t
						t = doppelrotationLinkerSohn(t);
					}
					else
					{
						// Rechtsrotation um t
						t = rechtsrotation(t);
					}
				}
			}
			else if(element.key > t.element.key) // Der Schlüssel muss rechts vom aktuellen Knoten eingetragen werden
			{
				t.right = insert(element, t.right);
				if(t.balance() == 2)
				{
					if(t.right.balance() == -1)
					{
						// Doppelrotation um rechten Sohn von t
						t = doppelrotationRechterSohn(t);
					}
					else
					{
						// Linksrotation um t
						t = linksrotation(t);
					}
				}
			}
			
			t.height = max(height(t.left), height(t.right))+1;
			return t;
		}
		
		public int height(AVLNode a)
		{
			return a == null ? -1 : a.height;
		}
		
		public AVLNode rechtsrotation(AVLNode t)
		{
			AVLNode left = t.left;
			t.left = left.right;
			left.right = t;
			
			t.height = max(t.left.height, t.right.height)+1;
			left.height = max(left.left.height, left.right.height)+1;
			
			return left;
		}
		
		public AVLNode linksrotation(AVLNode t)
		{
			AVLNode right = t.right;
			t.right = right.left;
			right.left = t;
			
			t.height = max(height(t.left), height(t.right))+1;
			right.height = max(height(right.left), height(right.right))+1;
			
			return right;
		}
		
		public AVLNode doppelrotationLinkerSohn(AVLNode t)
		{
			t.left = linksrotation(t.left);
			return rechtsrotation(t);
		}
		
		public AVLNode doppelrotationRechterSohn(AVLNode t)
		{
			t.right = rechtsrotation(t.right);
			return linksrotation(t);
		}
		
		public int max(int a, int b)
		{
			return a < b ? b : a;
		}
		
		public String toString()
		{
			return toString(tree.root);
		}
		
		public String toString(AVLNode t)
		{
			TreeElement tmp = t.element;
			String returnS = "";
			if(t.left != null)
			{
				returnS += toString(t.left);
			}
			returnS += tmp.toString();
			tmp = tmp.parent;
			while(tmp.parent != tmp)
			{
				returnS += " -> " + tmp.toStringNext();
				tmp = tmp.parent;
			}
			returnS += "\n";
			if(t.right != null)
			{
				returnS += toString(t.right);
			}
			
			return returnS;
		}
		
		public class AVLNode
		{
			TreeElement element;
			AVLNode left;
			AVLNode right;
			int height;
			
			AVLNode(TreeElement element)
			{
				this.element = element;
				left = null;
				right = null;
				height = 0;
			}
			
			AVLNode(TreeElement element, AVLNode left, AVLNode right)
			{
				this.element = element;
				this.left = left;
				this.right = right;
				height = 0;
			}
			
			public int balance()
			{
				if(right != null && left != null)
				{
					return right.height - left.height;
				}
				else if(right != null)
				{
					return right.height;
				}
				else if(left != null)
				{
					return left.height;
				}
				else
				{
					return -1;
				}
				
			}
		}
	}
}