package jarden.cards;

import static jarden.cards.Hand.*;

/**
 * Created by john.denny@gmail.com on 08/06/2019.
 * Example parsing:
 *    token   previousMin previousMax
 *    5        5           5
 *    5+       5          -1
 *    <5      -1           4
 *    5-6      5           6
 */
public class ParsedAnswer {
    private int minPP = -1, maxPP = -1;
    private int minHCP = -1, maxHCP = -1;
    private int minSuit = -1, maxSuit = -1;
    private int minMajor = -1;
    private int minMinor = -1, maxMinor = -1;
    private int minClubWinners = -1;
    private int minDiamondWinners = -1;
    private int minHeartWinners = -1;
    private int minSpadeWinners = -1;
    private int minClubs = -1, maxClubs = -1;
    private int minDiamonds = -1, maxDiamonds = -1;
    private int minHearts = -1, maxHearts = -1;
    private int minSpades = -1, maxSpades = -1;
    private int heartsWithHonours = -1;
    private int spadesWithHonours = -1;
    private int minBiddableSuits = -1;
    private boolean allSuitsGuarded = false;
    private boolean clubGuard = false;
    private boolean diamondGuard = false;
    private boolean heartGuard = false;
    private boolean spadeGuard = false;
    private boolean balanced = false;
    private Boolean clubKing = null;
    private Boolean diamondKing = null;
    private Boolean heartKing = null;
    private Boolean spadeKing = null;
    private Boolean clubQueen = null;
    private Boolean diamondQueen = null;
    private Boolean heartQueen = null;
    private Boolean spadeQueen = null;
    private int hcpOrSkew = -1;
    private int hcpOrSkewWith4PlusMinor = -1;
    private int keyCardsClubs = -1;
    private int keyCardsDiamonds = -1;
    private int keyCardsHearts = -1;
    private int keyCardsSpades = -1;
    private ParsedAnswer orParsedAnswer;
    private ParsedAnswer notParsedAnswer; // i.e. tokens within {...}
    private final String[] ignoredWords = {
            "", // empty token
            "&", // same as ','
            "in", "both", // readability
            // notes to reader:
            "autofit", "compelling-relay", "invitational-relay", "keycard-ask", "limited",
            "spade-queen-ask", "suit-setter", "to-play", "values-for-5", "waiting"
    };

