package quiz1;

import jarden.gui.ConsoleSwing;

import java.util.Random;

public class Arithmetic {

	public static void main(String[] args) {
		int a = new Random().nextInt(20); // 0 to 19
		int b = new Random().nextInt(20);
		ConsoleSwing console = new ConsoleSwing();
		int res = console.getInt(a + " + " + b + " = ");
		console.println("your answer: " + res +
				"; correct answer: " + (a+b));
	}
}
