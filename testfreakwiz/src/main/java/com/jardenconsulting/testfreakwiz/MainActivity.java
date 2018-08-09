package com.jardenconsulting.testfreakwiz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jarden.app.revisequiz.FreakWizFragment;
import jarden.quiz.PresetQuiz;

import static jarden.quiz.PresetQuiz.QuizMode.LEARN;


public class MainActivity extends AppCompatActivity implements FreakWizFragment.Quizable {
    public static final String TAG = "TestFreakWiz";
    private static final int[] notesResIds = {
            R.string.pythagoras
    };

    private PresetQuiz presetQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.quiz);
            this.presetQuiz = new PresetQuiz(new InputStreamReader(inputStream));
            this.presetQuiz.setQuizMode(LEARN);
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
