package jarden.draw;

import java.awt.Color;
import java.awt.Graphics;

public class Disk extends MyShape {
	public Disk() {
		super();
	}

	public Disk(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public Disk(int x, int y, int width, int height, Color lineColour) {
		super(x, y, width, height, lineColour);
	}

	public Disk(int x, int y, int width, int height, Color lineColour,
			Color fillColour) {
		super(x, y, width, height, lineColour, fillColour);
	}

	public void draw(Graphics g) {
		int ellipseHeight = height / 4;
		if (fillColour != null) {
			g.setColor(fillColour);
			g.fillOval(x, y + height - ellipseHeight, width, ellipseHeight);
		}
		if (lineColour != null) {
			g.setColor(lineColour);
			g.drawOval(x, y + height - ellipseHeight, width, ellipseHeight);
		}
		if (fillColour != null) {
			g.setColor(fillColour);
			g.fillRect(x, y + ellipseHeight / 2, width, height - ellipseHeight);
			g.fillOval(x, y, width, ellipseHeight);
		}
		if (lineColour != null) {
			g.setColor(lineColour);
			g.drawOval(x, y, width, ellipseHeight);
			g.drawLine(x, y + ellipseHeight / 2, x, y + height - ellipseHeight
					/ 2);
			g.drawLine(x + width, y + ellipseHeight / 2, x + width, y + height
					- ellipseHeight / 2);
			g.drawString(text, x + 10, y + ellipseHeight + 20);
		}
	}

	public String getName() {
		return "Disk";
	}
}
