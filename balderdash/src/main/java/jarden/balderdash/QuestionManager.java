package jarden.balderdash;

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
    public QuestionManager() {

    }
    private final QuestionAnswer[] questions = {
            new QuestionAnswer("A Swiss teenager has made a fully functional submarine out of... what?", "a pig trough"),
            new QuestionAnswer("What are 'Pooks'?","small piles of hay"),
            new QuestionAnswer("In San Francisco, California, it is illegal to dance...", "to the Star Spangled Banner"),
            new QuestionAnswer("What does F.E.F.O. an abbreviation of?", "Petrified Forest National Park"),
            new QuestionAnswer("Who was Gustav Vigeland?", "Norway's most famous sculptor, known for his giant sculpture park in Oslo")
    };
    private int questionIndex = 0;
    public QuestionAnswer getNext() {
        // temporary until we get a proper database!
        if (questionIndex >= questions.length) questionIndex = 0;
        return questions[questionIndex++];
    }
}
