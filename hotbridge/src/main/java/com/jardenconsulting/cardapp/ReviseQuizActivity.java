package com.jardenconsulting.cardapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import jarden.quiz.EndOfQuestionsException;
import jarden.quiz.PresetQuiz;

import static jarden.quiz.PresetQuiz.QuizMode.LEARN;
import static jarden.quiz.PresetQuiz.QuizMode.REVISE;

/**
 * Create a Quiz class based on Q-A text file called <i>quizFileName</i>.
 * Get text file from downloads directory; this allows a user of the app
 * to supply their own Q-A file, called <i>quizFileName</i>, and download
 * it onto their device.
 * If it doesn't exists, we use file in res/raw
 */
/*
https://sites.google.com/site/amazequiz/home/problems/reviseit.txt
on moto g5, downloaded to file:///storage/emulated/0/Download/reviseit.txt

TODO:
This activity needs to be in a
library if we want hotbridge and a separate FunkWiz app
Name options: Freak Wiz, york, mike, dunk, punk, trike, funk
save fails in preferences; how about save fails and currentIndex when closing app
and reload on restart? Hold failList as indices, to make it easier to save?
 */
public class ReviseQuizActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String TAG = "ReviseIt";
    private final static String quizFileName = "reviseit.txt";
        // "reviseitmini.txt"; // ***also change name of resource file***
    private final static String QUESTION_INDEX_KEY = "questionIndexKey";
    private final static String FAIL_INDICES_KEY = "failIndicesKey";
    private static final String LEARN_MODE_KEY = "learnModeKey";

    private PresetQuiz reviseItQuiz;
    private TextView questionTextView;
    private TextView answerTextView;
    private TextView statsTextView;
    private TextView statusTextView;
    private Button goButton;
    private ViewGroup selfMarkLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise);
        TextView quizTitle = findViewById(R.id.quizTitle);
        this.questionTextView = findViewById(R.id.questionTextView);
        this.answerTextView = findViewById(R.id.answerTextView);
        this.statsTextView = findViewById(R.id.statsTextView);
        this.statusTextView = findViewById(R.id.statusTextView);
        this.goButton = findViewById(R.id.goButton);
        Button correctButton = findViewById(R.id.correctButton);
        Button incorrectButton = findViewById(R.id.incorrectButton);
        this.selfMarkLayout =  findViewById(R.id.selfMarkLayout);
        this.selfMarkLayout.setVisibility(View.GONE);
        this.goButton.setOnClickListener(this);
        correctButton.setOnClickListener(this);
        incorrectButton.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            Log.d(TAG, "can NOT read external storage");
        } else {
            Log.d(TAG, "CAN read external storage");
        }
        try {
            File publicDirectory = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File file = new File(publicDirectory, quizFileName);
            Log.d(TAG, file.getAbsolutePath());
            InputStream inputStream;
            if (file.canRead()) {
                inputStream = new FileInputStream(file);
            } else {
                inputStream = getResources().openRawResource(R.raw.reviseit);
            }
            this.reviseItQuiz = new PresetQuiz(new InputStreamReader(inputStream));
            quizTitle.setText(reviseItQuiz.getHeading());
            this.sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
            int savedQuestionIndex = sharedPreferences.getInt(QUESTION_INDEX_KEY, -1);
            if (savedQuestionIndex > -1) {
                reviseItQuiz.setQuestionIndex(savedQuestionIndex);
            }
            boolean learnMode = sharedPreferences.getBoolean(LEARN_MODE_KEY, true);
            reviseItQuiz.setQuizMode(learnMode ? LEARN : REVISE);
            String failIndexStr = sharedPreferences.getString(FAIL_INDICES_KEY, "");
            if (failIndexStr.length() > 0) {
                String[] failIndices = failIndexStr.split(",");
                reviseItQuiz.setFailIndices(failIndices);
            }
            setTitle(R.string.learnMode);
            askQuestion();
        } catch (IOException e) {
            showMessage("unable to load quiz: " + e);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.revise, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();
        if (id == R.id.learnModeButton) {
            reviseItQuiz.setQuizMode(LEARN);
            setTitle(R.string.learnStr);
        } else if (id == R.id.reviseModeButton) {
            reviseItQuiz.setQuizMode(REVISE);
            setTitle(R.string.reviseStr);
        } else {
            return super.onOptionsItemSelected(item);
        }
        askQuestion();
        return true;
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int questionIndex = reviseItQuiz.getQuestionIndex();
        editor.putInt(QUESTION_INDEX_KEY, questionIndex);
        PresetQuiz.QuizMode quizMode = reviseItQuiz.getQuizMode();
        editor.putBoolean(LEARN_MODE_KEY, quizMode == LEARN);
        List<Integer> failList = reviseItQuiz.getFailedIndexList();
        String failStr;
        if (failList.size() == 0) {
            failStr = "";
        } else {
            StringBuilder sBuilder = new StringBuilder();
            for (int failIndex : failList) {
                sBuilder.append(failIndex + ",");
            }
            failStr = sBuilder.substring(0, sBuilder.length() - 1);
        }
        editor.putString(FAIL_INDICES_KEY, failStr);
        editor.apply();
    }
    private void askQuestion() {
        String question;
        try {
            question = reviseItQuiz.getNextQuestion(1);
        } catch (EndOfQuestionsException e) {
            showMessage("end of questions! starting revise mode");
            reviseItQuiz.setQuizMode(REVISE);
            setTitle(R.string.reviseStr);
            try {
                question = reviseItQuiz.getNextQuestion(1);
            } catch (EndOfQuestionsException e1) {
                showMessage("exception: " + e);
                return;
            }
        }
        this.questionTextView.setText(question);
        this.answerTextView.setText("");
        showButtonLayout();
        showStats();
    }
    private void showStats() {
        String stats = "Current=" +
                reviseItQuiz.getCurrentCount() +
                " Fails=" +
                reviseItQuiz.getFailedCount();
        this.statsTextView.setText(stats);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.goButton) {
            goPressed();
        } else if (id == R.id.correctButton) {
            selfMarkButton(true);
        } else if (id == R.id.incorrectButton) {
            selfMarkButton(false);
        }
    }

    private void selfMarkButton(boolean isCorrect) {
        this.reviseItQuiz.setCorrect(isCorrect);
        showButtonLayout();
        askQuestion();
    }

    private void goPressed() {
        this.answerTextView.setText(reviseItQuiz.getCorrectAnswer());
        showSelfMarkLayout();
    }

    private void showSelfMarkLayout() {
        this.goButton.setVisibility(View.GONE);
        this.selfMarkLayout.setVisibility(View.VISIBLE);
    }
    private void showButtonLayout() {
        this.selfMarkLayout.setVisibility(View.GONE);
        this.goButton.setVisibility(View.VISIBLE);
    }
    private void showMessage(String message) {
        Log.d(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
