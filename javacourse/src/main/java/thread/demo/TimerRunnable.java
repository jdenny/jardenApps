package thread.demo;

import java.util.Scanner;

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
		Scanner scanner = new Scanner(System.in);
		System.out.println("Supply delay before we interrupt a thread");
		System.out.println("in tenths of a second; default to no delay");
		String delayStr = scanner.nextLine();
		scanner.close();
		int delay = 0;
		if (delayStr.length() > 0) {
			delay = Integer.parseInt(delayStr);
		}
		Runnable run1 = new TimerRunnable("silk");
		Thread silk = new Thread(run1);
		silk.start();

		Runnable run2 = new TimerRunnable("cotton");
		Thread cotton = new Thread(run2);
		cotton.start();
		if (delay > 0) {
			// give threads a chance to start:
			try {
				Thread.sleep(delay * 100);
			}
			catch(InterruptedException ex) {
				System.out.println("thread interrupted:" + ex);
			}
			System.out.println("main thread awoken");
			silk.interrupt();
		}
	}
}

