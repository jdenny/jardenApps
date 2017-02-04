package jarden.ovenu;

/**
 * Created by john.denny@gmail.com on 21/12/2016.
 */

public class CustomerCSV {
    String title;
    String firstName;
    String lastName;
    String address1 = "";
    String address2 = "";
    String address3 = "";
    String town = "";
    String county = "Dorset";
    String postcode;
    String email;
    String phone;
    String mobile;
    String lastCleanDate;
    int lastCleanPrice;

    public static String getTitles() {
        return "Title\tFirstName\tLastName\tAddress Line 1\tAddress Line 2\t" +
                "Address Line 3\tTown/City\tCounty\tPostcode\tEmail\tPhone\t" +
                "Mobile\tLast Job date DD-MM-YY\tLast Job Value (£)";
    }
    public String toString() {
        return title + "\t" + firstName + "\t" + lastName + "\t" +
                address1 + "\t" + address2 + "\t" + address3 + "\t" + town +
                "\t" + county + "\t" + postcode + "\t" + email + "\t" + phone +
                "\t" + mobile + "\t" + lastCleanDate + "\t" + lastCleanPrice;
    }
}

