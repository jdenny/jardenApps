package chillitest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jarden.cards.CardPack;
import jarden.cards.Hand;
import jarden.cards.Player;
import jarden.quiz.BridgeQuiz;
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
import static jarden.cards.CardPack.CardEnum.CT;
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
import static jarden.cards.CardPack.CardEnum.S2;
import static jarden.cards.CardPack.CardEnum.S3;
import static jarden.cards.CardPack.CardEnum.S4;
import static jarden.cards.CardPack.CardEnum.S5;
import static jarden.cards.CardPack.CardEnum.S6;
import static jarden.cards.CardPack.CardEnum.S7;
import static jarden.cards.CardPack.CardEnum.S8;
import static jarden.cards.CardPack.CardEnum.S9;
import static jarden.cards.CardPack.CardEnum.SA;
import static jarden.cards.CardPack.CardEnum.SJ;
import static jarden.cards.CardPack.CardEnum.SK;
import static jarden.cards.CardPack.CardEnum.SQ;
import static jarden.cards.CardPack.CardEnum.ST;
import static jarden.quiz.BridgeQuiz.OPENING_BIDS;

/**
 * Created by john.denny@gmail.com on 08/06/2019.
 */
public class TestHand {
    private final Hand hand1C = new Hand(new CardPack.CardEnum[] { // 25pp, 5 hearts
            CK, CQ, CJ, C8, DK, DQ, D4, HA, H6, H5, H4, H3, SJ
    });
    private final Hand hand13Hcp3145 = new Hand(new CardPack.CardEnum[] { // 22pp, 13HCP, 3-1-4-5
            CA, CJ, C9, DK, HK, HJ, H9, H5, SJ, S6, S5, S4, S3 // skew but no 4+ minor
    });
    private final Hand hand14Hcp3343 = new Hand(new CardPack.CardEnum[] { // 21pp, 14HCP, 3-3-4-3
            CA, CQ, C9, D8, D5, DK, HJ, H9, H8, H6, S5, S9, SA // 14 HCP but no 4+ minor
    });
    private final Hand hand14Hcp2542 = new Hand(new CardPack.CardEnum[] { // 23pp, 14HCP, 2-5-4-2
            CA, CT, DK, DQ, DJ, D4, D2, HJ, H8, H7, H6, SK, S5 // 4+ minor, 14+ HCP
    });
    private final Hand hand14Hcp4540 = new Hand(new CardPack.CardEnum[] { // 23pp, 14HCP, 4-5-4-0
            CA, CT, C9, C7, DK, DQ, DJ, D4, D2, HK, HJ, H7, H6 // 4+ minor, 14+ HCP
    });

    private BridgeQuiz bridgeQuiz;
    private List<QuestionAnswer> primaryBids;
    private CardPack cardPack;

    public static void main(String[] args) throws IOException {
        System.out.println("start of test");
        TestHand testHand = new TestHand();
        /*
        testHand.testPage50();
        testHand.testPage56();
        */
        testHand.testPage56B();
        /*
        testHand.testOneBid();
        testHand.testKeycards();
        testHand.testHcpOrSkew();
        testHand.testHcpOrSkewWith4PlusMinor();
        testHand.testBiddableSuits();
        testHand.testSuitWinners();
        testHand.parseAllBids();
        testHand.testMyPrimaryBids();
        testHand.testRandomPrimaryBids();
        testHand.test2Hands1H1S();
        testHand.testOrs();
        testHand.testNots();
        testHand.testRandomNBids(10, 2);
        testHand.testRandomSecondBids();
        testHand.testAllSecondBids();
        testHand.testMySecondBids();
        */
        System.out.println("end of test");
    }

