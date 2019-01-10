package demo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class HandlingErrors {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// doSumsNoChecks(args);
		doSumsChecks(args);
		System.out.println("End of program");
	}
	public static void doSumsNoChecks(String[] args) {
		int arg1 = Integer.parseInt(args[0]);
		char oper = args[1].charAt(0);
		int arg2 = Integer.parseInt(args[2]);
		arithmetic(arg1, oper, arg2);
	}
	private static void doSumsChecks(String[] args) {
		try {
			int arg1 = Integer.parseInt(args[0]);
			char oper = args[1].charAt(0);
			int arg2 = Integer.parseInt(args[2]);
			arithmetic(arg1, oper, arg2);
		} catch (NumberFormatException nfe) {
			System.out.println(nfe);
		} catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println(ex);
		}
	}
	public static void arithmetic(int arg1, char oper, int arg2) {
		int result;
		switch(oper) {
		case '-':
			result = arg1 - arg2;
			break;
		case '*':
			result = arg1 * arg2;
			break;
		case '/':
			result = arg1 / arg2;
			break;
		default:
			result = arg1 + arg2;
		}
		System.out.println(arg1 + oper + arg2 + "=" + result);
	}
	public static void fileDBStuff() throws SQLException, IOException {
		final String JDBC_URL =
				"jdbc:derby://localhost:1527/johndb;user=john";

		FileInputStream fis = new FileInputStream("file.txt");
		Connection connection = DriverManager.getConnection(JDBC_URL);
		Statement statement = connection.createStatement();
		statement.execute("select * from SalesUnit");
		fis.close();
	}
	public static void fileDBStuff2() {
		final String JDBC_URL =
				"jdbc:derby://localhost:1527/johndb;user=john";
		FileInputStream fis = null;
		try {
			fis = new FileInputStream("file.txt");
			Connection connection;
			connection = DriverManager.getConnection(JDBC_URL);
			Statement statement = connection.createStatement();
			statement.execute("select * from SalesUnit");
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void bankStuff() {
		System.out.println("about to do something dangerous");
		try {
			withdraw(200);
		} catch (InsufficientFundsException e) {
			e.printStackTrace();
		}
		System.out.println("please don't try this at home");
	}
	public static double withdraw(double amount) throws InsufficientFundsException {
		double balance = 100;
		if (amount > balance) {
			throw new InsufficientFundsException("cannot withdraw " + amount);
		}
		balance -= amount;
		return balance;
	}
	public static char getRandomOperator() {
		int opCode = new Random().nextInt(4);
		char op;
		switch(opCode) {
		case 0:
			op = '+';
			break;
		case 1:
			op = '-';
			break;
		case 2:
			op = '*';
			break;
		default:
			assert opCode == 3: "unexpected opCode=" + opCode;
			op = '/';
			break;
		}
		return op;
	}
}
