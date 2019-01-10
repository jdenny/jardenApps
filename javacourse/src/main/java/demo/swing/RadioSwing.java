package demo.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * Simple java application to create a graphical user interface (GUI).
 * The user types in a string, which is added to a display area
 * in the middle of the window.
 */
public class RadioSwing implements ActionListener {
	private JTextField messageField;

	public static void main(String[] args) {
		new RadioSwing();
	}
	public RadioSwing() {
		JFrame frame = new JFrame("RadioSwing");
		JLabel textLabel = new JLabel("Choose a colour:");
		messageField = new JTextField(20);
		JRadioButton redButton = new JRadioButton("Red");
		JRadioButton greenButton = new JRadioButton("Green");
		JRadioButton blueButton = new JRadioButton("Blue");
		JRadioButton yellowButton = new JRadioButton("Yellow");
		Container container = frame.getContentPane();
		container.add(textLabel, BorderLayout.NORTH);
		ButtonGroup group = new ButtonGroup();
		group.add(redButton);
		group.add(greenButton);
		group.add(blueButton);
		group.add(yellowButton);
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.add(redButton);
		radioPanel.add(greenButton);
		radioPanel.add(blueButton);
		radioPanel.add(yellowButton);
		container.add(radioPanel, BorderLayout.CENTER);
		container.add(messageField, BorderLayout.SOUTH);
		redButton.addActionListener(this);
		greenButton.addActionListener(this);
		blueButton.addActionListener(this);
		yellowButton.addActionListener(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true); // start event handling thread
	}
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		messageField.setText("you have chosen colour " + action);
		if (action.equals("Red")) {
			messageField.setBackground(Color.red);
		} else if (action.equals("Green")) {
			messageField.setBackground(Color.green);
		} else if (action.equals("Blue")) {
			messageField.setBackground(Color.blue);
		} else if (action.equals("Yellow")) {
			messageField.setBackground(Color.yellow);
		}
	}
}

