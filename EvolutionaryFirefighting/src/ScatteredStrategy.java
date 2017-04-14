import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class ScatteredStrategy extends Strategy {
	
	/** this sequence tells which cell to protect next */
	private Coordinate[] sequence;
	
	/**
	 * Constructor
	 */
	public ScatteredStrategy() {
		super();
		
		nonBurningPerLevel = new int[xBoundary];
		grid = new int[Strategy.xBoundary][Strategy.yBoundary];
		sequence = new Coordinate[(int)((xBoundary+yBoundary)*Math.max(budget,1)+1)];
		
		if(initializeRandom) {
			for(int i=0;i<sequence.length;i++)
				sequence[i] = new Coordinate(rand.nextInt(xBoundary-1),rand.nextInt(yBoundary));
		}
		else {
			for(int i=0;i<sequence.length;i++) {
				if(i%2 == 0) {
					sequence[i] = new Coordinate(xBoundary-2,Math.min(yBoundary/2+i/2,yBoundary-1));
				}
				else {
					sequence[i] = new Coordinate(xBoundary-2,Math.max(yBoundary/2-i/2,0));
				}
			}
		}
	}
	
	/**
	 * Creates a new Strategy given two parent strategies by randomly mixing their genome.
	 * @param parent1
	 * @param parent2
	 */
	public ScatteredStrategy(ScatteredStrategy parent1, ScatteredStrategy parent2) {
		super();
		
		sequence = new Coordinate[(int)((xBoundary+yBoundary)*Math.max(budget,1)+1)];
		for(int i=0;i<sequence.length;i++) {
			if(rand.nextBoolean())
				sequence[i] = new Coordinate(parent1.sequence[i].x,parent1.sequence[i].y);
			else
				sequence[i] = new Coordinate(parent2.sequence[i].x,parent2.sequence[i].y);
		}
	}
	
	@Override
	public boolean simulate(boolean force, boolean printGrid, boolean saveAllSteps) {
		if(!changed && !force) return false;
		
		clearGrid();
		
		double account = initialAccount;
		int iterations = Math.max(xBoundary/2,yBoundary/2);
		int protectIterator = 0;
		
		grid[startFireX][startFireY] = 0;
		Queue<Coordinate> burningBoundary = new LinkedList<Coordinate>();
		burningBoundary.add(new Coordinate(startFireX,startFireY));
		timeToReachHighway = 0;
		if(saveAllSteps) {
			File dir = new File("steps");
			if(dir.exists()) {
				String[] toDelete = dir.list();
				for(String s:toDelete) {
					File curr = new File(dir.getPath(),s);
					curr.delete();
				}
			}
			else
				dir.mkdir();
			save("steps/step_0");
		}
		
		fitness = 1;
		for(int time=0;time<iterations && !burningBoundary.isEmpty();time++) {
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
			
			fitness += burningBoundary.size();
			
			if(burningBoundary.isEmpty())
				fireEnclosed = true;
			else
				timeToEncloseFire++;
			
			if(!spreadFire(burningBoundary,time+1)) {
				timeToReachHighway++;
				highwayProtected = true;
				if(burningBoundary.isEmpty()) {
					timeToReachHighway = iterations+1;
				}
			}
			else
				highwayProtected = false;
			
			account += budget;
			
			if(saveAllSteps)
				save("steps/step_" + (time+1));
		}
		
		fitness += burningBoundary.size();
		
		nonBurningHighwayCells = 0;
		for(int i=0;i<yBoundary;i++) {
			if(grid[xBoundary-1][i] < 0)
				nonBurningHighwayCells++;
		}
		
		totalSavedCells = 0;
		for(int i=0;i<xBoundary;i++) {
			nonBurningPerLevel[i] = 0;
			for(int j=0;j<yBoundary;j++) {
				if(grid[i][j] < 0) {
					nonBurningPerLevel[i]++;
					totalSavedCells++;
				}
			}
		}
		
		changed = false;
		return true;
	}

	@Override
	public void mutate() {
		
		double version = rand.nextDouble();
		
		int xChange, yChange, swap;
		Coordinate tmp;
		double chance = mutationRate/sequence.length;
		for(int i=0;i<sequence.length;i++) {
			if(rand.nextDouble() <= chance) {
				if(version < mutationVersionWiggle) {
					xChange = wiggleOffset();
					yChange = wiggleOffset();
					
					if(rand.nextBoolean())
						sequence[i].x += xChange;
					if(rand.nextBoolean())
						sequence[i].y += yChange;
					
					if(sequence[i].x < 0)
						sequence[i].x = 0;
					if(mode == Mode.ProtectHighway) {
						if(sequence[i].x > xBoundary-2)
							sequence[i].x = xBoundary-2;
					}
					else {
						if(sequence[i].x > xBoundary-1)
							sequence[i].x = xBoundary-1;
					}
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
					if(mode == Mode.ProtectHighway)
						sequence[i] = new Coordinate(rand.nextInt(xBoundary-1),rand.nextInt(yBoundary));
					else
						sequence[i] = new Coordinate(rand.nextInt(xBoundary),rand.nextInt(yBoundary));
				}
				chance = mutationRate/sequence.length;
			}
			changed = true;
		}
	}
}
