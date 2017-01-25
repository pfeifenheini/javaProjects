import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Strategy implements Comparable<Strategy> {
	public static final int FREE = 0;
	public static final int BURNING = 1;
	public static final int SAVE = 2;
	public static final int SAVEOFFSET = 3;
	
	
	public static long seed = new Random().nextLong();
	public static Random rand = null;
	
	public static int xBoundary = 20;
	public static int yBoundary = 201; // = 10*xBoundary+1
	
	public static double initialAccount = 2.0;
	public static double budged = 2.0;
	public static double mutationRate = 0.01;
	
	private Coordinate[] sequence;
	
	private int timeToReachHighway = 0;
	private int nonBurningHighwayCells = 0;
	private int[] nonBurningPerLevel;
	
	boolean changed = true;
	
	public Strategy() {
		if(rand == null)
			rand = new Random(seed);
		
		nonBurningPerLevel = new int[xBoundary];
		
		sequence = new Coordinate[(int)((xBoundary+yBoundary)*Math.max(budged,1)*10)];
		for(int i=0;i<sequence.length;i++)
			sequence[i] = new Coordinate(rand.nextInt(xBoundary),rand.nextInt(yBoundary));
	}
	
	public Strategy(Strategy toClone) {
		if(rand == null)
			rand = new Random(seed);
		
		nonBurningPerLevel = new int[xBoundary];
		
		sequence = new Coordinate[(int)((xBoundary+yBoundary)*Math.max(budged,1)*10)];
		for(int i=0;i<sequence.length;i++)
			sequence[i] = new Coordinate(toClone.sequence[i].x,toClone.sequence[i].y);
	}
	
	//TODO correct fitness
	public int fitness() {
		if(changed) {
			System.out.println("need to create new grid");
			int grid[][] = new int[xBoundary][yBoundary];
			simulate(grid, false);
		}
		return yBoundary*timeToReachHighway+nonBurningHighwayCells;
	}
	
	public void simulate(int[][] grid, boolean force) {
		if(!changed && !force) return;
		
		clearGrid(grid);
		
		double account = initialAccount;
		int iterations = yBoundary/2; //TODO think about iterations
		int protectIterator = 0;
		
		grid[0][yBoundary/2] = BURNING;
		Queue<Coordinate> burningBoundary = new LinkedList<Coordinate>();
		burningBoundary.add(new Coordinate(0,yBoundary/2));
		timeToReachHighway = 0;
		for(int i=0;i<iterations;i++) {
			while(account >= 1 && protectIterator<sequence.length) {
				if(protect(grid,sequence[protectIterator])) {
					account -= 1.0;
				}
				protectIterator++;
			}
			
//			printGrid(grid);
			
			if(!spreadFire(grid,burningBoundary)) {
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
	}
	
	// returns true if a cell has been protected
	private boolean protect(int[][] grid, Coordinate cell) {
		if(grid[cell.x][cell.y] != BURNING) {
			grid[cell.x][cell.y] = SAVE;
			return true;
		}
		else {
			int offset = 1;
			while(cell.x+offset < xBoundary && grid[cell.x+offset][cell.y] != FREE) {
				offset++;
			}
			if(cell.x+offset < xBoundary) {
				grid[cell.x+offset][cell.y] = SAVEOFFSET;
				return true;
			}
		}
		return false;
	}
	
	// return true if the highway is reached
	private boolean spreadFire(int[][] grid, Queue<Coordinate> burningBoundary) {
		int steps = burningBoundary.size();
		Coordinate cell;
		for(int i=0;i<steps;i++) {
			cell = burningBoundary.remove();
			if(grid[Math.min(cell.x+1,xBoundary-1)][cell.y] == FREE) {
				grid[cell.x+1][cell.y] = BURNING;
				burningBoundary.add(new Coordinate(cell.x+1, cell.y));
			}
			if(grid[Math.max(cell.x-1,0)][cell.y] == FREE) {
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
	
	public void mutate(int version) {
		if(version == 1) {
			int xChange, yChange;
			for(int i=0;i<sequence.length;i++) {
				if(rand.nextDouble() <= mutationRate) {
					xChange = rand.nextInt(3)-1;
					yChange = rand.nextInt(3)-1;
					if(sequence[i].x+xChange>=0 && sequence[i].x+xChange<xBoundary)
						sequence[i].x += xChange;
					if(sequence[i].y+yChange>=0 && sequence[i].y+yChange<yBoundary)
						sequence[i].y += yChange;
				}
			}
		}
		else if(version == 2) {
			for(int i=0;i<sequence.length;i++) {
				if(rand.nextDouble() <= mutationRate) {
					sequence[i] = new Coordinate(rand.nextInt(xBoundary),rand.nextInt(yBoundary));
				}
			}
		}
		changed = true;
	}
	
	private void clearGrid(int[][] grid) {
		for(int i=0;i<grid.length;i++) {
			for(int j=0;j<grid[i].length;j++) {
				grid[i][j] = FREE;
			}
		}
	}
	
	public void printGrid(int[][] grid) {
		for(int i=0;i<grid.length;i++) {
			for(int j=0;j<grid[i].length;j++) {
				if(grid[i][j] == FREE)
					System.out.print(" ");
				if(grid[i][j] == BURNING)
					System.out.print("X");
				if(grid[i][j] == SAVE)
					System.out.print("-");
				if(grid[i][j] == SAVEOFFSET)
					System.out.print("=");
			}
			System.out.println();
		}
		System.out.println("Time: " + timeToReachHighway);
		System.out.println("NonBurning: " + nonBurningHighwayCells);
		System.out.println("Fitness:" + fitness());
	}

	@Override
	public int compareTo(Strategy arg0) {
		for(int i=xBoundary-1;i>=0;i--) {
			if(arg0.nonBurningPerLevel[i] != nonBurningPerLevel[i])
				return arg0.nonBurningPerLevel[i] - nonBurningPerLevel[i];
		}
		return 0;
	}
}
