import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JPanel;

public class RobotGrid extends JPanel implements Runnable { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** default pixel size */
	public static final int DEFAULT_PIXEL_SIZE = 13;
	/** default grid side length */
	public static final int DEFAULT_GRID_SIZE = 50;
	/** default animation delay */
	public static final int DEFAULT_ANIMATION_DELAY = 500;
	
	/** the grid */
	private int[][] _grid;
	/** the robot */
	private Robot _robot;
	
	/** */
	private volatile boolean _isRunning = false;
	public boolean isRunning() {return _isRunning;}
	/** */
	private volatile boolean _highlightCells = true;
	/** */
	private volatile boolean _showRaster = true;
	/** delay between two animation steps */
	private volatile int _animationDelay = DEFAULT_ANIMATION_DELAY;
	/** pixel size of a cell */
	private int _pixelSize = DEFAULT_PIXEL_SIZE;
	/** @return pixel size of a cell */
	public int pixelSize() {return _pixelSize;}
	/** grid side length */
	private int _gridSize = DEFAULT_GRID_SIZE;
	
	/**
	 * constructor
	 */
	public RobotGrid() {
		_grid = new int[_gridSize][_gridSize];
		_robot = new Robot(_grid,_gridSize);
		readFile();
		setPreferredSize(new Dimension(_gridSize*_pixelSize,_gridSize*_pixelSize));
		setBackground(Color.white);
		GridMouseAdapter a = new GridMouseAdapter(this);
		addMouseListener(a);
		addMouseMotionListener(a);
	}
	
