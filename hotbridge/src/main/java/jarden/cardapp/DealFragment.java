package jarden.cardapp;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.jardenconsulting.cardapp.MainActivity;
import com.jardenconsulting.cardapp.R;

import java.lang.reflect.Field;
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
	/*
	              mePlayer     btClientMode
	              --------     ----------
	single user   West         false
	2-player:
		server:   West         false
		client:   East         true
	 */
	private FragmentManager fragmentManager;
	private boolean btClientMode = false; // turned on if we connect to remote server
	private HandFragment northFragment;
	private HandFragment southFragment;
	private HandFragment eastFragment;
	private HandFragment westFragment;
	private final static int SHOW_ME = 0;
	private final static int SHOW_US = 1;
	private final static int SHOW_ALL = 2;
	private int handToShow;
	private Button bidButton;
	private Button handsButton;
	private Player mePlayer = Player.West;
	private Player partnerPlayer = Player.East;
	private TextView suggestedBidTextView;
	private MainActivity mainActivity;
	private BluetoothService bluetoothService;
	private boolean biased = true; // shuffle & deal in our favour
	private LinearLayout[] bidLayouts;
	private TextView[] bidTextViews;

	private CardPack cardPack;
	private BidEnum lastBid;
	private List<BidEnum> bidList;
	private boolean westDeal;
	private boolean primaryBid;
	private int bidNumber;
	private int consecutivePasses;
	private boolean biddingOver;
	
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.i(MainActivity.TAG, "DealFragment.onCreateView()");
		View view = inflater.inflate(R.layout.deal_layout, container, false);
		this.mainActivity = (MainActivity) getActivity();
		this.fragmentManager = mainActivity.getSupportFragmentManager();

		Button dealButton = view.findViewById(R.id.dealButton);
		dealButton.setOnClickListener(this);
		handsButton = view.findViewById(R.id.handsButton);
		handsButton.setOnClickListener(this);
		bidButton = view.findViewById(R.id.bidButton);
		bidButton.setOnClickListener(this);
		this.suggestedBidTextView = view.findViewById(R.id.suggestedBidtextView);
		bidList = new ArrayList<>();
		bidLayouts = new LinearLayout[4];
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
			bidTextView = new TextView(mainActivity);
			bidTextView.setLayoutParams(layoutParams);
			bidTextView.setTextAppearance(mainActivity, android.R.style.TextAppearance_Medium);
			bidTextViews[i] = bidTextView;
			bidLayouts[i/4].addView(bidTextView);
		}
		northFragment = new HandFragment();
		southFragment = new HandFragment();
		eastFragment = new HandFragment();
		westFragment = new HandFragment();
		FragmentManager childFragmentManager = getChildFragmentManager();
		FragmentTransaction transaction = childFragmentManager.beginTransaction();
		transaction.add(R.id.northContainer, northFragment);
		transaction.add(R.id.southContainer, southFragment);
		transaction.add(R.id.eastContainer, eastFragment);
		transaction.add(R.id.westContainer, westFragment);
		transaction.commit();
		cardPack = new CardPack();
		northFragment.setData(Player.North, cardPack);
		southFragment.setData(Player.South, cardPack);
		eastFragment.setData(Player.East, cardPack);
		westFragment.setData(Player.West, cardPack);
		setClientMode(false); // because initially single user
		return view;
	}
	private void resetBidList() {
		this.bidList.clear();
		this.bidNumber = 0;
		for (TextView bidTextView: this.bidTextViews) bidTextView.setText("");
	}
	@Override
	public void onResume() {
        if(BuildConfig.DEBUG) Log.i(MainActivity.TAG, "DealFragment.onResume()");
		super.onResume();
		if (!this.btClientMode) shuffleAndDeal();
	}
	@Override
	public void onDetach() {
        if(BuildConfig.DEBUG) Log.i(MainActivity.TAG, "DealFragment.onDetach()");
	    super.onDetach();
	    
	    // see http://stackoverflow.com/questions/15207305/getting-the-error-java-lang-illegalstateexception-activity-has-been-destroyed/15656428#15656428
	    try {
	        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
	        childFragmentManager.setAccessible(true);
	        childFragmentManager.set(this, null);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.dealButton) {
			this.suggestedBidTextView.setText("");
			shuffleAndDeal();
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
			showHands();
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
				Toast.makeText(mainActivity, "null bid returned!", Toast.LENGTH_LONG).show();
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
	public void shuffleAndDeal() {
        if(BuildConfig.DEBUG) Log.i(MainActivity.TAG, "DealFragment.shuffleAndDeal()");
		cardPack.shuffle();
		if (mainActivity.isTwoPlayer()) {
	        if (bluetoothService.getState() == BTState.connected) {
	    		byte[] data = cardPack.getDealAsBytes();
	        	bluetoothService.write(data);
	        } else {
	            Toast.makeText(mainActivity, "Not connected", Toast.LENGTH_LONG).show();
	        }
		}
		dealAndShow();
	}
	private void dealAndShow() {
        if(BuildConfig.DEBUG) Log.i(MainActivity.TAG, "DealFragment.dealAndShow()");
		cardPack.deal(biased);
		resetBidList();
		handsButton.setText("Us");
		this.primaryBid = true;
		this.lastBid = null;
		this.handToShow = SHOW_ME;
		this.westDeal = !this.westDeal;
		// game ends after 3 consecutive passes, or first 4
		this.consecutivePasses = -1;
		this.biddingOver = false;
		this.bidButton.setEnabled(true);
		northFragment.getHand();
		southFragment.getHand();
		eastFragment.getHand();
		westFragment.getHand();
		showHands();
		if (westDeal) {
			this.bidTextViews[bidNumber].setText("?");
		} else {
			this.bidNumber += 2;
			getPartnerBid();
		}
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
			Toast.makeText(mainActivity, "null bid returned!", Toast.LENGTH_LONG).show();
		} else {
			addBid(lastBid);
			addBid(BidEnum.PASS);
			if (!this.biddingOver) {
				this.bidTextViews[bidNumber].setText("?");
			}
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
	}
	private void showHands() {
        if(BuildConfig.DEBUG) Log.i(MainActivity.TAG, "DealFragment.showHands()");
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
        	Log.i(MainActivity.TAG, "DealFragment.setClientMode(" +
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
	// Fragment lifecycle methods:
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) {
        	Log.i(MainActivity.TAG,
    			"DealFragment.onActivityCreated(savedInstanceState=" +
    			(savedInstanceState==null?"null":"not null") + ")");
        }
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public void onAttach(Context context) {
        if(BuildConfig.DEBUG) Log.i(MainActivity.TAG, "DealFragment.onAttach()");
		super.onAttach(context);
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
        if(BuildConfig.DEBUG) Log.i(MainActivity.TAG, "DealFragment.onConfigurationChanged()");
		super.onConfigurationChanged(newConfig);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) {
        	Log.i(MainActivity.TAG,
        			"DealFragment.onCreate(savedInstanceState=" +
        			(savedInstanceState==null?"null":"not null") +
        			")");
        }
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(BuildConfig.DEBUG) Log.i(MainActivity.TAG, "DealFragment.onCreateOptionsMenu()");
		super.onCreateOptionsMenu(menu, inflater);
	}
	@Override
	public void onDestroy() {
        if(BuildConfig.DEBUG) Log.i(MainActivity.TAG, "DealFragment.onDestroy()");
		super.onDestroy();
	}
	@Override
	public void onPause() {
        if(BuildConfig.DEBUG) Log.i(MainActivity.TAG, "DealFragment.onPause()");
		super.onPause();
	}
	@Override
	public void onStart() {
        if(BuildConfig.DEBUG) Log.i(MainActivity.TAG, "DealFragment.onStart()");
		super.onStart();
	}
	@Override
	public void onStop() {
        if(BuildConfig.DEBUG) Log.i(MainActivity.TAG, "DealFragment.onStop()");
		super.onStop();
	}
	public void onMessageRead(byte[] data) {
		if (cardPack != null) {
			cardPack.setPackFromBytes(data);
			dealAndShow();
		} else {
            Toast.makeText(mainActivity, "message read, but no card pack!", Toast.LENGTH_LONG).show();
		}
	}
	public void setBluetoothService(BluetoothService bluetoothService) {
		this.bluetoothService = bluetoothService;
	}
}
