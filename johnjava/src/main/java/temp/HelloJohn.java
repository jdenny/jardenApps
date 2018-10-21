package temp;

import java.util.ArrayList;
import java.util.List;

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
        testGetBidItems();
        testGetBackBid();
        System.out.println("adios amigo");
    }
    private static void testGetBackBid() {
        String backBid;
        for (String sequence: bidSequences) {
            System.out.println(sequence + "; backBid=" +
                    getBackBid(sequence));
        }
    }
    private static String getBackBid(String bidSequence) {
        //!! int index = bidSequence.lastIndexOf('(');
        //!! if (index == -1) {
            int lastComma = bidSequence.lastIndexOf(',');
            int lastColon = bidSequence.lastIndexOf(';');
            int lastSeparator = (lastComma > lastColon) ? lastComma : lastColon;
            if (lastSeparator == -1) return null;
            return bidSequence.substring(0, lastSeparator);
        /*!!
        } else {
            return bidSequence.substring(0, index - 2);
        }
        */
    }
    private static void testGetBidItems() {
        List<String> bidList;
        for (String sequence: bidSequences) {
            System.out.println("****" + sequence + "****");
            bidList = getBidItems(sequence);
            for (String bid: bidList) {
                System.out.println('\t' + bid);
            }
        }
    }
    private static List<String> getBidItems(String bidSequence) {
        List<String> bidItems = new ArrayList();
        int index = 0;
        String nextBid;
        /*!!
        index = bidSequence.indexOf('(');
        boolean compete = (index != -1);
        if (compete) {
            if (index > 0) {
                bidItems.add(bidSequence.substring(0, index - 2));
            }
        } else {
            index = 0;
        }
        */
        do {
            /*!!
            if (compete) {
                index = bidSequence.indexOf(')', index) + 3;
            }
            */
            index = findNextSeparator(bidSequence, index);
            if (index == -1) {
                nextBid = bidSequence;
            } else {
                nextBid = bidSequence.substring(0, index);
                index += 2;
            }
            bidItems.add(nextBid);
        } while (index != -1);
        return bidItems;
    }
    private static int findNextSeparator(String sequence, int start) {
        int commaI = sequence.indexOf(',', start);
        int colonI = sequence.indexOf(';', start);
        if (commaI == -1) {
            if (colonI == -1) return -1;
            return colonI;
        } else {
            if (colonI == -1) return commaI;
            return (commaI > colonI) ? colonI : commaI;
        }
    }
}
