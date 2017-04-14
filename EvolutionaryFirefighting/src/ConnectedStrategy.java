import java.util.LinkedList;
import java.util.Queue;

public class ConnectedStrategy extends Strategy {

	/** possible directions to protect the next cell */
	enum Direction {N, NE, E, SE, S, SW, W, NW};
	
	/**
	 *	An Extension contains the information where to extend the barrier.
	 */
	private class Extension {
		
		/** direction of the next cell */
		Direction dir;
		/** decides whether to extend the front or the back of the barrier */
		boolean extendFront;
		
		/**
		 * Constructor
		 */
		public Extension() {
			dir = Direction.values()[Strategy.rand.nextInt(Direction.values().length)];
			if(dir.ordinal()%2==1)
				dir = Direction.values()[Strategy.rand.nextInt(Direction.values().length)];
			extendFront = Strategy.rand.nextBoolean();
		}
		
		public Extension(Direction dir, boolean extendFront) {
			this.dir = dir;
			this.extendFront = extendFront;
		}
		
		@Override
		public Extension clone() {
			Extension e = new Extension();
			e.dir = dir;
			e.extendFront = extendFront;
			return e;
		}
	}
	
	/** start coordinate */
	private Coordinate[] start;
	/** tells which cell to protect next */
	private Extension[][] sequence;
	/** tells which barrier to continue */
	private int[] pickedBarrier; 
	
	/**
	 * Constructor
	 */
	public ConnectedStrategy() {
		super();
		
		start = new Coordinate[numberOfConnectedBarriers];
		
		for(int i=0;i<numberOfConnectedBarriers;i++) {
			int x = (int)(10*rand.nextGaussian()+startFireX);
			int y = (int)(10*rand.nextGaussian()+startFireY);
			if(mode == Mode.CatchFire)
				x = Math.max(Math.min(x, xBoundary-1), 0);
			else
				x = Math.max(Math.min(x, xBoundary-2), 0);
			y = Math.max(Math.min(y, yBoundary-1), 0);
			
			start[i] = new Coordinate(x,y);
		}
		
		sequence = new Extension[numberOfConnectedBarriers][];
		for(int i=0;i<numberOfConnectedBarriers;i++)
			sequence[i] = new Extension[(int)((xBoundary+yBoundary)*Math.max(budget,1)+1)];
		for(int i=0;i<numberOfConnectedBarriers;i++)
			for(int j=0;j<sequence[i].length;j++)
				sequence[i][j] = new Extension();
		
		pickedBarrier = new int[numberOfConnectedBarriers*sequence[0].length];
		for(int i=0;i<pickedBarrier.length;i++) {
			pickedBarrier[i] = rand.nextInt(numberOfConnectedBarriers);
		}
		
		if(!initializeRandom) {
			start[0] = new Coordinate(xBoundary-2,startFireY+0);
			pickedBarrier[0] = 0;
		}
	}
	
	/**
	 * Creates a new Strategy given two parent strategies
	 * @param parent1
	 * @param parent2
	 */
	public ConnectedStrategy(ConnectedStrategy parent1, ConnectedStrategy parent2) {
		super();
		
		start = new Coordinate[numberOfConnectedBarriers];
		int crossPoint = rand.nextInt(parent1.start.length);
		for(int i=0;i<numberOfConnectedBarriers;i++) {
			if(i<=crossPoint)
				start[i] = parent1.start[i].clone();
			else
				start[i] = parent2.start[i].clone();
		}
		
		sequence = new Extension[numberOfConnectedBarriers][];
		for(int i=0;i<numberOfConnectedBarriers;i++)
			sequence[i] = new Extension[(int)((xBoundary+yBoundary)*Math.max(budget,1)+1)];
		for(int i=0;i<numberOfConnectedBarriers;i++) {
			crossPoint = rand.nextInt(parent1.sequence[i].length);
			for(int j=0;j<sequence[i].length;j++) {
				if(j<=crossPoint)
					sequence[i][j] = parent1.sequence[i][j].clone();
				else
					sequence[i][j] = parent2.sequence[i][j].clone();
			}
		}
		
		pickedBarrier = new int[parent1.pickedBarrier.length];
		crossPoint = rand.nextInt(pickedBarrier.length);
		for(int i=0;i<pickedBarrier.length;i++) {
			if(i<=crossPoint)
				pickedBarrier[i] = parent1.pickedBarrier[i];
			else
				pickedBarrier[i] = parent2.pickedBarrier[i];
		}
	}
	
