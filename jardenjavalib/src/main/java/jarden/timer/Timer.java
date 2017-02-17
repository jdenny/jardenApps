package jarden.timer;

public class Timer implements Runnable {
	private boolean running;
	private int intervalTenths;
	private TimerListener listener;

	public Timer(TimerListener listener, int intervalTenths) {
		this.listener = listener;
		this.intervalTenths = intervalTenths;
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		this.running = true;
		thread.start();
	}
	public void run() {
		while (running) {
			try { Thread.sleep(intervalTenths * 100); }
			catch(InterruptedException e) {}
			// check again, in case stopped while we were asleep
			if (running) { 
				listener.onTimerEvent();
			}
		}
	}
	public void setInterval(int intervalTenths) {
		this.intervalTenths = intervalTenths;
	}
	public void stop() {
		running = false;
	}
}