	/**
	 * Reads the grid from the file 'SS16-4201-PA-F.grid'
	 * that has to lie in the same folder from which the 
	 * application is started.
	 */
	private void readFile() {
		BufferedReader br = null;
		String line;
		Scanner s;
		int x, y;
		try {
			br = new BufferedReader(new FileReader("SS16-4201-PA-F.grid"));
			
			while((line = br.readLine()) != null) {
				
				if(line.charAt(0) != '#') {
					s = new Scanner(line);
					x = s.nextInt();
					y = s.nextInt();
					_grid[x][y] = 1;
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		} finally {
			try {
				if(br != null) br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Overrides run method of Runnable interface.
	 * Animates one step of the robot after another.
	 */
	@Override
	public void run() {
		_isRunning = true;
		while(_isRunning) {
			_robot.step();
			repaint();
			try {
				Thread.sleep(_animationDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Overrides the paintComponent method of the JPanel class.
	 * Draws the grid and the robot in the scene. Highlights
	 * the cells which the robot observes to determine its 
	 * movements.
	 * 
	 * @param g Graphics
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Draw Grid
		for(int x=0;x<_gridSize;x++) {
			for(int y=0;y<_gridSize;y++) {
				if(_grid[x][y] == 1) {
					g.setColor(Color.black);
					g.fillRect(x*_pixelSize, (_gridSize-y-1)*_pixelSize, _pixelSize, _pixelSize);
				}
				else {
					if(_showRaster) {
						g.setColor(Color.lightGray);
						g.drawRect(x*_pixelSize, (_gridSize-y-1)*_pixelSize, _pixelSize, _pixelSize);
					}
					
					if(_grid[x][y] > 1) {
						float[] c = new float[3];
						Color.RGBtoHSB(255-_grid[x][y], 255-_grid[x][y], 255, c);
						g.setColor(Color.getHSBColor(c[0], c[1], c[2]));
						g.fillOval(x*_pixelSize+1, (_gridSize-y-1)*_pixelSize+1, _pixelSize-2, _pixelSize-2);
					}
				}
			}
		}
		
		// Highlight observed cells
		if(_highlightCells) {
			if(_robot.strategy == Robot.Strategy.Breitenberg) {
				for(int i=-2;i<3;i++)
					if(i != 0)
						paintHighlightedCells(g, i);
			}
			else if(_robot.strategy == Robot.Strategy.WallFollow){
				for(int i=1;i<8;i++)
					paintHighlightedCells(g, i);
			}
		}
		
		// Paint Robot
		paintDirection(g);
		g.setColor(Color.gray);
		g.fillOval(_robot.x()*_pixelSize+1, (_gridSize-_robot.y()-1)*_pixelSize+1, _pixelSize-2, _pixelSize-2);
		g.setColor(Color.black);
		g.drawOval(_robot.x()*_pixelSize+1, (_gridSize-_robot.y()-1)*_pixelSize+1, _pixelSize-2, _pixelSize-2);
	}
	
	/**
	 * Draws the direction of the robot.
	 * @param g Graphics
	 */
	private void paintDirection(Graphics g) {
		if(_robot.pathBlocked(0))
			g.setColor(Color.red);
		else
			g.setColor(Color.green);
		
		int centerX = _robot.x()*_pixelSize;
		int centerY = (_gridSize-_robot.y()-1)*_pixelSize;
		int angle = -_robot.direction().degree()-245;
		
		g.fillArc(centerX-_pixelSize, centerY-_pixelSize, 3*_pixelSize, 3*_pixelSize, angle, -50);
		g.setColor(Color.black);
	}
	
	/**
	 * Highlights the cells which the robot observes to determine
	 * it movements.
	 * 
	 * @param g Graphics
	 * @param turning direction relative to current direction of the robot
	 */
	private void paintHighlightedCells(Graphics g, int turning) {
		g.setColor(Color.red);
		if(_grid[_robot.facingX(_robot.getDirection(turning))][_robot.facingY(_robot.getDirection(turning))] == 1) {
			g.fillRect(_robot.facingX(_robot.getDirection(turning))*_pixelSize, (_gridSize-_robot.facingY(_robot.getDirection(turning))-1)*_pixelSize, _pixelSize, _pixelSize);
		}
		g.drawRect(_robot.facingX(_robot.getDirection(turning))*_pixelSize, (_gridSize-_robot.facingY(_robot.getDirection(turning))-1)*_pixelSize, _pixelSize, _pixelSize);
		if(_showRaster)
			g.drawRect(_robot.facingX(_robot.getDirection(turning))*_pixelSize+1, (_gridSize-_robot.facingY(_robot.getDirection(turning))-1)*_pixelSize+1, _pixelSize-2, _pixelSize-2);
	}

	/**
	 * @return current strategy
	 */
	public Robot.Strategy getStrategy() {
		return _robot.strategy;
	}
	
	/**
	 * Sets the pixel size.
	 * 
	 * @param size the new size
	 */
	public void setPixelSize(int size) {
		_pixelSize = size;
		setPreferredSize(new Dimension(_gridSize*_pixelSize,_gridSize*_pixelSize));
	}
	
	/**
	 * Tests whether the robot is at the given position.
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return true iff robot is at given position
	 */
	public boolean robotAt(int x, int y) {
		return _robot.x() == x && _robot.y() == y;
	}

	/**
	 * Sets the state of the given cell.
	 * Cells on the border can not be changed (have to be walls).
	 * 
	 * @param x x-coodrinate
	 * @param y y-coordinate
	 * @param state new state
	 */
	public void setState(int x, int y, int state) {
		if(x<=0 || x>=_gridSize-1) return;
		if(y<=0 || y>=_gridSize-1) return;
		_grid[x][y] = state;
	}

	/**
	 * Returns the state of the given cell.
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return state of the cell, 0=free, 1=wall
	 */
	public int getState(int x, int y) {
		if(coordinatesOnGrid(x, y))
			return _grid[x][y];
		return -1;
	}

	/**
	 * Sets the position of the robot.
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public void moveRobot(int x, int y) {
		if(coordinatesOnGrid(x, y))
			_robot.setPosition(x, y);
	}

	/**
	 * Turns the robot.
	 * 
	 * @param offset positiv=clockwise, negative counter clockwise 
	 */
	public void turnRobot(int offset) {
		_robot.turn(offset);
	}

	/**
	 * @return side length of the grid
	 */
	public int getSideLength() {
		return _gridSize;
	}

	/**
	 * Performs a backtracking step.
	 */
	public void backtrack() {
		_grid[_robot.x()][_robot.y()] = 0;
		_robot.backtrack();
	}

	/**
	 * Performs one step of the robot.
	 */
	public void step() {
		_robot.step();
	}

	/**
	 * increases the animation speed.
	 */
	public void increaseAnimationSpeed() {
		_animationDelay = Math.max(5, (int)(_animationDelay/1.5));
	}

	/**
	 * Decreases the animation speed.
	 */
	public void decreaseAnimationSpeed() {
		_animationDelay = Math.min(2000, (int)(_animationDelay*1.5));
	}

	/**
	 * Stops the animation if it is running as a thread.
	 */
	public void stopAnimation() {
		_isRunning = false;
	}

	/**
	 * Resets the whole grid.
	 */
	public void reset() {
		_isRunning = false;
		try {
			Thread.sleep(_animationDelay+100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		for(int x=0;x<_gridSize;x++) {
			for(int y=0;y<_gridSize;y++) {
					_grid[x][y] = 0;
			}
		}
		readFile();
		Robot.Strategy currentStrategy = _robot.strategy;
		_robot = new Robot(_grid,_gridSize);
		_robot.strategy = currentStrategy;
		_animationDelay = DEFAULT_ANIMATION_DELAY;
	}

	/**
	 * Sets the strategy.
	 * 
	 * @param strategy The new strategy
	 */
	public void setStrategy(Robot.Strategy strategy) {
		_robot.strategy = strategy;
	}

	/**
	 * Sets whether the relevant neighbourhood of the robot should be highlighted.
	 * 
	 * @param selected
	 */
	public void paintHighlights(boolean selected) {
		_highlightCells = selected;
	}

	/**
	 * Sets whether the raster lines should be painted.
	 * 
	 * @param selected
	 */
	public void paintRaster(boolean selected) {
		_showRaster = selected;
	}
	
	/**
	 * Tests wheter the given coordinates lie on the grid. 
	 * 
	 * @param x x-coodrinate
	 * @param y y-coodrinate
	 * @return true iff the coordinates lie on the grid
	 */
	public boolean coordinatesOnGrid(int x, int y) {
		if(x<0 || x>=_gridSize) return false;
		if(y<0 || y>=_gridSize) return false;
		return true;
	}
}
