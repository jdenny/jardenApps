package thread.demo.boxes;

import jarden.gui.GridBag;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 * Sample application to show the use of threads, synchronization
 * and wait/notify.
 * @author john.denny@gmail.com, October 1998.
 * @see thread.demo.BoxCanvas.
 * @see thread.demo.StackJPanel.
 * @see thread.demo.FillBoxThread.
 * @see thread.demo.EmptyBoxThread.
 */
public class BoxesJPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JButton fillRedButton, fillGreenButton, fillBlueButton;
	private JButton emptyButton1, emptyButton2, emptyButton3;
	private StackJPanel stack;
	private BoxCanvas inRedBox, inGreenBox, inBlueBox;
	private BoxCanvas outBox1, outBox2, outBox3;
	private JComboBox<String> technoChoice;

	public BoxesJPanel() {
		// create components:
		String[] items = {
				"No Threads",
				"Threads",
				"Threads & Sync.",
				"Threads, Sync & Wait"
		};
		technoChoice = new JComboBox<String>(items);
		fillRedButton = new JButton("Fill");
		fillGreenButton = new JButton("Fill");
		fillBlueButton = new JButton("Fill");
		inRedBox = new BoxCanvas(Color.red);
		inGreenBox = new BoxCanvas(Color.green);
		inBlueBox = new BoxCanvas(Color.blue);
		emptyButton1 = new JButton("Empty");
		emptyButton2 = new JButton("Empty");
		emptyButton3 = new JButton("Empty");
		outBox1 = new BoxCanvas();
		outBox2 = new BoxCanvas();
		outBox3 = new BoxCanvas();
		stack = new StackJPanel(this);
		// set layout of components:
		GridBag gridBag = new GridBag(this);
		gridBag.anchor = GridBag.SOUTH;
		gridBag.add(technoChoice, 0, 3, 2, 1);
		gridBag.add(inRedBox, 0, 1, 1, 1);
		gridBag.add(fillRedButton, 0, 2);
		gridBag.add(inGreenBox, 1, 1);
		gridBag.add(fillGreenButton, 1, 2);
		gridBag.add(inBlueBox, 2, 1);
		gridBag.add(fillBlueButton, 2, 2);
		gridBag.add(outBox1, 4, 1);
		gridBag.add(emptyButton1, 4, 2);
		gridBag.add(outBox2, 5, 1);
		gridBag.add(emptyButton2, 5, 2);
		gridBag.add(outBox3, 6, 1);
		gridBag.add(emptyButton3, 6, 2);
		gridBag.add(stack, 3, 0, 1, 2);
		// specify event listeners:
		fillRedButton.addActionListener(this);
		fillGreenButton.addActionListener(this);
		fillBlueButton.addActionListener(this);
		emptyButton1.addActionListener(this);
		emptyButton2.addActionListener(this);
		emptyButton3.addActionListener(this);
	}
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == fillRedButton) {
			new FillBoxThread(this, inRedBox);
		}
		else if (event.getSource() == fillGreenButton) {
			new FillBoxThread(this, inGreenBox);
		}
		else if (event.getSource() == fillBlueButton) {
			new FillBoxThread(this, inBlueBox);
		}
		else if (event.getSource() == emptyButton1) {
			new EmptyBoxThread(this, stack, outBox1);
		}
		else if (event.getSource() == emptyButton2) {
			new EmptyBoxThread(this, stack, outBox2);
		}
		else if (event.getSource() == emptyButton3) {
			new EmptyBoxThread(this, stack, outBox3);
		}
	}
	public void doneIt(BoxCanvas inBox) {
		if (stack.stackBox(inBox)) {
			inBox.empty();
		}
	}
	public boolean useSynch() {
		return technoChoice.getSelectedIndex() > 1;
	}
	public boolean useThreads() {
		return technoChoice.getSelectedIndex() > 0;
	}
	public boolean useWait() {
		return technoChoice.getSelectedIndex() > 2;
	}
}