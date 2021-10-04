package com.jardenconsulting.testfreakwiz;

import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jarden.app.revisequiz.FreakWizFragment;
import jarden.quiz.PresetQuiz;


/**
 * Create a Quiz class based on Q-A text file called <i>quizFileName</i>.
 * Get text file from downloads directory; this allows a user of the app
 * to supply their own Q-A file, called <i>quizFileName</i>, and download
 * it onto their device.
 * If it doesn't exists, we use file in res/raw
 *
 * https://sites.google.com/site/amazequiz/home/problems/reviseit.txt
 * on moto g5, downloaded to file:///storage/emulated/0/Download/reviseit.txt
 */
public class MainActivity extends AppCompatActivity implements FreakWizFragment.Quizable {
    public static final String TAG = "TestFreakWiz";
    private static final String quizFileName = "quiz.txt";

    private static final int[] notesResIds = {
            R.string.Philippians4_6
    };

    private PresetQuiz presetQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // see if there is a file <quizFileName> in device downloads folder,
            // and if so use that; otherwise use res/raw/quiz.txt
            File publicDirectory = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File file = new File(publicDirectory, quizFileName);
            if (BuildConfig.DEBUG) Log.d(TAG, file.getAbsolutePath());
            InputStream inputStream;
            if (file.canRead()) {
                inputStream = new FileInputStream(file);
            } else {
                inputStream = getResources().openRawResource(R.raw.quiz);
            }
            this.presetQuiz = new PresetQuiz(new InputStreamReader(inputStream));
            //!! this.presetQuiz.setQuizMode(LEARN);
            presetQuiz.setLearnMode(true);
        } catch (IOException e) {
            showMessage("unable to load quiz: " + e);
            return;
        }
        setContentView(R.layout.activity_main);
    }
    private void showMessage(String message) {
        if (BuildConfig.DEBUG) Log.d(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public PresetQuiz getReviseQuiz() {
        return presetQuiz;
    }

    @Override
    public int[] getNotesResIds() {
        return notesResIds;
    }
}
