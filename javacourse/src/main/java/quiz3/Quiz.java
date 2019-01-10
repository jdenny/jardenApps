package quiz3;

import java.util.Random;

public abstract class Quiz {
	protected Random randomNum = new Random();
	private String question;
	private int correctIntAnswer;
	private String correctAnswer;
	
	public Quiz() {
	}
	public void setQuestionAnswer(String question, String answer) {
		this.question = question;
		this.correctAnswer = answer;
	}
	public void setQuestionAnswer(String question, int answer) {
		this.question = question;
		this.correctIntAnswer = answer;
		this.correctAnswer = Integer.toString(answer);
	}
	public abstract String getNextQuestion();

	public String getCurrentQuestion() {
		return question;
	}
	public boolean isCorrect(int answer) {
		return answer == correctIntAnswer;
	}
	public boolean isCorrect(String answer) {
		return answer.equals(correctAnswer);
	}
	public String getAnswer() {
		return correctAnswer;
	}
}


