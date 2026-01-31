package jarden.codswallop;

/**
 * Created by john.denny@gmail.com on 08/01/2026.
 */
public class Player {
    private String name;
    private String answer;
    private int score;

    public Player(String name, String answer, int score) {
        this.name = name;
        this.answer = answer;
        this.score = score;
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
    public void resetScore() {
        score = 0;
    }
}
