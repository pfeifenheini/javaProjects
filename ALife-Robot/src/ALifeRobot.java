import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ALifeRobot extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JPanel _contentPane;
	
	private JButton _back = 
			new JButton("Back");
	private JButton _step = 
			new JButton("Step");
	private JButton _slower = 
			new JButton("-");
	private JButton _run = 
			new JButton("Run");
	private JButton _faster = 
			new JButton("+");
	private JButton _reset = 
			new JButton("Reset");
	
	public ALifeRobot() {
		this.setTitle("ALife Robot");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 0, Simulation.res*Simulation.gridSize+100, Simulation.res*Simulation.gridSize+100);
		_contentPane = new JPanel();
		_contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		_contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(_contentPane);
		
		JPanel center = new JPanel();
		Simulation sim = new Simulation();
		center.add(sim);
		_contentPane.add(center, BorderLayout.CENTER);
		
		JPanel buttons = new JPanel();
		buttons.add(_back);
		buttons.add(_step);
		buttons.add(_slower);
		buttons.add(_run);
		buttons.add(_faster);
		buttons.add(_reset);
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
		
		_faster.setEnabled(false);
		_slower.setEnabled(false);
	}
	
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Run")) {
			_back.setEnabled(false);
			_step.setEnabled(false);
			_faster.setEnabled(true);
			_slower.setEnabled(true);
			_run.setText("Stop");
		}
		if(e.getActionCommand().equals("Stop")) {
			_back.setEnabled(true);
			_step.setEnabled(true);
			_faster.setEnabled(false);
			_slower.setEnabled(false);
			_run.setText("Run");
		}
		if(e.getActionCommand().equals("Reset")) {
			_back.setEnabled(true);
			_step.setEnabled(true);
			_faster.setEnabled(false);
			_slower.setEnabled(false);
			_run.setText("Run");
		}
	}
}
