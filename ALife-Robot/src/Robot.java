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
	
	private int _gridSize;
	private int[][] _grid;
	private Stack<State> _history;
	
	public int x;
	public int y;
	public int direction;
	
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
	
	public Robot(int[][] grid, int gridSize) {
		this(48,17,S,grid,gridSize);
	}
	
	public Robot(int startX, int startY, int direction, int[][] grid, int gridSize) {
		x = startX;
		y = startY;
		this.direction = direction;
		_gridSize = gridSize;
		_grid = grid;
		_history = new Stack<State>();
	}
	
	public void step() {
		State s = null;
		if(!_history.isEmpty())
			s = _history.lastElement();
		if(s == null || !(s.x == x && s.y == y && s.direction == direction))
			_history.push(new State(x,y,direction));
		
		_grid[x][y] = 255;
		
		if(leftCells() > rightCells()) {
			direction = turn(1);
		}
		if(rightCells() > leftCells()) {
			direction = turn(-1);
		}
		if(_grid[facingX()][facingY()] != 1) {
			x = facingX();
			y = facingY();
		}
		else if(rightCells() == leftCells()) {
			if(Math.random()<0.5)
				direction = turn(-1);
			else
				direction = turn(1);
		}
		
		for(int x=0;x<_gridSize;x++) {
			for(int y=0;y<_gridSize;y++) {
				if(_grid[x][y] > 20) {
					_grid[x][y]--;
				}
			}
		}
		
	}
	
	public int turn(int dir) {
		if(dir>-8 && dir<8) {
			return (direction+8+dir)%8;
		}
		return direction;
	}
	
	public int leftCells() {
		int sum = 0;
		if(_grid[facingX(turn(-1))][facingY(turn(-1))] == 1) sum++;
		if(_grid[facingX(turn(-2))][facingY(turn(-2))] == 1) sum++;
//		if(_grid[facingX(turn(-3))][facingY(turn(-3))] == 1) sum++;
		return sum;
	}
	
	public int rightCells() {
		int sum = 0;
		if(_grid[facingX(turn(1))][facingY(turn(1))] == 1) sum++;
		if(_grid[facingX(turn(2))][facingY(turn(2))] == 1) sum++;
//		if(_grid[facingX(turn(3))][facingY(turn(3))] == 1) sum++;
		return sum;
	}
	
	public void backtrack() {
		if(!_history.isEmpty()) {
			State s = _history.peek();
			x = s.x;
			y = s.y;
			direction = s.direction;
			_history.pop();
		}
	}
	
	public int facingX() {
		return facingX(direction);
	}
	
	public int facingY() {
		return facingY(direction);
	}
	
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
