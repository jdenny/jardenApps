package com.jardenconsulting.androidcourse;

import com.jardenconsulting.androidcourse.R;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

public class DisplayMessageActivity extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_message);
		Intent intent = this.getIntent();
		String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		TextView messageView = (TextView) this.findViewById(R.id.messageView);
		messageView.setText(message);
		TextView extraView = new TextView(this);
		extraView.setText("plus: " + message);
		LinearLayout rootLayout =
				(LinearLayout) this.findViewById(R.id.rootLayout);
		rootLayout.addView(extraView);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
