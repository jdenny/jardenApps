package jarden.draw;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * User interface to Shapes classes. Enables users dynamically to draw graphical
 * shapes on the screen.
 */
public class DrawControlPanel extends JPanel implements ItemListener, ActionListener {
	private static final long serialVersionUID = 1L;
	private JCheckBox animateCheck;
	private JButton lineColourButton;
	private JButton fillColourButton;
	private JComboBox<String> actionChoice;
	private JComboBox<String> shapeChoice;
	private DrawingCanvas drawingCanvas;

	public DrawControlPanel() {
		actionChoice = new JComboBox<String>(DrawingCanvas.actions);
		shapeChoice = new JComboBox<String>(DrawingCanvas.shapeNames);
		lineColourButton = new JButton("ChangeLine");
		lineColourButton.setBackground(DrawingCanvas.DEFAULT_LINE_COLOUR);
		fillColourButton = new JButton("ChangeFill");
		fillColourButton.setBackground(DrawingCanvas.DEFAULT_FILL_COLOUR);
		add(actionChoice);
		add(shapeChoice);
		add(new JLabel("LineColour:", JLabel.RIGHT));
		add(lineColourButton);
		add(new JLabel("FillColour:", JLabel.RIGHT));
		add(fillColourButton);
		add(animateCheck = new JCheckBox("Animate"));
		// add event listeners:
		shapeChoice.addItemListener(this);
		lineColourButton.addActionListener(this);
		fillColourButton.addActionListener(this);
		animateCheck.addItemListener(this);
		actionChoice.addItemListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("ChangeLine")) {
			Color lineColour = JColorChooser.showDialog(this, "Choose Line Color",
					Color.black); // TODO: fix this!
		    if (lineColour != null) {
		    	drawingCanvas.setLineColour(lineColour);
		    	lineColourButton.setBackground(lineColour);
		    }
		} else if (event.getActionCommand().equals("ChangeFill")) {
			Color fillColour = JColorChooser.showDialog(this, "Choose Fill Color",
					Color.yellow); // TODO: fix this!
		    if (fillColour != null) {
		    	drawingCanvas.setFillColour(fillColour);
		    	fillColourButton.setBackground(fillColour);
		    }
		}
	}
	@Override
	public void itemStateChanged(ItemEvent event) {
		Object source = event.getSource();
		if (source == actionChoice) {
			drawingCanvas.setAction((String) actionChoice.getSelectedItem());
		} else if (source == shapeChoice) {
			drawingCanvas.setShapeName((String) shapeChoice.getSelectedItem());
		} else if (source == animateCheck) {
			drawingCanvas.setAnimated(animateCheck.isSelected());
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("JDrawControlPanel");
		Container pane = frame.getContentPane();
		pane.add(new DrawControlPanel(), "Center");
		frame.setSize(500, 400);
		frame.setVisible(true);
	}

	public void setShapeCanvas(DrawingCanvas drawingCanvas) {
		this.drawingCanvas = drawingCanvas;
	}

}
