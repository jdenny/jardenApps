package thread.jdk5.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimerRunnable implements Runnable {
	private String name;

	public TimerRunnable(String name) {
		this.name = name;
	}
	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			System.out.println(name + " " + i);
			try {
				Thread.sleep(300);
			}
			catch(InterruptedException ex) {
				System.out.println("thread interrupted: " + ex);
				break;
			}
		}
	}
	public static void main(String[] args) {
		Runnable run1 = new TimerRunnable("silk");
		ExecutorService executor = Executors.newFixedThreadPool(2);
		executor.execute(run1); // equivalent to new Thread(run1).start();

		Runnable run2 = new TimerRunnable("cotton");
		executor.execute(run2); // equivalent to new Thread(run2).start();
		executor.shutdown(); // shutdown after existing tasks complete
	}
}

