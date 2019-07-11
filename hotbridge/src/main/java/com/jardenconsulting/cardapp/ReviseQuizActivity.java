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
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import jarden.app.revisequiz.FreakWizFragment;
import jarden.app.revisequiz.ReviseQuizFragment;
import jarden.quiz.BridgeQuiz;
import jarden.quiz.PresetQuiz;

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
        implements FreakWizFragment.Quizable, ReviseQuizFragment.Reviseable {
    public static final String TAG = "ReviseIt";
    private static final String quizFileName = "reviseit.txt";
        // "reviseitmini.txt"; // ***also change name of resource file***
    private static final String QUESTION_INDEX_KEY = "questionIndexKey";
    private static final String TARGET_CORRECTS_KEY = "targetCorrectsKey";
    private static final String FAIL_INDICES_KEY = "failIndicesKey";
    private static final String LEARN_MODE_KEY = "learnModeKey";
    private static final int[] notesResIds = {
            R.string.autofit,
            R.string.balanced,
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
    private FreakWizFragment freakWizFragment;
    private SharedPreferences sharedPreferences;

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
            this.bridgeQuiz = BridgeQuiz.getInstance(new InputStreamReader(inputStream));
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
    public PresetQuiz getReviseQuiz() {
        return this.bridgeQuiz;
    }

    @Override // Quizable
    public int[] getNotesResIds() {
        return notesResIds;
    }
    @Override // Reviseable
    public void setLearnMode() {
        bridgeQuiz.setQuizMode(LEARN);
        setTitle(R.string.learnMode);
    }
    @Override // Reviseable
    public void setPracticeMode() {
        bridgeQuiz.setQuizMode(PRACTICE);
        setTitle(R.string.practiceMode);
    }
    private void showMessage(String message) {
        if (BuildConfig.DEBUG) Log.d(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
