package thread.jdk5.demo.boxes;

import java.awt.Color;

import thread.demo.boxes.BoxCanvas;

class EmptyBoxRunnable implements Runnable {
	private StackJPanel stack;
	private BoxCanvas outBox;

	public EmptyBoxRunnable(StackJPanel stack, BoxCanvas outBox) {
		this.stack = stack;
		this.outBox = outBox;
	}
	public void run() {
		Color colour = stack.unstackBox();
		outBox.setColour(colour);
		outBox.fill();
		outBox.removeTillEmpty();
	}
}