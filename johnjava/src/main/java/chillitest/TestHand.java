package chillitest;

import jarden.cards.CardPack;
import jarden.cards.Hand;
import jarden.quiz.QuestionAnswer;

import static jarden.cards.CardPack.CardEnum.C2;
import static jarden.cards.CardPack.CardEnum.C3;
import static jarden.cards.CardPack.CardEnum.C4;
import static jarden.cards.CardPack.CardEnum.C5;
import static jarden.cards.CardPack.CardEnum.C6;
import static jarden.cards.CardPack.CardEnum.C7;
import static jarden.cards.CardPack.CardEnum.C8;
import static jarden.cards.CardPack.CardEnum.C9;
import static jarden.cards.CardPack.CardEnum.CA;
import static jarden.cards.CardPack.CardEnum.CJ;
import static jarden.cards.CardPack.CardEnum.CK;
import static jarden.cards.CardPack.CardEnum.CQ;
import static jarden.cards.CardPack.CardEnum.D2;
import static jarden.cards.CardPack.CardEnum.D3;
import static jarden.cards.CardPack.CardEnum.D4;
import static jarden.cards.CardPack.CardEnum.D5;
import static jarden.cards.CardPack.CardEnum.D6;
import static jarden.cards.CardPack.CardEnum.D7;
import static jarden.cards.CardPack.CardEnum.D8;
import static jarden.cards.CardPack.CardEnum.D9;
import static jarden.cards.CardPack.CardEnum.DA;
import static jarden.cards.CardPack.CardEnum.DJ;
import static jarden.cards.CardPack.CardEnum.DK;
import static jarden.cards.CardPack.CardEnum.DQ;
import static jarden.cards.CardPack.CardEnum.DT;
import static jarden.cards.CardPack.CardEnum.H2;
import static jarden.cards.CardPack.CardEnum.H3;
import static jarden.cards.CardPack.CardEnum.H4;
import static jarden.cards.CardPack.CardEnum.H5;
import static jarden.cards.CardPack.CardEnum.H6;
import static jarden.cards.CardPack.CardEnum.H7;
import static jarden.cards.CardPack.CardEnum.H8;
import static jarden.cards.CardPack.CardEnum.H9;
import static jarden.cards.CardPack.CardEnum.HA;
import static jarden.cards.CardPack.CardEnum.HJ;
import static jarden.cards.CardPack.CardEnum.HK;
import static jarden.cards.CardPack.CardEnum.HQ;
import static jarden.cards.CardPack.CardEnum.HT;
import static jarden.cards.CardPack.CardEnum.S3;
import static jarden.cards.CardPack.CardEnum.S4;
import static jarden.cards.CardPack.CardEnum.S5;
import static jarden.cards.CardPack.CardEnum.S7;
import static jarden.cards.CardPack.CardEnum.S8;
import static jarden.cards.CardPack.CardEnum.S9;
import static jarden.cards.CardPack.CardEnum.SJ;
import static jarden.cards.CardPack.CardEnum.SK;
import static jarden.cards.CardPack.CardEnum.SQ;

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
    private static final String bid2H = "6 hearts, <20 pp, 6+ HCP";
    private static final String bid2S = "6 spades, <20 pp, 6+ HCP";
    private static final String bid2NT = "5+ in both minors, 20-22 pp";
    private static final String bid3C = "7 clubs, <20 pp, 6+ HCP";
    private static final String bid4D = "8 diamonds, <20 pp, 6+ HCP";
    private static final String bid4H = "8+ hearts, <20 pp, 6+ HCP";
    private static final String bid5C = "9+ clubs, <20 pp, 6+ HCP";
    private static final String bidPass = "<20 pp, not {6+ suit, 6+ HCP}";
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
    private static final CardPack.CardEnum[] cards2NT = { // 22pp, 5-6-1-1
            CA, CK, C9, C8, C5, DK, DJ, D9, D8, D6, D5, H9, S7
    };
    private static final CardPack.CardEnum[] cards3C = { // 16pp, 7-1-2-3
            CK, C9, C8, C6, C5, C4, C2, DK, H9, H8, S5, S4, S3
    };
    private static final CardPack.CardEnum[] cards4D = { // 17pp, 0-8-2-3
            DK, DQ, DT, D9, D8, D7, D6, D5, HJ, H8, S5, S4, S3
    };
    private static final CardPack.CardEnum[] cards4H = { // 18pp, 3-0-8-2
            C9, C8, C5, HK, HQ, H9, H8, H7, H6, H5, H3, SQ, S3
    };
    private static final CardPack.CardEnum[] cards5C = { // 19pp, 9-1-0-3
            CA, CK, C9, C8, C7, C5, C4, C3, C2, D8, S5, S4, S3
    };
    private static final CardPack.CardEnum[] cardsPass = { // 19pp, 5-1-2-5
            C9, C8, C5, C3, C2, D9, HA, HK, SQ, S8, S5, S4, S3
    };
    private static final QuestionAnswer[] answers = {
            new QuestionAnswer("1C", bid1C),
            new QuestionAnswer("1D", bid1D),
            new QuestionAnswer("1H", bid1H),
            new QuestionAnswer("1S", bid1S),
            new QuestionAnswer("1N", bid1NT),
            new QuestionAnswer("2C", bid2C),
            new QuestionAnswer("2D", bid2D),
            new QuestionAnswer("2H", bid2H),
            new QuestionAnswer("2S", bid2S),
            new QuestionAnswer("2N", bid2NT),
            new QuestionAnswer("3C", bid3C),
            new QuestionAnswer("4D", bid4D),
            new QuestionAnswer("4H", bid4H),
            new QuestionAnswer("5C", bid5C),
            new QuestionAnswer("NB", bidPass)
    };
    private static final Hand[] hands = {
            new Hand(cards1C),
            new Hand(cards1Cb),
            new Hand(cards1D),
            new Hand(cards1H),
            new Hand(cards1Hb),
            new Hand(cards1S),
            new Hand(cards1Sb),
            new Hand(cards1NT),
            new Hand(cards2C),
            new Hand(cards2D),
            new Hand(cards2H),
            new Hand(cards2S),
            new Hand(cards2NT),
            new Hand(cards3C),
            new Hand(cards4D),
            new Hand(cards4H),
            new Hand(cards5C),
            new Hand(cardsPass)
    };

    public static void main(String[] args) {
        System.out.println("start of test");
        String initialBlank = "                  ";
        int headerSize = initialBlank.length();
        System.out.print(initialBlank);
        for (QuestionAnswer qa: answers) {
            System.out.print(" " + qa.question);
        }
        System.out.println();
        for (Hand hand : hands) {
            String handName = hand.toString();
            System.out.print(hand);
            for (int i = handName.length(); i < headerSize; i++) {
                System.out.print(" ");
            }
            int matchCt = 0;
            for (QuestionAnswer qa : answers) {
                boolean isMatch = qa.getParsedAnswer().doesMatchHand(hand);
                if (isMatch) ++matchCt;
                System.out.print("  " + (isMatch ? "T" : "."));
            }
            if (matchCt != 1) System.out.print(" *** matchCt=" + matchCt);
            System.out.println();

        }
        System.out.println("end of test");
    }
}
