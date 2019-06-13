package jarden.cards;

import java.util.ArrayList;
import java.util.List;

import jarden.cards.CardPack.AuctionType;
import jarden.cards.CardPack.BidEnum;
import jarden.cards.CardPack.CardEnum;
import jarden.quiz.BridgeQuiz;
import jarden.quiz.QuestionAnswer;

import static jarden.cards.CardPack.BidEnum.B1C;
import static jarden.cards.CardPack.BidEnum.B1D;
import static jarden.cards.CardPack.BidEnum.B1H;
import static jarden.cards.CardPack.BidEnum.B1N;
import static jarden.cards.CardPack.BidEnum.B1S;
import static jarden.cards.CardPack.BidEnum.B2C;
import static jarden.cards.CardPack.BidEnum.B2D;
import static jarden.cards.CardPack.BidEnum.B2H;
import static jarden.cards.CardPack.BidEnum.B2N;
import static jarden.cards.CardPack.BidEnum.B2S;
import static jarden.cards.CardPack.BidEnum.B3C;
import static jarden.cards.CardPack.BidEnum.B3D;
import static jarden.cards.CardPack.BidEnum.B3H;
import static jarden.cards.CardPack.BidEnum.B3N;
import static jarden.cards.CardPack.BidEnum.B3S;
import static jarden.cards.CardPack.BidEnum.B4C;
import static jarden.cards.CardPack.BidEnum.B4D;
import static jarden.cards.CardPack.BidEnum.B4H;
import static jarden.cards.CardPack.BidEnum.B4S;
import static jarden.cards.CardPack.BidEnum.B5C;
import static jarden.cards.CardPack.BidEnum.B5D;
import static jarden.cards.CardPack.BidEnum.NONE;
import static jarden.cards.CardPack.BidEnum.BPass;
import static jarden.quiz.BridgeQuiz.OPENING_BIDS;

public class Hand {
	private final static int C = 0, D = 1, H = 2, S = 3;
	private ArrayList<Card> cards;
	protected int[] suitLengths = new int[4]; // c, d, h, s
	// suitValues: add 1 for J, 2 for Q, 3 for K, 4 for A
	protected int[] suitValues = new int[4]; // c, d, h, s
	private int highCardPoints;
	private int playingPoints;
	private boolean balanced;
	private String bidVerbose;
	private boolean limited;
	private BidEnum previousBid = null;
	private Hand partner;
	private AuctionType auctionType = AuctionType.NEUTRAL; // all auctions start neutral

	// ? private boolean partnerLimited = false;
	// ? private int partnerNominalHCP = 0; // starts off unlimited

