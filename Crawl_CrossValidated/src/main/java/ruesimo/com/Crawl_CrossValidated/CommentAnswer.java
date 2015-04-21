/**
 * @author Simon
 */
package ruesimo.com.Crawl_CrossValidated;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CommentAnswer extends Message {

	private int answerIndex;
	private int commentIndex;

	public CommentAnswer(int parentMessageID, Document document,
			int answerIndex, int commentIndex) {
		super(parentMessageID, document);
		this.id = parentMessageID;
		this.answerIndex = answerIndex;
		this.commentIndex = commentIndex;
	}

	public void crawl() {
		Element answers = document.getElementById("answers");
		Element answer = answers.select("div.answer").get(answerIndex);
		Element comment = answer.select("tr.comment").get(commentIndex);
		Element commentText = answer.select("td.comment-text")
				.get(commentIndex);

		setSubIDComment(comment);
		setText(commentText);
		setDate(commentText);
		setUserID(commentText);
		setUserName(commentText);
		setVotes(answer);
	}

	private void setSubIDComment(Element comment) {
		String preSubIDCommentString = comment.attr("id");
		String subIDCommentString = preSubIDCommentString.substring(
				preSubIDCommentString.indexOf("comment-") + 8,
				preSubIDCommentString.length());
		subIDComment = Integer.parseInt(subIDCommentString);
	}

	private void setText(Element commentText) {
		Element commentCopy = commentText.select("span.comment-copy").first();
		text = commentCopy.text();
		textBlob = commentCopy.html();
	}

	private void setDate(Element commentText) {
		Element commentDate = commentText.select("span.relativetime-clean")
				.first();
		String preDate = commentDate.attr("title");
		// example of preDate:
		// 2009-02-02 14:21:12Z
		date = preDate.substring(0, preDate.indexOf("Z"));
	}

	private void setUserID(Element commentText) {
		Element commentUser = commentText.select("a.comment-user").last();
		String userLink = "";
		try {
			userLink = commentUser.select("a[href]").last().attr("href");
			// last() to ignore links in comment
		} catch (NullPointerException n) {
			System.out.println("CommentAnswer.get(" + answerIndex + ", "
					+ commentIndex + "): No userID");
		}
		if (!userLink.isEmpty()) {
			// example of userLink
			// /users/7420/mittenchops
			String idString = userLink.substring(
					userLink.indexOf("/users/") + 7, userLink.lastIndexOf('/'));
			userID = Integer.parseInt(idString);
		}
	}

	private void setUserName(Element commentText) {
		if (userID != -1) {
			userName = commentText.select("a.comment-user").first().text();
		}
	}

	private void setVotes(Element answer) {
		Element commentScore = answer.select("td.comment-score").get(
				commentIndex);
		try {
			votes = Integer.parseInt(commentScore.text());
		} catch (NumberFormatException n) {
			System.out.println("CommentAnswer.get(" + answerIndex + ", "
					+ commentIndex + "): No votes");
		}
	}
}