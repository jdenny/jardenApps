package jarden.balderdash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by john.denny@gmail.com on 12/01/2026.
 */
public class QuestionManager {
    public class QuestionAnswer {
        public String question;
        public String answer;
        QuestionAnswer(String q, String a) {
            question = q;
            answer = a;
        }
    }
    private final QuestionAnswer[] questions = {
            new QuestionAnswer("A Swiss teenager has made a fully functional submarine out of what?", "a pig trough"),
            new QuestionAnswer("What are 'Pooks'?","small piles of hay"),
            new QuestionAnswer("In San Francisco, California, it is illegal to dance...", "to the Star Spangled Banner"),
            new QuestionAnswer("What does F.E.F.O. an abbreviation of?", "Petrified Forest National Park"),
            new QuestionAnswer("Who was Gustav Vigeland?", "Norway's most famous sculptor, known for his giant sculpture park in Oslo"),
            new QuestionAnswer("Thigging", "Begging or borrowing"),
            new QuestionAnswer("Barrad", "Tall hat resembling a dunce cap"),
            new QuestionAnswer("Scrivello", "Tusk of an elephant"),
            new QuestionAnswer("Griddler", "Street musician who plays from sheet music"),
            new QuestionAnswer("Xylopolist", "Proprietor who deals in wooden products"),
            new QuestionAnswer("Chromonema", "a coiled or convoluted thread in prophase of mitosis; central thread in chromosome"),
            new QuestionAnswer("Sarplier", "A large sack of coarse canvas for wool; a sack or bale of wool containing eighty tods; also used as a measure of quantity for wool"),
            new QuestionAnswer("Prunt", "Piece of ornamental glass"),
            new QuestionAnswer("Uxorilocal", "Pertaining to a husband who lives with his wife's parents"),
            new QuestionAnswer("Tregetour", "Juggling magician"),
            new QuestionAnswer("Pelmatogram", "Footprint")
    };
    private int questionIndex = 0;
    private final List<QuestionAnswer> shuffledQuestions = new ArrayList<>();
    public QuestionManager() {
        for (QuestionAnswer qa: questions) {
            shuffledQuestions.add(qa);
        }
        Collections.shuffle(shuffledQuestions);
    }
    public QuestionAnswer getNext() {
        if (questionIndex >= questions.length) questionIndex = 0;
        return shuffledQuestions.get(questionIndex++);
    }
}
