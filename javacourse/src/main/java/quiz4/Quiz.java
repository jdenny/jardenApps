package quiz4;

import java.util.Random;

public abstract class Quiz {
    public final static int CORRECT = 1;
    public final static int INCORRECT = 0;
    public final static int FAIL = -1;
    public final static int MAX_ATTEMPTS = 3;
	protected Random randomNum = new Random();
	private String question;
	private int correctIntAnswer;
	private String correctAnswer;
	private int attempts;
	
	public Quiz() {
	}
	public void setQuestionAnswer(String question, String answer) {
		this.question = question;
		this.correctAnswer = answer;
		this.attempts = 0;
	}
	public void setQuestionAnswer(String question, int answer) {
		this.correctIntAnswer = answer;
		this.question = question;
		this.correctAnswer = Integer.toString(answer);
		this.attempts = 0;
	}
	public abstract String getNextQuestion();

	public String getCurrentQuestion() {
		return question;
	}
	public int isCorrect(int answer) {
		return checkAttempts(answer == correctIntAnswer);
	}
	public int isCorrect(String answer) {
		return checkAttempts(answer.equals(correctAnswer));
	}
	private int checkAttempts(boolean correct) {
		if (correct) {
			return Quiz.CORRECT;
		}
		this.attempts++;
		if (this.attempts >= Quiz.MAX_ATTEMPTS) {
			return Quiz.FAIL;
		} else {
			return Quiz.INCORRECT;
		}
	}
	public String getAnswer() {
		return correctAnswer;
	}
}


