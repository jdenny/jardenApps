package jarden.music;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.View;

import com.jardenconsulting.music.R;

import java.util.Random;

/**
 * Created by john.denny@gmail.com on 01/09/2016.
 */
public class StaveView extends View {
    public static final double[] FREQUENCIES = {
          261.626, 293.665, 329.628, 349.228, 391.995, 440, 493.883, 523.251
    };
    private static final int INDENT = 5;
    private static final int NOTE_CT = 12;

    /*
    add sound:
    http://stackoverflow.com/questions/2413426/playing-an-arbitrary-tone-with-android

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

    private int maxPitch = 3;
    private int staveGap;
    private int radius;
    private int noteGap;
    private Paint blackPaint = new Paint();
    private Paint redPaint;
    private int[] notePitches = new int[NOTE_CT];
    private Random random = new Random();
    private int viewWidth;
    private int viewHeight;
    private boolean stopping = false;
    private int highlightedNote = -1;
    private SoundPool soundPool;
    private int[] guitarSounds;

    public StaveView(Context context) {
        super(context);
    }
    public StaveView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Resources res = getResources();
        redPaint = new Paint();
        redPaint.setColor(Color.RED);
        this.staveGap = res.getDimensionPixelSize(R.dimen.staveGap);
        this.radius = staveGap / 2;
        this.noteGap = 3 * staveGap;
        this.soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        guitarSounds = new int[8];
        guitarSounds[0] = soundPool.load(context, R.raw.guitarc, 1);
        guitarSounds[1] = soundPool.load(context, R.raw.guitard, 1);
        guitarSounds[2] = soundPool.load(context, R.raw.guitare, 1);
        guitarSounds[3] = soundPool.load(context, R.raw.guitarf, 1);
        guitarSounds[4] = soundPool.load(context, R.raw.guitarg, 1);
        guitarSounds[5] = soundPool.load(context, R.raw.guitara, 1);
        guitarSounds[6] = soundPool.load(context, R.raw.guitarb, 1);
        guitarSounds[7] = soundPool.load(context, R.raw.guitarc2, 1);
        newNotes();
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.viewWidth = w;
        this.viewHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < 5; i++) {
            int y = INDENT + i * this.staveGap;
            canvas.drawLine(0, y, viewWidth, y, this.blackPaint);
        }
        for (int i = 0; i < NOTE_CT; i++) {
            Paint paint = (i == this.highlightedNote) ? redPaint : blackPaint;
            showNote(canvas, i + 1, notePitches[i], paint);
        }
    }
    public void newNotes() {
        highlightedNote = -1;
        newNotes2();
        invalidate();
    }
    private void newNotes2() {
        for (int i = 0; i < NOTE_CT; i++) {
            int newPitch = random.nextInt(maxPitch);
            // don't have same note 3 times in succession
            if (i >= 2 && newPitch == notePitches[i-1] && newPitch == notePitches[i-2]) {
                if (++newPitch >= maxPitch) newPitch = 0;
            }
            notePitches[i] = newPitch;
        }
    }
    private void showNote(Canvas canvas, int position, int notePitch, Paint paint) {
        int x = INDENT + position * noteGap;
        int y = INDENT + (10 - notePitch) * radius;
        canvas.drawCircle(x, y, radius, paint);
        canvas.drawLine(x - radius, y, x - radius, y + 80, paint);
        if (notePitch < 1) {  // TODO: may need several ledger lines
            canvas.drawLine(x - radius * 2, y, x + radius * 2, y, blackPaint);
        }
    }
    public int getMaxPitch() {
        return maxPitch;
    }
    public void setMaxPitch(int maxPitch) {
        this.maxPitch = maxPitch;
    }

    public void playNext() {
        if (++highlightedNote >= notePitches.length) highlightedNote = 0;
        invalidate(); // i.e. redraw showing new highlighted note
        this.soundPool.play(guitarSounds[notePitches[highlightedNote]], 1.0f, 1.0f, 0, 0, 1.5f);
    }
    public void playC() {
        this.soundPool.play(guitarSounds[0], 1.0f, 1.0f, 0, 0, 1.5f);
    }
    public void stop() {
        this.stopping = true;
    }
    // alternative way of playing sounds:
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
    public void playAll() {
        this.stopping = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < notePitches.length && !stopping; i++) {
                    playSound(FREQUENCIES[notePitches[i]], 44100);
                }
            }
        }).start();
        /*
        playSound(FREQUENCIES[highlightedNote++], 44100);
        if (highlightedNote >= FREQUENCIES.length) highlightedNote = 0;
        */
        this.soundPool.play(guitarSounds[highlightedNote++], 1.0f, 1.0f, 0, 0, 1.5f);
        if (highlightedNote >= guitarSounds.length) highlightedNote = 0;
    }

}
