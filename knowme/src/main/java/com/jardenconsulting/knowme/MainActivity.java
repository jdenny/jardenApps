package com.jardenconsulting.knowme;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jardenconsulting.bluetooth.BluetoothFragment;
import com.jardenconsulting.bluetooth.BluetoothListener;
import com.jardenconsulting.bluetooth.BluetoothService;
import com.jardenconsulting.bluetooth.BluetoothService.BTState;

import jarden.knowme.EndOfQuestionsException;

public class MainActivity extends AppCompatActivity
		implements BluetoothListener, OnClickListener, KnowMeActivityIF {
    public static final String TAG = "KnowMe";

    private TextView statusText;
	private FragmentManager fragmentManager;
	private IntroFragment introFragment;
	private KnowMeFragment knowMeFragment;
	private AnswerFragment answerFragment;
	private SummaryFragment summaryFragment;
	private BluetoothFragment bluetoothFragment;
	private Fragment currentFragment;
	private EditText playerNameEditText;
    private EditText otherPlayerNameEditText;
	private String playerName;
	/*
	 * When the app is closing down (using pressing back button or
	 * rotating device) we will probably get notified the connection
	 * is lost, and we don't want to restart BluetoothFragment.
	 */
	private boolean closing = false;
	
	// variables shared across fragments:
	private String[] howDoneOptions;
	private int[] percentages;
	private String resultsTemplate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) Log.d(TAG, "MainActivity.onCreate()");
		setContentView(R.layout.activity_main);
		this.fragmentManager = getSupportFragmentManager();
		this.introFragment = (IntroFragment)
				this.fragmentManager.findFragmentById(R.id.introFragment);
		this.knowMeFragment = (KnowMeFragment)
				this.fragmentManager.findFragmentById(R.id.knowMeFragment);
		this.answerFragment = (AnswerFragment)
				this.fragmentManager.findFragmentById(R.id.answerFragment);
		this.summaryFragment =
				(SummaryFragment) fragmentManager.findFragmentById(R.id.summaryFragment);
		this.statusText = findViewById(R.id.statusText);
		// hide all fragments but introFragment
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.hide(knowMeFragment);
		ft.hide(answerFragment);
		ft.hide(summaryFragment);
		this.currentFragment = introFragment;
		ft.commit();
		// resources for IntroFragment...
		findViewById(R.id.playButton).setOnClickListener(this);
		findViewById(R.id.playOneDeviceButton).setOnClickListener(this);
		this.playerNameEditText = findViewById(R.id.yourNameEditText);
		this.playerName = BluetoothFragment.getPlayerName(this);
		this.playerNameEditText.setText(this.playerName);
        this.otherPlayerNameEditText = findViewById(R.id.otherNameEditText);
		// resources for SummaryFragment...
		findViewById(R.id.nextQuizButton).setOnClickListener(this);
		findViewById(R.id.nextQuestionButton).setOnClickListener(this);
		Resources resources = getResources();
		this.howDoneOptions = resources.getStringArray(R.array.howDoneStrings);
		this.percentages = resources.getIntArray(R.array.percentages);
		this.resultsTemplate = resources.getString(R.string.resultsTemplate);
		// end of resources for fragments
	}

	/* Menu not used at the moment
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	*/

	@Override
	public void onClick(View view) {
		int viewId = view.getId();
		if (viewId == R.id.playButton || viewId == R.id.playOneDeviceButton) {
            String playerNam = this.playerNameEditText.getText().toString().trim();
            if (playerNam.length() == 0) {
                this.statusText.setText("Please supply your name");
                this.playerNameEditText.requestFocus();
                return;
            }
            if (!playerNam.equals(this.playerName)) {
                this.playerName = playerNam;
                BluetoothFragment.setPlayerName(this, this.playerName);
            }
        }
		if (viewId == R.id.playButton) {
	 		if (this.bluetoothFragment == null) {
	 	 		FragmentTransaction ft = fragmentManager.beginTransaction();
	 			if (this.currentFragment != null) {
	 				ft.hide(currentFragment);
	 			}
	 			this.bluetoothFragment = new BluetoothFragment();
	 			knowMeFragment.setPlayerName(playerName);
	 			ft.add(R.id.bluetoothFragmentContainer, bluetoothFragment);
	 			ft.commit();
	 		}
			showFragment(this.bluetoothFragment);
		} else if (viewId == R.id.playOneDeviceButton) {
			String otherPlayerName = this.otherPlayerNameEditText.getText().toString().trim();
            if (otherPlayerName.length() == 0) {
                this.statusText.setText("Please supply name of other player");
                this.otherPlayerNameEditText.requestFocus();
                return;
            }
			this.knowMeFragment.setSingleDeviceMode(playerName, otherPlayerName);
		} else if (viewId == R.id.nextQuestionButton) {
			this.knowMeFragment.nextQuestion();
		} else if (viewId == R.id.nextQuizButton) {
			try {
				knowMeFragment.nextQuiz();
				showFragment(this.knowMeFragment);
			} catch (EndOfQuestionsException e) {
				// TODO: option to restart? show SummaryFragment
				setStatusMessage("end of quizzes");
			}
		} else {
			String errorMessage = "unexpected onClick viewId: " + viewId; 
			setStatusMessage(errorMessage);
			Log.e(TAG, errorMessage);
		}
	}

	@Override
    public void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) Log.d(TAG, "MainActivity.onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (BuildConfig.DEBUG) Log.d(TAG, "MainActivity.onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (BuildConfig.DEBUG) Log.d(TAG, "MainActivity.onDestroy()");
        this.closing = true;
    }
    
	private void showFragment(Fragment fragment) {
		if (fragment == this.currentFragment) return;
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.hide(currentFragment);
		ft.show(fragment);
		this.currentFragment = fragment;
		ft.commit();
	}
	
	@Override // KnowMeActivityIF
	public void setOtherPlayerName(String otherPlayerName) {
		String template = getResources().getString(R.string.titleTemplate);
		template = template.replace("{0}", this.knowMeFragment.getPlayerName());
		template = template.replace("{1}", otherPlayerName);
		getSupportActionBar().setTitle(template);
		this.answerFragment.setOtherPlayerName(otherPlayerName);
	}

	@Override // KnowMeActivityIF
	public void questionPosed() {
		showFragment(this.knowMeFragment);
		setStatusMessage("");
	}

	@Override // KnowMeActivityIF
	public void displayResults(String[] questionArray, int myMe, int myHim,
			int hisHim, int hisMe) {
		this.answerFragment.displayResults(questionArray, myMe, myHim, hisHim, hisMe);
		setStatusMessage("");
		showFragment(this.answerFragment);
	}

	@Override // KnowMeActivityIF
	public void endOfQuestions(int percentCorrect) {
		String howDone = null;
		for (int i = 0; i < percentages.length; i++) {
			if (percentCorrect >= percentages[i]) {
				howDone = howDoneOptions[i];
				break;
			}
		}
		if (howDone == null)
			howDone = howDoneOptions[percentages.length];
		String message = this.resultsTemplate.replace("{0}", Integer.toString(percentCorrect));
		message = message.replace("{1}", howDone);
		setStatusMessage(message);
		showFragment(this.summaryFragment);
	}

	@Override // BluetoothListener
	public void onStateChange(BTState state) {
		setStatusMessage(state.toString());
	}

	@Override // BluetoothListener
	public void onConnectedAsServer(String deviceName) {
		onConnected(true);
	}

	@Override // BluetoothListener
	public void onConnectedAsClient(String deviceName) {
		onConnected(false);
	}

	private void onConnected(boolean serverMode) {
		// client and server should now both be running
        if (BuildConfig.DEBUG) Log.d(TAG, "MainActivity.onConnected(serverMode=" +
        		serverMode + ")");
		String otherPlayerName = this.knowMeFragment.connected(serverMode);
        showFragment(this.knowMeFragment);
		setStatusMessage("Now connected to " + otherPlayerName);
	}

	@Override // BluetoothListener
	public void onMessageRead(byte[] data) {
		this.knowMeFragment.handleDataReceived(new String(data));
	}

	@Override // BluetoothListener
	public void onConnectionLost() {
        if (BuildConfig.DEBUG) Log.d(TAG, "MainActivity.onConnectionLost()");
		String otherPlayerName = this.knowMeFragment.onConnectionLost();
        if (this.closing) return;
        showFragment(this.bluetoothFragment);
		setStatusMessage(otherPlayerName + " has disconnected; waiting for another player");
		getSupportActionBar().setTitle("KnowMe: " +
				this.knowMeFragment.getPlayerName() + "; not connected");
	}

	@Override // BluetoothListener
	public void onError(String message) {
    	setStatusMessage(message);
	}

	@Override // BluetoothListener
	public void onMessageToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_LONG).show();
	}

	@Override // BluetoothListener
	public String getHelpString() {
		return "";
	}

	@Override // BluetoothListener
	public void setBluetoothService(BluetoothService bluetoothService) {
		if (bluetoothService == null) {
			Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_LONG).show();
			finish();
		}
		this.knowMeFragment.setBluetoothService(bluetoothService);
	}

	@Override // KnowMeActivityIF, BluetoothListener
	public void setStatusMessage(String message) {
		statusText.setText(message);
	}
}
