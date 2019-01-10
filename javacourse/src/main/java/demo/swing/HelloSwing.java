package demo.swing;

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

/**
 * Simple java application to create a graphical user interface (GUI).
 * The user types in a string, which is added to a display area
 * in the middle of the window.
 */
public class HelloSwing implements ActionListener {
	private JTextField textField;
	private JTextArea messageArea;

	public static void main(String[] args) {
		new HelloSwing();
	}
	public HelloSwing() {
		// create components:
		JFrame frame = new JFrame("HelloSwing");
		JLabel textLabel = new JLabel("Type And Go:");
		textField = new JTextField(20);
		messageArea = new JTextArea(10, 20);
		JButton goButton = new JButton("Go");
		// set layout of components:
		Container container = frame.getContentPane();
		JPanel controlPanel = new JPanel();
		container.add(controlPanel, BorderLayout.NORTH);
		container.add(messageArea, BorderLayout.CENTER);
		controlPanel.add(textLabel);
		controlPanel.add(textField);
		controlPanel.add(goButton);
		// handle component events:
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				messageArea.append(textField.getText() + " [return]\n");
			}
		});
		goButton.addActionListener(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true); // start event handling thread
	}
	public void actionPerformed(ActionEvent event) {
		messageArea.append(textField.getText() + " [button]\n");
	}
}

