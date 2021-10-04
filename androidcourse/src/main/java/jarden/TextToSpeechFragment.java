package jarden;

import java.util.Locale;

import com.jardenconsulting.androidcourse.BuildConfig;
import com.jardenconsulting.androidcourse.MainActivity;
import com.jardenconsulting.androidcourse.R;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TextToSpeechFragment extends Fragment
		implements OnClickListener, OnInitListener {
	private Button speakButton;
	private TextView statusTextView;
	private EditText speakEditText;
	private TextToSpeech textToSpeech;
	private int orientation;
	private String phrase;

	@Override // Fragment
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) logState("onCreateView(savedInstanceState=" + savedInstanceState + ")");
		setRetainInstance(true);
		View rootView = inflater.inflate(R.layout.text_to_speech_fragment, container, false);
		this.speakButton = (Button) rootView.findViewById(R.id.speakButton);
		speakButton.setOnClickListener(this);
		this.statusTextView = (TextView) rootView.findViewById(R.id.statusTextView);
		this.speakEditText = (EditText) rootView.findViewById(R.id.speakEditText);
		saveOrientation();
		return rootView;
	}

	@Override // Fragment
	public void onResume() {
		if (BuildConfig.DEBUG) logState("onResume()");
		super.onResume();
		if (this.phrase != null) speakPhrase(phrase);
	}
	private void logState(String method) {
		Log.d(MainActivity.TAG,
				"TextToSpeechActivity." + method + "; phrase=" + phrase +
				"; textToSpeech is " + (textToSpeech==null?"":"not ") + "null" +
				"; statusTextView is " + (statusTextView==null?"":"not ") + "null");
	}
	@Override // OnClickListener
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.speakButton) {
			if (BuildConfig.DEBUG) logState("onClick(id=speakButton!)");
			speakPhrase(this.speakEditText.getText().toString());
		} else {
			statusTextView.setText("unrecognised button: " + id);
		}
	}
	@Override // OnInitListener (called when textToSpeech is initialised)
	public void onInit(int status) {
		if (BuildConfig.DEBUG) Log.d(MainActivity.TAG, "TexttoSpeechActivity.onInit()");
		this.statusTextView.setText("");
		if (status == TextToSpeech.SUCCESS) {
			if (this.textToSpeech == null) {
				// this could happen if activity is paused between creating
				// new textToSpeech and getting the response back here
				this.statusTextView.setText("textToSpeech closed down");
				return;
			}
			int result = textToSpeech.setLanguage(Locale.ENGLISH);
			if (BuildConfig.DEBUG) {
				Log.d(MainActivity.TAG, "textToSpeech.setLanguage(); result=" + result);
			}
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				this.statusTextView.setText("TextToSpeech for Spanish is not supported");
			}
			if (this.phrase != null) speakPhrase2();
		} else {
			Log.w(MainActivity.TAG, "TextToSpeechActivity.onInit(" + status + ")");
			this.statusTextView.setText(
				"Initilization of textToSpeech failed! Have you installed text-to-speech?");
		}
	}
	@Override // Activity
	public void onPause() {
		boolean orientationChanged = isOrientationChanged();
		if (BuildConfig.DEBUG) {
			Log.d(MainActivity.TAG,
					"TextToSpeechActivity.onPause(); phrase=" + phrase +
					"; textToSpeech is " + (textToSpeech==null?"":"not ") +
					"null; orientationChanged=" + orientationChanged);
		}
		super.onPause();

		// TODO: try to work out why this doesn't work
		// according to the exception stacktrace,
		// somehow android is calling onClick by itself, and
		// somehow textToSpeech is set to null
		if (!orientationChanged &&
				this.textToSpeech != null) {
			textToSpeech.stop();
			textToSpeech.shutdown();
			textToSpeech = null;
			if (BuildConfig.DEBUG) Log.d(MainActivity.TAG,
					"TextToSpeechActivity.onPause(); textToSpeech closed");
		}
		
	}
	// return true if orientation changed since previous call
	private boolean isOrientationChanged() {
		int oldOrientation = this.orientation;
		saveOrientation();
		if (BuildConfig.DEBUG) Log.d(MainActivity.TAG,
				"TextToSpeechActivity.getOrientation(); orientation was: " +
				oldOrientation + ", is: " + this.orientation);
		return this.orientation != oldOrientation;
	}
	private void saveOrientation() {
		this.orientation = getResources().getConfiguration().orientation;
	}
	public void speakPhrase(String phrase) {
		if (BuildConfig.DEBUG) logState("speakPhrase()");
		this.phrase = phrase;
		if (this.textToSpeech == null) {
			// invokes onInit() on completion
			textToSpeech = new TextToSpeech(getActivity().getApplicationContext(), TextToSpeechFragment.this);
			statusTextView.setText("loading textToSpeech...");
		} else {
			speakPhrase2();
		}
	}

	@SuppressWarnings("deprecation")
	private void speakPhrase2() {
		if (BuildConfig.DEBUG) logState("speakPhrase2()");
		textToSpeech.speak(this.phrase, TextToSpeech.QUEUE_ADD, null);
	}
}
