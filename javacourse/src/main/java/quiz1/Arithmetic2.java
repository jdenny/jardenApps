package quiz1;

import jarden.gui.ConsoleSwing;

import java.util.Random;

public class Arithmetic2 {

	public static void main(String[] args) {
		Random randomNum = new Random();
		ConsoleSwing console = new ConsoleSwing();
		String another;
		do {
			int a = randomNum.nextInt(20) + 1; // 0 to 19 -> 1 to 20
			int b = randomNum.nextInt(20) + 1;
			int opCode = randomNum.nextInt(4);
			int correctAnswer;
			char op;
			switch(opCode) {
			case 0:
				op = '+';
				correctAnswer = a + b;
				break;
			case 1:
				op = '-';
				correctAnswer = a - b;
				break;
			case 2:
				op = '*';
				correctAnswer = a * b;
				break;
			default:
				op = '/';
				// shuffle the values a bit, so that the answer is an integer
				correctAnswer = a;
				a = a * b;
				break;
			}
			int res = console.getInt(a + " " + op + " " + b + " = ");
			if (res == correctAnswer) {
				console.println("correct!");
			} else {
				console.println("your answer: " + res +
						"; correct answer: " + correctAnswer);
			}
			another = console.getString("another? (y/n)");
		} while (another.startsWith("y"));
		System.out.println("Gracias y adios");
		console.close();
	}
}
