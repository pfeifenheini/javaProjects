import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ALifeRobot extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JPanel _contentPane;
	
	private JPanel _centerPane;
	private JPanel _topPane;
	private JPanel _bottomPane;
	
	/** The Grid */
	private RobotGrid sim;
	
	/** Button to show previous step */
	private JButton _back = 
			new JButton("Back");
	/** Button to perform a single step */
	private JButton _step = 
			new JButton("Step");
	/** Button to slow down the animation speed */
	private JButton _slower = 
			new JButton("<<");
	/** Button to start the animation */
	private JButton _run = 
			new JButton("Run");
	/** Button to increase the animation speed */
	private JButton _faster = 
			new JButton(">>");
	/** Button to reset the whole scene */
	private JButton _reset = 
			new JButton("Reset");
	/** Strategy choice */
	private JComboBox<Robot.Strategy> _strategy =
			new JComboBox<Robot.Strategy>(Robot.Strategy.values());
	/** Check box to toggle highlighted cells */
	private JCheckBox _highlight =
			new JCheckBox("Highlight Cells");
	/** Raster check box */
	private JCheckBox _raster =
			new JCheckBox("Raster");
	/** Zoom in Button */
	private JButton _zoomIn =
			new JButton("+");
	/** Zoom out Button */
	private JButton _zoomOut =
			new JButton("-");
	
	
	
	/**
	 * Create a new Frame and sets up the interface
	 */
	public ALifeRobot() {
		this.setTitle("ALife Robot");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_contentPane = new JPanel();
		_contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		_contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(_contentPane);
		
		_centerPane = new JPanel();
		sim = new RobotGrid();
		_centerPane.add(sim);
		_contentPane.add(_centerPane, BorderLayout.CENTER);
		
		_topPane = new JPanel();
		_topPane.add(new JLabel("Zoom: "));
		_topPane.add(_zoomOut);
		_topPane.add(_zoomIn);
		_topPane.add(_raster);
		_contentPane.add(_topPane,BorderLayout.PAGE_START);
		
		_bottomPane = new JPanel();
		_bottomPane.add(_back);
		_bottomPane.add(_step);
		_bottomPane.add(_slower);
		_bottomPane.add(_run);
		_bottomPane.add(_faster);
		_bottomPane.add(_reset);
		_bottomPane.add(_strategy);
		_bottomPane.add(_highlight);
		_contentPane.add(_bottomPane, BorderLayout.PAGE_END);
		
		_back.addActionListener(this);
		_step.addActionListener(this);
		_faster.addActionListener(this);
		_run.addActionListener(this);
		_slower.addActionListener(this);
		_reset.addActionListener(this);
		_strategy.addActionListener(this);
		_highlight.addActionListener(this);
		_zoomIn.addActionListener(this);
		_zoomOut.addActionListener(this);
		_raster.addActionListener(this);
		
		_faster.setEnabled(false);
		_slower.setEnabled(false);
		_highlight.setSelected(true);
		_raster.setSelected(true);
		
		pack();
	}
	
	/**
	 * Creates a new frame and makes it visible
	 * 
	 * @param args
	 */
    public static void main(String[] args)
    {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ALifeRobot frame = new ALifeRobot();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }

    /**
     * Overrides the actionPerformed method of the ActionListener Interface
     */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == _back) {
			sim.backtrack();
		}
		if(e.getSource() == _step) {
			sim.step();
		}
		if(e.getActionCommand().equals("Run")) {
			_back.setEnabled(false);
			_step.setEnabled(false);
			_faster.setEnabled(true);
			_slower.setEnabled(true);
			_run.setText("Stop");
			Thread t = new Thread(sim);
			t.start();
		}
		if(e.getSource() == _faster) {
			sim.increaseAnimationSpeed();
		}
		if(e.getSource() == _slower) {
			sim.decreaseAnimationSpeed();
		}
		if(e.getActionCommand().equals("Stop")) {
			sim.stopAnimation();
		}
		if(e.getSource() == _reset) {
			sim.reset();
			_strategy.setSelectedIndex(sim.getStrategy().ordinal());
		}
		if(e.getSource() == _strategy) {
			sim.setStrategy((Robot.Strategy)_strategy.getSelectedItem());
		}
		if(e.getSource() == _highlight) {
			sim.paintHighlights(_highlight.isSelected());
		}
		if(e.getSource() == _raster) {
			sim.paintRaster(_raster.isSelected());
		}
		if(e.getActionCommand().equals("Stop") || e.getActionCommand().equals("Reset")) {
			_back.setEnabled(true);
			_step.setEnabled(true);
			_faster.setEnabled(false);
			_slower.setEnabled(false);
			_run.setText("Run");
		}
		if(e.getSource() == _zoomIn) {
			int current = sim.pixelSize();
			current++;
			if(current <= 21) {
				sim.setPixelSize(current);
				pack();
			}
		}
		if(e.getSource() == _zoomOut) {
			int current = sim.pixelSize();
			current--;
			if(current >= 5) {
				sim.setPixelSize(current);
				pack();
			}
		}
		repaint();
	}
}
