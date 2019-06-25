package com.jardenconsulting.cardapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.jardenconsulting.bluetooth.BluetoothFragment;
import com.jardenconsulting.bluetooth.BluetoothListener;
import com.jardenconsulting.bluetooth.BluetoothService;
import com.jardenconsulting.bluetooth.BluetoothService.BTState;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jarden.cardapp.DealFragment;
import jarden.quiz.BridgeQuiz;

/**
 * Shuffle and dealAndSort a pack of cards, showing my hand (Me), or
 * my and partner's hand (Us) or All hands. Can be played with
 * one player or on two devices linked via bluetooth.
 * 
 * Android app version of jarden.cards in Java course.
 *
 * @author john.denny@gmail.com
 *
 * TODO following:

change all multi-word terms to use _ instead of -

Show qa.helpText under qa.answer on screen; make hypertext life, as in ReviseQuiz

add bidding to 2-player

Devise a way to share BridgeQuiz between 2 parts of app

Possibly change PresetQuiz to use generics?

Check that other uses of PresetQuiz are okay
if so, remove commented-out getNextQuestion(int level)

Investigate illegalstateexception: cannot perform this action after onSaveInstanceState, after closing one of two-player devices; stack trace: bluetoothHandler.java:42 -> HotBridgeAtivity.onConnectionList -> showBlueToothFragment (HotBrigeActivity.java:130)

reviseit.txt***

 */
public class HotBridgeActivity extends AppCompatActivity
		implements BluetoothListener, DealFragment.Bridgeable {
    public static final String TAG = "hotbridge";
    private static final String quizFileName = "reviseit.txt";
    // "reviseitmini.txt"; // ***also change name of resource file***
    private static final String BLUETOOTH = "bluetooth";
    private static final String USE_BOOK_HANDS_KEY = "useBookHandsKey";
    private String appName;
	private FragmentManager fragmentManager;
	private BluetoothFragment bluetoothFragment;
	private DealFragment dealFragment;
	private TextView statusText;
	private boolean closing = false;
    private BridgeQuiz bridgeQuiz;
    private SharedPreferences sharedPreferences;
    private boolean useBookHands;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        try {
            File publicDirectory = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File file = new File(publicDirectory, quizFileName);
            if (BuildConfig.DEBUG) Log.d(TAG, file.getAbsolutePath());
            InputStream inputStream;
            if (file.canRead()) {
                inputStream = new FileInputStream(file);
            } else {
                inputStream = getResources().openRawResource(R.raw.reviseit);
            }
            this.bridgeQuiz = new BridgeQuiz(new InputStreamReader(inputStream));
        } catch (IOException e) {
            showMessage("unable to load quiz: " + e);
            return;
        }
        setContentView(R.layout.activity_main);
		this.appName = getResources().getString(R.string.app_name);
		this.statusText = findViewById(R.id.statusText);
		this.fragmentManager = getSupportFragmentManager();
		this.dealFragment = (DealFragment) fragmentManager.findFragmentById(R.id.cardFragment);
		showDealFragment();
		// see if bluetoothFragment has been retained from previous creation
        if (savedInstanceState != null) {
            this.bluetoothFragment = (BluetoothFragment) fragmentManager.findFragmentByTag(BLUETOOTH);
        }
        this.sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        useBookHands = sharedPreferences.getBoolean(USE_BOOK_HANDS_KEY, true);
        dealFragment.setUseBookHands(useBookHands);
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		MenuItem item = menu.findItem(R.id.bookHandsButton);
        item.setChecked(useBookHands);
		return true;
	}
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.twoPlayerButton) {
            boolean twoPlayer = !item.isChecked(); // isChecked returns old state!
            item.setChecked(twoPlayer); // do what Android should do for us!
            this.dealFragment.setTwoPlayer(twoPlayer);
            if (twoPlayer) {
                showBluetoothFragment();
            } else {
                dealFragment.setClientMode(false);
                showDealFragment();
            }
            return true; // menu item dealt with
        } else if (id == R.id.bookHandsButton) {
            boolean bookHands = !item.isChecked(); // isChecked returns old state!
            item.setChecked(bookHands); // do what Android should do for us!
            this.dealFragment.setUseBookHands(bookHands);
		    return true;
        } else if (id == R.id.reviseButton) {
            Intent intent = new Intent(this, ReviseQuizActivity.class);
            startActivity(intent);
            return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
    @Override
    protected void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) Log.d(TAG, "HotBridgeActivity.onPause()");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USE_BOOK_HANDS_KEY, dealFragment.isUseBookHands());
        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(BuildConfig.DEBUG) Log.d(TAG, "HotBridgeActivity.onDestroy()");
        this.closing = true;
    }

	private void showBluetoothFragment() {
 		FragmentTransaction ft = fragmentManager.beginTransaction();
 		ft.hide(dealFragment);
 		if (this.bluetoothFragment == null) {
 			bluetoothFragment = new BluetoothFragment();
 			ft.add(R.id.fragmentContainer, bluetoothFragment, BLUETOOTH);
 		} else {
 			ft.show(this.bluetoothFragment);
 		}
		ft.commit();
		setTitle(this.appName + " - not connected");
	}
	public void setConnected(boolean clientMode) {
        if(BuildConfig.DEBUG) Log.d(TAG, "HotBridgeActivity.setConnected()");
        dealFragment.setClientMode(clientMode);
        showDealFragment();
		setStatusMessage("");
	}
	private void showDealFragment() {
        String title;
        if (dealFragment.isTwoPlayer()) {
            title = "two player " + (dealFragment.isClientMode() ? "(C)" : "(S)");
        } else {
            title = "single player";
        }
		setTitle(this.appName + " - " + title);
		FragmentTransaction ft = fragmentManager.beginTransaction();
		if (this.bluetoothFragment != null) {
			ft.hide(bluetoothFragment);
		}
		ft.show(dealFragment);
		ft.commit();
	}
    private void showMessage(String message) {
        if (BuildConfig.DEBUG) Log.d(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

	@Override // BluetoothListener
	public void onStateChange(BTState state) {
		Toast.makeText(this, state.toString(), Toast.LENGTH_LONG).show();
	}
	@Override // BluetoothListener
	public void onConnectedAsServer(String deviceName) {
    	setConnected(false);
        dealFragment.shuffleDealShow();
	}
	@Override // BluetoothListener
	public void onConnectedAsClient(String deviceName) {
    	setConnected(true);
	}
	@Override // BluetoothListener
	public void onMessageRead(byte[] data) {
		dealFragment.onMessageRead(data);
	}
	@Override // BluetoothListener
	public void onConnectionLost() {
        if(BuildConfig.DEBUG) Log.d(TAG, "HotBridgeActivity.handleConnectionLost()");
        if (this.closing || !this.dealFragment.isTwoPlayer()) return;
        showBluetoothFragment();
		setStatusMessage("other device has disconnected; waiting for another player");
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
		return "some useful help text!";
	}
	@Override // BluetoothListener
	public void setBluetoothService(BluetoothService bluetoothService) {
		if (bluetoothService == null) {
			Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_LONG).show();
			showDealFragment();
		} else {
			this.dealFragment.setBluetoothService(bluetoothService);
		}
	}
	@Override // BluetoothListener, Bridgeable
	public void setStatusMessage(String message) {
		this.statusText.setText(message);
	}

    @Override // Bridgeable
    public BridgeQuiz getBridgeQuiz() {
        return bridgeQuiz;
    }
}
