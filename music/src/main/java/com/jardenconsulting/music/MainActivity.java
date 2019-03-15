package com.jardenconsulting.music;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import jarden.music.StaveView;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, StaveView.StaveActivity {
    private static final String TAG = "MainActivity";
    private static final String MAX_PITCH_KEY = "MaxPitch";

    private StaveView staveView;
    private SharedPreferences sharedPreferences;
    private Button downButton;
    private Button upButton;
    private TextView maxPitchTextView;
    private int maxPitch = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get maxPitch before setContentView():
        this.sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        this.maxPitch = this.sharedPreferences.getInt(MAX_PITCH_KEY, 3);
        setContentView(R.layout.activity_main);
        this.staveView = findViewById(R.id.staveView);
        Button button = findViewById(R.id.goButton);
        button.setOnClickListener(this);
        button = findViewById(R.id.playButton);
        button.setOnClickListener(this);
        button = findViewById(R.id.cButton);
        button.setOnClickListener(this);
        this.upButton = findViewById(R.id.upButton);
        this.upButton.setOnClickListener(this);
        this.downButton = findViewById(R.id.downButton);
        this.downButton.setOnClickListener(this);
        this.maxPitchTextView = findViewById(R.id.maxPitchTextView);
        setMaxPitch2(maxPitch);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.goButton) {
            staveView.newNotes();
        } else if (id == R.id.playButton) {
            staveView.playNext();
        } else if (id == R.id.cButton) {
            staveView.playC();
        } else if (id == R.id.downButton) {
            setMaxPitch(maxPitch - 1);
            staveView.newNotes();
        } else if (id == R.id.upButton) {
            setMaxPitch(maxPitch + 1);
            this.staveView.newNotes();
        } else {
            Toast.makeText(this, "unrecognised onClick()", Toast.LENGTH_LONG).show();
        }
    }
    private void setMaxPitch(int maxPitch) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MAX_PITCH_KEY, maxPitch);
        editor.apply();
        setMaxPitch2(maxPitch);
    }
    private void setMaxPitch2(int maxPitch) {
        this.upButton.setEnabled(maxPitch <= 12);
        this.downButton.setEnabled(maxPitch >= 3);
        this.maxPitchTextView.setText(Integer.toString(maxPitch));
        this.maxPitch = maxPitch;
    }

    @Override
    public int getMaxPitch() {
        return this.maxPitch;
    }
}
