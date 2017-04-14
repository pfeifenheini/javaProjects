import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Queue;
import java.util.Random;

public abstract class Strategy implements Comparable<Strategy> {
	
	/**
	 * Class to represent two dimensional coordinates
	 */
	protected class Coordinate {
		public int x;
		public int y;
		
		/**
		 * Constructor
		 */
		public Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public Coordinate clone() {
			return new Coordinate(x,y);
		}
	}
	
	/** marks a free cell */
	public static final int FREE = -1;
	/** marks a protected cell */
	public static final int SAVE = -3;
	
	/** possible operation modes */
	public enum Mode { CatchFire, ProtectHighway};
	
	/** seed used for random number generator */
	public static long seed = new Random().nextLong();
	/** random number generator used for all random processes */
	public static Random rand = new Random(seed);
	
	// Parameters
	/** length of the simulated plane */
	public static int xBoundary = 161;
	/** height of the simulated plane */
	public static int yBoundary = 161;
	
	/** x coordinate where the fire starts */
	public static int startFireX = xBoundary/2;
	/** y coordinate where the fire starts */
	public static int startFireY = yBoundary/2;
	
	/** decides how the scattered strategy is initialized */
	public static boolean initializeRandom = true;
	/** initial account, determines how many cells can be protected in first step */
	public static double initialAccount = 2.0;
	/** defines how many additional cells can be protected in each step */
	public static double budget = 2.0;
	/** a higher value increases the probability the genome mutates */
	public static double mutationRate = 1.0;
	/** ratio of how many mutations are of type "wiggle" (only relevant to scattered strategy) */
	public static double mutationVersionWiggle = 0.6;
	/** higher value increases the probability for a larger change */
	public static double wiggleSize = 1.0;
	/** ratio of how many mutations are of type "swap" (only relevant to scattered strategy) */
	public static double mutationVersionSwap = 0.3;
	/** operation mode, either catch fire or protect highway */
	public static Mode mode = Mode.CatchFire;
	/** number of different connected barriers that shall be built */
	public static int numberOfConnectedBarriers = 1;
	
	/** the grid */
	protected int[][] grid;
	
	/** contains the time necessary to enclose the fire (if achieved) */
	protected int timeToEncloseFire = 0;
	/** contains the time necessary for the fire to reach the highway */
	protected int timeToReachHighway = 0;
	/** number of non burning cells on the highway */
	protected int nonBurningHighwayCells = 0;
	/** number of non burning cells per distance to the highway */
	protected int[] nonBurningPerLevel;
	/** total number of non burning cells on the grid */
	protected int totalSavedCells = 0;
	/** true iff the fire is highway is protected */
	protected boolean highwayProtected = false;
	/** true iff the fire is enclosed */
	protected boolean fireEnclosed = false;
	/** fitness of the strategy */
	protected double fitness = 0;
	/** indicates whether the strategy has changed and needs to be simulated again */
	boolean changed = true;
	
	/**
	 * Constructor
	 */
	public Strategy() {		
		nonBurningPerLevel = new int[xBoundary];
		grid = new int[Strategy.xBoundary][Strategy.yBoundary];
		for(int[] row:grid)
			Arrays.fill(row, FREE);
		
		changed = true;
	}
	
	/**
	 * Sets all parameters from config file
	 * @param config
	 */
	public static void setParameters(Properties config) {
		String s;
		
		s = config.getProperty("seed");
		if(!s.equalsIgnoreCase("random")) {
			seed = Long.parseLong(s);
		}
		else {
			seed = new Random().nextLong();
		}
		rand = new Random(seed);

		xBoundary = Integer.parseInt(config.getProperty("xBoundary"));
		yBoundary = Integer.parseInt(config.getProperty("yBoundary"));
		
		startFireX = Integer.parseInt(config.getProperty("startFireX"));
		if(startFireX==0)
			startFireX = xBoundary/2;
		startFireY = Integer.parseInt(config.getProperty("startFireY"));
		if(startFireY==0)
			startFireY = yBoundary/2;
		
		initializeRandom = Boolean.parseBoolean(config.getProperty("initializeRandom"));
		initialAccount = Double.parseDouble(config.getProperty("initialAccount"));
		budget = Double.parseDouble(config.getProperty("budget"));
		mutationRate = Double.parseDouble(config.getProperty("mutationRate"));
		mutationVersionWiggle = Double.parseDouble(config.getProperty("mutationVersionWiggle"));
		wiggleSize = Double.parseDouble(config.getProperty("wiggleSize"));
		mutationVersionSwap = Double.parseDouble(config.getProperty("mutationVersionSwap"));
		s = config.getProperty("mode");
		if(s.equalsIgnoreCase("catchFire"))
			mode = Mode.CatchFire;
		else if(s.equalsIgnoreCase("protectHighway"))
			mode = Mode.ProtectHighway;
		
		numberOfConnectedBarriers = Integer.parseInt(config.getProperty("numberOfConnectedBarriers"));
	}
	
