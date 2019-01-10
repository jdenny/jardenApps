package quiz2;

import jarden.gui.ConsoleSwing;

public class CapitalsQuiz {
	private String[] countries = {
		"Norway", "England",
		"France", "Italy", "Sweden",
		"Denmark", "Finland", "Netherlands", "Germany", "Spain",
		"Ireland", "Belgium", "Greece", "Portugal"
	};
	private String[] capitals = {
		"Oslo", "London", "Paris", "Rome", "Stockholm",
		"Copenhagen", "Helsinki", "Amsterdam", "Berlin", "Madrid",
		"Dublin", "Brussels", "Athens", "Lisbon"	
	};
	private int index = -1;
	
	public CapitalsQuiz() {
		
	}
	public String getNextQuestion() {
		if (index < (countries.length-1)) {
			index++;
		}
		return this.getCurrentQuestion();
	}
	public String getCurrentQuestion() {
		return "what is the capital of " + countries[index] + "? ";
	}
	public boolean isCorrect(String answer) {
		return answer.equals(getAnswer());
	}
	public String getAnswer() {
		return capitals[index];
	}
	
	public static void main(String[] args) {
		String another;
		CapitalsQuiz quiz = new CapitalsQuiz();
		ConsoleSwing console = new ConsoleSwing("CapitalsQuiz");
		do {
			String res = console.getString(quiz.getNextQuestion());
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
