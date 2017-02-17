package jarden.quiz;

public interface QuizListener {
	/**
	 * Called each time user gets question right.
	 */
	void onRightAnswer();
	/**
	 * Called each time user gets question wrong.
	 */
	void onWrongAnswer();
	/**
	 * Called each time user gets three questions right first time.
	 */
	void onThreeRightFirstTime();
	
	void onReset();
	
	void onEndOfQuestions();
}
