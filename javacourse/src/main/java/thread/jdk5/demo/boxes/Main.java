package thread.jdk5.demo.boxes;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Java 5 version of thread.demo.boxes, using a thread pool and a blocking queue.
 * Note that the options available in the earlier version - no threads, threads,
 * synchronise and wait/notify - are not available here, because these things
 * are built in to the blocking queue, so we don't have to worry about them:
 * that's the point of them!
 * @author john.denny@gmail.com
 */
public class Main {
	public static void main(String[] args) throws InterruptedException {
		// proofOfConcept();
		// if (true) return;
		JPanel panel = new BoxesJPanel();
		JFrame jFrame = new JFrame("Threads Demo");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.getContentPane().add(panel);
		jFrame.pack();
		jFrame.setVisible(true);
	}
	public static void proofOfConcept() throws InterruptedException {
		BlockingQueue<String> queue = new ArrayBlockingQueue<>(3);
		queue.add("john");
		queue.add("sam");
		queue.add("joe");
		try {
			queue.add("joe");
			System.out.println("no exception!");
		} catch (IllegalStateException ise) {
			System.out.println("ise thrown and caught: good!");
		}
		System.out.println( // should be (3, 0)
			"Queue size: " + queue.size() +
			"; remaining capacity: " + queue.remainingCapacity());
		boolean added = queue.offer("angela", 2, TimeUnit.SECONDS);
		System.out.println("added: " + added); // should be false
		
		Iterator<String> iter = queue.iterator();
		System.out.println("iteration:");
		while (iter.hasNext()) {
			System.out.println("  " + iter.next());
		}
		System.out.println("removed: " + queue.remove()); // john
		System.out.println("removed: " + queue.remove()); // sam
		System.out.println("removed: " + queue.remove()); // joe
		try {
			queue.remove();
			System.out.println("no exception!");
		} catch (NoSuchElementException nee) {
			System.out.println("nee thrown and caught: good!");
		}
		String polled = queue.poll(2, TimeUnit.SECONDS);
		System.out.println("polled: " + polled); // null

	}
}

