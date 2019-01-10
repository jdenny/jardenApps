package quiz;

import org.junit.Test;

import jarden.quiz.ArithmeticQuiz;
import jarden.quiz.EndOfQuestionsException;
import jarden.quiz.Quiz;

/**
 * Created by john on 04/05/2016.
 */
public class QuizUnitTest {
    private final static String WORD_FILE_NAME = "raw/words.txt";
    private final static String CAPITALS_FILE_NAME = "raw/capitals.properties";

    @Test
    public void arithmeticQuizTest() {
        Quiz quiz = new ArithmeticQuiz();
        try {
            String question = quiz.getNextQuestion(1);
            assert(quiz.isCorrect(30) == Quiz.INCORRECT);
            assert(quiz.isCorrect(31) == Quiz.INCORRECT);
            assert(quiz.isCorrect(32) == Quiz.FAIL);
            System.out.println("end of test!");
        } catch (EndOfQuestionsException e) {
            e.printStackTrace();
        }
    }
}
