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
	 * Get DEBUG tag
	 * @return
	 */
	String getTag();
	
	void setProgressBarVisible(boolean visible);

	/**
	 * Set Spanish word if user later clicks on
	 * speaker button.
	 * @param spanish
	 */
	void setSpanish(String spanish);

	void setStatus(String status);

	void showTopicDialog();

	/**
	 * Set Spanish word (see setSpanish) and speak it.
	 * @param spanish
	 */
	void speakSpanish(String spanish);
	
	/**
	 * Set engSpa title in the App bar. This is specifically the
	 * title used in EngSpaFragment.
	 * @param title
	 */
	void setEngSpaTitle(String title);
}


