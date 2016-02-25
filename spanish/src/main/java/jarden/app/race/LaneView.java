package jarden.app.race;

import com.jardenconsulting.spanishapp.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class LaneView extends View {
	private int cellSize;
	private int laneCols;
	private int border;
	private final static Paint gridPaint;
	private final static Paint lastCellPaint;
	private Paint mePaint;
	private Bitmap meBitmap = null;
	private int xposition = 0;
	private int status = GameData.RUNNING;

	static {
		gridPaint = new Paint();
		gridPaint.setColor(Color.BLACK);
		gridPaint.setStyle(Paint.Style.STROKE);
		lastCellPaint = new Paint();
		lastCellPaint.setColor(Color.RED);
		lastCellPaint.setStyle(Paint.Style.STROKE);
	}

	public LaneView(Context context) {
		super(context);
	}
	public LaneView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Resources res = getResources();
		this.cellSize = res.getDimensionPixelSize(R.dimen.cellSize);
		if (cellSize == 0) cellSize = 24;
		this.border = res.getDimensionPixelSize(R.dimen.border);
		if (border == 0) border = 4;
		this.laneCols = res.getInteger(R.integer.laneCols);
		this.mePaint = new Paint();
		int strokeWidth = res.getDimensionPixelSize(R.dimen.gridThickness);
		lastCellPaint.setStrokeWidth(strokeWidth);
		gridPaint.setStrokeWidth(strokeWidth);
	}
	public void setBitmapId(int bitmapId) {
		this.meBitmap = BitmapFactory.decodeResource(
				getResources(), bitmapId);
	}
	public int moveOn() {
		++this.xposition;
		invalidate();
		return this.xposition;
	}
	public void reset() {
		this.xposition = 0;
		invalidate();
	}
	public int getPosition() {
		return this.xposition;
	}
	public void setStatus(int status) {
		this.status = status;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawRect(cellSize * laneCols,
				0, cellSize * (laneCols+1), cellSize, lastCellPaint);
		canvas.drawRect(0, 0, cellSize * laneCols, cellSize, gridPaint);
		for (int i = 0; i < laneCols; i++) {
			canvas.drawLine(cellSize * i, 0, cellSize * i, cellSize, gridPaint);
		}
		float tx = cellSize * xposition + (cellSize / 2);
		float ty =  cellSize / 2;
		float radius = (cellSize - border) / 2;
		canvas.drawBitmap(this.meBitmap, cellSize * xposition + 2, 2, mePaint);
		if (this.status == GameData.CAUGHT) {
			canvas.drawLine(tx - radius, ty + radius, tx + radius, ty - radius, gridPaint);
		}
	}
	public void setData(GameData gameData) {
		this.xposition = gameData.position;
		this.status = gameData.status;
		this.invalidate();
	}
}
