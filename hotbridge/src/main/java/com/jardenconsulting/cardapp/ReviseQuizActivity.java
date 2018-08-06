package com.jardenconsulting.cardapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import jarden.document.DocumentTextView;
import jarden.quiz.EndOfQuestionsException;
import jarden.quiz.PresetQuiz;
import jarden.quiz.QuestionAnswer;

import static jarden.quiz.PresetQuiz.QuizMode.LEARN;
import static jarden.quiz.PresetQuiz.QuizMode.PRACTICE;

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
try to fix red cards on Julie's phone
get rid of "what does the last bid mean??
merge takeout_double and redouble into double
Add levels, to PresetQuiz and here
    Level 2 (all following QA at level 2 until told otherwise)

You type in a bid, e.g. 1C, it searches for Q: 1C and shows the answer, plus
a list of all the next bids! (i.e. search for Q: 1C, [something!] regular expression)
choose from list, and repeats from above
    add to menu Lookup mode
    list all single bids
    user clicks item in list; Q goes into blue, A into red, next bids into list
    buttons: back, quiz, more (shows F1 stuff, changes to 'less')

This activity needs to be in a
library if we want hotbridge and a separate FunkWiz app
Name options: Freak Wiz, york, mike, dunk, punk, trike, funk
 */
public class ReviseQuizActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    public final static String TAG = "ReviseIt";
    private final static String quizFileName = "reviseit.txt";
        // "reviseitmini.txt"; // ***also change name of resource file***
    private final static String QUESTION_INDEX_KEY = "questionIndexKey";
    private final static String FAIL_INDICES_KEY = "failIndicesKey";
    private static final String LEARN_MODE_KEY = "learnModeKey";
    private static final int[] helpResIds = {
            R.string.autofit,
            R.string.compelled,
            R.string.disturbed,
            R.string.guard,
            R.string.invitational_plus,
            R.string.keycard_ask,
            R.string.preempt,
            R.string.queen_ask,
            R.string.raw,
            R.string.redouble,
            R.string.relay,
            R.string.responses_to_1D,
            R.string.responses_to_1NT,
            R.string.sandpit,
            R.string.skew,
            R.string.splinter,
            R.string.strong_fit,
            R.string.suit_setter,
            R.string.takeout_double,
            R.string.to_play,
            R.string.two_choice,
            R.string.values_for_5,
            R.string.waiting_bid
    };

    private PresetQuiz reviseItQuiz;
    private List<QuestionAnswer> qaList;
    private List<QuestionAnswer> currentQAList;
    private TextView questionTextView;
    private TextView answerTextView;
    private TextView statsTextView;
    private TextView helpTextView;
    private Button goButton;
    private ViewGroup selfMarkLayout;
    private ViewGroup bidSearchLayout;
    private ViewGroup quizLayout;
    private SharedPreferences sharedPreferences;
    private DocumentTextView documentTextView;
    private ListView bidListView;
    private ArrayAdapter<String> bidListAdapter;
    private Stack<QuestionAnswer> bidSearchStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise);
        this.questionTextView = findViewById(R.id.questionTextView);
        this.answerTextView = findViewById(R.id.answerTextView);
        this.statsTextView = findViewById(R.id.statsTextView);
        this.helpTextView = findViewById(R.id.helpTextView);
        this.helpTextView.setMovementMethod(LinkMovementMethod.getInstance());
        this.helpTextView.setHighlightColor(Color.TRANSPARENT);
        this.goButton = findViewById(R.id.goButton);
        Button correctButton = findViewById(R.id.correctButton);
        Button incorrectButton = findViewById(R.id.incorrectButton);
        Button quizButton = findViewById(R.id.quizButton);
        Button backButton = findViewById(R.id.backButton);
        this.selfMarkLayout =  findViewById(R.id.selfMarkLayout);
        this.quizLayout = findViewById(R.id.quizLayout);
        this.selfMarkLayout.setVisibility(View.GONE);
        this.bidSearchLayout = findViewById(R.id.bidSearchLayout);
        this.bidSearchLayout.setVisibility(View.GONE);
        this.goButton.setOnClickListener(this);
        quizButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        correctButton.setOnClickListener(this);
        incorrectButton.setOnClickListener(this);
        this.bidListAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1);
        this.bidListView = findViewById(R.id.bidListView);
        this.bidListView.setAdapter(bidListAdapter);
        this.bidListView.setOnItemClickListener(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            showMessage("can NOT read external storage");
        } else {
            if (BuildConfig.DEBUG) Log.d(TAG, "CAN read external storage");
        }
        try {
            File publicDirectory = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File file = new File(publicDirectory, quizFileName);
            if (BuildConfig.DEBUG) Log.d(TAG, file.getAbsolutePath());
            InputStream inputStream;
            if (file.canRead()) {
                inputStream = new FileInputStream(file);
            } else {
                inputStream = getResources().openRawResource(R.raw.reviseit);
            }
            this.reviseItQuiz = new PresetQuiz(new InputStreamReader(inputStream));
            this.sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
            boolean learnMode = sharedPreferences.getBoolean(LEARN_MODE_KEY, true);
            if (learnMode) {
                setLearnMode();
            } else {
                setPracticeMode();
            }
            int savedQuestionIndex = sharedPreferences.getInt(QUESTION_INDEX_KEY, -1);
            if (savedQuestionIndex > 0) {
                // subtract 1, to repeat most recent question, not yet answered:
                reviseItQuiz.setQuestionIndex(savedQuestionIndex - 1);
            }
            String failIndexStr = sharedPreferences.getString(FAIL_INDICES_KEY, "");
            if (failIndexStr.length() > 0) {
                String[] failIndices = failIndexStr.split(",");
                reviseItQuiz.setFailIndices(failIndices);
            }
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
            setLearnMode();
        } else if (id == R.id.practiceModeButton) {
            setPracticeMode();
        } else if (id == R.id.bidSearchButton) {
            this.bidSearchLayout.setVisibility(View.VISIBLE);
            this.quizLayout.setVisibility(View.GONE);
            loadBidList(null);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
        askQuestion();
        return true;
    }
    private void setLearnMode() {
        reviseItQuiz.setQuizMode(LEARN);
        setTitle(R.string.learnMode);
    }
    private void setPracticeMode() {
        reviseItQuiz.setQuizMode(PRACTICE);
        setTitle(R.string.practiceMode);
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
            showMessage("end of questions! starting practice mode");
            setPracticeMode();
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
        } else if (id == R.id.quizButton) {
            backToQuiz();
        } else if (id == R.id.backButton) {
            if (bidSearchStack.empty()) backToQuiz();
            else {
                bidSearchStack.pop();
                if (bidSearchStack.empty()) backToQuiz();
                else showBidAndResponses(bidSearchStack.peek());
            }
        }
    }
    private void backToQuiz() {
        this.bidSearchLayout.setVisibility(View.GONE);
        this.quizLayout.setVisibility(View.VISIBLE);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "position=" + position);
        QuestionAnswer qa = this.currentQAList.get(position);
        this.bidSearchStack.add(qa);
        showBidAndResponses(qa);
    }
    private void showBidAndResponses(QuestionAnswer qa) {
        this.questionTextView.setText(qa.question);
        this.answerTextView.setText(qa.answer);
        loadBidList(qa.question);
    }

    private void selfMarkButton(boolean isCorrect) {
        this.reviseItQuiz.setCorrect(isCorrect);
        showButtonLayout();
        askQuestion();
        helpTextView.setText("");
    }

    private void goPressed() {
        QuestionAnswer currentQA = reviseItQuiz.getCurrentQuestionAnswer();
        this.answerTextView.setText(currentQA.answer);
        getDocumentTextView().showPageText(currentQA.helpText, "Notes");
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
        if (BuildConfig.DEBUG) Log.d(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    private DocumentTextView getDocumentTextView() {
        if (this.documentTextView == null) {
            this.documentTextView = new DocumentTextView(
                    getApplicationContext(),
                    helpTextView, helpResIds,
                    true, null, false);
        }
        return documentTextView;
    }
    /*
    Get all responses to supplied bid. If bid is null, get all opening bids.
     */
    private void loadBidList(String bid) {
        if (this.qaList == null) {
            qaList = reviseItQuiz.getQuestionAnswerList();
            this.currentQAList = new ArrayList<>();
            this.bidSearchStack = new Stack<>();
        } else {
            this.currentQAList.clear();
        }
        this.bidListAdapter.setNotifyOnChange(false);
        this.bidListAdapter.clear();
        for (QuestionAnswer qa: qaList) {
            String question = qa.question;
            if (isResponseToBid(bid, question)) {
                bidListAdapter.add(question);
                currentQAList.add(qa);
            }
        }
        this.bidListAdapter.notifyDataSetChanged();
    }
    private boolean isResponseToBid(String bid, String question) {
        if (bid == null) { // getting only opening bids:
            return !question.contains(",");
        } else {
            if (question.startsWith(bid + ", ") || question.startsWith(bid + "; ")) {
                int pos = bid.length() + 2;
                return !(question.substring(pos).contains(",") ||
                        question.substring(pos).contains(";"));
            } else return false;
        }
    }
}
