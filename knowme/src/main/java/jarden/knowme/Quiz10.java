package jarden.knowme;

import java.util.ArrayList;

public class Quiz10 {
	private ArrayList<MultiQA> tenQuestions;
	private int currentQuestionIndex = 0;
	private int player1CorrectCt = 0;
	private int player2CorrectCt = 0;
	private int[][] player1Answers;
	private int[][] player2Answers;
	private boolean combineScores = true;
	
	public Quiz10(ArrayList<MultiQA> tenQuestions) {
		assert tenQuestions.size() > 0: "no questions supplied to constructor!";
		this.tenQuestions = tenQuestions;
		int size = tenQuestions.size();
		this.player1Answers = new int[size][2];
		this.player2Answers = new int[size][2];
	}
	/**
	 * Log results for player, as index to answers array in MultiQA.
	 * Note: this index starts from 1. 
	 * @param player
	 * @param myMe
	 * @param myHim
	 * @return true if both players now answered.
	 */
	public boolean setMyAnswers(Player player, int myMe, int myHim) {
		boolean bothAnswered;
		int playerNumber = player.getPlayerNumber();
		if (playerNumber == 1) {
			player1Answers[currentQuestionIndex][0] = myMe;
			player1Answers[currentQuestionIndex][1] = myHim;
			bothAnswered = (player2Answers[currentQuestionIndex][0] > 0);
		} else {
			assert playerNumber == 2: ("invalid playerNumber: " + playerNumber);
			player2Answers[currentQuestionIndex][0] = myMe;
			player2Answers[currentQuestionIndex][1] = myHim;
			bothAnswered = (player1Answers[currentQuestionIndex][0] > 0);
		}
		if (bothAnswered) {
			if (player1Answers[currentQuestionIndex][1] == player2Answers[currentQuestionIndex][0]) {
				++this.player1CorrectCt;
			}
			if (player2Answers[currentQuestionIndex][1] == player1Answers[currentQuestionIndex][0]) {
				++this.player2CorrectCt;
			}
		}
		return bothAnswered;
	}
	public MultiQA getCurrentQuestion() {
		return tenQuestions.get(currentQuestionIndex);
	}
	public String[] getCurrentQuestionAsArray(Player player) {
		return getQuestionAsArray(getCurrentQuestion(), player);
	}
	public MultiQA getNextQuestion() throws EndOfQuestionsException {
		if ((this.currentQuestionIndex+1) < this.tenQuestions.size()) {
			++this.currentQuestionIndex;
			return getCurrentQuestion();
		} else {
			throw new EndOfQuestionsException();
		}
	}
	public String[] getNextQuestionAsArray(Player player) throws EndOfQuestionsException {
		MultiQA multiQA = getNextQuestion();
		return getQuestionAsArray(multiQA, player);
	}
	private String[] getQuestionAsArray(MultiQA multiQA, Player player) {
		String[] answers = multiQA.getAnswers(player);
		String[] questionArray = new String[answers.length + 2];
		questionArray[0] = multiQA.getMyQuestion(player);
		questionArray[1] = multiQA.getHisQuestion(player);
		for (int i = 0; i < answers.length; i++) {
			questionArray[2 + i] = answers[i];
		}
		return questionArray;
	}
	public String[] getMultiQAResults(Player player) {
		return getMultiQAResults2(player, this.currentQuestionIndex);
	}
	public QAResults getQAResults(Player player) {
		QAResults qaResults = new QAResults();
		int index = this.currentQuestionIndex;
		String hisName = player.getOtherPlayer().getName();
		int myPlayerNumber = player.getPlayerNumber();
		int[][] myAnswers, hisAnswers;
		int meCorrect, himCorrect;
		if (myPlayerNumber == 1) {
			myAnswers = this.player1Answers;
			hisAnswers = this.player2Answers;
			meCorrect = this.player1CorrectCt;
			himCorrect = this.player2CorrectCt;
		} else {
			assert myPlayerNumber == 2:
				("unexpected player number: " + myPlayerNumber);
			myAnswers = this.player2Answers;
			hisAnswers = this.player1Answers;
			meCorrect = this.player2CorrectCt;
			himCorrect = this.player1CorrectCt;
		}
		if (myAnswers[index][0] == 0 || hisAnswers[index][0] == 0) {
			// one player may have asked next question, and both not yet answered
			// in that case, move back to previous question
			--index;
		}
		qaResults.myMe = myAnswers[index][0];
		qaResults.myHim = myAnswers[index][1];
		qaResults.hisHim = hisAnswers[index][0];
		qaResults.hisMe = hisAnswers[index][1];
		if (qaResults.myMe < 1 || qaResults.myHim < 1 ||
				qaResults.hisMe < 1 || qaResults.hisHim < 1) {
			throw new RuntimeException("results have not been set");
		}
		qaResults.meRight = (qaResults.myHim == qaResults.hisHim);
		String meRes = "you were " + (qaResults.meRight?"right!":"wrong!");
		qaResults.himRight = (qaResults.myMe == qaResults.hisMe);
		String himRes = hisName + " was " + (qaResults.himRight?"right!":"wrong!");
		if (this.combineScores) {
			qaResults.summary = meRes + " " + himRes +
					" (" + (meCorrect + himCorrect) + "/" + (2 * (index + 1)) + ")";
		} else {
			qaResults.summary = meRes + " (" + meCorrect + "/" + (index + 1) + ") " +
					himRes + " (" + himCorrect + "/" + (index + 1) + ")";
		}
		// 50 instead of 100 because we are adding up the scores of both players:
		qaResults.percentCorrect = 50 * (player1CorrectCt + player2CorrectCt) / tenQuestions.size();
		return qaResults;
	}
	private String[] getMultiQAResults2(Player player, int index) {
		QAResults qaResults = getQAResults(player);
		String hisName = player.getOtherPlayer().getName();
		String[] results = new String[5];
		MultiQA multiQA = tenQuestions.get(index);
		String[] answers = multiQA.getAnswers(player);
		results[0] = qaResults.summary;
		results[1] = "you said: " + answers[qaResults.myMe - 1];
		results[2] = hisName + " thought you would say: " +
				answers[qaResults.hisMe - 1];
		results[3] = hisName + " said: " + answers[qaResults.hisHim - 1];
		results[4] = "you thought " + hisName + " would say: " +
				answers[qaResults.myHim - 1];
		return results;
	}
	// for debug purposes only:
	public void printQuiz10Results(Player player) {
		for (int i = 0; i < tenQuestions.size(); i++) {
			String[] results = getMultiQAResults2(player, i);
			for (String result: results) {
				System.out.println(result);
			}
		}
	}
}

