import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public abstract class Strategy implements Comparable<Strategy> {
	public static final int FREE = -1;
	public static final int BURNING = -2;
	public static final int SAVE = -3;
//	public static final int SAVEOFFSET = 3;
	
	
	public static long seed = new Random().nextLong();
//	public static long seed = 42;
	public static Random rand = null;
	
	public static int xBoundary = 33;
	public static int yBoundary = 103; // = 10*xBoundary+1
	
	public final static double initialAccount = 1.2;
	public final static double budged = 1.2;
	public final static double mutationRate = 0.5;
	public final static double mutationVersionWiggle = 0.6;
	public final static double wiggleSize = 1.0;
	public final static double mutationVersionSwap = 0.3;
	
	protected int[][] grid;
	
//	protected Coordinate[] sequence;
	
	protected int timeToReachHighway = 0;
	protected int nonBurningHighwayCells = 0;
	protected int[] nonBurningPerLevel;
	
	boolean changed = true;
	
	public Strategy() {
		if(rand == null)
			rand = new Random(seed);
		
		nonBurningPerLevel = new int[xBoundary];
		grid = new int[Strategy.xBoundary][Strategy.yBoundary];
		
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
	abstract boolean simulate(boolean force, boolean printGrid);
	
	abstract void mutate();
	
	/**
	 * Protects a specified cell if possible 
	 * @param cell
	 * @return true if a cell has been protected
	 */
	protected boolean protect(Coordinate cell) {
		if(grid[cell.x][cell.y] != BURNING) {
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
	boolean spreadFire(Queue<Coordinate> burningBoundary) {
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
	
	protected void clearGrid() {
		for(int i=0;i<grid.length;i++) {
			for(int j=0;j<grid[i].length;j++) {
				grid[i][j] = FREE;
			}
		}
	}
	
	public void printGrid() {
		for(int i=0;i<grid.length;i++) {
			for(int j=0;j<grid[i].length;j++) {
				if(grid[i][j] == FREE)
					System.out.print(" ");
				if(grid[i][j] == BURNING)
					System.out.print("X");
				if(grid[i][j] == SAVE)
					System.out.print("-");
//				if(grid[i][j] == SAVEOFFSET)
//					System.out.print("=");
			}
			System.out.println();
		}
		System.out.println("Time: " + timeToReachHighway);
		System.out.println("NonBurning: " + nonBurningHighwayCells);
		System.out.println("Fitness:" + fitness());
	}

	public void save(String prefix) {
		try {
			PrintWriter writer = new PrintWriter(prefix+".data","UTF-8");
			
			for(int i=0;i<xBoundary;i++) {
				for(int j=0;j<yBoundary;j++)
						writer.print(grid[i][j] + " ");
				writer.println();
			}
			
			writer.close();
			
			System.out.println(">>>>>>>>>>>>> SAVE SUCESSFUL <<<<<<<<<<<<<<<");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		try {
//			PrintWriter writerBurning = new PrintWriter(prefix+"Burning.data","UTF-8");
//			PrintWriter writerSave = new PrintWriter(prefix+"Save.data","UTF-8");
//			
//			for(int i=0;i<xBoundary;i++) {
//				for(int j=0;j<yBoundary;j++) {
//					if(grid[i][j] == BURNING)
//						writerBurning.println(i + " " + j);
//					if(grid[i][j] == SAVE)
//						writerSave.println(i + " " + j);
//				}
//			}
//			
//			writerBurning.close();
//			writerSave.close();
//			
//			System.out.println(">>>>>>>>>>>>> SAVE SUCESSFUL <<<<<<<<<<<<<<<");
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	public int compareTo(Strategy arg0) {
		
		if(timeToReachHighway != arg0.timeToReachHighway)
			return arg0.timeToReachHighway - timeToReachHighway;
		
		for(int i=xBoundary-1;i>=0;i--) {
			if(arg0.nonBurningPerLevel[i] != nonBurningPerLevel[i])
				return arg0.nonBurningPerLevel[i] - nonBurningPerLevel[i];
		}
		return 0;
	}
}
