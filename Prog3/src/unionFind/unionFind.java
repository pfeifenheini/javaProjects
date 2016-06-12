package unionFind;

//*********************************************************//
// Programm zur Demonstration zweier Implementierungen von //
// Union Find Datendtrukturen                              //
// Es können jeweils Integer Werte mit den Funktionen      //
// makeClass(int x), findClass(int x) und                  //
// union(int x, int y) verwaltet werden.                   //
// Zum finden der Einträge werden jeweils AVL Bäume        //
// verwendet.                                              //
//*********************************************************//

public class unionFind
{
	
	public static void main(String[] args)
	{
		int n=50;
		UnionFindList u = randomList(n);
		UnionFindTree v = randomTree(n);
		
		System.out.println(u.toString() + "\n");
		System.out.println(v.toString() + "\n");
	}
	
	public static UnionFindList randomList(int n)
	{
		UnionFindList liste = new UnionFindList(n);
		double rand = Math.random()*n*2;
		int step = (int)rand, i, a, b;
		for(i=0;i<step;i++)
		{
			a = (int)(Math.random()*n);
			b = (int)(Math.random()*n);
			liste.union(a, b);
		}
		
		return liste;
	}
	
	public static UnionFindTree randomTree(int n)
	{
		UnionFindTree baum = new UnionFindTree(n);
		double rand = Math.random()*n;
		int step = (int)rand, i, a, b;
		for(i=0;i<step;i++)
		{
			a = (int)(Math.random()*n);
			b = (int)(Math.random()*n);
			baum.union(a, b);
		}
		
		return baum;
	}
}