	/**
	 * Simulates the strategy to determine its fitness.
	 * @param force enforces a new simulation, regardless whether the strategy has changed
	 * @param printGrid prints the grid after each fire spread to the console
	 * @param saveAllSteps saves the grid after each fire spread in a file
	 * @return true if the simulation has been calculated new
	 */
	abstract boolean simulate(boolean force, boolean printGrid, boolean saveAllSteps);
	
	/**
	 * Mutates the strategy
	 */
	abstract void mutate();
	
	public int wiggleOffset() {
		double gaus = rand.nextGaussian();
		if(gaus > 0)
			return ((int)(gaus*wiggleSize)+1);
		else
			return ((int)(gaus*wiggleSize)-1);
	}
	
	/**
	 * Protects a specified cell if possible 
	 * @param cell
	 * @return true if a cell has been protected
	 */
	protected boolean protect(Coordinate cell) {
		if(grid[cell.x][cell.y] == FREE) {
			grid[cell.x][cell.y] = SAVE;
			return true;
		}
		return false;
	}
	
	/**
	 * Spreads the fire
	 * @param burningBoundary Burning cells on the boundary
	 * @return true if the highway is reached
	 */
	boolean spreadFire(Queue<Coordinate> burningBoundary, int time) {
		int steps = burningBoundary.size();
		Coordinate cell;
		for(int i=0;i<steps;i++) {
			cell = burningBoundary.remove();
			if(grid[Math.min(cell.x+1,xBoundary-1)][cell.y] == FREE) {
				grid[cell.x+1][cell.y] = time;
				burningBoundary.add(new Coordinate(cell.x+1, cell.y));
			}
			if(grid[Math.max(cell.x-1,0)][cell.y] == FREE && cell.x != xBoundary-1) {
				grid[cell.x-1][cell.y] = time;
				burningBoundary.add(new Coordinate(cell.x-1, cell.y));
			}
			if(grid[cell.x][Math.min(cell.y+1,yBoundary-1)] == FREE) {
				grid[cell.x][cell.y+1] = time;
				burningBoundary.add(new Coordinate(cell.x, cell.y+1));
			}
			if(grid[cell.x][Math.max(cell.y-1,0)] == FREE) {
				grid[cell.x][cell.y-1] = time;
				burningBoundary.add(new Coordinate(cell.x, cell.y-1));
			}
		}
		
		boolean highwayReached = false;
		for(int i=0;i<yBoundary;i++) {
			if(grid[xBoundary-1][i] >= 0) {
				highwayReached = true;
				break;
			}
		}
			
		return highwayReached;
	}
	
	/**
	 * Resets the grid
	 */
	protected void clearGrid() {
		for(int i=0;i<grid.length;i++) {
			for(int j=0;j<grid[i].length;j++) {
				grid[i][j] = FREE;
			}
		}
	}
	
