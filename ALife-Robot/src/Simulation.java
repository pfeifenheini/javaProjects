import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class Simulation extends JPanel implements ActionListener, Runnable, MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static int DEFAULT_PIXEL_SIZE = 11;
	/** grid side length */
	public static final int GRID_SIZE = 50;
	/** default animation delay */
	public static final int DEFAULT_ANIMATION_DELAY = 500;
	/** list of possible strategies */
	public static final String[] STRATEGIES = {"Breitenberg", "Wall Follow"};
	
	/** the grid */
	private int[][] _grid;
	/** the robot */
	private Robot _robot;
	
	/** indicates that the animation is running */
	private volatile boolean isRunning = false;
	/** */
	private volatile boolean highlightCells = true;
	/** delay between two animation steps */
	private volatile int _animationDelay = DEFAULT_ANIMATION_DELAY;
	/** pixel size of a cell */
	private int _pixelSize = 11;
	
	/** current x-coordinate of the mouse */
	private int mouseX = -1;
	/** current y-coordinate of the mouse */
	private int mouseY = -1;
	/** indication or the pressed mouse button */
	private boolean leftClick = false;
	/** */
	private boolean robotDragged = false;
	/** */
	private boolean robotChanged = false;
	
	/**
	 * constructor
	 */
	public Simulation() {
		_grid = new int[GRID_SIZE][GRID_SIZE];
		_robot = new Robot(_grid,GRID_SIZE);
		readFile();
		setPreferredSize(new Dimension(GRID_SIZE*_pixelSize,GRID_SIZE*_pixelSize));
		setBackground(Color.white);
		addMouseListener(this);
		addMouseMotionListener(this);
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
		while(isRunning) {
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
		for(int x=0;x<GRID_SIZE;x++) {
			for(int y=0;y<GRID_SIZE;y++) {
				if(_grid[x][y] == 1) {
					g.setColor(Color.black);
					g.fillRect(x*_pixelSize, (GRID_SIZE-y-1)*_pixelSize, _pixelSize, _pixelSize);
				}
				else if(_grid[x][y] > 1) {
					float[] c = new float[3];
					Color.RGBtoHSB(255-_grid[x][y], 255-_grid[x][y], 255, c);
					g.setColor(Color.getHSBColor(c[0], c[1], c[2]));
					g.fillOval(x*_pixelSize+1, (GRID_SIZE-y-1)*_pixelSize+1, _pixelSize-2, _pixelSize-2);
				}
			}
		}
		
		// Highlight observed cells
		if(highlightCells) {
			if(_robot.strategy == 0) {
				for(int i=-2;i<3;i++)
					if(i != 0)
						highlightCell(g, i);
			}
			else {
				for(int i=1;i<8;i++)
					highlightCell(g, i);
			}
		}
		
		// Draw Robot
		paintDirection(g);
		g.setColor(Color.blue);
		g.fillOval(_robot.x*_pixelSize+1, (GRID_SIZE-_robot.y-1)*_pixelSize+1, _pixelSize-2, _pixelSize-2);
		g.setColor(Color.black);
		g.drawOval(_robot.x*_pixelSize+1, (GRID_SIZE-_robot.y-1)*_pixelSize+1, _pixelSize-2, _pixelSize-2);
		

		
		
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
		
		int centerX = _robot.x*_pixelSize;
		int centerY = (GRID_SIZE-_robot.y-1)*_pixelSize;
		int angle = _robot.direction*(-45)-245;
		
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
	private void highlightCell(Graphics g, int turning) {
		g.setColor(Color.red);
		if(_grid[_robot.facingX(_robot.turn(turning))][_robot.facingY(_robot.turn(turning))] == 1) {
			g.fillRect(_robot.facingX(_robot.turn(turning))*_pixelSize, (GRID_SIZE-_robot.facingY(_robot.turn(turning))-1)*_pixelSize, _pixelSize, _pixelSize);
		}
		g.drawRect(_robot.facingX(_robot.turn(turning))*_pixelSize, (GRID_SIZE-_robot.facingY(_robot.turn(turning))-1)*_pixelSize, _pixelSize, _pixelSize);
	}
	
	/**
	 * Overrides actionPerformed method of ActionListener interface.
	 * Defines behavior when a button is pressed
	 * 
	 * @param e ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Back")) {
			_grid[_robot.x][_robot.y] = 0;
			_robot.backtrack();
			repaint();
		}
		if(e.getActionCommand().equals("Step")) {
			_robot.step();
			repaint();
		}
		if(e.getActionCommand().equals("Run")) {
			Thread t = new Thread(this);
			isRunning = true;
			t.start();
		}
		if(e.getActionCommand().equals(">>")) {
			_animationDelay = Math.max(5, (int)(_animationDelay/1.5));
		}
		if(e.getActionCommand().equals("<<")) {
			_animationDelay = Math.min(2000, (int)(_animationDelay*1.5));
		}
		if(e.getActionCommand().equals("Stop")) {
			isRunning = false;
		}
		if(e.getActionCommand().equals("Reset")) {
			isRunning = false;
			try {
				Thread.sleep(_animationDelay+100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			for(int x=0;x<GRID_SIZE;x++) {
				for(int y=0;y<GRID_SIZE;y++) {
						_grid[x][y] = 0;
				}
			}
			readFile();
			int currentStrategy = _robot.strategy;
			_robot = new Robot(_grid,GRID_SIZE);
			_robot.strategy = currentStrategy;
			_animationDelay = DEFAULT_ANIMATION_DELAY;
			repaint();
		}
		if(e.getActionCommand().equals("comboBoxChanged")) {
			@SuppressWarnings("unchecked")
			JComboBox<String> b = (JComboBox<String>) e.getSource();
			_robot.strategy = b.getSelectedIndex();
			repaint();
		}
		if(e.getActionCommand().equals("Highlight Cells")) {
			if(highlightCells) highlightCells = false;
			else highlightCells = true;
			repaint();
		}
	}
	
	/**
	 * Calculates mouse coordinates on grid. 
	 * 
	 * @return true iff position has changed
	 */
	private boolean calcMousePos() {
		Point p = getMousePosition();
		if(p==null) return false;
		int x = (int)(p.x/_pixelSize);
		int y = (GRID_SIZE-(int)(p.y/_pixelSize)-1);
		if(mouseX == x && mouseY == y) return false;
		mouseX = x;
		mouseY = y;
		return true;
	}
	
	/**
	 * Sets a cell as a wall or free depending on which mouse
	 * button is pressed. Left Click sets wall, all other clicks
	 * delete walls.
	 */
	private void toggleCell() {
		if(!robotDragged) {
			if(mouseX<=0 || mouseX>=GRID_SIZE-1 || mouseY<=0 || mouseY>=GRID_SIZE-1) return;
			
			if(leftClick) {
				_grid[mouseX][mouseY] = 1;
			}
			else {
				_grid[mouseX][mouseY] = 0;
			}
			repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	/**
	 * Overrides mousePresed method of MouseListener Interface.
	 * 
	 * @param e MouseEvent
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if(isRunning) return;
		
		if(e.getButton() == MouseEvent.BUTTON1) leftClick = true;
		else leftClick = false;
		
		if(calcMousePos()) {
			if(mouseX == _robot.x && mouseY == _robot.y && leftClick) {
				robotDragged = true;
			}
			else {
				robotDragged = false;
				toggleCell();
			}
		}
	}

	/**
	 * Overrides mouseReleased method of MouseListener Interface.
	 * 
	 * @param arg0
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(isRunning) return;
		
		if(!calcMousePos() && mouseX == _robot.x && mouseY == _robot.y && !robotChanged) {
			if(leftClick) {
				_robot.direction = _robot.turn(-1);
			}
			else {
				_robot.direction = _robot.turn(1);
			}
		}
		
		robotDragged = false;
		robotChanged = false;
		mouseX = -1;
		mouseY = -1;
		repaint();
	}

	/**
	 * Overrides mouseDragged method of MouseMotionListener Interface.
	 * 
	 * @param arg0
	 */
	@Override
	public void mouseDragged(MouseEvent arg0) {
		if(isRunning) return;
		if(calcMousePos()) {
			if(robotDragged && _grid[mouseX][mouseY] != 1) {
				_robot.setPosition(mouseX, mouseY);
				robotChanged = true;
			}
			else {
				toggleCell();
			}
		}
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		
	}

	public int getStrategy() {
		return _robot.strategy;
	}

	public int getPixelSize() {
		return _pixelSize;
	}
	
	public void changePixelSize(int value) {
		_pixelSize = value;
		setPreferredSize(new Dimension(GRID_SIZE*_pixelSize,GRID_SIZE*_pixelSize));
		repaint();
	}
}
