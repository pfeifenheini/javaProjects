public class AvlTree
{ // Klasse f�r die Verwaltung der Knoten des AvlTrees
    public AvlNode root; // Der Wurzelknoten
 
    public AvlTree()
    { // Konstruktor setzt Wurzel auf null
        root = null;
    }
 
    public void makeEmpty()
    { // setzt die Wurzel wieder auf null, somit wird der Baum gel�scht
        root = null;
    }
 
    public boolean isEmpty()
    { // pr�ft, ob der Baum leer ist
        return (root == null);
    }
 
    public AvlNode insert(Comparable e, AvlNode t)
    { // F�gt einen neuen Knoten als Knoten t ein
        if(t == null)
            // Wenn der Knoten t noch nicht existiert, wird t der neue Knoten
            t = new AvlNode(e, null, null);
        // Wenn t schon existiert...
        else if(e.compareTo(t.element) < 0)
        { // ... und der Schl�ssel e des neuen Elementes < dem von t ist,
          // geht der Befehl weiter an den linken Nachfolger:
            t.left = insert(e, t.left);
 
            // Nun muss die Balancierung angepasst werden. Es wird links ange-
            // f�gt, es k�nnte also der linke Teilbaum nun 2 l�nger sein als
            // der rechte (H�he rechts-H�he links ist dann -2)
            if(height(t.right)-height(t.left) == -2)
                // wenn der �bergebene Schl�ssel kleiner ist als der des linken
                // Nachfolgers, h�ngt am linken Nachfolger noch ein linker N.
                // es muss dann nach rechts rotiert werden, wobei der linke
                // Nachfolger ber�cksichtigt wird (with left child)
                if(e.compareTo(t.left.element) < 0)
                    t = rotateWithLeftChild(t);
                else
                    // ansonsten muss erst links rotiert werden, um den oben
                    // genannten Fall zu erreichen, dann wird rechts rotiert:
                    t = doubleWithRightChild(t);
        } // end else if
        else if(e.compareTo(t.element) > 0)
        { // ... und der Schl�ssel e des neuen Elementes > dem von t ist
          // Verfahren ist analog zu dem oben genannten
            t.right = insert(e, t.right);
            if( height(t.right)-height(t.left) == 2)
                if(e.compareTo(t.right.element) > 0)
                    t = rotateWithRightChild(t);
                else
                    t = doubleWithLeftChild(t);
        } // end else if
        // Zum Schluss wird noch der H�he des Knotens das neue Maximum zugew.
        t.height = max(height(t.left), height(t.right))+1;
        return t;
    } // end insert
    
    public static Comparable find(Comparable e, AvlNode t)
    { // Pr�ft ob e im Baum enthalten ist
    	if(e.compareTo(t.element) < 0)
    	{ // Wenn der schl�ssel von e < als der von t ist...
    		if(t.left == null)
    		{ // kommt linke kein Kronten mehr, ist e nicht enthalten
    			return null;
    		}
    		else
    		{ // ansonsten suche links weiter
    			return find(e, t.left);
    		}
    	}
    	else if(e.compareTo(t.element) > 0)
    	{ // analog
    		if(t.right == null)
    		{
    			return null;
    		}
    		else
    		{
    			return find(e, t.right);
    		}
    	}
    	else
    	{ // Sonst wurde der Eintrag gefunden und wird zur�ckgegeben
    		return t.element;
    	}
    }
 
    public static int height(AvlNode t)
    { // Wenn t == null -> gib -1 zur�ck, Wenn t != null -> gib t.height zur�ck
        return t == null ? -1 : t.height;
    }
 
    public static int max(int lhs, int rhs)
    { // Wenn lhs > rhs -> gib lhs zur�ck, Wenn lhs <= rhs -> gib rhs zur�ck
        return lhs > rhs ? lhs : rhs;
    }
 
    public static AvlNode rotateWithLeftChild(AvlNode k2)
    {
        // Nachfolger werden vertauscht:
        AvlNode k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        // Neue H�hen werden angepasst
        k2.height = max(height(k2.left), height(k2.right))+1;
        k1.height = max(height(k1.left), k2.height)+1;
        return k1;
    } // end rotateWithLeftChild
 
    public static AvlNode rotateWithRightChild(AvlNode k1)
    {
        // Nachfolger werden vertauscht:
        AvlNode k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        // Neue H�hen werden angepasst
        k1.height = max(height(k1.left), height(k1.right))+1;
        k2.height = max(height(k2.right), k1.height)+1;
        return k2;
    } // end rotateWithRightChild
 
    public static AvlNode doubleWithRightChild(AvlNode k3)
    { // Kombination aus Links- und Rechtsrotation
        k3.left = rotateWithRightChild(k3.left);
        return rotateWithLeftChild(k3);
    } // end doubleWithLeftChild
 
    public static AvlNode doubleWithLeftChild(AvlNode k1)
    { // Kombination aus Rechts- und Linksrotation
        k1.right = rotateWithLeftChild(k1.right);
        return rotateWithRightChild(k1);
    } // end doubleWithRightChild
} // end class AvlTree

class AvlNode
{ // private Klasse zur Speicherung der Knotendaten
    AvlNode(Comparable theElement)
    { // Konstruktor 1, ruft Konstruktor 2 auf mit Standardeinstellungen
        this(theElement, null, null);
    } // end AvlNode

    AvlNode(Comparable theElement, AvlNode lt, AvlNode rt)
    { // Konstruktor 2, weist Parameter den Eigenschaften zu
        element  = theElement;
        left     = lt;
        right    = rt;
        height   = 0;
    } // end AvlNode

    Comparable element;      // Die Daten im Knoten
    AvlNode    left;         // Der linke Nachfolger
    AvlNode    right;        // Der rechte Nachfolger
    int        height;       // Die H�he des l�ngeren der beiden
                             // an dem Knoten h�ngenden Teilb�ume
} // end private class AvlNode