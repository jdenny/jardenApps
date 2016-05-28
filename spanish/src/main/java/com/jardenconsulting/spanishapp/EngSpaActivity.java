package com.jardenconsulting.spanishapp;

import android.content.SharedPreferences;

import jarden.engspa.EngSpaDAO;
import jarden.engspa.EngSpaQuiz;
import jarden.engspa.EngSpaUser;

public interface EngSpaActivity {
    int CLEAR_STATUS = -1;

    EngSpaDAO getEngSpaDAO();
    EngSpaQuiz getEngSpaQuiz();
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
    void setAppBarTitle();
    void setProgressBarVisible(boolean visible);
    void setShowHelp();

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


