/**
 * @author Simon
 */
package ruesimo.com.Crawl_CrossValidated;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Question extends Message {

	private Boolean isFromCommunityWiki;

	public Question(int id, Document document) {
		super(-1, document);
		this.id = id;
		this.isFromCommunityWiki = false;
	}

	public void crawl() {
		Element question = document.getElementById("question");

		setText(question);
		setDate(question);
		setUserID(question);
		setUserName(question);
		setVotes(question);
		setFavVotes(question);
		setWithHiddenContent(question);
	}

	public int getNumberOfComments() {
		Element question = document.getElementById("question");
		Elements comments = question.select("tr.comment");
		return comments.size();
	}

	public int getNumberOfAnswers() {
		Element answer = document.getElementById("answers");
		Elements answers = answer.select("div.answer");
		return answers.size();
	}

	public Document getDocument() {
		return document;
	}

	private void setText(Element question) {
		text = question.select("div.post-text").text();
		textBlob = question.select("div.post-text").html();
	}

	private void setDate(Element question) {
		Element postSignature = question.select("td.post-signature").last();
		// last: because owner is chosen, not editor
		Element actionTime = postSignature.select("div.user-action-time")
				.first();
		String preDate = "";
		try {
			preDate = actionTime.select("span.relativetime").attr("title");
		} catch (NullPointerException n) {
			System.out.println("Question: From community wiki");
			isFromCommunityWiki = true;
			postSignature = question.select("td.post-signature").first();
			actionTime = postSignature.select("div.user-action-time").first();
			preDate = actionTime.select("span.relativetime").attr("title");
		}
		date = preDate.substring(0, preDate.indexOf("Z"));
	}

	private void setUserID(Element question) {
		Element userDetails = null;
		if (!isFromCommunityWiki) {
			userDetails = question.select("div.user-details").last();
			// last: because owner is chosen, not editor
			String userLink = userDetails.select("a[href]").attr("href");
			// example of userLink
			// /users/7420/mittenchops
			if (userLink.isEmpty()) {
				System.out.println("Question: No userID");
			} else {
				String idString = userLink.substring(
						userLink.indexOf("/users/") + 7,
						userLink.lastIndexOf('/'));
				userID = Integer.parseInt(idString);
			}
		}
	}

	private void setUserName(Element question) {
		Element userDetails = question.select("div.user-details").last();
		// last: because owner is chosen, not editor
		if (!isFromCommunityWiki) {
			userName = userDetails.select("a[href]").text();
		}
	}

	private void setVotes(Element question) {
		Element voteCount = question.select("span.vote-count-post").first();
		// example of vote-count-post:
		// <span itemprop="upvoteCount" class="vote-count-post ">2</span>
		votes = Integer.parseInt(voteCount.text());
	}

	private void setFavVotes(Element question) {
		Element favoriteCount = question.select("div.favoritecount").first();
		// example of favoritecount:
		// <div class="favoritecount">
		// <b>3</b>
		// </div>
		String favVotesString = favoriteCount.text();
		if (favVotesString.isEmpty()) {
			System.out.println("Question: No favVotes");
		} else {
			favVotes = Integer.parseInt(favoriteCount.text());
		}
	}

	private void setWithHiddenContent(Element question) {
		if (question.text().contains("show")
				&& question.text().contains("more comment")) {
			withHiddenContent = 1;
			setDocument(id);
		}
	}

	private void setDocument(int id) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String html = "";

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost/"
					+ Controller.DATABASE, "root", Controller.PASSWORD);
			statement = connection.createStatement();
			resultSet = statement
					.executeQuery("SELECT * FROM hidden_contents where id=" + id);
			while (resultSet.next()) {
				html = resultSet.getString("html");
				if (!html.isEmpty()) {
					document = Jsoup.parse(html);
				}
			}
			connection.close();
		} catch (Exception e) {
			System.out.println("Question: Couldn't load hidden content" + e);
		}
	}
}