package com.jardenconsulting.music;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import jarden.music.StaveView;

/*
    next step: c is middle c (c'); C is octave above (c'')

    current strategy, which only copes with key of C:
    pitches are specified relative to middle c (c'):
        c'=0, d'=1, e'=2, f'=3, etc, c''=7
    y-axis defined as (10 - pitch) * stave-gap / 2

    alternative strategy (not yet fully formed!):
    pitches are specified relative to middle c (c'):
        c'=0, d'=2, e'=4, f'=5, g'=7, a'=9, b'=11, c''=12
        c#=1, d#=3, f#=6, g#=8, a#=10
    pitchMapping defines y offset of note in terms of half-stave-gap from top line
    e.g. middle d' (note above middle c) has pitch of 2, mapped to 9, so d' is shown
    9 half-gaps from top line (e''), using mapping array:
    private static final int[] pitchMapping = {10, 10, 9, 9, 8, 7, 7, 6, 6, 5, 5, 4, 3};
 */
public class MusicActivity extends AppCompatActivity
        implements View.OnClickListener, TextWatcher {
    private static final String TAG = "MusicActivity";
    private static final String MAX_PITCH_KEY = "MaxPitch";
    private static final int MAX_NOTE_CT = 12;
    private static final char[] noteMap = {
            'c', 'd', 'e', 'f', 'g', 'a', 'b', 'C', 'D', 'E', 'F', 'G', 'A', 'B'
    };

    private int[] notePitches = new int[MAX_NOTE_CT];
    private int noteCt = MAX_NOTE_CT;
    private StaveView staveView;
    private SharedPreferences sharedPreferences;
    private Button downButton;
    private Button upButton;
    private TextView maxPitchTextView;
    private EditText notesEditText;
    private int maxPitch = 3;
    private SoundPool soundPool;
    private int[] guitarSounds;
    private Random random = new Random();
    private int highlightedNote = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get maxPitch before setContentView():
        this.sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        this.maxPitch = this.sharedPreferences.getInt(MAX_PITCH_KEY, 3);
        setContentView(R.layout.activity_music);
        this.staveView = findViewById(R.id.staveView);
        Button button = findViewById(R.id.goButton);
        button.setOnClickListener(this);
        button = findViewById(R.id.playButton);
        button.setOnClickListener(this);
        button = findViewById(R.id.repeatButton);
        button.setOnClickListener(this);
        button = findViewById(R.id.cButton);
        button.setOnClickListener(this);
        this.upButton = findViewById(R.id.plusButton);
        this.upButton.setOnClickListener(this);
        this.downButton = findViewById(R.id.minusButton);
        this.downButton.setOnClickListener(this);
        this.maxPitchTextView = findViewById(R.id.maxPitchTextView);
        this.notesEditText = findViewById(R.id.notesEditText);
        notesEditText.addTextChangedListener(this);
        setMaxPitch2(maxPitch);
        /*
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();
        this.soundPool =
                new SoundPool.Builder()
                        .setMaxStreams(2)
                        .setAudioAttributes(audioAttributes)
                        .build();
         */
        this.soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        guitarSounds = new int[14];
        // To create new sounds, use QuickTime player, trim, save,
        // copy m4a files to res/raw; m4a are audio files
        guitarSounds[0] = soundPool.load(this, R.raw.guitarc, 1);
        guitarSounds[1] = soundPool.load(this, R.raw.guitard, 1);
        guitarSounds[2] = soundPool.load(this, R.raw.guitare, 1);
        guitarSounds[3] = soundPool.load(this, R.raw.guitarf, 1);
        guitarSounds[4] = soundPool.load(this, R.raw.guitarg, 1);
        guitarSounds[5] = soundPool.load(this, R.raw.guitara, 1);
        guitarSounds[6] = soundPool.load(this, R.raw.guitarb, 1);
        guitarSounds[7] = soundPool.load(this, R.raw.guitarc2, 1);
        guitarSounds[8] = soundPool.load(this, R.raw.guitard2, 1);
        guitarSounds[9] = soundPool.load(this, R.raw.guitare2, 1);
        guitarSounds[10] = soundPool.load(this, R.raw.guitarf2, 1);
        guitarSounds[11] = soundPool.load(this, R.raw.guitarg2, 1);
        guitarSounds[12] = soundPool.load(this, R.raw.guitara2, 1);
        guitarSounds[13] = soundPool.load(this, R.raw.guitarb2, 1);
        newNotes();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.goButton) {
            newNotes();
        } else if (id == R.id.playButton) {
            playNext();
        } else if (id == R.id.repeatButton) {
            playCurrent();
        } else if (id == R.id.cButton) {
            playC();
        } else if (id == R.id.minusButton) {
            setMaxPitch(maxPitch - 1);
            newNotes();
        } else if (id == R.id.plusButton) {
            setMaxPitch(maxPitch + 1);
            newNotes();
        } else {
            Toast.makeText(this, "unrecognised onClick()", Toast.LENGTH_LONG).show();
        }
    }
    private void newNotes() {
        if (BuildConfig.DEBUG) Log.d(TAG, "newNotes()");
        int[] chosenPitches = new int[maxPitch];
        if (maxPitch < 5) {
            chosenPitches[0] = 0; // i.e. root
            chosenPitches[1] = 4; // i.e. fifth
            if (maxPitch > 2) {
                chosenPitches[2] = 2; // i.e. third
                if (maxPitch > 3) {
                    chosenPitches[3] = 7; // i.e. octave
                }
            }
        } else {
            for (int i = 0; i < maxPitch; i++) {
                chosenPitches[i] = i;
            }
        }
        for (int i = 0; i < MAX_NOTE_CT; i++) {
            int newPitchIndex = random.nextInt(maxPitch);
            // don't have same note 3 times in succession
            if (i >= 2 && newPitchIndex == notePitches[i-1] && newPitchIndex == notePitches[i-2]) {
                if (++newPitchIndex >= maxPitch) newPitchIndex = 0;
            }
            notePitches[i] = chosenPitches[newPitchIndex];
        }
        noteCt = MAX_NOTE_CT;
        setNotesText();
        staveView.setNotePitches(notePitches, noteCt);
        staveView.setHighlightedNote(-1);
    }
    private void setNotesText() {
        StringBuilder strBuilder = new StringBuilder();
        for (int pitch: notePitches) {
            strBuilder.append(noteMap[pitch]);
            strBuilder.append(' ');
        }
        notesEditText.setText(strBuilder.toString());
    }
    public void playNext() {
        if (++highlightedNote >= noteCt) highlightedNote = 0;
        staveView.setHighlightedNote(highlightedNote);
        playCurrent();
    }
    public void playCurrent() {
        this.soundPool.play(guitarSounds[notePitches[highlightedNote]], 1.0f, 1.0f, 0, 0, 1.0f);
    }
    public void playC() {
        this.soundPool.play(guitarSounds[0], 1.0f, 1.0f, 0, 0, 1.0f);
    }

    private void setMaxPitch(int maxPitch) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MAX_PITCH_KEY, maxPitch);
        editor.apply();
        setMaxPitch2(maxPitch);
    }
    private void setMaxPitch2(int maxPitch) {
        this.upButton.setEnabled(maxPitch <= MAX_NOTE_CT);
        this.downButton.setEnabled(maxPitch >= 3);
        this.maxPitchTextView.setText(Integer.toString(maxPitch));
        this.maxPitch = maxPitch;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onTextChanged(" + s + ")");
        String notesText = notesEditText.getText().toString();
        char ch;
        int chi;
        noteCt = 0;
        for (int i = 0; i < notesText.length() && noteCt < MAX_NOTE_CT; i++) {
            ch = notesText.charAt(i);
            chi = -1;
            if (ch != ' ') {
                for (int j = 0; j < noteMap.length && chi < 0; j++) {
                    if (ch == noteMap[j]) {
                        chi = j;
                    }
                }
                if (chi >= 0) {
                    notePitches[noteCt++] = chi;
                } else {
                    Toast.makeText(this, "illegal char: " + ch, Toast.LENGTH_LONG).show();
                }
            }
        }
        staveView.setNotePitches(notePitches, noteCt);
    }
    @Override
    public void afterTextChanged(Editable s) {
    }

    // alternative way of playing sounds:
    public static final double[] FREQUENCIES = {
            261.626, 293.665, 329.628, 349.228, 391.995, 440, 493.883, 523.251
    };

    private void playSound(double frequency, int duration) {
        // AudioTrack definition
        int mBufferSize = AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT);

        AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                mBufferSize, AudioTrack.MODE_STREAM);

        // Sine wave
        double[] mSound = new double[4410];
        short[] mBuffer = new short[duration];
        for (int i = 0; i < mSound.length; i++) {
            mSound[i] = Math.sin((2.0 * Math.PI * i / (44100 / frequency)));
            mBuffer[i] = (short) (mSound[i] * Short.MAX_VALUE);
        }

        mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
        // mAudioTrack.setVolume(AudioTrack.getMaxVolume()); // API 21 onwards
        mAudioTrack.play();

        mAudioTrack.write(mBuffer, 0, mSound.length);
        mAudioTrack.stop();
        mAudioTrack.release();

    }
}
