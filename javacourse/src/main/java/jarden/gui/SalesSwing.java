package jarden.gui;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EmptyStackException;
import java.util.Stack;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SalesSwing implements ActionListener, ListSelectionListener {
	private final static String IMAGE_FOLDER = "docs"; 
	// GUI components:
	private DefaultListModel<String> listModel;
	private JList<String> unitList;
	private JButton upButt, downButt;
	private ImageCanvas imageCanvas;
	private JTextField teamName;
	private JTextField ownerTeamName;
	private JTextField teamSales;
	private JTextField unitName;
	private JTextField unitSales;
	private JLabel unitTypeLabel;
	private Toolkit toolKit;
	private JTextField statusField;
	// end of GUI components
	private SalesTeamIF currentTeam;
	private Image currentImage;
	private Stack<SalesTeamIF> ownerStack;

	public SalesSwing(SalesTeamIF salesTeam) {
		ownerStack = new Stack<SalesTeamIF>();
		currentTeam = salesTeam;
		System.out.println("currentTeam = " + currentTeam);
		System.out.println("total sales: " + currentTeam.getSales());
		toolKit = Toolkit.getDefaultToolkit();

		// Create components:
		JFrame frame = new JFrame("SalesSwing");
		teamName = new JTextField(15);
		ownerTeamName = new JTextField(15);
		ownerTeamName.setEditable(false);
		teamSales = new JTextField(15);
		teamSales.setEditable(false);
		listModel = new DefaultListModel<String>();
		unitList = new JList<String>(listModel);
		unitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		showTeam();
		upButt = new JButton("Up");
		downButt = new JButton("Down");
		// unit details could be for SalesTeam or SalesPerson
		unitTypeLabel = new JLabel("Sales person:");
		unitName = new JTextField(15);
		unitName.setEditable(false);
		unitSales = new JTextField(15);
		unitSales.setEditable(false);
		imageCanvas = new ImageCanvas();
		imageCanvas.setSize(150, 200);
		statusField = new JTextField(20);

		// Set layout of components:
		GridBag gridBag = new GridBag(frame);
		gridBag.add(new JLabel("Team:"), 0, 0);
		gridBag.add(new JLabel("Owning Team:"), 0, 1);
		gridBag.add(new JLabel("Total Sales:"), 0, 2);
		gridBag.add(teamName, 1, 0);
		gridBag.add(ownerTeamName, 1, 1);
		gridBag.add(teamSales, 1, 2);
		gridBag.fill = GridBag.HORIZONTAL;
		gridBag.add(unitList, 0, 3, 2, 2);
		gridBag.add(statusField, 0, 8, 2, 1);
		gridBag.add(upButt, 2, 3, 1, 1);
		gridBag.add(downButt, 2, 4);
		gridBag.fill = GridBag.NONE;
		gridBag.add(unitTypeLabel, 0, 5);
		gridBag.add(new JLabel("Sales:"), 0, 6);
		gridBag.add(unitName, 1, 5);
		gridBag.add(unitSales, 1, 6);
		gridBag.add(imageCanvas, 0, 7, 2, 1);

		// add event listeners:
		unitList.addListSelectionListener(this);
		upButt.addActionListener(this);
		downButt.addActionListener(this);

		// set frame visible:
		frame.setSize(350, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		Object target = event.getSource();
		statusField.setText("");
		if (target == upButt) {
			try {
				SalesTeamIF owner = ownerStack.pop();
				currentTeam = owner;
				showTeam();
			}
			catch(EmptyStackException ex) {
			  statusField.setText("No owner!");
			}
		}
		else if (target == downButt) {
			int unitIndex = unitList.getSelectedIndex();
			if (unitIndex >= 0) {
				SalesUnitIF unit = currentTeam.getMember(unitIndex);
				if (unit instanceof SalesTeamIF) {
					ownerStack.push(currentTeam);
					currentTeam = (SalesTeamIF)unit;
					showTeam();
				}
				else {
					statusField.setText("selection not a team!");
				}
			}
			else {
				statusField.setText("no unit selected!");
			}
		}
	}
	@Override
	public void valueChanged(ListSelectionEvent event) {
		statusField.setText("");
		if (event.getSource() == unitList && !event.getValueIsAdjusting()) {
			int unitIndex = unitList.getSelectedIndex();
			if (unitIndex >= 0) {
				SalesUnitIF unit = currentTeam.getMember(unitIndex);
				showUnit(unit);
			}
			else {
				statusField.setText("No Sales Unit selected!");
			}
		}
	}
	private void showTeam() {
		System.out.println("showTeam: " + currentTeam.getName());
		teamName.setText(currentTeam.getName());
		try {
			SalesTeamIF owner = (SalesTeamIF)(ownerStack.peek());
			ownerTeamName.setText(owner.getName());
		}
		catch(EmptyStackException ex) {
			ownerTeamName.setText("None");
		}
		teamSales.setText("" + currentTeam.getSales());
		listModel.clear();
		for (int i = 0; i < currentTeam.getMemberCt(); i++) {
			listModel.addElement(currentTeam.getMember(i).getName());
		}
	}
	private void showUnit(SalesUnitIF unit) {
	    System.out.println("name=" + unit.getName()
	        + "; sales=" + unit.getSales());
		unitName.setText(unit.getName());
		unitSales.setText("" + unit.getSales());
	    if (unit instanceof SalesPersonIF) {
	        unitTypeLabel.setText("Sales person:");
	        SalesPersonIF salesbod = (SalesPersonIF)unit;
	        String photoFileName = SalesSwing.IMAGE_FOLDER + "/" +
					salesbod.getPhotoFile();
	        Path path = Paths.get(photoFileName);
	        System.out.println("photo file is: " + path.toAbsolutePath());
			currentImage = toolKit.createImage(photoFileName);
			if (currentImage == null) {
				System.out.println("faceImage null for "
					+ photoFileName);
			}
			else {
				imageCanvas.setImage(currentImage);
			}
	    }
	    else {
	        unitTypeLabel.setText("Sales team:");
	        currentImage = null;
	    }
	}
}

class ImageCanvas extends Canvas {
	private static final long serialVersionUID = 1L;
	private Image image = null;

	public void paint(Graphics g) {
		if (image != null) {
			int h = image.getHeight(this);
			int w = image.getWidth(this);
			if (h > 0 && w > 0) {
				setSize(w, h);
				g.drawImage(image, 0, 0, this);
			}
		}
	}
	public void setImage(Image image) {
		this.image = image;
		repaint();
	}
	public void setImage(String fileName) {
		this.image = getToolkit().getImage(fileName);
		repaint();
	}
}