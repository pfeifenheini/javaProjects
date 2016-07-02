import java.util.Stack;

public class Robot {
	
	/** To represent the direction the robot can be turned to */
	public static enum Direction {
		N, NE, E, SE, S, SW, W, NW;
		
		/**
		 * North is at 0 0egrees. Increase by 45 for every clockwise turn.
		 * 
		 * @return
		 */
		public int degree() {
			return ordinal()*45;
		}
	}
	
	public static enum Strategy {
		Breitenberg, WallFollow, DFS;
	}
	
	/** Side length of the grid containing this robot */
	private int _gridSize;
	/** Grid containing this robot */
	private int[][] _grid;
	/** Contains all former positions of the robot */
	private Stack<State> _history;
	/** chosen strategy */
	private volatile Strategy _strategy = Strategy.Breitenberg;
	public Strategy strategy() {return _strategy;}
	
	/** x-coordinate on the grid */
	private int _x;
	/** @return x-coordinate on the grid */
	public int x() {return _x;}
	/** y-coordinate on the grid */
	private int _y;
	/** @return y-coordinate on the grid */
	public int y() {return _y;}
	/** Current direction of the robot */
	private Direction _direction;
	/** @return Current direction of the robot */
	public Direction direction() {return _direction;}
	
	/**
	 * Defines the state of the robot.
	 */
	private class State {
		public int x;
		public int y;
		public Direction direction;
		
		public State(int x, int y, Direction direction) {
			this.x = x;
			this.y = y;
			this.direction = direction;
		}
		
