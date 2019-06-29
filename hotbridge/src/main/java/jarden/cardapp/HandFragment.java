package jarden.cardapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jardenconsulting.cardapp.BuildConfig;
import com.jardenconsulting.cardapp.HotBridgeActivity;
import com.jardenconsulting.cardapp.R;

import java.util.List;

import jarden.cards.Card;
import jarden.cards.CardPack;
import jarden.cards.Hand;
import jarden.cards.Player;
import jarden.cards.Suit;

public class HandFragment extends Fragment {
    private CardPack cardPack;
    private Player player;
	private TextView playerTitle;
	private TextView playerHCP;
	private TextView spadeValues;
	private TextView heartValues;
	private TextView diamondValues;
	private TextView clubValues;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.hand_layout, container, false);

		playerTitle = view.findViewById(R.id.playerTitle);
        playerHCP = view.findViewById(R.id.playerHCP);
		spadeValues = view.findViewById(R.id.spades);
		heartValues = view.findViewById(R.id.hearts);
		diamondValues = view.findViewById(R.id.diamonds);
		clubValues = view.findViewById(R.id.clubs);

		return view;
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) {
            Log.i(HotBridgeActivity.TAG,
                    "HandFragment.onCreate(savedInstanceState=" +
                            (savedInstanceState==null?"null":"not null") +
                            ")");
        }
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
	public void setData(Player player, CardPack cardPack) {
        this.playerTitle.setText(player.toString());
        this.player = player;
        this.cardPack = cardPack;
	}
	public void showHand() {
        Hand hand = cardPack.getHand(player);
		List<Card> cards = hand.getCards();
		StringBuilder sb = new StringBuilder();
		int index = 0;
		int handSize = cards.size();
		Card card;
		while (index < handSize) {
			card = cards.get(index);
			if (!card.getSuit().equals(Suit.clubs)) break;
			sb.append(card.getShortRank() + " ");
			++index;
		}
		clubValues.setText(sb.toString());
		sb.setLength(0);
		while (index < handSize) {
			card = cards.get(index);
			if (!card.getSuit().equals(Suit.diamonds)) break;
			sb.append(card.getShortRank() + " ");
			++index;
		}
		diamondValues.setText(sb.toString());
		sb.setLength(0);
		while (index < handSize) {
			card = cards.get(index);
			if (!card.getSuit().equals(Suit.hearts)) break;
			sb.append(card.getShortRank() + " ");
			++index;
		}
		heartValues.setText(sb.toString());
		sb.setLength(0);
		while (index < handSize) {
			card = cards.get(index);
			if (!card.getSuit().equals(Suit.spades)) break;
			sb.append(card.getShortRank() + " ");
			++index;
		}
		spadeValues.setText(sb.toString());
		showHCP();
	}
	public void showHCP() {
        Hand hand = cardPack.getHand(player);
        if (hand != null) {
            playerHCP.setText(" " + hand.getHighCardPoints() + "hcp");
        }
    }
}
