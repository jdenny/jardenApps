package jarden.music;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.jardenconsulting.music.BuildConfig;
import com.jardenconsulting.music.R;

/**
 * Created by john.denny@gmail.com on 01/09/2016.
 */
public class StaveView extends View {
    private static final int INDENT = 5;
    public static final int NOTE_CT = 12;
    private static final String TAG = "StaveView";

    private int staveGap;
    private int radius;
    private int noteGap;
    private int bulge;
    private Paint blackPaint = new Paint();
    private Paint redPaint;
    private int[] notePitches;
    private int viewWidth;
    private boolean stopping = false;
    private int highlightedNote = -1;

    public StaveView(Context context) {
        super(context);
    }
    public StaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.viewWidth = w;
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
        int notePitch;
        for (int i = 0; i < NOTE_CT; i++) {
            notePitch = notePitches[i];
            if (notePitch >= 0) {
                Paint paint = (i == this.highlightedNote) ? redPaint : blackPaint;
                showNote(canvas, i + 1, notePitches[i], paint);
            }
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

    public void setHighlightedNote(int highlightedNote) {
        this.highlightedNote = highlightedNote;
        invalidate();
    }
    public void setNotePitches(int[] notePitches) {
        this.notePitches = notePitches;
        invalidate();
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
}
