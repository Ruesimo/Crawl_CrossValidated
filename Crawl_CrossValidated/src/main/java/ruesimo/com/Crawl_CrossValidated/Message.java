/**
 * @author Simon
 */
package ruesimo.com.Crawl_CrossValidated;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jsoup.nodes.Document;

public class Message {

	private static final String SQL_INSERT = "INSERT INTO messages"
			+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE text=?, votes=?, fav_votes=?, accepted_as_best=?, text_blob=?, with_hidden_content=?";

	protected Document document;

	protected int id;
	protected int subIDComment;
	protected int parentMessageID;
	protected String text;
	protected String date;
	protected int userID;
	protected String userName;
	protected int votes;
	protected int favVotes;
	protected int acceptedAsBest;
	protected String textBlob;
	protected int withHiddenContent;

	public Message(int parentMessageID, Document document) {
		this.id = -1;
		this.subIDComment = -1;
		this.parentMessageID = parentMessageID;
		this.text = "";
		this.date = "1900-01-01 00:00:00";
		this.userID = -1;
		this.userName = "";
		this.votes = 0;
		this.favVotes = 0;
		this.acceptedAsBest = 0;
		this.textBlob = "";
		this.withHiddenContent = 0;

		this.document = document;
	}

	public void save(String database, String password) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost/"
					+ database, "root", password);
			statement = connection.prepareStatement(SQL_INSERT);
			statement.setInt(1, id);
			statement.setInt(2, subIDComment);
			statement.setInt(3, parentMessageID);
			statement.setString(4, text);
			statement.setString(5, date);
			statement.setInt(6, userID);
			statement.setInt(7, votes);
			statement.setInt(8, favVotes);
			statement.setInt(9, acceptedAsBest);
			statement.setString(10, textBlob);
			statement.setInt(11, withHiddenContent);

			statement.setString(12, text);
			statement.setInt(13, votes);
			statement.setInt(14, favVotes);
			statement.setInt(15, acceptedAsBest);
			statement.setString(16, textBlob);
			statement.setInt(17, withHiddenContent);

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e1) {
			System.out.println("Message: " + e1);

			try {
				String textError = "Encoding error: look up text_blob";

				connection = DriverManager.getConnection(
						"jdbc:mysql://localhost/" + database, "root", password);
				statement = connection.prepareStatement(SQL_INSERT);
				statement.setInt(1, id);
				statement.setInt(2, subIDComment);
				statement.setInt(3, parentMessageID);
				statement.setString(4, textError);
				statement.setString(5, date);
				statement.setInt(6, userID);
				statement.setInt(7, votes);
				statement.setInt(8, favVotes);
				statement.setInt(9, acceptedAsBest);
				statement.setString(10, textBlob);
				statement.setInt(11, withHiddenContent);

				statement.setString(12, textError);
				statement.setInt(13, votes);
				statement.setInt(14, favVotes);
				statement.setInt(15, acceptedAsBest);
				statement.setString(16, textBlob);
				statement.setInt(17, withHiddenContent);

				statement.executeUpdate();

				connection.close();
			} catch (SQLException e2) {
				System.out.println("Message: " + e2);
			}
		}

		User user = new User(userID, userName);
		user.save(database, password);
	}

	public int getID() {
		return id;
	}
}