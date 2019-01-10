package shape;

import java.awt.Color;

public class MyShape {
	private int x, y, width, height;
	private Color lineColour, fillColour;
	private String text;

	public MyShape() {
		
	}
	@Override
	public String toString() {
		return "MyShape [x=" + x + ", y=" + y + ", width=" + width
				+ ", height=" + height + ", lineColour=" + lineColour
				+ ", fillColour=" + fillColour + ", text=" + text + "]";
	}
	public MyShape(int x, int y, int width, int height, Color lineColour,
			Color fillColour, String text) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.lineColour = lineColour;
		this.fillColour = fillColour;
		this.text = text;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Color getLineColour() {
		return lineColour;
	}

	public void setLineColour(Color lineColour) {
		this.lineColour = lineColour;
	}

	public Color getFillColour() {
		return fillColour;
	}

	public void setFillColour(Color fillColour) {
		this.fillColour = fillColour;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public static void main(String[] args) {
		MyShape shape1 = new MyShape();
		System.out.println("shape1=" + shape1);
		shape1.setText("shape with no location");
		System.out.println("shape1=" + shape1);
		MyShape shape2 = new MyShape(10, 20, 30, 40, Color.red, Color.yellow,
				"helloShape");
		System.out.println("shape2=" + shape2);

	}

}
