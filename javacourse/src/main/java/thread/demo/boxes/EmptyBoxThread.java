package thread.demo.boxes;

public class EmptyBoxThread extends Thread {
	private StackJPanel stack;
	private BoxCanvas outBox;
	private BoxesJPanel client;

	public EmptyBoxThread(BoxesJPanel client, StackJPanel stack, BoxCanvas outBox) {
		this.client = client;
		this.stack = stack;
		this.outBox = outBox;
		if (client.useThreads()) {
			start();
		}
		else {
			run();
		}
	}
	public void run() {
		BoxCanvas stackBox;
		if (client.useSynch()) {
			synchronized (stack) {
				stackBox = stack.unstackBox();
				stack.notify();
			}
		}
		else {
			stackBox = stack.unstackBox();
		}
		outBox.setColour(stackBox.getColour());
		outBox.fill();
		outBox.removeTillEmpty();
	}
}