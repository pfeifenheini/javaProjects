
public class TNN2Perzeptron {

	public static void main(String[] args) {
		Perzeptron p = new Perzeptron(2,1);
		p.print();
		double[] input = new double[2];
		
		for(int i=0;i<2;i++) {
			for(int j=0;j<2;j++) {
				input[0] = i;
				input[1] = j;
				double[] output = p.calculate(input);
				System.out.println("(" + i + "," + j + ") --> " + output[0]);
			}
		}
		
	}

}
