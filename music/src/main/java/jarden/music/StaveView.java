package jarden.music;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.jardenconsulting.music.BuildConfig;
import com.jardenconsulting.music.R;

import java.util.Random;

/**
 * Created by john.denny@gmail.com on 01/09/2016.
 */
public class StaveView extends View {
    public interface StaveActivity {
        int getMaxPitch();
    }

    private static final int INDENT = 5;
    private static final int NOTE_CT = 12;
    private static final String TAG = "StaveView";

    /*
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

    private StaveActivity staveActivity;
    private int staveGap;
    private int radius;
    private int noteGap;
    private int bulge;
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
        this.staveActivity = (StaveActivity) getActivity();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "StaveView(context, attrs)");
        }
        Resources res = getResources();
        redPaint = new Paint();
        redPaint.setColor(Color.RED);
        this.staveGap = res.getDimensionPixelSize(R.dimen.staveGap);
        this.bulge = res.getDimensionPixelSize(R.dimen.bulge);
        this.radius = staveGap / 2;
        this.noteGap = 3 * staveGap;
        if (!this.isInEditMode()) {
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
            guitarSounds = new int[8];
            // To create new sounds, use QuickTime player, trim, save,
            // copy m4a files to res/raw; m4a are audio files
            guitarSounds[0] = soundPool.load(context, R.raw.guitarc, 1);
            guitarSounds[1] = soundPool.load(context, R.raw.guitard, 1);
            guitarSounds[2] = soundPool.load(context, R.raw.guitare, 1);
            guitarSounds[3] = soundPool.load(context, R.raw.guitarf, 1);
            guitarSounds[4] = soundPool.load(context, R.raw.guitarg, 1);
            guitarSounds[5] = soundPool.load(context, R.raw.guitara, 1);
            guitarSounds[6] = soundPool.load(context, R.raw.guitarb, 1);
            guitarSounds[7] = soundPool.load(context, R.raw.guitarc2, 1);
        }
        newNotes2();
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
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDraw()");
        }
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
        if (BuildConfig.DEBUG) Log.d(TAG, "newNotes()");
        highlightedNote = -1;
        newNotes2();
        invalidate();
    }
    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
    private void newNotes2() {
        if (BuildConfig.DEBUG) Log.d(TAG, "newNotes2()");
        int maxPitch = (staveActivity == null) ? 5 : staveActivity.getMaxPitch();
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
        int left = x - radius - bulge;
        canvas.rotate(-20, x, y);
        canvas.drawOval(new RectF(left, y - radius, x + radius + bulge, y + radius),
                paint);
        canvas.rotate(20, x, y);
        canvas.drawLine(left, y, left, y + 80, paint);
        if (notePitch < 1) {
            canvas.drawLine(x - radius * 2, y, x + radius * 2, y, blackPaint);
        }
    }
    public void playNext() {
        if (++highlightedNote >= notePitches.length) highlightedNote = 0;
        invalidate(); // i.e. redraw showing new highlighted note
        this.soundPool.play(guitarSounds[notePitches[highlightedNote]], 1.0f, 1.0f, 0, 0, 1.0f);
        //?? playSound(FREQUENCIES[notePitches[highlightedNote]], 144100);
    }
    public void playC() {
        this.soundPool.play(guitarSounds[0], 1.0f, 1.0f, 0, 0, 1.0f);
        //?? playSound(FREQUENCIES[0], 144100);
    }
    public void stop() {
        this.stopping = true;
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
        this.soundPool.play(guitarSounds[highlightedNote++], 1.0f, 1.0f, 0, 0, 1.0f);
        if (highlightedNote >= guitarSounds.length) highlightedNote = 0;
    }

}
