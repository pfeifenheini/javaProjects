import static java.lang.Math.*;
import java.util.Random;
//Betrachtung eines Polynoms: Poyls werden als Arrays gespeichert, Position 0 gibt die verschiebung an (nur bei kara relevant) Position n != 0 gibt den wert des koeffizenten von x^(n-1) an.
public class Polynom {
	static int counter_mult_sdt = 0;
	static int counter_mult_karatsuba = 0;
	
	
	void print_polynom(double[] poly){
		System.out.print(poly[0]+"  ");
		for(int n=1;n<poly.length;n++){ //das 1. Element wird gesondert behandelt
			if(poly[n]>=0) {
			System.out.printf("+"+poly[n]+"x^"+(n-1+poly[0])+" ");
			}
			else {
			System.out.printf(+poly[n]+"x^"+(n-1+poly[0])+" ");
			}
		}
	}
	double[] norm(double[] poly, int max_poly_length) { //erzeugt ein Array (Polynom) der größe 2n und füllt fehlende Plätze mit 0 auf
		if(max_poly_length >= poly.length) { //max_poly_length endspricht der länge des späteren Arrays und muss >= der länge des input Arrays sein
			int length=0; 
			
			if(max_poly_length%2 == 0) { //Fall: max_poly_length == 2n => größe ok
				length = max_poly_length;
			}
			else { //Fall: max_poly_length != 2n => max_poly_length++ == 2n
				length = max_poly_length+1;
			}
			double[] res = new double[length];
			for(int i=0; i<res.length;i++) { //Zielarray wird mit 0 angefüllt
				res[i]=0;
			}
			for(int i=0; i<poly.length;i++) { //input Array wird ins Ziel Array kopiert, sofern möglich
				res[i]=poly[i];
			}
			return res;
		}
		else {
			return null;
		}
	}
	double[] norm_kara(double[] poly){ //normiert ein vom kara algo benutzes poly
		double[] res = new double[(int) poly[0]+2];
		for(int i=0; i<res.length;i++) { //Zielarray wird mit 0 angefüllt
			res[i]=0;
		}
		res[(int)poly[0]+1]=poly[1]; //polys müssen genau die länge 2 haben
		return res;
		
	}
	double[] norm_2(double[] poly, int max_poly_length){ //normt die eingabe zu einer 2er Potenz + 1 (head)
		if(max_poly_length >= poly.length) { 
			int length = max_poly_length;
			Integer check = new Integer(max_poly_length);
			while(check.bitCount(length-1)!=1){ //überprüfung auf 2er Potenz
				length++;
			}		
			double[] res = new double[length];
			for(int i=0; i<res.length;i++) { //Zielarray wird mit 0 angefüllt
				res[i]=0;
			}
			for(int i=0; i<poly.length;i++) { //input Array wird ins Ziel Array kopiert, sofern möglich
				res[i]=poly[i];
			}
			return res;
		}
		else {
			System.out.println("Error: denorm");
			return null;
		}
	}
	double[] cut_left(double[] poly, int end){ //kopiert von poly[0] bis poly[end] alle Elemente von poly in das output Array
		if(poly.length > end) {
			if(poly.length == 2){
				return poly;
			} 
			else {		
				int res_index = end;
				double[] res = new double[res_index+1];
				for(int i=0;i<res.length;i++){
					res[i]=poly[i];
				}
			return res;
			}
		}
		else {
			System.out.println("Error: end >= poly_a bei cut_left");
			return null;
		}
	}
	double[] cut_right(double[] poly, int begin){ //kopiert von poly[beginn+1] bis poly[poly.length] alle Elemente von poly in das output Array res, dabei ist die Verschiebung res[0]+=beginn
		if(poly.length > begin) {
			if(poly.length == 2){
				return poly;
			} 
			else {
				int res_index=(poly.length-begin);
				double[] res = new double[res_index];
				res_index = 1;
				res[0] = poly[0] + begin;			
			
				for(int i=1;i<res.length;i++){
					res[i]=0; //initalisierung
				}
				for(int i=begin+1;i<poly.length;i++){
					res[res_index]=poly[i];
					res_index++;
				}
				return res;
			}
		}
		else {
			System.out.println("Error: begin >= poly_a bei cut_right");
			return null;
		}
	}
	double[] add(double[] poly_a, double[] poly_b){ //Addition von Polynomen
		poly_a = norm(poly_a, (max(poly_a.length, poly_b.length)));
		poly_b = norm(poly_b, (max(poly_a.length, poly_b.length)));
		double[] res = new double[poly_a.length]; //poly_a und poly_b haben die gleiche Länge
				
		for(int n = 2; n<res.length; n++) {
			res[n] = poly_a[n] + poly_b[n];
		}
		return res;
	}
	double[] mult_std(double[] p, double[] q) {
		double[] r = new double[(p.length-1)+(q.length-1)+1];
		for (int i = 1; i < p.length; i++) {
			for (int j = 1; j < q.length; j++) {
				r[i+j-1] = r[i+j-1] + p[i]*q[j];
			}
		}
	return r;
	}
	double[] mult_karatsuba_recursion(double[] poly_a, double[] poly_b){ //Kopf der Karatsuba funktion, hier werden nur die eingaben auf ungrade länge gebracht
		poly_a=norm_2(poly_a,(max(poly_a.length, poly_b.length)));
		poly_b=norm_2(poly_b,(max(poly_a.length, poly_b.length)));
		double[] res = mult_karatsuba_recursion_intern(poly_a,poly_b);
		return res;
	}
	private double[] mult_karatsuba_recursion_intern(double[] poly_a, double[] poly_b){ //rekursive karatsuba funktion
		
		if (poly_a.length == 2 && poly_b.length == 2) {  //trivial Fall
			double[] res = new double[2];
			res[0]=poly_a[0]+poly_b[0];
			res[1]=poly_a[1]*poly_b[1];
			res=norm_kara(res);
			return res;
		} else {
			//System.out.println("b");
			// 1. Schritt: Teile die 2 großen Polynome in 4 kleinere Polynome
			
			
			
			double[] P0 = cut_left(poly_a, (int)(poly_a.length-1)/2); //anfang bis Mitte von poly_a
			double[] P1 = cut_right(poly_a, (int)((poly_a.length-1)/2)); //Mitte bis Ende von poly_a
			double[] Q0 = cut_left(poly_b, (int)(poly_b.length-1)/2); //anfang bis Mitte von poly_b
			double[] Q1 = cut_right(poly_b, (int)((poly_b.length-1)/2)); //Mitte bis Ende von poly_b

			// 2. Schritt: poly_a*poly_b = P0Q0 + (P0 + P1)*(Q0 + Q1) + P1Q1
			return(add(add(mult_karatsuba_recursion_intern(P0,Q0),add(mult_karatsuba_recursion_intern(P0,Q1),mult_karatsuba_recursion_intern(P1,Q0))),mult_karatsuba_recursion_intern(P1,Q1)));

		}
	}
	static double[] mult_karatsuba(double[] poly1, double[] poly2){ //2. versuch eines kara algos
		double[] pl, pm, pr, ql, qm, qr, zl, zm, zr, r;
		
		double[] pol1;
		double[] pol2;
		
		if(poly1.length%2==1) //überprüfung ob eingbae arrays durch 2 teilbar und evtl fehlerbehebung
			pol1 = new double[poly1.length+1];
		else 
			pol1 = new double[poly1.length];
		
		if(poly2.length%2==1)  //überprüfung ob eingbae arrays durch 2 teilbar und evtl fehlerbehebung
			pol2 = new double[poly2.length+1];
		else
			pol2 = new double[poly2.length];
		
		for(int i=0;i<poly1.length;i++){ //arry befüllungs Hilfsfunktion 
			pol1[i]=poly1[i];
			pol2[i]=poly2[i];
		}
		
		if((poly1.length)==1){ //trivialfall
			r = new double[1];
			r[0] = poly1[0]*poly2[0];
			return r;
		}else{ //aufteling der polys
			pl = new double[(pol1.length)/2];
			pr = new double[(pol1.length)/2];
			ql = new double[(pol1.length)/2];
			qr = new double[(pol1.length)/2];
			
			for(int i=0;i<(pol1.length)/2;i++){
				pl[i] = pol1[i];
				ql[i] = pol2[i];
			}
			for(int i=(pol1.length)/2;i<(pol1.length);i++){
				pr[i-(pol1.length)/2] = pol1[i];
				qr[i-(pol1.length)/2] = pol2[i];
			}
			
			pm = new double[(pol1.length)/2];
			qm = new double[(pol1.length)/2];
			
			for(int i=0;i<(pol1.length)/2;i++){
				pm[i] = pl[i]+pr[i];
			}
			for(int i=0;i<(pol1.length)/2;i++){
				qm[i] = ql[i]+qr[i];
			}
			//rekursiover Teil
			zl = mult_karatsuba(pl, ql);
			zm = mult_karatsuba(pm, qm);
			zr = mult_karatsuba(pr, qr);
			
			r = new double[2*(pol1.length)-1];
			
			for(int i=0;i<(pol1.length)-1;i++){
				r[i]=zl[i];
				r[(pol1.length)-1]=0;
			}
			for(int i=0;i<(pol1.length)-1;i++){
				r[(pol1.length)+i]=zr[i];
			}
			for(int i=0;i<(pol1.length)-1;i++){
			r[(pol1.length)/2+i]+=zm[i]-(zl[i]+zr[i]);}
			
			return r;
		}
	}
	double[] gen_poly(int degree){
		Random generator = new Random();
		double[] res = new double[degree+1];
		
		for(int i=0;i<res.length;i++){
			res[i] = generator.nextInt(10);
		}
		return res;
	}
	double[] gen_poly_r(int degree){
		Random generator = new Random();
		double[] res = new double[degree+1];
		
		res[0] = 0;
		for(int i=1;i<res.length;i++){
			res[i] = generator.nextInt(10);
		}
		return res;
	}
	double[] mult(double[] poly_a, double[] poly_b){ //ungefärer wert bei dem kara schneller ist liegt bei 90000
		if(poly_a.length >= 90000 || poly_b.length >= 90000){
			return mult_karatsuba(poly_a, poly_b);
		}
		else {
			return mult_std(poly_a, poly_b);
		}
	}
	
}
