package thread.demo;

import java.io.IOException;
import java.util.Scanner;

/**
 * Tryout for fork/join pattern. Early days yet.
 * @author john.denny@gmail.com
 *
 */
public class ForkJoinPattern implements Runnable {
	private String name;
	private boolean stop = false;
	
	public ForkJoinPattern(String name) {
		this.name = name;
	}
	
	public static void main(String[] args) throws IOException {
		ForkJoinPattern temp = new ForkJoinPattern("sam");
		new Thread(temp).start();
		temp = new ForkJoinPattern("joe");
		Thread thread = new Thread(temp);
		thread.setPriority(Thread.NORM_PRIORITY + 2);
		thread.start();
		System.out.println("press enter to exit");
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		temp.stop = true;
		scanner.close();
		System.out.println("adios mi amiguito");
	}

	@Override
	public void run() {
		double variance = 0.0;
		final int max = 20;
		for (int j = 0; j < max; j++) {
			if (this.stop) break;
			for (int i = 0; i < 10_000_000; i++) {
				double doble = i / 10.0;
				double square = doble * doble;
				double root = Math.sqrt(square);
				variance += Math.abs(root - doble);
			}
			System.out.println(name + "; " + j + "/" + max +
					"; variance = " + variance +
					"; priority=" + Thread.currentThread().getPriority());
		}
		//? System.exit(0);
	}
}
