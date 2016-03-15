package com.jardenconsulting.spanishapp;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.Locale;

import jarden.engspa.EngSpaDAO;
import jarden.engspa.EngSpaUser;
import jarden.provider.engspa.EngSpaContract;

/**
 * Created by john.denny@gmail.com on 10/03/2016.
 */
public class ViewlessFragment extends Fragment implements TextToSpeech.OnInitListener {
    private static final String TAG = "ViewlessFragment";
    private static final Locale LOCALE_ES = new Locale("es", "ES");
    private static final long[] WRONG_VIBRATE = {
            0, 200, 200, 200
    };
    private static final long[] LOST_VIBRATE = {
            0, 400, 400, 400, 400, 400
    };

    // used to check if pause has been caused by screen rotation,
    // and if so we won't turn off textToSpeech
    private int orientation;
    private EngSpaActivity engSpaActivity;
    private TextToSpeech textToSpeech;
    private String spanish; // word to be spoken
    private Vibrator vibrator;
    private SoundPool soundPool;
    private int soundError;
    private int soundLost;
    private EngSpaDAO engSpaDAO;
    private EngSpaUser engSpaUser;


    @Override // Fragment
    public void onAttach(Context context) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onAttach()");
        super.onAttach(context);
    }
    @SuppressWarnings("deprecation")
    @Override // Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate(" +
                (savedInstanceState == null ? "" : "not ") + "null)");
        setRetainInstance(true);
        this.vibrator = (Vibrator) getActivity().getSystemService(
                FragmentActivity.VIBRATOR_SERVICE);
		// requires API 21 or above:
//		AudioAttributes audioAttributes = new AudioAttributes.Builder()
//				.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//				.setUsage(AudioAttributes.USAGE_GAME)
//				.build();
//		this.soundPool = new SoundPool.Builder()
//				.setMaxStreams(2)
//				.setAudioAttributes(audioAttributes)
//				.build();
//		Activity activity = getActivity();
//		this.soundError = soundPool.load(activity, R.raw.error, 1);
//		this.soundLost = soundPool.load(activity, R.raw.lost, 1);
        this.soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        Activity activity = getActivity();
        this.engSpaActivity = (EngSpaActivity) activity;
        this.soundError = soundPool.load(activity, R.raw.error, 1);
        this.soundLost = soundPool.load(activity, R.raw.lost, 1);
        this.engSpaDAO = engSpaActivity.getEngSpaDAO();
        this.engSpaUser = engSpaDAO.getUser();
        if (this.engSpaUser == null) { // i.e. no user yet on database
            this.engSpaUser = new EngSpaUser("your name",
                    1, EngSpaContract.QAStyle.writtenSpaToEng);
            engSpaDAO.insertUser(engSpaUser);
        }
        saveOrientation();
    }

    @Override // Fragment
    public void onResume() {
        if (BuildConfig.DEBUG) Log.d(TAG, "onResume(); textToSpeech is " +
                (textToSpeech == null ? "" : "not ") + "null");
        super.onResume();
    }
    @Override // Fragment
    public void onPause() {
        boolean orientationChanged = isOrientationChanged();
        super.onPause();
        if (!orientationChanged &&
                this.textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
            if (BuildConfig.DEBUG) Log.d(TAG,
                    "onPause(); textToSpeech closed");
        }
    }
    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }
    @Override // OnInitListener (called when textToSpeech is initialised)
    public void onInit(int status) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onInit()");
        engSpaActivity.setProgressBarVisible(false);
        if (status == TextToSpeech.SUCCESS) {
            if (this.textToSpeech == null) {
                // this could happen if activity is paused between creating
                // new textToSpeech and getting the response back here
                engSpaActivity.setStatus(R.string.ttsClosed);
                return;
            }
            int result = textToSpeech.setLanguage(LOCALE_ES);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "textToSpeech.setLanguage(); result=" + result);
            }
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // this.statusTextView.setText("TextToSpeech for Spanish is not supported");
                engSpaActivity.setStatus(R.string.ttsNotSupported);
            } else {
                engSpaActivity.setStatus("");
            }
            if (this.spanish != null) speakSpanish2();
        } else {
            Log.w(TAG, "onInit(" + status + ")");
            engSpaActivity.setStatus(R.string.ttsFailed);
        }
    }
    // return true if orientation changed since previous call
    private boolean isOrientationChanged() {
        int oldOrientation = this.orientation;
        saveOrientation();
        if (BuildConfig.DEBUG) Log.d(TAG,
                "getOrientation(); orientation was: " +
                        oldOrientation + ", is: " + this.orientation);
        return this.orientation != oldOrientation;
    }
    private void saveOrientation() {
        this.orientation = getResources().getConfiguration().orientation;
    }
    /**
     * Create or update engSpaUser.
     * @return true if level changed
     */
    public boolean setUser(String userName, int userLevel,
                           EngSpaContract.QAStyle qaStyle) {
        userLevel = this.engSpaDAO.validateUserLevel(userLevel);
        if (engSpaUser != null &&
                engSpaUser.getUserName().equals(userName) &&
                engSpaUser.getUserLevel() == userLevel &&
                engSpaUser.getQAStyle() == qaStyle) {
            engSpaActivity.setStatus(R.string.userNotChanged);
            return false;
        }
        boolean isNewLevel = true;
        if (engSpaUser == null) { // i.e. new user
            this.engSpaUser = new EngSpaUser(userName,
                    userLevel, qaStyle);
            engSpaDAO.insertUser(engSpaUser);
        } else { // update to existing user
            isNewLevel = engSpaUser.getUserLevel() != userLevel;
            engSpaUser.setUserName(userName);
            engSpaUser.setUserLevel(userLevel);
            engSpaUser.setQAStyle(qaStyle);
            engSpaDAO.updateUser(engSpaUser);
        }
        return isNewLevel;
    }
    public EngSpaUser getEngSpaUser() {
        return this.engSpaUser;
    }
    public void setSpanish(String spanish) {
        this.spanish = spanish;
    }
    public void speakSpanish(String spanish) {
        setSpanish(spanish);
        speakSpanish();
    }
    /**
     * Ensure textToSpeech is initialised, then speak the
     * current Spanish word, set by setSpanish(String spanish)
     * or speakSpanish(String spanish).
     * @return false if no Spanish word set, else true
     */
    public boolean speakSpanish() {
        if (this.spanish == null) return false;
        if (this.textToSpeech == null) {
            // invokes onInit() on completion
            textToSpeech = new TextToSpeech(getActivity().getApplicationContext(), this);
            engSpaActivity.setStatus(R.string.ttsLoading);
            engSpaActivity.setProgressBarVisible(true);
        } else {
            speakSpanish2();
        }
        return true;
    }
    /**
     * Part 2 of speakSpanish, invoked when textToSpeech initialised.
     */
    @SuppressWarnings("deprecation")
    private void speakSpanish2() {
        textToSpeech.speak(this.spanish, TextToSpeech.QUEUE_ADD, null);
    }
    public void onWrongAnswer() {
        this.vibrator.vibrate(WRONG_VIBRATE, -1);
        this.soundPool.play(soundError, 1.0f, 1.0f, 0, 0, 1.5f);
    }
    public void onLost() {
        this.vibrator.vibrate(LOST_VIBRATE, -1);
        this.soundPool.play(soundLost, 1.0f, 1.0f, 0, 0, 1.5f);
    }
}
