package jarden.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * Simple Swing version of a console.
 */
public class ConsoleSwing implements ActionListener, KeyListener {
	private JFrame frame;
	private JTextField inputField;
	private JTextArea messageArea;
	private String inputString;
	private ArrayList<String> inputList;
	private int inputListIndex = 0;
	
	public ConsoleSwing() {
		this("ConsoleSwing");
	}
	public ConsoleSwing(String title) {
		inputList = new ArrayList<>();
		frame = new JFrame(title);
		JLabel textLabel = new JLabel("Type here:");
		inputField = new JTextField(20);
		messageArea = new JTextArea(10, 20);
		
		JScrollPane scrollPane = new JScrollPane(messageArea); 
		messageArea.setEditable(false);
		
		JButton goButton = new JButton("Go");
		Container container = frame.getContentPane();
		JPanel controlPanel = new JPanel();
		container.add(controlPanel, BorderLayout.NORTH);
		container.add(scrollPane, BorderLayout.CENTER);
		controlPanel.add(textLabel);
		controlPanel.add(inputField);
		controlPanel.add(goButton);
		goButton.addActionListener(this);
		inputField.addActionListener(this);
		inputField.addKeyListener(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		/*
		 * Note, can't synchronize on String inputLine, because String, being
		 * immutable, could be shared by other code.
		 */
		this.inputString = inputField.getText();
		if (inputListIndex >= inputList.size() ||
				!this.inputString.equals(inputList.get(inputListIndex))) {
			inputList.add(inputString);
			inputListIndex = inputList.size();
		} else {
			++inputListIndex;
		}
		synchronized(inputField) {
			inputField.notify();
		}
	}

	@Override
	public void keyPressed(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (keyCode == KeyEvent.VK_UP) {
			if ((inputListIndex) <= 0 || inputList.size() == 0) return;
			inputField.setText(inputList.get(--inputListIndex));
		} else if (keyCode == KeyEvent.VK_DOWN) {
			if (inputListIndex >= inputList.size()) return;
			++inputListIndex;
			if (inputListIndex >= inputList.size()) {
				inputField.setText(""); // gone past list
			} else {
				inputField.setText(inputList.get(inputListIndex));
			}
		}
	}
	/**
	 * Display message, wait for the user to supply input
	 * and return it.
	 * @param message: used to prompt the user to supply input
	 * @return the user's input.
	 */
	public String getString(String message) {
		println(message);
		try {
			synchronized(inputField) {
				inputField.wait();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		inputField.setText("");
		return inputString;
	}

	/**
	 * Display message, wait for the user to supply input,
	 * convert it to an int and return the int.
	 * @param message: used to prompt the user to supply input
	 * @return the user's input, converted into an int.
	 * If the input is not an int, prompt the user to have another go.
	 */
	public int getInt(String message) {
		while (true) {
			println(message);
			try {
				synchronized(inputField) {
					inputField.wait();
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			try {
				int number = Integer.parseInt(inputString);
				inputField.setText("");
				return number;
			} catch(NumberFormatException e) {
				 println("invalid integer! Have another go");
			}
		}
	}

	/**
	 * Display message, wait for the user to supply input,
	 * convert it to an double and return the double.
	 * @param message: used to prompt the user to supply input
	 * @return the user's input, converted into a double. If the
	 * input is not a double, prompt the user to have another go.
	 */
	public double getDouble(String message) {
		while (true) {
			println(message);
			try {
				synchronized(inputField) {
					inputField.wait();
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			try {
				double number = Double.parseDouble(inputString);
				inputField.setText("");
				return number;
			} catch(NumberFormatException e) {
				 println("invalid double! Have another go");
			}
		}
	}
	/**
	 * Add message plus newline to messageArea.
	 * @param message
	 */
	public void println(String message) {
		messageArea.append(message + "\n");
	}
	public void println(Object object) {
		messageArea.append(object.toString() + "\n");
	}
	public void close() {
		frame.dispose();
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}
}
