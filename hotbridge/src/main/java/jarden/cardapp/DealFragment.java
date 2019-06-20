package jarden.cardapp;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jardenconsulting.bluetooth.BluetoothService;
import com.jardenconsulting.bluetooth.BluetoothService.BTState;
import com.jardenconsulting.cardapp.BuildConfig;
import com.jardenconsulting.cardapp.R;

import jarden.cards.BadBridgeTokenException;
import jarden.cards.CardPack;
import jarden.cards.Hand;
import jarden.cards.ParsedAnswer;
import jarden.cards.Player;
import jarden.cards.Suit;
import jarden.quiz.BridgeQuiz;
import jarden.quiz.QuestionAnswer;

import static com.jardenconsulting.cardapp.HotBridgeActivity.TAG;
import static jarden.quiz.BridgeQuiz.OPENING_BIDS;

/**
 * DealFragment is the main fragment of CardApp. It has up to 4 PlayerFragments
 * as components.
 * @author john.denny@gmail.com
 *
 */
public class DealFragment extends Fragment implements OnClickListener {
    public interface Bridgeable {
        BridgeQuiz getBridgeQuiz();
        void setStatusMessage(String message);
    }
    private static final int SHOW_ME = 0;
    private static final int SHOW_US = 1;
    private static final int SHOW_ALL = 2;
    private static final int MAX_BIDS = 24;

    private FragmentManager fragmentManager;
    /*
                      mePlayer     btClientMode
                      --------     ----------
        single user   West         false
        2-player:
            server:   West         false
            client:   East         true
         */
	private boolean btClientMode = false; // turned on if we connect to remote server
	private HandFragment northFragment;
	private HandFragment southFragment;
	private HandFragment eastFragment;
	private HandFragment westFragment;
	private int handToShow;
	private Button bidButton;
	private Button handsButton;
	private Player mePlayer = Player.West;
	private Player partnerPlayer = Player.East;
	private TextView suggestedBidTextView;
	private FragmentActivity activity;
	private Bridgeable bridgeable;
	private BluetoothService bluetoothService;
    private TextView[] bidTextViews;

	private CardPack cardPack;
    private QuestionAnswer lastQA;
	private boolean westDeal;
	private boolean biddingOver;
    private boolean shuffled = false;
    private boolean twoPlayer = false;
    private boolean firstBidPass;
    private BridgeQuiz bridgeQuiz;

