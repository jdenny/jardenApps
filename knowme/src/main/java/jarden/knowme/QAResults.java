package jarden.knowme;

public 	class QAResults {
	public int myMe;
	public int myHim;
	public int hisHim;
	public int hisMe;
	public boolean meRight;
	public boolean himRight;
	/**
	 * Combined scores as percentage of total size of quiz.
	 * E.g. in the example below: 600/8 = 75.
	 */
	public int percentCorrect;
	/**
	 * String of the form: You were wrong! Julie was right! (6/8)
	 * Alternatively, if Quiz10.combineScores is false:
	 *     You were wrong! (2/4) Julie was right! (4/4)
	 */
	public String summary;
}

