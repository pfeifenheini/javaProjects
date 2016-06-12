import java.lang.*;

class polynom{
	int grad;
	double[] koef;
	
	public polynom()
	{
		grad = 0;
		koef = new double[1];
	}
	
	public polynom(int n)
	{
		grad = n;
		koef = new double[n+1];
	}
	
	public polynom(int n, double array[])
	{
		int i;
		
		grad = n;
		koef = new double[n+1];
		
		for(i=0;i<=n;i++)
		{
			koef[i] = array[i];
		}
	}
	
	static int maximum(int a, int b)
	{
		if(a>b)
			return a;
		else
			return b;
	}
	
	static int zweierpotenz(int a, int b)
	{
		int n=1, i;
		a = maximum(a,b);
		for(i=0;n<a;i++)
		{
			n = n*2;
		}
		return n;
	}
	
	static polynom normalisieren(polynom pol1, int grad)
	{
		int i;
		//int grad = zweierpotenz(pol1.grad+1,pol2.grad+1)-1;
		double[] neu1 = new double[grad+1];
		//double[] neu2 = new double[grad+1];
		
		for(i=0;i<=grad;i++)
		{
			neu1[i] = 0;
			//neu2[i] = 0;
			if(i<=pol1.grad)
			{
				neu1[i] = pol1.koef[i];
			}
			//if(i<=pol2.grad)
			//{
			//	neu2[i] = pol2.koef[i];
			//}
		}
		pol1.koef = neu1;
		//pol2.koef = neu2;
		pol1.grad = grad;
		//pol2.grad = grad;
		return pol1;
	}
	
	static polynom addition(polynom pol1, polynom pol2)
	{
		int i;
		polynom ergebnis = new polynom( maximum(pol1.grad,pol2.grad));
		
		for(i=0;i<=ergebnis.grad;i++)
		{
			if(i<=pol1.grad && i<=pol2.grad)
			{
				ergebnis.koef[i] = pol1.koef[i] + pol2.koef[i];
			}
			else if(i<=pol1.grad)
			{
				ergebnis.koef[i] = pol1.koef[i];
			}
			else
			{
				ergebnis.koef[i] = pol2.koef[i];
			}
		}
		
		return ergebnis;
	}
	
	static polynom addition(polynom pol1, polynom pol2, int n)
	{
		int i;
		polynom ergebnis = new polynom( maximum(pol1.grad,pol2.grad+n));
		
		for(i=0;i<=ergebnis.grad;i++)
		{
			if(i<=pol1.grad && i<=pol2.grad)
			{
				if(i-n>=0)
				{
					ergebnis.koef[i] = pol1.koef[i] + pol2.koef[i-n];
				}
				else
				{
					ergebnis.koef[i] = pol1.koef[i];
				}
			}
			else if(i<=pol1.grad)
			{
				ergebnis.koef[i] = pol1.koef[i];
			}
			else
			{
				if(i-n>=0)
				{
					ergebnis.koef[i] = pol2.koef[i-n];
				}
				else
				{
					ergebnis.koef[i] = 0;
				}
			}
		}
		
		return ergebnis;
	}
	
	static polynom multNaiv(polynom pol1, polynom pol2)
	{
		int i;
		
		int neuerGrad = zweierpotenz(pol1.grad+1,pol2.grad+1)-1;
		
		if (neuerGrad == 0){
			polynom ergebnis = new polynom(0);
			ergebnis.koef[0] = pol1.koef[0] * pol2.koef[0];
			return ergebnis;
		}
		
		pol1 = normalisieren(pol1,neuerGrad);
		pol2 = normalisieren(pol2,neuerGrad);
		
		polynom p_l = new polynom((pol1.grad+1)/2-1);
		polynom p_r = new polynom((pol1.grad+1)/2-1);
		polynom q_l = new polynom((pol2.grad+1)/2-1);
		polynom q_r = new polynom((pol2.grad+1)/2-1);
		
		for(i=0;i<(pol1.grad+1)/2;i++)
		{
			p_l.koef[i] = pol1.koef[i];
			p_r.koef[i] = pol1.koef[i+p_l.grad+1];
			q_l.koef[i] = pol2.koef[i];
			q_r.koef[i] = pol2.koef[i+q_l.grad+1];
		}
		
		polynom summand1, summand2, summand3, summand4;
		
		summand1 = multNaiv(p_l,q_l);
		summand2 = multNaiv(p_l,q_r);
		summand3 = multNaiv(p_r,q_l);
		summand4 = multNaiv(p_r,q_r);
		
		summand2 = addition(summand2,summand3);
		
		summand1 = addition(summand1,summand2,(pol1.grad+1)/2);
		summand1 = addition(summand1,summand4,pol1.grad);
		
		return summand1;
	}
	
	polynom multKaratuba(polynom pol1, polynom pol2)
	{
		polynom ergebnis = new polynom(); 
		return ergebnis;
	}
	
	
	public String toString(){
		String returnS = "";
		
		
		for (int i = grad; i>=0; i-- ){
			returnS += koef[i] + "x^" + i + " ";
			
			if (i > 0)
				returnS += "+ ";
			
		}
		
		
		return returnS;
	}
	
}

public class MyApplication
{
	public static void main(String[] args)
	{
	
		//erzeuge 2, 2, 2, 2, 2
		double[] zweier = {2,2,2,2,2,2,2};
		double[] dreier = {1,2,3,4,5,6,7};
		
		polynom pol1 = new polynom(zweier.length-1, zweier) ;
		
		polynom pol2 = new polynom(zweier.length-1, dreier) ;
		
		System.out.println("Teste addition");
		
		polynom ergebnisAddition = polynom.addition(pol1, pol2,3);
		
		System.out.println(ergebnisAddition.toString());
		
		System.out.println("---------------------------------\n\n");
		
		System.out.println("Teste naive multiplikation");
		
		polynom ergebnisMultiNav = polynom.multNaiv(pol1, pol2);
		
		System.out.println(ergebnisMultiNav.toString());
		
		System.out.println("---------------------------------\n\n");
		
		
	}
}