package thread.demo.boxes;

import jarden.gui.GridBag;

import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A visual representation of a stack of BoxCanvas objects.
 */
public class StackJPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int S_MAX = 5;
	private BoxCanvas[] boxes;
	private int boxCt;
	private JTextField boxCtField;
	private BoxesJPanel client;

	public StackJPanel(BoxesJPanel client) {
		this.client = client;
		boxes = new BoxCanvas[S_MAX];
		boxCt = 0;
		boxCtField = new JTextField();
		GridBag gridBag = new GridBag(this);
		for (int i = 0; i < S_MAX; i++) {
			boxes[i] = new BoxCanvas();
			gridBag.add(boxes[i], 0, S_MAX - 1 - i);
		}
		gridBag.fill = GridBag.HORIZONTAL;
		gridBag.add(boxCtField, 0, S_MAX);
	}
	public synchronized boolean stackBox(BoxCanvas box) {
		if (client.useWait()) {
			while (boxCt >= S_MAX) {
				try {
					System.out.println("StackPanel.stackBox() about to wait");
					wait();
				}
				catch (InterruptedException ex) {}
				System.out.println("StackPanel.stackBox() done waiting");
			}
		}
		if (boxCt < S_MAX) { // should always be true if method is synchronized
			BoxCanvas currentBox = boxes[boxCt++];
			currentBox.setColour(box.getColour());
			boxCtField.setText("" + boxCt);
			currentBox.fill();
			if (client.useWait()) {
				notify();
			}
			return true;
		}
		else {
			System.out.println("stack full!");
			return false;
		}
	}
	public BoxCanvas unstackBox() {
		if (client.useWait()) {
			while (boxCt < 1) {
				try {
					System.out.println("StackPanel.unstackBox() about to wait");
					wait();
				}
				catch (InterruptedException ex) {}
				System.out.println("StackPanel.unstackBox() done waiting");
			}
		}
		if (boxCt > 0) { // would always be true if method is synchronized
			int oldBoxCt = boxCt;
			// sleep, to allow something to go wrong at the critical point,
			// where the state is inconsistent!
			System.out.println("with you in 2 seconds guv");
			try { Thread.sleep(2000); }
			catch(InterruptedException ex) {}
			BoxCanvas currentBox = boxes[boxCt - 1];
			boxCt = oldBoxCt - 1;
			boxCtField.setText("" + boxCt);
			currentBox.empty();
			return currentBox;
		}
		else {
			System.out.println("stack empty!");
			return null;
		}
	}
}