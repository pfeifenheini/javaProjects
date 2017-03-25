import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class ScatteredStrategy extends Strategy {
	
	private Coordinate[] sequence;
	
	public ScatteredStrategy() {
		super();
		
		nonBurningPerLevel = new int[xBoundary];
		grid = new int[Strategy.xBoundary][Strategy.yBoundary];
		sequence = new Coordinate[(int)((yBoundary)*Math.max(budged,1)+1)];
		
//		for(int i=0;i<sequence.length;i++)
//			sequence[i] = new Coordinate(rand.nextInt(xBoundary-1),rand.nextInt(yBoundary));
		
		for(int i=0;i<sequence.length;i++) {
			if(i%2 == 0)
				sequence[i] = new Coordinate(xBoundary-2,Math.min(yBoundary/2+i/2,yBoundary-1));
			else
				sequence[i] = new Coordinate(xBoundary-2,Math.max(yBoundary/2-i/2,0));
		}
	}
	
	public ScatteredStrategy(ScatteredStrategy toClone) {
		super();
		
		sequence = new Coordinate[(int)((yBoundary)*Math.max(budged,1)+1)];
		for(int i=0;i<sequence.length;i++)
			sequence[i] = new Coordinate(toClone.sequence[i].x,toClone.sequence[i].y);
		if(!toClone.changed)
			changed = false;
	}
	
	public ScatteredStrategy(ScatteredStrategy parent1, ScatteredStrategy parent2) {
		super();
		
		sequence = new Coordinate[(int)((yBoundary)*Math.max(budged,1)+1)];
//		int crossPoint = rand.nextInt(sequence.length);
		for(int i=0;i<sequence.length;i++) {
			if(rand.nextBoolean())
				sequence[i] = new Coordinate(parent1.sequence[i].x,parent1.sequence[i].y);
			else
				sequence[i] = new Coordinate(parent2.sequence[i].x,parent2.sequence[i].y);
		}
	}
	
	/**
	 * 
	 * @param force
	 * @return true if the simulation has been calculated new
	 */
	public boolean simulate(boolean force, boolean printGrid) {
		if(!changed && !force) return false;
		
		clearGrid();
		
		double account = initialAccount;
		int iterations = yBoundary/2; //TODO think about iterations
		int protectIterator = 0;
		
		int startFireX = xBoundary/3;
		int startFireY = yBoundary/2; 
		
		grid[startFireX][startFireY] = BURNING;
		Queue<Coordinate> burningBoundary = new LinkedList<Coordinate>();
		burningBoundary.add(new Coordinate(startFireX,startFireY));
		timeToReachHighway = 0;
		for(int i=0;i<iterations && !burningBoundary.isEmpty();i++) {
			while(account >= 1 && protectIterator<sequence.length) {
				if(protect(sequence[protectIterator])) {
					account -= 1.0;
				}
				protectIterator++;
			}
			
			if(printGrid) {
				printGrid();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(!spreadFire(burningBoundary)) {
				timeToReachHighway++;
			}
			account += budged;
		}
		nonBurningHighwayCells = 0;
		for(int i=0;i<yBoundary;i++) {
			if(grid[xBoundary-1][i] != BURNING)
				nonBurningHighwayCells++;
		}
		
		for(int i=0;i<xBoundary;i++) {
			nonBurningPerLevel[i] = 0;
			for(int j=0;j<yBoundary;j++) {
				if(grid[i][j] != BURNING)
					nonBurningPerLevel[i]++;
			}
		}
		
		changed = false;
//		printGrid(grid);
//		System.out.println("Time: " + timeToReachHighway);
//		System.out.println("NonBurning: " + nonBurningHighwayCells);
//		System.out.println("Fitness:" + fitness());
		return true;
	}
	
	public int wiggleOffset() {
		double gaus = rand.nextGaussian();
		if(gaus > 0)
			return ((int)(gaus*wiggleSize)+1);
		else
			return ((int)(gaus*wiggleSize)-1);
	}
	
	public void mutate() {
		
		double version = rand.nextDouble();
		
		int xChange, yChange, swap;
		Coordinate tmp;
		for(int i=0;i<sequence.length;i++) {
			if(rand.nextDouble() <= mutationRate/sequence.length) {
				if(version < mutationVersionWiggle) {
					xChange = wiggleOffset();
					yChange = wiggleOffset();
					
					if(rand.nextBoolean())
						sequence[i].x += xChange;
					if(rand.nextBoolean())
						sequence[i].y += yChange;
					
					if(sequence[i].x < 0)
						sequence[i].x = 0;
					if(sequence[i].x > xBoundary-2)
						sequence[i].x = xBoundary-2;
					if(sequence[i].y < 0)
						sequence[i].y = 0;
					if(sequence[i].y > yBoundary-1)
						sequence[i].y = yBoundary-1;
				}
				else if(version < mutationVersionWiggle+mutationVersionSwap) {
					swap = rand.nextInt(sequence.length);
					tmp = sequence[swap];
					sequence[swap] = sequence[i];
					sequence[i] = tmp;
				}
				else {
					if(rand.nextDouble() <= mutationRate/sequence.length) {
						sequence[i] = new Coordinate(rand.nextInt(xBoundary-1),rand.nextInt(yBoundary));
					}
				}
			}
			changed = true;
		} 
	}
}
