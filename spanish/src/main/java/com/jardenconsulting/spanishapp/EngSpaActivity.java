package com.jardenconsulting.spanishapp;

import jarden.engspa.EngSpaDAO;
import jarden.engspa.EngSpaQuiz;
import android.content.SharedPreferences;

public interface EngSpaActivity {
	/**
	 * Check the dictionary update file on the server.
	 */
	void checkForDBUpdates();
	EngSpaDAO getEngSpaDAO();
	EngSpaQuiz getEngSpaQuiz();

	/**
	 * Each question is given a unique, increment sequence
	 * number, to help determine when to repeat a question.
	 * @return
	 */
	int getQuestionSequence();
	
	SharedPreferences getSharedPreferences();
	
	/**
	 * Vibrate and play soundError.
	 */
	void onLost();

	/**
	 * Vibrate and play soundError.
	 */
	void onWrongAnswer();

	void setProgressBarVisible(boolean visible);

	/**
	 * Set Spanish word if user later clicks on
	 * speaker button.
	 * @param spanish
	 */
	void setSpanish(String spanish);

	/**
	 * @param statusId String resource id
	 */
	void setStatus(int statusId);
	void setStatus(String statusText);

	void setHelp(int resId);
	void showTopicDialog();

	/**
	 * Set Spanish word (see setSpanish) and speak it.
	 * @param spanish
	 */
	void speakSpanish(String spanish);
	
	void setAppBarTitle(int resId);
    void setAppBarTitle(String title);
}


