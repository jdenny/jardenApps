package jarden.draw;

import java.awt.Color;
import java.awt.Graphics;

public class MyRectangle extends MyShape {
	public MyRectangle() {
		super();
	}

	public MyRectangle(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public MyRectangle(int x, int y, int width, int height, Color lineColour) {
		super(x, y, width, height, lineColour);
	}

	public MyRectangle(int x, int y, int width, int height, Color lineColour,
			Color fillColour) {
		super(x, y, width, height, lineColour, fillColour);
	}

	@Override
	public void draw(Graphics g) {
		if (fillColour != null) {
			g.setColor(fillColour);
			g.fillRect(x, y, width, height);
		}
		Color colour = lineColour;
		if (colour == null) {
			g.setColor(Color.black);
		} else {
			g.setColor(colour);
			g.drawRect(x, y, width, height);
		}
		if (text.length() > 0) {
			g.drawString(text, x + 10, y + 20);
		}
	}

	@Override
	public String getName() {
		return "MyRectangle";
	}
}
