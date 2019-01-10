package demo.swing;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Simple demo to show phase 1 of a Swing application:
 * create components. Although the components do have
 * a layout of sorts, it is very poor, and there is 
 * no event handling. Notice that although you can
 * close the frame, the application is still running!
 */
public class SwingNoEvents {
	private JTextField textField;
	private JTextArea messageArea;

	public static void main(String[] args) {
		new SwingNoEvents();
	}
	public SwingNoEvents() {
		JFrame frame = new JFrame("HelloSwing");
		JLabel textLabel = new JLabel("Type And Go:");
		textField = new JTextField(20);
		messageArea = new JTextArea(10, 20);
		JButton goButton = new JButton("Go");
		JPanel controlPanel = new JPanel();
		Container container = frame.getContentPane();
		container.add(controlPanel, BorderLayout.CENTER);
		controlPanel.add(textLabel);
		controlPanel.add(textField);
		controlPanel.add(goButton);
		controlPanel.add(messageArea);
		frame.pack();
		frame.setVisible(true);
	}
}
