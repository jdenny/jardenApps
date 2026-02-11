package jarden.codswallop;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jarden.engspa.EngSpaUtils;
import jarden.quiz.EndOfQuestionsException;

/**
 * Created by john.denny@gmail.com on 12/01/2026.
 */
public class QuestionManager {
    private final String TAG = "QuestionManager";
    private final Context context;

    public class QuestionAnswer {
        public String type;
        public String question;
        public String answer;
        public String comment;
        QuestionAnswer(String t, String q, String a) {
            type = t;
            question = q;
            answer = a;
        }
        QuestionAnswer(String t, String q, String a, String c) {
            this(t, q, a);
            comment = c;
        }
    }
    private int questionIndex = 0;
    private final List<QuestionAnswer> questionList = new ArrayList<>();
    public QuestionManager(Context context) {
        this.context = context;
        int qaFileId = BuildConfig.DEBUG ? R.raw.test_questions : R.raw.questions;
        try (InputStream is =
                     context.getResources().openRawResource(qaFileId)) {
            List<String> lines = EngSpaUtils.getLinesFromStream(is);
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    String[] qa = line.split("\\|");
                    if (qa.length == 3) {
                        questionList.add(new QuestionAnswer(qa[0].trim(), qa[1].trim(),
                                qa[2].trim()));
                    } else if (qa.length == 4) {
                        questionList.add(new QuestionAnswer(qa[0].trim(), qa[1].trim(),
                                qa[2].trim(), qa[3].trim()));

                    } else {
                        Log.w(TAG, "Skipping malformed line: " + line);
                    }
                }
            }
            Log.d(TAG, "loaded " + questionList.size() + " questions from R.raw.questions");
        } catch (IOException e) {
            Log.e(TAG, "Failed to load questions: " + e);
            throw new RuntimeException(e);
        }
    }
    public QuestionAnswer getNext(int questionIndex) throws EndOfQuestionsException {
        if (questionIndex < questionList.size()) {
            return questionList.get(questionIndex);
        } else {
            throw new EndOfQuestionsException();
        }
    }
}