	@Override
	boolean simulate(boolean force, boolean printGrid, boolean saveAllSteps) {
		if(!changed && !force) return false;
		
		int minX=0, minY=0, maxX=xBoundary-1, maxY=yBoundary-1;
		if(saveAllSteps) {
			minX=xBoundary-1; minY=yBoundary-1; maxX=0; maxY=0;
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
		}
		
		clearGrid();
		
		double account = initialAccount;
		int iterations = Math.max(xBoundary/2,yBoundary/2);
		int[] sequenceIterator = new int[numberOfConnectedBarriers];
		int barrierIterator = 0;
		
		Coordinate[] front = new Coordinate[start.length];
		Coordinate[] back = new Coordinate[start.length];
		for(int i=0;i<start.length;i++) {
			front[i] = start[i].clone();
			back[i] = start[i].clone();
		}
		
		boolean[] startProtected = new boolean[numberOfConnectedBarriers];
		for(int i=0;i<startProtected.length;i++)
			startProtected[i] = false;
		
		grid[startFireX][startFireY] = 0;
		Queue<Coordinate> burningBoundary = new LinkedList<Coordinate>();
		burningBoundary.add(new Coordinate(startFireX,startFireY));
		timeToReachHighway = 0;
		timeToEncloseFire = 1;
		
		Extension ext;
		Coordinate nextCell = null;
		Direction scan = null;
		
		if(saveAllSteps)
			save("steps/step_0",minX,maxX,minY,maxY);
		
		fitness = 1;
		for(int time=0;time<iterations && !burningBoundary.isEmpty();time++) {
			while(account >= 1 
					&& barrierIterator<pickedBarrier.length 
					&& sequenceIterator[pickedBarrier[barrierIterator]]<sequence[pickedBarrier[barrierIterator]].length) {
				
				int currBarrier = pickedBarrier[barrierIterator];
				
				if(!startProtected[currBarrier]) {
					if(protect(start[currBarrier]))
						account -= 1.0;
					startProtected[currBarrier] = true;
				}
				else {
					
					ext = sequence[currBarrier][sequenceIterator[currBarrier]];
					
					if(ext.extendFront) {
						for(int look=0;look<8;look++) {
							scan = Direction.values()[(ext.dir.ordinal()+look)%Direction.values().length];
							nextCell = findCell(front[currBarrier], scan);
							if(grid[nextCell.x][nextCell.y] < 0)
								break;
						}
						if(protect(nextCell)) {
							account -= 1.0;
						}
						if(grid[nextCell.x][nextCell.y] == SAVE)
							front[currBarrier] = nextCell;
					}
					else {
						for(int look=7;look>=0;look--) {
							scan = Direction.values()[(ext.dir.ordinal()+look)%Direction.values().length];
							nextCell = findCell(back[currBarrier], scan);
							if(grid[nextCell.x][nextCell.y] < 0)
								break;
						}
						if(protect(nextCell)) {
							account -= 1.0;
						}
						if(grid[nextCell.x][nextCell.y] == SAVE)
							back[currBarrier] = nextCell;
					}
					
//					sequence[currBarrier][sequenceIterator[currBarrier]] = new Extension(scan, ext.extendFront);
					
					sequenceIterator[currBarrier]++;
				}
				barrierIterator++;
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
			
			if(!spreadFire(burningBoundary,time+1)) {
				timeToReachHighway++;
				highwayProtected = true;
				if(burningBoundary.isEmpty()) {
					timeToReachHighway = iterations+1;
				}
			}
			else
				highwayProtected = false;
			
			if(burningBoundary.isEmpty())
				fireEnclosed = true;
			else
				timeToEncloseFire++;
			
			account += budget;
			
			if(saveAllSteps)
				save("steps/step_" + (time+1),minX,maxX,minY,maxY);
		}
		
		fitness += burningBoundary.size();
		
		nonBurningHighwayCells = 0;
		for(int i=0;i<yBoundary;i++) {
			if(grid[xBoundary-1][i] < 0)
				nonBurningHighwayCells++;
		}
		totalSavedCells = 0;
		for(int x=0;x<xBoundary;x++) {
			nonBurningPerLevel[x] = 0;
			for(int y=0;y<yBoundary;y++) {
				if(grid[x][y] < 0) {
					nonBurningPerLevel[x]++;
					totalSavedCells++;
				}
			}
		}
		
		changed = false;
		return true;
	}

	/**
	 * Determines the next cell given a start cell and a direction
	 * @param cell start cell
	 * @param course direction of the next cell
	 * @return next cell
	 */
	private Coordinate findCell(Coordinate cell, Direction course) {
		int x = cell.x;
		int y = cell.y;
		
		if(course == Direction.N || course == Direction.NE || course == Direction.NW)
			x++;
		if(course == Direction.S || course == Direction.SE || course == Direction.SW) 
			x--;
		if(course == Direction.W || course == Direction.NW || course == Direction.SW)
			y--;
		if(course == Direction.E || course == Direction.NE || course == Direction.SE)
			y++;
		
		// correction if the next cell lies otside of the grid
		x = Math.max(0, x);
		x = Math.min(xBoundary-2, x);
		y = Math.max(0, y);
		y = Math.min(yBoundary-1, y);
		
		return new Coordinate(x,y);
	}

	@Override
	void mutate() {
		
		double chance = mutationRate/(start.length+numberOfConnectedBarriers*sequence[0].length+pickedBarrier.length); 
				
		for(int i=0;i<numberOfConnectedBarriers;i++) {
			if(rand.nextDouble() <= chance) {
				start[i].x = Math.max(0, Math.min(xBoundary-2, start[i].x+wiggleOffset()));
				start[i].y = Math.max(0, Math.min(yBoundary-1, start[i].y+wiggleOffset()));
			}
		}
		
		for(int i=0;i<numberOfConnectedBarriers;i++) {
			for(int j=0;j<sequence[i].length;j++) {
				if(rand.nextDouble() <= chance) {
					sequence[i][j] = new Extension();
					changed = true;
				}
			}
		}
		
		for(int i=0;i<pickedBarrier.length;i++) {
			if(rand.nextDouble() <= chance) {
				pickedBarrier[i] = rand.nextInt(numberOfConnectedBarriers);
			}
		}
		
		if(!initializeRandom) {
			start[0] = new Coordinate(xBoundary-2,startFireY+0);
			pickedBarrier[0] = 0;
		}
	}
}