    // @SuppressWarnings("deprecation")
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.onCreateView()");
        // get previous state of handsButton if fragment already exists:
        String handsButtonText = null;
        if (this.handsButton != null) handsButtonText = handsButton.getText().toString();
        View view = inflater.inflate(R.layout.deal_layout, container, false);
		this.activity = getActivity();
        this.fragmentManager = getChildFragmentManager();
        Button dealButton = view.findViewById(R.id.dealButton);
		dealButton.setOnClickListener(this);
		handsButton = view.findViewById(R.id.handsButton);
		handsButton.setOnClickListener(this);
        if (handsButtonText != null) {
            handsButton.setText(handsButtonText);
        }
		bidButton = view.findViewById(R.id.bidButton);
        this.bidButton.setEnabled(!twoPlayer);
        bidButton.setOnClickListener(this);
		this.suggestedBidTextView = view.findViewById(R.id.suggestedBidtextView);
        LinearLayout[] bidLayouts = new LinearLayout[6];
		bidLayouts[0] = view.findViewById(R.id.bid1Layout);
		bidLayouts[1] = view.findViewById(R.id.bid2Layout);
		bidLayouts[2] = view.findViewById(R.id.bid3Layout);
		bidLayouts[3] = view.findViewById(R.id.bid4Layout);
        bidLayouts[4] = view.findViewById(R.id.bid5Layout);
        bidLayouts[5] = view.findViewById(R.id.bid6Layout);
		bidTextViews = new TextView[MAX_BIDS];
		TextView bidTextView;
		LinearLayout.LayoutParams layoutParams =
				new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
		layoutParams.weight = 1;
		for (int i = 0; i < MAX_BIDS; i++) {
			bidTextView = new TextView(activity);
			bidTextView.setLayoutParams(layoutParams);
			bidTextView.setTextAppearance(activity, android.R.style.TextAppearance_Medium);
			bidTextViews[i] = bidTextView;
			bidLayouts[i/4].addView(bidTextView);
		}
        this.northFragment = (HandFragment) fragmentManager.findFragmentById(R.id.northFragment);
        this.southFragment = (HandFragment) fragmentManager.findFragmentById(R.id.southFragment);
        this.eastFragment = (HandFragment) fragmentManager.findFragmentById(R.id.eastFragment);
        this.westFragment = (HandFragment) fragmentManager.findFragmentById(R.id.westFragment);
		northFragment.setData(Player.North, cardPack);
		southFragment.setData(Player.South, cardPack);
		eastFragment.setData(Player.East, cardPack);
		westFragment.setData(Player.West, cardPack);
		return view;
	}
	@Override
	public void onResume() {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.onResume()");
		super.onResume();
        if (shuffled) showHands();
        else shuffleDealShow();
	}
	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.dealButton) {
			this.suggestedBidTextView.setText("");
			shuffleDealShow();
		} else if (id == R.id.handsButton) {
			String handsText = this.handsButton.getText().toString();
			if (handsText.equals("Us")) {
				handsButton.setText("All");
				this.handToShow = SHOW_US;
			} else if (handsText.equals("All")) {
				handsButton.setText("Me");
				this.handToShow = SHOW_ALL;
			} else {
				handsButton.setText("Us");
				this.handToShow = SHOW_ME;
			}
			showSelectedHands();
		} else if (id == R.id.bidButton) {
			getNextBid(this.mePlayer);
            if (!this.biddingOver) {
                this.suggestedBidTextView.setText(lastQA.answer);
                getNextBid(partnerPlayer);
            }
		} else {
			throw new RuntimeException("unrecognised view clicked: " + view);
		}
	}
	private void getNextBid(Player player) {
        Hand hand = cardPack.getHand(player);
        QuestionAnswer previousQA = lastQA;
        boolean openerPassed = false;
        String lastBid = null;
        try {
            lastQA = bridgeQuiz.getNextBid(hand, lastQA);
            if (lastQA != null) {
                ParsedAnswer pa = lastQA.getParsedAnswer();
                Suit trumpSuit = pa.getTrumpSuit();
                if (trumpSuit != null) {
                    Player partner = (player == Player.West ? Player.East : Player.West);
                    Hand partnerHand = cardPack.getHand(partner);
                    if (pa.isSuitSetter()) {
                        hand.setTrumpSuit(trumpSuit, true);
                        partnerHand.setTrumpSuit(trumpSuit, false);
                    } else {
                        hand.setTrumpSuit(trumpSuit, false);
                        partnerHand.setTrumpSuit(trumpSuit, true);
                    }
                    eastFragment.showHCP();
                    westFragment.showHCP();
                }
            }
        } catch (BadBridgeTokenException e) {
            // treat as no bid!
            if (BuildConfig.DEBUG) Log.e(TAG, e.toString());
            lastBid = "exception";
        }
        if (lastQA == null) {
            lastBid = "null";
            if (BuildConfig.DEBUG) Log.d(TAG, "null response from getNextBid(); hand=" + hand +
                    " previousQA=" + previousQA);
            // convert it into Pass!
            lastQA = new QuestionAnswer(previousQA.question + ", Pass", previousQA.answer);
        }
        if (lastQA.question.endsWith("Pass")) {
            if ((previousQA == OPENING_BIDS) && !firstBidPass) {
                openerPassed = true;
            }
            else {
                biddingOver = true;
                bidButton.setEnabled(false);
                String status = "biddingOver";
                if (lastBid != null) status = lastBid + " from last bid; " + status;
                bridgeable.setStatusMessage(status);
            }
        }
        showBids();
        if (openerPassed) {
            lastQA = OPENING_BIDS;
            firstBidPass = true;
        }
    }
    private void showBids() {
        String[] bids = lastQA.question.split("[ ,;]+");
        int j = westDeal ? 0: 2;
        if (firstBidPass) {
            bidTextViews[j++].setText("Pass");
            bidTextViews[j++].setText("-");
        }
        for (String bid: bids) {
            bidTextViews[j++].setText(bid);
            bidTextViews[j++].setText("-");
        }
        if (!biddingOver) bidTextViews[j].setText("?");
    }
	public void shuffleDealShow() {
        if (BuildConfig.DEBUG) Log.i(TAG, "DealFragment.shuffleDealShow()");
        cardPack.shuffle();
        if (twoPlayer) {
            if (bluetoothService != null && bluetoothService.getState() == BTState.connected) {
                byte[] data = cardPack.getDealAsBytes();
                bluetoothService.write(data);
            } else {
                Toast.makeText(activity, "Not connected", Toast.LENGTH_LONG).show();
            }
        }
        dealAndShow();
    }
    private void dealAndShow() {
        this.westDeal = !this.westDeal;
        firstBidPass = false;
        cardPack.deal(true); // i.e. dealShow with bias in our favour
        bridgeable.setStatusMessage("");
        resetBidList();
        lastQA = OPENING_BIDS;
        handsButton.setText("Us");
        this.handToShow = SHOW_ME;
        // game ends after 3 consecutive passes, or first 4
        this.biddingOver = false;
        this.bidButton.setEnabled(!twoPlayer);
        this.shuffled = true;
        if (!westDeal && !twoPlayer) { // TODO: same as in showHands()
            getNextBid(partnerPlayer);
        }
        showHands();
	}
    private void resetBidList() {
        for (TextView bidTextView: this.bidTextViews) bidTextView.setText("");
        int firstBidPos = westDeal ? 0 : 2;
        bidTextViews[firstBidPos].setText("?");
    }
	public void showHands() {
        if (BuildConfig.DEBUG) Log.i(TAG, "DealFragment.showHands()");
        northFragment.showHand();
        southFragment.showHand();
        eastFragment.showHand();
        westFragment.showHand();
        showSelectedHands();
        /*!!
        if (!twoPlayer) {
            // bidding doesn't yet work on twoPlayer; TODO: fix it!
            for (int i = 0; i < bidList.size(); i++) {
                this.bidTextViews[i].setText(bidList.get(i).toString());
            }
            indicateNextBid();
        }
        */
	}
    private void showSelectedHands() {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.showSelectedHands()");
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (this.handToShow == SHOW_US) {
            ft.hide(northFragment);
            ft.hide(southFragment);
            ft.show(westFragment);
            ft.show(eastFragment);
        } else if (this.handToShow == SHOW_ME) {
            ft.hide(northFragment);
            ft.hide(southFragment);
            if (mePlayer == Player.West) {
                ft.show(westFragment);
                ft.hide(eastFragment);
            } else {
                ft.show(eastFragment);
                ft.hide(westFragment);
            }
        } else { // defaults to All
            ft.show(northFragment);
            ft.show(southFragment);
            ft.show(westFragment);
            ft.show(eastFragment);
        }
        ft.commit();
    }
	public void setClientMode(boolean clientMode) {
        if(BuildConfig.DEBUG) {
        	Log.i(TAG, "DealFragment.setClientMode(" +
        			clientMode + ")");
        }
		this.btClientMode = clientMode;
		if (this.btClientMode) {
			this.mePlayer = Player.East;
			this.partnerPlayer = Player.West;
		} else {
			this.mePlayer = Player.West;
			this.partnerPlayer = Player.East;
		}
	}
	public boolean isClientMode() {
        return btClientMode;
    }
	// Fragment lifecycle methods:
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bridgeable = (Bridgeable) getActivity();
        this.bridgeQuiz = bridgeable.getBridgeQuiz();
    }
	@Override
	public void onAttach(Context context) {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.onAttach()");
		super.onAttach(context);
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.onConfigurationChanged()");
		super.onConfigurationChanged(newConfig);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) {
        	Log.i(TAG,
        			"DealFragment.onCreate(savedInstanceState=" +
        			(savedInstanceState==null?"null":"not null") +
        			")");
        }
		super.onCreate(savedInstanceState);
        setRetainInstance(true);
        cardPack = new CardPack();
        setClientMode(false); // because initially single user
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.onCreateOptionsMenu()");
		super.onCreateOptionsMenu(menu, inflater);
	}
	@Override
	public void onDestroy() {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.onDestroy()");
		super.onDestroy();
	}
	@Override
	public void onPause() {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.onPause()");
		super.onPause();
	}
	@Override
	public void onStart() {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.onStart()");
		super.onStart();
	}
	@Override
	public void onStop() {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.onStop()");
		super.onStop();
	}
	public void onMessageRead(byte[] data) {
		if (cardPack != null) {
			cardPack.setPackFromBytes(data);
			dealAndShow();
		} else {
            Toast.makeText(activity, "message read, but no card pack!", Toast.LENGTH_LONG).show();
		}
	}
	public void setBluetoothService(BluetoothService bluetoothService) {
		this.bluetoothService = bluetoothService;
	}
    public boolean isTwoPlayer() {
        return this.twoPlayer;
    }
    public void setTwoPlayer(boolean twoPlayer) {
        this.twoPlayer = twoPlayer;
    }
}
