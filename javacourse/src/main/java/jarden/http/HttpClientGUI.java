package jarden.http;

import jarden.gui.GridBag;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class HttpClientGUI implements ActionListener {
	private final static String defaultUrl =
		"https://sites.google.com/site/amazequiz/home/problems/";
	private final static String defaultFileName =
		"C:/Users/John/java/problems/elHogar.txt";
	private JTextField urlField;
	private JTextField fileNameField;
	private JTextField statusField;
	private JTextArea outputArea;
	private JButton getButton;
	private JButton postButton;

	public HttpClientGUI() {
		JFrame frame = new JFrame("John's simple HttpClient");
		urlField = new JTextField(defaultUrl);
		getButton = new JButton("Get");
		postButton = new JButton("Post");
		fileNameField = new JTextField(defaultFileName);
		statusField = new JTextField();
		outputArea = new JTextArea();
		outputArea.setLineWrap(true);
		outputArea.setWrapStyleWord(true);
		JScrollPane areaScrollPane = new JScrollPane(outputArea);
		GridBag gridBag = new GridBag(frame);
		gridBag.add(new JLabel("URL"), 0, 0);
		gridBag.add(new JLabel("File name"), 0, 1);
		gridBag.add(getButton, 2, 0);
		gridBag.add(postButton, 2, 1);
		gridBag.fill = GridBag.HORIZONTAL;
		gridBag.weightx = 1.0;
		gridBag.add(urlField, 1, 0);
		gridBag.add(fileNameField, 1, 1);
		gridBag.add(statusField, 0, 3, 3, 1);
		gridBag.fill = GridBag.BOTH;
		gridBag.weighty = 1.0;
		gridBag.add(areaScrollPane, 0, 2, 3, 1);
		urlField.addActionListener(this);
		getButton.addActionListener(this);
		postButton.addActionListener(this);
		// frame.setSize(600, 600);
		Dimension dim = frame.getToolkit().getScreenSize();
		frame.setBounds(dim.width / 4,
			dim.height / 4,
			dim.width / 2,
			dim.height / 2);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true); // start event handling thread
	}
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		statusField.setText("");
		outputArea.setText("");
		try {
			if (source == urlField || source == getButton) {
				String page = MyHttpClient.getPage(urlField.getText());
				this.outputArea.setText(page);
			} else if (source == postButton) {
				String page = MyHttpClient.post(urlField.getText(),
						fileNameField.getText());
				this.outputArea.setText(page);
			} else {
				System.out.println("unexpected event: " + event);
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
			statusField.setText(ioe.toString());
		}
	}
	public static void main(String args[]) {
		new HttpClientGUI();
	}
}
