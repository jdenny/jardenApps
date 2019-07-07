package jarden.cards;

import static jarden.cards.CardPack.CardEnum.*;

/**
 * Created by john.denny@gmail.com on 2019-06-25.
 */
public class BookHand {
    private static final BookHand page50 = new BookHand(
            "page50",
            new Hand(new CardPack.CardEnum[] { // 22pp, 13-1+0+1HCP/+2, 4-5-1-3
                    CA, CQ, C6, C5, DK, DQ, DJ, D8, D6, H6, SJ, S7, S4 }),
            new Hand(new CardPack.CardEnum[] { // 24pp, 12+0+0+1HCP/+4, 2-0-6-5
                    CK, C7, HA, H9, H7, H5, H4, H3, SK, SQ, ST, S6, S5 }),
            "1NT, 2D; 2S, 3S; 4S, Pass",
            "1H, 2D; 2H, 2S; 2NT, 4C; 4S, Pass",
            new int[] {22, 24, 15, 17}, false);
    private static final BookHand page56 = new BookHand(
            "page56",
            new Hand(new CardPack.CardEnum[] { // 25pp, 15+0-0+1HCP/+0, 4-5-3-1
                    CK, CQ, CJ, C9, DA, DK, D8, D6, D5, HQ, H8, H2, S8 }),
            new Hand(new CardPack.CardEnum[] { // 21pp, 12+0-0+0HCP/+0, 3-1-3-6
                    C7, C3, C2, D6, HA, HJ, H7, SA, SK, S7, S6, S4, S3 }),
            "1D, 1S; 2D, 3NT; Pass",
            "1S, 2D; 2H, 3NT; Pass",
            new int[] {25, 21, 16, 12}, false);
    public static final BookHand page56B = new BookHand(
            "page56B",
            new Hand(new CardPack.CardEnum[] { // 24pp, 15+0+0+1HCP/+2, 4-4-1-4
                    // hack: changed D6 to S6
                    CK, CQ, CJ, C9, DA, DK, D8, D5, H8, SQ, S8, S6, S2 }),
            new Hand(new CardPack.CardEnum[] { // 22pp, 13+0+0+0HCP/+5, 3-1-3-6
                    // hack: changed HJ to HQ
                    C7, C3, C2, D6, HA, HQ, H7, SA, SK, S7, S6, S4, S3 }),
            // book version: "1D, 1S; 2S, 2NT; 4H, 4NT; 5D, 5H; 6C, 6S; Pass";
            "1D, 1S; 2S, 2NT; 3C, 4D; 4S, 4NT; 6C, 6S; Pass",
            "1S, 2NT; 4C, 4D; 4H, 6S; Pass",
            new int[] {24, 22, 18, 18}, false);
    private static final BookHand page58 = new BookHand(
            "page58",
            new Hand(new CardPack.CardEnum[] { // 23pp, 15+0+0+0HCP/+1, 4-3-2-4
                    CQ, C8, C7, C4, DK, DQ, DT, HA, H8, SK, SJ, S7, S3 }),
            new Hand(new CardPack.CardEnum[] { // 8pp, 0+0+0+0HCP/+1, 2-4-3-4
                    C6, C3, D9, D8, D5, D2, HT, H7, H3, S8, S6, S5, S2 }),
            "1D, 1S; 2S, Pass",
            "1D, 1S; 2S, Pass",
            new int[] {23, 8, 16, 1}, false);
    private static final BookHand page61 = new BookHand(
            "page61",
            new Hand(new CardPack.CardEnum[] { // 15pp, 6+0+0+0HCP, 4-1-3-5
                    CA, C9, C6, C4, D7, HT, H4, H2, SQ, ST, S7, S5, S4 }),
            new Hand(new CardPack.CardEnum[] { // 26pp, 16+0+0+1HCP, 3-5-4-1
                    // hack: changed D9 to DT
                    CK, C8, C2, DA, DK, DT, D4, D3, HA, HQ, H6, H3, S6 }),
            "1C, 1S; 1NT, 2H; Pass",
            "1C, 1S; 1NT, 2H; Pass",
            new int[] {15, 26, 6, 17}, true);
    public static final BookHand page62 = new BookHand(
            "page62",
            new Hand(new CardPack.CardEnum[] { // 29pp, 20+0+0+1HCP/+2, 4-4-1-4
                    CK, CQ, CJ, C6, DA, DK, D5, D2, H3, SA, SK, S6, S4 }),
            new Hand(new CardPack.CardEnum[] { // 11pp, 2+0+0+0HCP/+2, 3-1-5-4
                    CT, C7, C5, D9, H8, H7, H4, H3, H2, SQ, S8, S5, S3 }),
            "1C, 1D; 1S, 2S; 2NT, 4S; Pass",
            "1C, 1D; 1S, 2S; 2NT, 4S; Pass",
            new int[] {29, 11, 23, 4}, false);
    private static final BookHand page62B = new BookHand(
            "page62B",
            new Hand(new CardPack.CardEnum[] { // 29pp, 20+0+0+0HCP/+0, 2-5-4-2
                    CA, C6, DA, DK, D8, D7, D6, HK, HQ, H8, H5, SA, S6 }),
            new Hand(new CardPack.CardEnum[] { // 11pp, 2+0+0+0HCP/+0, 5-3-1-4
                    CJ, C8, C7, C5, C3, D9, D5, D2, H9, SJ, S8, S4, S3 }),
            "1C, 1D; 1H, 1S; 1NT, Pass",
            "1C, 1D; 1H, 1S; 1NT, Pass",
            new int[] {29, 11, 20, 2}, false);
    private static final BookHand page63 = new BookHand(
            "page63",
            new Hand(new CardPack.CardEnum[] { // 30pp, 20+1+0+1HCP, 2-4-4-3
                    CA, CK, DA, D5, D3, D2, HA, HQ, HT, H4, SK, S7, S2 }),
            new Hand(new CardPack.CardEnum[] { // 10pp, 3-1+0+0HCP, 5-2-3-3
                    CJ, C9, C6, C4, C2, DQ, D4, H9, H5, H3, S8, S6, S5 }),
            // book version: "1C, 1D; 1NT, 2C; Pass",
            "1C, 1D; 1H, 1NT; Pass",
            "1C, 1D; 1H, 1NT; Pass",
            new int[] {30, 10, 22, 2}, false);
    private static final BookHand page63B = new BookHand(
            "page63B",
            new Hand(new CardPack.CardEnum[] { // 32pp, 23+0+0+1HCP/+1, 2-5-3-3
                    CA, CK, DK, DQ, DJ, D8, D5, HK, HQ, HJ, SA, S8, S6 }),
            new Hand(new CardPack.CardEnum[] { // 12pp, 3+0+0+0HCP/+3, 6-4-1-2
                    CT, C8, C7, C5, C3, C2, D9, D6, D3, D2, H9, SK, S3 }),
            "1C, 1D; 2C, 3C; 3D, 4H; 5D, Pass",
            "1C, 1D; 2C, 3C; 3D, 4H; 5D, Pass",
            new int[] {32, 13, 25, 6}, false);
    private static final BookHand page67 = new BookHand(
            "page67",
            new Hand(new CardPack.CardEnum[] { // 22pp, 14+0+0+0HCP, 2-3-4-4
                    // hack: change HT to H9
                    CK, C7, DA, D6, D4, HK, HQ, H9, H3, SQ, S9, S4, S3 }),
            new Hand(new CardPack.CardEnum[] { // 23pp, 13+0+0+1HCP, 4-5-1-3
                    CQ, CT, C6, C5, DK, DJ, DT, D5, D3, H7, SA, SK, S5 }),
            // book sequence: 1H, 2D; 3NT, Pass
            "1H, 2D; 2NT, 3NT; Pass",
            "1D, 1H; 2D, 3NT; Pass",
            new int[] {22, 23, 14, 14}, false);
    private static final BookHand page67B = new BookHand(
            "page67B",
            new Hand(new CardPack.CardEnum[] { // 29pp, 22+0+0+0HCP, 4-3-3-3
                    CA, CK, C8, C4, DK, DJ, D7, HK, HQ, H5, SA, SQ, S4 }),
            new Hand(new CardPack.CardEnum[] { // 14pp, 6+0+0+0HCP, 4-4-1-4
                    CQ, C9, C5, C2, D8, D6, D5, D3, H7, SK, SJ, S5, S3 }),
            "1C, 1S; 2D, 3C; 3NT, Pass",
            "1C, 1S; 2D, 3C; 3NT, Pass",
            new int[] {29, 14, 22, 6}, false);
    private static final BookHand page69 = new BookHand(
            "page69",
            new Hand(new CardPack.CardEnum[] { // 29pp, 21+0+0+0HCP/+1, 4-2-3-4
                    CA, CK, C4, C2, DK, D7, HK, HQ, H6, SA, SQ, S8, S5 }),
            new Hand(new CardPack.CardEnum[] { // 17pp, 10-1-1+1HCP/+1, 4-4-2-3
                    // hack: changed east's D6 to DT
                    CQ, CJ, C8, C6, DQ, DJ, DT, D5, HJ, H3, SK, S9, S4 }),
            "1C, 2D; 3C, 4C; 4H, 4S; 4NT, Pass",
            "1C, 2D; 3C, 4C; 4H, 4S; 4NT, Pass",
            new int[] {29, 17, 22, 10}, false);
    private static final BookHand page70 = new BookHand(
            "page70",
            new Hand(new CardPack.CardEnum[] { // 22pp, 14-1+0+1HCP/+1, 4-4-3-2
                    CK, CQ, CJ, C7, DA, DQ, D5, D3, HQ, H8, H4, S9, S2 }),
            new Hand(new CardPack.CardEnum[] { // 20pp, 12+0+0+0HCP/+1, 3-4-2-4
                    CA, C5, C4, DK, D8, D7, D2, H9, H6, SK, SQ, S8, S5 }),
            "1S, 2D; 2NT, 3C; 3D, 4D; Pass",
            "1NT, 2C; 2H, 3D; 4D, Pass",
            new int[] {22, 20, 15, 13}, true);
    private static final BookHand page71 = new BookHand(
            "page71",
            new Hand(new CardPack.CardEnum[] { // 22pp, 15+0-1+0HCP/+1, 5-2-3-3
                    // hack: replace SJ with SQ, C8 with CT
                    CA, CK, CT, C4, C3, DQ, D8, HK, H8, H5, SQ, ST, S4 }),
            new Hand(new CardPack.CardEnum[] { // 21pp, 12+0-1+0HCP/+6, 6-4-2-1
                    // hack: replace CJ with CQ, CT to C7
                    CQ, C9, C8, C7, C5, C2, DK, DT, D7, D3, HQ, HJ, SA }),
            // book version: "1NT, 2D; 2H, 3C; 4D, 5C; Pass",
            "1NT, 2C; 2H, 3C; 4D, 5C; Pass",
            "2C, 2NT; 3D, 4D; 5C, Pass",
            new int[] {22, 21, 15, 17}, false);
    private static final BookHand page72 = new BookHand(
            "page72",
            new Hand(new CardPack.CardEnum[] { // 22pp, 12+0+0+0HCP/+6, 3-6-4-0
                    // hack: changed S7 to D2; DJ to DT
                    CQ, C7, C6, DK, DT, D8, D7, D4, D2, HA, HK, H9, H3 }),
            new Hand(new CardPack.CardEnum[] { // 24pp, 17+0-1+0HCP/+1, 3-4-2-4
                    // hack: ST changed to SQ
                    CA, C8, C5, DA, DQ, D9, D2, HJ, H8, SA, SQ, S6, S2 }),
            // book version: "1H, 1S; 1NT, 2D; 3D, 4S; 5D, 6D; Pass",
            "1H, 1S; 3D, 4H; 4S, 4NT; 6D, Pass",
            "1D, 1H; 1S, 2D; 2NT, 3D; 4D, 4S; 4NT, 6D; Pass",
            new int[] {22, 24, 18, 17}, false);
    private static final BookHand page72B = new BookHand(
            "page72B",
            new Hand(new CardPack.CardEnum[] { // 22pp, 14+0+0+0HCP/+1, 4-3-4-2
                    CK, C8, C5, C4, DJ, D8, D3, HA, HK, H7, H5, SK, S8 }),
            new Hand(new CardPack.CardEnum[] { // 25pp, 14+0+0+2HCP/+2, 4-1-3-5
                    CA, CQ, CT, C6, D7, HJ, H8, H4, SA, SQ, SJ, S7, S6 }),
            // book version: "1H, 1S; 1NT, 2D; 2NT, 3C; 4D, 4H; 5C, 6C; Pass",
            "1H, 1S; 1NT, 2D; 3C, 4D; 4H, 5C; 6C, Pass",
            "1C, 1H; 1S, 2D; 2H, 3C; 4C, 4D; 5C, Pass",
            new int[] {22, 25, 15, 18}, false);
    private static final BookHand page77 = new BookHand(
            "page77",
            new Hand(new CardPack.CardEnum[] { // 22pp, 13+0+0+1HCP/+1, 3-4-4-2
                    CA, CQ, C4, DK, DJ, D6, D3, HQ, HJ, HT, H5, S8, S5 }),
            new Hand(new CardPack.CardEnum[] { // 22pp, 13+0+0+1HCP/+1, 3-5-3-2
                    C7, C5, C2, DA, DQ, DT, D5, D2, HA, HK, H4, S7, S3 }),
            // book version: "1H, 2D; 2NT, 3H; 4H, Pass",
            "1H, 2D; 3D, 3H; 4H, Pass",
            "1NT, 2D; 2H, 3D; 4D, Pass",
            new int[] {22, 22, 15, 15}, false);
    private static final BookHand page82 = new BookHand(
            "page82",
            new Hand(new CardPack.CardEnum[] { // 22pp, 13+0+0+0HCP/+5, 6-3-1-3
                    CA, CJ, C8, C6, C5, C3, DA, D7, D4, H6, SK, SJ, S5 }),
            new Hand(new CardPack.CardEnum[] { // 17pp, 11-1-1+0HCP/+1, 3-4-2-4
                    CK, C9, C7, DK, DJ, D5, D2, HQ, H4, SQ, S8, S6, S4 }),
            // book version: "2C, 2NT; 3D, 3S; 4C, Pass",
            "2C, 3C; 3D, 3S; 4C, Pass",
            "2C, 3C; 3D, 3S; 4C, Pass",
            new int[] {22, 17, 18, 10}, false);
    private static final BookHand page82B = new BookHand(
            "page82B",
            new Hand(new CardPack.CardEnum[] { // 21pp, 12+0+0+0HCP/+5, 3-6-3-1
                    CK, CQ, C7, DA, DK, D7, D5, D4, D2, H8, H4, H3, S7 }),
            new Hand(new CardPack.CardEnum[] { // 28pp, 19+0+0+0HCP/+2, 1-3-3-6
                    // hack: replace C9 with CA and H7 with HJ
                    CA, DQ, DJ, D8, HA, HJ, H5, SA, SK, S9, S7, S6, S4 }),
            "2D, 2S; 3D, 4S; 5D, 6D; Pass",
            "",
            new int[] {21, 28, 17, 21}, false);
    private static final BookHand page84 = new BookHand(
            "page84",
            new Hand(new CardPack.CardEnum[] { // 20pp, 12+0-1+0HCP/+2, 6-2-2-3
                    // hack: replace H3 with HJ
                    CK, CQ, C8, C7, C5, C4, DA, D6, HQ, HJ, S9, S8, S4 }),
            new Hand(new CardPack.CardEnum[] { // 19pp, 11+0+0+0HCP/+1, 2-3-3-5
                    // hack: replace ST with S7
                    C9, C2, DK, D8, D7, HK, H5, H4, SA, SJ, S7, S5, S2 }),
            "2C, 2S; 3S, Pass",
            "2C, 2S; 3S, Pass",
            new int[] {20, 19, 13, 12}, false);
    private static final BookHand page113 = new BookHand(
            "page113",
            new Hand(new CardPack.CardEnum[] { // 22pp, 15+0-1+0HCP/+0, 3-5-2-3
                    // hack: replace CT with CK
                    CK, C7, C5, DK, DJ, D9, D7, D4, HJ, H3, SA, SK, S4 }),
            new Hand(new CardPack.CardEnum[] { // 18pp, 9+0+0+0HCP/+0, 4-5-3-1
                    // hack: CK with CT
                    CT, C8, C6, C4, DA, DT, D8, D3, D2, HK, HQ, H4, S5 }),
            "1NT, 2NT; 3D, 3H; 3NT, Pass",
            "1NT, 2NT; 3D, 3H; 3NT, Pass",
            new int[] {22, 18, 14, 9}, false);
    private static final BookHand page124 = new BookHand(
            "page124",
            new Hand(new CardPack.CardEnum[] { // 22pp, 12+0+0+1HCP/+0, 1-4-5-3
                    C7, DQ, DT, D9, D5, HK, HQ, HT, H7, H4, SK, SQ, S4 }),
            new Hand(new CardPack.CardEnum[] { // 22pp, 13+0+0+1HCP/+0, 4-3-2-4
                    CK, CQ, CT, C6, DK, D7, D6, HA, H3, SJ, S7, S6, S3 }),
            // book version: "1H, 1S; 2S, 2NT; 3D, 3NT; Pass",
            "1H, 1S; 2D, 3NT; Pass",
            "",
            new int[] {22, 22, 13, 14}, false);
    private static final BookHand page125 = new BookHand(
            "page125",
            new Hand(new CardPack.CardEnum[] { // 21pp, 11+0+0+1HCP/+4, 2-6-3-2
                    // hack: replace DJ with D6
                    CA, C4, DK, DT, D9, D7, D6, D4, HA, H5, H3, S9, S5 }),
            new Hand(new CardPack.CardEnum[] { // 18pp, 11+0-1+0HCP/+1, 4-4-2-3
                    CK, C8, C7, C5, DA, DQ, D8, D2, HQ, H6, S8, S4, S2 }),
            "2D, 2NT; 3H, 4D; Pass",
            "2D, 2NT; 3H, 4D; Pass",
            new int[] {21, 18, 16, 11}, false);
    private static final BookHand page137 = new BookHand(
            "page137",
            new Hand(new CardPack.CardEnum[] { // 20pp, 9+0+0+1HCP/+3, 2-7-1-3
                    C8, C7, DA, DQ, DJ, D8, D5, D3, D2, H4, SQ, S7, S3 }),
            new Hand(new CardPack.CardEnum[] { // 31pp, 21+0+0+1HCP/+5, 3-1-3-6
                    CA, CK, CQ, D6, HA, H6, H2, SA, SK, SJ, ST, S6, S2 }),
            // book version: "3D, 3S; 4H, 4NT; 5D, 5H; 5NT, 7S; Pass",
            "2D, 2S; 3S, 4D; 4S, 4NT; 5NT, 7S; Pass",
            "",
            new int[] {20, 31, 13, 27}, false);

    private static final BookHand[] bookHands = {
            page50, page56, page56B, page58, page61, page62, page62B, page63,
            page63B, page67, page67B, page69, page70, page71, page72, page72B,
            page77, page82, page82B, page84, page113, page124, page125, page137
    };

    public String name;
    public Hand handWest;
    public Hand handEast;
    public String expectedBidSequence;
    public String expectedReverseSequence;
    public int[] pointCounts; // westPP, eastPP, westFinalHCP, eastFinalHCP
    public boolean dealerEast = false;

    public BookHand(String name, Hand handWest, Hand handEast) {
        this.name = name;
        this.handWest = handWest;
        this.handEast = handEast;
    }
    public BookHand(String name, Hand handWest, Hand handEast, String expectedBidSequence,
                    String expectedReverseSequence,
                    int[] pointCounts, boolean dealerEast) {
        this(name, handWest, handEast);
        this.expectedBidSequence = expectedBidSequence;
        this.expectedReverseSequence = expectedReverseSequence;
        this.pointCounts = pointCounts;
        this.dealerEast = dealerEast;
    }
    public static BookHand[] getBookHands() {
        return bookHands;
    }
}
