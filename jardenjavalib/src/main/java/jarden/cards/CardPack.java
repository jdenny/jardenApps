package jarden.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

// TODO: hold cards as byte[], to indicate which player has this card!
public class CardPack {
    public enum CardEnum {
		C2, C3, C4, C5, C6, C7, C8, C9, CT, CJ, CQ, CK, CA,
		D2, D3, D4, D5, D6, D7, D8, D9, DT, DJ, DQ, DK, DA,
		H2, H3, H4, H5, H6, H7, H8, H9, HT, HJ, HQ, HK, HA,
		S2, S3, S4, S5, S6, S7, S8, S9, ST, SJ, SQ, SK, SA
	}
	/*!!
	public enum BidEnum {
		BPass(null, "pass"), NONE(null, "-"),
		B1C(Club, "1C"), B1D(Diamond, "1D"), B1H(Heart, "1H"), B1S(Spade, "1S"), B1N(null, "1NT"),
		B2C(Club, "2C"), B2D(Diamond, "2D"), B2H(Heart, "2H"), B2S(Spade, "2S"), B2N(null, "2NT"),
		B3C(Club, "3C"), B3D(Diamond, "3D"), B3H(Heart, "3H"), B3S(Spade, "3S"), B3N(null, "3NT"),
		B4C(Club, "4C"), B4D(Diamond, "4D"), B4H(Heart, "4H"), B4S(Spade, "4S"), B4N(null, "4NT"),
		B5C(Club, "5C"), B5D(Diamond, "5D"), B5H(Heart, "5H"), B5S(Spade, "5S"), B5N(null, "5NT"),
		B6C(Club, "6C"), B6D(Diamond, "6D"), B6H(Heart, "6H"), B6S(Spade, "6S"), B6N(null, "6NT"),
		B7C(Club, "7C"), B7D(Diamond, "7D"), B7H(Heart, "7H"), B7S(Spade, "7S"), B7N(null, "7NT");
		
		public Suit suit;
		public String text;

		BidEnum(Suit suit, String text) {
			this.suit = suit;
			this.text = text;
		}
		public String toString() {
			return text;
		}
	}
	public enum AuctionType {
		NEUTRAL, DISTURBED, FIT, SCRAMBLE
	}
	*/
	public static final Card[] SORTED_CARDS;
	static {
		SORTED_CARDS = new Card[52];
		int i = 0;
		for (Suit suit: Suit.values()) {
			for (Rank rank: Rank.values()) {
				SORTED_CARDS[i++] = new Card(suit, rank);
			}
		}
	}
	private final static int DECK_SIZE = 52;
	private final static int PLAYER_CT = 4;
	private final static int HAND_SIZE = DECK_SIZE / PLAYER_CT;
	private final static int AVE_PLAYING_POINTS = 18;

	private Card[] cards;
	private Hand[] hands; // in Player order, i.e. West, North, East, South
	
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
	 * Actually, dealAndSort and sort. We're good like that.
	 * If biased is true, make sure East/West have got the best hand!
	 * In reality, if East and West both have less than 18 playing points,
	 * swap them both with North/South.
	 */
	public void dealAndSort(boolean biased) {
		hands = new Hand[PLAYER_CT];
		ArrayList<Card> cardList;
		Card card;
		for (int p = 0; p < PLAYER_CT; p++) {
			cardList = new ArrayList<>();
			for (int i = 0; i < HAND_SIZE; i++) {
				card = cards[i * PLAYER_CT + p];
				cardList.add(card);
			}
			Collections.sort(cardList);
			hands[p] = new Hand(cardList);
		}
		if (biased) {
			int[] playingPoints = new int[PLAYER_CT];
			for (int p = 0; p < 4; p++) {
				playingPoints[p] = hands[p].getPlayingPoints();
			}
			if (playingPoints[0] < AVE_PLAYING_POINTS &&
				playingPoints[2] < AVE_PLAYING_POINTS) {
				Hand temp = hands[0];
				hands[0] = hands[1];
				hands[1] = temp;
				temp = hands[2];
				hands[2] = hands[3];
				hands[3] = temp;
			}
		}
	}
    public void setEastWest(BookHand bookHand) {
        hands = new Hand[PLAYER_CT];
	    hands[0] = bookHand.handWest;
	    hands[2] = bookHand.handEast;
    }
    public Hand getHand(Player player) {
		return hands[player.ordinal()];
	}
	public void setPackFromBytes(byte[] data) {
		int rankNum;
		int suitNum;
		byte cardData;
		Rank[] rankValues = Rank.values();
		Suit[] suitValues = Suit.values();
		for (int i = 0; i < DECK_SIZE; i++) {
			cardData = data[i];
			rankNum = cardData % 16;
			suitNum = cardData / 16;
			cards[i] = new Card(suitValues[suitNum], rankValues[rankNum]);
		}
	}
	/**
	 * Use one byte to encode each card: suitNum and rankNum.
	 * @return byte[] to be sent across network
	 */
	public byte[] getDealAsBytes() {
		byte[] data = new byte[DECK_SIZE];
		Card card;
		int rankNum;
		int suitNum;
		for (int i = 0; i < DECK_SIZE; i++) {
			card = cards[i];
			rankNum = card.getRank().ordinal();
			suitNum = card.getSuit().ordinal();
			data[i] = (byte) (suitNum * 16 + rankNum);
		}
		return data;
	}
}
