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

import javax.swing.JPanel;

public class Simulation extends JPanel implements ActionListener, Runnable, MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** pixel size of a cell */
	public static int res = 13;
	/** grid side length */
	public static final int gridSize = 50;
	/** default animation delay */
	public static final int defaultAnimationDelay = 500;
	
	/** the grid */
	private int[][] _grid;
	/** the robot */
	private Robot _robot;
	
	/** indicates that the animation is running */
	private volatile boolean isRunning = false;
	/** delay between two animation steps */
	private volatile int _animationDelay = defaultAnimationDelay;
	
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
		_grid = new int[gridSize][gridSize];
		_robot = new Robot(_grid,gridSize);
		readFile();
		setPreferredSize(new Dimension(gridSize*res,gridSize*res));
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
				// TODO Auto-generated catch block
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
		for(int x=0;x<gridSize;x++) {
			for(int y=0;y<gridSize;y++) {
				if(_grid[x][y] == 1) {
					g.setColor(Color.black);
					g.fillRect(x*res, (gridSize-y-1)*res, res, res);
				}
				else if(_grid[x][y] > 1) {
					float[] c = new float[3];
					Color.RGBtoHSB(255-_grid[x][y], 255-_grid[x][y], 255, c);
					g.setColor(Color.getHSBColor(c[0], c[1], c[2]));
					g.fillRect(x*res, (gridSize-y-1)*res, res, res);
				}
			}
		}
		
		// Draw Robot
		g.setColor(Color.blue);
		g.fillRect(_robot.x*res, (gridSize-_robot.y-1)*res, res, res);		
		g.setColor(Color.green);
		g.fillOval(_robot.x*res+1, (gridSize-_robot.y-1)*res+1, res-2, res-2);
		
		// Highlight observed cells
		g.setColor(Color.red);
		for(int i=-2;i<3;i++)
			drawObservedCell(g, i);
	}
	
	/**
	 * Highlights the cells which the robot observes to determine
	 * it movements.
	 * 
	 * @param g Graphics
	 * @param turning direction relative to current direction of the robot
	 */
	private void drawObservedCell(Graphics g, int turning) {
		if(_grid[_robot.facingX(_robot.turn(turning))][_robot.facingY(_robot.turn(turning))] == 1) {
			g.fillRect(_robot.facingX(_robot.turn(turning))*res, (gridSize-_robot.facingY(_robot.turn(turning))-1)*res, res, res);
		}
		g.drawRect(_robot.facingX(_robot.turn(turning))*res, (gridSize-_robot.facingY(_robot.turn(turning))-1)*res, res, res);
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
		if(e.getActionCommand().equals("+")) {
			_animationDelay = Math.max(5, (int)(_animationDelay/1.5));
		}
		if(e.getActionCommand().equals("-")) {
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
			for(int x=0;x<gridSize;x++) {
				for(int y=0;y<gridSize;y++) {
						_grid[x][y] = 0;
				}
			}
			readFile();
			_robot = new Robot(_grid,gridSize);
			_animationDelay = defaultAnimationDelay;
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
		int x = (int)(p.x/res);
		int y = (gridSize-(int)(p.y/res)-1);
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
			if(mouseX<=0 || mouseX>=gridSize-1 || mouseY<=0 || mouseY>=gridSize-1) return;
			
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
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}
}
