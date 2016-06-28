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
	private JComboBox<String> _strategy =
			new JComboBox<String>(Simulation.STRATEGIES);
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
	
	/** the simulation */
	private Simulation sim;
	
	/**
	 * Create a new Frame and sets up the interface
	 */
	public ALifeRobot() {
		this.setTitle("ALife Robot");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 0, Simulation.DEFAULT_PIXEL_SIZE*Simulation.GRID_SIZE+100, Simulation.DEFAULT_PIXEL_SIZE*Simulation.GRID_SIZE+140);
		_contentPane = new JPanel();
		_contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		_contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(_contentPane);
		
		JPanel center = new JPanel();
		sim = new Simulation();
		center.add(sim);
		_contentPane.add(center, BorderLayout.CENTER);
		
		JPanel zoom = new JPanel();
		zoom.add(new JLabel("Zoom: "));
		zoom.add(_zoomOut);
		zoom.add(_zoomIn);
		zoom.add(_raster);
		_contentPane.add(zoom,BorderLayout.PAGE_START);
		
		JPanel buttons = new JPanel();
		buttons.add(_back);
		buttons.add(_step);
		buttons.add(_slower);
		buttons.add(_run);
		buttons.add(_faster);
		buttons.add(_reset);
		buttons.add(_strategy);
		buttons.add(_highlight);
		_contentPane.add(buttons, BorderLayout.PAGE_END);
		
		_back.addActionListener(sim);
		_step.addActionListener(sim);
		_faster.addActionListener(sim);
		_faster.addActionListener(this);
		_run.addActionListener(sim);
		_run.addActionListener(this);
		_slower.addActionListener(sim);
		_slower.addActionListener(this);
		_reset.addActionListener(sim);
		_reset.addActionListener(this);
		_strategy.addActionListener(sim);
		_highlight.addActionListener(sim);
		_zoomIn.addActionListener(this);
		_zoomOut.addActionListener(this);
		_raster.addActionListener(sim);
		
		_faster.setEnabled(false);
		_slower.setEnabled(false);
		_highlight.setSelected(true);
		_raster.setSelected(true);
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
		if(e.getActionCommand().equals("Run")) {
			_back.setEnabled(false);
			_step.setEnabled(false);
			_faster.setEnabled(true);
			_slower.setEnabled(true);
			_run.setText("Stop");
		}
		if(e.getActionCommand().equals("Stop") || e.getActionCommand().equals("Reset")) {
			_back.setEnabled(true);
			_step.setEnabled(true);
			_faster.setEnabled(false);
			_slower.setEnabled(false);
			_run.setText("Run");
		}
		if(e.getActionCommand().equals("Reset")) {
			_strategy.setSelectedIndex(sim.getStrategy());
		}
		if(e.getSource() == _zoomIn) {
			int current = sim.getPixelSize();
			current++;
			if(current <= 21) {
				setSize(current*Simulation.GRID_SIZE+100, current*Simulation.GRID_SIZE+140);
				sim.changePixelSize(current);
			}
		}
		if(e.getSource() == _zoomOut) {
			int current = sim.getPixelSize();
			current--;
			if(current >= 5) {
				setSize(current*Simulation.GRID_SIZE+100, current*Simulation.GRID_SIZE+140);
				sim.changePixelSize(current);
			}
		}
	}
}
