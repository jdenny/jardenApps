package jarden.draw;

import jarden.gui.GridBag;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PropertyDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField xField, yField;
	private JTextField widthField, heightField;
	private JButton lineColourButton;
	private JButton fillColourButton;
	private JTextField textField;

	public PropertyDialog(ActionListener actionListener) {
		super(new JFrame(), "Properties", false);
		// components:
		xField = new JTextField(6);
		xField.setName("X");
		yField = new JTextField(6);
		yField.setName("Y");
		widthField = new JTextField(6);
		widthField.setName("Width");
		heightField = new JTextField(6);
		heightField.setName("Height");
		lineColourButton = new JButton("LineColour");
		fillColourButton = new JButton("FillColour");
		textField = new JTextField();
		textField.setName("Text");
		JButton firstButton = new JButton("<<");
		JButton nextButton = new JButton(">");
		JButton previousButton = new JButton("<");
		// layout:
		GridBag gridBag = new GridBag(this);
		gridBag.add(new JLabel("'Enter' after each change"), 0, 0, 2, 1);
		gridBag.add(new JLabel("X"), 0, 1, 1, 1);
		gridBag.add(xField, 1, 1);
		gridBag.add(new JLabel("Y"), 0, 2);
		gridBag.add(yField, 1, 2);
		gridBag.add(new JLabel("Width"), 0, 3);
		gridBag.add(widthField, 1, 3);
		gridBag.add(new JLabel("Height"), 0, 4);
		gridBag.add(heightField, 1, 4);
		gridBag.add(new JLabel("Line"), 0, 5);
		gridBag.add(lineColourButton, 1, 5);
		gridBag.add(new JLabel("Fill"), 0, 6);
		gridBag.add(fillColourButton, 1, 6);
		gridBag.add(new JLabel("Text:"), 0, 7);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(firstButton);
		buttonPanel.add(nextButton);
		buttonPanel.add(previousButton);
		gridBag.add(buttonPanel, 0, 9, 2, 1);
		gridBag.fill = GridBag.HORIZONTAL;
		gridBag.weightx = 1.0;
		gridBag.add(textField, 0, 8, 2, 1);
		// setSize(150, 260);
		pack();
		// add event listeners:
		xField.addActionListener(actionListener);
		yField.addActionListener(actionListener);
		widthField.addActionListener(actionListener);
		heightField.addActionListener(actionListener);
		lineColourButton.addActionListener(actionListener);
		fillColourButton.addActionListener(actionListener);
		textField.addActionListener(actionListener);
		firstButton.addActionListener(actionListener);
		nextButton.addActionListener(actionListener);
		previousButton.addActionListener(actionListener);
	}

	public void popup(MyShape shape) {
		xField.setText("" + shape.x);
		yField.setText("" + shape.y);
		widthField.setText("" + shape.width);
		heightField.setText("" + shape.height);
		textField.setText(shape.text);
		lineColourButton.setBackground(shape.lineColour);
		fillColourButton.setBackground(shape.fillColour);
		setVisible(true);
	}
	public void setLineColour(Color colour) {
		lineColourButton.setBackground(colour);
	}
	public void setFillColour(Color colour) {
		fillColourButton.setBackground(colour);
	}
}
