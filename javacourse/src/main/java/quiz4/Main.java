package quiz4;

import jarden.gui.ConsoleSwing;

public class Main {
	public static void main(String[] args) {
		ConsoleSwing console = new ConsoleSwing("ArithmeticQuiz 4");
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
			String question = quiz.getNextQuestion();
			String answer;
			int result;
			do {
				answer = console.getString(question);
				result = quiz.isCorrect(answer);
				if (result == Quiz.INCORRECT) {
					++wrongCt;
					question = "wrong; try again; " +
							quiz.getCurrentQuestion();
				}
			} while (result == Quiz.INCORRECT);
			if (result == Quiz.CORRECT) {
				++rightCt;
				console.println("right!");
			} else { // result must be FAIL
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
		console.println("Gracias y adios");
	}
}