    private TestHand() throws IOException {
        File file = new File("./hotbridge/src/main/res/raw/reviseit.txt");
        InputStream is = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(is);
        bridgeQuiz = new BridgeQuiz(isr);
        cardPack = new CardPack();
        primaryBids = bridgeQuiz.getPossibleResponses(OPENING_BIDS);

    }
    private void testPage50() {
        System.out.println("testPage50");
        Hand handWest = new Hand(new CardPack.CardEnum[] { // 22pp, 13HCP, 4-5-1-3
                CA, CQ, C6, C5, DK, DQ, DJ, D8, D6, H6, SJ, S7, S4
        });
        Hand handEast = new Hand(new CardPack.CardEnum[] { // 23pp, 12HCP, 2-0-6-5
                CK, C7, HA, H9, H7, H5, H4, H3, SK, SQ, ST, S6, S5
        });
        QuestionAnswer qa = OPENING_BIDS;
        boolean west = true;
        Hand hand;
        System.out.println("West: " + handWest);
        System.out.println("East: " + handEast);
        for (int i = 0; i < 6 && qa != null; i++) {
            hand = west ? handWest : handEast;
            qa = bridgeQuiz.getNextBid(hand, qa);
            west = !west;
            System.out.println(qa);
        }
    }
    private void testPage56() {
        System.out.println("testPage56");
        Hand handWest = new Hand(new CardPack.CardEnum[] { // 24pp, 15HCP, 4-5-3-1
                CK, CQ, CJ, C9, DA, DK, D8, D6, D5, HQ, H8, H2, S8
        });
        Hand handEast = new Hand(new CardPack.CardEnum[] { // 21pp, 12HCP, 3-1-3-6
                C7, C3, C2, D6, HA, HJ, H7, SA, SK, S7, S6, S4, S3
        });
        QuestionAnswer qa = OPENING_BIDS;
        boolean west = true;
        Hand hand;
        System.out.println("West: " + handWest);
        System.out.println("East: " + handEast);
        for (int i = 0; i < 6 && qa != null; i++) {
            hand = west ? handWest : handEast;
            qa = bridgeQuiz.getNextBid(hand, qa);
            west = !west;
            System.out.println(qa);
        }
    }
    private void testPage56B() {
        System.out.println("testPage56B");
        Hand handWest = new Hand(new CardPack.CardEnum[] { // 22pp, 13HCP, 4-5-1-3
                CK, CQ, CJ, C9, DA, DK, D8, D6, D5, H8, SQ, S8, S2
        });
        Hand handEast = new Hand(new CardPack.CardEnum[] { // 23pp, 12HCP, 2-0-6-5
                C7, C3, C2, D6, HA, HJ, H7, SA, SK, S7, S6, S4, S3
        });
        QuestionAnswer qa = OPENING_BIDS;
        boolean west = true;
        Hand hand;
        System.out.println("West: " + handWest);
        System.out.println("East: " + handEast);
        for (int i = 0; i < 6 && qa != null; i++) {
            hand = west ? handWest : handEast;
            qa = bridgeQuiz.getNextBid(hand, qa);
            west = !west;
            System.out.println(qa);
        }
    }
    private void testOneBid() {
        System.out.println("testOneBid()");
        String answer = "<4 spades, not {5+ hearts & 4+ minor}, not 4+ in both minors, no 6+ suit";
        QuestionAnswer qa = new QuestionAnswer("qa", answer);
        qa.getParsedAnswer();
    }
    private void parseAllBids() {
        System.out.println("parseAllBids()");
        List<QuestionAnswer> qaList = bridgeQuiz.getQuestionAnswerList();
        for (QuestionAnswer qa: qaList) {
            qa.getParsedAnswer().doesMatchHand(hand1C);
        }
    }
    private void testKeycards() {
        System.out.println("testKeycards()");
        CardPack.CardEnum[] cards0KeycardsClubs = { // 17pp, 6HCP, 5-6-1-1
                CQ, CT, C9, C8, C5, DK, DJ, D9, D8, D6, D5, H9, S7 // D=1, H=0, S=0
        };
        CardPack.CardEnum[] cards1KeycardsClubs = { // 19pp, 8HCP, 5-6-1-1
                CA, CT, C9, C8, C5, DK, DJ, D9, D8, D6, D5, H9, S7 // D=2, H=1, S=1
        };
        CardPack.CardEnum[] cards2KeycardsClubs = { // 22pp, 12 HCP, 5-5-2-1
                CA, CT, C9, C8, C5, DJ, DT, D9, D8, D6, HA, HK, S7 // D=2, H=3, S=2
        };
        CardPack.CardEnum[] cards3KeycardsClubs = { // 26pp, 15HCP, 5-6-1-1
                CA, CK, C9, C8, C5, DK, DJ, D9, D8, D6, D5, H9, SA // D=3, H=2, S=2
        };
        CardPack.CardEnum[] cards4KeycardsClubs = { // 27pp, 16HCP, 5-6-1-1
                CA, CK, C9, C8, C5, DA, DJ, D9, D8, D6, D5, HA, SK // D=3, H3=3, S=4
        };
        CardPack.CardEnum[] cards5KeycardsClubs = { // 31pp, 20HCP, 5-6-1-1
                CA, CK, C9, C8, C5, DA, DJ, D9, D8, D6, D5, HA, SA // D=4, H=4, S=4
        };
        Hand[] hands = {
                new Hand(cards0KeycardsClubs), new Hand(cards1KeycardsClubs),
                new Hand(cards2KeycardsClubs), new Hand(cards3KeycardsClubs),
                new Hand(cards4KeycardsClubs), new Hand(cards5KeycardsClubs)
        };
        String[] answersClubs = {
                "0-or-3 keycards-clubs", "1-or-4 keycards-clubs", "2-or-5 keycards-clubs"
        };
        String[] answersDiamonds = {
                "0-or-3 keycards-diamonds", "1-or-4 keycards-diamonds", "2-or-5 keycards-diamonds"
        };
        String[] answersHearts = {
                "0-or-3 keycards-hearts", "1-or-4 keycards-hearts", "2-or-5 keycards-hearts"
        };
        String[] answersSpades = {
                "0-or-3 keycards-spades", "1-or-4 keycards-spades", "2-or-5 keycards-spades"
        };
        String[][] allAnswers = {
                answersClubs, answersDiamonds, answersHearts, answersSpades
        };
        boolean[] keycardClubsMatch = {
                true, false, false, true, false, false, // 0-or-3
                false, true, false, false, true, false, // 1-or-4
                false, false, true, false, false, true  // 2-or-5
        };
        boolean[] keycardDiamondsMatch = {
                false, false, false, true, true, false, // 0-or-3
                true, false, false, false, false, true, // 1-or-4
                false, true, true, false, false, false  // 2-or-5
        };
        boolean[] keycardHeartsMatch = {
                true, false, true, false, true, false,  // 0-or-3
                false, true, false, false, false, true, // 1-or-4
                false, false, false, true, false, false // 2-or-5
        };
        boolean[] keycardSpadesMatch = {
                true, false, false, false, false, false, // 0-or-3
                false, true, false, false, true, true,   // 1-or-4
                false, false, true, true, false, false   // 2-or-5
        };
        boolean[][] suitMatches = {
                keycardClubsMatch, keycardDiamondsMatch, keycardHeartsMatch, keycardSpadesMatch
        };
        for (int s = 0; s < suitMatches.length; s++) {
            QuestionAnswer qa;
            for (int a = 0; a < allAnswers[s].length; a++) {
                qa = new QuestionAnswer("qa", allAnswers[s][a]);
                boolean match;
                Hand hand;
                for (int h = 0; h < hands.length; h++) {
                    hand = hands[h];
                    match = qa.getParsedAnswer().doesMatchHand(hand);
                    if (match != suitMatches[s][hands.length * a + h]) {
                        System.out.println("*****" + hand + " did" +
                                (match ? "" : " not") + " match " + qa.answer);
                    }
                }
            }
        }
    }
    private void testHcpOrSkew() {
        System.out.println("testHcpOrSkew()");
        CardPack.CardEnum[] cards15Hcp5611 = { // 26pp, 15HCP, 5-6-1-1
                CA, CK, C9, C8, C5, DK, DJ, D9, D8, D6, D5, H9, SA
        };
        CardPack.CardEnum[] cards12Hcp5611 = { // 23pp, 12HCP, 5-6-1-1
                CA, CK, C9, C8, C5, DK, DJ, D9, D8, D6, D5, H9, SJ
        };
        CardPack.CardEnum[] cards15Hcp5422 = { // 24pp, 15HCP, 5-4-2-2
                CA, CK, C9, C8, C5, DK, DJ, D9, D8, H9, H6, SA, S5
        };
        CardPack.CardEnum[] cards12Hcp5422 = { // 20pp, 11HCP, 5-4-2-2
                CA, CT, C9, C8, C5, DK, DJ, D9, D8, H9, H6, SK, S5
        };
        Hand[] hands = {
                new Hand(cards15Hcp5611), new Hand(cards12Hcp5611),
                new Hand(cards15Hcp5422), new Hand(cards12Hcp5422)
        };
        boolean[] handMatches = {
                true, true, true, false
        };
        String hcpOrSkew = "no 5+ major, 4+ clubs, 15+ HCP-or-skew";
        QuestionAnswer qaSkew = new QuestionAnswer("qa", hcpOrSkew);
        for (int h = 0; h < hands.length; h++) {
            Hand hand = hands[h];
            boolean match = qaSkew.getParsedAnswer().doesMatchHand(hand);
            if (match != handMatches[h]) {
                System.out.println("*****hand[" + h + "] " + hand + " did" +
                        (match ? "" : " not") + " match " + qaSkew.answer);
            }
        }
        System.out.println("end of testHcpOrSkew");
    }
    private void testHcpOrSkewWith4PlusMinor() {
        System.out.println("testHcpOrSkewWith4PlusMinor()");
        CardPack.CardEnum[] cards13Hcp3343 = { // 20pp, 13HCP, 3-3-4-3
                // no 4+ minor; no 14+ HCP or skew
                CA, CJ, C9, D8, D5, DK, HJ, H9, H8, H6, S5, S9, SA
        };
        CardPack.CardEnum[] cards13Hcp6241 = { // 23pp, 13HCP, 6-2-4-1
                CA, CJ, C9, C8, C5, CK, DJ, D9, HA, H9, H6, H2, S5 // 4+ minor, skew
        };
        Hand[] hands = {
                new Hand(cards13Hcp3343), hand14Hcp3343, hand13Hcp3145,
                hand14Hcp2542, new Hand(cards13Hcp6241)
        };
        boolean[] handMatches = {
                true, true, true, false, false
        };
        String notHcpOrSkew = "4 hearts, not 14+ HCP-or-skew-with-4+minor";
        QuestionAnswer qaNotSkew = new QuestionAnswer("qa", notHcpOrSkew);
        for (int h = 0; h < hands.length; h++) {
            Hand hand = hands[h];
            boolean match = qaNotSkew.getParsedAnswer().doesMatchHand(hand);
            if (match != handMatches[h]) {
                System.out.println("*****hand[" + h + "] " + hand + " did" +
                        (match ? "" : " not") + " match " + qaNotSkew.answer);
            }
        }
        System.out.println("end of testHcpOrSkewWith4PlusMinor");
    }
    private void testBiddableSuits() {
        System.out.println("testBiddableSuits()");
        // biddable suit: 4+ in suit
        Hand[] hands = {
                hand14Hcp3343, hand13Hcp3145, hand14Hcp4540
        };
        boolean[] handMatches = {
                false, true, true
        };
        String biddableQuestion = "20+ pp, 2+ biddable-suits";
        QuestionAnswer qa = new QuestionAnswer("qa", biddableQuestion);
        for (int h = 0; h < hands.length; h++) {
            Hand hand = hands[h];
            boolean match = qa.getParsedAnswer().doesMatchHand(hand);
            if (match != handMatches[h]) {
                System.out.println("*****hand[" + h + "] " + hand + " did" +
                        (match ? "" : " not") + " match " + qa.answer);
            }
        }
        System.out.println("end of testBiddableSuits");
    }
    private void testSuitWinners() {
        System.out.println("testSuitWinners()");
        // suitLength + suitValues >= minWinners * 3
        Hand hand11Hcp6025 = new Hand(new CardPack.CardEnum[] { // 22pp, 11HCP, 6-0-2-5
                CA, CK, CQ, CJ, C9, C8, H9, H5, SJ, S6, S5, S4, S3
        });
        Hand hand17Hcp2623 = new Hand(new CardPack.CardEnum[] { // 26pp, 17HCP, 2-6-2-3
                CA, CK, DA, DK, DQ, DT, D9, D8, H9, H5, SJ, S6, S5
        });
        Hand hand15Hcp2074 = new Hand(new CardPack.CardEnum[] { // 26pp, 15HCP, 2-0-7-4
                CA, CK, HA, HK, HJ, HT, H9, H8, H4, S8, S7, S6, S5
        });
        Hand hand16Hcp1138 = new Hand(new CardPack.CardEnum[] { // 27pp, 16HCP, 1-1-3-8
                CA, D4, HA, HK, HJ, SA, SK, ST, S9, S8, S7, S6, S5
        });
        Hand[] hands = {
                hand11Hcp6025, hand17Hcp2623, hand15Hcp2074, hand16Hcp1138
        };
        String suitSetterClubs = "5+ club-winners, suit-setter";
        String suitSetterDiamonds = "5+ diamond-winners, suit-setter";
        String suitSetterHearts = "5+ heart-winners";
        String suitSetterSpades = "5+ spade-winners, suit-setter";
        String[] answers = {
                suitSetterClubs, suitSetterDiamonds, suitSetterHearts, suitSetterSpades
        };
        boolean[] matches = {
                true, false, false, false, // club-winners
                false, true, false, false, // diamond-winners
                false, false, true, false, // heart-winners
                false, false, false, true  // spade-winners
        };
        QuestionAnswer qa;
        for (int a = 0; a < answers.length; a++) {
            qa = new QuestionAnswer("qa", answers[a]);
            Hand hand;
            boolean match;
            for (int h = 0; h < hands.length; h++) {
                hand = hands[h];
                match = qa.getParsedAnswer().doesMatchHand(hand);
                if (match != matches[hands.length * a + h]) {
                    System.out.println("*****hand[" + h + "] " + hand + " did" +
                            (match ? "" : " not") + " match " + qa.answer);
                }
            }
        }
    }
    private void testMultipleNotClauses() {

    }
    private void testMyPrimaryBids() {
        System.out.println("testMyPrimaryBids()");
        CardPack.CardEnum[] cards1Cb = { // 26pp
                CA, CK, CQ, CJ, C8, DK, D8, D4, HA, H6, H5, H4, S3
        };
        CardPack.CardEnum[] cards1D = { // 25pp, 5-4-2-2
                CA, CK, CQ, CJ, C8, DA, DQ, D5, D4, H5, H4, S5, S4
        };
        CardPack.CardEnum[] cards1H = { // 21pp, 4 hearts
                CJ, C8, DK, DQ, D5, D4, HA, HK, H5, H4, S5, S4, S3
        };
        CardPack.CardEnum[] cards1Hb = { // 23pp, 5 hearts
                CQ, C8, DK, DQ, D5, D4, HA, HK, H5, H4, H3, S4, S3
        };
        CardPack.CardEnum[] cards1Hc = { // 24pp, 4 hearts, 5 spades
                CK, CJ, C7, C6, HA, HJ, HT, H5, SA, SQ, S8, S3, S2
        };
        CardPack.CardEnum[] cards1S = { // 22pp, 4 spades, <4 hearts
                CJ, C8, DK, DQ, D5, D4, HA, HK, HJ, S8, S5, S4, S3
        };
        CardPack.CardEnum[] cards1Sb = { // 23pp, 6 spades, <4 hearts
                CQ, C8, DK, DQ, D5, D4, HA, SQ, S9, S8, S5, S4, S3
        };
        CardPack.CardEnum[] cards1NT = { // 21pp, 4-4-2-3
                CQ, C8, C4, C3, DK, DQ, D5, D4, HA, HQ, S9, S8, S5
        };
        CardPack.CardEnum[] cards2C = { // 20pp, 7-1-2-3
                CK, CQ, CJ, C8, C4, C3, C2, D4, HA, HT, S9, S8, S5
        };
        CardPack.CardEnum[] cards2D = { // 21pp, 2-6-2-3
                CK, CQ, DJ, D8, D7, D4, D3, D2, HA, HQ, S9, S8, S5
        };
        CardPack.CardEnum[] cards2H = { // 19pp, 1-5-6-1
                CJ, D8, D7, D6, D5, D4, HA, HK, H5, H4, H3, H2, S3
        };
        CardPack.CardEnum[] cards2S = { // 15pp, 3-2-2-6
                C9, C8, C5, DQ, D5, H9, H8, SK, SJ, S8, S5, S4, S3
        };
        CardPack.CardEnum[] cards2NT = { // 22pp, 5-6-1-1
                CA, CK, C9, C8, C5, DK, DJ, D9, D8, D6, D5, H9, S7
        };
        CardPack.CardEnum[] cards3C = { // 16pp, 7-1-2-3
                CK, C9, C8, C6, C5, C4, C2, DK, H9, H8, S5, S4, S3
        };
        CardPack.CardEnum[] cards4D = { // 17pp, 0-8-2-3
                DK, DQ, DT, D9, D8, D7, D6, D5, HJ, H8, S5, S4, S3
        };
        CardPack.CardEnum[] cards4H = { // 18pp, 3-0-8-2
                C9, C8, C5, HK, HQ, H9, H8, H7, H6, H5, H3, SQ, S3
        };
        CardPack.CardEnum[] cards5C = { // 19pp, 9-1-0-3
                CA, CK, C9, C8, C7, C5, C4, C3, C2, D8, S5, S4, S3
        };
        CardPack.CardEnum[] cardsPass = { // 19pp, 5-1-2-5
                C9, C8, C5, C3, C2, D9, HA, HK, SQ, S8, S5, S4, S3
        };
        CardPack.CardEnum[] cardsPassb = { // 19pp, 6-3-1-3
                CA, CQ, C8, C6, C4, C2, DK, DT, D5, HT, SJ, S7, S4
        };
        Hand[] myHands = {
                hand1C,
                new Hand(cards1Cb),
                new Hand(cards1D),
                new Hand(cards1H),
                new Hand(cards1Hb),
                new Hand(cards1Hc),
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
                new Hand(cardsPass),
                new Hand(cardsPassb)
        };
        String initialBlank = "                  ";
        int headerSize = initialBlank.length();
        System.out.print(initialBlank);
        for (QuestionAnswer qa: primaryBids) {
            System.out.print(" " + qa.question);
        }
        System.out.println();
        for (Hand hand : myHands) {
            String handName = hand.toString();
            System.out.print(hand);
            for (int i = handName.length(); i < headerSize; i++) {
                System.out.print(" ");
            }
            int matchCt = 0;
            for (QuestionAnswer qa : primaryBids) {
                boolean match = qa.getParsedAnswer().doesMatchHand(hand);
                if (match) ++matchCt;
                System.out.print("  " + (match ? "T" : "."));
            }
            if (matchCt != 1) System.out.print(" *** matchCt=" + matchCt);
            System.out.println();
        }
    }
    // deal random hands, and check that each one matches exactly 1 primary bid
    private void testRandomPrimaryBids() {
        System.out.println("testRandomPrimaryBids()");
        for (int i = 0; i < 50; i++) {
            cardPack.shuffle();
            cardPack.deal(true);
            Hand hand = cardPack.getHand(Player.West);
            System.out.print(hand);
            int matchCt = 0;
            for (QuestionAnswer qa : primaryBids) {
                boolean match = qa.getParsedAnswer().doesMatchHand(hand);
                if (match) ++matchCt;
            }
            if (matchCt != 1) {
                System.out.println(hand + " *** matchCt=" + matchCt);
                System.out.println(hand.cardsAsString());
                // put breakpoint at next line to debug problem
                bridgeQuiz.getPrimaryBid(hand);
            }
            System.out.println();
        }
    }
    private void test2Hands4H() {
        System.out.println("test2Hands4H()");
        CardPack.CardEnum[] cardsWest = {
                CA, CQ, C6, C5, DK, DQ, DJ, D8, D6, H6, SJ, S7, S4
        };
        CardPack.CardEnum[] cardsEast = {
                CK, C7, HA, H9, H7, H5, H4, H3, SK, SQ, ST, S6, S5
        };
        Hand handWest = new Hand(cardsWest);
        Hand handEast = new Hand(cardsEast);
        QuestionAnswer qa1 = bridgeQuiz.getNextBid(handWest, OPENING_BIDS);
        System.out.println("qa1=" + qa1);
        QuestionAnswer qa2 = bridgeQuiz.getNextBid(handEast, qa1);
        System.out.println("qa2=" + qa2);
    }
    private void test2Hands1H1S() {
        System.out.println("test2Hands1H1S()");
        CardPack.CardEnum[] cardsWest = {
                CK, C8, C5, C4, DJ, D8, D3, HA, HK, H7, H5, SK, S8
        };
        CardPack.CardEnum[] cardsEast = {
                CA, CQ, CT, C6, D7, HJ, H8, H4, SA, SQ, SJ, S7, S6
        };
        Hand handWest = new Hand(cardsWest);
        Hand handEast = new Hand(cardsEast);
        QuestionAnswer qa = OPENING_BIDS;
        boolean west = true;
        Hand hand;
        for (int i = 0; i < 4; i++) {
            hand = west ? handWest : handEast;
            qa = bridgeQuiz.getNextBid(hand, qa);
            west = !west;
            System.out.println(qa);
        }
    }
    private void testOrs() {
        System.out.println("testOrs()");
        CardPack.CardEnum[] cards6C = { // 10 HCP, 6-1-2-4
                CA, CK, CQ, CJ, CT, C9, D8, H7, H6, S5, S4, S3, S2
        };
        CardPack.CardEnum[] cards6D = { // 10 HCP, 3-6-0-4
                CA, CK, CQ, DJ, DT, D9, D8, D7, D6, S5, S4, S3, S2
        };
        CardPack.CardEnum[] cards6H = { // 10 HCP, 4-1-6-2
                CA, CK, CQ, CJ, DT, H9, H8, H7, H6, H5, H4, S3, S2
        };
        CardPack.CardEnum[] cards6S = { // 10 HCP, 4-3-0-6
                CA, CK, CQ, CJ, DT, D9, D8, S7, S6, S5, S4, S3, S2
        };
        Hand[] hands = {
                new Hand(cards6C), new Hand(cards6D), new Hand(cards6H), new Hand(cards6S)
        };
        boolean[] expectedResults = {
                true, true, false, true
        };
        QuestionAnswer qa = new QuestionAnswer("ors", "6 clubs or 6 diamonds or 6 spades");
        for (int i = 0; i < hands.length; i++) {
            boolean match = qa.getParsedAnswer().doesMatchHand(hands[i]);
            System.out.print(match);
            if (match != expectedResults[i]) System.out.print(" *** wrong!");
            System.out.println();
        }
    }
    private void testNots() {
        System.out.println("testNots()");
        CardPack.CardEnum[] cards6C = { // 10 HCP, 6-1-2-4
                CA, CK, CQ, CJ, CT, C9, D8, H7, H6, S5, S4, S3, S2
        };
        CardPack.CardEnum[] cards6D = { // 10 HCP, 3-6-0-4
                CA, CK, CQ, DJ, DT, D9, D8, D7, D6, S5, S4, S3, S2
        };
        CardPack.CardEnum[] cards6H = { // 10 HCP, 4-1-6-2
                CA, CK, CQ, CJ, DT, H9, H8, H7, H6, H5, H4, S3, S2
        };
        CardPack.CardEnum[] cards6S = { // 10 HCP, 4-3-0-6
                CA, CK, CQ, CJ, DT, D9, D8, S7, S6, S5, S4, S3, S2
        };
        Hand[] hands = {
                new Hand(cards6C), new Hand(cards6D), new Hand(cards6H), new Hand(cards6S)
        };
        boolean[] expectedResults = {
                false, false, true, false
        };
        QuestionAnswer qa = new QuestionAnswer(
                "nots", "not {10 HCP, 6 clubs}, not {10 HCP, 6 diamonds}, not {10 HCP, 6 spades}");
        for (int i = 0; i < hands.length; i++) {
            boolean match = qa.getParsedAnswer().doesMatchHand(hands[i]);
            System.out.print(match);
            if (match != expectedResults[i]) System.out.print(" *** wrong!");
            System.out.println();
        }
    }
    /*
    For dealCt deals, get handWest and handEast to do bidCt bids between them
     */
    private void testRandomNBids(int dealCt, int bidCt) {
        System.out.println("testRandomNBids()");
        for (int j = 0; j < dealCt; j++) {
            cardPack.shuffle();
            cardPack.deal(true);
            Hand handWest = cardPack.getHand(Player.West);
            Hand handEast = cardPack.getHand(Player.East);
            System.out.println("West: " + handWest);
            System.out.println("East: " + handEast);
            QuestionAnswer qa = OPENING_BIDS;
            boolean west = true;
            Hand hand;
            for (int i = 0; i < bidCt && qa != null; i++) {
                hand = west ? handWest : handEast;
                qa = bridgeQuiz.getNextBid(hand, qa);
                west = !west;
                System.out.println(qa);
            }
            System.out.println();
        }
    }
    // deal random hands
    // for each primary bid:
    //      check that randomEast matches exactly 1 second bid
    private void testAllSecondBids() {
        System.out.println("testAllSecondBids()");
        for (int i = 0; i < 50; i++) {
            cardPack.shuffle();
            cardPack.deal(true);
            Hand handEast = cardPack.getHand(Player.East);
            List<QuestionAnswer> matches = new ArrayList<>();

            for (QuestionAnswer qa1 : primaryBids) {
                matches.clear();
                List<QuestionAnswer> qa2List = bridgeQuiz.getPossibleResponses(qa1);
                for (QuestionAnswer qa2 : qa2List) {
                    boolean match = qa2.getParsedAnswer().doesMatchHand(handEast);
                    if (match) matches.add(qa2);
                }
                if (matches.size() != 1) {
                    System.out.println("primary bid: " + qa1);
                    System.out.println("East: " + handEast + " *** matchCt=" + matches.size());
                    System.out.println(handEast.cardsAsString());
                    for (QuestionAnswer qaMatch: matches) System.out.println("matched: " + qaMatch);
                    // put breakpoint at next line to debug problem
                    bridgeQuiz.getPrimaryBid(handEast);
                }
                System.out.println();
            }
        }
    }
    private void testMySecondBids() { // 2 matches for 1C; 3 for 1D
        System.out.println("testMySecondBids()");
        CardPack.CardEnum[] cards1 = { // 22pp, 11HCP, 5-6-2-0
                C9, C7, C6, C5, C2, DA, DK, DJ, DT, D9, D4, HK, H8
        };
        Hand hand1 = new Hand(cards1);
        List<QuestionAnswer> possibles =
                bridgeQuiz.getPossibleResponses(primaryBids.get(1)); // get responses for 1D
        int matchCt = 0;
        for (QuestionAnswer qa : possibles) {
            boolean match = qa.getParsedAnswer().doesMatchHand(hand1);
            if (match) {
                ++matchCt;
                System.out.println(qa);
            }
        }
        if (matchCt != 1) System.out.print(" *** matchCt=" + matchCt);
        System.out.println();
    }
    // deal random hands and check that each one matches exactly 1 primary bid
    private void testRandomSecondBids() {
        System.out.println("testRandomSecondBids()");
        for (int i = 0; i < 50; i++) {
            cardPack.shuffle();
            cardPack.deal(true);
            Hand handWest = cardPack.getHand(Player.West);
            Hand handEast = cardPack.getHand(Player.East);

            // first check West matches exactly 1 primary bid
            int matchCt = 0;
            for (QuestionAnswer qa : primaryBids) {
                boolean match = qa.getParsedAnswer().doesMatchHand(handWest);
                if (match) ++matchCt;
            }
            if (matchCt != 1) {
                System.out.println(handWest + " *** matchCt=" + matchCt);
                System.out.println(handWest.cardsAsString());
                // put breakpoint at next line to debug problem
                bridgeQuiz.getPrimaryBid(handWest);
            }
            System.out.println();

            // now check East matches exactly 1 second bid
            QuestionAnswer qa1 = bridgeQuiz.getPrimaryBid(handWest);
            List<QuestionAnswer> qa2List = bridgeQuiz.getPossibleResponses(qa1);
            matchCt = 0;
            for (QuestionAnswer qa2 : qa2List) {
                boolean match = qa2.getParsedAnswer().doesMatchHand(handEast);
                if (match) ++matchCt;
            }
            if (matchCt != 1) {
                System.out.println("West: " + handWest);
                System.out.println("East: " + handEast + " *** matchCt=" + matchCt);
                System.out.println(handWest.cardsAsString());
                // put breakpoint at next line to debug problem
                bridgeQuiz.getPrimaryBid(handEast);
            }
            System.out.println();
        }
    }

}