	/**
	 * Prints the grid to the console. Skips parts where all cells are free.
	 */
	public void printGrid() {
		
		int minX=xBoundary-1, minY=yBoundary-1, maxX=0, maxY=0;
	    for(int x=0;x<xBoundary;x++)
	    {
	        for(int y=0;y<yBoundary;y++)
	        {
	            if(grid[x][y] != FREE)
	            {
	                if(x<minX) minX = x;
	                if(y<minY) minY = y;
	                if(x>maxX) maxX = x;
	                if(y>maxY) maxY = y;
	            }
	        }
	    }
		
	    if(Math.max(maxX-minX, maxY-minY)>200) {
	    	System.out.println("grid to big to print!");
	    }
	    else {
			for(int x=minX;x<=maxX;x++) {
				for(int y=minY;y<=maxY;y++) {
					if(grid[x][y] == FREE)
						System.out.print(" ");
					if(grid[x][y] >= 0)
						System.out.print(grid[x][y]%10);
					if(grid[x][y] == SAVE)
						System.out.print("X");
				}
				System.out.println();
			}
	    }
		
	    System.out.println("budget: " + budget);
		if(mode == Mode.CatchFire) {
			System.out.println("Fire stopped: " + fireEnclosed);
			System.out.println("Time needed: " + timeToEncloseFire);
			System.out.println("Burning Vertices: " + (xBoundary*yBoundary-totalSavedCells));
		}
		else {
			System.out.println("Fire stopped: " + highwayProtected);
			System.out.println("Time needed: " + timeToReachHighway);
		}
		
	}
	
	/**
	 * @return copy of the current grid
	 */
	public int[][] cloneCurrentGrid() {
		return grid.clone();
	}
	
	/**
	 * Saves the currents grid in a file.
	 * @param prefix name of the file
	 */
	public void save(String prefix) {
		save(grid,prefix);
	}
	
	/**
	 * Saves the content of the grid within the specified boundaries
	 * @param prefix
	 * @param minX
	 * @param maxX
	 * @param minY
	 * @param maxY
	 */
	public void save(String prefix, int minX, int maxX, int minY, int maxY) {
		save(grid,prefix,minX,maxX,minY,maxY);
	}
	
	/**
	 * Saves a given grid
	 * @param grid
	 * @param prefix
	 */
	public static void save(int[][] grid, String prefix) {
		save(grid,prefix,0,grid.length-1,0,grid[0].length-1);
	}
	
	/**
	 * Saves the content of a given grid within the specified boundaries
	 * @param grid the grid
	 * @param prefix name of the file
	 */
	public static void save(int[][] grid, String prefix, int minX, int maxX, int minY, int maxY) {
		
		try {
			PrintWriter writer = new PrintWriter(prefix+".grid","UTF-8");
			
			for(int i=minX;i<=maxX;i++) {
				for(int j=minY;j<=maxY;j++) {
					if(grid[i][j] >= 0)
						writer.print(grid[i][j]%10 + " ");
					if(grid[i][j] == FREE)
						writer.print(100 + " ");
					if(grid[i][j] == SAVE)
						writer.print(-100 + " ");
				}
				writer.println();
			}
			
			writer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Calculates the fitness of the strategy. In the case of catching the fire, this is the total
	 * number of non burning cells on the grid. In the case of highway protection it calculates a
	 * value that corresponds to a lexicographic order for the non burning cells per distance to the barrier.
	 * @return fitness of the strategy
	 */
	public double fitness() {
		if(changed)
			simulate(false,false,false);
		
//		if(mode == Mode.ProtectHighway) {
//			return timeToReachHighway;
//			double value = 0;
//			int nonBurningInLine;
//			for(int i=0;i<xBoundary;i++) {
//				
//				nonBurningInLine = 0;
//				for(int j=0;j<yBoundary;j++) {
//					if(grid[i][j] < 0)
//						nonBurningInLine++;
//				}
//				
//				value += nonBurningInLine*Math.pow(yBoundary, (double)i);
//			}
//			value += timeToReachHighway*Math.pow(yBoundary, (double)xBoundary);
//			
//			return Math.log(value); 
//		}
		
		if(mode == Mode.CatchFire)
			return xBoundary*yBoundary-totalSavedCells;
		
		return xBoundary*yBoundary-totalSavedCells;
	}
	
	/**
	 * Compares to strategies
	 */
	@Override
	public int compareTo(Strategy arg0) {
		if(mode == Mode.ProtectHighway) { // lexicographic
			if(timeToReachHighway != arg0.timeToReachHighway)
				return arg0.timeToReachHighway - timeToReachHighway;
			
			for(int i=xBoundary-1;i>=0;i--) {
				if(arg0.nonBurningPerLevel[i] != nonBurningPerLevel[i])
					return arg0.nonBurningPerLevel[i] - nonBurningPerLevel[i];
			}
		}
		
		if(mode == Mode.CatchFire) { // strategy with fewer burning cells
			return (int)Math.signum(fitness - arg0.fitness);
		}
		
		return 0;
	}
}
