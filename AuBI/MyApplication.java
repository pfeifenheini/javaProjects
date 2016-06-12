
public class MyApplication
{
	public static void main(String[] args)
	{
		int grad=30000;
		System.out.println("Grad: "+grad);
		int i;
		long Start;
		long Ende;
		double[] zweier = new double[grad+1];
		double[] dreier = new double[grad+1];
		
		for(i=0;i<=grad;i++)
		{
			zweier[i] = 2;
			dreier[i] = 2;
		}
		
		polynom pol1 = new polynom(zweier.length-1, zweier) ;
		
		polynom pol2 = new polynom(dreier.length-1, dreier) ;
		
		System.out.println("Teste addition");
		
		Start = System.currentTimeMillis();
		polynom ergebnisAddition = polynom.add(pol1, pol2);
		Ende = System.currentTimeMillis();
		
		//System.out.println(ergebnisAddition.toString());
		System.out.println("Zeit: "+(Ende-Start)+" ms");
		System.out.println("---------------------------------\n");
		
		System.out.println("Teste Schulmethode");
		
		Start = System.currentTimeMillis();
		polynom ergebnisMultiSchul = polynom.multAllgemein(pol1, pol2);
		Ende = System.currentTimeMillis();
		
		//System.out.println(ergebnisMultiSchul.toString());
		
		System.out.println("Zeit: "+(Ende-Start)+" ms");
		System.out.println("---------------------------------\n");
		
		//System.out.println("Teste naive multiplikation");
		
		Start = System.currentTimeMillis();
		//polynom ergebnisMultiNav = polynom.multNaiv(pol1, pol2);
		Ende = System.currentTimeMillis();
		
		//System.out.println(ergebnisMultiNav.toString());
		
		//System.out.println("Zeit: "+(Ende-Start)+" ms");
		//System.out.println("---------------------------------\n");
		
		System.out.println("Teste Karatsuba multiplikation");
		
		Start = System.currentTimeMillis();
		polynom ergebnisMultiKara = polynom.multKaracuba(pol1, pol2);
		Ende = System.currentTimeMillis();
		
		//System.out.println(ergebnisMultiKara.toString());
		
		System.out.println("Zeit: "+(Ende-Start)+" ms");
		System.out.println("---------------------------------\n");
	}
}