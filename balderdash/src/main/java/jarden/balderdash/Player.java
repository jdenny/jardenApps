package jarden.balderdash;

/**
 * Created by john.denny@gmail.com on 08/01/2026.
 */
public class Player {
    private String name;
    private String answer;
    private int score;

    public Player(int score, String answer, String name) {
        this.score = score;
        this.answer = answer;
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
