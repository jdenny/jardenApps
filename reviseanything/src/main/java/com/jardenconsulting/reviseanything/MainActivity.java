package com.jardenconsulting.reviseanything;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import jarden.quiz.QuestionAnswer;

/*
https://sites.google.com/site/amazequiz/home/problems/reviseit.txt
on moto g5, downloaded to file:///storage/emulated/0/Download/reviseit.txt

User notes:
to override question file, download a file called reviseit.txt

 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String quizFileName = "reviseit.txt";
    public final static String TAG = "MainActivity";
    private PresetQuiz quiz;
    private List<QuestionAnswer> questionAnswerList;

    private TextView quizTitle;
    private TextView questionTextView;
    private TextView answerTextView;
    private TextView statsTextView;
    private TextView statusTextView;
    private Button goButton;
    private Button correctButton;
    private Button incorrectButton;
    private ViewGroup selfMarkLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.quizTitle = (TextView) findViewById(R.id.quizTitle);
        this.questionTextView = (TextView) findViewById(R.id.questionTextView);
        this.answerTextView = (TextView) findViewById(R.id.answerTextView);
        this.statsTextView = (TextView) findViewById(R.id.statsTextView);
        this.statusTextView = (TextView) findViewById(R.id.statusTextView);
        this.goButton = (Button) findViewById(R.id.goButton);
        this.correctButton = (Button) findViewById(R.id.correctButton);
        this.incorrectButton = (Button) findViewById(R.id.incorrectButton);
        this.selfMarkLayout = (ViewGroup) findViewById(R.id.selfMarkLayout);
        this.selfMarkLayout.setVisibility(View.GONE);
        this.goButton.setOnClickListener(this);
        this.correctButton.setOnClickListener(this);
        this.incorrectButton.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            Log.d(TAG, "can NOT read external storage");
        } else {
            Log.d(TAG, "CAN read external storage");
        }
        this.quiz = getQuiz();
        quizTitle.setText(quiz.getHeading());
        questionAnswerList = this.quiz.getQuestionAnswerList();

        askQuestion();
    }
    /**
     * Create a Quiz class based on Q-A text file called <i>quizFileName</i>.
     * Get text file from downloads directory
     * If it doesn't exists, use file in res/raw
     */
    private PresetQuiz getQuiz() {
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
            InputStreamReader isr = new InputStreamReader(inputStream);
            return new PresetQuiz(isr);
        } catch (IOException ioe) {
            Log.e(TAG, "exception in getQuiz: " + ioe);
            return null;
        }
    }
    private void askQuestion() {
        try {
            String question = quiz.getNextQuestion(1);
            this.questionTextView.setText(question);
            this.answerTextView.setText("");
            showButtonLayout();
        } catch (EndOfQuestionsException e) {
            e.printStackTrace();
        }
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
        showButtonLayout();
        askQuestion();
    }

    private void goPressed() {
        this.answerTextView.setText(quiz.getCorrectAnswer());
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

    private void loadQuizzes() {
        // I've left this here in case we need to use a background thread
        Runnable runnable = new Runnable() {
            private String message = "end of loadQuizzes()";
            @Override
            public void run() {
                getQuiz();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
        new Thread(runnable).start();
    }

}