	public Hand(ArrayList<Card> cards) {
		this.cards = cards;
	}
	public Hand(CardEnum[] cardEnums) {
		cards = new ArrayList<>();
		for (CardEnum cardEnum : cardEnums) {
			cards.add(CardPack.SORTED_CARDS[cardEnum.ordinal()]);
		}
	}
	public void setPartner(Hand partner) {
		this.partner = partner;
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

	public String toString() {
        if (this.playingPoints == 0) evaluateHand();
        return playingPoints + "pp " + highCardPoints + "HCP " +
                suitLengths[0] + "-" + suitLengths[1] + "-" +
                suitLengths[2] + "-" + suitLengths[3];
    }

	public String getBidVerbose() {
		return this.bidVerbose;
	}

	public BidEnum getSecondaryBid(BidEnum partnerBid) {
		// "Failure to bid a major at the one-level denies a four-card holding
		// in the suit, regardless of what is held elsewhere."
		if (this.playingPoints == 0) evaluateHand();
		boolean forcing = (this.partner.previousBid == B1C &&
				(partnerBid == B1H || partnerBid == B1S));
		/*
		 * ? if (partnerBid == B1C) { partnerNominalHCP = 16; } else {
		 * this.partnerLimited = true; if (partnerBid == B1D) partnerNominalHCP
		 * = 15; else partnerNominalHCP = 13; }
		 */
		BidEnum bid;
		if (partnerBid == BPass) {
			if (this.previousBid == null) { // i.e. my first bid
				return getPrimaryBid();
			} else {
				bid = BPass;
				bidVerbose = "passing, as partner passed";
			}
		} else if (partnerBid == B1C) {
			bid = getResponseTo1C();
		} else if (partnerBid == B1D) {
			bid = getResponseTo1D();
		} else if (partnerBid == B1H) {
			bid = getResponseTo1H(forcing, partnerBid);
		} else if (partnerBid == B1S) {
			bid = getResponseTo1S(forcing, partnerBid);
		} else if (partnerBid == B1N) {
			bid = getResponseTo1NT();
		} else if (partnerBid == B2C) {
			bid = getResponseTo2C(partnerBid);
		} else if (partnerBid == B2D) {
			bid = getResponseTo2D(partnerBid);
		//} else if (partnerBid == B2H) {
		//	bid = getResponseTo2H(forcing, partnerBid);
		} else if (partnerBid == B2S) {
			bid = getResponseTo2S(forcing, partnerBid);
		} else { // TODO: 2H and 2NT up
			bid = null;
			bidVerbose = "don't know what to bid next!";
		}
		this.previousBid = bid;
		if (this.partner != null) { // should only be null in testing!
			this.partner.auctionType = this.auctionType;
		}
		return bid;
	}
	private BidEnum getResponseTo1C() {
		BidEnum bid;
		if ((bid = checkForMajor1(4)) != null) {
			// do nothing - bid found
		} else if ((bid = checkForRelay(7)) != null) {
			// do nothing - bid found
		} else if (this.highCardPoints >= 4) {
			if (suitLengths[C] >= 5 && suitLengths[D] >= 5) {
				bid = B2N;
				bidVerbose = "4-6 HCP, no 4+ major, 5+ in both minors: 2NT";
			} else if (this.suitLengths[C] >= 6) {
				bid = B3C;
				bidVerbose = "4-6 HCP, no 4+ major, 6+ clubs: 3C";
			} else if (this.suitLengths[D] >= 6) {
				bid = B3D;
				bidVerbose = "4-6 HCP, no 4+ major, 6+ diamonds: 3D";
			} else {
				bid = B1N;
				bidVerbose = "4-6 HCP, no 4+ major or 6+ minor: 1NT";
			}
		} else {
			if (suitLengths[H] >= 6) {
				bid = B2H;
				bidVerbose = "0-3 HCP, 6+ hearts: 2H";
			} else if (suitLengths[S] >= 6) {
				bid = B2S;
				// TODO: should we check for 4 or 5 hearts?
				bidVerbose = "0-3 HCP, 6+ spades: 2S";
			} else {
				bid = B1D;
				bidVerbose = "0-3 HCP, no long suit: 1D";
			}
		}
		// TODO: 3H/3S
		return bid;
	}
	private BidEnum getResponseTo1D() {
		BidEnum bid;
		if (this.previousBid == B1C) {
			bid = getPrimaryBid();
		} else if ((bid = checkForMajor1(0)) != null) {
			// do nothing - bid found
		} else if ((bid = checkForRelay(8)) != null) {
			// do nothing - bid found
		} else if (this.highCardPoints >= 4) {
			if (suitLengths[C] >= 5 && suitLengths[D] >= 5) {
				bid = B2N;
				bidVerbose = "4-6 HCP, no 4+ major, 5+ in both minors: 2NT";
			} else if (this.suitLengths[C] >= 6) {
				bid = B3C;
				bidVerbose = "4-6 HCP, no 4+ major, 6+ clubs: 3C";
			} else if (this.suitLengths[D] >= 6) {
				bid = B3D;
				bidVerbose = "4-6 HCP, no 4+ major, 6+ diamonds: 3D";
			} else {
				bid = B1N;
				bidVerbose = "4-6 HCP, no 4+ major or 6+ minor: 1NT";
			}
		} else {
			// TODO: 2H and 2S will never happen, as checkForMajor1 above
			// will return true
			if (suitLengths[H] >= 6) {
				bid = B2H;
				bidVerbose = "0-3 HCP, 6+ hearts: 2H";
			} else if (suitLengths[S] >= 6) {
				bid = B2S;
				// TODO: should we check for 4 or 5 hearts?
				bidVerbose = "0-3 HCP, 6+ spades: 2S";
			} else {
				bid = B1N;
				bidVerbose = "0-3 HCP, no long suit: 1NT";
			}
		}
		// TODO: 3H/3S
		return bid;
	}
	private BidEnum getResponseTo1H(boolean forcing, BidEnum partnerBid) {
		BidEnum bid;
		String pointRangeHint;
		if (previousBid != null) {
			pointRangeHint = "";
		} else {
			pointRangeHint = "8-10 HCP, ";
		}
		if (this.highCardPoints <= 7 && !forcing) {
			if (this.highCardPoints >= 5 && this.suitLengths[S] >= 6) {
				bid = B2S;
				bidVerbose = "5-7 HCP: 6+ spades: 2S";
			} else {
				bid = BPass;
				bidVerbose = "0-7 HCP: BPass";
			}
		} else if (suitLengths[H] >= 4) { // i.e. heart fit
			this.auctionType = AuctionType.FIT;
			if (this.previousBid != null) {
				bid = B2H;
				bidVerbose = "4+ hearts: 2H";
			} else {
				if ((bid = checkForSplinter(partnerBid)) != null) {
					// nothing to do
				} else if (this.highCardPoints >= 11) {
					bid = B2N;
					bidVerbose = "11+ HCP, 4+ hearts: 2NT";
				} else if (suitLengths[H] >= 6) {
					bid = B4H;
					bidVerbose = "8-10 HCP, 6+ hearts: 4H";
				} else if (suitLengths[H] == 5) {
					bid = B3H;
					bidVerbose = "8-10 HCP, 5 hearts: 3H";
				} else {
					bid = B2H;
					bidVerbose = "8-10 HCP, 4 hearts: 2H";
				}
			}
		} else if (suitLengths[S] >= 4) {
			bid = B1S;
			bidVerbose = pointRangeHint + "<4 hearts, 4+ spades: 1S";
		} else if ((bid = checkFor3NT()) != null) {
			// nothing to do
		} else if ((bid = checkForRelay()) != null) {
			// nothing to do
		// TODO: next 4 bids can't be right!
		} else if (this.previousBid != null && this.suitLengths[C] >= 5
				&& this.suitLengths[D] >= 5) {
			bid = B2N;
			bidVerbose = "< 4 hearts, 5+ in both minors: 2NT (raw)";
		} else if (this.previousBid != null && this.suitLengths[C] >= 4
				&& this.suitLengths[D] >= 4) {
			bid = B2N;
			bidVerbose = "< 4 hearts, 4+ in both minors: 2NT (raw)";
		} else if (this.previousBid == null && this.suitLengths[C] >= 5
				&& this.suitLengths[D] == 4) {
			bid = B2C;
			bidVerbose = "< 4 hearts, 5+ clubs, 4 diamonds: 2C (raw)";
		} else if (this.previousBid == null && this.suitLengths[D] >= 5
				&& this.suitLengths[C] == 4) {
			bid = B2D;
			bidVerbose = "< 4 hearts, 5+ diamonds, 4 clubs: 2D (raw)";
		// TODO: end of strange bit!
		} else if (this.suitLengths[C] >= 6) {
			bid = B3C;
			bidVerbose = pointRangeHint + "no 4+ major, 6+ clubs: 3C (raw)";
		} else if (this.suitLengths[D] >= 6) {
			bid = B3D;
			bidVerbose = pointRangeHint
					+ "no 4+ major, 6+ diamonds: 3D (raw)";
		} else {
			bid = B1N;
			bidVerbose = pointRangeHint + "no 4+ major or 6+ minor: 1NT";
		}
		return bid;
	}
	private BidEnum getResponseTo1S(boolean forcing, BidEnum partnerBid) {
		BidEnum bid;
		String pointRangeHint;
		if (this.previousBid != null) {
			pointRangeHint = "";
		} else {
			pointRangeHint = "8-10 HCP, ";
		}
		if (this.highCardPoints <= 7 && !forcing) { // will be false if
													// already bid
			bid = BPass;
			bidVerbose = "0-7 HCP: BPass";
		} else if (suitLengths[S] >= 4) { // i.e. spade fit
			this.auctionType = AuctionType.FIT;
			if (this.previousBid == B1C) {
				if (this.playingPoints >= 19) {
					bid = B2N;
					bidVerbose = "4+ spades, 19+ HCP: 2NT (strong fit)";
				} else {
					bid = B2S;
					bidVerbose = "4+ spades, < 19 HCP: 2S";
				}
			} else if (this.previousBid == B1D) {
				bid = B2S;
				bidVerbose = "4 spades: 2S";
			} else if ((bid = checkForSplinter(partnerBid)) != null) {
					// nothing to do!
			} else if (!this.limited && this.highCardPoints >= 11) {
				bid = B2N;
				bidVerbose = "11+ HCP, 4+ spades: 2NT";
			} else if (suitLengths[S] >= 6) {
				bid = B4S;
				bidVerbose = pointRangeHint + "6+ spades: 4S";
			} else if (suitLengths[S] == 5) {
				bid = B3S;
				bidVerbose = pointRangeHint + "5 spades: 3S";
			} else {
				bid = B2S;
				bidVerbose = pointRangeHint + "4 spades: 2S";
			}
		} else if ((bid = checkFor3NT()) != null) {
			// do nothing - bid found
		} else if ((bid = checkFor2Choice()) != null) {
			// do nothing - bid found
		} else if ((bid = checkForRelay()) != null) {
			// do nothing - bid found
		} else if (this.previousBid == B1D && this.suitLengths[C] >= 5
				&& this.suitLengths[D] >= 4) {
			bid = B2C;
			bidVerbose = "<4 spades, 5+ clubs, 4 diamonds: 2C";
		} else if (this.previousBid == B1D && this.suitLengths[D] >= 5
				&& this.suitLengths[C] >= 4) {
			bid = B2D;
			bidVerbose = "<4 spades, 5+ diamonds, 4 clubs: 2D";
		} else if (this.previousBid == B1H && this.suitLengths[H] >= 5
				&& this.suitLengths[C] >= 4) {
			bid = B2C;
			bidVerbose = "<4 spades, 5+ hearts, 4+ clubs: 2C (raw)";
		} else if (this.previousBid == B1H && this.suitLengths[H] >= 5
				&& this.suitLengths[D] >= 4) {
			bid = B2D;
			bidVerbose = "<4 spades, 5+ hearts, 4+ diamonds: 2D (raw)";
		} else if ((bid = checkFor6(pointRangeHint + "<4 spades, ")) != null) {
			// do nothing - bid found
		} else {
			bid = B1N;
			bidVerbose = pointRangeHint + "no 4+ spades or 6+ suit: 1NT";
		}
		return bid;
	}
	private BidEnum getResponseTo1NT() {
		BidEnum bid;
		if (this.highCardPoints <= 10) {
			if (this.suitLengths[H] >= 6) {
				bid = B2H;
				bidVerbose = "0-10 HCP, 6+ hearts: 2H";
			} else if (this.suitLengths[S] >= 6) {
				bid = B2H;
				bidVerbose = "0-10 HCP, 6+ spades: 2S";
			} else {
				bid = BPass;
				bidVerbose = "0-10 HCP: BPass";
			}
		} else if (this.highCardPoints >= 12) {
			bid = B2D;
			bidVerbose = "12+ HCP: 2D";
		} else if (this.highCardPoints >= 11) {
			bid = B2C;
			bidVerbose = "11 HCP: 2C";
		} else if (this.suitLengths[C] >= 4 && this.suitLengths[D] >= 4) {
			bid = B2N;
			bidVerbose = "11+ HCP, 4+ both minors: 2NT";
		} else if (this.suitLengths[C] >= this.suitLengths[D]) {
			bid = B3C;
			bidVerbose = "8-10 HCP, clubs length >= diamonds: 2C";
		} else {
			bid = B3D;
			bidVerbose = "8-10 HCP, more diamonds than clubs: 2D";
		}
		return bid;
	}
	private BidEnum getResponseTo2C(BidEnum partnerBid) {
		BidEnum bid;
		if (this.highCardPoints <= 7) {
			bid = BPass;
			bidVerbose = "0-7 HCP: BPass";
		} else if (this.highCardPoints >= 11 && this.suitLengths[H] >= 5) {
			bid = B2H;
			bidVerbose = "11+ HCP, 5+ hearts: 1H";
		} else if (this.highCardPoints >= 11 && this.suitLengths[S] >= 5) {
			bid = B2S;
			bidVerbose = "11+ HCP, <5 hearts, 5+ spades: 2S";
		} else if (this.suitLengths[C] >= 3) { // i.e. clubs fit; 2C opener by partner shows 6+ clubs
			this.auctionType = AuctionType.FIT;
			if (this.highCardPoints >= 11) { 
				bid = checkForSplinter(partnerBid);
				if (bid != null) {
					// nothing to do!
				} else {
					bid = B2N;
					bidVerbose = "11+ HCP, <5 in majors, 4+ clubs: 2NT";
				}
			} else if (suitLengths[C] >= 5) {
				bid = B5C;
				bidVerbose = "8+ HCP, 5+ clubs: 5C";
			} else if (suitLengths[C] == 4) {
				bid = B4C;
				bidVerbose = "8+ HCP, 4 clubs: 4C";
			} else {
				bid = B3C;
				bidVerbose = "8+ HCP, 3 clubs: 3C";
			}
		} else {
			bid = BPass;
			bidVerbose = "8+ HCP, <5 in majors, <3 clubs: BPass";
		}
		return bid;
	}
	private BidEnum getResponseTo2D(BidEnum partnerBid) {
		BidEnum bid;
		if (this.highCardPoints <= 7) {
			bid = BPass;
			bidVerbose = "0-7 HCP: BPass";
		} else if (this.highCardPoints >= 11 && this.suitLengths[H] >= 5) {
			bid = B2H;
			bidVerbose = "11+ HCP, 5+ hearts: 1H";
		} else if (this.highCardPoints >= 11 && this.suitLengths[S] >= 5) {
			bid = B2S;
			bidVerbose = "11+ HCP, <5 hearts, 5+ spades: 2S";
		} else if (this.suitLengths[D] >= 3) { // i.e. diamonds fit; 2D opener by partner shows 6+ diamonds
			this.auctionType = AuctionType.FIT;
			if (this.highCardPoints >= 11) { 
				bid = checkForSplinter(partnerBid);
				if (bid != null) {
					// nothing to do!
				} else {
					bid = B2N;
					bidVerbose = "11+ HCP, <5 in majors, 4+ clubs: 2NT";
				}
			} else if (suitLengths[D] >= 5) {
				bid = B5D;
				bidVerbose = "8+ HCP, 5+ diamonds: 5D";
			} else if (suitLengths[D] == 4) {
				bid = B4D;
				bidVerbose = "8+ HCP, 4 diamonds: 4D";
			} else {
				bid = B3D;
				bidVerbose = "8+ HCP, 3 diamonds: 3D";
			}
		} else {
			bid = BPass;
			bidVerbose = "8+ HCP, <5 in majors, <4 diamonds: BPass";
		}
		return bid;
	}
	private BidEnum getResponseTo2S(boolean forcing, BidEnum partnerBid) {
		BidEnum bid;
		
		String pointRangeHint;
		if (this.previousBid != null) {
			pointRangeHint = "";
		} else {
			pointRangeHint = "8-10 HCP, ";
		}
		if (this.auctionType == AuctionType.FIT) {
			if ((bid = checkForSplinter(partnerBid)) != null) {
				// nothing to do!
			} else if (suitLengths[C] >= 4) {
				bid = B3C;
				bidVerbose = "fit, 4+ clubs: 3C";
			} else if (suitLengths[D] >= 4) {
				bid = B3D;
				bidVerbose = "fit, 4+ diamonds: 3D";
			} else {
				bid = B2N;
				bidVerbose = "fit, waiting: 2NT";
			}
			// TODO: rest of this doesn't work yet!
		} else if (this.highCardPoints <= 7 && !forcing) {
			bid = BPass;
			bidVerbose = "0-7 HCP: BPass";
		} else if (suitLengths[S] >= 4) { // i.e. spade fit
			this.auctionType = AuctionType.FIT;
			if (this.previousBid == B1C) {
				if (this.playingPoints >= 19) {
					bid = B2N;
					bidVerbose = "4+ spades, 19+ HCP: 2NT (strong fit)";
				} else {
					bid = B3S;
					bidVerbose = "4+ spades, < 19 HCP: 3S";
				}
			} else if (this.previousBid == B1D) {
				bid = B3S;
				bidVerbose = "4 spades: 3S";
			} else if ((bid = checkForSplinter(partnerBid)) != null) {
					// nothing to do!
			} else if (!this.limited && this.highCardPoints >= 11) {
				bid = B2N;
				bidVerbose = "11+ HCP, 4+ spades: 2NT";
			} else if (suitLengths[S] >= 6) {
				bid = B4S;
				bidVerbose = pointRangeHint + "6+ spades: 4S";
			} else if (suitLengths[S] == 5) {
				bid = B3S;
				bidVerbose = pointRangeHint + "5 spades: 3S";
			} else {
				bid = B2S;
				bidVerbose = pointRangeHint + "4 spades: 2S";
			}
		} else if ((bid = checkFor3NT()) != null) {
			// do nothing - bid found
		} else if ((bid = checkFor2Choice()) != null) {
			// do nothing - bid found
		} else if ((bid = checkForRelay()) != null) {
			// do nothing - bid found
		} else if (this.previousBid == B1D && this.suitLengths[C] >= 5
				&& this.suitLengths[D] >= 4) {
			bid = B3C;
			bidVerbose = "<4 spades, 5+ clubs, 4 diamonds: 2C";
		} else if (this.previousBid == B1D && this.suitLengths[D] >= 5
				&& this.suitLengths[C] >= 4) {
			bid = B3D;
			bidVerbose = "<4 spades, 5+ diamonds, 4 clubs: 2D";
		} else if (this.previousBid == B1H && this.suitLengths[H] >= 5
				&& this.suitLengths[C] >= 4) {
			bid = B3C;
			bidVerbose = "<4 spades, 5+ hearts, 4+ clubs: 2C (raw)";
		} else if (this.previousBid == B1H && this.suitLengths[H] >= 5
				&& this.suitLengths[D] >= 4) {
			bid = B3D;
			bidVerbose = "<4 spades, 5+ hearts, 4+ diamonds: 2D (raw)";
		} else if ((bid = checkFor6(pointRangeHint + "<4 spades, ")) != null) {
			// do nothing - bid found
		} else {
			bid = B2N;
			bidVerbose = pointRangeHint + "no 4+ spades or 6+ suit: 1NT";
		}
		return bid;
	}

	public BidEnum getPrimaryBid() {
		if (this.playingPoints == 0) evaluateHand();
		BidEnum bid;
		boolean fiveCardMajor = (suitLengths[H] >= 5 || suitLengths[S] >= 5);
		if (this.previousBid == null && playingPoints >= 26) {
			bid = B1C;
			bidVerbose = ">= 26 pp: 1C";
		} else if (this.previousBid == null && playingPoints == 25) {
			if (fiveCardMajor) {
				bid = B1C;
				bidVerbose = "25 pp, 5-card major: 1C";
			} else {
				bid = B1D;
				bidVerbose = "25 pp, no 5-card major: 1D";
			}
		} else if (this.previousBid == null && playingPoints >= 23) {
			if (balanced) {
				bid = B1D;
				bidVerbose = "23-24 pp, balanced: 1D";
			} else if (fiveCardMajor) {
				if (suitLengths[H] >= 4) {
					bid = B1H;
					bidVerbose = "23-24 pp, 5-card major and 4+ hearts: 1H";
				} else {
					bid = B1S;
					bidVerbose = "23-24 pp, 5-card major: 1S";
				}
			} else
				bid = B1D;
		} else if (playingPoints >= 20) {
			String pointsHint;
			if (this.previousBid == null) {
				pointsHint = "20-22 pp, ";
			} else {
				pointsHint = "";
			}
			if ((bid = checkForMajor1(10)) != null) {
				// do nothing - bid found
			} else if (suitLengths[C] >= 5 && suitLengths[D] >= 5) {
				bid = B2N;
				bidVerbose = pointsHint + "5+ clubs, 5+ diamonds: 2NT";
			} else if (suitLengths[C] >= 6) {
				bid = B2C;
				bidVerbose = pointsHint + "6+ clubs: 2C";
			} else if (suitLengths[D] >= 6) {
				bid = B2D;
				bidVerbose = pointsHint + "6+ diamonds: 2D";
			} else {
				bid = B1N;
				bidVerbose = pointsHint + "no 4+ major, no 6+ minor: 1NT";
			}
		} else if (highCardPoints >= 6) {
			this.auctionType = AuctionType.DISTURBED;
			if (suitLengths[H] >= 8) {
				bid = B4H;
				bidVerbose = "< 20 pp, 6+ HCP, 8+ hearts: 4H";
			} else if (suitLengths[H] == 7) {
				bid = B3H;
				bidVerbose = "< 20 pp, 6+ HCP, 7 hearts: 3H";
			} else if (suitLengths[H] == 6) {
				bid = B2H;
				bidVerbose = "< 20 pp, 6+ HCP, 6 hearts: 2H";
			} else if (suitLengths[S] >= 8) {
				bid = B4S;
				bidVerbose = "< 20 pp, 6+ HCP, 8+ spades: 4S";
			} else if (suitLengths[S] == 7) {
				bid = B3S;
				bidVerbose = "< 20 pp, 6+ HCP, 7 spades: 3S";
			} else if (suitLengths[S] == 6) {
				bid = B2S;
				bidVerbose = "< 20 pp, 6+ HCP, 6 spades: 2S";
			} else if (suitLengths[C] >= 9) {
				bid = B5C;
				bidVerbose = "< 20 pp, 6+ HCP, 9+ clubs: 5C";
			} else if (suitLengths[C] == 8) {
				bid = B4C;
				bidVerbose = "< 20 pp, 6+ HCP, 8 clubs: 4C";
			} else if (suitLengths[C] == 7) {
				bid = B3C;
				bidVerbose = "< 20 pp, 6+ HCP, 7 clubs: 3C";
			} else if (suitLengths[D] >= 9) {
				bid = B5D;
				bidVerbose = "< 20 pp, 6+ HCP, 9+ diamonds: 5D";
			} else if (suitLengths[D] == 8) {
				bid = B4D;
				bidVerbose = "< 20 pp, 6+ HCP, 8 diamonds: 4D";
			} else if (suitLengths[D] == 7) {
				bid = B3D;
				bidVerbose = "< 20 pp, 6+ HCP, 7 diamonds: 3D";
			} else {
				bid = BPass;
				bidVerbose = "< 20 pp, 6+ HCP, but no long suit: BPass";
			}
		} else {
			bid = BPass;
			bidVerbose = "< 20 pp, < 6 HCP: BPass";
		}
		this.limited = !(bid == BidEnum.B1C);
		this.previousBid = bid;
		if (this.partner != null) { // can only be null during testing
			this.partner.auctionType = this.auctionType;
		}
		return bid;
	}
    public BidEnum getPrimaryBid(BridgeQuiz reviseItQuiz) {
        if (this.playingPoints == 0) evaluateHand();
        List<QuestionAnswer> openingBids = reviseItQuiz.getPossibleResponses(OPENING_BIDS);
        for (QuestionAnswer qa: openingBids) {
            // TODO: change qa into qap
            // if (doesMatchHand(qa.answer)) return BidEnum.valueOf("B" + qa.question);
        }
	    return NONE;
    }

    public void evaluateHand() {
		this.limited = false;
		for (Card card : cards) {
			int suitOrdinal = card.getSuit().ordinal();
			suitLengths[suitOrdinal]++;
			Rank rank = card.getRank();
			int hcp = rank.ordinal() - Rank.R10.ordinal();
			if (hcp > 0)
				highCardPoints += hcp;
			if (rank == Rank.Ace)
				suitValues[suitOrdinal] += 4;
			else if (rank == Rank.King)
				suitValues[suitOrdinal] += 3;
			if (rank == Rank.Queen)
				suitValues[suitOrdinal] += 2;
			if (rank == Rank.Jack)
				suitValues[suitOrdinal] += 1;
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

	private BidEnum checkForMajor1(int minHCP) {
		BidEnum bid = null;
		if (this.highCardPoints >= minHCP) {
			String pointsHint = minHCP + "+ HCP, ";
			if (suitLengths[H] >= 4) {
				bid = B1H;
				bidVerbose = pointsHint + "4+ hearts: 1H";
			} else if (suitLengths[S] >= 4) {
				bid = B1S;
				bidVerbose = pointsHint + "<4 hearts, 4+ spades: 1S";
			}
		}
		return bid;
	}

	private BidEnum checkForRelay() {
		return checkForRelay((this.previousBid == B1C) ? 19 : 11);
	}

	private BidEnum checkForRelay(int relayHCP) {
		if (this.limited)
			return null;
		BidEnum responseBid;
		if (this.highCardPoints >= (relayHCP + 2)) {
			responseBid = B2D;
			bidVerbose = (relayHCP + 2) + "+ HCP, no 4+ major: 2D (relay)";
		} else if (this.highCardPoints >= relayHCP) {
			responseBid = B2C;
			bidVerbose = relayHCP + "-" + (relayHCP + 1)
					+ " HCP, no 4+ major: 2C (relay)";
		} else {
			responseBid = null;
		}
		return responseBid;
	}

	private BidEnum checkFor2Choice() {
		if (!this.limited)
			return null;
		BidEnum responseBid;
		if (this.previousBid == B1D) {
			if (this.suitLengths[C] >= 5 && this.suitLengths[D] >= 5) {
				responseBid = B2N;
				bidVerbose = "<4 spades, 5+ clubs, 5+ diamonds: 2NT (2-choice)";
			} else {
				responseBid = null;
			}
		} else {
			if (this.suitLengths[C] >= 4 && this.suitLengths[D] >= 4) {
				responseBid = B2N;
				bidVerbose = "<4 spades, 4+ clubs, 4+ diamonds: 2NT (2-choice)";
			} else {
				responseBid = null;
			}
		}
		return responseBid;
	}

	private BidEnum checkFor6(String pointRangeHint) {
		BidEnum responseBid;
		if (this.suitLengths[H] >= 6) {
			responseBid = B2H;
			bidVerbose = pointRangeHint + "6+ hearts: 2H";
		} else if (this.suitLengths[S] >= 6) {
			responseBid = B2S;
			bidVerbose = pointRangeHint + "6+ spades: 2S";
		} else if (this.suitLengths[C] >= 6) {
			responseBid = B3C;
			bidVerbose = pointRangeHint + "6+ clubs: 3C";
		} else if (this.suitLengths[D] >= 6) {
			responseBid = B3D;
			bidVerbose = pointRangeHint + "6+ diamonds: 3D";
		} else {
			responseBid = null;
		}
		return responseBid;
	}

	private BidEnum checkFor3NT() {
		if (this.previousBid == null && this.highCardPoints >= 13
				&& this.highCardPoints <= 15 && this.suitLengths[S] == 3
				&& this.suitLengths[H] == 3
				&& (this.suitLengths[D] == 3 || this.suitLengths[D] == 4)
				&& (this.suitLengths[C] == 3 || this.suitLengths[C] == 4)) {
			bidVerbose = "13-15 HCP, 3343 or 3334: 3NT";
			return B3N;
		} else
			return null;
	}

	private BidEnum checkForSplinter(BidEnum bid) {
		Suit bidSuit = bid.suit;
		int bidSuitOrdinal = bidSuit.ordinal();
		Suit responseSuit = null;
		for (int i = 0; i < 4; i++) {
			int suitLen = this.suitLengths[i];
			if (i == bidSuitOrdinal) {
				if (suitLen < 4)
					return null; // no support for partner's suit
			} else {
				if (suitLen <= 1) {
					Suit shortSuit = Suit.values()[i];
					if (suitLen == 0) { // found void
						responseSuit = shortSuit;
					} else { // suitLen = 1
						if (this.suitValues[i] < 2) { // check not A or K or Q
							responseSuit = shortSuit;
						} else if (this.suitValues[i] == 2 || this.suitValues[i] == 3) {
							return null; // found singleton K or Q
						} // else continue to search for suitable short suit
					}
				} else if (this.suitValues[i] < 3) {
				    // i.e. no 1st or 2nd round control
					return null;
				}
			}
		}
		BidEnum responseBid = null;
		if (responseSuit != null) {
			/*
			 * map from bid to response (where response is in responseSuit):
			 * 1s/2s: 4c, 4d, 4h 1h/2h: 3s, 4c, 4d natural 2d: 3h, 3s, 4c
			 * natural 2c: 3d, 3h, 3s
			 */
			if (responseSuit == Suit.Club) {
				responseBid = B4C;
				this.bidVerbose = "splinter: 4C";
			} else if (responseSuit == Suit.Diamond) {
				if (bidSuit == Suit.Club) {
					responseBid = B3D;
					this.bidVerbose = "splinter: 3D";
				} else {
					responseBid = B4D;
					this.bidVerbose = "splinter: 4D";
				}
			} else if (responseSuit == Suit.Heart) {
				if (bidSuit == Suit.Spade) {
					responseBid = B4H;
					this.bidVerbose = "splinter: 4H";
				} else {
					responseBid = B3H;
					this.bidVerbose = "splinter: 3H";
				}
			} else if (responseSuit == Suit.Spade) {
				responseBid = B3S;
				this.bidVerbose = "splinter: 3S";
			}
		}
		return responseBid;
	}
}
