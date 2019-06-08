package chillitest;

import jarden.cards.CardPack;
import jarden.cards.Hand;
import jarden.cards.QuestionAnswerParsed;

import static jarden.cards.CardPack.CardEnum.C8;
import static jarden.cards.CardPack.CardEnum.CA;
import static jarden.cards.CardPack.CardEnum.CJ;
import static jarden.cards.CardPack.CardEnum.CK;
import static jarden.cards.CardPack.CardEnum.CQ;
import static jarden.cards.CardPack.CardEnum.D4;
import static jarden.cards.CardPack.CardEnum.D5;
import static jarden.cards.CardPack.CardEnum.D8;
import static jarden.cards.CardPack.CardEnum.DA;
import static jarden.cards.CardPack.CardEnum.DK;
import static jarden.cards.CardPack.CardEnum.DQ;
import static jarden.cards.CardPack.CardEnum.H3;
import static jarden.cards.CardPack.CardEnum.H4;
import static jarden.cards.CardPack.CardEnum.H5;
import static jarden.cards.CardPack.CardEnum.H6;
import static jarden.cards.CardPack.CardEnum.HA;
import static jarden.cards.CardPack.CardEnum.HK;
import static jarden.cards.CardPack.CardEnum.S3;
import static jarden.cards.CardPack.CardEnum.S4;
import static jarden.cards.CardPack.CardEnum.S5;
import static jarden.cards.CardPack.CardEnum.SJ;

/**
 * Created by john.denny@gmail.com on 08/06/2019.
 */
public class TestHand {
    private static final String bidC1 = "25pp & 5+ major or 26+pp";
    private static final String bidD1 = "23-25pp, no 5+ major";
    private static final String bidH1 = "20-22pp & 4+ hearts or 23-24pp & 5+ hearts";
    private static final QuestionAnswerParsed qapC1 = new QuestionAnswerParsed("", bidC1);
    private static final QuestionAnswerParsed qapD1 = new QuestionAnswerParsed("", bidD1);
    private static final QuestionAnswerParsed qapH1 = new QuestionAnswerParsed("", bidH1);
    private static final QuestionAnswerParsed[] answers = {
            qapC1, qapD1, qapH1
    };

    public static void main(String[] args) {
        System.out.println("start of test");
        CardPack.CardEnum[] cards1C = { // 25pp, 5 hearts
                CK, CQ, CJ, C8, DK, DQ, D4, HA, H6, H5, H4, H3, SJ
        };
        CardPack.CardEnum[] cards1Cb = { // 26pp
                CA, CK, CQ, CJ, C8, DK, D8, D4, HA, H6, H5, H4, S3
        };
        CardPack.CardEnum[] cards1D = { // 25pp, no 5+ major
                CA, CK, CQ, CJ, C8, DA, DQ, D5, D4, H5, H4, S5, S4
        };
        CardPack.CardEnum[] cards1H = { // 21pp, 4 hearts
                CJ, C8, DK, DQ, D5, D4, HA, HK, H5, H4, S5, S4, S3
        };
        Hand hand1C = new Hand(cards1C);
        Hand hand1Cb = new Hand(cards1Cb);
        Hand hand1D = new Hand(cards1D);
        Hand hand1H = new Hand(cards1H);
        testMatch(hand1C, "hand1C");
        testMatch(hand1Cb, "hand1Cb");
        testMatch(hand1D, "hand1D");
        testMatch(hand1H, "hand1H");
        System.out.println("end of test");
    }

    private static void testMatch(Hand hand, String handName) {
        System.out.print(handName + " matches:");
        for (int i = 0; i < answers.length; i++) {
            System.out.print(" " + i + "=" +
                    answers[i].getParsedAnswer().doesHandMatch(hand));
        }
        System.out.println();
    }
}
