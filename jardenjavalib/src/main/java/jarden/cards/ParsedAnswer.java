package jarden.cards;

/**
 * Created by john.denny@gmail.com on 08/06/2019.
 */
public class ParsedAnswer {
    private int minPP = -1, maxPP = -1;
    private int minMajor = -1, maxMajor = -1;
    private int minHearts = -1, maxHearts = -1;
    private int minSpades = -1, maxSpades = -1;
    private ParsedAnswer orParsedAnswer;

    public ParsedAnswer(String answer) {
        int indexOr = answer.indexOf(" or ");
        if (indexOr > 0) {
            orParsedAnswer = new ParsedAnswer(answer.substring(indexOr + 4));
            answer = answer.substring(0, indexOr);
        }
        String[] tokens = answer.split(" ");
        int previousPlus = -1;
        boolean isNegative = false;
        for (String token: tokens) {
            if (token.equals("&")) continue;
            if (token.endsWith(",")) token = token.substring(0, token.length() - 1);
            if (token.endsWith("pp")) {
                String pp = token.substring(0, token.length() - 2);
                int indexMinus = pp.indexOf('-');
                if (indexMinus > 0) {
                    minPP = Integer.parseInt(pp.substring(0, indexMinus));
                    maxPP = Integer.parseInt(pp.substring(indexMinus + 1));
                } else {
                    int indexPlus = pp.indexOf('+');
                    if (indexPlus > 0) {
                        minPP = Integer.parseInt(pp.substring(0, indexPlus));
                    } else {
                        minPP = Integer.parseInt(pp);
                        maxPP = minPP;
                    }
                }
            } else if (token.endsWith("+")) {
                previousPlus = Integer.parseInt(token.substring(0, token.length() - 1));
            } else if (token.equals("hearts")) {
                minHearts = previousPlus;
                previousPlus = -1;
            } else if (token.equals("major")) {
                if (isNegative) {
                    maxMajor = previousPlus - 1;
                    isNegative = false;
                } else {
                    minMajor = previousPlus;
                }
                previousPlus = -1;
            } else if (token.equals("no")) {
                isNegative = true;
            }
        }
    }
    // 1C=25pp & 5+ major or 26+pp
    // 1D=23-25pp, no 5+ major
    // 1H=20-22pp & 4+ hearts or 23-24pp & 5+ hearts
    // 1S=<4 hearts; 20-22pp & 4+ spades or 23-24pp & 5+ spades
    public boolean doesHandMatch(Hand hand) {
        boolean match = doesHandMatch2(hand, this);
        if (!match && orParsedAnswer != null) match = doesHandMatch2(hand, orParsedAnswer);
        return match;
    }
    private static boolean doesHandMatch2(Hand hand, ParsedAnswer pa) {
        if (pa.minPP >= 0 && hand.getPlayingPoints() < pa.minPP) return false;
        if (pa.maxPP >= 0 && hand.getPlayingPoints() > pa.maxPP) return false;
        if (pa.minMajor >= 0 &&
                hand.suitLengths[2] < pa.minMajor &&
                hand.suitLengths[3] < pa.minMajor) return false;
        if (pa.maxMajor >= 0 &&
                (hand.suitLengths[2] > pa.maxMajor ||
                hand.suitLengths[3] > pa.maxMajor)) return false;
        if (pa.minHearts >= 0 &&
                hand.suitLengths[2] < pa.minHearts) return false;
        return true;
    }
}
