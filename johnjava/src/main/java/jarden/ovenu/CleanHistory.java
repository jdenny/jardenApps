package jarden.ovenu;

/**
 * Created by john.denny@gmail.com on 20/12/2016.
 */

public class CleanHistory {
    int id;
    int customerId;
    String cleanDate;
    int cleanPrice;
    String cleaner;
    String cleanDescription;

    public static String getTitles() {
        return "id\tcustomerId\tcleanDate\tcleanPrice\tcleaner\tcleanDescription";
    }
    public String toString() {
        return id + "\t" + customerId + "\t" + cleanDate + "\t" + cleanPrice +
                "\t" + cleaner + "\t" + cleanDescription;
    }
}
