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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final String MAX_PITCH_KEY = "MaxPitch";

    private StaveView staveView;
    private SharedPreferences sharedPreferences;
    private Button downButton;
    private Button upButton;
    private TextView maxPitchTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.staveView = (StaveView) findViewById(R.id.staveView);
        this.sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        Button button = (Button) findViewById(R.id.goButton);
        button.setOnClickListener(this);
        this.upButton = (Button) findViewById(R.id.upButton);
        this.upButton.setOnClickListener(this);
        this.downButton = (Button) findViewById(R.id.downButton);
        this.downButton.setOnClickListener(this);
        this.maxPitchTextView = (TextView) findViewById(R.id.maxPitchTextView);
        int maxPitch = this.sharedPreferences.getInt(MAX_PITCH_KEY, 3);
        setMaxPitch2(maxPitch);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.goButton) {
            staveView.invalidate();
        } else if (id == R.id.downButton) {
            int maxPitch = this.staveView.getMaxPitch() - 1;
            setMaxPitch(maxPitch);
            staveView.invalidate();
        } else if (id == R.id.upButton) {
            int maxPitch = this.staveView.getMaxPitch() + 1;
            setMaxPitch(maxPitch);
            this.staveView.invalidate();
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
        this.staveView.setMaxPitch(maxPitch);
    }
}
