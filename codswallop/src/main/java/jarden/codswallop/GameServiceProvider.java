package jarden.codswallop;

/**
 * Created by john.denny@gmail.com on 22/04/2026.
 */
public interface GameServiceProvider {
    boolean isServiceReady();
    void submitAnswer(String answer);
    void submitVote(int position);
}
