import java.util.Arrays;
import java.util.Random;
/**
 * Compile with:
 *  javac MonteCarloEval.java
 * Run with:
 *  java MonteCarloEval
 * 
 * @author Martin
 *
 */
public class MonteCarloEval {
	/** random number generator */
	Random rand;
	
	/** current state of the robot */
	int state;
	/** counts number of steps needed to reach state 0 for each start position */
	int[] F;
	/** counts the number of runs that have started from the same position */
	int[] runsPerStartPosition;
	
	public MonteCarloEval(long seed){
		if(seed == 0)
			rand = new Random();
		else
			rand = new Random(seed);
		
		state = rand.nextInt(9)+1;
		F = new int[11];
		runsPerStartPosition = new int[11];
	}

	private void simulate(int steps) {
		
		// reset counter for each position
		for(int i=0;i<F.length;i++) {
			F[i] = 0;
			runsPerStartPosition[i] = 0;
		}
		
		int progress = 0; // used to display progress
		
		// main loop, repeating the experiment 'steps' times
		for(int step=0;step<steps;step++) {
			state = rand.nextInt(10)+1; // initialize start position 
			int start = state; // remember start pposition
			runsPerStartPosition[start]++;
			
			// Perform experiment. As long as the robot does not reach state 0,
			// increase or decrease the current state randomly. From state 10
			// only state 9 is reachable.
			while(state != 0) {
				if(rand.nextBoolean()) {
					if(state<10)
						state++;
					else
						state--;
				}
				else
					state--;
				F[start]--;
			}
			
			// displays progress
			if((double)step/steps>((double)progress+10)/100) {
				progress+=10;
				System.out.println(progress+ "%");
			}
		}
		
		// calculates the average steps needed per start position
		for(int i=0;i<F.length;i++) {
			if(runsPerStartPosition[i] != 0)
				F[i] = F[i]/runsPerStartPosition[i];
		}
		
		// print result to standard output
		System.out.println("start   steps");
		for(int i=0;i<F.length;i++) {
			System.out.format("%3d    %5d\n",i,F[i]);
		}
	}
	
	public static void main(String[] args) {
		MonteCarloEval m = new MonteCarloEval(0);
		m.simulate(1000000);
	}
}
