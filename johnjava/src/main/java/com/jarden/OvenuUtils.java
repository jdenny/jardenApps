package com.jarden;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/*
SQLite3 datatypes are dynamic (like python); in particular the field
nextActionDateTime TIMESTAMP, can accept strings in the correct format
see https://www.sqlite.org/datatype3.html
for general help with SQLite,
see https://www.tutorialspoint.com/sqlite/sqlite_java.htm
  */
public class OvenuUtils {
    private static final String dbLocation =
            "/Users/john/OvenuCRM/ovenuLite.db";
    private static final String[] titles = {
            "Mr", "Mrs", "Miss", "Dr"
    };
    private static final String MRNMRS = "Mr & Mrs";
    private static final String[] fieldNames = {
            "id", "fullName", "address1", "address2", "town",
            "postcode", "phone", "mobile", "email", "ovenType",
            "howFound", "notes", "nextAction", "nextActionDate",
            "nextActionTime", "cleanIntervalMonths"
    };
    private static final String[] histFieldNames = {
            "id", "cleanDate", "cleanPrice", "cleaner",
            "cleanDescription", "customerId"
    };
    private static final String customerTxtFilesDir = "/Users/john/downloads/ovenUBackup/";
    private static final String customerTxtFileName = customerTxtFilesDir + "customer.txt";
    private static final String historyTxtFileName = customerTxtFilesDir + "cleanHistory.txt";
    private static final String insertCustomer =
            "insert into Customer (id, fullName, address1, address2, town, " +
            "postcode, phone, mobile, email, ovenType, howFound, notes, " +
            "nextAction, nextActionDateTime, cleanIntervalMonths) " +
            "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String insertCleanHistory =
            "insert into CleanHistory (id, customerId, cleanDescription, cleanDate, " +
                    "cleanPrice, cleaner) " +
                    "values(?, ?, ?, ?, ?, ?)";
    private static final String[] CSVTitles = {
            "Title", "FirstName", "LastName", "Address Line 1", "Address Line 2",
            "Address Line 3", "Town/City", "County", "Postcode", "Email", "Phone",
            "Mobile", "Last Job date DD-MM-YY", "Last Job Value"
    };
    private static final String selectForCSV =
            "select fullName, address1, address2, town, postcode, email, phone, " +
            "mobile, cleanDate, cleanPrice " +
            "from Customer, CleanHistory where " +
            "Customer.id < 10 " + // comment out this line when it's working
            "and customerId = Customer.id and cleanDate in " +
            "(select MAX(cleanDate) from CleanHistory " +
            "where customerId = Customer.id)";
    private static final String[] testNames = {
            "Mrs Abbott", "Stavy Antoniou", "Mrs Ingrid Aird",
            "Mr Andrews", "Alan ", "Kay Allendale Comm Centre",
            "Mr & Mrs Patterson", "Mrs Jill Webber", "Bill",
            "Mr & Mrs John Colls"
    };

