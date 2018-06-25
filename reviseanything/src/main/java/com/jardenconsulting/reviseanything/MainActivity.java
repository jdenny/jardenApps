package com.jardenconsulting.reviseanything;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import jarden.quiz.ReviseItQuiz;

/*
https://sites.google.com/site/amazequiz/home/problems/reviseit.txt
on moto g5, downloaded to file:///storage/emulated/0/Download/reviseit.txt

User notes:
to override question file, download a file called reviseit.txt

TODO:
Add this activity (renamed ReviseItActivity) to hotbridge
1st pass (learn): go through mainList in order; save fails
    after 3 corrects from mainList, ask a fail
    when all mainList have been done, ask all fails
    add fail to end of failList, including repeat fails
    save, in preferences, where we are up to in currents, and which fails
2nd pass (revise): as above, but go through mainList in random order
    save last 3 questions, so don't repeat


 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String TAG = "MainActivity";
    private ReviseItQuiz reviseItQuiz;

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
        try {
            this.reviseItQuiz = new ReviseItQuiz(this);
            quizTitle.setText(reviseItQuiz.getHeading());
        } catch (IOException e) {
            String message = "unable to load quiz: " + e;
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }
        askQuestion();
    }
    private void askQuestion() {
        String question = reviseItQuiz.getNextQuestion(1);
        this.questionTextView.setText(question);
        this.answerTextView.setText("");
        showButtonLayout();
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
}
