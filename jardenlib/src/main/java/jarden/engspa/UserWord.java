package jarden.engspa;

import jarden.provider.engspa.EngSpaContract.QAStyle;

public class UserWord {
	private int userId;
	private int wordId;
	private int consecutiveRightCt;
	private int questionSequence = 0;
	/**
	 * The active style when question was answered incorrectly.
	 * This allows us to re-ask the question in the same style.
	 */
	private QAStyle qaStyle;

	public UserWord(int userId, int wordId) {
		this(userId, wordId, 0, 0, QAStyle.writtenSpaToEng);
	}
	public UserWord(int userId, int wordId,
			int questionSequence,
			int consecutiveRightCt, QAStyle qaStyle) {
		super();
		this.userId = userId;
		this.wordId = wordId;
		this.consecutiveRightCt = consecutiveRightCt;
		this.questionSequence = questionSequence;
		this.qaStyle = qaStyle;
	}
	/**
	 * Add result of asking and answering this question.
	 * @param correct: true if answered correctly
	 * @param questionSequence
	 * @return consecutiveRightCt
	 */
	public int addResult(boolean correct, int questionSequence, QAStyle qaStyle) {
		this.questionSequence = questionSequence;
		if (!correct) this.qaStyle = qaStyle; // style of last wrong answer
		if (correct) {
			++consecutiveRightCt;
		} else {
			consecutiveRightCt = 0;
		}
		return consecutiveRightCt;
	}
	public boolean isRecentlyUsed(int questionSequence) {
		int requiredGap = (consecutiveRightCt < 3)?3:10;
		return questionSequence - this.questionSequence <= requiredGap;
	}
	
	@Override
	public String toString() {
		return "UserWord [userId=" + userId + ", wordId=" + wordId +
				", " + toShortString() + ", " + qaStyle + "]";
	}
	public String toShortString() {
		return 	"consecRightCt=" + consecutiveRightCt +
				", questionSeq=" + questionSequence;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getWordId() {
		return wordId;
	}

	public void setWordId(int wordId) {
		this.wordId = wordId;
	}

	public int getConsecutiveRightCt() {
		return consecutiveRightCt;
	}

	public void setConsecutiveRightCt(int consecutiveRightCt) {
		this.consecutiveRightCt = consecutiveRightCt;
	}
	public int getQuestionSequence() {
		return questionSequence;
	}
	public void setQuestionSequence(int questionSequence) {
		this.questionSequence = questionSequence;
	}
	public QAStyle getQaStyle() {
		return qaStyle;
	}
	public void setQaStyle(QAStyle qaStyle) {
		this.qaStyle = qaStyle;
	}
}
