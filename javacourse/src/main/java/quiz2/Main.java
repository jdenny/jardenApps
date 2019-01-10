package quiz2;

import jarden.gui.ConsoleSwing;

public class Main {
	public static void main(String[] args) {
		String another;
		ArithmeticQuiz quiz = new ArithmeticQuiz();
		ConsoleSwing console = new ConsoleSwing("ArithmeticQuiz 2");
		do {
			int res = console.getInt(quiz.getNextQuestion());
			if (quiz.isCorrect(res)) {
				console.println("correct!");
			} else {
				console.println("wrong! the correct answer is: " + quiz.getAnswer());
			}
			another = console.getString("another? (y/n)");
		} while (another.startsWith("y"));
		System.out.println("Gracias y adios");
		console.close();
	}

}
