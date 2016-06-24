
public class NormierteRegistermaschine {

	private static int n = 3;
//	private static boolean slow = false;
	private static boolean slow = true;
	private int[] r;
	
	public NormierteRegistermaschine(int mem) {
		r = new int[mem];
	}
	
	public int fib(int n) {
		r[1] = n;
		printReg();
		System.out.println("");
		program();
		return r[1];
	}
	
	private void program() {
		a(2);
		while(r[1] != 0) {
			s(1);
			while(r[3] != 0) {s(3);a(4);}
			while(r[2] != 0) {s(2);a(3);a(4);}
			while(r[4] != 0) {s(4);a(2);}
		}
		while(r[2] != 0) {s(2);a(1);}
	}
	
	private void a(int i) {
		r[i]++;
		printReg();
		if(slow)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.out.println(" a(" + i + ")");
	}
	
	private void s(int i) {
		r[i]--;
		printReg();
		if(slow)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.out.println(" s(" + i + ")");
	}
	
	public void printReg() {
		System.out.print("[");
		for(int i=1;i<r.length-1;i++) {
			System.out.print(r[i] + ", ");
		}
		System.out.print(r[r.length-1]);
		System.out.print("]");
	}
	
	public static void main(String[] args) {
		NormierteRegistermaschine m = new NormierteRegistermaschine(11);
		System.out.println(m.fib(n));
	}

}
