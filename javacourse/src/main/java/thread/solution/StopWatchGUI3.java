package thread.solution;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Similar to StopWatchGUI, but at timeout run extra on original thread.
 * @author john.denny@gmail.com
 *
 */
public class StopWatchGUI3 implements ActionListener {
	private JLabel lab1;
	private JLabel lab2;

	public StopWatchGUI3() {
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
		printThreadData();
		StopWatchGUI3 sw3 = new StopWatchGUI3();
		sw3.doSomeMoreWork();
	}
	private static void printThreadData() {
		Thread mainThread = Thread.currentThread();
		System.out.println("mainThread.id=" + mainThread.getId() +
				"; name=" + mainThread.getName() +
				"; priority=" + mainThread.getPriority());
	}
	private synchronized void doSomeMoreWork() {
		System.out.println("StopWatchGUI3.doSomeMorework()");
		try {
			wait();
		} catch (InterruptedException e) {
			System.out.println("StopWatchGUI3.doSomeMorework() InterruptedException: " + e);
		}
		System.out.println("StopWatchGUI3.doSomeMorework() after wait()");
	}
	class StopWatch implements Runnable {
		private JLabel label;
		
		public StopWatch(JLabel label) {
			this.label = label;
			new Thread(this).start();
		}
		public void run() {
			printThreadData();
			for (int i = 0; i < 20; i++) {
				label.setText(Integer.toString(i));
				try { Thread.sleep(200); }
				catch (InterruptedException e) {
					e.printStackTrace(); }
			}
			notifyMainThread();
		}
		private void notifyMainThread() {
			synchronized(StopWatchGUI3.this) {
				StopWatchGUI3.this.notify();
			}
			
		}
	}
}