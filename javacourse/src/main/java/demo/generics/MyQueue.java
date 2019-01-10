package demo.generics;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;

public class MyQueue {

	public static void main(String[] args) {
		final String prompt =
				"a[dd] <string>, o[ffer] <string>, r[emove], po[ll], e[lement], pe[ek], q[uit]";
		Scanner scanner = new Scanner(System.in);
		Queue<String> myQueue = new LinkedList<String>();
		String line;
		while (true) {
			showQueue(myQueue);
			System.out.println(prompt);
			line = scanner.nextLine();
			if (line.startsWith("a")) {
				String[] tokens = line.split(" ");
				if (tokens.length < 2) {
					System.out.println("please supply string for this action");
					continue;
				}
				try {
					System.out.println(myQueue.add(tokens[1])?"success":"failed");
				} catch(IllegalStateException e) {
					System.out.println(e.toString());
				}
			} else if (line.startsWith("o")) {
				String[] tokens = line.split(" ");
				if (tokens.length < 2) {
					System.out.println("please supply string for this action");
					continue;
				}
				System.out.println(myQueue.offer(tokens[1])?"success":"failed");
			} else if (line.startsWith("r")) {
				try {
					System.out.println("removed: " + myQueue.remove());
				} catch(NoSuchElementException e) {
					System.out.println(e.toString());
				}
			} else if (line.startsWith("po")) {
				System.out.println("removed by poll: " + myQueue.poll());
			} else if (line.startsWith("e")) {
				try {
					System.out.println("retrieved: " + myQueue.element());
				} catch(NoSuchElementException e) {
					System.out.println(e.toString());
				}
			} else if (line.startsWith("pe")) {
				System.out.println("peeked: " + myQueue.peek());
			} else if (line.startsWith("q")) {
				break;
			} else {
				System.out.println("unrecognised action: " + line);
			}
			
		}
		scanner.close();
		System.out.println("adios mi amiguito");
	}

	private static void showQueue(Queue<String> queue) {
		for (String s: queue) {
			System.out.println(s);
		}
	}

}
