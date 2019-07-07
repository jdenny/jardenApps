package temp;

/**
 * Created by john.denny@gmail.com on 22/06/2018.
 */

public class HelloJohn {
    private final static String[] questions = {
            "1C, 1H; 2D",
            "1D, 1S; 2S, 4H; 4NT, 5D; 5H, 6C",
            "1C, (1S) double; (pass) 2NT",
            "(1S) double, (pass) 2NT",
            "(1H) double"
    };

    public static void main(String[] args) {
        System.out.println("hello John");
        // testGetBidsFromSequence();
        version2();
        if (true) return;
        System.out.println("version 1");
        boolean bookHandWest = true;
        boolean westDeal = true;
        boolean westDealHand1 = true;
        boolean part2 = false;
        int bookHandsIndex = -1;
        int handCt = 5;
        for (int i = 0; i < (handCt * 4); i++) {
            if (++bookHandsIndex >= handCt) {
                bookHandsIndex = 0;
                bookHandWest = !bookHandWest;
                if (part2) westDealHand1 = !westDealHand1;
                part2 = !part2;
                westDeal = westDealHand1;
            }
            System.out.println((bookHandsIndex + 1) +
                    " " + (bookHandWest ? "W" : "E") +
                    " " + (westDeal ? "W" : "E"));
            westDeal = !westDeal;
        }
        System.out.println("adios amigo");
    }
    private static void version2() {
        System.out.println("version 2");
        int bookHandsIndex = -1;
        int handCt = 4;
        boolean bookHandWest = true;
        boolean westDeal = true;
        // lap 0 is W W
        //  (I am West for this lap; dealer alternates, but starts with West)
        // lap 1 is E W
        // lap 2 is W E
        // lap 3 is E E
        int lap = 0;

        for (int i = 0; i < (handCt * 8); i++) {
            if (++bookHandsIndex >= handCt) {
                bookHandsIndex = 0;
                if (++lap >= 4) lap = 0;
                bookHandWest = (lap % 2 == 0);
                westDeal = (lap < 2);
            }
            System.out.println((bookHandsIndex + 1) +
                    " " + (bookHandWest ? "W" : "E") +
                    " " + (westDeal ? "W" : "E"));
            westDeal = !westDeal;
        }

    }

    private static void testGetBidsFromSequence() {
        for (String question: questions) {
            System.out.println(question);
            getBidsFromSequence(question, true);
        }
        for (String question: questions) {
            System.out.println(question);
            getBidsFromSequence(question, false);
        }
    }

    private static void getBidsFromSequence(String question, boolean westDeal) {
        String[] bids = question.split("[ ,;]+");
        boolean theyBid = question.contains("(");
        boolean theyBidFirst = bids[0].charAt(0) == '(';
        int j = theyBidFirst ? (westDeal ? 3: 1) : (westDeal ? 0: 2);
        System.out.println("West\tNorth\tEast\tSouth");
        for (int i = 0; i < j; i++) {
            System.out.print("\t");
        }
        for (String bid: bids) {
            printBid(j++, bid);
            if (!theyBid) printBid(j++, "pass");
        }
        printBid(j, "?");
        System.out.println();
    }
    private static void printBid(int j, String bid) {
        String formatStr = (j + 1) % 4 == 0 ? "\n" : "\t";
        System.out.print(bid + formatStr);
    }

}
