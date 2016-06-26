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

public class Simulation extends JPanel implements ActionListener, Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static int res = 13;
	public static final int gridSize = 50;
	
	private int[][] _grid;
	private Robot _robot;
	
	private volatile boolean isRunning = false;
	private volatile int _animationSpeed = 400;
	
	public Simulation() {
		_grid = new int[gridSize][gridSize];
		_robot = new Robot(_grid,gridSize);
		readFile();
		setPreferredSize(new Dimension(gridSize*res,gridSize*res));
		setBackground(Color.white);
	}
	
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
		} finally {
			try {
				if(br != null) br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		while(isRunning) {
			_robot.step();
			repaint();
			try {
				Thread.sleep(_animationSpeed);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for(int x=0;x<gridSize;x++) {
			for(int y=0;y<gridSize;y++) {
				if(_grid[x][y] == 1) {
					g.setColor(Color.black);
					g.fillRect(x*res, (gridSize-y-1)*res, res, res);
				}
				else if(_grid[x][y] > 1) {
					float[] c = new float[3];
					Color.RGBtoHSB(255-_grid[x][y], 240, 240, c);
					g.setColor(Color.getHSBColor(c[0], c[1], c[2]));
					g.fillRect(x*res, (gridSize-y-1)*res, res, res);
				}
			}
		}
		
		g.setColor(Color.blue);
		g.fillRect(_robot.x*res, (gridSize-_robot.y-1)*res, res, res);		
		g.setColor(Color.green);
		g.fillOval(_robot.x*res+1, (gridSize-_robot.y-1)*res+1, res-2, res-2);
		
		g.setColor(Color.red);
		for(int i=-2;i<3;i++)
			drawSensoredCell(g, i);
	}
	
	private void drawSensoredCell(Graphics g, int turning) {
		if(_grid[_robot.facingX(_robot.turn(turning))][_robot.facingY(_robot.turn(turning))] == 1) {
			g.fillRect(_robot.facingX(_robot.turn(turning))*res, (gridSize-_robot.facingY(_robot.turn(turning))-1)*res, res, res);
		}
		g.drawRect(_robot.facingX(_robot.turn(turning))*res, (gridSize-_robot.facingY(_robot.turn(turning))-1)*res, res, res);
	}
	
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
			_animationSpeed = Math.max(5, (int)(_animationSpeed/1.3));
		}
		if(e.getActionCommand().equals("-")) {
			_animationSpeed = Math.min(2000, (int)(_animationSpeed*1.3));
		}
		if(e.getActionCommand().equals("Stop")) {
			isRunning = false;
		}
		if(e.getActionCommand().equals("Reset")) {
			isRunning = false;
			try {
				Thread.sleep(_animationSpeed+100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			for(int x=0;x<gridSize;x++) {
				for(int y=0;y<gridSize;y++) {
					if(_grid[x][y] != 1) {
						_grid[x][y] = 0;
					}
				}
			}
			_robot = new Robot(_grid,gridSize);
			_animationSpeed = 400;
			repaint();
		}
	}
}
