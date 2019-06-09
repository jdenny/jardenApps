package chillitest;

import jarden.cards.CardPack;
import jarden.cards.Hand;
import jarden.cards.QuestionAnswerParsed;

import static jarden.cards.CardPack.CardEnum.*;

/**
 * Created by john.denny@gmail.com on 08/06/2019.
 */
public class TestHand {
    private static final String bid1C = "25 pp, 5+ major or 26+ pp";
    private static final String bid1D = "23-25 pp, no 5+ major";
    private static final String bid1H = "20-22 pp, 4+ hearts or 23-24 pp, 5+ hearts";
    private static final String bid1S =
            "<4 hearts, 20-22 pp, 4+ spades or <4 hearts, 23-24 pp, 5+ spades";
    private static final String bid1NT =
            "20-22 pp, no 4+ major, not 5+ in both minors, no 6+ minor";
    private static final String bid2C =
            "6+ clubs, 20-22 pp, no 4+ major, <5 diamonds";
    private static final String bid2D =
            "6+ diamonds, 20-22 pp, no 4+ major, <5 clubs";
    private static final String bid2H =
            "6 hearts, <20 pp";
    private static final String bid2S =
            "6 spades, <20 pp";
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
    private static final CardPack.CardEnum[] cards2C = { // 20pp, 7-1-2-3
            CK, CQ, CJ, C8, C4, C3, C2, D4, HA, HT, S9, S8, S5
    };
    private static final CardPack.CardEnum[] cards2D = { // 21pp, 2-6-2-3
            CK, CQ, DJ, D8, D7, D4, D3, D2, HA, HQ, S9, S8, S5
    };
    private static final CardPack.CardEnum[] cards2H = { // 19pp, 1-5-6-1
            CJ, D8, D7, D6, D5, D4, HA, HK, H5, H4, H3, H2, S3
    };
    private static final CardPack.CardEnum[] cards2S = { // 15pp, 3-2-2-6
            C9, C8, C5, DQ, D5, H9, H8, SK, SJ, S8, S5, S4, S3
    };
    public static final QuestionAnswerParsed[] answers = {
            new QuestionAnswerParsed("", bid1C),  // 0
            new QuestionAnswerParsed("", bid1D),  // 1
            new QuestionAnswerParsed("", bid1H),  // 2
            new QuestionAnswerParsed("", bid1S),  // 3
            new QuestionAnswerParsed("", bid1NT), // 4
            new QuestionAnswerParsed("", bid2C),  // 5
            new QuestionAnswerParsed("", bid2D),  // 6
            new QuestionAnswerParsed("", bid2H),  // 7
            new QuestionAnswerParsed("", bid2S)   // 8
    };
    public static final Hand[] hands = {
            new Hand(cards1C),  // 0
            new Hand(cards1Cb), // 1
            new Hand(cards1D),  // 2
            new Hand(cards1H),  // 3
            new Hand(cards1Hb), // 4
            new Hand(cards1S),  // 5
            new Hand(cards1Sb), // 6
            new Hand(cards1NT), // 7
            new Hand(cards2C),  // 8
            new Hand(cards2D),  // 9
            new Hand(cards2H),  // 10
            new Hand(cards2S),  // 11
    };

    public static void main(String[] args) {
        System.out.println("start of test");
        for (int i = 0; i < hands.length; i++) {
            System.out.print("hand" + i + " matches:");
            int matchCt = 0;
            for (int j = 0; j < answers.length; j++) {
                boolean isMatch = answers[j].getParsedAnswer().doesMatchHand(hands[i]);
                if (isMatch) ++matchCt;
                System.out.print(" " + j + "=" + isMatch);
            }
            if (matchCt != 1) System.out.print(" *** matchCt=" + matchCt);
            System.out.println();

        }
        System.out.println("end of test");
    }
}
