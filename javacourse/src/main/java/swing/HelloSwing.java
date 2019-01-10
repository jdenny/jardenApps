package swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Simple java application to create a graphical user interface (GUI).
 * The user types in a string, which is added to a display area
 * in the middle of the window.
 */
public class HelloSwing implements ActionListener {
	private JTextField textField;
	private JTextArea messageArea;

	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new HelloSwing();
            }
        });
	}
	public HelloSwing() {
		JFrame frame = new JFrame("HelloSwing");
		JLabel textLabel = new JLabel("Type And Go:");
		textField = new JTextField(20);
		messageArea = new JTextArea(10, 20);
		JButton goButton = new JButton("Go");
		JButton clearButton = new JButton("Clear");
		Container container = frame.getContentPane();
		JPanel controlPanel = new JPanel();
		container.add(controlPanel, BorderLayout.NORTH);
		container.add(messageArea, BorderLayout.CENTER);
		controlPanel.add(textLabel);
		controlPanel.add(textField);
		controlPanel.add(goButton);
		controlPanel.add(clearButton);
		goButton.addActionListener(this); // action is click on button...
		textField.addActionListener(this); // ... or press Enter
		clearButton.addActionListener(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true); // start event handling thread
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		if (action.equals("Clear")) {
			textField.setText("");
		} else {
			messageArea.append(textField.getText() + "\n");
		}
	}
}

