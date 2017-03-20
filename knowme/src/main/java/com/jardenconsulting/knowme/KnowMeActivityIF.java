package com.jardenconsulting.knowme;

/**
 * Interface between activity and fragments. All fragments communicate
 * with each other via the activity, using this interface. 
 * @author john.denny@gmail.com
 */
public interface KnowMeActivityIF {
	void setStatusMessage(String message);
	void setOtherPlayerName(String otherPlayerName);
	void questionPosed();
	void displayResults(String[] questionArray, int myMe, int myHim,
			int hisHim, int hisMe);
	void endOfQuestions(int percentCorrect);
}
