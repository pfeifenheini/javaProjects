package benchmark;

import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.graph.*;

//*****************************************************************//
// Dieses Programm f�rht einen kleinen Benchmark durch,            //
// indem es zufallsgraphen verschiedener Gr��en (ahzahl Knoten)    //
// generiert und dann den Kruskal Algorithmus auf diesen Graphen   //
// durchf�hrt.                                                     //
// Die Gr��en k�nnen in dem Feld "n[]" ver�ndert werden und die    //
// Anzahl der Graphen pro Gr��e kann in "count" ver�ndert werden   //
// Gemessen wird nur die Zeit die der Kruskal Algorithmus ben�tigt //
// und die durchschnittliche Zeit pro Graphengr��e ausgegeben      //
//*****************************************************************//

public class benchmark
{
	public static void main(String[] args)
	{
		int i, j, count=10; // count speichert wie viele Graphen pro gr��enordnung generiert werden sollen
		int n[] = {100, 500, 1000, 1500, 2000}; // Speichert wie viele Knoten die verschiedenen Graphen haben sollen
		long start, ende; // zur Zeitmessung
		long time[] = new long[n.length]; // Speichert die gemessenen Zeiten
		
		for(i=0;i<n.length;i++)
		{
			time[i] = 0; // initialisierung der Zeit (zur Sicherheit)
			for(j=0;j<count;j++)
			{
				System.out.println("----- Zufallsgraph Nr. " + (j+1) + "/" + count + " mit " + n[i] + " wird generiert -----");
				
				SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = randomGraph(n[i]); // Zufallsgraph wird generiert
				
				System.out.println("----- Zufallsgraph wurde generiert -----");
				
				System.out.println("----- Kruskal Algorithmus wird ausgef�hrt -----");
				
				start = System.currentTimeMillis(); // Start der Zeitmessung
				KruskalMinimumSpanningTree<Integer, DefaultWeightedEdge> gMST = new KruskalMinimumSpanningTree<Integer, DefaultWeightedEdge>(g); // Kruskal Algorithmus wird durchgef�hrt
				ende = System.currentTimeMillis(); // Ende der Zeitmessung
				time[i] += (ende - start);
				
				System.out.println("----- Kruskal Algorithmus beendet -----");
				
			}
			time[i] = time[i]/count; // Durchschnittliche Zeit wird berechnet
		}
		
		for(i=0;i<n.length;i++) // Ausgabe
		{
			System.out.println("Durchschnittliche Zeit f�r Graphen der groesse " + n[i] + ": " + time[i] + " ms");
		}
	}
	
	public static SimpleWeightedGraph<Integer, DefaultWeightedEdge> randomGraph(int n)
	{ // Methode um einen zufallsgraphen zu generieren
		int i, v, w;
		int maxEdges = (n*(n-1))/2; // Der vollst�ndige (ungerichtete) K_n hat n(n-1)/2 Kanten 
		double weight;
		DefaultWeightedEdge edge = null;
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class); // Neuer Graph wird initialisiert
		
		for(i=0;i<n;i++)
		{
			g.addVertex(i); // Es werden n Knoten eingef�gt
		}
		
		// Durch diese Art den Zufallsgraphen zu generieren wird der Graph nach dem Gesetz der kleinen Zahlen
		// im schnitt zu 2/3 gef�llt sein
		for(i=0;i<maxEdges;i++) // Kanten werden eingef�gt
		{
			v = (int) (Math.random()*(n)); // Zuf�lliger Startknoten
			w = (int) (Math.random()*(n)); // Zuf�lliger Zielknoten
			weight = Math.random()*100; // Zuf�lliges Gewicht
			
			if(v != w) // Kante braucht nur eingef�gt werden, wenn die Knoten verschieden sind
			{
				edge = g.addEdge(v, w); // Kante wird eingef�gt
			}
			
			if(edge != null) // Wurde die Kante eingef�gt ...
			{
				g.setEdgeWeight(edge, weight); // ... wird das Gewicht gesetzt
			}
		}
		
		return g;
	}
}