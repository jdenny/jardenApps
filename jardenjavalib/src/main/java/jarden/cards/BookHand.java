package jarden.cards;

import static jarden.cards.CardPack.CardEnum.*;

/**
 * Created by john.denny@gmail.com on 2019-06-25.
 */
public class BookHand {
    private static final BookHand page50 = new BookHand(
            "Page50",
            new Hand(new CardPack.CardEnum[]{ // 22pp, 13-1+0+1HCP/+4, 4-5-1-3
                    CA, CQ, C6, C5, DK, DQ, DJ, D8, D6, H6, SJ, S7, S4 }),
            new Hand(new CardPack.CardEnum[]{ // 24pp, 12+0+0+1HCP/+6, 2-0-6-5
                    CK, C7, HA, H9, H7, H5, H4, H3, SK, SQ, ST, S6, S5 }),
            "1NT, 2D; 2S, 3S; 4S, Pass",
            new int[] {22, 24, 15, 17}, false);
    private static final BookHand page56 = new BookHand(
            "Page56",
            new Hand(new CardPack.CardEnum[]{ // 25pp, 15+0-0+1HCP/+0, 4-5-3-1
                    CK, CQ, CJ, C9, DA, DK, D8, D6, D5, HQ, H8, H2, S8 }),
            new Hand(new CardPack.CardEnum[]{ // 21pp, 12+0-0+0HCP/+0, 3-1-3-6
                    C7, C3, C2, D6, HA, HJ, H7, SA, SK, S7, S6, S4, S3 }),
            "1D, 1S; 2D, 3NT; Pass",
            new int[] {25, 21, 16, 12}, false);
    private static final BookHand page56B = new BookHand(
            "Page56B",
            new Hand(new CardPack.CardEnum[] { // 24pp, 15+0+0+1HCP/+2, 4-4-1-4
                    CK, CQ, CJ, C9, DA, DK, D8, D5, H8, SQ, S8, S6, S2 }),
            new Hand(new CardPack.CardEnum[]{ // 21pp, 12+0+0+0HCP/+5, 3-1-3-6
                    C7, C3, C2, D6, HA, HJ, H7, SA, SK, S7, S6, S4, S3}),
            "1D, 1S; 2S, 2NT; 4H, 4NT; 5D, 5H; 6C, 6S; Pass",
            new int[] {24, 21, 18, 17}, false);
    private static final BookHand page58 = new BookHand(
            "Page58",
            new Hand(new CardPack.CardEnum[] { // 23pp, 15+0+0+0HCP/+1, 4-3-2-4
                    CQ, C8, C7, C4, DK, DQ, DT, HA, H8, SK, SJ, S7, S3 }),
            new Hand(new CardPack.CardEnum[]{ // 8pp, 0+0+0+0HCP/+1, 2-4-3-4
                    C6, C3, D9, D8, D5, D2, HT, H7, H3, S8, S6, S5, S2 }),
            "1D, 1S; 2S, Pass",
            new int[] {23, 8, 16, 1}, false);
    private static final BookHand page61 = new BookHand(
            "Page61",
            new Hand(new CardPack.CardEnum[]{ // 15pp, 6+0+0+0HCP, 4-1-3-5
                    CA, C9, C6, C4, D7, HT, H4, H2, SQ, ST, S7, S5, S4 }),
            new Hand(new CardPack.CardEnum[] { // 26pp, 16+0+0+1HCP, 3-5-4-1
                    // hack: changed D9 to DT
                    CK, C8, C2, DA, DK, DT, D4, D3, HA, HQ, H6, H3, S6 }),
            "1C, 1S; 1NT, 2H; Pass",
            new int[] {26, 15, 17, 6}, true);
    private static final BookHand page62 = new BookHand(
            "Page62",
            new Hand(new CardPack.CardEnum[] { // 29pp, 20+0+0+1HCP/+2, 4-4-1-4
                    CK, CQ, CJ, C6, DA, DK, D5, D2, H3, SA, SK, S6, S4 }),
            new Hand(new CardPack.CardEnum[]{ // 11pp, 2+0+0+0HCP/+2, 3-1-5-4
                    CT, C7, C5, D9, H8, H7, H4, H3, H2, SQ, S8, S5, S3 }),
            "1C, 1D; 1S, 2S; 2NT, 4S; Pass",
            new int[] {29, 11, 23, 4}, false);
    private static final BookHand page62B = new BookHand(
            "Page62B",
            new Hand(new CardPack.CardEnum[] { // 29pp, 20+0+0+0HCP/+0, 2-5-4-2
                    CA, C6, DA, DK, D8, D7, D6, HK, HQ, H8, H5, SA, S6 }),
            new Hand(new CardPack.CardEnum[]{ // 11pp, 2+0+0+0HCP/+0, 5-3-1-4
                    CJ, C8, C7, C5, C3, D9, D5, D2, H9, SJ, S8, S4, S3 }),
            "1C, 1D; 1H, 1S; 1NT, Pass",
            new int[] {29, 11, 20, 2}, false);

