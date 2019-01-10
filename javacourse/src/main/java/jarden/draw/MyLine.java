package jarden.draw;

import java.awt.Color;
import java.awt.Graphics;

class MyLine extends MyShape {
	public MyLine() {
		super();
	}

	public MyLine(int xpos, int ypos, int width, int height, Color colour) {
		super(xpos, ypos, width, height, colour);
	}

	@Override
	public void draw(Graphics g) {
		Color colour = lineColour;
		if (colour == null) {
			colour = Color.black;
		}
		g.setColor(colour);
		g.drawLine(x, y, width + x, height + y);
	}

	@Override
	public String getName() {
		return "MyLine";
	}
}
