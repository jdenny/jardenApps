package thread.jdk5.demo;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Graphical User Interface, using Swing, for ProblemCache.
 */
public class ProblemSwing implements ActionListener, ProblemCache.ResultListener {
	private JTextField statusTextField;
	private JComboBox<String> fileNameComboBox;
	private JTextArea textArea;
	private ProblemCache problemCache;

	public static void main(String[] args) {
		new ProblemSwing();
	}
	public ProblemSwing() {
		this.problemCache = new ProblemCache(this);
		// create components:
		JFrame frame = new JFrame("ProblemSwing");
		statusTextField = new JTextField();
		textArea = new JTextArea(10, 20);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		fileNameComboBox = new JComboBox<String>();
		problemCache.getFileList();
		// set layout of components:
		Container container = frame.getContentPane();
		JPanel controlPanel = new JPanel();
		container.add(controlPanel, BorderLayout.NORTH);
		container.add(areaScrollPane, BorderLayout.CENTER);
		container.add(statusTextField, BorderLayout.SOUTH);
		controlPanel.add(new JLabel("file names:"));
		controlPanel.add(fileNameComboBox);
		// handle component events:
		fileNameComboBox.addActionListener(this);
		Dimension dim = frame.getToolkit().getScreenSize();
		frame.setBounds(dim.width / 4,
			dim.height / 4,
			dim.width / 2,
			dim.height / 2);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true); // start event handling thread
	}
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == this.fileNameComboBox) {
			String fileName = (String) fileNameComboBox.getSelectedItem();
			this.problemCache.getFile(fileName);
		} else {
			throw new IllegalStateException("unrecognised event: " + event);
		}
	}
	@Override
	public void onFileNameList(List<String> fileNames) {
		this.fileNameComboBox.removeAll();
		for (String fileName: fileNames) {
			fileNameComboBox.addItem(fileName);	
		}
	}
	@Override
	public void onFileRetrieved(String fileContent) {
		this.textArea.setText(fileContent);
	}
}

