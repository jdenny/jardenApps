package jarden.knowme;

import java.util.ArrayList;
import java.util.LinkedList;

public class QuizSession {
	private ArrayList<MultiQA> questions;
	private LinkedList<MultiQA> outstandingQuestions;
	private int questionIndex = 0;
	private Quiz10 quiz10;
	private final static int QUIZ_SIZE = 10;
	/*
	 * When first player moves to next question, set this true;
	 * so when second player clicks next question, return current
	 * question.
	 */
	private boolean onePlayerAhead = false;

	/*
	 * TODO: when players link (KnowMeService.linkPlayers()), see if
	 * there is a saved instance of outstandingQuestions for this
	 * playerPairing; if so use this, instead of getting a new one.
	 * At start, no previous pairing; when A connects, B becomes server.
	 * B gets outstanding questions, and saves it on the device, along
	 * with the email of the other player; adjusts each time a question
	 * is answered. Game ends. Later, B connects to A or vice versa;
	 * B finds a saved outstandingQuestions; B thus becomes server again
	 */
	public QuizSession() {
		QuestionManager qm = QuestionManager.getInstance();
		questions = qm.getQuestionList();
		outstandingQuestions = new LinkedList<MultiQA>();
		for (MultiQA multiQA: questions) {
			outstandingQuestions.add(multiQA);
		}
		try {
			getNextQuiz10();
		} catch (EndOfQuestionsException e) {
			throw new IllegalStateException("no questions!");
		}
	}
	public boolean isOnePlayerAhead() {
		return onePlayerAhead;
	}
	public void setOnePlayerAhead(boolean onePlayerAhead) {
		this.onePlayerAhead = onePlayerAhead;
	}
	public Quiz10 getNextQuiz10() throws EndOfQuestionsException {
		// get next QUIZ_SIZE questions
		// TODO: cope with running out of questions, and need to go on to
		// those one of the players got wrong

		ArrayList<MultiQA> tenQuestions = new ArrayList<MultiQA>();

		for (int i = 0; i < QUIZ_SIZE; i++) {
			if (this.questionIndex >= this.questions.size()) break;
			tenQuestions.add(questions.get(this.questionIndex));
			++this.questionIndex;
		}
		if (tenQuestions.size() == 0) {
			throw new EndOfQuestionsException("No more questions in the pot!");
		}
		this.quiz10 = new Quiz10(tenQuestions);
		return this.quiz10;
	}
	public Quiz10 getCurrentQuiz10() {
		return this.quiz10;
	}
	public QAResults getQAResults(Player player) {
		return quiz10.getQAResults(player);
	}
	public String[] getMultiQAResults(Player player) {
		return quiz10.getMultiQAResults(player);
	}
	public void printQuiz10Results(Player player) {
		quiz10.printQuiz10Results(player);
	}

}

