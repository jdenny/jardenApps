package thread.jdk5.demo.boxes;

import thread.demo.boxes.BoxCanvas;

class FillBoxRunnable implements Runnable {
	private BoxCanvas inBox;
	private BoxesJPanel client;

	public FillBoxRunnable(BoxesJPanel client, BoxCanvas inBox) {
		this.inBox = inBox;
		this.client = client;
	}
	public void run() {
		inBox.addTillFull();
		client.doneIt(inBox);
	}
}