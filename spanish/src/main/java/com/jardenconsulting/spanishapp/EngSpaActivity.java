package com.jardenconsulting.spanishapp;

import jarden.engspa.EngSpaDAO;
import jarden.engspa.EngSpaUser;

import android.content.SharedPreferences;

public interface EngSpaActivity {

	EngSpaDAO getEngSpaDAO();
    EngSpaUser getEngSpaUser();

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
    void setAppBarTitle(int resId);
    void setAppBarTitle(String title);
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

    /**
     * Resource id of String to be shown in tip field
     * @param resId
     */
    void setTip(int resId);
	void showTopicDialog();

    void speakEnglish(String english);
	/**
	 * Set Spanish word (see {@link #setSpanish(String)}) and speak it.
	 * @param spanish
	 */
	void speakSpanish(String spanish);
	
}


