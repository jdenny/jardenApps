package com.jarden;

/**
 * Created by john.denny@gmail.com on 19/12/2016.
 */

public class Customer {
    int id;
    String fullName;
    String address1;
    String address2;
    String town;
    String postcode;
    String phone;
    String mobile;
    String email;
    String ovenType;
    String howFound;
    String notes;
    String nextAction;
    // Timestamp nextActionDateTime;
    String nextActionDateTime;
    int cleanIntervalMonths;

    public static String getTitles() {
        return "fullName\taddress1\ttown\tpostcode\tphone\tmobile" +
               "\temail\tovenType\thowFound\tnotes\tnextAction" +
               "\tnextActionDateTime\tcleanIntervalMonths";
    }
    public String toString() {
        return fullName + "\t" + address1 + "\t" + town +
                "\t" + postcode + "\t" + phone + "\t" + mobile +
                "\t" + email + "\t" + ovenType + "\t" + howFound +
                "\t" + notes + "\t" + nextAction +
                "\t" + nextActionDateTime + "\t" + cleanIntervalMonths;
    }
}

