package com.jardenconsulting.cardapp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.jardenconsulting.bluetooth.BluetoothFragment;
import com.jardenconsulting.bluetooth.BluetoothListener;
import com.jardenconsulting.bluetooth.BluetoothService;
import com.jardenconsulting.bluetooth.BluetoothService.BTState;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import jarden.app.revisequiz.FreakWizFragment;
import jarden.cardapp.DealFragment;
import jarden.cardapp.ReviseQuizFragment;
import jarden.quiz.BridgeQuiz;
import jarden.quiz.PresetQuiz;
import jarden.quiz.QuestionAnswer;

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
add 'alert' and 'forcing-artificial' and 'forcing-compelled' to reviseit.txt

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

Possibly change PresetQuiz to use generics?

Check that other uses of PresetQuiz are okay
if so, remove commented-out getNextQuestion(int level)

Investigate illegalstateexception: cannot perform this action after onSaveInstanceState,
after closing one of two-player devices; stack trace:
    bluetoothHandler.java:42 -> HotBridgeAtivity.onConnectionList ->
    showBlueToothFragment (HotBrigeActivity.java:130)
 */
public class HotBridgeActivity extends AppCompatActivity
		implements BluetoothListener, DealFragment.Bridgeable,
        FreakWizFragment.Quizable {
    public static final String TAG = "hotbridge";
    private static final String BLUETOOTH = "bluetooth";
    private static final String REVISE_QUIZ = "reviseQuiz";
    private static final int[] notesResIds = {
            R.string.autofit,
            R.string.balanced,
            R.string.compelled,
            R.string.disturbed,
            R.string.Double,
            R.string.guard,
            R.string.help,
            R.string.invitational_plus,
            R.string.keycard_ask,
            R.string.preempt,
            R.string.queen_ask,
            R.string.raw,
            R.string.relay,
            R.string.responses_to_relay,
            R.string.responses_to_1D,
            R.string.responses_to_1NT,
            R.string.sandpit,
            R.string.side_suit,
            R.string.skew,
            R.string.strong_fit,
            R.string.strong_or_skew,
            R.string.suit_setter,
            R.string.threeNT,
            R.string.to_play,
            R.string.two_choice,
            R.string.values_for_5,
            R.string.waiting
    };

    private String appName;
	private FragmentManager fragmentManager;
	private BluetoothFragment bluetoothFragment;
	private DealFragment dealFragment;
    private ReviseQuizFragment reviseQuizFragment;
	private boolean closing = false;
    private BridgeQuiz bridgeQuiz;
    private boolean quizFragmentShowing;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // TODO: does the app survive screen rotation?
        setContentView(R.layout.activity_main);
		this.appName = getResources().getString(R.string.app_name);
		this.fragmentManager = getSupportFragmentManager();
		this.dealFragment = (DealFragment) fragmentManager.findFragmentById(R.id.dealFragment);
		showDealFragment();
		bridgeQuiz = dealFragment.getBridgeQuiz();
		// see if bluetoothFragment has been retained from previous creation
        if (savedInstanceState != null) {
            this.bluetoothFragment =
                    (BluetoothFragment) fragmentManager.findFragmentByTag(BLUETOOTH);
            this.reviseQuizFragment =
                    (ReviseQuizFragment) fragmentManager.findFragmentByTag(REVISE_QUIZ);
        }
    }

    @Override // Activity
    public void onBackPressed() {
        if (BuildConfig.DEBUG) Log.d(TAG, "onBackPressed()");
        if (quizFragmentShowing) {
            showDealFragment();
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit",
                    Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(BuildConfig.DEBUG) Log.d(TAG, "HotBridgeActivity.onDestroy()");
        this.closing = true;
    }
    @Override // Bridgeable
    public void showReviseQuizFragment() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.hide(dealFragment);
        if (this.reviseQuizFragment == null) {
            reviseQuizFragment = new ReviseQuizFragment();
            ft.add(R.id.fragmentContainer2, reviseQuizFragment, REVISE_QUIZ);
        } else {
            ft.show(this.reviseQuizFragment);
        }
        ft.commit();
        quizFragmentShowing = true;
        int titleId = bridgeQuiz.isLearnMode() ? R.string.learnMode : R.string.practiceMode;
        setTitle(titleId);
    }

    @Override // Bridgeable
    public void showDetailQA(QuestionAnswer detailQA) {
        showReviseQuizFragment();
        reviseQuizFragment.showDetailQA(detailQA);
        setTitle(dealFragment.getDealName());
    }

    @Override // Bridgeable
    public void showBluetoothFragment() {
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
	private void showDealFragment() {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		if (this.bluetoothFragment != null) ft.hide(bluetoothFragment);
		if (this.reviseQuizFragment != null) ft.hide(reviseQuizFragment);
		ft.show(dealFragment);
		ft.commit();
		quizFragmentShowing = false;
		setTitle(dealFragment.getDealName());
	}
    private void setConnected(boolean clientMode) {
        if(BuildConfig.DEBUG) Log.d(TAG, "HotBridgeActivity.setConnected()");
        dealFragment.setClientMode(clientMode);
        showDealFragment();
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
        if (!this.closing && this.dealFragment.isTwoPlayer()) {
            showBluetoothFragment();
            setStatusMessage("other device has disconnected; waiting for another player");
        }
	}
	@Override // BluetoothListener
	public void onError(String message) {
    	setStatusMessage(message);
    	dealFragment.setSinglePlayer();
        showDealFragment();
    }
	@Override // BluetoothListener
	public void onMessageToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_LONG).show();
	}
	@Override // BluetoothListener
	public String getHelpString() {
		return "Use Bluetooth to connect to another device running HotBridge";
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
	@Override // BluetoothListener
    public void setStatusMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

    @Override // Bridgeable
    public void stopBluetooth() {
        if (bluetoothFragment != null) bluetoothFragment.close();
    }
    @Override // Bridgeable
    public void showMessage(String message) {
        if (BuildConfig.DEBUG) Log.d(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    @Override // Quizable
    public PresetQuiz getReviseQuiz() {
        return bridgeQuiz;
    }

    @Override // Quizable
    public int[] getNotesResIds() {
        return notesResIds;
    }
}
