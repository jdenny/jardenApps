package thread.demo;

public class TimerThread extends Thread {
	private String name;

	public TimerThread(String name) {
		this.name = name;
	}
	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			System.out.println(name + " " + i);
			try {
				Thread.sleep(300);
			}
			catch(InterruptedException ex) {}
		}
	}
	public static void main(String[] args) {
		Thread silk = new TimerThread("silk");
		Thread cotton = new TimerThread("cotton");
		silk.start();
		cotton.start();
	}
}

