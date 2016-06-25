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
	
	public Robot(int[][] grid) {
		this(48,17,S,grid);
	}
	
	public Robot(int startX, int startY, int direction, int[][] grid) {
		x = startX;
		y = startY;
		this.direction = direction;
		_grid = grid;
		_history = new Stack<State>();
		_grid[x][y] = 2;
	}
	
	public void step() {
		for(int i=0;i<8;i++){
			if(_grid[facingX()][facingY()] != 0)
				direction = (direction+1)%8;
		}
		if(_grid[facingX()][facingY()] != 0) {
			backtrack();
		}
		else {
			_history.push(new State(x,y,direction));
			x = facingX();
			y = facingY();
			_grid[x][y] = 2;
		}
	}
	
	public void backtrack() {
		if(!_history.isEmpty()) {
			State s = _history.lastElement();
			x = s.x;
			y = s.y;
			direction = s.direction;
			_history.pop();
		}
	}
	
	public int facingX() {
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
	
	public int facingY() {
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
