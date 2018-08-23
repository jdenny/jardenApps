package temp;

import java.io.IOException;

/**
 * Created by john.denny@gmail.com on 22/06/2018.
 */

public class HelloJohn {
    public static void main(String[] args) throws IOException {
        System.out.println("hello John");
        new HelloJohn();
    }
    public HelloJohn() {
        String question = "1D, 1S; 2S, 4H; 4NT, 5D; 5H, 6C";
        String backBid = question;
        while ((backBid = back(backBid)) != null) {
            System.out.println(backBid);
        }
        System.out.println("adios amigo");
    }
    private String back(String bid) {
        int lastComma = bid.lastIndexOf(',');
        int lastColon = bid.lastIndexOf(';');
        int lastSeparator = (lastComma > lastColon) ? lastComma : lastColon;
        if (lastSeparator < 0) return null;
        else return bid.substring(0, lastSeparator);
    }
}
