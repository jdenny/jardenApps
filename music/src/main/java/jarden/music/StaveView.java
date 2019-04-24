package jarden.music;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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
    private static final String TAG = "StaveView";

    private int staveGap;
    private int radius;
    private int noteGap;
    private int bulge;
    private Paint blackPaint = new Paint();
    private Paint redPaint;
    private int[] notePitches;
    private int noteCt;
    private int viewWidth;
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
        for (int i = 0; i < noteCt; i++) {
            Paint paint = (i == this.highlightedNote) ? redPaint : blackPaint;
            showNote(canvas, i + 1, notePitches[i], paint);
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
    public void setNotePitches(int[] notePitches, int noteCt) {
        this.notePitches = notePitches;
        this.noteCt = noteCt;
        invalidate();
    }
}