		public boolean equal(State s) {
			return s.x == x && s.y == y && s.direction == direction;
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
		this(48,17,Direction.S,grid,gridSize);
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
	public Robot(int startX, int startY, Direction direction, int[][] grid, int gridSize) {
		_x = startX;
		_y = startY;
		this._direction = direction;
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
		this._x = x;
		this._y = y;
		if(_strategy == Strategy.DFS) {
			for(State s : _history) {
				_grid[s.x][s.y] = 0;
			}
		}
		_history.clear();
	}
	
	/**
	 * Executes on step of the robot.
	 */
	public void step() {
		State last = null, s = new State(_x,_y,_direction);;
		if(!_history.isEmpty()) {
			last = _history.peek();
			if(!last.equal(s))
				_history.push(s);
		}
		else {
			_history.push(s);
		}
		
		
		for(int x=0;x<_gridSize;x++) {
			for(int y=0;y<_gridSize;y++) {
				if(_grid[x][y] >= 5) {
					_grid[x][y] -= 5;
				}
			}
		}
		
		if(_strategy == Strategy.Breitenberg) {
			_grid[_x][_y] = 250;
			breitenbergStep();
			_grid[_x][_y] = 250;
		}
		else if(_strategy == Strategy.WallFollow) {
			_grid[_x][_y] = 250;
			leftHandStep();
			_grid[_x][_y] = 250;
		}
		else if(_strategy == Strategy.DFS) {
			DFSStep();
		}
	}
	
	/**
	 * Breitenberg 3b behavior
	 */
	public void breitenbergStep() {
		
		if(wallsToTheLeft() > wallsToTheRight())
			turn(1);
		else if(wallsToTheRight() > wallsToTheLeft())
			turn(-1);

//		if(_grid[facingX()][facingY()] != 1) {
		if(!pathBlocked(0)) {
			_x = facingX();
			_y = facingY();
		}
		else if(wallsToTheRight() == wallsToTheLeft()) {
			if(Math.random()<0.5)
				turn(-1);
			else
				turn(1);
		}
	}
	
	/**
	 * Wall following with left hand rule
	 */
	public void leftHandStep() {
		if(noWallAround()) {
			_x = facingX();
			_y = facingY();
			return;
		}
		
		if(pathBlocked(0)) {
			if(!(pathBlocked(-1) && pathBlocked(-2) && pathBlocked(-3))) {
				turn(-1);
			}
			else {
				turn(1);
			}
			return;
		}
		
		if(pathBlocked(-1)) {
			_x = facingX();
			_y = facingY();
			return;
		}
		else {
			turn(-1);
		}
	}
	
	public void DFSStep() {
		_grid[_x][_y] = -1;
		int rand = (int)(Math.random()*5);
		if(rand == 1)
			turn(-1);
		else if(rand == 2)
			turn(1);
		else
			turn(0);
		
		for(@SuppressWarnings("unused") Direction d : Direction.values()) {
			if(!pathBlocked(0) && _grid[facingX()][facingY()] >= 0) {
				_x = facingX();
				_y = facingY();
				return;
			}
			turn(1);
		}
		_grid[_x][_y] = -2;
		_direction = _history.peek().direction;
		_history.pop();
		backtrack();
	}
	
	/**
	 * 
	 * @param offset Offset
	 * @return true iff the direction is blocked
	 */
	public boolean pathBlocked(int offset) {
		if(_grid[facingX(getDirection(offset))][facingY(getDirection(offset))] == 1) return true;
		if(getDirection(offset).ordinal()%2 == 1) {
			if(_grid[facingX(getDirection(offset-1))][facingY(getDirection(offset-1))] == 1 &&
					_grid[facingX(getDirection(offset+1))][facingY(getDirection(offset+1))] == 1) {
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
			if(_grid[facingX(getDirection(i))][facingY(getDirection(i))] == 1)
				count++;
		}
		return count == 0;
	}
	
	/**
	 * Calculates the direction relative to the current direction.
	 * A negative value is interpreted as a left turn, a positive
	 * as a right turn.
	 * 
	 * @param offset Turning direction
	 * @return New direction
	 */
	public Direction getDirection(int offset) {
		if(offset>-8 && offset<8) {
			return Direction.values()[(_direction.ordinal()+8+offset)%8];
		}
		return _direction;
	}
	
	public void turn(int offset) {
		_direction = getDirection(offset);
	}
	
	/**
	 * Calculates how many walls are to the left
	 * @return Number of Walls
	 */
	public int wallsToTheLeft() {
		int sum = 0;
		if(_grid[facingX(getDirection(-1))][facingY(getDirection(-1))] == 1) sum++;
		if(_grid[facingX(getDirection(-2))][facingY(getDirection(-2))] == 1) sum++;
		return sum;
	}
	 /**
	  * Calculates how many walls are to the right
	  * @return Number of Walls
	  */
	public int wallsToTheRight() {
		int sum = 0;
		if(_grid[facingX(getDirection(1))][facingY(getDirection(1))] == 1) sum++;
		if(_grid[facingX(getDirection(2))][facingY(getDirection(2))] == 1) sum++;
		return sum;
	}
	
	/**
	 * Executes a backtracking step
	 */
	public void backtrack() {
		if(!_history.isEmpty()) {
			State s = _history.peek();
			_x = s.x;
			_y = s.y;
			_direction = s.direction;
			_history.pop();
		}
	}
	
	/**
	 * @return x-coordinate of the faced cell
	 */
	public int facingX() {
		return facingX(_direction);
	}
	
	/**
	 * @return y-coordinate of the faced cell
	 */
	public int facingY() {
		return facingY(_direction);
	}
	
	/**
	 * @param direction Direction
	 * @return x-coodrinate of the cell in the given direction
	 */
	public int facingX(Direction direction) {
		switch (direction) {
		case NE:
			return _x+1;
		case E:
			return _x+1;
		case SE:
			return _x+1;
		case SW:
			return _x-1;
		case W:
			return _x-1;
		case NW:
			return _x-1;
		default:
			return _x;
		}
	}
	
	/**
	 * @param direction Direction
	 * @return y-coodrinate of the cell in the given direction
	 */
	public int facingY(Direction direction) {
		switch (direction) {
		case N:
			return _y+1;
		case NE:
			return _y+1;
		case SE:
			return _y-1;
		case S:
			return _y-1;
		case SW:
			return _y-1;
		case NW:
			return _y+1;
		default:
			return _y;
		}
	}

	public void setStrategy(Strategy strategy) {
		_strategy = strategy;
		if(strategy == Strategy.DFS) {
			_history.clear();
		}
	}
}
