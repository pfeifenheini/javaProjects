import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.Timer;

public class RobotGrid extends JPanel implements ActionListener { 

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
	/** minimum animation delay */
	public static final int MIN_ANIMATION_DELAY = 17;
	/** maximum animation delay */
	public static final int MAX_ANIMATION_DELAY = 1000;
	
	/** the grid */
	private int[][] _grid;
	/** the robot */
	private Robot _robot;
	
	/** timer for the animation */
	private Timer _timer;
	/** */
	private volatile boolean _highlightCells = true;
	/** */
	private volatile boolean _showRaster = true;
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
		_timer = new Timer(0,this);
		_timer.setDelay(DEFAULT_ANIMATION_DELAY);
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
	 * Starts the animation.
	 */
	public void startAnimation() {
		_timer.start();
	}
	
	public boolean isAnimating() {
		return _timer.isRunning();
	}
	
	public int getAnimationDelay() {
		return _timer.getDelay();
	}

	/**
	 * increases the animation speed.
	 */
	public void increaseAnimationSpeed() {
		_timer.setDelay(Math.max(MIN_ANIMATION_DELAY, (int)(_timer.getDelay()/2)));
		_timer.restart();
	}

	/**
	 * Decreases the animation speed.
	 */
	public void decreaseAnimationSpeed() {
		_timer.setDelay(Math.min(MAX_ANIMATION_DELAY, (int)(_timer.getDelay()*2)));
	}

	/**
	 * Animation step that is called by the timer.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		_robot.step();
		repaint();
	}
	
	/**
	 * Performs one step of the robot.
	 */
	public void step() {
		_robot.step();
	}

	/**
	 * Stops the animation
	 */
	public void stopAnimation() {
		_timer.stop();
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
	 * Sets whether the raster lines should be painted.
	 * 
	 * @param selected
	 */
	public void paintRaster(boolean selected) {
		_showRaster = selected;
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
					
					if(_grid[x][y] == -1) {
						g.setColor(Color.green);
						g.fillOval(x*_pixelSize+1, (_gridSize-y-1)*_pixelSize+1, _pixelSize-2, _pixelSize-2);
					}
					else if(_grid[x][y] == -2) {
						g.setColor(Color.blue);
						g.fillOval(x*_pixelSize+1, (_gridSize-y-1)*_pixelSize+1, _pixelSize-2, _pixelSize-2);
					}
				}
			}
		}
		
		// Highlight observed cells
		if(_highlightCells) {
			if(_robot.strategy() == Robot.Strategy.Breitenberg) {
				for(int i=-2;i<3;i++)
					if(i != 0)
						paintHighlightedCells(g, i);
			}
			else if(_robot.strategy() == Robot.Strategy.WallFollow){
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
	 * Sets whether the relevant neighbourhood of the robot should be highlighted.
	 * 
	 * @param selected
	 */
	public void paintHighlights(boolean selected) {
		_highlightCells = selected;
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
		return _robot.strategy();
	}
	
	/**
	 * Sets the strategy.
	 * 
	 * @param strategy The new strategy
	 */
	public void setStrategy(Robot.Strategy strategy) {
		_robot.setStrategy(strategy);
		clearGrid();
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

	/**
	 * Sets the position of the robot.
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public void moveRobot(int x, int y) {
		if(coordinatesOnGrid(x, y))
			_robot.setPosition(x, y);
		clearGrid();
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
		_robot.backtrack();
	}

	/**
	 * Resets the whole grid.
	 */
	public void reset() {
		stopAnimation();
		while(_timer.isRunning()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		for(int x=0;x<_gridSize;x++) {
			for(int y=0;y<_gridSize;y++) {
					_grid[x][y] = 0;
			}
		}
		readFile();
		Robot.Strategy currentStrategy = _robot.strategy();
		_robot = new Robot(_grid,_gridSize);
		_robot.setStrategy(currentStrategy);
		_timer.setDelay(DEFAULT_ANIMATION_DELAY);
	}

	/**
	 * Sets all cells that are no walls to 0
	 */
	public void clearGrid() {
		for(int i=0;i<_gridSize;i++) {
			for(int j=0;j<_gridSize;j++) {
				if(_grid[i][j] != 1)
					_grid[i][j] = 0;
			}
		}
	}
}
