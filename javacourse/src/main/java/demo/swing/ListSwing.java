package demo.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 * Simple java application to illustrate the use of JList.
 * Based on HelloSwing, but with a JList instead of a JTextField
 * The user types in a string, which is added to a display area
 * in the middle of the window.
 */
public class ListSwing implements ActionListener, Serializable {
	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private DefaultListModel<String> listModel;
	private JList<String> messageList;

	public static void main(String[] args) {
		new ListSwing();
	}
	public ListSwing() {
		// create components:
		JFrame frame = new JFrame("ListSwing");
		JLabel textLabel = new JLabel("Type And Go:");
		textField = new JTextField(20);
		listModel = new DefaultListModel<String>();
		messageList = new JList<String>(listModel);
		messageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JButton goButton = new JButton("Go");
		// set layout of components:
		Container container = frame.getContentPane();
		JPanel controlPanel = new JPanel();
		container.add(controlPanel, BorderLayout.NORTH);
		container.add(messageList, BorderLayout.CENTER);
		controlPanel.add(textLabel);
		controlPanel.add(textField);
		controlPanel.add(goButton);
		// handle component events:
		goButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listModel.addElement(textField.getText() + " [button]");
			}
		});
		textField.addActionListener(this); // ... or press Enter
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true); // start event handling thread
	}
	public void actionPerformed(ActionEvent event) {
		listModel.addElement(textField.getText());
	}
}

