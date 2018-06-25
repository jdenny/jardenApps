package jarden.quiz;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by john.denny@gmail.com on 25/06/2018.
 */
public class ReviseItQuiz {
    public enum QuizMode {
        LEARN, REVISE
    }
    private final static int RECENTS_CT = 3;
    private final static int targetCorrectCt = 3;
    private final static String TAG = "ReviseItQuiz";

    private QuizMode quizMode = QuizMode.LEARN;
    private List<QuestionAnswer> questionAnswerList;
    private int qaListSize;
    private List<QuestionAnswer> failedList;
    private QuestionAnswer[] recents = new QuestionAnswer[RECENTS_CT];
    private QuestionAnswer currentQA;
    private int questionIndex = -1;
    private String heading;
    private int consecutiveCorrects = 0;

    public ReviseItQuiz(InputStream inputStream) throws IOException {
        this.failedList = new LinkedList<>();
        InputStreamReader isr = new InputStreamReader(inputStream);
        PresetQuiz presetQuiz = new PresetQuiz(isr);
        this.questionAnswerList = presetQuiz.getQuestionAnswerList();
        this.qaListSize = questionAnswerList.size();
        this.heading = presetQuiz.getHeading();
    }
    public String getHeading() {
        return this.heading;
    }
    public void setQuizMode(QuizMode quizMode) {
        this.quizMode = quizMode;
        questionIndex = -1;
    }
    public QuizMode getQuizMode() {
        return this.quizMode;
    }

    public String getCorrectAnswer() {
        return this.currentQA.getAnswer();
    }

    /**
     * if QuizMode.LEARN
     *    if no fails && end of currentQA: throw endOfQuestionsException
     *    if (3 consecutiveCorrects && fails) or end of current: get first fail
     *    get next currentQA
     * if QuizMode.REVISE
     *    if 3 consecutiveCorrects && fails: get first fail
     *    get random from current, but not recent
     * save last 3 questions, so don't repeat
     *
     * @return question string from current questionAnswer
     * @throws EndOfQuestionsException
     */
    public String getNextQuestion() throws EndOfQuestionsException {
        int failCt = getFailedCount();
        if (this.quizMode == QuizMode.LEARN) {
            int currentCt = getCurrentCount() - 1; // haven't incremented questionIndex yet!
            if (failCt == 0 && currentCt == 0) throw new EndOfQuestionsException();
            if ((this.consecutiveCorrects >= targetCorrectCt && failCt > 0) || currentCt == 0) {
                consecutiveCorrects = 0;
                this.currentQA = this.failedList.remove(0);
            } else {
                this.questionIndex++;
                this.currentQA = this.questionAnswerList.get(questionIndex);
            }
        } else { // must be REVISE mode
            if (this.consecutiveCorrects >= targetCorrectCt && failCt > 0) {
                consecutiveCorrects = 0;
                this.currentQA = this.failedList.remove(0);
            } else {
                this.currentQA = getRandomNRUQuestion();
            }
        }
        recents[0] = recents[1];
        recents[1] = recents[2];
        recents[2] = currentQA;
        return this.currentQA.getQuestion();
    }
    public int getQuestionIndex() {
        return questionIndex;
    }
    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public void setCorrect(boolean correct) {
        if (correct) {
            this.consecutiveCorrects++;
        } else {
            consecutiveCorrects = 0;
            failedList.add(this.currentQA);
        }
    }
    public int getCurrentCount() {
        return this.qaListSize - this.questionIndex;
    }
    public int getFailedCount() {
        return this.failedList.size();
    }

    private QuestionAnswer getRandomNRUQuestion() {
        int randomI = ThreadLocalRandom.current().nextInt(this.qaListSize);
        QuestionAnswer qa = this.questionAnswerList.get(randomI);
        for (int i = 0; isRecentQuestion(qa) && i < RECENTS_CT; i++) {
            if (++randomI >= this.qaListSize) randomI = 0;
            qa = this.questionAnswerList.get(randomI);
        }
        return qa;
    }
    private boolean isRecentQuestion(QuestionAnswer qa) {
        for (int i = 0; i < RECENTS_CT; i++) {
            if (qa.equals(recents[i])) return true;
        }
        return false;
    }
}
