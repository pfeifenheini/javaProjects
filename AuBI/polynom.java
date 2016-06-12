class polynom{
	int grad;
	double[] koef;
	
	public polynom() //Empfängt der Konstruktor keine Parameter, wird ein Polynom mit Grad Eins erstellt
	{
		grad = 0;
		koef = new double[1];
	}
	
	public polynom(int n) //Wird eine Ganzzahl übergeben, erhält das Polynom diesen Grad
	{
		grad = n;
		koef = new double[n+1];
	}
	
	public polynom(int n, double array[]) //Wird noch zusätzlich ein Array übergeben, werden in den Koeffizienten entsprechend die Werte übernommen. Dabei muss das array genau n+1 Einträge haben und Array[i] wird zum Koeffizienten von x^i
	{
		int i;
		
		grad = n;
		koef = new double[n+1];
		
		for(i=0;i<=n;i++)
		{
			koef[i] = array[i];
		}
	}
	
	static int maximum(int a, int b) //Bestimmt das Maximum der beiden Zahlen a und b
	{
		if(a>b)
			return a;
		else
			return b;
	}
	
	static int zweierpotenz(int a, int b) //Bestimmt die kleinste Zweierpotenz, die größer ist als das Maximum von a und b
	{
		int n=1;
		a = maximum(a,b);
		while(n<a)
		{
			n = n*2;
		}
		return n;
	}
	
	static polynom normalisieren(polynom pol1, int grad) //Verlängert die Liste mit den Koeffizienten und füllt die neuen Plätze mit Null auf
	{
		int i;
		double[] neu1 = new double[grad+1];
		
		for(i=0;i<=grad;i++)
		{
			neu1[i] = 0;
			if(i<=pol1.grad)
			{
				neu1[i] = pol1.koef[i];
			}
		}
		pol1.koef = neu1;
		pol1.grad = grad;
		return pol1;
	}
	
	static polynom add(polynom pol1, polynom pol2) //Addiert die beiden Polynome pol1 und pol2 und gibt das Ergebnispolynom zurück
	{
		int i;
		polynom ergebnis = new polynom( maximum(pol1.grad,pol2.grad)); //Ergebnispolynom wird erstellt. Das Ergebnis hat den Grad n, vobei n der größere Grad der beiden Polynome pol1, pol2 ist
		
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
	
	static polynom add(polynom pol1, polynom pol2, int n) //Hier wird das zweite Polynom zunächst mit x^n Multipliziert und erst dann addiert
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
				if(i-n>=0)
				{
					ergebnis.koef[i] = pol1.koef[i]+pol2.koef[i-n];
				}
				else
				{
					ergebnis.koef[i] = pol1.koef[i];
				}
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
	
	static polynom subtraktion(polynom pol1, polynom pol2) //Polynom pol2 wird von Polynom pol1 abgezogen
	{
		polynom ergebnis = new polynom(maximum(pol1.grad,pol2.grad));
		int i;
		
		for(i=0;i<=ergebnis.grad;i++)
		{
			if(i<=pol1.grad && i<=pol2.grad)
			{
				ergebnis.koef[i] = pol1.koef[i] - pol2.koef[i];
			}
			else if(i<=pol1.grad)
			{
				ergebnis.koef[i] = pol1.koef[i];
			}
			else
			{
				ergebnis.koef[i] = -pol2.koef[i];
			}
		}
		
		return ergebnis;
	}
	
	static polynom multNaiv(polynom pol1, polynom pol2) //Der naive Divide-and-Conquer Algorithmus zur Multiplikation zweier Polynom, welcher keine Laufzeiverbesserung zur Schulmethode liefert
	{
		int i;
		
		int neuerGrad = zweierpotenz(pol1.grad+1,pol2.grad+1)-1; //Die Polynome müssen dazu "normalisiert" werden. Das heißt die Anzahl der Koeffizienten muss einer Zweierpotenz entsprechen
		
		if (neuerGrad == 0){ //Falls die beiden Polynome bereits Grad Null haben, werden einfach die Beiden Zahlen multipliziert und das Ergebnis zurückgegeben
			polynom ergebnis = new polynom(0);
			ergebnis.koef[0] = pol1.koef[0] * pol2.koef[0];
			return ergebnis;
		}
		
		if(pol1.grad != neuerGrad) //Normalisierung von pol1, falls nötig
			pol1 = normalisieren(pol1,neuerGrad);
		if(pol2.grad != neuerGrad) //Normalisiertung von pol2, falls nötig
			pol2 = normalisieren(pol2,neuerGrad);
		
		//Aufteilen der Beiden Polynome jeweils in ein linkes und rechtes
		polynom p_l = new polynom((pol1.grad+1)/2-1);
		polynom p_r = new polynom((pol1.grad+1)/2-1);
		polynom q_l = new polynom((pol2.grad+1)/2-1);
		polynom q_r = new polynom((pol2.grad+1)/2-1);
		
		for(i=0;i<(pol1.grad+1)/2;i++) //Hier werden die Koeffizienten entsprechend kopiert
		{
			p_l.koef[i] = pol1.koef[i];
			p_r.koef[i] = pol1.koef[i+p_l.grad+1];
			q_l.koef[i] = pol2.koef[i];
			q_r.koef[i] = pol2.koef[i+q_l.grad+1];
		}
		//Aufteilung beendet
		
		polynom summand1, summand2, summand3, summand4;
		
		//Berechnung der Teilmultiplikationen
		summand1 = multNaiv(p_l,q_l);
		summand2 = multNaiv(p_l,q_r);
		summand3 = multNaiv(p_r,q_l);
		summand4 = multNaiv(p_r,q_r);
		
		//Die Summe der Teilergebnisse wird berechnet p_l*q_l + (p_l*q_r + p_r*q_l)*x^(n/2) + p_r*q_r*x^n
		summand2 = add(summand2,summand3);
		summand1 = add(summand1,summand2,(pol1.grad+1)/2);
		summand1 = add(summand1,summand4,pol1.grad+1);
		
		return summand1;
	}
	
	static polynom multKaracuba(polynom pol1, polynom pol2) //Der Divide-and-Conquer Algorithmus zur berechnung der Produkts zweier Polynome nach der Karacuba-Methode
	{
		int i;
		int neuerGrad = zweierpotenz(pol1.grad+1,pol2.grad+1)-1; //Die Polynome müssen dazu "normalisiert" werden. Das heißt die Anzahl der Koeffizienten muss einer Zweierpotenz entsprechen
		
		if (neuerGrad == 0){ //Falls die beiden Polynome bereits Grad Null haben, werden einfach die Beiden Zahlen multipliziert und das Ergebnis zurückgegeben
			polynom ergebnis = new polynom(0);
			ergebnis.koef[0] = pol1.koef[0] * pol2.koef[0];
			return ergebnis;
		}
		
		if(pol1.grad != neuerGrad) //Normalisierung von pol1, falls nötig
			pol1 = normalisieren(pol1,neuerGrad);
		if(pol2.grad != neuerGrad) //Normalisierung von pol2, falls nötig
			pol2 = normalisieren(pol2,neuerGrad);
		
		//Aufteilen der Beiden Polynome jeweils in ein linkes und rechtes
		polynom p_l = new polynom((pol1.grad+1)/2-1);
		polynom p_r = new polynom((pol1.grad+1)/2-1);
		polynom q_l = new polynom((pol2.grad+1)/2-1);
		polynom q_r = new polynom((pol2.grad+1)/2-1);
		
		for(i=0;i<(pol1.grad+1)/2;i++)//Hier werden die Koeffizienten entsprechend kopiert
		{
			p_l.koef[i] = pol1.koef[i];
			p_r.koef[i] = pol1.koef[i+p_l.grad+1];
			q_l.koef[i] = pol2.koef[i];
			q_r.koef[i] = pol2.koef[i+q_l.grad+1];
		}
		//Aufteilung beendet
		
		polynom z_l, z_m, z_r, p_m, q_m;
		
		
		//Berechnung der Teilpolynome
		p_m = add(p_l,q_l);
		q_m = add(q_l,q_r);
		
		//Nur drei Teilprodukte müssen berechnet werden
		z_l = multKaracuba(p_l,q_l);
		z_r = multKaracuba(p_r,q_r);
		z_m = multKaracuba(p_m,q_m);
		
		
		//Teilergebnisse werden zusammengerechnet: z_l + (z_m - z_l - z_r)*x^(n/2) + z_r*x^n
		z_m = subtraktion(z_m,z_l);
		z_m = subtraktion(z_m,z_r);
		z_l = add(z_l,z_m,(neuerGrad+1)/2);
		z_l = add(z_l,z_r,neuerGrad+1);
		
		return z_l;
	}
	
	static polynom multAllgemein(polynom pol1, polynom pol2) //Multiplikation zweier Polynome nach der Schulmethode
	{
		int i, j;
		polynom ergebnis = new polynom(pol1.grad+pol2.grad);
		
		for(i=0;i<=ergebnis.grad;i++)
		{
			ergebnis.koef[i] = 0;
		}
		
		for(i=0;i<=pol1.grad;i++)
		{
			for(j=0;j<=pol2.grad;j++)
			{
				ergebnis.koef[i+j] = ergebnis.koef[i+j] + pol1.koef[i] * pol2.koef[j];
			}
		}
		return ergebnis;
	}
	
	
	public String toString(){
		String returnS = "";
		
		
		for (int i = 0; i<=grad; i++ ){
			returnS += koef[i] + "x^" + i + " ";
			
			if (i < 0)
				returnS += "+ ";
			
		}
		
		
		return returnS;
	}
	
	static polynom mult(polynom pol1, polynom pol2) //Entscheidet je nach Eingabe welcher Algorithmus zur Berechnung des Prdukts aufgerufen werden soll
	{
		polynom ergebnis = new polynom();
		//if(maximum(pol1.grad,pol2.grad)>122000)
		if(maximum(pol1.grad,pol2.grad)>24500)
			ergebnis = multKaracuba(pol1,pol2);
		else
			ergebnis = multAllgemein(pol1,pol2);
		return ergebnis;
	}
}