import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class ScatteredStrategy extends Strategy {
	public static final int FREE = 0;
	public static final int BURNING = 1;
	public static final int SAVE = 2;
	public static final int SAVEOFFSET = 3;
	
	
//	public static long seed = new Random().nextLong();
	public static long seed = 42;
	public static Random rand = null;
	
	public static int xBoundary = 33;
	public static int yBoundary = 103; // = 10*xBoundary+1
	
	public final static double initialAccount = 1.2;
	public final static double budged = 1.2;
	public final static double mutationRate = 0.5;
	public final static double mutationVersionWiggle = 0.6;
	public final static double wiggleSize = 1.0;
	public final static double mutationVersionSwap = 0.3;
	
	private int[][] grid;
	
	private Coordinate[] sequence;
	
	private int timeToReachHighway = 0;
	private int nonBurningHighwayCells = 0;
	private int[] nonBurningPerLevel;
	
	boolean changed = true;
	
	public Strategy() {
		if(rand == null)
			rand = new Random(seed);
		
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
		
//		for(int i=0;i<sequence.length;i++)
//		sequence[i] = new Coordinate(xBoundary-2,yBoundary-1);
	}
	
	public Strategy(Strategy toClone) {
		if(rand == null)
			rand = new Random(seed);
		
		nonBurningPerLevel = new int[xBoundary];
		grid = new int[Strategy.xBoundary][Strategy.yBoundary];
		sequence = new Coordinate[(int)((yBoundary)*Math.max(budged,1)+1)];
		for(int i=0;i<sequence.length;i++)
			sequence[i] = new Coordinate(toClone.sequence[i].x,toClone.sequence[i].y);
		if(!toClone.changed)
			changed = false;
	}
	
	public Strategy(Strategy parent1, Strategy parent2) {
		if(rand == null)
			rand = new Random(seed);
		
		nonBurningPerLevel = new int[xBoundary];
		grid = new int[Strategy.xBoundary][Strategy.yBoundary];
		sequence = new Coordinate[(int)((yBoundary)*Math.max(budged,1)+1)];
		int crossPoint = rand.nextInt(sequence.length);
		for(int i=0;i<sequence.length;i++) {
			if(rand.nextBoolean())
				sequence[i] = new Coordinate(parent1.sequence[i].x,parent1.sequence[i].y);
			else
				sequence[i] = new Coordinate(parent2.sequence[i].x,parent2.sequence[i].y);
		}
		
		changed = true;
	}
	
	//TODO correct fitness
	public double fitness() {
		if(changed) {
			System.out.println("need to create new grid");
			simulate(false,false);
		}
		
		double value = 0;
		int burningInLine;
		for(int i=0;i<xBoundary;i++) {
			
			burningInLine = 0;
			for(int j=0;j<yBoundary;j++) {
				if(grid[i][j] == BURNING)
					burningInLine++;
			}
			
			value += burningInLine*Math.pow(yBoundary, (double)i);
		}
		
		return Math.log(value);
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
	
	// returns true if a cell has been protected
	private boolean protect(Coordinate cell) {
		if(grid[cell.x][cell.y] != BURNING) {
			grid[cell.x][cell.y] = SAVE;
			return true;
		}
//		else {
//			int offset = 1;
//			while(cell.x+offset < xBoundary && grid[cell.x+offset][cell.y] != FREE) {
//				offset++;
//			}
//			if(cell.x+offset < xBoundary) {
//				grid[cell.x+offset][cell.y] = SAVEOFFSET;
//				return true;
//			}
//		}
		return false;
	}
	
	// return true if the highway is reached
	private boolean spreadFire(Queue<Coordinate> burningBoundary) {
		int steps = burningBoundary.size();
		Coordinate cell;
		for(int i=0;i<steps;i++) {
			cell = burningBoundary.remove();
			if(grid[Math.min(cell.x+1,xBoundary-1)][cell.y] == FREE) {
				grid[cell.x+1][cell.y] = BURNING;
				burningBoundary.add(new Coordinate(cell.x+1, cell.y));
			}
			if(grid[Math.max(cell.x-1,0)][cell.y] == FREE && cell.x != xBoundary-1) {
				grid[cell.x-1][cell.y] = BURNING;
				burningBoundary.add(new Coordinate(cell.x-1, cell.y));
			}
			if(grid[cell.x][Math.min(cell.y+1,yBoundary-1)] == FREE) {
				grid[cell.x][cell.y+1] = BURNING;
				burningBoundary.add(new Coordinate(cell.x, cell.y+1));
			}
			if(grid[cell.x][Math.max(cell.y-1,0)] == FREE) {
				grid[cell.x][cell.y-1] = BURNING;
				burningBoundary.add(new Coordinate(cell.x, cell.y-1));
			}
		}
		
		boolean highwayReached = false;
		for(int i=0;i<yBoundary;i++) {
			if(grid[xBoundary-1][i] == BURNING) {
				highwayReached = true;
				break;
			}
		}
			
		return highwayReached;
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
	
//	public void mutate() {
//		if(rand.nextDouble() < mutationVersionWiggle) {
//			int xChange, yChange;
//			for(int i=0;i<sequence.length;i++) {
//				if(rand.nextDouble() <= mutationRate/sequence.length) {
//					changed = true;
//					xChange = rand.nextInt(3)-1;
//					yChange = rand.nextInt(3)-1;
//					if(sequence[i].x+xChange>=0 && sequence[i].x+xChange<xBoundary)
//						sequence[i].x += xChange;
//					if(sequence[i].y+yChange>=0 && sequence[i].y+yChange<yBoundary)
//						sequence[i].y += yChange;
//				}
//			}
//		}
//		else {
//			for(int i=0;i<sequence.length;i++) {
//				if(rand.nextDouble() <= mutationRate/sequence.length) {
//					changed = true;
//					sequence[i] = new Coordinate(rand.nextInt(xBoundary),rand.nextInt(yBoundary));
//				}
//			}
//		}
//	}
	
	private void clearGrid() {
		for(int i=0;i<grid.length;i++) {
			for(int j=0;j<grid[i].length;j++) {
				grid[i][j] = FREE;
			}
		}
	}
}
