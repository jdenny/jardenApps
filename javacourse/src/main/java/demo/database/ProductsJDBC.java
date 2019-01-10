package demo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductsJDBC {
	private final static String JDBC_URL =
			"jdbc:derby://localhost:1527/javaprogdb";

	public static void main(String[] args) throws Exception {
		// from jdk6 no longer need to pre-load driver:
		// Class.forName("org.apache.derby.jdbc.ClientDriver");
		printProducts();
	}
	public static void printProducts() throws SQLException {
		Connection connection = DriverManager.getConnection(JDBC_URL);
		Statement statement = connection.createStatement();
		String sqlString = "select partNumber, description, price from products";
		statement.executeQuery(sqlString);
		ResultSet resultSet = statement.getResultSet();
		System.out.println("PartNumber, description, price");
		while(resultSet.next()) {
			int partNum = resultSet.getInt(1);
			String description = resultSet.getString(2);
			double price = resultSet.getDouble(3);
			System.out.println("  " + partNum + ", " + description + ", " + price);
		}
		statement.close();
		connection.close();
		System.out.println("Database connection closed");
	}
}
