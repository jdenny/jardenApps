package jarden.codswallop;

/**
 * Created by john.denny@gmail.com on 15/02/2026.
 */
public class Constants {
    /* sample usage:
        String s = PlayerState.AWAITING_HOST_IP.toString(); // preferred
        String s2 = PlayerState.AWAITING_HOST_IP.name();
        PlayerState ps = PlayerState.AWAITING_QUESTION;
        if (ps == PlayerState.DISCONNECTED) {} // returns true
     */
    public enum HostState {
        AWAITING_PLAYERS, PLAYER_JOINED, AWAITING_CT_ANSWERS, AWAITING_CT_VOTES,
        READY_FOR_NEXT_QUESTION, DUPLICATE_PLAYER_NAME
    }
    public enum PlayerState {
        // used by QuestionFragment
        AWAITING_HOST_IP, AWAITING_FIRST_QUESTION, SUPPLY_ANSWER, AWAITING_ANSWERS,
        // used by AnswerFragment
        SUPPLY_VOTE, AWAITING_VOTES, AWAITING_NEXT_QUESTION, DISCONNECTED
    }
    public enum Protocol {
        HOST_ANNOUNCE, QUESTION, ALL_ANSWERS, NAMED_ANSWERS, ANSWER, VOTE
    }
    public static final String QUESTION = "QUESTION";
    public static final String ANSWER = "ANSWER";
    public static final String ALL_ANSWERS = "ALL_ANSWERS";
    public static final String NAMED_ANSWERS = "NAMED_ANSWERS";
    public static final String CORRECT = "CORRECT";
    public static final String VOTE = "VOTE";
    public static final String LOGIN_DIALOG = "LOGIN_DIALOG";
    public static final String QUESTION_SEQUENCE_KEY = "QUESTION_SEQUENCE_KEY";
}
