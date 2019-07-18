package jarden.cardapp;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.view.MenuItem;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jarden.cards.BadBridgeTokenException;
import jarden.cards.BookHand;
import jarden.cards.CardPack;
import jarden.cards.Hand;
import jarden.cards.Player;
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
        void showMessage(String message);
        void showReviseQuizFragment();
        void showBluetoothFragment();
        void stopBluetooth();
        void showDetailQA(QuestionAnswer detailQA);
    }
    private static final String BOOK_HANDS_INDEX_KEY = "bookHandsIndexKey";
    private static final String BOOK_HANDS_LAP_KEY = "bookHandsLapKey";
    private static final String RANDOM_DEALS_KEY = "randomDealsKey";

    private static final int SHOW_ME = 0;
    private static final int SHOW_US = 1;
    private static final int SHOW_ALL = 2;
    private static final int MAX_BIDS = 24;

    private FragmentManager fragmentManager;
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
    private String detailStr;


    private CardPack cardPack;
	private boolean randomDeals = false;
	private final BookHand[] bookHands = BookHand.getBookHands();
	private int bookHandsIndex = -1;
	/*
	    bookHandsLap used to control the bookHands; we go through all the hands
	    4 times (4 laps): first with me having bookHand.handWest (i.e. bookHandWest = true)
	    and dealer alternating but starting with me (i.e. westDeal = true);
	    then me having bookHand.handEast (even though from a UI point of view I am West!
	    then the next two laps similar to the first, except that the
	    dealer is switched
	    in summary:
	    lap myHand dealer (see key to headings below)
	      0   West   West
	      1   East   West
	      2   West   East
	      3   East   East
	      lap is bookHandsLap; myHand based on bookHandWest, i.e. true means I am West
	      dealer, based on westDeal, if true means I deal for the 1st hand, then it alternates
	 */
    private int bookHandsLap = 0;
	private boolean bookHandWest = true;
    private boolean westDeal = false;
    private SharedPreferences sharedPreferences;

    private QuestionAnswer lastQA;
	private boolean biddingOver;
    private boolean shuffled = false;
    private boolean twoPlayer = false;
    private boolean firstBidPass;
    private BridgeQuiz bridgeQuiz;
    private String dealName;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.onCreateView()");
        // get previous state of handsButton if fragment already exists:
        String handsButtonText = null;
        if (this.handsButton != null) handsButtonText = handsButton.getText().toString();
        View view = inflater.inflate(R.layout.deal_layout, container, false);
        setHasOptionsMenu(true);
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
        detailStr = getResources().getString(R.string.detail);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.onCreateOptionsMenu(...)");
        inflater.inflate(R.menu.activity_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
        MenuItem item = menu.findItem(R.id.randomDealButton);
        item.setChecked(randomDeals);
        item = menu.findItem(R.id.twoPlayerButton);
        item.setChecked(twoPlayer);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.twoPlayerButton) {
            twoPlayer = !item.isChecked(); // isChecked returns old state!
            item.setChecked(twoPlayer); // do what Android should do for us!
            // if turning on twoPlayer, clientMode will be set depending
            // on result of bluetooth connection
            if (twoPlayer) {
                bridgeable.showBluetoothFragment();
            } else {
                setClientMode(false);
                bridgeable.stopBluetooth();
            }
            return true; // menu item dealt with
        } else if (id == R.id.randomDealButton) {
            boolean randomDeal = !item.isChecked(); // isChecked returns old state!
            item.setChecked(randomDeal); // do what Android should do for us!
            setRandomDeals(randomDeal);
            shuffleDealShow();
            return true;
        } else if (id == R.id.reviseButton) {
            bridgeable.showReviseQuizFragment();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onPause() {
        if (BuildConfig.DEBUG) Log.i(TAG, "DealFragment.onPause()");
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(RANDOM_DEALS_KEY, randomDeals);
        editor.putInt(BOOK_HANDS_INDEX_KEY, bookHandsIndex);
        editor.putInt(BOOK_HANDS_LAP_KEY, bookHandsLap);
        editor.apply();
    }

    @Override
	public void onResume() {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.onResume()");
		super.onResume();
        if (shuffled) showHands();
        else shuffleDealShow();
        if (lastQA != null) showBids();
	}
	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.dealButton) {
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
		    String buttonText = bidButton.getText().toString();
		    if (buttonText.equals(detailStr)) {
		        bridgeable.showDetailQA(lastQA);
            } else {
                getNextBid(this.mePlayer);
                if (!this.biddingOver) {
                    this.suggestedBidTextView.setText(lastQA.answer);
                    getNextBid(partnerPlayer);
                }
            }
		} else {
			throw new RuntimeException("unrecognised view clicked: " + view);
		}
	}
    public String getDealName() {
	    return dealName;
    }

    private void setRandomDeals(boolean randomDeals) {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.setRandomDeals(" +
                randomDeals + ")");
        this.randomDeals = randomDeals;
    }
    private void getNextBid(Player player) {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.getNextBid(" + player + ")");
        Hand hand = cardPack.getHand(player);
        Player partner = (player == Player.West ? Player.East : Player.West);
        Hand partnerHand = cardPack.getHand(partner);
        QuestionAnswer previousQA = lastQA;
        boolean openerPassed = false;
        String lastBid = null;
        try {
            lastQA = bridgeQuiz.getNextBid(hand, lastQA, partnerHand);
            eastFragment.showHCP();
            westFragment.showHCP();
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
                if (randomDeals) {
                    bidButton.setEnabled(false);
                } else {
                    bidButton.setText(getString(R.string.detail));
                }
                String status = "biddingOver";
                if (lastBid != null) status = lastBid + " from last bid; " + status;
                Toast.makeText(getContext(), status, Toast.LENGTH_LONG).show();
            }
        }
        showBids();
        if (openerPassed) {
            lastQA = OPENING_BIDS;
            firstBidPass = true;
        }
    }
    private void showBids() {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.showBids(); lastQA=" + lastQA);
        int j = westDeal ? 0 : 2;
	    if (lastQA != null && lastQA != OPENING_BIDS) {
            String[] bids = lastQA.question.split("[ ,;]+");
            if (firstBidPass) {
                bidTextViews[j++].setText("Pass");
                bidTextViews[j++].setText("-");
            }
            for (String bid : bids) {
                bidTextViews[j++].setText(bid);
                bidTextViews[j++].setText("-");
            }
        }
        if (!biddingOver) bidTextViews[j].setText("?");
    }

    /**
     * if randomDeals: shuffle and deal the cards
     * else: get next book-hand (preset hands)
     */
    public void shuffleDealShow() {
        if (BuildConfig.DEBUG) Log.i(TAG, "DealFragment.shuffleDealShow()");
        this.suggestedBidTextView.setText("");
        bidButton.setText(R.string.bid);
        if (!randomDeals) {
            if (++bookHandsIndex >= bookHands.length) {
                bookHandsIndex = 0;
                if (++bookHandsLap >= 4) bookHandsLap = 0;
                evaluateBookHandsLap();
            }
            BookHand bookHand = bookHands[bookHandsIndex];
            bookHand.handWest.reset();
            bookHand.handEast.reset();
            cardPack.setBookHand(bookHand, bookHandWest);
            dealName = "book hand " + (bookHandsIndex + 1) + " (" + bookHand.name + ")";
        } else {
            cardPack.shuffleAndDeal(true); // i.e. dealShow with bias in our favour
            dealName = "random deal";
        }
        this.westDeal = !this.westDeal;
        if (twoPlayer) {
            if (bluetoothService != null && bluetoothService.getState() == BTState.connected) {
                // boolean randomDeals, boolean westDeal, int dealNumber
                byte[] prefix = new byte[3];
                if (randomDeals) {
                    prefix[0] = 1;
                    // prefix[2] = 0; // i.e. already set to zero!
                } else {
                    // prefix[0] = 0;
                    prefix[2] = (byte)(bookHandsIndex + 1);
                }
                if (westDeal) prefix[1] = 1;
                // else prefix[1] = 0;
                byte[] data = cardPack.getDealAsBytes(randomDeals, prefix);
                bluetoothService.write(data);
            } else {
                Toast.makeText(activity, "Not connected", Toast.LENGTH_LONG).show();
            }
        }
        showDeal();
    }

    private void evaluateBookHandsLap() {
        bookHandWest = (bookHandsLap % 2 == 0);
        westDeal = !(bookHandsLap < 2); // the opposite of what you would think,
        // because it's about to be reversed, as part of alternating the dealer
    }

    private void showDeal() {
        if (BuildConfig.DEBUG) Log.i(TAG, "DealFragment.showDeal()");
        firstBidPass = false;
        for (TextView bidTextView: this.bidTextViews) bidTextView.setText("");
        lastQA = OPENING_BIDS;
        handsButton.setText("Us");
        this.handToShow = SHOW_ME;
        // game ends after 3 consecutive passes, or first 4
        this.biddingOver = false;
        this.bidButton.setEnabled(true);
        this.shuffled = true;
        boolean iAmWest = (mePlayer == Player.West);
        if (iAmWest == westDeal) {
            int bidPos = westDeal ? 0 : 2;
            bidTextViews[bidPos].setText("?");
        } else {
            getNextBid(partnerPlayer);
        }
        showHands();
	}
	private void showHands() {
        if (BuildConfig.DEBUG) Log.i(TAG, "DealFragment.showHands()");
        activity.setTitle(dealName);
        westFragment.showHand();
        eastFragment.showHand();
        if (randomDeals) {
            northFragment.showHand();
            southFragment.showHand();
        }
        showSelectedHands();
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
            if (randomDeals) {
                ft.show(northFragment);
                ft.show(southFragment);
            }
            ft.show(westFragment);
            ft.show(eastFragment);
        }
        ft.commit();
    }

    /**
     *               mePlayer     clientMode
     *               --------     ----------
     * single user   West         false
     * 2-player:
     *      server   West         false
     *      client   East         true
     *
     * @param clientMode true if we are connected to remote server
     */
	public void setClientMode(boolean clientMode) {
        if(BuildConfig.DEBUG) {
        	Log.i(TAG, "DealFragment.setClientMode(" +
        			clientMode + ")");
        }
		if (clientMode) {
			this.mePlayer = Player.East;
			this.partnerPlayer = Player.West;
		} else {
			this.mePlayer = Player.West;
			this.partnerPlayer = Player.East;
		}
	}
    public BridgeQuiz getBridgeQuiz() {
        return bridgeQuiz;
    }
    // Fragment lifecycle methods:
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bridgeable = (Bridgeable) getActivity();
        this.sharedPreferences = getActivity().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        randomDeals = sharedPreferences.getBoolean(RANDOM_DEALS_KEY, false);
        setRandomDeals(randomDeals);
        int index = sharedPreferences.getInt(BOOK_HANDS_INDEX_KEY, -1);
        --index; // subtract 1, to repeat most recent deal
        if (index < -1) index = -1;
        bookHandsIndex = index;
        bookHandsLap = sharedPreferences.getInt(BOOK_HANDS_LAP_KEY, 0);
        evaluateBookHandsLap();
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
        	Log.i(TAG, "DealFragment.onCreate(savedInstanceState=" +
        			(savedInstanceState==null?"null":"not null") + ")");
        }
		super.onCreate(savedInstanceState);
        setRetainInstance(true);
        cardPack = new CardPack();
        setClientMode(false); // because initially single user
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.reviseit);
            this.bridgeQuiz = new BridgeQuiz(new InputStreamReader(inputStream));
        } catch (IOException e) {
            bridgeable.showMessage("unable to load quiz: " + e);
        }
    }
	@Override
	public void onDestroy() {
        if(BuildConfig.DEBUG) Log.i(TAG, "DealFragment.onDestroy()");
		super.onDestroy();
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

    /**
     * @param data Expected structure:
     *      0   boolean randomDeals
     *      1   boolean westDeal
     *      2   int dealNumber
     *      3+  26 or 52 cards
     */
	public void onMessageRead(byte[] data) {
        if (cardPack != null) {
            randomDeals = (data[0] == 1);
            westDeal = (data[1] == 1);
            dealName = (data[2] == 0) ? "random deal" : "book hand " + data[2];
			cardPack.setDealFromBytes(data, randomDeals, 3);
			showDeal();
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
    public void setSinglePlayer() {
	    this.twoPlayer = false;
	    setClientMode(false);
    }
}
