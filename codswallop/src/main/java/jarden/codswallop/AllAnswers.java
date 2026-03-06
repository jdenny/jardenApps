package jarden.codswallop;

import java.util.List;

/**
 * Created by john.denny@gmail.com on 11/02/2026.
 */
public class AllAnswers {
    public final String question;
    public final List<String> answers;
    public final boolean named;
    public boolean isCorrect;
    public List<Integer> linesVotedForMe;

    public AllAnswers(String question, List<String> answers, boolean named) {
        this.question = question;
        this.answers = answers;
        this.named = named;
    }
    public AllAnswers(String question, List<String> answers, boolean named, boolean isCorrect,
                      List<Integer> linesVotedForMe ) {
        this(question, answers, named);
        this.linesVotedForMe = linesVotedForMe;
        this.isCorrect = isCorrect;
    }
}