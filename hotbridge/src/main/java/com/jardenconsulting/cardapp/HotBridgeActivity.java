package com.jardenconsulting.cardapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.jardenconsulting.bluetooth.BluetoothFragment;
import com.jardenconsulting.bluetooth.BluetoothListener;
import com.jardenconsulting.bluetooth.BluetoothService;
import com.jardenconsulting.bluetooth.BluetoothService.BTState;

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
need to alert before bidding (as in bridj)

verbose mode to show meaning of partner's bid; could be hypertext on bids in table
go further and show each bid with meaning, as in quiz; or even open quiz on that
QA! (Send QA index); get both activities to share BridgeQuiz, so that this HotBridge calls bridgeQuiz.setDetailQA() and Quiz then proceeds as normal, calling getNextQuestion

keep track of failed deals, current deals, as in quiz

Fix crash shown in pre-launch report

"About Hot Chilli" menu item which shows version

currently if both hands have 5 hearts, nothing added to hcp; can we
calculate extra trumps? e.g. bidding says 4+ and we have 5, so one extra trump
examples:
A: 4+ hearts
I have extras if >4 hearts
send message to partner assuming she has 4 hearts, so any more are extras
A: 3 hearts
partnerHand.assumeTrumps(5);

if no response, but previous bid contained "to-play", count no response as pass

MINOR
change all multi-word terms to use _ instead of -

Show qa.helpText under qa.answer on screen; make hypertext live, as in ReviseQuiz

Devise a way to share BridgeQuiz between 2 parts of app

Possibly change PresetQuiz to use generics?

Check that other uses of PresetQuiz are okay
if so, remove commented-out getNextQuestion(int level)

Investigate illegalstateexception: cannot perform this action after onSaveInstanceState,
after closing one of two-player devices; stack trace:
    bluetoothHandler.java:42 -> HotBridgeAtivity.onConnectionList ->
    showBlueToothFragment (HotBrigeActivity.java:130)
 */
public class HotBridgeActivity extends AppCompatActivity
		implements BluetoothListener, DealFragment.Bridgeable {
    public static final String TAG = "hotbridge";
//    private static final String quizFileName = "reviseit.txt";
//    // "reviseitmini.txt"; // ***also change name of resource file***
    private static final String BLUETOOTH = "bluetooth";
    private static final String RANDOM_DEALS_KEY = "randomDealsKey";
    private String appName;
	private FragmentManager fragmentManager;
	private BluetoothFragment bluetoothFragment;
	private DealFragment dealFragment;
	private TextView statusText;
	private boolean closing = false;
//    private BridgeQuiz bridgeQuiz;
    private SharedPreferences sharedPreferences;
    private boolean randomDeals;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//        try {
//            File publicDirectory = Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DOWNLOADS);
//            File file = new File(publicDirectory, quizFileName);
//            if (BuildConfig.DEBUG) Log.d(TAG, file.getAbsolutePath());
//            InputStream inputStream;
//            if (file.canRead()) {
//                inputStream = new FileInputStream(file);
//            } else {
//                inputStream = getResources().openRawResource(R.raw.reviseit);
//            }
//            InputStream inputStream = getResources().openRawResource(R.raw.reviseit);
//            // TODO: revert to normal constructor for BridgeQuiz
//            this.bridgeQuiz = new BridgeQuiz(new InputStreamReader(inputStream));
//            this.bridgeQuiz = BridgeQuiz.getInstance(new InputStreamReader(inputStream));
//        } catch (IOException e) {
//            showMessage("unable to load quiz: " + e);
//            return;
//        }
        // TODO: check backstack for returning from quiz to deals
        // see https://developer.android.com/training/basics/fragments/fragment-ui
        // TODO: does the app survive screen rotation?
        // should bridgeQuiz be saved in DealFragment, so that it is retained?
        setContentView(R.layout.activity_main);
		this.appName = getResources().getString(R.string.app_name);
		this.statusText = findViewById(R.id.statusText);
		this.fragmentManager = getSupportFragmentManager();
		this.dealFragment = (DealFragment) fragmentManager.findFragmentById(R.id.dealFragment);
		showDealFragment();
		// see if bluetoothFragment has been retained from previous creation
        if (savedInstanceState != null) {
            this.bluetoothFragment = (BluetoothFragment) fragmentManager.findFragmentByTag(BLUETOOTH);
        }
        this.sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        randomDeals = sharedPreferences.getBoolean(RANDOM_DEALS_KEY, false);
        dealFragment.setRandomDeals(randomDeals);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) Log.d(TAG, "HotBridgeActivity.onPause()");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(RANDOM_DEALS_KEY, dealFragment.isRandomDeals());
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
        return dealFragment.getBridgeQuiz();
    }

    @Override // Bridgeable
    public void setTitle(String message) {
        super.setTitle(message);
    }

    @Override // Bridgeable
    public void setTwoPlayer(boolean twoPlayer) {
        if (twoPlayer) {
            showBluetoothFragment();
        } else {
            dealFragment.setClientMode(false);
            showDealFragment();
        }
    }
    @Override // Bridgeable
    public void showMessage(String message) {
        if (BuildConfig.DEBUG) Log.d(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
