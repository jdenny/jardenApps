package jarden.cardapp;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
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
import com.jardenconsulting.cardapp.HotBridgeActivity;
import com.jardenconsulting.cardapp.R;

import java.util.ArrayList;
import java.util.List;

import jarden.cards.CardPack;
import jarden.cards.CardPack.BidEnum;
import jarden.cards.Hand;
import jarden.cards.Player;

/**
 * DealFragment is the main fragment of CardApp. It has up to 4 PlayerFragments
 * as components.
 * @author john.denny@gmail.com
 *
 */
public class DealFragment extends Fragment implements OnClickListener {
    private final static int SHOW_ME = 0;
    private final static int SHOW_US = 1;
    private final static int SHOW_ALL = 2;

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
	private BluetoothService bluetoothService;
    private TextView[] bidTextViews;

	private CardPack cardPack;
	private BidEnum lastBid;
	private List<BidEnum> bidList;
	private boolean westDeal;
	private boolean primaryBid;
	private int bidNumber;
	private int consecutivePasses;
	private boolean biddingOver;
    private boolean shuffled = false;
    private boolean twoPlayer = false;

    @SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.i(HotBridgeActivity.TAG, "DealFragment.onCreateView()");
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
        LinearLayout[] bidLayouts = new LinearLayout[4];
		bidLayouts[0] = view.findViewById(R.id.bid1Layout);
		bidLayouts[1] = view.findViewById(R.id.bid2Layout);
		bidLayouts[2] = view.findViewById(R.id.bid3Layout);
		bidLayouts[3] = view.findViewById(R.id.bid4Layout);
		bidTextViews = new TextView[16];
		TextView bidTextView;
		LinearLayout.LayoutParams layoutParams =
				new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
		layoutParams.weight = 1;
		for (int i = 0; i < 16; i++) {
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
	private void resetBidList() {
		this.bidList.clear();
		this.bidNumber = 0;
		for (TextView bidTextView: this.bidTextViews) bidTextView.setText("");
	}
	@Override
	public void onResume() {
        if(BuildConfig.DEBUG) Log.i(HotBridgeActivity.TAG, "DealFragment.onResume()");
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
			Hand hand = cardPack.getHand(this.mePlayer);
			if (this.primaryBid) {
				this.lastBid = hand.getPrimaryBid();
				this.primaryBid = false;
			} else {
				this.lastBid = hand.getSecondaryBid(this.lastBid);
			}
			String bidVerbose = hand.getBidVerbose();
			this.suggestedBidTextView.setText(bidVerbose);
			if (this.lastBid == null) {
				Toast.makeText(activity, "null bid returned!", Toast.LENGTH_LONG).show();
			} else {
				addBid(this.lastBid);
				addBid(BidEnum.PASS);
				if (!this.biddingOver) {
					getPartnerBid();
				}
			}
		} else {
			throw new RuntimeException("unrecognised view clicked: " + view);
		}
	}
	public void shuffleDealShow() {
        if (BuildConfig.DEBUG) Log.i(HotBridgeActivity.TAG, "DealFragment.shuffleDealShow()");
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
        cardPack.deal(true); // i.e. dealShow with bias in our favour
        resetBidList();
        this.westDeal = !this.westDeal;
        this.primaryBid = true;
        this.lastBid = null;
        handsButton.setText("Us");
        this.handToShow = SHOW_ME;
        // game ends after 3 consecutive passes, or first 4
        this.consecutivePasses = -1;
        this.biddingOver = false;
        this.bidButton.setEnabled(!twoPlayer);
        this.shuffled = true;
        if (!westDeal && !twoPlayer) { // TODO: same as in showHands()
            addBid(BidEnum.None);
            addBid(BidEnum.None);
            getPartnerBid();
        }
        showHands();
	}
	public void showHands() {
        if (BuildConfig.DEBUG) Log.i(HotBridgeActivity.TAG, "DealFragment.showHands()");
        northFragment.showHand();
        southFragment.showHand();
        eastFragment.showHand();
        westFragment.showHand();
        showSelectedHands();
        if (!twoPlayer) {
            // bidding doesn't yet work on twoPlayer; TODO: fix it!
            for (int i = 0; i < bidList.size(); i++) {
                this.bidTextViews[i].setText(bidList.get(i).toString());
            }
            indicateNextBid();
        }
	}
	private void indicateNextBid() {
        if (!this.biddingOver) {
            this.bidTextViews[bidNumber].setText("?");
        }
    }
    private void showSelectedHands() {
        if(BuildConfig.DEBUG) Log.i(HotBridgeActivity.TAG, "DealFragment.showSelectedHands()");
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
	private void getPartnerBid() {
		Hand hand = cardPack.getHand(partnerPlayer);
		if (this.primaryBid) {
			this.lastBid = hand.getPrimaryBid();
			this.primaryBid = false;
		} else {
			this.lastBid = hand.getSecondaryBid(lastBid);
		}
		if (this.lastBid == null) {
			Toast.makeText(activity, "null bid returned!", Toast.LENGTH_LONG).show();
		} else {
			addBid(lastBid);
			addBid(BidEnum.PASS);
		}
	}
	private void addBid(BidEnum bid) {
		if (this.biddingOver) return;
		this.bidList.add(bid);
		if (bid == BidEnum.PASS) {
			if (++consecutivePasses >= 3) {
				this.bidButton.setEnabled(false);
				this.biddingOver = true;
			}
		} else {
			consecutivePasses = 0;
		}
		this.bidTextViews[bidNumber++].setText(bid.toString());
		indicateNextBid();
	}
	public void setClientMode(boolean clientMode) {
        if(BuildConfig.DEBUG) {
        	Log.i(HotBridgeActivity.TAG, "DealFragment.setClientMode(" +
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
        if(BuildConfig.DEBUG) {
        	Log.i(HotBridgeActivity.TAG,
    			"DealFragment.onActivityCreated(savedInstanceState=" +
    			(savedInstanceState==null?"null":"not null") + ")");
        }
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public void onAttach(Context context) {
        if(BuildConfig.DEBUG) Log.i(HotBridgeActivity.TAG, "DealFragment.onAttach()");
		super.onAttach(context);
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
        if(BuildConfig.DEBUG) Log.i(HotBridgeActivity.TAG, "DealFragment.onConfigurationChanged()");
		super.onConfigurationChanged(newConfig);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) {
        	Log.i(HotBridgeActivity.TAG,
        			"DealFragment.onCreate(savedInstanceState=" +
        			(savedInstanceState==null?"null":"not null") +
        			")");
        }
		super.onCreate(savedInstanceState);
        setRetainInstance(true);
        cardPack = new CardPack();
        bidList = new ArrayList<>();
        setClientMode(false); // because initially single user
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(BuildConfig.DEBUG) Log.i(HotBridgeActivity.TAG, "DealFragment.onCreateOptionsMenu()");
		super.onCreateOptionsMenu(menu, inflater);
	}
	@Override
	public void onDestroy() {
        if(BuildConfig.DEBUG) Log.i(HotBridgeActivity.TAG, "DealFragment.onDestroy()");
		super.onDestroy();
	}
	@Override
	public void onPause() {
        if(BuildConfig.DEBUG) Log.i(HotBridgeActivity.TAG, "DealFragment.onPause()");
		super.onPause();
	}
	@Override
	public void onStart() {
        if(BuildConfig.DEBUG) Log.i(HotBridgeActivity.TAG, "DealFragment.onStart()");
		super.onStart();
	}
	@Override
	public void onStop() {
        if(BuildConfig.DEBUG) Log.i(HotBridgeActivity.TAG, "DealFragment.onStop()");
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
