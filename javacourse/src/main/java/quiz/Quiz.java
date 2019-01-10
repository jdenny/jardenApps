package quiz;

import java.util.Random;

public abstract class Quiz {
    public final static int CORRECT = 1;
    public final static int INCORRECT = 0;
    public final static int FAIL = -1;
    public final static int MAX_ATTEMPTS = 3;
    public final static int BASE_MAX = 20;
	protected Random randomNum = new Random();
	private String question;
	private int correctIntAnswer;
	private String correctAnswer;
	private int attempts;
	private int level = 1;
	private int consecutiveRights = 0;
	
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
	public void notifyRightFirstTime() {
		++this.consecutiveRights;
		if (this.consecutiveRights >= 3) {
			++this.level;
			this.consecutiveRights = 0;
		}
	}
	public abstract String getNextQuestion() throws EndOfQuestionsException;

	public String getCurrentQuestion() {
		return this.question;
	}
	/**
	 * Compare answer with correctIntAnswer and return result.
	 * If right first time 3 consecutive times, increment level.
	 * @param answer
	 * @return Quiz.CORRECT if correct
	 *         Quiz.INCORRECT if wrong but still got more attempts
	 *         Quiz.FAIL if still wrong after Quiz.MAX_ATTEMPTS
	 */
	public int isCorrect(int answer) {
		return this.checkAttempts(answer == correctIntAnswer);
	}
	public int isCorrect(String answer) {
		return this.checkAttempts(answer.equals(correctAnswer));
	}
	private int checkAttempts(boolean correct) {
		if (correct) {
			if (this.attempts == 0) {
				this.notifyRightFirstTime();
			}
			return Quiz.CORRECT;
		}
		this.attempts++;
		this.consecutiveRights = 0;
		if (this.attempts >= Quiz.MAX_ATTEMPTS) {
			return Quiz.FAIL;
		} else {
			return Quiz.INCORRECT;
		}
	}
	public String getAnswer() {
		return this.correctAnswer;
	}
	public int getMaxInt() {
		return getMaxInt(Quiz.BASE_MAX);
	}
	public int getMaxInt(int max) {
		return (int)(max * Math.pow(1.2, level));
	}
	public int getLevel() {
		return this.level;
	}
}


