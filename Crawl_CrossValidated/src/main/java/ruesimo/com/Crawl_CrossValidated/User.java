/**
 * @author Simon
 */
package ruesimo.com.Crawl_CrossValidated;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class User {

	private static final String SQL_INSERT = "INSERT INTO users VALUES (?, ?) ON DUPLICATE KEY UPDATE username=?";

	private int userID;
	private String userName;

	public User(int userID, String userName) {
		this.userID = userID;
		this.userName = userName;
	}

	public void save(String database, String password) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost/"
					+ database, "root", password);
			statement = connection.prepareStatement(SQL_INSERT);
			statement.setInt(1, userID);
			statement.setString(2, userName);

			statement.setString(3, userName);

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			System.out.println("User: " + e);
		}
	}
}