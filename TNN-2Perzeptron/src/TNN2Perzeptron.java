
public class TNN2Perzeptron {

	public static void main(String[] args) {
		int n = 5, m = 2;
		Perzeptron p = new Perzeptron(n,m,Perzeptron.TransferFunction.FERMI);
		System.out.println(p.toString());
		p.train("PA-A-train.dat");
	}
}
