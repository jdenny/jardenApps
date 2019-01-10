package thread.solution;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StopWatchGUI2 implements ActionListener {
	private JLabel lab1;
	private JLabel lab2;

	public StopWatchGUI2() {
		JFrame frame = new JFrame("StopWatchGUI");
		Container container = frame.getContentPane();
		JPanel panel = new JPanel();
		JButton goButton = new JButton("Go");
		lab1 = new JLabel("00");
		lab2 = new JLabel("00");
		panel.add(goButton);
		panel.add(lab1);
		panel.add(lab2);
		container.add(panel, BorderLayout.NORTH);
		frame.setSize(300, 80);
		goButton.addActionListener(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		new StopWatch(lab1);
		new StopWatch(lab2);
	}
	public static void main(String[] args) {
		new StopWatchGUI2();
	}
	class StopWatch extends Thread {
		private JLabel label;
		
		public StopWatch(JLabel label) {
			this.label = label;
			this.start();
		}
		public void run() {
			for (int i = 0; i < 20; i++) {
				label.setText(Integer.toString(i));
				try { Thread.sleep(200); }
				catch (InterruptedException e) {
					e.printStackTrace(); }
			}
		}
	}
}