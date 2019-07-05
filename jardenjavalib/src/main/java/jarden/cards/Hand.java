package jarden.cards;

import java.util.ArrayList;
import java.util.List;

import jarden.cards.CardPack.CardEnum;

public class Hand {
	public static final int C = 0;
    public static final int D = 1;
    public static final int H = 2;
    public static final int S = 3;
    private static final char[] icons = {
            Card.ICON_CLUB, Card.ICON_DIAMOND, Card.ICON_HEART, Card.ICON_SPADE
    };

    private List<Card> cards;
	// all 4-element arrays for suits in order: c, d, h, s
	private int[] suitLengths = new int[4];
	private int[] suitValues = new int[4]; // A=4, K=3, Q=2, J=1
    private int[] honoursCt = new int[4]; // how many out of top 5 honours: AKQJ10
	private int highCardPoints;
	private int playingPoints;
	private int aceCt = 0;
	private int kingCt = 0;
    private int queenCt = 0;
	private int jackCt = 0;
    private int tenCt = 0;
    private boolean[] aces = new boolean[4];
    private boolean[] kings = new boolean[4];
    private boolean[] queens = new boolean[4];
	private boolean balanced;
    private Suit trumpSuit = null;

    public Hand(List<Card> cards) {
		this.cards = cards;
	}
	public Hand(CardEnum[] cardEnums) {
		cards = new ArrayList<>();
		for (CardEnum cardEnum : cardEnums) {
			cards.add(CardPack.SORTED_CARDS[cardEnum.ordinal()]);
		}
	}
	public List<Card> getCards() {
		return this.cards;
	}
	public String cardsAsString() {
	    StringBuilder builder = new StringBuilder();
        Suit currentSuit = null;
        for (Card card: cards) {
            Suit suit = card.getSuit();
            if (suit != currentSuit) {
                if (currentSuit != null) builder.append("  ");
                builder.append(icons[suit.ordinal()]);
                currentSuit = suit;
            }
            builder.append(" " + card.getShortRank());
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
        if (this.playingPoints == 0) return super.toString();
        return playingPoints + "pp " + highCardPoints + "HCP " +
                suitLengths[0] + "-" + suitLengths[1] + "-" +
                suitLengths[2] + "-" + suitLengths[3];
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
    public boolean hasKing(Suit suit) {
        return kings[suit.ordinal()];
    }
    public boolean hasQueen(Suit suit) {
        return queens[suit.ordinal()];
    }
    // TODO: add method has SuitHalfGuard, replacing 6 with 5
    public boolean hasSuitGuard(int suitOrdinal) {
        return (suitValues[suitOrdinal] + suitLengths[suitOrdinal]) >= 6;
    }
    public void reset() {
        if (trumpSuit != null) {
            trumpSuit = null;
            evaluateHand();
        }
    }
    private void evaluateHand() {
		for (Card card : cards) {
			int suitOrdinal = card.getSuit().ordinal();
			suitLengths[suitOrdinal]++;
			Rank rank = card.getRank();
			int hcp = rank.ordinal() - Rank.R10.ordinal();
			if (hcp > 0) highCardPoints += hcp;
			if (rank == Rank.Ace) {
			    suitValues[suitOrdinal] += 4;
			    honoursCt[suitOrdinal] += 1;
                aces[suitOrdinal] = true;
                ++aceCt;
            } else if (rank == Rank.King) {
			    suitValues[suitOrdinal] += 3;
                honoursCt[suitOrdinal] += 1;
                ++kingCt;
			    kings[suitOrdinal] = true;
            } else if (rank == Rank.Queen) {
			    suitValues[suitOrdinal] += 2;
                honoursCt[suitOrdinal] += 1;
			    ++queenCt;
			    queens[suitOrdinal] = true;
            } else if (rank == Rank.Jack) {
			    suitValues[suitOrdinal] += 1;
                honoursCt[suitOrdinal] += 1;
                ++jackCt;
            } else if (rank == Rank.R10) {
                honoursCt[suitOrdinal] += 1;
			    ++tenCt;
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
		refineHCP();
		playingPoints = highCardPoints + high1 + high2;
		this.balanced = getBalanced();
	}
	private void refineHCP() {
        // over- or under-valued cards:
        highCardPoints += ((aceCt + tenCt - queenCt - jackCt) / 3);
        for (int s = 0; s < 4; s++) {
            int suitLength = suitLengths[s];
            int suitValue = suitValues[s];
            // unsupported honours: subtract 1 for each singleton K, Q, J
            if ((suitLength == 1) &&
                    suitValue > 0 && suitValue < 4) --highCardPoints;
            // subtract 1 for each doubleton: AJ, KQ, KJ, QJ, Qx, Jx
            if ((suitLength == 2) && suitValue > 0 && suitValue < 6 &&
                    !(aces[s] && suitValue == 4) && // i.e. ignore Ax
                    !(kings[s] && suitValue == 3)) { // i.e. ignore Kx
                --highCardPoints;
            }
            // quality suits: if suit has 3+ of top 5 honours and 4+ cards, add 1
            if (honoursCt[s] >= 3 && suitLength > 3) ++highCardPoints;
        }
    }
    /**
     * Re-evaluate hand now we've agreed trump suit
     * @param trumpSuit
     */
    public void setTrumpSuit(Suit trumpSuit) {
        this.trumpSuit = trumpSuit;
        this.highCardPoints += getAdjustmentForTrumps(trumpSuit);
    }
    /**
     * After finding fit in trumps, make adjustments to HCP
     * This is John's version!
     * add 2 for each trump after 5
     * for each void: add 3 + extra 1 if trumpct > 5
     * for each singleton: add 2 + extra 1 if trumpct > 5
     * for each doubleton: add 1 + extra 1 if trumpct > 5
     */
    public int getAdjustmentForTrumps(Suit trumpSuit) {
        int adjustmentForTrumps = 0;
        if (trumpSuit != null) {
            int trumpCt = suitLengths[trumpSuit.ordinal()];
            if (trumpCt > 0) {
                int longTrumpsAdjust = 0;
                int trumpsOver5 = trumpCt - 5;
                if (trumpsOver5 > 0) {
                    longTrumpsAdjust = 1;
                    // extra trumps: add 2 for each trump after 5
                    adjustmentForTrumps += (trumpsOver5 * 2);
                }
                for (int s = 0; s < 4; s++) {
                    int suitLength;
                    // process side-suits
                    if (s != trumpSuit.ordinal()) {
                        suitLength = suitLengths[s];
                        // add for side-suit shortages
                        if (suitLength == 0) adjustmentForTrumps += (3 + longTrumpsAdjust);
                        else if (suitLength == 1) adjustmentForTrumps += (2 + longTrumpsAdjust);
                        else if (suitLength == 2) adjustmentForTrumps += (1 + longTrumpsAdjust);
                    }
                }
            }
        }
        return adjustmentForTrumps;
    }
    /**
     * Add for shortages in non-trump suits:
     *    for doubleton: add 1
     *    for singleton: add 2 or with 4+ trumps add 3
     *    for void, add the number of trumps in hand
     */
    public int getAdjustmentForTrumpsBergen(Suit trumpSuit, boolean declarer) {
        int adjustment = 0;
        if (trumpSuit != null) {
            int trumpCt = suitLengths[trumpSuit.ordinal()];
            if (trumpCt > 0) {
                if (declarer) {
                    // extra trumps: add 1 for each trump 6+
                    int doubletonCt = 0;
                    int trumpsExtra = trumpCt - 5;
                    if (trumpsExtra > 0) adjustment += trumpsExtra;
                    for (int s = 0; s < 4; s++) {
                        int suitLength;
                        // process side-suits
                        if (s != trumpSuit.ordinal()) {
                            suitLength = suitLengths[s];
                            // add 4 for each side-suit void
                            if (suitLength == 0) adjustment += 4;
                                // add 2 for each side-suit singleton
                            else if (suitLength == 1) adjustment += 2;
                            else if (suitLength == 2) ++doubletonCt;
                            /*??
                                // add 1 for each side-suit length 4+
                            else if (suitLength > 3) adjustment += 1;
                            */
                        }
                    }
                    if (doubletonCt > 1) adjustment += 1;
                } else { // dummy
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
        }
        return adjustment;
    }
    public String getEstimateBid(Hand partnerHand) {
        Suit suit = null;
        int level = 0;
        int teamHCP = getHighCardPoints() + partnerHand.getHighCardPoints();
        int[] suitLengthsBoth = new int[4];
        /*
        Suit choice:
            4+ hearts in both
            4+ spades in both
            3 and 5+ hearts
            3 and 5+ spades
            suit-setter in hearts
            suit-setter in spades
            same for minors; if all suits guarded: no trumps
            no trumps
         */
        int longestSuitCt = 0;
        for (int i = 0; i < 4; i++) {
            int s = (i + 2) % 4; // i.e. H, S, C, D
            suitLengthsBoth[s] = suitLengths[s] + partnerHand.suitLengths[s];
            if (suitLengthsBoth[s] > longestSuitCt) {
                longestSuitCt = suitLengthsBoth[s];
                suit = Suit.values()[s];
            }
        }
        // 4+ spades in both?
        if (suitLengths[2] >= 4 && partnerHand.suitLengths[2] >= 4) {
            suit = Suit.hearts;
        } else if (suitLengths[3] >= 4 && partnerHand.suitLengths[3] >= 4) {
            suit = Suit.spades;
        } else if (partnerHand.suitLengths[2] ==3 && suitLengths[2] >= 5 ||
                suitLengths[2] ==3 && partnerHand.suitLengths[2] >= 5) {
            suit = Suit.hearts;
        } else if (partnerHand.suitLengths[3] ==3 && suitLengths[3] >= 5 ||
                suitLengths[3] ==3 && partnerHand.suitLengths[3] >= 5) {
            suit = Suit.spades;
        } else if (longestSuitCt < 8) {
            suit = null; // no trumps
        }
        int keyCardCt;
        if (suit != null) {
            teamHCP += getAdjustmentForTrumps(suit);
            teamHCP += partnerHand.getAdjustmentForTrumps(suit);
            keyCardCt = getKeyCardCt(suit) + partnerHand.getKeyCardCt(suit);
        } else {
            keyCardCt = aceCt + partnerHand.aceCt;
            if (kingCt + partnerHand.kingCt >= 3) ++keyCardCt;
        }
        /*
        point-count table:
        0
        1 21
        2
        3
        4 25
        5 29
        6 33 missing 1 keycard or void
        7 37 & void or ace in each suit, K trumps
         */
        boolean minorSuit = (suit == Suit.clubs || suit == Suit.diamonds);
        if (teamHCP >= 37) {
            level = keyCardCt == 5 ? 7 : 6;
        } else if (teamHCP >= 33) {
            level = keyCardCt == 4 ? 6 : (minorSuit ? 5 : 4);
        } else if (teamHCP >= 29 && minorSuit) {
            level = 5;
        } else if (teamHCP >= 25) {
            level = (suit == null) ? 3 : 4;
        } else if (teamHCP >= 21) {
            level = minorSuit ? 2 : 1;
        };
        String suitStr = (suit == null ? "NT" : suit.name().substring(0, 1));
        return (level == 0) ? "Pass" : (level + suitStr);

    }
}
