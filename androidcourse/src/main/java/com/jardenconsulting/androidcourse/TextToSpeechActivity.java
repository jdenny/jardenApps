package com.jardenconsulting.androidcourse;

import java.io.IOException;

import jarden.http.MyHttpClient;
import jarden.quiz.QuizCache;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

//TODO: rename this to RunInBackground. We can ditch the textToSpeech bits!
// see also DoWorkInThread in this project, and
// JardenAppLib EngSpaUtils.runBackgroundTask()
// The idea is to demonstrate a pattern:
//		use dialog to ask for confirmation to run big task
//		run task in background (e.g. access database or network)
//		report results to foreground

public class TextToSpeechActivity extends AppCompatActivity
		implements NewDBDataDialog.UpdateDBListener,
		OnClickListener {
    private static final String ENG_SPA_UPDATES_NAME = 
    		QuizCache.serverUrlStr + "engspaupdates.txt?attredirects=0&d=1";
	private final static String TAG = "TextToSpeechActivity";
	private ProgressBar progressBar;
	private TextView statusTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_to_speech);
		this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
		this.progressBar.setVisibility(ProgressBar.INVISIBLE);
		this.statusTextView = (TextView) findViewById(R.id.statusTextView);
		Button goButton = (Button) findViewById(R.id.goButton);
		goButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.layouts, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Log.w(TAG, "unrecognised option:" + item);
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View view) {
		DialogFragment dialog = new NewDBDataDialog();
		dialog.show(getSupportFragmentManager(), "New Dictionary Updates");
	}

	/**
	 * Process response from NewDBDataDialog; if confirmed, get
	 * date last modified of file engSpaUpdates on server. This
	 * is run as a background thread.
	 */
	@Override // NewDBDataDialog.UpdateDBListener
	public void onUpdateDecision(boolean doUpdate) {
		if (doUpdate) {
			this.statusTextView.setText("loading new dictionary version...");
			this.progressBar.setVisibility(ProgressBar.VISIBLE);
			new Thread(new Runnable() {
				private String updateStatus = "";
				@Override
				public void run() {
					try {
						String urlStr = ENG_SPA_UPDATES_NAME + "?attredirects=0&d=1";
						long dateEngSpaUpdatesModified = MyHttpClient.getLastModified(urlStr);
						updateStatus = "date EngSpaUpdates modified: " + dateEngSpaUpdatesModified;
					} catch (IOException e) {
						updateStatus = "error getting date EngSpaUpdates modified: " + e;
					}
					// now post the results to the UI thread:
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							statusTextView.setText(updateStatus);
							progressBar.setVisibility(ProgressBar.INVISIBLE);
						}
					});
				}
			}).start();
		}
	}
}
