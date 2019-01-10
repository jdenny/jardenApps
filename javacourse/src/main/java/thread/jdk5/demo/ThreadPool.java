package thread.jdk5.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPool {

	public static void main(String[] args) {
		String[] names = {
			"john", "peet", "paul", "sam", "julie", "sheila", "ann", "jack"	
		};
		ExecutorService executorService = Executors.newFixedThreadPool(3);
		for (String name: names) {
			executorService.submit(new Task(name));
			System.out.println("submitted task " + name);
		}
		try {
			executorService.shutdown();
			System.out.println("executorService.shutdown() called");
			executorService.awaitTermination(10, TimeUnit.SECONDS);
			System.out.println("executorService terminated");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class Task implements Runnable {
	private String name;
	
	public Task(String name) {
		this.name = name;
	}
	@Override
	public void run() {
		for (int i = 0; i < 3; i++) {
			System.out.println("task name: " + this.name + "(" + i + ")" +
				"; threadName: " + Thread.currentThread().getName());
			Thread.yield();
		}
	}
}
