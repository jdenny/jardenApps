package jarden.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    /**
     * Shuffle the cards, deal and sort. If biased: give West and East the better hands.
     * @param biased if West/East both have less than 18 playing points,
     *               swap them with North/South.
     */
	public void shuffleAndDeal(boolean biased) {
	    shuffle2(cards);
	    // now deal:
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
	private static void shuffle2(Card[] cards2) {
        Random random = new Random();
        int length = cards2.length;
        Card swap;
        for (int i = 0; i < length; i++) {
            int j = random.nextInt(length);
            swap = cards2[j];
            cards2[j] = cards2[i];
            cards2[i] = swap;
        }
    }
    public void setBookHand(BookHand bookHand) {
        hands = new Hand[PLAYER_CT];
	    hands[0] = bookHand.handWest;
	    hands[2] = bookHand.handEast;
    }
    public Hand getHand(Player player) {
		return hands[player.ordinal()];
	}
	public void setDealFromBytes(byte[] data, boolean randomDeal, int index) {
        hands = new Hand[PLAYER_CT];
        hands[0] = new Hand(getCardsFromData(data, index));
        if (randomDeal) {
            index += 13;
            hands[1] = new Hand(getCardsFromData(data, index));
        }
        index += 13;
        hands[2] = new Hand(getCardsFromData(data, index));
        if (randomDeal) {
            index += 13;
            hands[3] = new Hand(getCardsFromData(data, index));
        }
	}
	private static List<Card> getCardsFromData(byte[] data, int index) {
        ArrayList<Card> cardList = new ArrayList<>();
        int rankNum;
        int suitNum;
        byte cardData;
        Rank[] rankValues = Rank.values();
        Suit[] suitValues = Suit.values();
        for (int c = 0; c < 13; c++) {
            cardData = data[c + index];
            rankNum = cardData % 16;
            suitNum = cardData / 16;
            cardList.add(new Card(suitValues[suitNum], rankValues[rankNum]));
        }
        return cardList;
    }
	/**
	 * Use one byte to encode each card: suitNum and rankNum.
	 * @return byte[] to be sent across network
	 */
	public byte[] getDealAsBytes(boolean randomDeals, byte[] prefix) {
        int prefixLength = prefix.length;
        int dataLength = randomDeals ? 52 : 26;
        dataLength += prefixLength;
        byte[] data = new byte[dataLength];
        int index = 0;
        for (int i = 0; i < prefixLength; i++) {
            data[index++] = prefix[i];
        }
        addHandToData(hands[0], data, index);
        if (randomDeals) {
            index += 13;
            addHandToData(hands[1], data, index);
        }
        index += 13;
        addHandToData(hands[2], data, index);
        if (randomDeals) {
            index += 13;
            addHandToData(hands[3], data, index);
        }
		return data;
	}
	private static void addHandToData(Hand hand, byte[] data, int index) {
        Card card;
        int rankNum;
        int suitNum;
        List<Card> cards = hand.getCards();
        for (int c = 0; c < 13; c++) {
            card = cards.get(c);
            rankNum = card.getRank().ordinal();
            suitNum = card.getSuit().ordinal();
            data[index + c] = (byte) (suitNum * 16 + rankNum);
        }
    }
}
