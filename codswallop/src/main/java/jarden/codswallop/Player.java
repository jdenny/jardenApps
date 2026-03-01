package jarden.codswallop;

/**
 * Created by john.denny@gmail.com on 08/01/2026.
 */
public class Player {
    private String name;
    private String answer = null;
    private int score = 0;
    private int votedIndex = -1;
    private int votedForCt = 0;

    public Player(String name) {
        this.name = name;
    }
    public void reset() {
        answer = null; // i.e. not answered yet
        votedIndex = -1; // i.e. not voted yet
        votedForCt = 0; // i.e. no one voted for your answer yet
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
    public int getVotedForCt() {
        return votedForCt;
    }
    public void incrementScore() {
        score++;
    }

    public int getVotedIndex() {
        return votedIndex;
    }
    public void setVotedIndex(int votedIndex) {
        this.votedIndex = votedIndex;
    }

    public void incrementVotedForScore() {
        votedForCt++; // someone voted for my answer...
        score++; // which increases my total score
    }
}
