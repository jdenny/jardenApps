package jarden.codswallop;

import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jarden.engspa.EngSpaUtils;
import jarden.quiz.EndOfQuestionsException;
import jarden.tcp.HttpClient;

/**
 * Created by john.denny@gmail.com on 12/01/2026.
 */
public class QuestionManager {
    private final String TAG = "QuestionManager";

    public static class QuestionAnswer {
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
    private final List<QuestionAnswer> questionList = new ArrayList<>();
    public QuestionManager(Resources resources) {
        int qaFileId = BuildConfig.DEBUG ? R.raw.test_questions : R.raw.questions;
        try (InputStream is =
                     resources.openRawResource(qaFileId)) {
            List<String> lines = EngSpaUtils.getLinesFromStream(is);
            getQuestionsFromStrings(lines);
            ExecutorService udpExecutor =
                    Executors.newSingleThreadExecutor();
            String uriStr =
                    "https://raw.githubusercontent.com/jdenny/jardenApps/refs/heads/master/questions.txt";
            udpExecutor.execute(() -> {
                try {
                    List<String> lines2 = new HttpClient().downloadQuestions(uriStr);
                    getQuestionsFromStrings(lines2);
                } catch (IOException | URISyntaxException e) {
                    Log.e(TAG,
                            String.valueOf(e));
                }
                Log.d(TAG, "loaded " + questionList.size() +
                        " questions from R.raw.questions & gitHub raw file");
            });
        } catch (IOException e) {
            Log.e(TAG, "Failed to load questions: " + e);
            throw new RuntimeException(e);
        }
    }
    private void getQuestionsFromStrings(List<String> lines) {
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
    }
    public QuestionAnswer getQuestionAnswer(int questionIndex) throws EndOfQuestionsException {
        if (questionIndex < questionList.size()) {
            return questionList.get(questionIndex);
        } else {
            throw new EndOfQuestionsException();
        }
    }
}
