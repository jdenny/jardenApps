package temp;

/**
 * Created by john.denny@gmail.com on 22/06/2018.
 */

public class HelloJohn {
    private final static String[] bidSequences = {
            "1C, 1H; 2D",
            "1D, 1S; 2S, 4H; 4NT, 5D; 5H, 6C",
            "1C, (1S) double; (pass) 2NT",
            "(1S) double, (pass) 2NT",
            "(1H) double"
    };

    public static void main(String[] args) {
        System.out.println("hello John");
        testGetBidsFromSequence();
        System.out.println("adios amigo");
    }

    private static void testGetBidsFromSequence() {
        for (String sequence: bidSequences) {
            System.out.println(sequence);
            String[] bidStrs = getBidsFromSequence(sequence);
            for (String bidStr: bidStrs) {
                System.out.println("  " + bidStr);
            }
        }
    }

    private static String[] getBidsFromSequence(String sequence) {
        return sequence.split("[ ,;]+");
    }
}
