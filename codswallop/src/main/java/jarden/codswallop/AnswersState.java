package jarden.codswallop;

import java.util.List;

/**
 * Created by john.denny@gmail.com on 11/02/2026.
 */
public class AnswersState {
    public final String question;
    public final List<String> answers;
    public AnswersState(String question, List<String> answers) {
        this.question = question;
        this.answers = answers;
    }
}