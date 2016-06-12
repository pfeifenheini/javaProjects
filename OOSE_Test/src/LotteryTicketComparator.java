import java.util.Comparator;

public class LotteryTicketComparator implements Comparator<LotteryTicket> {
	
	public int compare(LotteryTicket a, LotteryTicket b) {
		int[] arr1 = a.getSortetNumbers();
		int[] arr2 = b.getSortetNumbers();
		for(int i = 0; i < 6 ; i++) {
			if(arr1[i] < arr2[i]) return -1;
			if(arr1[i] > arr2[i]) return 1;
		}
		return 0;
	}
}
