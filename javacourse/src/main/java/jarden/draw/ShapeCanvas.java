package jarden.draw;

import java.awt.Canvas;
import java.awt.Graphics;

public class ShapeCanvas extends Canvas {
	private static final long serialVersionUID = 1L;
	protected ShapeSet shapeSet = new ShapeSet();
	protected boolean animated = false;

	public ShapeSet getShapeSet() {
		return this.shapeSet;
	}

	public void paint(Graphics g) {
		if (animated) {
			shapeSet.animate(g);
		} else {
			shapeSet.drawShapes(g);
		}
	}
}
