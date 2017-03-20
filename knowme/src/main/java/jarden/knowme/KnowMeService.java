package jarden.knowme;

import java.util.HashMap;

public class KnowMeService {
	private static KnowMeService instance = new KnowMeService();
	private HashMap<String, Player> activePlayers;
	
	public static KnowMeService getInstance() {
		return instance;
	}
	private KnowMeService() {
		activePlayers = new HashMap<String, Player>();
	}
	/**
	 * Create a new Player and add to the list of active players.
	 * @param name: used to identify the player in the list of active players.
	 * @param email
	 */
	public Player login(String name, String email) {
		Player newPlayer = new Player(name, email);
		activePlayers.put(name, newPlayer);
		return newPlayer;
	}
	// TODO: send a request to player2 to accept link
	/**
	 * Start a game between these two players and return the first question and answers.
	 * @param name1; login this player, using name1 and email
	 * @param email
	 * @param name2; name of existing player to link to
	 */
	public void linkPlayers(String name1, String email, String name2) {
		Player player1 = login(name1, email);
		Player player2 = activePlayers.get(name2);
		player1.setPairing(player2);
	}
	
	public void logout(String name) {
		Player player = activePlayers.get(name);
		if (player != null) {
			activePlayers.remove(name);
			Player otherPlayer = player.getOtherPlayer();
			if (otherPlayer != null) {
				otherPlayer.setPairing(null);
			}
		}
	}

	/**
	 * Provide 2 answers: answers for player {name}, and guess of answer for other player. 
	 * @param name: name of player giving answers.
	 * @param myMe: index to multiple choice answers, starting from 1.
	 * @param myHim: ditto.
	 */
	public boolean setMyAnswers(String name, int myMe, int myHim) {
		Player player = activePlayers.get(name);
		QuizSession quizSession = player.getQuizSession();
		Quiz10 quiz10 = quizSession.getCurrentQuiz10();
		return quiz10.setMyAnswers(player, myMe, myHim);
	}
	public String[] getCurrentQuestion(String name) {
		Player player = activePlayers.get(name);
		QuizSession quizSession = player.getQuizSession();
		return quizSession.getCurrentQuiz10().getCurrentQuestionAsArray(player);
	}
	/**
	 * Get next question from set of 10.
	 * @param name
	 * @return: questions and answers.
	 * @throws EndOfQuestionsException
	 * @see #login(String, String)
	 */
	public String[] getNextQuestion(String name) throws EndOfQuestionsException {
		Player player = activePlayers.get(name);
		QuizSession quizSession = player.getQuizSession();
		String[] questionArray;
		if (quizSession.isOnePlayerAhead()) {
			questionArray = quizSession.getCurrentQuiz10().getCurrentQuestionAsArray(player);
			quizSession.setOnePlayerAhead(false); // or toggle, but more obscure!
		} else {
			questionArray = quizSession.getCurrentQuiz10().getNextQuestionAsArray(player);
			quizSession.setOnePlayerAhead(true);
		}
		return questionArray;
	}
	public String[] getNextQuiz10(String name) throws EndOfQuestionsException {
		Player player = activePlayers.get(name);
		QuizSession quizSession = player.getQuizSession();
		String[] questionArray;
		if (quizSession.isOnePlayerAhead()) {
			questionArray = quizSession.getCurrentQuiz10().getCurrentQuestionAsArray(player);
			quizSession.setOnePlayerAhead(false); // or toggle, but more obscure!
		} else {
			questionArray = quizSession.getNextQuiz10().getCurrentQuestionAsArray(player);
			quizSession.setOnePlayerAhead(true);
		}
		return questionArray;
	}
	/**
	 * Return an array of Strings of the form:
	 * [0] you were right; Bob was wrong
	 * [1] you said red
	 * [2] Bob thought you would say blue
	 * [3] Bob said green
	 * [4] you thought Bob would said green
	 * @param name
	 * @return
	 * @deprecated because KnowMeService is only called on the server side,
	 * so we don't know if the client is set to verbose.
	 */
	public String[] getVerboseResults(String name) {
		Player player = activePlayers.get(name);
		QuizSession quizSession = player.getQuizSession();
		return quizSession.getMultiQAResults(player);
	}
	public QAResults getQAResults(String name) {
		Player player = activePlayers.get(name);
		QuizSession quizSession = player.getQuizSession();
		return quizSession.getQAResults(player);
	}
	public void printResultSummary(String name) {
		Player player = activePlayers.get(name);
		QuizSession quizSession = player.getQuizSession();
		quizSession.printQuiz10Results(player);
	}
}
