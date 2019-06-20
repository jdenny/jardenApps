package jarden.cards;

import java.util.ArrayList;

import jarden.cards.CardPack.CardEnum;

public class Hand {
	public final static int C = 0;
    public final static int D = 1;
    public final static int H = 2;
    public final static int S = 3;
	private ArrayList<Card> cards;
	private int[] suitLengths = new int[4]; // c, d, h, s
	private int[] suitValues = new int[4]; // c, d, h, s; A=4, K=3, Q=2, J=1
	private int highCardPoints;
	private int playingPoints;
	private int aceCt = 0;
	private boolean[] kings = new boolean[4];
    private boolean[] queens = new boolean[4];
	private boolean balanced;
    private Suit trumpSuit = null;

    public Hand(ArrayList<Card> cards) {
		this.cards = cards;
	}
	public Hand(CardEnum[] cardEnums) {
		cards = new ArrayList<>();
		for (CardEnum cardEnum : cardEnums) {
			cards.add(CardPack.SORTED_CARDS[cardEnum.ordinal()]);
		}
	}
	public ArrayList<Card> getCards() {
		return this.cards;
	}
	public String cardsAsString() {
	    StringBuilder builder = new StringBuilder();
	    for (Card card: cards) {
	        builder.append(card);
	        builder.append(", ");
        }
	    return builder.toString();
    }

	public int getHighCardPoints() {
		if (this.playingPoints == 0) evaluateHand();
		return this.highCardPoints;
	}

	public int getPlayingPoints() {
		if (this.playingPoints == 0) evaluateHand();
		return this.playingPoints;
	}

	public boolean isBalanced() {
		if (this.playingPoints == 0) evaluateHand();
		return this.balanced;
	}
	public boolean isSkew() {
        for (int i = 0; i < 4; i++) {
            if (suitLengths[i] < 2) {
                return true;
            }
        }
        return false;
    }
    public int[] getSuitLengths() {
	    return suitLengths;
    }
    public int[] getSuitValues() {
	    return suitValues;
    }

	public String toString() {
        if (this.playingPoints == 0) evaluateHand();
        return playingPoints + "pp " + highCardPoints + "HCP " +
                suitLengths[0] + "-" + suitLengths[1] + "-" +
                suitLengths[2] + "-" + suitLengths[3];
    }
    private void evaluateHand() {
		for (Card card : cards) {
			int suitOrdinal = card.getSuit().ordinal();
			suitLengths[suitOrdinal]++;
			Rank rank = card.getRank();
			int hcp = rank.ordinal() - Rank.R10.ordinal();
			if (hcp > 0)
				highCardPoints += hcp;
			if (rank == Rank.Ace) {
			    suitValues[suitOrdinal] += 4;
			    ++aceCt;
            }
			else if (rank == Rank.King) {
			    suitValues[suitOrdinal] += 3;
			    kings[suitOrdinal] = true;
            }
			if (rank == Rank.Queen) {
			    suitValues[suitOrdinal] += 2;
			    queens[suitOrdinal] = true;
            }
			if (rank == Rank.Jack) {
			    suitValues[suitOrdinal] += 1;
            }
		}
		// find 2 longest suits:
		int high1 = suitLengths[C];
		int high2 = suitLengths[D];
		if (high2 > high1) {
			int temp = high2;
			high2 = high1;
			high1 = temp;
		}
		if (suitLengths[H] > high1) {
			high2 = high1;
			high1 = suitLengths[H];
		} else if (suitLengths[H] > high2) {
			high2 = suitLengths[H];
		}
		if (suitLengths[S] > high1) {
			high2 = high1;
			high1 = suitLengths[S];
		} else if (suitLengths[S] > high2) {
			high2 = suitLengths[S];
		}
		playingPoints = highCardPoints + high1 + high2;
		this.balanced = getBalanced();
	}
	private boolean getBalanced() {
		boolean doubleton = false;
		for (int suitLen : suitLengths) {
			if (suitLen < 2)
				return false;
			if (suitLen == 2) {
				if (doubleton)
					return false; // i.e. 2 doubletons
				else
					doubleton = true;
			}
		}
		return true;
	}
    public int getKeyCardCt(Suit suit) {
	    int suitOrdinal = suit.ordinal();
	    return aceCt + (kings[suitOrdinal] ? 1 : 0);
    }
    public boolean hasKing(int suitOrdinal) {
	    return kings[suitOrdinal];
    }
    public boolean hasQueen(int suitOrdinal) {
        return queens[suitOrdinal];
    }

    /**
     * Re-evaluate hand now we've agreed trump suit
     * @param trumpSuit
     * @param declarer, else dummy
     */
    public void setTrumpSuit(Suit trumpSuit, boolean declarer) {
        this.trumpSuit = trumpSuit;
        this.highCardPoints += getAdjustmentForTrumps(trumpSuit, declarer);
    }
    /**
     * Add for shortages in non-trump suits:
     *    for doubleton: add 1
     *    for singleton: add 2 or with 4+ trumps add 3
     *    for void, add the number of trumps in hand
     */
    public int getAdjustmentForTrumps(Suit trumpSuit, boolean declarer) {
        int adjustment = 0;
        if (!declarer) {
            if (trumpSuit != null) {
                int trumpCt = suitLengths[trumpSuit.ordinal()];
                for (int s = 0; s < 4; s++) {
                    int suitLength;
                    if (s != trumpSuit.ordinal()) {
                        suitLength = suitLengths[s];
                        if (suitLength == 0) {
                            adjustment += trumpCt;
                        } else if (suitLength == 1) {
                            adjustment += ((trumpCt >= 4) ? 3 : 2);
                        } else if (suitLength == 2) {
                            ++adjustment;
                        }
                    }
                }
            }
        }
        return adjustment;
    }
}
