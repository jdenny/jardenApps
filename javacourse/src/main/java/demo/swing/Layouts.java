package demo.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;


/**
 * Example class to show the behaviour of the different layout managers.
 * @author John Denny, Oct 1998. Converted to Swing Sept 2012.
 */
public class Layouts implements ActionListener {
	private JFrame frame;
	private JButton flowButton, flowRightButton;
	private JButton borderButton, gridButton;
	private JButton gridBagButton;
	private FlowLayout flowLayout, flowRightLayout;
	private BorderLayout borderLayout;
	private GridLayout gridLayout;
	private GridBagLayout gridBagLayout;
	private GridBagConstraints constraints;

	public Layouts() {
		// create components
		frame = new JFrame("Layout Managers Demo");
		flowButton = new JButton("Flow Layout");
		flowRightButton = new JButton("Flow Right Layout");
		borderButton = new JButton("Border Layout");
		gridButton = new JButton("Grid Layout");
		gridBagButton = new JButton("Grid Bag Layout");
		Font font = gridButton.getFont();
		Font font2 = font.deriveFont(Font.BOLD, font.getSize() * 2);
		gridBagButton.setFont(font2);
		// position components
		Container container = frame.getContentPane();
		flowLayout = new FlowLayout();
		flowRightLayout = new FlowLayout(FlowLayout.RIGHT, 10, 10);
		borderLayout = new BorderLayout();
		gridLayout = new GridLayout(3, 2);
		setGridBag();
		frame.setLayout(borderLayout);
		container.add(flowButton, "North");
		container.add(flowRightButton, "South");
		container.add(borderButton, "Center");
		container.add(gridButton, "West");
		container.add(gridBagButton, "East");
		Dimension dim = frame.getToolkit().getScreenSize();
		frame.setBounds(dim.width / 4,
			dim.height / 4,
			dim.width / 2,
			dim.height / 2);
		// add event handling
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		flowButton.addActionListener(this);
		flowRightButton.addActionListener(this);
		borderButton.addActionListener(this);
		gridButton.addActionListener(this);
		gridBagButton.addActionListener(this);
		frame.setVisible(true);
	}
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == flowButton) {
			frame.setLayout(flowLayout);
			frame.validate();
		}
		else if (event.getSource() == flowRightButton) {
			frame.setLayout(flowRightLayout);
			frame.repaint();
			frame.validate();
		}
		else if (event.getSource() == borderButton) {
			frame.setLayout(borderLayout);
			frame.validate();
		}
		else if (event.getSource() == gridButton) {
			frame.setLayout(gridLayout);
			frame.validate();
		}
		else if (event.getSource() == gridBagButton) {
			frame.setLayout(gridBagLayout);
			frame.validate();
		}
	}
	public static void main(String[] args) {
		new Layouts();
		// TODO: when the panic is over, import CardDialog
		// and convert it to Swing
		// new CardDialog(layouts).setVisible(true);
	}
	public void setGridBag() {
		gridBagLayout = new GridBagLayout();
		constraints = new GridBagConstraints();
		constraints.insets = new Insets(1, 2, 2, 2);
		constraints.gridx = 0;
		constraints.gridy = 0;
		gridBagLayout.setConstraints(flowButton, constraints);
		constraints.gridwidth = 2;
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagLayout.setConstraints(flowRightButton, constraints);
		constraints.gridwidth = 1;
		constraints.gridy = 2;
		constraints.fill = GridBagConstraints.NONE;
		gridBagLayout.setConstraints(borderButton, constraints);
		constraints.gridy = 3;
		gridBagLayout.setConstraints(gridButton, constraints);
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridheight = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		gridBagLayout.setConstraints(gridBagButton, constraints);
	}
}