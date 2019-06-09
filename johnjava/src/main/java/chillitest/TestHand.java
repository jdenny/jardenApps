package chillitest;

import jarden.cards.CardPack;
import jarden.cards.Hand;
import jarden.cards.QuestionAnswerParsed;

import static jarden.cards.CardPack.CardEnum.*;

/**
 * Created by john.denny@gmail.com on 08/06/2019.
 */
public class TestHand {
    private static final String bidC1 = "25 pp, 5+ major or 26+ pp";
    private static final String bidD1 = "23-25 pp, no 5+ major";
    private static final String bidH1 = "20-22 pp, 4+ hearts or 23-24 pp, 5+ hearts";
    private static final String bidS1 =
            "<4 hearts, 20-22 pp, 4+ spades or <4 hearts, 23-24 pp, 5+ spades";
    private static final String bidNT1 =
            "20-22 pp, no 4+ major, not 5+ in both minors, no 6+ minor";
    private static final QuestionAnswerParsed qapC1 = new QuestionAnswerParsed("", bidC1);
    private static final QuestionAnswerParsed qapD1 = new QuestionAnswerParsed("", bidD1);
    private static final QuestionAnswerParsed qapH1 = new QuestionAnswerParsed("", bidH1);
    private static final QuestionAnswerParsed qapS1 = new QuestionAnswerParsed("", bidS1);
    private static final QuestionAnswerParsed qapNT1 = new QuestionAnswerParsed("", bidNT1);
    public static final QuestionAnswerParsed[] answers = {
            qapC1, qapD1, qapH1, qapS1, qapNT1
    };
    private static final CardPack.CardEnum[] cards1C = { // 25pp, 5 hearts
            CK, CQ, CJ, C8, DK, DQ, D4, HA, H6, H5, H4, H3, SJ
    };
    private static final CardPack.CardEnum[] cards1Cb = { // 26pp
            CA, CK, CQ, CJ, C8, DK, D8, D4, HA, H6, H5, H4, S3
    };
    private static final CardPack.CardEnum[] cards1D = { // 25pp, 5-4-2-2
            CA, CK, CQ, CJ, C8, DA, DQ, D5, D4, H5, H4, S5, S4
    };
    private static final CardPack.CardEnum[] cards1H = { // 21pp, 4 hearts
            CJ, C8, DK, DQ, D5, D4, HA, HK, H5, H4, S5, S4, S3
    };
    private static final CardPack.CardEnum[] cards1Hb = { // 23pp, 5 hearts
            CQ, C8, DK, DQ, D5, D4, HA, HK, H5, H4, H3, S4, S3
    };
    private static final CardPack.CardEnum[] cards1S = { // 22pp, 4 spades, <4 hearts
            CJ, C8, DK, DQ, D5, D4, HA, HK, HJ, S8, S5, S4, S3
    };
    private static final CardPack.CardEnum[] cards1Sb = { // 23pp, 6 spades, <4 hearts
            CQ, C8, DK, DQ, D5, D4, HA, SQ, S9, S8, S5, S4, S3
    };
    private static final CardPack.CardEnum[] cards1NT = { // 21pp, 4-4-2-3
            CQ, C8, C4, C3, DK, DQ, D5, D4, HA, HQ, S9, S8, S5
    };
    private static final Hand hand1C = new Hand(cards1C); // 0
    private static final Hand hand1Cb = new Hand(cards1Cb); // 1
    private static final Hand hand1D = new Hand(cards1D); // 2
    private static final Hand hand1H = new Hand(cards1H); // 3
    private static final Hand hand1Hb = new Hand(cards1Hb); // 4
    private static final Hand hand1S = new Hand(cards1S); // 5
    private static final Hand hand1Sb = new Hand(cards1Sb); // 6
    private static final Hand hand1NT = new Hand(cards1NT); // 7
    public static final Hand[] hands = {
         hand1C, hand1Cb, hand1D, hand1H, hand1Hb, hand1S, hand1Sb, hand1NT
    };

    public static void main(String[] args) {
        System.out.println("start of test");
        for (int i = 0; i < hands.length; i++) {
            System.out.print("hand" + i + " matches:");
            for (int j = 0; j < answers.length; j++) {
                System.out.print(" " + j + "=" +
                        answers[j].getParsedAnswer().doesMatchHand(hands[i]));
            }
            System.out.println();

        }
        /*!!
        testMatch(hand1C, "hand1C");
        testMatch(hand1Cb, "hand1Cb");
        testMatch(hand1D, "hand1D");
        testMatch(hand1H, "hand1H");
        testMatch(hand1Hb, "hand1Hb");
        testMatch(hand1S, "hand1S");
        testMatch(hand1Sb, "hand1Sb");
        */
        System.out.println("end of test");
    }

    private static void testMatch(Hand hand, String handName) {
        System.out.print(handName + " matches:");
        for (int j = 0; j < answers.length; j++) {
            System.out.print(" " + j + "=" +
                    answers[j].getParsedAnswer().doesMatchHand(hand));
        }
        System.out.println();
    }
}
