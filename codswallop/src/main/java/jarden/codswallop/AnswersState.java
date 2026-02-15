package jarden.codswallop;

import java.util.List;

/**
 * Created by john.denny@gmail.com on 11/02/2026.
 */
public class AnswersState {
    public final String question;
    public final List<String> answers;
    public final boolean named;
    public AnswersState(String question, List<String> answers, boolean named) {
        this.question = question;
        this.answers = answers;
        this.named = named;
    }
}