    public static void main(String[] args) throws IOException, SQLException {
        System.out.println("Hello John");
        // getMetaData();
        // readCustomerFile();
        // loadDB();
        // readCustomerTable();
        /*
        List<CleanHistory> historyList = readHistoryFile();
        for (int i = 0; i < 10; i++) {
            System.out.println(historyList.get(i));
        }
        */
        // loadHistoryToDB();
        // System.out.println("uncomment what you want to do!");
        // exportToCSV();
        testSplitNames();
    }
    private static void testSplitNames() {
        for (String testName: testNames) {
            System.out.println(testName + "=" + splitFullName(testName));
        }
    }
    static class NameTrio {
        String title = "";
        String firstName = "";
        String lastName = "";
        public String toString() {
            return "[" + title + "," + firstName + "," + lastName + "]";
        }
    }
    private static NameTrio splitFullName(String fullName) {
        NameTrio nameTrio = new NameTrio();
        int index = fullName.indexOf(" ");
        if (index == -1) {
            nameTrio.firstName = fullName;
        } else {
            String nameAfterTitle;
            boolean titled;
            if (fullName.startsWith(MRNMRS + " ")) {
                nameTrio.title = MRNMRS;
                titled = true;
                nameAfterTitle = fullName.substring(9);
            } else {
                String firstToken = fullName.substring(0, index);
                titled = isTitle(firstToken);
                if (titled) {
                    nameTrio.title = firstToken;
                    nameAfterTitle = fullName.substring(index + 1);
                } else {
                    nameAfterTitle = fullName;
                }
            }
            index = nameAfterTitle.indexOf(" ");
            if (index == -1) {
                if (titled) {
                    nameTrio.lastName = nameAfterTitle;
                } else {
                    nameTrio.firstName = nameAfterTitle;
                }
            } else {
                nameTrio.firstName = nameAfterTitle.substring(0, index);
                nameTrio.lastName = nameAfterTitle.substring(index + 1);
            }
        }
        return nameTrio;
    }
    private static boolean isTitle(String name) {
        for (String title: titles) {
            if (title.equalsIgnoreCase(name)) return true;
        }
        return false;
    }
    public static void exportToCSV() {
        // make sure no fields contain tabs; use tabs as field separators;
        // split fullName into 3 fields

    }
    public static void getMetaData() throws SQLException {
        Connection conn = connectToSQLite();
        DatabaseMetaData metaData = conn.getMetaData();
        // ResultSet rs = metaData.getTables(null, null, "Customer", null);
        ResultSet rs = metaData.getColumns(null, null, "Customer", null);
        while (rs.next()) {
            String colName = rs.getString("COLUMN_NAME");
            String colType = rs.getString("TYPE_NAME");
            System.out.println("name=" + colName + "; colType=" + colType);
        }
        rs.close();
        conn.close();
        System.out.println("db closed");
    }
    public static void readCustomerTable() {
        try {
            Connection conn = connectToSQLite();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "select id, fullName, address1, address2, town, " +
                    "postcode, phone, mobile, email, ovenType, howFound, " +
                    "notes, nextAction, nextActionDateTime, " +
                    "cleanIntervalMonths from Customer where id < 10");
            System.out.println(Customer.getTitles());
            while (rs.next()) {
                Customer customer = new Customer();
                customer.id = rs.getInt("id");
                customer.fullName = rs.getString("fullName");
                customer.address1 = rs.getString("address1");
                customer.address2 = rs.getString("address2");
                customer.town = rs.getString("town");
                customer.postcode = rs.getString("postcode");
                customer.phone = rs.getString("phone");
                customer.mobile = rs.getString("mobile");
                customer.email = rs.getString("email");
                customer.ovenType = rs.getString("ovenType");
                customer.howFound = rs.getString("howFound");
                customer.notes = rs.getString("notes");
                customer.nextAction = rs.getString("nextAction");
                System.out.println("nextActionDateTime as String: " + rs.getString("nextActionDateTime"));
                customer.cleanIntervalMonths = rs.getInt("cleanIntervalMonths");
                System.out.println(customer);
            }
            rs.close();
            stmt.close();
            conn.close();
            System.out.println("db closed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static List<Customer> readCustomerFile() throws IOException {
        System.out.println("About to read " + customerTxtFileName);
        String customersTxt = fileToString(customerTxtFileName);
        List<Customer> customerList = new ArrayList<>();
        String[] customerStrs = customersTxt.split("\\^");
        System.out.println("customers.length=" + customerStrs.length);
        for (int i = 0; i < customerStrs.length; i++) {
            String customerStr = customerStrs[i];
            String[] customerFields = customerStr.split("\\$");
            int customerFieldsLength = customerFields.length;
            if (customerFieldsLength == 16) {
                Customer customer = new Customer();
                customer.id = Integer.parseInt(customerFields[0]);
                customer.fullName = customerFields[1];
                customer.address1 = customerFields[2];
                customer.address2 = customerFields[3];
                customer.town = customerFields[4];
                customer.postcode = customerFields[5];
                customer.phone = customerFields[6];
                customer.mobile = customerFields[7];
                customer.email = customerFields[8];
                customer.ovenType = customerFields[9];
                customer.howFound = customerFields[10];
                customer.notes = customerFields[11];
                customer.nextAction = customerFields[12];
                String naDate = customerFields[13];
                String naTime = customerFields[14];
                if (naDate != null && naDate.length() > 0 && !naDate.equals("\\N")) {
                    if (naTime != null && naTime.length() > 0 && !naTime.equals("\\N")) {
                        customer.nextActionDateTime = naDate + " " + naTime;
                    } else {
                        customer.nextActionDateTime = naDate + " 00:00:00";
                    }
                }
                customer.cleanIntervalMonths = Integer.parseInt(customerFields[15]);
                customerList.add(customer);
            } else {
                System.out.println("unexpected customerFields.length=" +
                        customerFieldsLength + "; i=" + i + "; customerStr=" +
                        customerStr);
            }
        }
        return customerList;
    }
    private static List<CleanHistory> readHistoryFile() throws IOException {
        System.out.println("About to read " + historyTxtFileName);
        String historyTxt = fileToString(historyTxtFileName);
        List<CleanHistory> historyList = new ArrayList<>();
        String[] historyStrs = historyTxt.split("\\^");
        System.out.println("history.length=" + historyStrs.length);
        for (int i = 0; i < historyStrs.length; i++) {
            String historyStr = historyStrs[i];
            String[] historyFields = historyStr.split("\\$");
            int historyFieldsLength = historyFields.length;
            if (historyFieldsLength == 6 || historyFieldsLength == 5) {
                CleanHistory cleanHistory = new CleanHistory();
                cleanHistory.id = Integer.parseInt(historyFields[0]);
                cleanHistory.customerId = Integer.parseInt(historyFields[1]);
                cleanHistory.cleanDescription = historyFields[2];
                cleanHistory.cleanDate = historyFields[3];
                Double priceD = Double.parseDouble(historyFields[4]);
                cleanHistory.cleanPrice = priceD.intValue();
                if (historyFieldsLength == 6) cleanHistory.cleaner = historyFields[5];
                historyList.add(cleanHistory);
            } else {
                System.out.println("unexpected historyFieldsLength.length=" +
                        historyFieldsLength + "; i=" + i + "; historyStr=" +
                        historyStr);
            }
        }
        return historyList;
    }
    public static String fileToString(String fileName) throws IOException {
        File file = new File(fileName);
        int length = (int)file.length();
        char[] charData = new char[length];
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        isr.read(charData);
        isr.close();
        return new String(charData);
    }
    public static Connection connectToSQLite() throws SQLException {
        // new org.sqlite.JDBC(); // now done automatically in getConnection
        // Class.forName("org.sqlite.JDBC");
        Connection conn =
                DriverManager.getConnection("jdbc:sqlite:" + dbLocation);
        System.out.println("Opened database successfully");
        return conn;
    }
    public static void loadDB() {
        try {
            List<Customer> customerList = readCustomerFile();
            Connection conn = connectToSQLite();
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement(insertCustomer);
            for (Customer customer: customerList) {
                stmt.setInt(1, customer.id);
                stmt.setString(2, customer.fullName);
                stmt.setString(3, customer.address1);
                stmt.setString(4, customer.address2);
                stmt.setString(5, customer.town);
                stmt.setString(6, customer.postcode);
                stmt.setString(7, customer.phone);
                stmt.setString(8, customer.mobile);
                stmt.setString(9, customer.email);
                stmt.setString(10, customer.ovenType);
                stmt.setString(11, customer.howFound);
                stmt.setString(12, customer.notes);
                stmt.setString(13, customer.nextAction);
                // stmt.setTimestamp(14, customer.nextActionDateTime);
                // stmt.setLong(14, customer.nextActionDateTime.getTime());
                stmt.setString(14, customer.nextActionDateTime);
                stmt.setInt(15, customer.cleanIntervalMonths);
                stmt.executeUpdate();
            }
            stmt.close();
            conn.commit();
            conn.close();
            System.out.println("db closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void loadHistoryToDB() {
        try {
            List<CleanHistory> historyList = readHistoryFile();
            Connection conn = connectToSQLite();
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement(insertCleanHistory);
            for (CleanHistory cleanHistory: historyList) {
                stmt.setInt(1, cleanHistory.id);
                stmt.setInt(2, cleanHistory.customerId);
                stmt.setString(3, cleanHistory.cleanDescription);
                stmt.setString(4, cleanHistory.cleanDate);
                stmt.setInt(5, cleanHistory.cleanPrice);
                stmt.setString(6, cleanHistory.cleaner);
                stmt.executeUpdate();
            }
            stmt.close();
            conn.commit();
            conn.close();
            System.out.println("db closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
