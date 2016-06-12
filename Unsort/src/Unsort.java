
public class Unsort {
	public static void main(String[] args) {
		int n = 10;
		SortetList L = new SortetList(n);
		System.out.println(L.toString());
		
		int[] A = new int[n];
		int index;
		for(int i=n-1;i>=0;i--) {
			index = rand(i+1);
			System.out.println("Entfern = " + index);
			A[i] = L.pop(index);
			System.out.println(L.toString());
		}
		
		System.out.print("A = [ ");
		for(int i=0;i<A.length-1;i++) {
			System.out.print(A[i] + ", ");
		}
		System.out.println(A[n-1] + " ]");
	}
	
	static int rand(int max) {
		return (int) (Math.random() * (max)+1);
	}
}
