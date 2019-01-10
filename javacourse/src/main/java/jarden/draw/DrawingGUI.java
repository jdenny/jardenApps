package jarden.draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DrawingGUI implements ActionListener {
	private final static String aboutMe =
			"john.denny@gmail.com\nEnterprise Java Trainer";
	private JFrame frame;
	private JFileChooser fileDialog = null;
	private File saveFile = null;
	private DrawingCanvas drawingCanvas;

	public DrawingGUI() {
		frame = new JFrame("John's Shape Drawer");
		
		Dimension dim = frame.getToolkit().getScreenSize();
		frame.setBounds(dim.width / 4,
			dim.height / 4,
			dim.width / 2,
			dim.height / 2);
		drawingCanvas = new DrawingCanvas();
		DrawControlPanel controlPanel = new DrawControlPanel();
		controlPanel.setShapeCanvas(drawingCanvas);
		frame.add(drawingCanvas, "Center");
		frame.add(controlPanel, "South");
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(fileMenu);
		// menuBar.setHelpMenu(helpMenu); // not yet implemented in JVM!
		menuBar.add(helpMenu);
		frame.setJMenuBar(menuBar);
		JMenuItem newItem = new JMenuItem("New");
		JMenuItem openItem = new JMenuItem("Open");
		JMenuItem saveItem = new JMenuItem("Save");
		JMenuItem saveAsItem = new JMenuItem("Save As");
		JMenuItem printItem = new JMenuItem("Print");
		JMenuItem exitItem = new JMenuItem("Exit");
		JMenuItem aboutItem = new JMenuItem("About");
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.add(printItem);
		fileMenu.add(exitItem);
		helpMenu.add(aboutItem);
		newItem.addActionListener(this);
		openItem.addActionListener(this);
		saveItem.addActionListener(this);
		saveAsItem.addActionListener(this);
		printItem.addActionListener(this);
		exitItem.addActionListener(this);
		aboutItem.addActionListener(this);
		// frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if (command.equals("New")) {
			newFile();
		} else if (command.equals("Open")) {
			openFile();
		} else if (command.equals("Save")) {
			if (saveFile == null)
				saveAsFile();
			else
				saveFile();
		} else if (command.equals("Save As")) {
			saveAsFile();
		} else if (command.equals("Print")) {
			Toolkit toolkit = frame.getToolkit();
			PrintJob job = toolkit.getPrintJob(frame, "JohnPrintJob", null);
			if (job == null)
				return; // Cancel on print dialog
			Graphics g = job.getGraphics();
			g.setFont(new Font("Serif", Font.ITALIC, 14));
			g.setColor(Color.black);
			// by default, print() calls paint()
			drawingCanvas.print(g);
			g.dispose();
			job.end();
		} else if (command.equals("Exit")) {
			System.exit(0);
		} else if (command.equals("About")) {
			JOptionPane.showMessageDialog(frame, aboutMe, "Authored by",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public static void main(String[] args) {
		new DrawingGUI();
	}

	public void newFile() {
		ShapeSet shapeSet = drawingCanvas.getShapeSet();
		if (shapeSet.isChanged()) {
			int res = JOptionPane.showConfirmDialog(frame,
					"Do you want to save changes first?");
			if (res == JOptionPane.YES_OPTION) {
				saveFile();
			} else if (res == JOptionPane.CANCEL_OPTION)
				return;
		}
		shapeSet.clear();
		drawingCanvas.repaint();
		saveFile = null;
	}

	public void openFile() {
		ShapeSet shapeSet = drawingCanvas.getShapeSet();
		if (shapeSet.isChanged()) {
			int res = JOptionPane.showConfirmDialog(frame,
					"Do you want to save changes first?");
			if (res == JOptionPane.YES_OPTION) {
				saveFile();
			} else if (res == JOptionPane.CANCEL_OPTION)
				return;
		}
		initialiseFileChooser();
		int returnVal = fileDialog.showOpenDialog(frame);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File selectedFile = fileDialog.getSelectedFile();
		String path = selectedFile.getAbsolutePath();
		frame.setTitle(path);
		try {
			shapeSet.open(selectedFile);
			drawingCanvas.repaint();
		} catch (Exception ex) {
			System.out.println("Exception in openFile" + ex);
		}
	}
	
	private void initialiseFileChooser() {
		if (fileDialog == null) {
			File myDir = new File("/Users/John/java/jarden/draw/data");
			fileDialog = new JFileChooser(myDir);
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"John Draw Data (*.jdd)", "jdd");
			fileDialog.setFileFilter(filter);
		}

	}

	public void saveAsFile() {
		initialiseFileChooser();
		int returnVal = fileDialog.showSaveDialog(frame);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		saveFile = fileDialog.getSelectedFile();
		frame.setTitle(saveFile.getAbsolutePath());
		saveFile();
	}

	public void saveFile() {
		try {
			ShapeSet shapeSet = drawingCanvas.getShapeSet();
			shapeSet.save(saveFile);
		} catch (IOException ex) {
			System.out.println("Exception in saveFile" + ex);
		}
	}
}
