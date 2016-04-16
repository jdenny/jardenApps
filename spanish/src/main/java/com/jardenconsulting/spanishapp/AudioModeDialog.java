package com.jardenconsulting.spanishapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import jarden.engspa.EngSpa;
import jarden.engspa.EngSpaDAO;

/**
 * Created by john.denny@gmail.com on 16/04/2016.
 */
public class AudioModeDialog extends DialogFragment
        implements View.OnClickListener, Runnable {
    private static final String TAG = "AudioModeDialog";
    private boolean playing = true;
    private Thread speakingThread;
    private EngSpaActivity engSpaActivity;
    private int userLevel;
    private EngSpaDAO engSpaDAO;
    private EditText pauseEditText;
    private ImageButton playPauseImageButton;
    private int sleepTimeMillis = 3000;

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater =activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_audio_mode, null);
        this.pauseEditText = (EditText) view.findViewById(R.id.pauseEditText);
        Button button = (Button) view.findViewById(R.id.setPauseButton);
        button.setOnClickListener(this);
        this.playPauseImageButton = (ImageButton) view.findViewById(R.id.playPauseImageButton);
        this.playPauseImageButton.setOnClickListener(this);

        this.engSpaActivity = (EngSpaActivity) activity;
        this.userLevel = engSpaActivity.getEngSpaUser().getUserLevel();
        this.engSpaDAO = engSpaActivity.getEngSpaDAO();
        builder.setTitle(R.string.audioMode);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnDismissListener(this);
        startThread();
        return alertDialog;
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onDismiss");
        if (speakingThread != null) speakingThread.interrupt();
        super.onDismiss(dialog);
    }
    private void startThread() {
        this.speakingThread = new Thread(this);
        speakingThread.start();
    }
    @Override
    public void run() {
        EngSpa es;
        try {
            while (!Thread.currentThread().interrupted()) {
                es = engSpaDAO.getRandomPassedWord(userLevel);
                engSpaActivity.speakSpanish(es.getSpanish());
                Thread.sleep(sleepTimeMillis);
                engSpaActivity.speakEnglish(es.getEnglish());
                Thread.sleep(sleepTimeMillis);
                es = engSpaDAO.getRandomPassedWord(userLevel);
                engSpaActivity.speakEnglish(es.getEnglish());
                Thread.sleep(sleepTimeMillis);
                engSpaActivity.speakSpanish(es.getSpanish());
                Thread.sleep(sleepTimeMillis);
            }
        } catch (InterruptedException e) {
            Log.w(TAG, "audioMode thread interrupted");
        }
    }
    private void pause() throws InterruptedException {
        int pauseSeconds = Integer.parseInt(this.pauseEditText.getText().toString());

    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.setPauseButton) {
            this.sleepTimeMillis =
                    Integer.parseInt(this.pauseEditText.getText().toString()) * 1000;
        } else if (id == R.id.playPauseImageButton) {
            if (playing) {
                speakingThread.interrupt();
                speakingThread = null;
                playPauseImageButton.setImageResource(android.R.drawable.ic_media_play);
            } else {
                startThread();
                playPauseImageButton.setImageResource(android.R.drawable.ic_media_pause);
            }
            playing = !playing;
        } else {
            Log.w(TAG, "onClick(), unknown view.id: " + id);
        }
    }
}
