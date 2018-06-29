package jarden.knowme;

/**
 * Session state for 2-player game.
 * @author john.denny@gmail.com
 *
 */
public class Player {
	private String name;
	private int playerNumber;
	private Player otherPlayer;
	private QuizSession quizSession;

	public Player(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	/**
	 * Link two players together for a quiz; "this" becomes player1.
	 * This method should only called for one of the players.
	 * @param player2
	 */
	public void setPairing(Player player2) {
		if (player2 == null) {
			this.otherPlayer = null;
			quizSession = null;
		} else {
			this.playerNumber = 1;
			this.otherPlayer = player2;
			player2.playerNumber = 2;
			player2.otherPlayer = this;
			QuizSession quizSession = new QuizSession();
			this.setQuizSession(quizSession);
			player2.setQuizSession(quizSession);
		}
	}
	public int getPlayerNumber() {
		return this.playerNumber;
	}
	public Player getOtherPlayer() {
		return this.otherPlayer;
	}
	public void setQuizSession(QuizSession quizSession) {
		this.quizSession = quizSession;
	}
	public QuizSession getQuizSession() {
		return this.quizSession;
	}
}

