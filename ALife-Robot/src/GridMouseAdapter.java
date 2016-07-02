import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This class handles the manipulation of the grid using the mouse cursor.
 * 
 * @author Martin
 *
 */
public class GridMouseAdapter extends MouseAdapter {

	private RobotGrid _grid;
	
	private boolean _leftClick;
	private boolean _robotDragged;
	private boolean _robotChanged;
	
	private int _mouseX = -1;
	private int _mouseY = -1;
	
	public GridMouseAdapter(RobotGrid grid) {
		_grid = grid;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(_grid.isAnimating()) return;
		
		if(e.getButton() == MouseEvent.BUTTON1) _leftClick = true;
		else _leftClick = false;
		
		if(calcMousePos()) {
			if(_grid.robotAt(_mouseX, _mouseY) && _leftClick) {
				_robotDragged = true;
			}
			else {
				_robotDragged = false;
				if(_leftClick)
					_grid.setState(_mouseX, _mouseY, 1);
				else
					_grid.setState(_mouseX, _mouseY, 0);
			}
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		if(_grid.isAnimating()) return;
		
		if(calcMousePos()) {
			if(_robotDragged && _grid.getState(_mouseX, _mouseY) != 1) {
				_grid.moveRobot(_mouseX, _mouseY);
				_robotChanged = true;
			}
			else {
				if(_leftClick)
					_grid.setState(_mouseX, _mouseY, 1);
				else
					_grid.setState(_mouseX, _mouseY, 0);
			}
		}
		_grid.repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(_grid.isAnimating()) return;
		
		if(!calcMousePos() && _grid.robotAt(_mouseX, _mouseY) && !_robotChanged) {
			if(_leftClick) {
				_grid.turnRobot(-1);
			}
			else {
				_grid.turnRobot(1);
			}
		}
		
		_robotDragged = false;
		_robotChanged = false;
		_mouseX = -1;
		_mouseY = -1;
		_grid.repaint();
	}
	
	/**
	 * Calculates mouse coordinates on grid. 
	 * 
	 * @return true iff position has changed
	 */
	private boolean calcMousePos() {
		Point p = _grid.getMousePosition();
		if(p==null) return false;
		
		int x = (int)(p.x/_grid.pixelSize());
		int y = (_grid.getSideLength()-(int)(p.y/_grid.pixelSize())-1);
		if(_mouseX == x && _mouseY == y) return false;
		
		_mouseX = x;
		_mouseY = y;
		return true;
	}
}
