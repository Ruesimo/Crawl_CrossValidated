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

public class Answer extends Message {

	private int answerIndex;
	private Boolean isFromCommunityWiki;

	public Answer(int parentMessageID, Document document, int answerIndex) {
		super(parentMessageID, document);
		this.parentMessageID = parentMessageID;
		this.answerIndex = answerIndex;
		this.isFromCommunityWiki = false;
	}

	public void crawl() {
		Element answers = document.getElementById("answers");
		Element answer = answers.select("div.answer").get(answerIndex);
		setID(answer);
		setText(answer);
		setDate(answer);
		setUserID(answer);
		setUserName(answer);
		setVotes(answer);
		setAcceptedAsBest(answer);
		setWithHiddenContent(answer);
	}

	public int getNumberOfComments() {
		Element answers = document.getElementById("answers");
		Element answer = answers.select("div.answer").get(answerIndex);
		Elements comments = answer.select("tr.comment");
		return comments.size();
	}

	public Document getDocument() {
		return document;
	}

	private void setID(Element answer) {
		String preIdString = answer.attr("id");
		String preId = preIdString.substring(
				preIdString.indexOf("answer-") + 7, preIdString.length());
		id = Integer.parseInt(preId);
	}

	private void setText(Element answer) {
		text = answer.select("div.post-text").text();
		textBlob = answer.select("div.post-text").html();
	}

	private void setDate(Element answer) {
		Element postSignature = answer.select("td.post-signature").last();
		// last: because owner is chosen, not editor
		Element actionTime = postSignature.select("div.user-action-time")
				.first();
		String preDate = "";
		try {
			preDate = actionTime.select("span.relativetime").attr("title");
		} catch (NullPointerException n) {
			System.out.println("Answer.get(" + answerIndex
					+ "): From community wiki");
			isFromCommunityWiki = true;
			postSignature = answer.select("td.post-signature").first();
			actionTime = postSignature.select("div.user-action-time").first();
			preDate = actionTime.select("span.relativetime").attr("title");
		}
		date = preDate.substring(0, preDate.indexOf("Z"));
	}

	private void setUserID(Element answer) {
		Element userDetails = null;
		if (isFromCommunityWiki) {
			System.out
					.println("Answer: No userID, because it's from community wiki");
		} else {
			userDetails = answer.select("div.user-details").last();
			// last: because owner is chosen, not editor
			String userLink = userDetails.select("a[href]").attr("href");
			// example of userLink
			// /users/7420/mittenchops
			if (userLink.isEmpty()) {
				System.out.println("Answer: No userID");
			} else {
				String idString = userLink.substring(
						userLink.indexOf("/users/") + 7,
						userLink.lastIndexOf('/'));
				userID = Integer.parseInt(idString);
			}
		}
	}

	private void setUserName(Element answer) {
		Element postSignature = answer.select("td.post-signature").last();
		Element userDetails = postSignature.select("div.user-details").first();
		if (!isFromCommunityWiki) {
			userName = userDetails.select("a[href]").text();
		}
	}

	private void setVotes(Element answer) {
		Element voteCount = answer.select("span.vote-count-post").first();
		// example of vote-count-post:
		// <span itemprop="upvoteCount" class="vote-count-post ">2</span>
		votes = Integer.parseInt(voteCount.text());
	}

	private void setAcceptedAsBest(Element answer) {
		Elements accepted = answer.select("span.vote-accepted-on");
		if (accepted.size() > 0) {
			acceptedAsBest = 1;
		}
	}

	private void setWithHiddenContent(Element answer) {
		if (answer.text().contains("show")
				&& answer.text().contains("more comment")) {
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
					.executeQuery("SELECT * FROM hidden_contents where id="
							+ id);
			while (resultSet.next()) {
				html = resultSet.getString("html");
				if (!html.isEmpty()) {
					document = Jsoup.parse(html);
				}
			}
			connection.close();
		} catch (Exception e) {
			System.out.println("Answer.get(" + answerIndex
					+ "): Couldn't load hidden content" + e);
		}
	}
}
