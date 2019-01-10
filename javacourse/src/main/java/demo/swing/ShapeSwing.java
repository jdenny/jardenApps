package demo.swing;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JFrame;

/**
 * Simple java application to simple shapes: rectangle, ellipse, line.
 */
public class ShapeSwing {

	public static void main(String[] args) {
		new ShapeSwing();
	}
	public ShapeSwing() {
		// create components:
		JFrame frame = new JFrame("ShapeSwing");
		ShapeCanvas shapeCanvas = new ShapeCanvas();
		// set layout of components:
		Container container = frame.getContentPane();
		container.add(shapeCanvas, BorderLayout.CENTER);
		frame.setSize(560, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true); // start event handling thread
	}
}

class ShapeCanvas extends Canvas {
	private static final long serialVersionUID = 1L;
	public void paint(Graphics g) {
		// text
		g.drawString("Rectangles", 20, 20);
		g.drawString("Ellipses", 220, 20);
		g.drawString("Lines", 420, 20);

		// rectangle outline
		g.drawRect(20, 40, 40, 50);
		
		// filled red rectangle
		g.setColor(Color.red);
		g.fillRect(80, 40, 50, 40);
		
		// green square with black border
		g.setColor(Color.green);
		g.fillRect(150, 40, 50, 50);
		g.setColor(Color.black);
		g.drawRect(150, 40, 50, 50);
		
		// oval outline
		g.drawOval(220, 40, 40, 50);
		
		// filled red oval
		g.setColor(Color.red);
		g.fillOval(280, 40, 50, 40);
		
		// green circle with black border
		g.setColor(Color.green);
		g.fillOval(350, 40, 50, 50);
		g.setColor(Color.black);
		g.drawOval(350, 40, 50, 50);
		
		// blue lines
		g.setColor(Color.blue);
		g.drawLine(420, 40, 470, 40);
		g.drawLine(470, 40, 430, 80);
		g.drawLine(430, 80, 480, 110);
		
		// put these together...
	    Font font1 = new Font("Times New Roman", Font.PLAIN, 20);
		Font font2 = new Font("Arial", Font.PLAIN, 16);
	    g.setFont(font1);
		g.drawString("Example of using these in combination:", 20, 140);
	    g.setFont(font2);

	    g.setColor(Color.cyan);
		g.fillRect(200, 160, 150, 100);
		g.setColor(Color.black);
		g.drawRect(200, 160, 150, 100);
		g.drawString("Shape", 205, 178);
		g.drawLine(200, 182, 350, 182);
		g.drawString("x, y, w, h, col, fillCol", 205, 199);
		g.drawLine(200, 204, 350, 204);
		g.drawString("move(dx, dy)", 205, 219);
		g.drawString("resize(dw, dh)", 205, 237);
		g.drawString("draw()", 205, 255);
		
		g.drawLine(275, 260, 275, 280);
		g.drawLine(275, 280, 265, 290);
		g.drawLine(275, 280, 285, 290);
		g.drawLine(100, 290, 450, 290);
		g.drawLine(100, 290, 100, 320);
		g.drawLine(275, 290, 275, 320);
		g.drawLine(450, 290, 450, 320);
		
	    g.setColor(Color.cyan);
		g.fillRect(50, 320, 100, 64);
		g.setColor(Color.black);
		g.drawRect(50, 320, 100, 64);
		g.drawString("Rectangle", 55, 338);
		g.drawLine(50, 342, 150, 342);
		g.drawLine(50, 364, 150, 364);
		g.drawString("draw()", 55, 379);
		
	    g.setColor(Color.cyan);
		g.fillRect(225, 320, 100, 64);
		g.setColor(Color.black);
		g.drawRect(225, 320, 100, 64);
		g.drawString("Ellipse", 230, 338);
		g.drawLine(225, 342, 325, 342);
		g.drawLine(225, 364, 325, 364);
		g.drawString("draw()", 230, 379);
		
	    g.setColor(Color.cyan);
		g.fillRect(400, 320, 100, 64);
		g.setColor(Color.black);
		g.drawRect(400, 320, 100, 64);
		g.drawString("Line", 405, 338);
		g.drawLine(400, 342, 500, 342);
		g.drawLine(400, 364, 500, 364);
		g.drawString("draw()", 405, 379);
	}
}

