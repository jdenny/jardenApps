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
    private int circleRadius = 40;
    private int gridMargin = circleRadius + 10;
    private int cellSize = circleRadius * 2 + 10;
    private int blobRadius = 10;
    private int deltaH = circleRadius + blobRadius; // delta hypotenuse!

    static {
        paintBlack = new Paint();
        paintBlack.setColor(Color.BLACK);
        paintBlack.setTextSize(40);
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
            Person personA, personB;
            for (Person person : people) {
                int x = gridMargin + cellSize * person.getX();
                int y = gridMargin + cellSize * person.getY();
                canvas.drawCircle(x, y, circleRadius, paint);
                canvas.drawText(person.getName(), x, y, paintBlack);
                paint = (paint == paint1) ? paint2 : paint1;
                personA = person.getPersonA();
                int xA = gridMargin + cellSize * personA.getX();
                int yA = gridMargin + cellSize * personA.getY();
                canvas.drawLine(x, y, xA, yA, paintBlack);
                drawBlob(x, xA, y, yA, canvas);
                personB = person.getPersonB();
                int xB = gridMargin + cellSize * personB.getX();
                int yB = gridMargin + cellSize * personB.getY();
                canvas.drawLine(x, y, xB, yB, paintBlack);
                drawBlob(x, xB, y, yB, canvas);
                //!! canvas.drawCircle(xB, yB, blobRadius, paintBlack);
            }
        }
    }
    private void drawBlob(int x, int xA, int y, int yA, Canvas canvas) {
        // calculate blob position
        double dx = xA - x;
        double dy = yA - y;
        double h = Math.sqrt(dx * dx + dy * dy);
        double deltaX = deltaH * dx / h;
        double deltaY = deltaH * dy / h;
        double blobX = xA - deltaX;
        double blobY = yA - deltaY;
        canvas.drawCircle((float) blobX, (float) blobY, blobRadius, paintBlack);
    }

}
