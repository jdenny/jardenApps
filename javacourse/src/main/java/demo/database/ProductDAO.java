package demo.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import solution.Product;

/**
 * Version of Products that gets its database connection values from
 * a properties file.
 */
public class ProductDAO {
	public final static String CONNECTION_PROPERTIES = "docs/jdbc.properties";
	
	public static void main(String[] args) throws Exception {
		Connection connection = getConnection();
		Product newProduct = newProduct(connection, 200, "test product", 12.34);
		ArrayList<Product> products;
		products = getAllProducts(connection);
		for (Product product: products) {
			System.out.println(product);
		}
		newProduct.setPrice(220);
		newProduct.setDescription("more expensive test product");
		updateProduct(connection, newProduct);
		products = getAllProducts(connection);
		for (Product product: products) {
			System.out.println(product);
		}
		deleteProduct(connection, 200);
		products = getAllProducts(connection);
		for (Product product: products) {
			System.out.println(product);
		}
		connection.close();
		System.out.println("Database connection closed");
	}
	public static ArrayList<Product> getAllProducts(Connection connection) throws Exception {
		String sqlString = "select partNumber, description, price from products";
		PreparedStatement statement = connection.prepareStatement(sqlString);
		ArrayList<Product> products = new ArrayList<>();
		statement.executeQuery();
		ResultSet resultSet = statement.getResultSet();
		while(resultSet.next()) {
			int partNumber = resultSet.getInt(1);
			String description = resultSet.getString(2);
			double price = resultSet.getDouble(3);
			products.add(new Product(partNumber, description, price));
		}
		statement.close();
		return products;
	}
	public static Product getProduct(Connection connection, int partNumber) throws Exception {
		String sqlString =
				"select description, price from products where partnumber = ?";
		PreparedStatement statement = connection.prepareStatement(sqlString);
		statement.setInt(1, partNumber);
		statement.executeQuery();
		ResultSet resultSet = statement.getResultSet();
		Product product = null;
		if(resultSet.next()) {
			String description = resultSet.getString(1);
			double price = resultSet.getDouble(2);
			product = new Product(partNumber, description, price);
		}
		statement.close();
		return product;
	}
	public static Product newProduct(Connection connection, int partNumber, String description,
			double price) throws Exception {
		Product product = new Product(partNumber, description, price);
		String sqlString =
			"insert into products (partnumber, description, price) values (?, ?, ?)";
		PreparedStatement statement = connection.prepareStatement(sqlString);
		statement.setInt(1, partNumber);
		statement.setString(2, description);
		statement.setDouble(3, price);
		int result = statement.executeUpdate();
		System.out.println(result + " product(s) added to database");
		statement.close();
		return product;
	}
	public static void updateProduct(Connection connection, Product product) throws Exception {
		String sqlString =
			"update products set description = ?, price = ? where partnumber = ?";
		PreparedStatement statement = connection.prepareStatement(sqlString);
		statement.setString(1, product.getDescription());
		statement.setDouble(2, product.getPrice());
		statement.setInt(3, product.getPartNumber());
		int result = statement.executeUpdate();
		System.out.println(result + " product(s) updated database");
		statement.close();
	}
	public static void deleteProduct(Connection connection, int partNumber) throws Exception {
		String sqlString =
			"delete from products where partnumber = ?";
		PreparedStatement statement = connection.prepareStatement(sqlString);
		statement.setInt(1, partNumber);
		int result = statement.executeUpdate();
		System.out.println(result + " product(s) removed from database");
		statement.close();
	}
	public static Connection getConnection() throws SQLException, IOException {
		Properties jdbcProperties = new Properties();
		FileInputStream fis = new FileInputStream(CONNECTION_PROPERTIES);
		jdbcProperties.load(fis);
		// String jdbcDriver = jdbcProperties.getProperty("jdbcDriver");
		String jdbcURL = jdbcProperties.getProperty("jdbcURL");
		String jdbcUserName = jdbcProperties.getProperty("jdbcUserName");
		String jdbcPassword = jdbcProperties.getProperty("jdbcPassword");
		// Class.forName(jdbcDriver); // no longer required from jdk6
		if (jdbcUserName != null && jdbcUserName.length() > 0) {
			return DriverManager.getConnection(jdbcURL, jdbcUserName, jdbcPassword);
		}
		else {
			return DriverManager.getConnection(jdbcURL);
		}
	}
}
