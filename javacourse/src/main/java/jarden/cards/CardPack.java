package jarden.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class CardPack {
	public final static int DECK_SIZE = 52;
	public final static int PLAYERS = 4;
	public final static int HAND_SIZE = DECK_SIZE / PLAYERS;
	

	private Card[] cards;
	private Hand[] hands;
	
	public CardPack() {
		cards = new Card[DECK_SIZE];
		int i = 0;
		for (Suit suit: Suit.values()) {
			for (Rank rank: Rank.values()) {
				cards[i++] = new Card(suit, rank);
			}
		}
	}
	public Card[] getCards() {
		return cards;
	}
	public void shuffle() {
		Random random = new Random();
		Card swap;
		for (int i = 0; i < DECK_SIZE; i++) {
			int j = random.nextInt(DECK_SIZE);
			swap = cards[j];
			cards[j] = cards[i];
			cards[i] = swap;
		}
	}
	/**
	 * Actually, deal and sort. We're good like that.
	 */
	public void deal() {
		hands = new Hand[PLAYERS];
		ArrayList<Card> cardList;
		for (int p = 0; p < PLAYERS; p++) {
			cardList = new ArrayList<Card>();
			for (int i = 0; i < HAND_SIZE; i++) {
				cardList.add(cards[i * PLAYERS + p]);
			}
			Collections.sort(cardList);
			hands[p] = new Hand();
			hands[p].cards = cardList;
		}
	}
	public Hand getHand(Player player) {
		return hands[player.ordinal()];
	}
}
