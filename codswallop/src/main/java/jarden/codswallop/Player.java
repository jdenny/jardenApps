package jarden.codswallop;

/**
 * Created by john.denny@gmail.com on 08/01/2026.
 */
public class Player {
    private String name;
    private String answer = null;
    private int score = 0;
    private int votedIndex = -1;
    private String nameVotedFor = null;

    public Player(String name) {
        this.name = name;
    }
    public void reset() {
        answer = null; // i.e. not answered yet
        nameVotedFor = null; // i.e. not voted yet
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
    public void setNameVotedFor(String nameVotedFor) {
        this.nameVotedFor = nameVotedFor;
    }

    public String getNameVotedFor() {
        return nameVotedFor;
    }
}
