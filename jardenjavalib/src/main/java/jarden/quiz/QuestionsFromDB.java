package jarden.quiz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class QuestionsFromDB {
	private final static String DRIVER_NAME =
			"org.apache.derby.jdbc.ClientDriver";
	private final static String JDBC_URL =
			"jdbc:derby://localhost:1527/johndb;user=john";
	private final static String CATEGORY = "food";

	public static void main(String[] args) throws Exception {
		Class.forName(DRIVER_NAME);
		ArrayList<QuestionAnswer> qaList = questionsFromDB(CATEGORY);
		System.out.println("Question/Answer list from DB, category " + CATEGORY);
		for (QuestionAnswer qa: qaList) {
			System.out.println("  " + qa);
		}
	}
	public static ArrayList<QuestionAnswer> questionsFromDB(String category) {
		try {
			ArrayList<QuestionAnswer> qaList = new ArrayList<QuestionAnswer>();
			Connection connection = DriverManager.getConnection(JDBC_URL);
			Statement statement = connection.createStatement();
			String sqlString =
					"select english, spanish from ENGSPA where category = '" +
					category + "'";
			statement.executeQuery(sqlString);
			ResultSet resultSet = statement.getResultSet();
			while(resultSet.next()) {
				String english = resultSet.getString(1);
				String spanish = resultSet.getString(2);
				qaList.add(new QuestionAnswer(english, spanish));
			}
			statement.close();
			connection.close();
			return qaList;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
