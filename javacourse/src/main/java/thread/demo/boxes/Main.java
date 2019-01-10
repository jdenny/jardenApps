package thread.demo.boxes;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Demo showing need for threads, synchronisation and cooperation.
 * @author john.denny@gmail.com
 */
public class Main {
	public static void main(String[] args) {
		JPanel panel = new BoxesJPanel();
		JFrame jFrame = new JFrame("Threads Demo");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.getContentPane().add(panel);
		jFrame.pack();
		jFrame.setVisible(true);
	}
}

