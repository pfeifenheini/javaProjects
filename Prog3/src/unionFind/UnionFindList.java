package unionFind;

// Union Find durch verkesstete Listen

class UnionFindList
{
	AVLTree tree; // Die Elemente werden in einem AVL Baum verwaltet
	
	// Initialisiert einen leeren Baum
	UnionFindList() 
	{
		tree = new AVLTree();
	}
	
	// Initialisiert einen Baum mit den Eintr�gen von 0 bis n-1
	UnionFindList(int n) 
	{
		int i;
		tree = new AVLTree();
		
		for(i=0;i<n;i++)
		{
			makeClass(i);
		}
	}
	
	// makeClass erzeut schlicht einen neuen Knoten im AVL Baum
	public ListElement makeClass(int key) 
	{
		ListElement neu = new ListElement(key);
		tree.root = tree.insert(neu, tree.root);
		return neu;
	}
	
	// Sucht den Knoten mit Eintrag key und gibt den Schl�ssel des ersten Listenelements dieses Eintrags zur�ck
	public int findClass(int key)
	{
		return tree.findNode(key, tree.root).first.key;
	}
	
	// Vereinigt die Mengen, die Eintrag a und b enthalten
	public void union(int a, int b)
	{
		ListElement x = tree.findNode(a, tree.root); // Findet den Eintrag, der den Schl�ssel a enth�lt
		ListElement y = tree.findNode(b, tree.root); // entsprechend f�r b
		
		// Repr�sentanten werden ermittelt
		x = x.first;
		y = y.first;
		
		if(x.key == y.key) return; // Wenn die Klassen bereits gleich sind, gibt es nichts zu tun
		
		if(x.setSize >= y.setSize) // Fallunterscheidung, damit die kleinere an die gr��ere geh�ngt wird
		{
			union(x,y);
		}
		else
		{
			union(y,x);
		}
	}
	
	// Vereinigt zwei Klassen a und b, dabei wird b an a geh�ngt
	public void union(ListElement a, ListElement b)
	{
		ListElement tmp = a;
		while(tmp.next != null) // Findet das Ende der Liste a
		{
			tmp = tmp.next;
		}
		
		tmp.next = b; // Das Letzte Element von a zeigt nun auf den Anfang von b
		tmp = b;
		while(tmp != null) // Geht liste b durch um jeden Zeiger auf den Anfang von a zu ver�ndern
		{
			tmp.first = a;
			tmp = tmp.next;
		}
		a.setSize += b.setSize; // Gr��e der Liste wird aktualisiert
	}
	
	public String toString()
	{
		return tree.toStringClasses(tree.root);
	}
	
	// Klasse der Listenelemente
	class ListElement
	{
		int key; // Speicher den Schl�ssel
		int setSize; // Speichert die Gr��e der Liste (wird nur bei Repr�sentanten aktuell gehalten)
		ListElement next; // Zeiger auf das n�chste Element (null, falls keins exisitiert)
		ListElement first; // Zeiger auf das erste Element
		
		ListElement(int key)
		{
			this.key = key;
			next = null;
			first = this;
			setSize = 1;
		}
		
		public String toString()
		{
			if(next != null)
			{
				return (key + " -> " + this.next.key + ", Klasse: " + this.first.key);
			}
			else
			{
				return (key + " -> null, Klasse: " + this.first.key);
			}
			
		}
		
		public String toStringClass(ListElement akt)
		{
			String returnS = "";
			if(akt == null)
			{
				return returnS;
			}
			returnS += akt.key + " -> ";
			if(akt.next != null)
			{
				returnS += toStringClass(akt.next);
			}
			else
			{
				returnS += "null";
			}
			
			return returnS;
		}
	}
	
	// Klasse des AVL Baums
	class AVLTree
	{
		AVLNode root; // Der Baum wird durch die Wurzel repr�sentiert
		
		public AVLTree()
		{
			root = null;
		}
		
		// Sucht das Listenelement, welches den Schl�ssel "key" enth�lt
		public ListElement findNode(int key, AVLNode t)
		{
			if(t.element.key == key) // Schl�ssel Gefunden
			{
				return t.element;
			}
			else if((key < t.element.key) && (t.left != null)) // Schl�ssel muss sich links befinden, wenn er existiert
			{
				return findNode(key, t.left); // Rekursiver Aufruf am linken Knoten
			}
			else if((key > t.element.key) && (t.right != null)) // Schl�ssel muss sich rechts befinden, wenn er existiert
			{
				return findNode(key, t.right); // Rekursiver Aufruf am rechten Knoten
			}
			else // Es folgen keine Knoten mehr
			{
				return null; // Element ist nicht enthalten
			}
		}
		
		// F�gt einen neuen Knoten an die Stelle t ein
		public AVLNode insert(ListElement element, AVLNode t)
		{
			if(t == null) // Der Schl�ssel kann an Stelle t eingetragen werden
			{
				t = new AVLNode(element);
			}
			else if(element.key < t.element.key) // Der Schl�ssel muss links vom aktuellen Knoten eingetragen werden
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
			else if(element.key > t.element.key) // Der Schl�ssel muss rechts vom aktuellen Knoten eingetragen werden
			{
				t.right = insert(element, t.right);
				if(t.balance() == 2) // Rechter Teilbaum ist zu tief
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
			
			t.height = max(height(t.left), height(t.right))+1; // H�he wird aktualisiert
			return t; // t enth�lt nun den aktuellen Knoten, der ehemals an genau dieser Stelle Stand
		}
		
		// Gibt die Tiefe des Teilbaums zur�ck, dessen Wurzel a ist
		public int height(AVLNode a)
		{
			return a == null ? -1 : a.height;
		}
		
		// F�hrt eine Rechtsrotation an t durch
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
			String returnS = "";
			if(t == null) return returnS;
			if(t.left != null)
			{
				returnS += toString(t.left);
			}
			returnS += t.element.toString() + "\n";
			if(t.right != null)
			{
				returnS += toString(t.right);
			}
			
			return returnS;
		}
		
		public String toStringClasses(AVLNode t)
		{
			String returnS = "";
			if(t.left != null)
			{
				returnS += toStringClasses(t.left);
			}
			if(t.element.key == t.element.first.key)
			{
				returnS += t.element.toStringClass(t.element) + "\n";
			}
			if(t.right != null)
			{
				returnS += toStringClasses(t.right);
			}
			return returnS;
		}
		
		// Klasse f�r die Knoten des AVL Baums
		public class AVLNode
		{
			ListElement element; // Eintrag der Knoten
			AVLNode left; // Linkter Sohn
			AVLNode right; // Rechter Sohn
			int height; // Hohe des Teilbaums
			
			AVLNode(ListElement element)
			{
				this.element = element;
				left = null;
				right = null;
				height = 0;
			}
			
			AVLNode(ListElement element, AVLNode left, AVLNode right)
			{
				this.element = element;
				this.left = left;
				this.right = right;
				height = 0;
			}
			
			// Ermittelt den Balancefaktor des aktuellen Knotens
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