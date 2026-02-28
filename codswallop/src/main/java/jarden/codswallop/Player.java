package jarden.codswallop;

/**
 * Created by john.denny@gmail.com on 08/01/2026.
 */
public class Player {
    private String name;
    private String answer = null;
    private int score = 0;
    //!! private boolean awaitingAnswer;
    //!! private boolean awaitingVote;
    private int votedIndex = -1;

    public Player(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "Player: " +
                "name='" + name +
                ", answer='" + answer +
                ", score=" + score;
    }
    public String getName() {
        return name;
    }
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public int getScore() {
        return score;
    }
    public void incrementScore() {
        score++;
    }
    /*!!
    public boolean isAwaitingAnswer() {
        return awaitingAnswer;
    }
    public void setAwaitingAnswer(boolean awaitingAnswer) {
        this.awaitingAnswer = awaitingAnswer;
    }
    public boolean isAwaitingVote() {
        return awaitingVote;
    }
    public void setAwaitingVote(boolean awaitingVote) {
        this.awaitingVote = awaitingVote;
    }

     */

    public int getVotedIndex() {
        return votedIndex;
    }
    public void setVotedIndex(int votedIndex) {
        this.votedIndex = votedIndex;
    }
}
