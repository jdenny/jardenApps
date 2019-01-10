package thread.demo.boxes;

public class FillBoxThread extends Thread {
	private BoxCanvas inBox;
	private BoxesJPanel client;

	public FillBoxThread(BoxesJPanel client, BoxCanvas inBox) {
		this.inBox = inBox;
		this.client = client;
		if (client.useThreads()) {
			start();
		}
		else {
			run();
		}
	}
	public void run() {
		inBox.addTillFull();
		client.doneIt(inBox);
	}
}