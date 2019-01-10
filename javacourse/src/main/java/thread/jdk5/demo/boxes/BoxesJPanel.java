package thread.jdk5.demo.boxes;

import jarden.gui.GridBag;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JPanel;

import thread.demo.boxes.BoxCanvas;

class BoxesJPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private ExecutorService executor;
	private JButton fillRedButton, fillGreenButton, fillBlueButton;
	private JButton emptyButton1, emptyButton2, emptyButton3;
	private StackJPanel stack;
	private BoxCanvas inRedBox, inGreenBox, inBlueBox;
	private BoxCanvas outBox1, outBox2, outBox3;

	public BoxesJPanel() {
		this.executor = Executors.newCachedThreadPool();
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
		stack = new StackJPanel();
		// set layout of components:
		GridBag gridBag = new GridBag(this);
		gridBag.anchor = GridBag.SOUTH;
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
			this.executor.execute(new FillBoxRunnable(this, inRedBox));
		}
		else if (event.getSource() == fillGreenButton) {
			this.executor.execute(new FillBoxRunnable(this, inGreenBox));
		}
		else if (event.getSource() == fillBlueButton) {
			this.executor.execute(new FillBoxRunnable(this, inBlueBox));
		}
		else if (event.getSource() == emptyButton1) {
			this.executor.execute(new EmptyBoxRunnable(stack, outBox1));
		}
		else if (event.getSource() == emptyButton2) {
			this.executor.execute(new EmptyBoxRunnable(stack, outBox2));
		}
		else if (event.getSource() == emptyButton3) {
			this.executor.execute(new EmptyBoxRunnable(stack, outBox3));
		}
	}
	public void doneIt(BoxCanvas inBox) {
		if (stack.stackBox(inBox)) {
			inBox.empty();
		}
	}
}