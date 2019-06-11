package com.jardenconsulting.cardapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import jarden.app.dialog.IntegerDialog;
import jarden.app.revisequiz.FreakWizFragment;
import jarden.quiz.BridgeQuiz;

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
* replace calculated bids in deals with lookup based on reviseit.txt
    for each deal:
        get list of next bids (based on bidding so far)
        for each response: match hand against criteria of each
        add bid to bidList
        if raw, stop bidding!
* new bid search not working with competitive bids!
* perhaps hold bidList as QuestionAnswer array, then use adapter to
    display qa.question = qa.answer;
    see https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
* add new fit bids from google doc
* long press on response, then Wrong marks the original bid as wrong, not the current
* show bid sequence in columns
* Add to document: meaning of 3H/3S; comprehensive responses to fit
* Add levels, to PresetQuiz and here
    Level 2 (all following QA at level 2 until told otherwise)
* add spy glass icon for bid search
* mark all the raw bids
 */
public class ReviseQuizActivity extends AppCompatActivity
        implements FreakWizFragment.Quizable, IntegerDialog.IntValueListener {
    public static final String TAG = "ReviseIt";
    private static final String quizFileName = "reviseit.txt";
        // "reviseitmini.txt"; // ***also change name of resource file***
    private static final String QUESTION_INDEX_KEY = "questionIndexKey";
    private static final String TARGET_CORRECTS_KEY = "targetCorrectsKey";
    private static final String FAIL_INDICES_KEY = "failIndicesKey";
    private static final String LEARN_MODE_KEY = "learnModeKey";
    private static final int[] notesResIds = {
            R.string.autofit,
            R.string.compelled,
            R.string.disturbed,
            R.string.Double,
            R.string.guard,
            R.string.help,
            R.string.invitational_plus,
            R.string.keycard_ask,
            R.string.preempt,
            R.string.queen_ask,
            R.string.raw,
            R.string.relay,
            R.string.responses_to_relay,
            R.string.responses_to_1D,
            R.string.responses_to_1NT,
            R.string.sandpit,
            R.string.side_suit,
            R.string.skew,
            R.string.strong_fit,
            R.string.strong_or_skew,
            R.string.suit_setter,
            R.string.threeNT,
            R.string.to_play,
            R.string.two_choice,
            R.string.values_for_5,
            R.string.waiting
    };

    private BridgeQuiz bridgeQuiz;
    private boolean changingQuestionIndex;
    private FreakWizFragment freakWizFragment;
    private SharedPreferences sharedPreferences;
    private IntegerDialog integerDialog;
    // new way of calculating bid
    private boolean dataDrivenBidding = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            this.bridgeQuiz = new BridgeQuiz(new InputStreamReader(inputStream));
        } catch (IOException e) {
            showMessage("unable to load quiz: " + e);
            return;
        }
        this.sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        boolean learnMode = sharedPreferences.getBoolean(LEARN_MODE_KEY, true);
        int savedQuestionIndex = sharedPreferences.getInt(QUESTION_INDEX_KEY, -1);
        if (savedQuestionIndex > 0) {
            bridgeQuiz.setQuestionIndex(savedQuestionIndex);
        }
        int savedTargetsCorrect = sharedPreferences.getInt(TARGET_CORRECTS_KEY, -1);
        if (savedTargetsCorrect > 0) {
            bridgeQuiz.setTargetCorrectCt(savedTargetsCorrect);
        }
        String failIndexStr = sharedPreferences.getString(FAIL_INDICES_KEY, "");
        if (failIndexStr.length() > 0) {
            String[] failIndices = failIndexStr.split(",");
            bridgeQuiz.setFailIndices(failIndices);
        }
        setContentView(R.layout.activity_revise);
        FragmentManager fragmentManager =
                getSupportFragmentManager();
        this.freakWizFragment = (FreakWizFragment)
                fragmentManager.findFragmentById(R.id.freakWizFragment);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            showMessage("can NOT read external storage");
        } else {
            if (BuildConfig.DEBUG) Log.d(TAG, "CAN read external storage");
        }
        if (learnMode) {
            setLearnMode();
        } else {
            setPracticeMode();
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
        int id = item.getItemId();
        if (id == R.id.learnModeButton) {
            setLearnMode();
            this.freakWizFragment.askQuestion();
        } else if (id == R.id.practiceModeButton) {
            setPracticeMode();
            this.freakWizFragment.askQuestion();
        } else if (id == R.id.setCurrentIndexButton) {
            // if we add more ints for the user to update, then
            // change this boolean to an enum
            changingQuestionIndex = true;
            if (this.integerDialog == null) {
                this.integerDialog = new IntegerDialog();
            }
            this.integerDialog.setTitle("Change Current Index");
            this.integerDialog.setIntValue(bridgeQuiz.getQuestionIndex() + 1);
            this.integerDialog.show(getSupportFragmentManager(), "UserLevelDialog");
        } else if (id == R.id.setTargetCorrectsButton) {
            changingQuestionIndex = false;
            if (this.integerDialog == null) {
                this.integerDialog = new IntegerDialog();
            }
            this.integerDialog.setTitle("Change Target Corrects");
            this.integerDialog.setIntValue(bridgeQuiz.getTargetCorrectCt());
            this.integerDialog.show(getSupportFragmentManager(), "TargetCorrectsDialog");
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) Log.d(TAG, "ReviseQuizActivity.onPause()");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int questionIndex = bridgeQuiz.getQuestionIndex();
        editor.putInt(QUESTION_INDEX_KEY, questionIndex);
        int targetCorrectCt = bridgeQuiz.getTargetCorrectCt();
        editor.putInt(TARGET_CORRECTS_KEY, targetCorrectCt);
        BridgeQuiz.QuizMode quizMode = bridgeQuiz.getQuizMode();
        editor.putBoolean(LEARN_MODE_KEY, quizMode == LEARN);
        Set<Integer> failedIndexSet = bridgeQuiz.getFailedIndexSet();
        String failStr;
        if (failedIndexSet.size() == 0) {
            failStr = "";
        } else {
            StringBuilder sBuilder = new StringBuilder();
            for (int failIndex : failedIndexSet) {
                sBuilder.append(failIndex).append(",");
            }
            failStr = sBuilder.substring(0, sBuilder.length() - 1);
        }
        editor.putString(FAIL_INDICES_KEY, failStr);
        editor.apply();
    }
    @Override // Quizable
    public BridgeQuiz getBridgeQuiz() {
        return this.bridgeQuiz;
    }

    @Override // Quizable
    public int[] getNotesResIds() {
        return notesResIds;
    }

    @Override // IntValueListener
    public void onUpdateIntValue(int intValue) {
        if (changingQuestionIndex) {
            // to people, ordinals start from 1; to computers, they start from 0
            this.bridgeQuiz.setQuestionIndex(intValue - 1);
            this.freakWizFragment.askQuestion();
        } else {
            this.bridgeQuiz.setTargetCorrectCt(intValue);
        }
    }
    private void setLearnMode() {
        bridgeQuiz.setQuizMode(LEARN);
        setTitle(R.string.learnMode);
    }
    private void setPracticeMode() {
        bridgeQuiz.setQuizMode(PRACTICE);
        setTitle(R.string.practiceMode);
    }
    private void showMessage(String message) {
        if (BuildConfig.DEBUG) Log.d(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
