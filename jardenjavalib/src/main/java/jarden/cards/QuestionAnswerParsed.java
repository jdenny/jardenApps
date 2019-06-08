package jarden.cards;

import jarden.quiz.QuestionAnswer;

/**
 * Created by john.denny@gmail.com on 08/06/2019.
 */
public class QuestionAnswerParsed extends QuestionAnswer {
    private ParsedAnswer parsedAnswer;

    public QuestionAnswerParsed(String question, String answer) {
        super(question, answer);
    }
    public ParsedAnswer getParsedAnswer() {
        if (parsedAnswer == null) {
            parsedAnswer = new ParsedAnswer(answer);
        }
        return parsedAnswer;
    }

}
