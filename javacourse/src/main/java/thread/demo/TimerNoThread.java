package thread.demo;

public class TimerNoThread {
	private String name;

	public TimerNoThread(String name) {
		this.name = name;
	}
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
		TimerNoThread silk = new TimerNoThread("silk");
		TimerNoThread cotton = new TimerNoThread("cotton");
		silk.run();
		cotton.run();
	}
}

