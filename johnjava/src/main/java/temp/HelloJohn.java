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
        int number = 175;
        byte bye = (byte) number;
        int number2 = bye;
        int number3 = Byte.toUnsignedInt(bye);
        System.out.println("number2=" + number2);
        System.out.println("number3=" + number3);
        System.out.println("adios amigo");
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
