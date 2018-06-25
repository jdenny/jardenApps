package jarden.quiz;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.jardenconsulting.reviseanything.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by john.denny@gmail.com on 25/06/2018.
 */
public class ReviseItQuiz {
    private String quizFileName = "reviseit.txt";
    public final static String TAG = "ReviseItQuiz";
    private List<QuestionAnswer> questionAnswerList;
    private int questionIndex = -1;
    private String heading;

    /**
     * Create a Quiz class based on Q-A text file called <i>quizFileName</i>.
     * Get text file from downloads directory
     * If it doesn't exists, use file in res/raw
     */
    public ReviseItQuiz(Activity activity) throws IOException {
        File publicDirectory = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File file = new File(publicDirectory, quizFileName);
        Log.d(TAG, file.getAbsolutePath());
        InputStream inputStream;
        if (file.canRead()) {
            inputStream = new FileInputStream(file);
        } else {
            inputStream = activity.getResources().openRawResource(R.raw.reviseit);
        }
        InputStreamReader isr = new InputStreamReader(inputStream);
        PresetQuiz presetQuiz = new PresetQuiz(isr);
        this.questionAnswerList = presetQuiz.getQuestionAnswerList();
        this.heading = presetQuiz.getHeading();
    }
    public String getHeading() {
        return this.heading;
    }

    public String getCorrectAnswer() {
        return this.questionAnswerList.get(questionIndex).getAnswer();
    }

    public String getNextQuestion(int level) {
        this.questionIndex++;
        if (questionIndex >= this.questionAnswerList.size()) {
            questionIndex = 0;
        }
        return this.questionAnswerList.get(questionIndex).getQuestion();
    }
}
