package jarden.codswallop;

/**
 * Created by john.denny@gmail.com on 15/02/2026.
 */
public class Constants {
    /* sample usage:
        String s = PlayerState.AWAITING_HOST_IP.toString(); // preferred
        String s2 = PlayerState.AWAITING_HOST_IP.name();
        PlayerState ps = PlayerState.AWAITING_QUESTION;
        if (ps == PlayerState.DISCONNECTED) {} // returns false
        if (ps == PlayerState.AWAITING_QUESTION) {} // returns true
     */
    public enum HostState {
        AWAITING_PLAYERS,
        AWAITING_CT_ANSWERS,
        AWAITING_CT_VOTES,
        READY_FOR_NEXT_QUESTION, DUPLICATE_PLAYER_NAME
    }
    public enum PlayerState {
        // used by QuestionFragment
        AWAITING_HOST_IP, AWAITING_FIRST_QUESTION,
        SUPPLY_ANSWER, AWAITING_ANSWERS,
        // used by AnswerFragment
        SUPPLY_VOTE, AWAITING_VOTES, AWAITING_NEXT_QUESTION
    }
    public enum Protocol {
        // *************************************
        // Host to all Players using broadcast *
        // *************************************
        HOST_ANNOUNCE, // e.g. HOST_ANNOUNCE|192.168.0.12|50001
        // *******************************
        // Host to all Players using Tcp *
        // *******************************
        QUESTION, // e.g. QUESTION|{questionSequence, e.g. 3}|PEOPLE|Ignaz Semmelveis
        ALL_ANSWERS, // e.g. ALL_ANSWERS|3|footballer|physician|Russian politican
        NAMED_ANSWERS,
        // NAMED_ANSWERS|3|CORRECT|{realAnswer}|{playerName}|{votedFor}|{totalScore}|{playerAnswer}|...
        // e.g. NAMED_ANSWERS|3|CORRECT|physician|Joe|John|2|footballer|John|CORRECT|4|physician
        // i.e. Joe voted for John and has total score of 2; John voted for the real answer and has
        // a total score of 4 so far.
        END_GAME, // i.e. END_GAME
        // *******************
        // Player to Host:   *
        // *******************
        ANSWER, // e.g. ANSWER|3|physician
        VOTE // e.g. VOTE|3|{indexOfSelectedAnswer}
    }
    public static final String QUESTION = "QUESTION";
    public static final String ANSWER = "ANSWER";
    public static final String ALL_ANSWERS = "ALL_ANSWERS";
    public static final String NAMED_ANSWERS = "NAMED_ANSWERS";
    public static final String CORRECT = "REAL ANSWER";
    public static final String VOTE = "VOTE";
    public static final String LOGIN_DIALOG = "LOGIN_DIALOG";
    public static final String QUESTION_SEQUENCE_KEY = "QUESTION_SEQUENCE_KEY";
    public static final String GAME_PREFS = "GAME_PREFS";
}