    public ParsedAnswer(String answer) throws BadBridgeTokenException {
        int indexOr = answer.indexOf(" or ");
        if (indexOr > 0) {
            orParsedAnswer = new ParsedAnswer(answer.substring(indexOr + 4));
            answer = answer.substring(0, indexOr);
        }
        String[] tokens = answer.split("[ ,;]+");
        int previousMin = -1;
        int previousMax = -1;
        int orNumber = -1;
        boolean isNegative = false;
        for (int tokenIndex = 0; tokenIndex < tokens.length; tokenIndex++) {
            String token = tokens[tokenIndex];
            boolean ignored = false;
            for (String ignoredWord: ignoredWords) {
                if (token.equals(ignoredWord)) {
                    ignored = true;
                    break;
                }
            }
            if (ignored) continue;

            if (token.equals("pp")) {
                // usage of pp: <20 pp, 26+ pp, 25 pp, 20-22 pp
                if (previousMax > 0) {
                    maxPP = previousMax;
                    previousMax = -1;
                }
                if (previousMin > 0) {
                    minPP = previousMin;
                    previousMin = -1;
                }
            } else if (token.equals("HCP")) {
                // usage of HCP: 6+ HCP, 11-15 HCP
                if (previousMax > 0) {
                    maxHCP = previousMax;
                    previousMax = -1;
                }
                if (previousMin > 0) {
                    minHCP = previousMin;
                    previousMin = -1;
                }
            } else if (token.equals("clubs")) {
                // usage of clubs|diamonds|hearts|spades:
                // <4 clubs, 4+ clubs, 7 clubs
                if (previousMax > 0) {
                    maxClubs = previousMax;
                    previousMax = -1;
                }
                if (previousMin > 0) {
                    minClubs = previousMin;
                    previousMin = -1;
                }
            } else if (token.equals("diamonds")) {
                if (previousMax > 0) {
                    maxDiamonds = previousMax;
                    previousMax = -1;
                }
                if (previousMin > 0) {
                    minDiamonds = previousMin;
                    previousMin = -1;
                }
            } else if (token.equals("hearts")) {
                if (previousMax > 0) {
                    maxHearts = previousMax;
                    previousMax = -1;
                }
                if (previousMin > 0) {
                    minHearts = previousMin;
                    previousMin = -1;
                }
            } else if (token.equals("hearts-with-honours")) {
                heartsWithHonours = previousMin;
                previousMin = -1;
            } else if (token.equals("spades-with-honours")) {
                spadesWithHonours = previousMin;
                previousMin = -1;
            } else if (token.equals("spades")) {
                if (previousMax > 0) {
                    maxSpades = previousMax;
                    previousMax = -1;
                }
                if (previousMin > 0) {
                    minSpades = previousMin;
                    previousMin = -1;
                }
            } else if (token.equals("major")) {
                // usage of major: [no] <n>+ major
                if (isNegative) {
                    if (previousMin > 0) {
                        maxSpades = maxHearts = previousMin - 1;
                        previousMin = -1;
                        isNegative = false;
                    }
                } else {
                    if (previousMin > 0) {
                        minMajor = previousMin;
                        previousMin = -1;
                    }
                }
            } else if (token.equals("minor")) {
                // usage of minor: [no] <n>+ minor
                if (isNegative) {
                    if (previousMin > 0) {
                        maxDiamonds = maxClubs = previousMin - 1;
                        previousMin = -1;
                        isNegative = false;
                    }
                } else {
                    if (previousMin > 0) {
                        minMinor = previousMin;
                        previousMin = -1;
                    }
                }
            } else if (token.equals("suit")) {
                // usage of suit: [no] <n>+ suit
                if (isNegative) {
                    if (previousMin > 0) {
                        maxSuit = previousMin - 1;
                        previousMin = -1;
                        isNegative = false;
                    }
                } else {
                    if (previousMin > 0) {
                        minSuit = previousMin;
                        previousMin = -1;
                    }
                }
            } else if (token.equals("minors")) {
                // usage of minors:
                //      not 5+/4+ in minors (put values in minMinor and maxMinor)
                //      [not] <n>+ in [both] minors
                if (previousMin > 0 && previousMax > 0) { // i.e. first usage
                    maxMinor = previousMax;
                    minMinor = previousMin;
                    previousMin = -1;
                    previousMax = -1;
                } else {
                    if (isNegative) {
                        if (previousMin > 0) {
                            maxMinor = previousMin - 1;
                            previousMin = -1;
                            isNegative = false;
                        }
                    } else {
                        if (previousMin > 0) {
                            minDiamonds = minClubs = previousMin;
                            previousMin = -1;
                        }
                    }
                }
            } else if (token.equals("club-winners")) {
                if (previousMin > 0) {
                    minClubWinners = previousMin;
                    previousMin = -1;
                }
            } else if (token.equals("diamond-winners")) {
                if (previousMin > 0) {
                    minDiamondWinners = previousMin;
                    previousMin = -1;
                }
            } else if (token.equals("heart-winners")) {
                if (previousMin > 0) {
                    minHeartWinners = previousMin;
                    previousMin = -1;
                }
            } else if (token.equals("spade-winners")) {
                if (previousMin > 0) {
                    minSpadeWinners = previousMin;
                    previousMin = -1;
                }
            } else if (token.equals("biddable-suits")) {
                if (previousMin > 0) {
                    minBiddableSuits = previousMin;
                    previousMin = -1;
                }
            } else if (token.equals("HCP-or-skew")) {
                // usage: n+ HCP-or-skew
                if (previousMin > 0) {
                    hcpOrSkew = previousMin;
                    previousMin = -1;
                }
            } else if (token.equals("HCP-or-skew-with-4+minor")) {
                // usage: not n+ HCP-or-skew-with-4+minor
                if (previousMin > 0) {
                    hcpOrSkewWith4PlusMinor = previousMin;
                    previousMin = -1;
                    isNegative = false;
                }
            } else if (token.equals("keycards-clubs")) {
                keyCardsClubs = previousMin;
                // previousMax not needed to keycards
                previousMin = -1;
                previousMax = -1;
            } else if (token.equals("keycards-diamonds")) {
                keyCardsDiamonds = previousMin;
                // previousMax not needed to keycards
                previousMin = -1;
                previousMax = -1;
            } else if (token.equals("keycards-hearts")) {
                keyCardsHearts = previousMin;
                // previousMax not needed to keycards
                previousMin = -1;
                previousMax = -1;
            } else if (token.equals("keycards-spades")) {
                keyCardsSpades = previousMin;
                // previousMax not needed to keycards
                previousMin = -1;
                previousMax = -1;
            } else if (token.equals("club-guard")) {
                clubGuard = true;
            } else if (token.equals("diamond-guard")) {
                diamondGuard = true;
            } else if (token.equals("heart-guard")) {
                heartGuard = true;
            } else if (token.equals("spade-guard")) {
                spadeGuard = true;
            } else if (token.equals("club-king")) {
                clubKing = !isNegative;
                isNegative = false;
            } else if (token.equals("diamond-king")) {
                diamondKing = !isNegative;
                isNegative = false;
            } else if (token.equals("heart-king")) {
                heartKing = !isNegative;
                isNegative = false;
            } else if (token.equals("spade-king")) {
                spadeKing = !isNegative;
                isNegative = false;
            } else if (token.equals("club-queen")) {
                clubQueen = !isNegative;
                isNegative = false;
            } else if (token.equals("diamond-queen")) {
                diamondQueen = !isNegative;
                isNegative = false;
            } else if (token.equals("heart-queen")) {
                heartQueen = !isNegative;
                isNegative = false;
            } else if (token.equals("spade-queen")) {
                spadeQueen = !isNegative;
                isNegative = false;
            } else if (token.equals("all-suits-guarded")) {
                allSuitsGuarded = true;
            } else if (token.equals("balanced")) {
                balanced = true;
            } else if (token.equals("no") || token.equals("not")) {
                isNegative = true;
            } else if (token.startsWith("{")) {
                // usage: not {...} [not {...}]
                // create linkedList of ParsedAnswer, with first in this.notParsedAnswer
                int indexStartB = answer.indexOf('{');
                int indexEndB = answer.indexOf("}", indexStartB);
                String notAnswer = answer.substring(indexStartB + 1, indexEndB);
                ParsedAnswer notPA = this;
                while (notPA.notParsedAnswer != null) {
                    notPA = notPA.notParsedAnswer;
                }
                notPA.notParsedAnswer = new ParsedAnswer(notAnswer);
                // continue to process rest of tokens:
                answer = answer.substring(indexEndB + 1);
                tokens = answer.split("[ ,;]+");
                tokenIndex = 0;
                isNegative = false;
            } else if (token.contains("/")) { // assuming n+/m+
                int indexSlash = token.indexOf('/');
                previousMin = Integer.parseInt(token.substring(0, indexSlash - 1));
                previousMax = Integer.parseInt(
                        token.substring(indexSlash + 1, token.length() - 1));
            } else if (token.endsWith("+")) {
                previousMin = Integer.parseInt(token.substring(0, token.length() - 1));
            } else if (token.startsWith("<")) {
                previousMax = Integer.parseInt(token.substring(1)) - 1;
            } else if (token.contains("-or-")) {
                int indexMinus = token.indexOf('-');
                previousMin = Integer.parseInt(token.substring(0, indexMinus));
                orNumber = Integer.parseInt(token.substring(indexMinus + 4));
            } else if (token.contains("-")) {
                int indexMinus = token.indexOf('-');
                previousMin = Integer.parseInt(token.substring(0, indexMinus));
                previousMax = Integer.parseInt(token.substring(indexMinus + 1));
            } else {
                // assume it's a number; if not, Exception shows it's a token
                // we don't recognise!
                try {
                    int number = Integer.parseInt(token);
                    previousMin = previousMax = number;
                } catch (NumberFormatException nfe) {
                    throw new BadBridgeTokenException(answer, nfe);
                } catch (RuntimeException re) {
                    throw new RuntimeException(answer, re);
                }
            }
        }
    }
    public boolean doesMatchHand(Hand hand) {
        return doesHandMatch2(hand, this);
    }
    private boolean doesHandMatch2(Hand hand, ParsedAnswer pa) {
        boolean match = doesHandMatch3(hand, pa);
        if (!match && pa.orParsedAnswer != null) match = doesHandMatch2(hand, pa.orParsedAnswer);
        return match;
    }
    private boolean doesHandMatch3(Hand hand, ParsedAnswer pa) {
        int handPP = hand.getPlayingPoints();
        int[] suitLengths = hand.getSuitLengths();
        int[] suitValues = hand.getSuitValues();
        if (pa.minPP >= 0 && handPP < pa.minPP) return false;
        if (pa.maxPP >= 0 && handPP > pa.maxPP) return false;
        int handHCP = hand.getHighCardPoints();
        if (pa.minHCP >= 0 && handHCP < pa.minHCP) return false;
        if (pa.maxHCP >= 0 && handHCP > pa.maxHCP) return false;
        if (pa.minMajor >= 0 && suitLengths[2] < pa.minMajor &&
                suitLengths[3] < pa.minMajor) return false;
        if (pa.minMinor >= 0 && pa.maxMinor >= 0) {
            // special case (bodge!) to cater for:
            //      not 5+/4+ in minors (put values in minMinor and maxMinor)
            if (suitLengths[0] > pa.minMinor && suitLengths[1] > pa.maxMinor ||
                    suitLengths[1] > pa.minMinor && suitLengths[0] > pa.maxMinor) {
                return false;
            }
        }
        if (pa.minMinor >= 0 && suitLengths[0] < pa.minMinor &&
                suitLengths[1] < pa.minMinor) return false;
        // another special case(!) maxMinor used for both minors
        if (pa.maxMinor >= 0 && (suitLengths[0] > pa.maxMinor &&
                suitLengths[1] > pa.maxMinor)) return false;
        if (pa.minSuit >= 0 && suitLengths[0] < pa.minSuit &&
                suitLengths[1] < pa.minSuit &&
                suitLengths[2] < pa.minSuit &&
                suitLengths[3] < pa.minSuit) return false;
        if (pa.maxSuit >= 0 && (suitLengths[0] > pa.maxSuit ||
                suitLengths[1] > pa.maxSuit ||
                suitLengths[2] > pa.maxSuit ||
                suitLengths[3] > pa.maxSuit)) return false;
        if (pa.minClubs >= 0 &&
                suitLengths[0] < pa.minClubs) return false;
        if (pa.maxClubs >= 0 &&
                suitLengths[0] > pa.maxClubs) return false;
        if (pa.minDiamonds >= 0 &&
                suitLengths[1] < pa.minDiamonds) return false;
        if (pa.maxDiamonds >= 0 &&
                suitLengths[1] > pa.maxDiamonds) return false;
        if (pa.minHearts >= 0 &&
                suitLengths[2] < pa.minHearts) return false;
        if (pa.maxHearts >= 0 &&
                suitLengths[2] > pa.maxHearts) return false;
        if (pa.minSpades >= 0 &&
                suitLengths[3] < pa.minSpades) return false;
        if (pa.maxSpades >= 0 &&
                suitLengths[3] > pa.maxSpades) return false;
        if (pa.heartsWithHonours >= 0 &&
                suitLengths[2] < pa.heartsWithHonours && suitValues[2] < 7) return false;
        if (pa.minClubWinners > 0 &&
                (suitLengths[0] + suitValues[0] < pa.minClubWinners * 3)) return false;
        if (pa.minDiamondWinners > 0 &&
                (suitLengths[1] + suitValues[1] < pa.minDiamondWinners * 3)) return false;
        if (pa.minHeartWinners > 0 &&
                (suitLengths[2] + suitValues[2] < pa.minHeartWinners * 3)) return false;
        if (pa.minSpadeWinners > 0 &&
                (suitLengths[3] + suitValues[3] < pa.minSpadeWinners * 3)) return false;
        if (pa.hcpOrSkew >= 0 && handHCP < pa.hcpOrSkew && !hand.isSkew()) return false;
        if (pa.hcpOrSkewWith4PlusMinor >= 0) {
            /*
            boolean minor4 = suitLengths[0] >= 4 || suitLengths[1] >= 4;
            boolean enoughHCP = handHCP >= pa.hcpOrSkewWith4PlusMinor;
            boolean isSkew = hand.isSkew();
            return !(minor4 && (enoughHCP || isSkew));
            // more succinctly:
            */
            return !((suitLengths[0] >= 4 || suitLengths[1] >= 4) &&
                    (handHCP >= pa.hcpOrSkewWith4PlusMinor || hand.isSkew()));
        }
        if (pa.minBiddableSuits > 0) {
            int biddableSuits = 0;
            for (int i = 0; i < 4; i++) {
                if (suitLengths[i] >= 4) ++biddableSuits;
            }
            if (biddableSuits < pa.minBiddableSuits) return false;
        }
        if (pa.allSuitsGuarded) {
            for (int i = 0; i < 4; i++) {
                if (suitValues[i] < 4) return false;
            }
        }
        if (pa.clubGuard && suitValues[0] < 4) return false;
        if (pa.diamondGuard && suitValues[1] < 4) return false;
        if (pa.heartGuard && suitValues[2] < 4) return false;
        if (pa.spadeGuard && suitValues[3] < 4) return false;
        if (pa.balanced && !hand.isBalanced()) return false;
        if (pa.clubKing != null && (hand.hasKing(C) != pa.clubKing)) return false;
        if (pa.diamondKing != null && (hand.hasKing(D) != pa.diamondKing)) return false;
        if (pa.heartKing != null && (hand.hasKing(H) != pa.heartKing)) return false;
        if (pa.spadeKing != null && (hand.hasKing(S) != pa.spadeKing)) return false;
        if (pa.clubQueen != null && (hand.hasQueen(C) != pa.clubQueen)) return false;
        if (pa.diamondQueen != null && (hand.hasQueen(D) != pa.diamondQueen)) return false;
        if (pa.heartQueen != null && (hand.hasQueen(H) != pa.heartQueen)) return false;
        if (pa.spadeQueen != null && (hand.hasQueen(S) != pa.spadeQueen)) return false;
        if (pa.keyCardsClubs > -1) {
            int keycardCt = hand.getKeyCardCt(Suit.Club);
            if ((keycardCt != pa.keyCardsClubs) &&
                    (keycardCt != pa.keyCardsClubs + 3)) return false;
        }
        if (pa.keyCardsDiamonds > -1) {
            int keycardCt = hand.getKeyCardCt(Suit.Diamond);
            if ((keycardCt != pa.keyCardsDiamonds) &&
                    (keycardCt != pa.keyCardsDiamonds + 3)) return false;
        }
        if (pa.keyCardsHearts > -1) {
            int keycardCt = hand.getKeyCardCt(Suit.Heart);
            if ((keycardCt != pa.keyCardsHearts) &&
                    (keycardCt != pa.keyCardsHearts + 3)) return false;
        }
        if (pa.keyCardsSpades > -1) {
            int keycardCt = hand.getKeyCardCt(Suit.Spade);
            if ((keycardCt != pa.keyCardsSpades) &&
                    (keycardCt != pa.keyCardsSpades + 3)) return false;
        }
        ParsedAnswer notPA = pa.notParsedAnswer;
        while (notPA != null) {
            if (notPA.doesMatchHand(hand)) return false;
            notPA = notPA.notParsedAnswer;
        }
        return true;
    }
}
