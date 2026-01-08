package jarden.balderdash;

import java.util.Arrays;

/**
 * Created by john.denny@gmail.com on 08/01/2026.
 */
public class Results {
    private String playerAnswer;
    private String correctAnswer;
    private Player[] players;

    public Results(String playerAnswer, String correctAnswer, Player[] players) {
        this.playerAnswer = playerAnswer;
        this.correctAnswer = correctAnswer;
        this.players = players;
    }

    @Override
    public String toString() {
        return "Results{" +
                "playerAnswer=" + playerAnswer +
                ", correctAnswer=" + correctAnswer +
                ", players=" + Arrays.toString(players);
    }

    public String getPlayerAnswer() {
        return playerAnswer;
    }
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    public Player[] getPlayers() {
        return players;
    }
}
