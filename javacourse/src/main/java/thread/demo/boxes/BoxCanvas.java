package thread.demo.boxes;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

/**
 * A canvas to visually represent a container, holding a number of units;
 * for example a warehouse pallet holding a number of cartons of corn
 * flakes.
 */
public class BoxCanvas extends Canvas {
	private static final long serialVersionUID = 1L;
	public static final int BOX_WIDTH = 100;
	public static final int BOX_HEIGHT = 60;
	public static final int BOX_ROWS = 3;
	public static final int BOX_COLS = 4;
	private static final int UNIT_W = BOX_WIDTH / BOX_COLS;
	private static final int UNIT_H = BOX_HEIGHT / BOX_ROWS;
	private Color colour;
	private int unitCt;

	public BoxCanvas() {
		this(Color.lightGray);
	}
	public BoxCanvas(Color colour) {
		this.colour = colour;
		this.unitCt = 0;
		setSize(BOX_WIDTH, BOX_HEIGHT);
	}
	public void addTillFull() {
		while (unitCt < BOX_ROWS * BOX_COLS) {
			unitCt++;
			repaint();
			// sleep for random time, up to 1 second:
			try { Thread.sleep((int)(Math.random() * 1000)); }
			catch(InterruptedException ex) {}
		}
	}
	public void empty() {
		unitCt = 0;
		repaint();
	}
	public void fill() {
		unitCt = BOX_ROWS * BOX_COLS;
		repaint();
	}
	public Color getColour() {
		return colour;
	}
	@Override
	public void paint(Graphics g) {
		g.setColor(colour);
		for (int i = 0; i < unitCt; i++) {
			g.fillRect(
				(i % 4) * UNIT_W,
				(2 - (i / 4)) * UNIT_H,
				UNIT_W,
				UNIT_H);
		}
		g.setColor(Color.black);
		g.drawRect(0, 0, BOX_WIDTH - 1, BOX_HEIGHT - 1);
	}
	public void removeTillEmpty() {
		while (unitCt > 0) {
			unitCt--;
			repaint();
			// sleep for random time, up to 1 second:
			try { Thread.sleep((int)(Math.random() * 1000)); }
			catch(InterruptedException ex) {}
		}
	}
	public void setColour(Color colour) {
		this.colour = colour;
	}
}