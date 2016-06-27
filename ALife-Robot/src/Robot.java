import java.util.Stack;

public class Robot {
	
	public static final int N = 0;
	public static final int NE = 1;
	public static final int E = 2;
	public static final int SE = 3;
	public static final int S = 4;
	public static final int SW = 5;
	public static final int W = 6;
	public static final int NW = 7;
	
	/** Side length of the grid containing this robot */
	private int _gridSize;
	/** Grid containing this robot */
	private int[][] _grid;
	/** Contains all former positions of the robot */
	private Stack<State> _history;
	/** chosen strategy */
	public int strategy = 0;
	
	/** x-coordinate on the grid */
	public int x;
	/** y-coordinate on the grid */
	public int y;
	/** Current direction of the robot */
	public int direction;
	
	/**
	 * Defines the state of the robot.
	 * 
	 * @author Martin
	 *
	 */
	private class State {
		public int x;
		public int y;
		public int direction;
		
		public State(int x, int y, int direction) {
			this.x = x;
			this.y = y;
			this.direction = direction;
		}
	}
	
	/**
	 * Constructor. Sets the robot at the default position
	 * (48,17) facing south.
	 * 
	 * @param grid The grid
	 * @param gridSize Side length of the grid
	 */
	public Robot(int[][] grid, int gridSize) {
		this(48,17,S,grid,gridSize);
	}
	
	/**
	 * Constructor. Initial position of the Robot can be defined.
	 * 
	 * @param startX Initial x-coodrdinate
	 * @param startY Initial y-coodrdinate
	 * @param direction Initial direction
	 * @param grid The grid
	 * @param gridSize Side length of the grid
	 */
	public Robot(int startX, int startY, int direction, int[][] grid, int gridSize) {
		x = startX;
		y = startY;
		this.direction = direction;
		_gridSize = gridSize;
		_grid = grid;
		_history = new Stack<State>();
	}
	
	/**
	 * Sets the position of the robot and resets the history.
	 * 
	 * @param x New x-coordinate
	 * @param y New y-coordinate
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		_history = new Stack<State>();
	}
	
	/**
	 * Executes on step of the robot.
	 */
	public void step() {
		State s = null;
		if(!_history.isEmpty())
			s = _history.lastElement();
		if(s == null || !(s.x == x && s.y == y && s.direction == direction))
			_history.push(new State(x,y,direction));
		
		for(int x=0;x<_gridSize;x++) {
			for(int y=0;y<_gridSize;y++) {
				if(_grid[x][y] >= 5) {
					_grid[x][y] -= 5;
				}
			}
		}
		_grid[x][y] = 180;
		
		if(strategy == 0)
			breitenbergStep();
		else
			leftHandStep();
	}
	
	/**
	 * Breitenberg 3b behavior
	 */
	public void breitenbergStep() {
		
		if(leftCells() > rightCells())
			direction = turn(1);
		else if(rightCells() > leftCells())
			direction = turn(-1);

//		if(_grid[facingX()][facingY()] != 1) {
		if(!pathBlocked(0)) {
			x = facingX();
			y = facingY();
		}
		else if(rightCells() == leftCells()) {
			if(Math.random()<0.5)
				direction = turn(-1);
			else
				direction = turn(1);
		}
	}
	
	/**
	 * Wall following with left hand rule
	 */
	public void leftHandStep() {
		if(noWallAround()) {
			x = facingX();
			y = facingY();
			return;
		}
		
		if(pathBlocked(0)) {
			if(!(pathBlocked(-1) && pathBlocked(-2) && pathBlocked(-3))) {
				direction = turn(-1);
			}
			else {
				direction = turn(1);
			}
			return;
		}
		
		if(pathBlocked(-1)) {
			x = facingX();
			y = facingY();
			return;
		}
		else {
			direction = turn(-1);
		}
	}
	
	/**
	 * 
	 * @param offset Offset
	 * @return true iff the direction is blocked
	 */
	public boolean pathBlocked(int offset) {
		if(_grid[facingX(turn(offset))][facingY(turn(offset))] == 1) return true;
		if(turn(offset)%2 == 1) {
			if(_grid[facingX(turn(offset-1))][facingY(turn(offset-1))] == 1 &&
					_grid[facingX(turn(offset+1))][facingY(turn(offset+1))] == 1) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return true iff all eight cells around the robot are free
	 */
	public boolean noWallAround() {
		int count = 0;
		for(int i=0;i<8;i++) {
			if(_grid[facingX(turn(i))][facingY(turn(i))] == 1)
				count++;
		}
		return count == 0;
	}
	
	/**
	 * Calculates the direction relative to the current direction.
	 * A negative value is interpreted as a left turn, a positive
	 * as a right turn.
	 * 
	 * @param dir Turning direction
	 * @return New direction
	 */
	public int turn(int dir) {
		if(dir>-8 && dir<8) {
			return (direction+8+dir)%8;
		}
		return direction;
	}
	
	/**
	 * Calculates how many walls are to the left
	 * @return Number of Walls
	 */
	public int leftCells() {
		int sum = 0;
		if(_grid[facingX(turn(-1))][facingY(turn(-1))] == 1) sum++;
		if(_grid[facingX(turn(-2))][facingY(turn(-2))] == 1) sum++;
		return sum;
	}
	 /**
	  * Calculates how many walls are to the right
	  * @return Number of Walls
	  */
	public int rightCells() {
		int sum = 0;
		if(_grid[facingX(turn(1))][facingY(turn(1))] == 1) sum++;
		if(_grid[facingX(turn(2))][facingY(turn(2))] == 1) sum++;
		return sum;
	}
	
	/**
	 * Executes a backtracking step
	 */
	public void backtrack() {
		if(!_history.isEmpty()) {
			State s = _history.peek();
			x = s.x;
			y = s.y;
			direction = s.direction;
			_history.pop();
		}
	}
	
	/**
	 * @return x-coordinate of the faced cell
	 */
	public int facingX() {
		return facingX(direction);
	}
	
	/**
	 * @return y-coordinate of the faced cell
	 */
	public int facingY() {
		return facingY(direction);
	}
	
	/**
	 * @param direction Direction
	 * @return x-coodrinate of the cell in the given direction
	 */
	public int facingX(int direction) {
		switch (direction) {
		case NE:
			return x+1;
		case E:
			return x+1;
		case SE:
			return x+1;
		case SW:
			return x-1;
		case W:
			return x-1;
		case NW:
			return x-1;
		default:
			return x;
		}
	}
	
	/**
	 * @param direction Direction
	 * @return y-coodrinate of the cell in the given direction
	 */
	public int facingY(int direction) {
		switch (direction) {
		case N:
			return y+1;
		case NE:
			return y+1;
		case SE:
			return y-1;
		case S:
			return y-1;
		case SW:
			return y-1;
		case NW:
			return y+1;
		default:
			return y;
		}
	}
}
