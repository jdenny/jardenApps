package jarden.quiz;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jarden.cards.Hand;

/**
 * Created by john.denny@gmail.com on 2019-06-11.
 */
public class BridgeQuiz extends PresetQuiz {
    public static final QuestionAnswer OPENING_BIDS = new QuestionAnswer("Opening bids", " ");
    private static final int OPENING_BID_CT = 21;

    public BridgeQuiz(InputStreamReader is) throws IOException {
        super(is);
    }
    public QuestionAnswer getPrimaryBid(Hand hand) {
        List<QuestionAnswer> primaryBids = getPossibleResponses(OPENING_BIDS);
        for (QuestionAnswer qa : primaryBids) {
            if (qa.getParsedAnswer().doesMatchHand(hand)) {
                return qa;
            }
        }
        return null;
    }

    public List<QuestionAnswer> getPossibleResponses(QuestionAnswer targetQA) {
        List<QuestionAnswer> possibleResponses = new ArrayList<>();
        if (targetQA == OPENING_BIDS) {
            for (int i = 0; i < OPENING_BID_CT; i++) {
                possibleResponses.add(qaList.get(i));
            }
        } else {
            String question = targetQA.question;
            for (QuestionAnswer qa : qaList) {
                if (qa.question.startsWith(question + ", ") ||
                        qa.question.startsWith(question + "; ")) {
                    String response = qa.question.substring(question.length() + 2);
                    if (!(response.contains(",") || response.contains(";"))) {
                        possibleResponses.add(qa);
                    }
                }
            }
        }
        return possibleResponses;
    }
    /*
    Find QuestionAnswer corresponding to bidSequence with last bid removed.
     */
    public QuestionAnswer getBackBid(String bidSequence) {
        String backQuestion;
        int lastComma = bidSequence.lastIndexOf(',');
        int lastColon = bidSequence.lastIndexOf(';');
        int lastSeparator = (lastComma > lastColon) ? lastComma : lastColon;
        if (lastSeparator == -1) return null;
        backQuestion = bidSequence.substring(0, lastSeparator);
        for (QuestionAnswer qa: this.qaList) {
            if (backQuestion.equals(qa.question)) return qa;
        }
        return new QuestionAnswer(bidSequence, "no backBid found!");
    }
    public QuestionAnswer findNextBidAnswer(String nextBid) {
        for (QuestionAnswer qa: qaList) {
            if (qa.question.equals(nextBid)) return qa;
        }
        return null;
    }
    public static List<String> getBidItems(String bidSequence) {
        List<String> bidItems = new ArrayList();
        int index = 0;
        String nextBid;
        do {
            index = findNextSeparator(bidSequence, index);
            if (index == -1) {
                nextBid = bidSequence;
            } else {
                nextBid = bidSequence.substring(0, index);
                index += 2;
            }
            bidItems.add(nextBid);
        } while (index != -1);
        return bidItems;
    }
    private static int findNextSeparator(String bidSequence, int start) {
        int commaI = bidSequence.indexOf(',', start);
        int colonI = bidSequence.indexOf(';', start);
        if (commaI == -1) {
            if (colonI == -1) return -1;
            return colonI;
        } else {
            if (colonI == -1) return commaI;
            return (commaI > colonI) ? colonI : commaI;
        }
    }

}
