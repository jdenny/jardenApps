package jarden.app;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TableView extends LinearLayout {
	private LinearLayout[] rowLayouts;
	private TextView[] cellTextViews;
	private int cellNumber = 0;

	@SuppressWarnings("deprecation")
	public TableView(Context context, int rowCt, int columnCt) {
		super(context);
		int cellCt = rowCt * columnCt;
		rowLayouts = new LinearLayout[rowCt];
		for (int i = 0; i < rowCt; i++) {
			rowLayouts[i] = new LinearLayout(context);
		}
		cellTextViews = new TextView[cellCt];
		TextView cellTextView;
		LinearLayout.LayoutParams layoutParams =
				new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
		layoutParams.weight = 1;
		for (int i = 0; i < cellCt; i++) {
			cellTextView = new TextView(context);
			cellTextView.setLayoutParams(layoutParams);
			cellTextView.setTextAppearance(context, android.R.style.TextAppearance_Medium);
			cellTextViews[i] = cellTextView;
			rowLayouts[i/columnCt].addView(cellTextView);
		}
	}
	public void clearCellData() {
		// this.bidList.clear(); // clear data: needs to be done in client
		this.cellNumber = 0;
		for (TextView cellTextView: cellTextViews) cellTextView.setText("");
	}
	public void addData(String text) {
		updateData(text);
		this.cellNumber++;
	}
	public void updateData(Object obj) {
		this.cellTextViews[cellNumber].setText(obj.toString());
	}
	public void setCellNumber(int cellNumber) {
		this.cellNumber = cellNumber;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

}
