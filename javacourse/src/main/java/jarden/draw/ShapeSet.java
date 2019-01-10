package jarden.draw;

import java.awt.Graphics;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Maintains a set of Shape references.
 */
public class ShapeSet {
	private ArrayList<MyShape> shapeList = new ArrayList<MyShape>();
	private boolean changed = false;
	private int selectedIndex = -1;

	public void add(MyShape shape) {
		shapeList.add(shape);
		changed = true;
		selectedIndex = shapeList.size() - 1;
	}

	public void animate(Graphics g) {
		for (MyShape shape: shapeList) {
			shape.animate(g);
		}
	}

	/**
	 * Remove all shapes in set.
	 */
	public void clear() {
		shapeList.clear();
		changed = false;
		selectedIndex = -1;
	}

	public void drawShapes(Graphics g) {
		for (MyShape shape: shapeList) {
			shape.draw(g);
		}
	}

	/**
	 * Return Shape indicated by index.
	 */
	public MyShape get(int index) {
		selectedIndex = index;
		return shapeList.get(index);
	}

	/**
	 * Return most recently added Shape containing point(x, y).
	 */
	public MyShape get(int x, int y) {
		MyShape shape;
		for (int i = shapeList.size() - 1; i >= 0; i--) {
			shape = shapeList.get(i);
			if (shape.isInShape(x, y)) {
				selectedIndex = i;
				return shape;
			}
		}
		selectedIndex = -1;
		return null;
	}

	/**
	 * 
	 * @return first shape in set.
	 */
	public MyShape getFirst() {
		return get(0);
	}

	/**
	 * @return next shape in set.
	 */
	public MyShape getNext() {
		if ((selectedIndex + 1) >= shapeList.size()) {
			return get(0);
		} else {
			return get(selectedIndex + 1);
		}
	}

	/**
	 * @return previous shape in set.
	 */
	public MyShape getPrevious() {
		if (selectedIndex > 0) {
			return get(selectedIndex - 1);
		} else {
			return get(shapeList.size() - 1);
		}
	}

	/**
	 * 
	 * @return boolean to show if any changes made since last save().
	 */
	public boolean isChanged() {
		return changed;
	}

	public void open(File file) throws Exception {
		DataInputStream dis = new DataInputStream(new BufferedInputStream(
				new FileInputStream(file)));
		shapeList.clear();
		try {
			while (true) {
				String name = dis.readUTF();
				Class<?> shapeClass = Class.forName("jarden.draw." + name);
				MyShape shape = (MyShape) shapeClass.newInstance();
				shape.open(dis);
				shapeList.add(shape);
			}
		} catch (EOFException eofe) {
		} finally {
			dis.close();
			changed = false;
			selectedIndex = -1;
		}
	}

	public void remove(int i) {
		shapeList.remove(i);
		changed = true;
		selectedIndex = -1;
	}

	public void remove(MyShape shape) {
		if (shapeList.remove(shape)) {
			changed = true;
			selectedIndex = -1;
			return;
		}
	}

	public void save() throws IOException {
		save(new File("jardenDraw.jdd"));
	}

	public void save(File file) throws IOException {
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(
				new FileOutputStream(file)));
		for (MyShape shape: shapeList) {
			shape.save(dos);
		}
		dos.close();
		changed = false;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
}
