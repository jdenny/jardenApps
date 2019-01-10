package jarden.draw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class MyShape implements Runnable {
	protected int x, y, width, height;
	protected Color lineColour, fillColour;
	protected String text;
	private transient Graphics graphics;

	public MyShape() {
		
	}
	public MyShape(int x, int y, int width, int height) {
		this(x, y, width, height, Color.black, null, null);
	}

	public MyShape(int x, int y, int width, int height, Color colour) {
		this(x, y, width, height, colour, null, null);
	}

	public MyShape(int x, int y, int width, int height, Color lineColour,
			Color fillColour) {
		this(x, y, width, height, lineColour, fillColour, "");
	}

	public MyShape(DataInputStream dis) throws IOException {
		open(dis);
	}

	public MyShape(int x, int y, int width, int height,
			Color lineColour, Color fillColour, String text) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.lineColour = lineColour;
		this.fillColour = fillColour;
		this.text = text;
	}

	public void animate(Graphics g) {
		// Simple animation, added for exercise 9.3
		// Animation moved to run() for exercise 13
		graphics = g.create();
		Thread thread = new Thread(this);
		thread.start();
	}

	public abstract void draw(Graphics g);

	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}

	public abstract String getName();

	public boolean isInShape(int x1, int y1) {
		return (x1 > x && x1 < x + width && y1 > y && y1 < y + height);
	}

	public void move(int xMove, int yMove) {
		x += xMove;
		y += yMove;
	}

	public void open(DataInputStream dis) throws IOException {
		this.x = dis.readInt();
		this.y = dis.readInt();
		this.width = dis.readInt();
		this.height = dis.readInt();
		int rgb = dis.readInt();
		if (rgb == -1) {
			this.lineColour = null;
		} else {
			this.lineColour = new Color(rgb);
		}
		rgb = dis.readInt();
		if (rgb == -1) {
			this.fillColour = null;
		} else {
			this.fillColour = new Color(rgb);
		}
		text = dis.readUTF();
	}

	public void resize(int wPlus, int hPlus) {
		width += wPlus;
		height += hPlus;
	}

	public void run() {
		graphics.setXORMode(Color.white);
		for (int i = 0; i < 5; i++) {
			draw(graphics);
			try {
				Thread.sleep(500); // do nothing for 500 milliseconds
			} catch (InterruptedException ex) {
			}
		}
		graphics.setPaintMode();
		draw(graphics);
	}

	public void save(DataOutputStream dos) throws IOException {
		dos.writeUTF(getName());
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(width);
		dos.writeInt(height);
		if (lineColour == null) {
			dos.writeInt(-1);
		} else {
			dos.writeInt(lineColour.getRGB());
		}
		if (fillColour == null) {
			dos.writeInt(-1);
		} else {
			dos.writeInt(fillColour.getRGB());
		}
		dos.writeUTF(text);
	}

	public void setBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setBounds(Rectangle rectangle) {
		this.x = rectangle.x;
		this.y = rectangle.y;
		this.width = rectangle.width;
		this.height = rectangle.height;
	}

	public void setFillColour(Color colour) {
		this.fillColour = colour;
	}

	public void setLineColour(Color colour) {
		this.lineColour = colour;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public String toString() {
		return getName() + ": (" + x + ", " + y + ", " + width + ", " + height
				+ ", " + lineColour + ", " + fillColour + ")";
	}
}
