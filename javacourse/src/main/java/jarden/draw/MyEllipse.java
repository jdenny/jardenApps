package jarden.draw;

import java.awt.Color;
import java.awt.Graphics;

public class MyEllipse extends MyShape {
	public MyEllipse() {
		super();
	}

	public MyEllipse(int xpos, int ypos, int width, int height) {
		super(xpos, ypos, width, height);
	}

	public MyEllipse(int xpos, int ypos, int width, int height, Color lineColour) {
		super(xpos, ypos, width, height, lineColour);
	}

	public MyEllipse(int xpos, int ypos, int width, int height,
			Color lineColour, Color fillColour) {
		super(xpos, ypos, width, height, lineColour, fillColour);
	}

	@Override
	public void draw(Graphics g) {
		if (fillColour != null) {
			g.setColor(fillColour);
			g.fillOval(x, y, width, height);
		}
		if (lineColour != null) {
			g.setColor(lineColour);
			g.drawOval(x, y, width, height);
		}
	}

	@Override
	public String getName() {
		return "MyEllipse";
	}
}
