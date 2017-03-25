import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class ConnectedStrategy extends Strategy {

	enum Direction {N, NE, E, SE, S, SW, W, NW};
	
	private class Extension {
		
		Direction dir;
		boolean extendFront;
		
		public Extension() {
			dir = Direction.values()[Strategy.rand.nextInt(Direction.values().length)];
			extendFront = Strategy.rand.nextBoolean();
		}
		
		public Extension clone() {
			Extension e = new Extension();
			e.dir = dir;
			e.extendFront = extendFront;
			return e;
		}
	}
	
	private Coordinate start;
	
	private Extension[] sequence;
	
	public ConnectedStrategy() {
		super();
		
//		start = new Coordinate(rand.nextInt(xBoundary-1),rand.nextInt(yBoundary));
//		start = new Coordinate(xBoundary/3+1,yBoundary/2);
		start = new Coordinate(xBoundary-2,yBoundary/2);
		sequence = new Extension[(int)((yBoundary)*Math.max(budged,1)+1)];
		
		for(int i=0;i<sequence.length;i++)
			sequence[i] = new Extension();
	}
	
	public ConnectedStrategy(ConnectedStrategy toClone) {
		super();
		
//		start = new Coordinate(rand.nextInt(xBoundary-1),rand.nextInt(yBoundary));
//		start = new Coordinate(xBoundary/3+1,yBoundary/2);
		start = new Coordinate(xBoundary-2,yBoundary/2);
		sequence = new Extension[(int)((yBoundary)*Math.max(budged,1)+1)];
		
		for(int i=0;i<sequence.length;i++)
			sequence[i] = toClone.sequence[i].clone();
		
		if(!toClone.changed)
			changed = false;
	}
	
	public ConnectedStrategy(ConnectedStrategy parent1, ConnectedStrategy parent2) {
		super();
		
//		start = new Coordinate(rand.nextInt(xBoundary-1),rand.nextInt(yBoundary));
//		start = new Coordinate(xBoundary/3+1,yBoundary/2);
		start = new Coordinate(xBoundary-2,yBoundary/2);
		sequence = new Extension[(int)((yBoundary)*Math.max(budged,1)+1)];
		
		int crossPoint = Strategy.rand.nextInt(parent1.sequence.length);
		
		for(int i=0;i<sequence.length;i++) {
			if(i<=crossPoint)
				sequence[i] = parent1.sequence[i].clone();
			else
				sequence[i] = parent2.sequence[i].clone();
		}
	}
	
	@Override
	boolean simulate(boolean force, boolean printGrid) {
		if(!changed && !force) return false;
		
		clearGrid();
		
		double account = initialAccount;
		int iterations = yBoundary/2; //TODO think about iterations
		int protectIterator = 0;
		
		int startFireX = xBoundary/3;
		int startFireY = yBoundary/2;
		
		Coordinate front = start.clone();
		Coordinate back = start.clone();
		
		protect(start);
		account -= 1;
		
		grid[startFireX][startFireY] = BURNING;
		Queue<Coordinate> burningBoundary = new LinkedList<Coordinate>();
		burningBoundary.add(new Coordinate(startFireX,startFireY));
		timeToReachHighway = 0;
		
		Extension ext;
		Coordinate nextCell = null;
		Direction scan;
		
		for(int i=0;i<iterations && !burningBoundary.isEmpty();i++) {
			while(account >= 1 && protectIterator<sequence.length) {
				
				ext = sequence[protectIterator];
				
				if(ext.extendFront) {
					for(int look=0;look<8;look++) {
						scan = Direction.values()[(ext.dir.ordinal()+look)%Direction.values().length];
						nextCell = findCell(front, scan);
						if(grid[nextCell.x][nextCell.x] != BURNING)
							break;
					}
					if(protect(nextCell)) {
						front = nextCell;
						account -= 1.0;
					}
				}
				else {
					for(int look=0;look<8;look++) {
						scan = Direction.values()[(ext.dir.ordinal()+Direction.values().length-look)%Direction.values().length];
						nextCell = findCell(back, scan);
						if(grid[nextCell.x][nextCell.x] != BURNING)
							break;
					}
					if(protect(nextCell)) {
						back = nextCell;
						account -= 1.0;
					}
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
		
		x = Math.max(0, x);
		x = Math.min(xBoundary-2, x);
		y = Math.max(0, y);
		y = Math.min(yBoundary-1, y);
		
		return new Coordinate(x,y);
	}

	@Override
	void mutate() {
		if(rand.nextDouble() <= mutationRate) {
			start.x = Math.max(0, Math.min(xBoundary-2, start.x+wiggleOffset()));
			start.y = Math.max(0, Math.min(yBoundary-1, start.y+wiggleOffset()));
		}
		
		for(int i=0;i<sequence.length;i++) {
			if(rand.nextDouble() <= mutationRate/sequence.length) {
				sequence[i] = new Extension();
				changed = true;
			}
		}
	}
	
	public int wiggleOffset() {
		double gaus = rand.nextGaussian();
		if(gaus > 0)
			return ((int)(gaus*wiggleSize)+1);
		else
			return ((int)(gaus*wiggleSize)-1);
	}
	
}
