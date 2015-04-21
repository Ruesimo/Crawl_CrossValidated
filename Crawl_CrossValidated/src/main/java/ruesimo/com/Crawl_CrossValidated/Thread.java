/**
 * @author Simon
 */
package ruesimo.com.Crawl_CrossValidated;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Thread {

	private static final String SQL_INSERT = "INSERT INTO threads"
			+ " VALUES(?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE title=?, number_of_views=?, tags=?, url=?";

	private Document document;

	private int id;
	private String title;
	private int numberOfViews;
	private String tags;
	private String url;

	public Thread(Document document) {
		this.id = 0;
		this.title = "";
		this.numberOfViews = 0;
		this.tags = "";
		this.url = "";

		this.document = document;
	}

	public int getNumberOfQuestions() {
		Element questions = document.getElementById("questions");
		Elements allQuestions = questions.select("div.question-summary");
		return allQuestions.size();
	}

	public int getID() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public void crawl(int threadIndex) {
		Element questions = document.getElementById("questions");
		Elements allQuestions = questions.select("div.question-summary");
		Element currentQuestion = allQuestions.get(threadIndex);

		setID(currentQuestion);
		setTitle(currentQuestion);
		setNumberOfViews(currentQuestion);
		setTags(currentQuestion);
		setUrl(currentQuestion);
	}

	private void setID(Element currentQuestion) {
		String preIdString = currentQuestion.attr("id");
		String idString = preIdString.substring(17, preIdString.length());
		id = Integer.parseInt(idString);
	}

	private void setTitle(Element currentQuestion) {
		Element currentTitle = currentQuestion.select("a.question-hyperlink")
				.first();
		title = currentTitle.text();
	}

	private void setNumberOfViews(Element currentQuestion) {
		Element currentViews = currentQuestion.select("div.views").first();
		String preNumberOfViewsString = currentViews.attr("title");
		String numberOfViewsString = preNumberOfViewsString.substring(0,
				preNumberOfViewsString.indexOf(" views"));
		numberOfViews = Integer.parseInt(numberOfViewsString.replace(",", ""));
	}

	private void setTags(Element currentQuestion) {
		Element taglist = currentQuestion.select("div.tags").first();
		tags = " " + taglist.text() + " ";
	}

	private void setUrl(Element currentQuestion) {
		Element currentUrl = currentQuestion.select("a.question-hyperlink")
				.first();
		url = "http://stats.stackexchange.com" + currentUrl.attr("href");
	}

	public void save(String database, String password) {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost/"
					+ database, "root", password);
			statement = connection.prepareStatement(SQL_INSERT);
			statement.setInt(1, id);
			statement.setString(2, title);
			statement.setInt(3, numberOfViews);
			statement.setString(4, tags);
			statement.setString(5, url);

			statement.setString(6, title);
			statement.setInt(7, numberOfViews);
			statement.setString(8, tags);
			statement.setString(9, url);

			statement.executeUpdate();

			connection.close();
		} catch (SQLException sqlException) {
			System.out.println("Thread: " + sqlException);
		}
	}
}
