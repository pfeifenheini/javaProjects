import java.util.Comparator;

class UnionFindList
{
	AvlTree elements; //Alle Elemente werden in einem AVL Suchbaum gespeichert
	
	
	//Erstellt einen neuen, leeren AVL Baum um die Elemente zu speichern und später effizient zu finden
	public UnionFindList()
	{
		elements = new AvlTree();
	}
	
	//Erzeugt eine neue Klasse indem es den neuen Eintrag in den Suchbaum einfügt
	public void makeClass(Comparable entry)
	{
		ListElement neu = new ListElement(entry);
		elements.insert(neu, elements.root);
	}
	
	public Comparable findClass(Comparable e)
	{
		ListElement f = new ListElement(e);
		f = (ListElement) elements.find(f, elements.root);
		return f.entry;
	}
	
	public String toString()
	{
		String returnS = "";
		AvlNode a = elements.root;
		
		while(a != null)
		{
			returnS += ((ListElement)(a.element)).entry;
		}
		
		return returnS;
	}
	
	private class ListElement implements Comparable<ListElement>
	{
		public Comparable entry;
		public ListElement next;
		public ListElement first;
		
		public ListElement()
		{
			entry = null;
			next = null;
			first = this;
		}
		
		public ListElement(Comparable entry)
		{
			this.entry = entry;
			next = null;
			first = this;	
		}
		
		public int compareTo(ListElement b)
		{
			return this.entry.compareTo(b.entry);
		}
	}
}