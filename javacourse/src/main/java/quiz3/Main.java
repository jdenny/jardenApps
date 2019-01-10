package quiz3;

import jarden.gui.ConsoleSwing;

public class Main {
	public static void main(String[] args) {
		ConsoleSwing console = new ConsoleSwing("ArithmeticQuiz 3");
		Quiz quiz = null;
		while (true) {
			String option = console.getString("arithmetic (a) or capitals (c)?");
			if (option.startsWith("a")) {
				quiz = new ArithmeticQuiz();
				break;
			} else if (option.startsWith("c")) {
				quiz = new CapitalsQuiz();
				break;
			} else {
				console.println("option " + option + " is invalid!");
			}
		}
		String another;
		int rightCt = 0;
		int wrongCt = 0;
		do {
			String answer = console.getString(quiz.getNextQuestion());
			if (quiz.isCorrect(answer)) {
				++rightCt;
				console.println("right!");
			} else {
				++wrongCt;
				console.println("wrong! the right answer is: " + quiz.getAnswer());
			}
			another = console.getString("another? (y/n)");
		} while (another.startsWith("y"));
		int percentage = rightCt * 100 / (wrongCt + rightCt);
		console.println("you got " + rightCt + " right and " +
				wrongCt + " wrong, which is " + percentage + "%");
		if (percentage < 70) {
			console.println("must try harder!");
		}
		System.out.println("Gracias y adios");
	}
}
