package jarden.music;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.jardenconsulting.music.R;

import java.util.Random;

/**
 * Created by john.denny@gmail.com on 01/09/2016.
 */
public class StaveView extends View {
    private static final int INDENT = 5;
    private static final int NOTE_CT = 12;

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

    private int maxPitch = 3;
    private int staveGap;
    private int radius;
    private int noteGap;
    private Paint paint = new Paint();
    private int[] notePitches = new int[NOTE_CT];
    private Random random = new Random();
    private int viewWidth;
    private int viewHeight;

    public StaveView(Context context) {
        super(context);
    }
    public StaveView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Resources res = getResources();
        this.staveGap = res.getDimensionPixelSize(R.dimen.staveGap);
        this.radius = staveGap / 2;
        this.noteGap = 3 * staveGap;
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
            canvas.drawLine(0, y, viewWidth, y, this.paint);
        }
        for (int i = 0; i < NOTE_CT; i++) {
            int newPitch = random.nextInt(maxPitch);
            // don't have same note 3 times in succession
            if (i >= 2 && newPitch == notePitches[i-1] && newPitch == notePitches[i-2]) {
                if (++newPitch >= maxPitch) newPitch = 0;
            }
            notePitches[i] = newPitch;

        }
        for (int i = 0; i < NOTE_CT; i++) {
            showNote(canvas, i + 1, notePitches[i]);
        }
    }
    private void showNote(Canvas canvas, int position, int notePitch) {
        int x = INDENT + position * noteGap;
        int y = INDENT + (10 - notePitch) * radius;
        canvas.drawCircle(x, y, radius, this.paint);
        canvas.drawLine(x - radius, y, x - radius, y + 80, this.paint);
        if (notePitch < 1) {  // TODO: may need several ledger lines
            canvas.drawLine(x - radius * 2, y, x + radius * 2, y, this.paint);
        }
    }
    public int getMaxPitch() {
        return maxPitch;
    }
    public void setMaxPitch(int maxPitch) {
        this.maxPitch = maxPitch;
    }
}
