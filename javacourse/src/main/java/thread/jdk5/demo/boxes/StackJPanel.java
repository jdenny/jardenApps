package thread.jdk5.demo.boxes;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import jarden.gui.GridBag;

import javax.swing.JPanel;
import javax.swing.JTextField;

import thread.demo.boxes.BoxCanvas;

/**
 * A visual representation of a stack of BoxCanvas objects.
 * @see thread.jdk5.demo.boxes.Main
 */
public class StackJPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int S_MAX = 5;
	private ArrayBlockingQueue<Color> boxes; // the 'model'
	private BoxCanvas[] boxCanvases; // the 'view'
	private JTextField boxCtField;

	public StackJPanel() {
		this.boxes = new ArrayBlockingQueue<>(S_MAX); // capacity
		this.boxCanvases = new BoxCanvas[S_MAX];
		boxCtField = new JTextField();
		GridBag gridBag = new GridBag(this);
		for (int i = 0; i < S_MAX; i++) {
			boxCanvases[i] = new BoxCanvas();
			gridBag.add(boxCanvases[i], 0, S_MAX - 1 - i);
		}
		gridBag.fill = GridBag.HORIZONTAL;
		gridBag.add(boxCtField, 0, S_MAX);
	}
	@Override
	/**
	 * Ensure view (boxCanvases) is a valid representation of
	 * the model (boxes).
	 */
	public void paint(Graphics g) {
		int i = 0;
		Iterator<Color> iter = this.boxes.iterator();
		while (iter.hasNext()) {
			boxCanvases[i].setColour(iter.next());
			boxCanvases[i].fill();
			i++;
		}
		for (; i < boxCanvases.length; i++) {
			boxCanvases[i].empty();
		}
		super.paint(g);
		boxCtField.setText(Integer.toString(boxes.size()));
	}
	public boolean stackBox(BoxCanvas boxCanvas) {
		Color colour = boxCanvas.getColour();
		if (!boxes.offer(colour)) {
			System.out.println("StackPanel.stackBox() about to wait");
			try {
				boxes.put(colour);
				System.out.println("StackPanel.stackBox() done waiting");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		repaint();
		return true;
	}
	public Color unstackBox() {
		Color colour;
		if ((colour = boxes.poll()) == null) {
			System.out.println("StackPanel.unstackBox() about to wait");
			try {
				colour = boxes.take();
				System.out.println("StackPanel.unstackBox() done waiting");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		repaint();
		return colour;
	}
}