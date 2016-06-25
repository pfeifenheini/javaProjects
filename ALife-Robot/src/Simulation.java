import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JPanel;

public class Simulation extends JPanel implements ActionListener, Runnable {

	public static final int res = 10;
	public static final int gridSize = 50;
	
	private int[][] _grid;
	private Robot _robot;
	
	private volatile boolean isRunning = false;
	private volatile int _animationSpeed = 400;
	
	public Simulation() {
		_grid = new int[gridSize][gridSize];
		_robot = new Robot(_grid);
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
	public void paint(Graphics g) {
		super.paint(g);
		
		for(int x=0;x<gridSize;x++) {
			for(int y=0;y<gridSize;y++) {
				if(_grid[x][y] == 1) {
					g.setColor(Color.black);
					g.fillRect(x*res, (gridSize-y-1)*res, res, res);
				}
				else if(_grid[x][y] == 2) {
					g.setColor(Color.blue);
					g.fillRect(x*res, (gridSize-y-1)*res, res, res);
				}
			}
		}
		
		g.setColor(Color.red);
		g.fillRect(_robot.x*res, (gridSize-_robot.y-1)*res, res, res);
		g.setColor(Color.green);
		g.fillOval(_robot.facingX()*res, (gridSize-_robot.facingY()-1)*res, res, res);
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
			_animationSpeed = Math.max(10, (int)(_animationSpeed*0.8));
		}
		if(e.getActionCommand().equals("-")) {
			_animationSpeed *= 1.2;
		}
		if(e.getActionCommand().equals("Stop")) {
			isRunning = false;
		}
		if(e.getActionCommand().equals("Reset")) {
			isRunning = false;
			try {
				Thread.sleep(_animationSpeed*2);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for(int x=0;x<gridSize;x++) {
				for(int y=0;y<gridSize;y++) {
					if(_grid[x][y] == 2) {
						_grid[x][y] = 0;
					}
				}
			}
			_robot = new Robot(_grid);
			_animationSpeed = 400;
			repaint();
		}
	}
}
