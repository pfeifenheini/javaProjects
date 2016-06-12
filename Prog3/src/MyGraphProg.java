import java.util.HashSet;
import java.util.Set;

import org.jgrapht.*;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.graph.*;
import org.jgrapht.generate.*;


public class MyGraphProg
{
	
	public static void main(String[] args)
	{
		UnionFindList list = new UnionFindList();
		
		int a1 = 1;
		int a2 = 2;
		int a3 = 3;
		int a4 = 4;
		
		list.makeClass(a1);
		list.makeClass(a2);
		list.makeClass(a3);
		list.makeClass(a4);
		
		System.out.println(list.toString());
		
		
		/*int n=1000, i, j;
		long start;
		long ende;
		long time = 0;
		Set<DefaultWeightedEdge> set = new HashSet<DefaultWeightedEdge>();
		System.out.println("----- Zufallsgraph wird generiert -----");
		
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = randomGraph(n);
		
		System.out.println("----- Zufallsgraph wurde generiert -----");
		
		if(n<50)
		{
			System.out.println(g.toString());
		}
		
		if(n<50)
		{
			for(i=0;i<n;i++)
			{
				for(j=0;j<n;j++)
				{
					if(g.getEdge(i, j) != null)
					{
						System.out.println("{" + i + "," + j + "} - Weight: " + g.getEdgeWeight(g.getEdge(i,j)));
					}
				}
			}
		}

		
		System.out.println("----- Kruskal Algorithmus wird ausgeführt -----");
		
		start = System.currentTimeMillis();
		KruskalMinimumSpanningTree<Integer, DefaultWeightedEdge> gMST = new KruskalMinimumSpanningTree<Integer, DefaultWeightedEdge>(g);
		ende = System.currentTimeMillis();
		time = time + (ende - start);
		
		System.out.println("----- Kruskal Algorithmus beendet -----");
		
		set = gMST.getEdgeSet();
		
		
		if(n<50)
		{
			for(i=0;i<n;i++)
			{
				for(j=0;j<n;j++)
				{
					if(g.getEdge(i, j) != null)
					{
						if(set.contains(g.getEdge(i, j)))
						{
							System.out.println("-> {" + i + "," + j + "} - Weight: " + g.getEdgeWeight(g.getEdge(i,j)));
						}
						else
						{
							//System.out.println("{" + i + "," + j + "} - Weight: " + g.getEdgeWeight(g.getEdge(i,j)));
						}
					}
				}
			}
		}

		
		System.out.println("Zeit Benötigt: " + time + "ms");*/
		
		
		
		/*UnionFind a1 = new UnionFind(), a2 = new UnionFind(), a3 = new UnionFind(), a4 = new UnionFind(), a5 = new UnionFind();
		UnionFind b;
		
		a1.key = 1;
		a2.key = 2;
		a3.key = 3;
		a4.key = 4;
		a5.key = 5;
		
		a1.makeClass();
		a2.makeClass();
		a3.makeClass();
		a4.makeClass();
		a5.makeClass();
		
		a1.union(a2);
		a2.union(a3);
		a4.union(a5);
		
		b = a2.findClass();
		
		while(b != null)
		{
			System.out.print(b.key + " -> ");
			if(b.nextElem != null)
			{
				b = b.nextElem;
			}
			else
			{
				System.out.println("NULL");
				b = b.nextElem;
			}
		}*/
		
		/*UnionFindTree a1 = new UnionFindTree();
		UnionFindTree a2 = new UnionFindTree();
		UnionFindTree a3 = new UnionFindTree();
		UnionFindTree a4 = new UnionFindTree();
		UnionFindTree a5 = new UnionFindTree();
		UnionFindTree a6 = new UnionFindTree();
		UnionFindTree a7 = new UnionFindTree();
		UnionFindTree a8 = new UnionFindTree();
		UnionFindTree a9 = new UnionFindTree();
		UnionFindTree a10 = new UnionFindTree();
		
		a1.key = 1;
		a2.key = 2;
		a3.key = 3;
		a4.key = 4;
		a5.key = 5;
		a6.key = 6;
		a7.key = 7;
		a8.key = 8;
		a9.key = 9;
		a10.key = 10;
		
		a1.union(a2);
		a2.union(a3);
		a4.union(a5);
		a4.union(a6);
		a6.union(a9);
		a10.union(a9);
		a7.union(a8);
		a1.union(a10);
		a2.union(a8);
		
		System.out.println(a1.key + " -> " + a1.parent.key + " size: " + a1.size);
		System.out.println(a2.key + " -> " + a2.parent.key + " size: " + a2.size);
		System.out.println(a3.key + " -> " + a3.parent.key + " size: " + a3.size);
		System.out.println(a4.key + " -> " + a4.parent.key + " size: " + a4.size);
		System.out.println(a5.key + " -> " + a5.parent.key + " size: " + a5.size);
		System.out.println(a6.key + " -> " + a6.parent.key + " size: " + a6.size);
		System.out.println(a7.key + " -> " + a7.parent.key + " size: " + a7.size);
		System.out.println(a8.key + " -> " + a8.parent.key + " size: " + a8.size);
		System.out.println(a9.key + " -> " + a9.parent.key + " size: " + a9.size);
		System.out.println(a10.key + " -> " + a10.parent.key + " size: " + a10.size);*/
	}
	
	public static SimpleWeightedGraph<Integer, DefaultWeightedEdge> randomGraph(int n)
	{
		int i, v, w;
		int maxEdges = (n*(n-1))/2;
		double weight;
		DefaultWeightedEdge edge = null;
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		for(i=0;i<n;i++)
		{
			g.addVertex(i);
		}
		
		for(i=0;i<maxEdges;i++)
		{
			v = (int) (Math.random()*(n-1));
			w = (int) (Math.random()*(n-1));
			weight = Math.random()*100;
			
			if(v != w)
			{
				edge = g.addEdge(v, w);
			}
			
			if(edge != null)
			{
				g.setEdgeWeight(edge, weight);
			}
		}
		
		return g;
	}
}