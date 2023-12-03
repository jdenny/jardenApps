package com.jardenconsulting.equidistance;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import jarden.equidistance.Person;

/**
 * Created by john.denny@gmail.com on 03/12/2023.
 */
public class CanvasView  extends View {
    private final static Paint paintBlack;
    private final static Paint paint1;
    private final static Paint paint2;
    private Person[] people = null;

    static {
        paintBlack = new Paint();
        paintBlack.setColor(Color.BLACK);
        paintBlack.setTextSize(30);
        paint1 = new Paint();
        paint1.setColor(Color.CYAN);
        paint2 = new Paint();
        paint2.setColor(Color.YELLOW);
    }

    public CanvasView(Context context) {
        super(context);
    }
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPeople(Person[] people) {
        this.people = people;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = paint1;
        if (people != null) {
            for (Person person: people) {
                int x = 40 + 70 * person.getX();
                int y = 40 + 70 * person.getY();
                canvas.drawCircle(x, y, 30, paint);
                canvas.drawText(person.getName(), x, y, paintBlack);
                paint = (paint == paint1)? paint2 : paint1;
            }
        }
    }

}
