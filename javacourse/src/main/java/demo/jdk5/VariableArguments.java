package demo.jdk5;

import jarden.draw.MyEllipse;
import jarden.draw.MyRectangle;
import jarden.draw.MyShape;

import java.awt.Rectangle;

public class VariableArguments {

	public static void main(String[] args) {
		MyRectangle rectangle = new MyRectangle(20, 30, 40, 50);
		MyEllipse ellipse = new MyEllipse(120, 130, 140, 150);
		moveShapes(3, 4, rectangle, ellipse);
		
		Rectangle ellipseBounds = ellipse.getBounds();
		assert ellipseBounds.x == 123: "ellipseBounds.x=" + ellipseBounds.x;
		assert ellipseBounds.y == 134: "ellipseBounds.y=" + ellipseBounds.y;
		System.out.println("new location of ellipse is: " + ellipseBounds);
	}
	public static void moveShapes(int x, int y, MyShape... shapes) {
		System.out.println("number of shapes to move: " + shapes.length);
		for (MyShape shape: shapes) {
			shape.move(x, y);
		}
	}
}
