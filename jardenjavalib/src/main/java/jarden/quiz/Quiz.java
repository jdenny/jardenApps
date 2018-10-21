package jarden.quiz;

import java.util.Random;

public abstract class Quiz {
    public static final int CORRECT = 1;
    public static final int INCORRECT = 0;
    public static final int FAIL = -1;
    public static final int ANSWER_TYPE_INT = 0;
    public static final int ANSWER_TYPE_DOUBLE = 1;
    public static final int ANSWER_TYPE_STRING = 2;
    public static final String TEMPLATE_KEY = "$TEMPLATE";
	public static final String IO_KEY = "$IO";
    private static final int MAX_ATTEMPTS = 3;
    private static final int BASE_MAX = 20;

	protected Random randomNum = new Random();
	private char questionStyle = 'P';
	private char answerStyle = 'P';
	private QuizListener quizListener;
	private String question;
	private int correctIntAnswer;
	private String correctAnswer;
	private int attempts;
	private int rightFirstTimeCt = 0;
	
	public Quiz() {
	}
	public void setQuizListener(QuizListener quizListener) {
		this.quizListener = quizListener;
	}
	/**
	 * Used to indicate what type of input field is required for the
	 * user's answer.
	 * 
	 * @return one of the constants:
	 * 		ANSWER_TYPE_INT,
	 * 		ANSWER_TYPE_DOUBLE,
	 * 		ANSWER_TYPE_STRING
	 */
	public abstract int getAnswerType();
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
	/**
	 * For generated questions, e.g. Maths, no further action is required.
	 * For text questions from a file, may need to override this to reset
	 * list of unanswered questions.
	 */
	public void reset() {
	}
	/**
	 * Called if the user got the question right on first attempt.
	 * When there are 3 rightFirstTimes, notify the quizListener
	 * if there is one. Typical actions in the quizListener:
	 * 		if there is a maze, give the user a key
	 * 		if there is no maze, increment the level
	 */
	private void rightFirstTime() {
		notifyRightFirstTime();
		++this.rightFirstTimeCt;
		if (this.rightFirstTimeCt >= 3) {
			if (quizListener != null) {
				quizListener.onThreeRightFirstTime();
			}
			this.rightFirstTimeCt = 0;
		}
	}
	/**
	 * Called from rightFirstTime(). The default behaviour is nothing
	 * but subclass can override, e.g. for text questions can remove
	 * current question. 
	 */
	public void notifyRightFirstTime() {
	}
	public abstract String getNextQuestion(int level) throws EndOfQuestionsException;

	public String getCurrentQuestion() {
		return this.question;
	}
	/**
	 * Can be overridden to provide a hint after an incorrect answers.
	 * E.g. could return the first few letters of an answer, where the
	 * number of letters increases with the number of incorrectAnswers.
	 */
	public String getHint() {
		return null;
	}
	public int getAttempts() {
		return this.attempts;
	}
	/**
	 * QuestionStyle requests how the question should be asked:
	 * P=printed; all other styles mean spoken, in the appropriate
	 * language:
	 *    E=English, S=Spanish, F=French, G=German etc
	 * This is only a request, as the device might not support
	 * textToSpeech. Question and Answer style added 30th Dec 2013.
	 */
	public char getQuestionStyle() {
		return this.questionStyle;
	}
	public void setQuestionStyle(char questionStyle) {
		this.questionStyle = questionStyle;
	}
	/**
	 * AnswerStyle requests how the answer should be supplied:
	 * P=printed (i.e. typed); all other styles mean spoken -
	 * see getQuestionStyle.
	 * This is only a request, as the device might not support
	 * voiceRecognition.
	 */
	public char getAnswerStyle() {
		return this.answerStyle;
	}
	public void setAnswerStyle(char answerStyle) {
		this.answerStyle = answerStyle;
	}
	/**
	 * Compare answer with correctIntAnswer and return result.
	 * @return Quiz.CORRECT if correct
	 *         Quiz.INCORRECT if wrong but still got more attempts
	 *         Quiz.FAIL if still wrong after Quiz.MAX_ATTEMPTS
	 */
	public int isCorrect(int answer) {
		return this.checkAttempts(answer == correctIntAnswer);
	}
	/**
	 * Compare answer with correctIntAnswer and return result.
	 * @return Quiz.CORRECT if correct
	 *         Quiz.INCORRECT if wrong but still got more attempts
	 *         Quiz.FAIL if still wrong after Quiz.MAX_ATTEMPTS
	 */
	public int isCorrect(String answer) {
		return this.checkAttempts(answer.trim().equalsIgnoreCase(correctAnswer));
	}
	private int checkAttempts(boolean correct) {
		if (correct) {
			if (quizListener != null) {
				quizListener.onRightAnswer();
			}
			if (this.attempts == 0) {
				this.rightFirstTime();
			}
			return Quiz.CORRECT;
		}
		if (quizListener != null) {
			quizListener.onWrongAnswer();
		}
		this.attempts++;
		if (this.attempts >= Quiz.MAX_ATTEMPTS) {
			return Quiz.FAIL;
		} else {
			return Quiz.INCORRECT;
		}
	}
	public String getCorrectAnswer() {
		return this.correctAnswer;
	}
	public int getMaxInt(int level) {
		return getMaxInt(level, Quiz.BASE_MAX);
	}
	public int getMaxInt(int level, int max) {
		return (int)(max * Math.pow(1.2, level));
	}
	public String toString() {
		if (getAnswerType() == Quiz.ANSWER_TYPE_STRING) {
			return question + "; " + correctAnswer; 
		} else {
			return question + "; " + correctIntAnswer;
		}
	}

}


