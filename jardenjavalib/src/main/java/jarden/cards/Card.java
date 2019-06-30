package jarden.cards;

public class Card implements Comparable<Card> {
	public final static char ICON_SPADE = '\u2660';
	public final static char ICON_HEART_HOLLOW = '\u2661';
	public final static char ICON_DIAMOND_HOLLOW = '\u2662';
	public final static char ICON_CLUB = '\u2663';
	public final static char ICON_HEART = '\u2665';
	public final static char ICON_DIAMOND = '\u2666';

	private Suit suit;
	private Rank rank;
	private String shortString;
	private String shortRank;

	public Card(Suit suit, Rank rank) {
		this.suit = suit;
		this.rank = rank;
		String rankStr = rank.toString();
		if (rankStr.charAt(0) == 'R') {
			shortRank = rankStr.substring(1);
		} else {
			shortRank = rankStr.substring(0, 1);
		}
//		shortString = // suit.toString().substring(0, 1) + shortRank;
        shortString = Character.toUpperCase(suit.toString().charAt(0)) + shortRank;
	}
	public Suit getSuit() {
		return this.suit;
	}
	public Rank getRank() {
		return this.rank;
	}
	public String getShortRank() {
		return this.shortRank;
	}
	public String toString() {
		return this.shortString;
	}
	public String toLongString() {
		return rank.toString() + suit.toString();
	}
	@Override
	public int compareTo(Card card) {
		int compResult = this.suit.ordinal() - card.getSuit().ordinal();
		if (compResult == 0) {
			compResult = card.getRank().ordinal() - this.rank.ordinal();
		}
		return compResult;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Card) {
			Card card = (Card)obj;
			return (this.rank.equals(card.rank) && this.suit.equals(card.suit));
		} else {
			return false;
		}
	}
}
