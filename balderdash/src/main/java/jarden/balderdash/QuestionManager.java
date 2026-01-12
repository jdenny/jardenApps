package jarden.balderdash;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jarden.engspa.EngSpaUtils;

/**
 * Created by john.denny@gmail.com on 12/01/2026.
 */
public class QuestionManager {
    private final String TAG = "QuestionManager";
    private final Context context;

    public class QuestionAnswer {
        public String question;
        public String answer;
        QuestionAnswer(String q, String a) {
            question = q;
            answer = a;
        }
    }
    private int questionIndex = 0;
    private final List<QuestionAnswer> shuffledQuestions = new ArrayList<>();
    public QuestionManager(Context context) {
        this.context = context;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.questions);
            List<String> lines = EngSpaUtils.getLinesFromStream(is);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] qa = line.split("\\|");
                shuffledQuestions.add(new QuestionAnswer(qa[0], qa[1]));
                Log.d(TAG, "loaded " + lines.size() + " questions from R.raw.questions");
            }
        } catch (IOException e) {
            Log.e(TAG, "exception: " + e);
            throw new RuntimeException(e);
        }
        Collections.shuffle(shuffledQuestions);
    }
    public QuestionAnswer getNext() {
        if (questionIndex >= shuffledQuestions.size()) questionIndex = 0;
        return shuffledQuestions.get(questionIndex++);
    }
}
