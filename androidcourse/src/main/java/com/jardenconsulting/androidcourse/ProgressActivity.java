package com.jardenconsulting.androidcourse;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressActivity extends AppCompatActivity implements OnClickListener {
	private ProgressBar progressBar;
	private TextView textView;
	private final static String TAG = "ProgressActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress);
		this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
		this.textView = (TextView) findViewById(R.id.textView);
		Button longTaskButton = (Button) findViewById(R.id.longTaskButton);
		longTaskButton.setOnClickListener(this);
	}
	@Override
	public void onClick(View view) {
		longTask();
	}
	private void longTask() {
		this.textView.setText("loading database...");
		this.progressBar.setVisibility(ProgressBar.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 10; i++) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						Log.e(TAG, "longTask() InterruptedException");
					}
					Log.i(TAG, "longTask; i=" + i);
				}
				runOnUiThread(new Runnable() {
					public void run() {
						progressBar.setVisibility(ProgressBar.INVISIBLE);
						textView.setText("database load complete");
					}
				});
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.progress, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
