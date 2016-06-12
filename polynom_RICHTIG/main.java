import static java.lang.Math.*;
public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Polynom poly = new Polynom();
		Integer n = new Integer(42);
		double[] p_1 = poly.gen_poly(100000);
		double[] p_2 = poly.gen_poly(100000);
		//double[] p_1 = {0,2,1,3,4,5,6,8};
		//double[] p_2 = {0,1,5,3,4,5,6,8,9};
		//System.out.print("p_1: ");
		//poly.print_polynom(p_1);
		//System.out.println();
		//System.out.print("p_2: ");
		//poly.print_polynom(p_2);
		//System.out.println();
		//System.out.println();
		long start = System.currentTimeMillis(); 
		poly.mult_karatsuba(p_1, p_2);
		//poly.print_polynom(poly.mult_karatsuba(p_1, p_2));
		System.out.println();
		System.out.println("Duration in ms: " + (System.currentTimeMillis() - start));
		System.out.println();
		start = System.currentTimeMillis();
		poly.mult_std(p_1, p_2);
		//poly.print_polynom(poly.mult_std(p_1, p_2));
		System.out.println();
		System.out.println("Duration in ms: " + (System.currentTimeMillis() - start));
	}
}
