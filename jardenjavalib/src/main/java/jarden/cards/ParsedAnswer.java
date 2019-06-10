package jarden.cards;

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
    private int minClubs = -1, maxClubs = -1;
    private int minDiamonds = -1, maxDiamonds = -1;
    private int minHearts = -1, maxHearts = -1;
    private int minSpades = -1, maxSpades = -1;
    private ParsedAnswer orParsedAnswer;
    private ParsedAnswer notParsedAnswer; // i.e. tokens within {...}

    public ParsedAnswer(String answer) {
        int indexOr = answer.indexOf(" or ");
        if (indexOr > 0) {
            orParsedAnswer = new ParsedAnswer(answer.substring(indexOr + 4));
            answer = answer.substring(0, indexOr);
        }
        String[] tokens = answer.split(" ");
        int previousMin = -1;
        int previousMax = -1;
        boolean isNegative = false;
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            // '&' same as ','; 'in' and 'both' for readability
            if (token.equals("&") || token.equals("in") ||
                    token.equals("both")) continue;
            if (token.endsWith(",")) token = token.substring(0, token.length() - 1);
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
            } else if (token.equals("no") || token.equals("not")) {
                isNegative = true;
            } else if (token.startsWith("{")) {
                // usage: not {...}
                int indexStartB = answer.indexOf('{');
                int indexEndB = answer.indexOf("}", indexStartB);
                String notAnswer = answer.substring(indexStartB + 1, indexEndB);
                notParsedAnswer = new ParsedAnswer(notAnswer);
                answer = answer.substring(indexEndB + 1);
                tokens = answer.split(" ");
                i = 0;
                isNegative = false;
            } else if (token.endsWith("+")) {
                previousMin = Integer.parseInt(token.substring(0, token.length() - 1));
            } else if (token.startsWith("<")) {
                previousMax = Integer.parseInt(token.substring(1)) - 1;
            } else if (token.contains("-")) {
                int indexMinus = token.indexOf('-');
                previousMin = Integer.parseInt(token.substring(0, indexMinus));
                previousMax = Integer.parseInt(token.substring(indexMinus + 1));
            } else if (token.contains("/")) { // assuming n+/m+
                int indexSlash = token.indexOf('/');
                previousMin = Integer.parseInt(token.substring(0, indexSlash - 1));
                previousMax = Integer.parseInt(
                        token.substring(indexSlash + 1, token.length() - 1));
            } else {
                // assume it's a number; if not, Exception shows it's a token
                // we don't recognise!
                int number = Integer.parseInt(token);
                previousMin = previousMax = number;
            }
        }
    }
    public boolean doesMatchHand(Hand hand) {
        boolean match = doesHandMatch2(hand, this);
        if (!match && orParsedAnswer != null) match = doesHandMatch2(hand, orParsedAnswer);
        return match;
    }
    private static boolean doesHandMatch2(Hand hand, ParsedAnswer pa) {
        int handPP = hand.getPlayingPoints();
        if (pa.minPP >= 0 && handPP < pa.minPP) return false;
        if (pa.maxPP >= 0 && handPP > pa.maxPP) return false;
        int handHCP = hand.getHighCardPoints();
        if (pa.minHCP >= 0 && handHCP < pa.minHCP) return false;
        if (pa.maxHCP >= 0 && handHCP > pa.maxHCP) return false;
        if (pa.minMajor >= 0 && hand.suitLengths[2] < pa.minMajor &&
                hand.suitLengths[3] < pa.minMajor) return false;
        if (pa.minMinor >= 0 && pa.maxMinor >= 0) {
            // special case (bodge!) to cater for:
            //      not 5+/4+ in minors (put values in minMinor and maxMinor)
            if (hand.suitLengths[0] > pa.minMinor && hand.suitLengths[1] > pa.maxMinor ||
                    hand.suitLengths[1] > pa.minMinor && hand.suitLengths[0] > pa.maxMinor) {
                return false;
            }
        }
        if (pa.minMinor >= 0 && hand.suitLengths[0] < pa.minMinor &&
                hand.suitLengths[1] < pa.minMinor) return false;
        if (pa.maxMinor >= 0 && (hand.suitLengths[0] > pa.maxMinor ||
                hand.suitLengths[1] > pa.maxMinor)) return false;
        if (pa.minSuit >= 0 && hand.suitLengths[0] < pa.minSuit &&
                hand.suitLengths[1] < pa.minSuit &&
                hand.suitLengths[2] < pa.minSuit &&
                hand.suitLengths[3] < pa.minSuit) return false;
        if (pa.maxSuit >= 0 && (hand.suitLengths[0] > pa.maxSuit ||
                hand.suitLengths[1] > pa.maxSuit ||
                hand.suitLengths[2] > pa.maxSuit ||
                hand.suitLengths[3] > pa.maxSuit)) return false;
        if (pa.minClubs >= 0 &&
                hand.suitLengths[0] < pa.minClubs) return false;
        if (pa.maxClubs >= 0 &&
                hand.suitLengths[0] > pa.maxClubs) return false;
        if (pa.minDiamonds >= 0 &&
                hand.suitLengths[1] < pa.minDiamonds) return false;
        if (pa.maxDiamonds >= 0 &&
                hand.suitLengths[1] > pa.maxDiamonds) return false;
        if (pa.minHearts >= 0 &&
                hand.suitLengths[2] < pa.minHearts) return false;
        if (pa.maxHearts >= 0 &&
                hand.suitLengths[2] > pa.maxHearts) return false;
        if (pa.minSpades >= 0 &&
                hand.suitLengths[3] < pa.minSpades) return false;
        if (pa.maxSpades >= 0 &&
                hand.suitLengths[3] > pa.maxSpades) return false;
        if (pa.notParsedAnswer != null) {
            boolean notAnswerMatch = pa.notParsedAnswer.doesMatchHand(hand);
            if (notAnswerMatch) return false;
        }
        return true;
    }
}
