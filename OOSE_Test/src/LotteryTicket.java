import java.lang.*;

public class LotteryTicket {
	private final int[] numbers;
	private final int[] sortedNumbers;
	
	public LotteryTicket(int[] eingabe) {
		checkInput(eingabe);
		numbers = eingabe;
		sortedNumbers = Mergesort.sort(numbers);
	}
	
	public LotteryTicket(int a1, int a2, int a3, int a4, int a5, int a6) {
		int[] arr = {a1, a2, a3, a4, a5, a6};
		checkInput(arr);
		numbers = arr;
		sortedNumbers = Mergesort.sort(arr);
	}
	
	private void checkInput(int[] arr) {
		if(arr == null || arr.length != 6) {
			System.out.println("Ungültige Eingabe! (keine/zuviele/zuwenig Zahlen)");
			System.exit(-1);
		}
		for(int i = 0; i < 6; i++) {
			if(arr[i] < 1 || arr[i] > 46) {
				System.out.println("Ungültige Eingabe! (Zahlen außerhalb [1:46])");
				System.exit(-1);
			}
		}
		for(int i = 0; i < 6 ; i++) {
			for(int j = i+1 ; j < 6 ; j++) {
				if(arr[i] == arr[j]) {
					System.out.println("Ungültige Eingabe! (Zahl kommt doppelt vor)");
					System.exit(-1);
				}
			}
		}
	}

	public boolean equals(LotteryTicket other) {
		for(int i = 0 ; i < 6 ; i++) {
			if(this.sortedNumbers[i] != other.sortedNumbers[i]) return false;
		}
		return true;
	}
	
	public int hashCode() {
		return sortedNumbers.hashCode();
	}
	
	public int[] getSortetNumbers() {
		int[] out = sortedNumbers.clone();
		return out;
	}
	
	public String toString() {
		String returnStr = "[ ";
		for(int i = 0; i < 6 ; i++) {
			returnStr += sortedNumbers[i];
			if(i<5) returnStr += ", ";
		}
		returnStr += " ]";
		
		return returnStr;
	}
	
	public static void main(String[] args) {
		int[] arr = {5, 12, 43, 25, 18, 33};
		LotteryTicket a = new LotteryTicket(arr);
		System.out.println(a.toString());
	}
	
	static class Mergesort{
		
		public static int[] sort(final int[] input) {
			int[] output = input.clone();
			
			assert output.length > 0;
			if(output.length == 1) return output;
			
			int half = input.length/2;
			int[] left = new int[half];
			int[] right = new int[output.length - half];
			
			for(int i = 0;i < output.length;i++) {
				if(i<half) left[i] = output[i];
				else right[i-half] = output[i];
			}
			
			left = sort(left);
			right = sort(right);
			
			int j = 0;
			int k = 0;
			
			for(int i = 0;i < output.length;i++) {
				if(j < left.length && k < right.length) {
					if(left[j] < right[k]) {
						output[i] = left[j];
						j++;
					}
					else {
						output[i] = right[k];
						k++;
					}
				}
				else if(j < left.length) {
					output[i] = left[j];
					j++;
				}
				else { assert k < right.length;
					output[i] = right[k];
					k++;
				}
			}
			
			return output;
		}
	}
}