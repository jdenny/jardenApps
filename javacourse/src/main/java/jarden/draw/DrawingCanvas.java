package jarden.draw;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JTextField;

public class DrawingCanvas extends ShapeCanvas implements MouseListener,
		MouseMotionListener, ActionListener {
	private static final long serialVersionUID = 1L;
	public final static Color DEFAULT_LINE_COLOUR = Color.black;
	public final static Color DEFAULT_FILL_COLOUR = Color.yellow;
	public final static String[] actions = { "Draw", "Move", "Resize",
			"Delete", "Properties" };
	public final static String[] shapeNames = { "Rectangle", "Ellipse", "Line",
			"Disk" };
	private int startX = -1, startY = -1;
	private int currX, currY;
	private String shapeName = shapeNames[0];
	private Color lineColour = DEFAULT_LINE_COLOUR;
	private Color fillColour = DEFAULT_FILL_COLOUR;
	private String action = actions[0];
	private MyShape selectedShape;
	private PropertyDialog propertyDialog = null;

	public DrawingCanvas() {
		propertyDialog = new PropertyDialog(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source instanceof JButton) {
			String action = event.getActionCommand();
			if (action.equals("<<")) {
				selectedShape = shapeSet.getFirst();
			} else if (action.equals(">")) {
				selectedShape = shapeSet.getNext();
			} else if (action.equals("<")) {
				selectedShape = shapeSet.getPrevious();
			} else if (action.equals("LineColour")) {
				Color newLineColour = JColorChooser.showDialog(this,
						"Choose Line Colour", selectedShape.lineColour);
				if (newLineColour != null) {
					selectedShape.setLineColour(newLineColour);
					propertyDialog.setLineColour(newLineColour);
					repaint();
				}
			} else if (action.equals("FillColour")) {
				Color newFillColour = JColorChooser.showDialog(this,
						"Choose Fill Colour", selectedShape.fillColour);
				if (newFillColour != null) {
					selectedShape.setFillColour(newFillColour);
					propertyDialog.setFillColour(newFillColour);
					repaint();
				}
			} else {
				System.out.println("actionPerformed; event=" + event);
				return;
			}
			propertyDialog.popup(selectedShape);
		} else if (source instanceof JTextField) {
			JTextField textField = (JTextField) source;
			String name = textField.getName();
			if (name.equals("X")) {
				selectedShape.x = Integer.parseInt(textField.getText());
				repaint();
			} else if (name.equals("Y")) {
				selectedShape.y = Integer.parseInt(textField.getText());
				repaint();
			} else if (name.equals("Width")) {
				selectedShape.width = Integer.parseInt(textField.getText());
				repaint();
			} else if (name.equals("Height")) {
				selectedShape.height = Integer.parseInt(textField.getText());
				repaint();
			} else if (name.equals("Text")) {
				selectedShape.text = textField.getText();
				repaint();
			}
		} else {
			System.out.println("actionPerformed; event=" + event);
		}
	}

	/**
	 * Draw a rectangle, defined by 2 points (startX, startY) and (x, y).
	 */
	private void drawOutline(Graphics g, boolean ellipse, int startX,
			int startY, int x, int y) {
		int rx, ry, rw, rh;
		// allow for movement in all four quadrants:
		// (x, y) left, right, up, down of (startX, startY)
		if (x > startX) {
			rx = startX;
			rw = x - startX;
		} else {
			rx = x;
			rw = startX - x;
		}
		if (y > startY) {
			ry = startY;
			rh = y - startY;
		} else {
			ry = y;
			rh = startY - y;
		}
		if (ellipse) {
			g.drawOval(rx, ry, rw, rh);
		} else {
			g.drawRect(rx, ry, rw, rh);
		}
	}

	public void mouseClicked(MouseEvent event) {
	}

	public void mouseDragged(MouseEvent event) {
		// if amending existing shape, shape determined by
		// selectedShape.getName()
		// else shape determined by shapeName.
		boolean ellipse = false;
		if (action.equals("Delete") || action.equals("Properties"))
			return;
		if (!action.equals("Draw")) {
			if (selectedShape == null)
				return;
			if (selectedShape.getName().equals("Ellipse")) {
				ellipse = true;
			}
		} else if (shapeName.equals("Ellipse")) {
			ellipse = true;
		}
		if (startX == -1) {
			System.out.println("mouseDrag without corresponding mouseDown!");
			return;
		}
		Graphics cg = getGraphics();
		cg.setXORMode(Color.white);
		if (action.equals("Move")) {
			if (currX != -1) { // remove old band
				int x1 = selectedShape.x + currX - startX;
				int y1 = selectedShape.y + currY - startY;
				int x2 = x1 + selectedShape.width;
				int y2 = y1 + selectedShape.height;
				drawOutline(cg, ellipse, x1, y1, x2, y2);
			}
			currX = event.getX();
			currY = event.getY();
			int x1 = selectedShape.x + currX - startX;
			int y1 = selectedShape.y + currY - startY;
			int x2 = x1 + selectedShape.width;
			int y2 = y1 + selectedShape.height;
			drawOutline(cg, ellipse, x1, y1, x2, y2);
		} else if (action.equals("Resize")) {
			if (currX != -1) { // remove old band
				int x1 = selectedShape.x;
				int y1 = selectedShape.y;
				int x2 = x1 + selectedShape.width + currX - startX;
				int y2 = y1 + selectedShape.height + currY - startY;
				drawOutline(cg, ellipse, x1, y1, x2, y2);
			}
			currX = event.getX();
			currY = event.getY();
			int x1 = selectedShape.x;
			int y1 = selectedShape.y;
			int x2 = x1 + selectedShape.width + currX - startX;
			int y2 = y1 + selectedShape.height + currY - startY;
			drawOutline(cg, ellipse, x1, y1, x2, y2);
		} else if (action.equals("Draw")) {
			if (currX != -1) { // remove old band
				drawOutline(cg, ellipse, startX, startY, currX, currY);
			}
			currX = event.getX();
			currY = event.getY();
			drawOutline(cg, ellipse, startX, startY, currX, currY);
		}
	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void mouseMoved(MouseEvent event) {
	}

	public void mousePressed(MouseEvent event) {
		startX = event.getX();
		startY = event.getY();
		currX = currY = -1;
		if (!action.equals("Draw")) {
			selectedShape = shapeSet.get(event.getX(), event.getY());
		}
	}

	public void mouseReleased(MouseEvent event) {
		// shape for elastic band:
		boolean ellipse = false;
		if (!action.equals("Draw")) {
			if (selectedShape == null)
				return;
			if (selectedShape.getName().equals("Ellipse")) {
				ellipse = true;
			}
		} else if (shapeName.equals("Ellipse")) {
			ellipse = true;
		}
		if (startX == -1) {
			System.out.println("mouseUp without corresponding mouseDown!");
			return;
		}
		Graphics cg = getGraphics();
		if (action.equals("Delete")) {
			shapeSet.remove(selectedShape);
			repaint();
		} else if (action.equals("Move")) {
			if (currX != -1) {
				// remove old band
				cg.setXORMode(Color.white);
				int x1 = selectedShape.x + currX - startX;
				int y1 = selectedShape.y + currY - startY;
				int x2 = x1 + selectedShape.width;
				int y2 = y1 + selectedShape.height;
				drawOutline(cg, ellipse, x1, y1, x2, y2);
			}
			currX = event.getX();
			currY = event.getY();
			selectedShape.move(currX - startX, currY - startY);
			repaint();
		} else if (action.equals("Resize")) {
			if (currX != -1) { // remove old band
				cg.setXORMode(Color.white);
				int x1 = selectedShape.x + currX - startX;
				int y1 = selectedShape.y + currY - startY;
				int x2 = x1 + selectedShape.width;
				int y2 = y1 + selectedShape.height;
				drawOutline(cg, ellipse, x1, y1, x2, y2);
			}
			currX = event.getX();
			currY = event.getY();
			selectedShape.resize(currX - startX, currY - startY);
			repaint();
		} else if (action.equals("Draw")) {
			if (currX != -1) { // remove old band
				cg.setXORMode(Color.white);
				drawOutline(cg, ellipse, startX, startY, currX, currY);
				cg.setPaintMode();
			}
			MyShape myShape;
			int x = event.getX();
			int y = event.getY();
			int rx, ry, rw, rh;
			if (x > startX) {
				rx = startX;
				rw = x - startX;
			} else {
				rx = x;
				rw = startX - x;
			}
			if (y > startY) {
				ry = startY;
				rh = y - startY;
			} else {
				ry = y;
				rh = startY - y;
			}
			if (rw <= 0 || rh <= 0) {
				System.out.println("rx=" + rx + ", ry=" + ry + ", rw=" + rw
						+ ", rh=" + rh + ", shapeName=" + shapeName);
				startX = startY = -1;
				return;
			}
			if (shapeName.equals("Rectangle")) {
				shapeSet.add(myShape = new MyRectangle(rx, ry, rw, rh,
						lineColour, fillColour));
			} else if (shapeName.equals("Ellipse")) {
				shapeSet.add(myShape = new MyEllipse(rx, ry, rw, rh,
						lineColour, fillColour));
			} else if (shapeName.equals("Line")) {
				shapeSet.add(myShape = new MyLine(startX, startY, x - startX, y
						- startY, lineColour));
			} else if (shapeName.equals("Disk")) {
				shapeSet.add(myShape = new Disk(rx, ry, rw, rh, lineColour,
						fillColour));
			} else {
				System.out
						.println("program error in mouseReleased shapeChoice switch!");
				return;
			}
			startX = startY = -1;
			myShape.draw(cg);
			selectedShape = myShape;
		}
		propertyDialog.popup(selectedShape);
		shapeSet.setChanged(true);
	}

	public void setAction(String action) {
		this.action = action;
		if (action.equals("Move")) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else if (action.equals("Draw")) {
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		} else if (action.equals("Resize")) {
			setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
		} else if (action.equals("Delete") || action.equals("Properties")) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public void setAnimated(boolean animated) {
		this.animated = animated;
		if (animated)
			repaint();
	}

	public void setFillColour(Color fillColour) {
		this.fillColour = fillColour;
	}

	public void setLineColour(Color lineColour) {
		this.lineColour = lineColour;
	}

	public void setShapeName(String shapeName) {
		this.shapeName = shapeName;
	}

	public void setShapeSet(ShapeSet shapeSet) {
		// TODO: test if old shape set has changed; popup dialog if so.
		this.shapeSet = shapeSet;
	}
}