    /*
    private void testPage63() {
        System.out.println("\ntestPage63");
        Hand handWest = new Hand(new CardPack.CardEnum[]{ // 30pp, 20+1+0+1HCP, 2-4-4-3
                CA, CK, DA, D5, D3, D2, HA, HQ, HT, H4, SK, S7, S2
        });
        Hand handEast = new Hand(new CardPack.CardEnum[]{ // 10pp, 3-1+0+0HCP, 5-2-3-3
                CJ, C9, C6, C4, C2, DQ, D4, H9, H5, H3, S8, S6, S5
        });
        String expectedFinalBid = "1C, 1D; 1NT, 2C; Pass";
        testWestEast(handWest, handEast, expectedFinalBid, new int[] {30, 10, 22, 2});
    }
    private void testPage63B() {
        System.out.println("\ntestPage63B");
        Hand handWest = new Hand(new CardPack.CardEnum[]{ // 32pp, 23+0+0+1HCP/+1, 2-5-3-3
                CA, CK, DK, DQ, DJ, D8, D5, HK, HQ, HJ, SA, S8, S6
        });
        // slight hack to make it work! C2 changed to H2
        Hand handEast = new Hand(new CardPack.CardEnum[]{ // 12pp, 3+0+0+0HCP/+2, 5-4-2-2
                CT, C8, C7, C5, C3, H2, D9, D6, D3, D2, H9, SK, S3
        });
        String expectedFinalBid = "1C, 1D; 2C, 3C; 3D, 4H; 5D, Pass";
        testWestEast(handWest, handEast, expectedFinalBid, new int[] {32, 12, 25, 5});
    }
    private void testPage67() {
        System.out.println("\ntestPage67");
        Hand handWest = new Hand(new CardPack.CardEnum[]{ // 22pp, 14+0+0+0HCP, 2-3-4-4
                // hack: change HT to H9
                CK, C7, DA, D6, D4, HK, HQ, H9, H3, SQ, S9, S4, S3
        });
        Hand handEast = new Hand(new CardPack.CardEnum[]{ // 23pp, 13+0+0+1HCP, 4-5-1-3
                CQ, CT, C6, C5, DK, DJ, DT, D5, D3, H7, SA, SK, S5
        });
        String expectedFinalBid = "1H, 2D, 3NT, Pass";
        testWestEast(handWest, handEast, expectedFinalBid, new int[] {22, 23, 14, 14});
    }
    private void testPage67B() {
        System.out.println("\ntestPage67B");
        Hand handWest = new Hand(new CardPack.CardEnum[]{ // 29pp, 22+0+0+0HCP, 4-3-3-3
                CA, CK, C8, C4, DK, DJ, D7, HK, HQ, H5, SA, SQ, S4
        });
        Hand handEast = new Hand(new CardPack.CardEnum[]{ // 14pp, 6+0+0+0HCP, 4-4-1-4
                CQ, C9, C5, C2, D8, D6, D5, D3, H7, SK, SJ, S5, S3
        });
        String expectedFinalBid = "1C, 1S; 2D, 3C; 3NT, Pass";
        testWestEast(handWest, handEast, expectedFinalBid, new int[] {29, 14, 22, 6});
    }
    private void testPage69() {
        System.out.println("\ntestPage69");
        Hand handWest = new Hand(new CardPack.CardEnum[]{ // 29pp, 21+0+0+0HCP/+1, 4-2-3-4
                CA, CK, C4, C2, DK, D7, HK, HQ, H6, SA, SQ, S8, S5
        });
        // hack: changed east's D6 to DT
        Hand handEast = new Hand(new CardPack.CardEnum[]{ // 17pp, 10-1-1+1HCP/+1, 4-4-2-3
                CQ, CJ, C8, C6, DQ, DJ, DT, D5, HJ, H3, SK, S9, S4
        });
        String expectedFinalBid = "1C, 2D; 3C, 4C; 4H, 4S; 4NT, Pass";
        testWestEast(handWest, handEast, expectedFinalBid, new int[] {29, 17, 22, 10});
    }
    private void testPage70() {
        System.out.println("\ntestPage70");
        // west and east swapped over
        Hand handWest = new Hand(new CardPack.CardEnum[]{ // 20pp, 12+0+0+0HCP/+1, 3-4-2-4
                CA, C5, C4, DK, D8, D7, D2, H9, H6, SK, SQ, S8, S5
        });
        Hand handEast = new Hand(new CardPack.CardEnum[]{ // 22pp, 14-1+0+1HCP/+1, 4-4-3-2
                CK, CQ, CJ, C7, DA, DQ, D5, D3, HQ, H8, H4, S9, S2
        });
        String expectedFinalBid = "1S, 2D; 2NT, 3C; 3D, 4D; Pass";
        testWestEast(handWest, handEast, expectedFinalBid, new int[] {20, 22, 13, 15});
    }
    private void testPage71() {
        System.out.println("\ntestPage71");
        Hand handWest = new Hand(new CardPack.CardEnum[]{ // 20pp, 13+0-1+0HCP/+1, 5-2-3-3
                CA, CK, C8, C4, C3, DQ, D8, HK, H8, H5, SJ, ST, S4
        });
        Hand handEast = new Hand(new CardPack.CardEnum[]{ // 19pp, 11+0-1+0HCP/+7, 6-4-2-1
                CJ, CT, C9, C7, C5, C2, DK, DT, D7, D3, HQ, HJ, SA
        });
        String expectedFinalBid = "1NT, 2D; 2S, 3C; 4D, 5C; Pass";
        testWestEast(handWest, handEast, expectedFinalBid, new int[] {20, 20, 13, 17});
    }
    private void testPage72() {
        System.out.println("\ntestPage72");
        Hand handWest = new Hand(new CardPack.CardEnum[]{ // 22pp, 13+0+0+0HCP/+2, 3-5-4-1
                CQ, C7, C6, DK, DJ, D8, D7, D4, HA, HK, H9, H3, S7
        });
        Hand handEast = new Hand(new CardPack.CardEnum[]{ // 22pp, 15+0-1+0HCP/+1, 3-4-2-4
                CA, C8, C5, DA, DQ, D9, D2, HJ, H8, SA, ST, S6, S2
        });
        String expectedFinalBid = "1H, 1S; 1NT, 2D; 3D, 4S; 5D, 6D; Pass";
        testWestEast(handWest, handEast, expectedFinalBid, new int[] {22, 22, 15, 15});
    }
    private void testPage72B() {
        System.out.println("\ntestPage72B");
        Hand handWest = new Hand(new CardPack.CardEnum[]{ // 22pp, 14+0+0+0HCP/+1, 4-3-4-2
                CK, C8, C5, C4, DJ, D8, D3, HA, HK, H7, H5, SK, S8
        });
        Hand handEast = new Hand(new CardPack.CardEnum[]{ // 25pp, 14+0+0+2HCP/+2, 4-1-3-5
                CA, CQ, CT, C6, D7, HJ, H8, H4, SA, SQ, SJ, S7, S6
        });
        String expectedFinalBid = "1H, 1S; 1NT, 2D; 2NT, 3C; 4D, 4H; 5C, 6C; Pass";
        testWestEast(handWest, handEast, expectedFinalBid, new int[] {22, 25, 15, 18});
    }
    private void testPage77() {
        System.out.println("\ntestPage77");
        Hand handWest = new Hand(new CardPack.CardEnum[]{ // 22pp, 13+0+0+1HCP/+1, 3-4-4-2
                CA, CQ, C4, DK, DJ, D6, D3, HQ, HJ, HT, H5, S8, S5
        });
        Hand handEast = new Hand(new CardPack.CardEnum[]{ // 22pp, 13+0+0+1HCP/+1, 3-5-3-2
                C7, C5, C2, DA, DQ, DT, D5, D2, HA, HK, H4, S7, S3
        });
        String expectedFinalBid = "1H, 2D; 3D, 3H; 4H, Pass";
        testWestEast(handWest, handEast, expectedFinalBid, new int[] {22, 22, 15, 15});
    }
    */

    private static final BookHand[] bookHands = {
            page50, page56, page56B, page58, page61, page62, page62B
    };

    public String name;
    public Hand handWest;
    public Hand handEast;
    public String expectedBidSequence;
    public int[] pointCounts; // westPP, eastPP, westFinalHCP, eastFinalHCP
    public boolean dealerEast = false;

    public BookHand(String name, Hand handWest, Hand handEast) {
        this.name = name;
        this.handWest = handWest;
        this.handEast = handEast;
    }
    public BookHand(String name, Hand handWest, Hand handEast, String expectedBidSequence,
                    int[] pointCounts, boolean dealerEast) {
        this(name, handWest, handEast);
        this.expectedBidSequence = expectedBidSequence;
        this.pointCounts = pointCounts;
        this.dealerEast = dealerEast;
    }
    public static BookHand[] getBookHands() {
        return bookHands;
    }
}
