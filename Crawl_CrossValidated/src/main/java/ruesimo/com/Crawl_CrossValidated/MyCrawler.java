/**
 * @author Simon
 */
package ruesimo.com.Crawl_CrossValidated;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {

	/**
	 * Pages that are going to be visited contain a part of a list containg all
	 * questions on CrossValidated
	 * */
	@Override
	public boolean shouldVisit(Page page, WebURL url) {
		String href = url.getURL().toLowerCase();
		return href
				.matches("http://stats.stackexchange.com/questions?page=\\d*/&pagesize=50&sort=active");
	}

	/**
	 * For every page, a Thread is created, which will be newly initialized for
	 * every new question on the page. Every newly initialized thread is saved
	 * in table "Threads". Then, its question, commentquestions, answers, and
	 * commentanswers are created and saved on their respective tables.
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		System.out.println("*************************************************");
		System.out.println("URL: " + url);
		System.out.println("*************************************************");
		int counter = 1;

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();

			Document document = Jsoup.parse(html);
			Document currentDocument = null;

			Thread thread = new Thread(document);

			for (int threadIndex = 0; threadIndex < thread
					.getNumberOfQuestions(); threadIndex++) {
				thread.crawl(threadIndex);
				thread.save(Controller.DATABASE, Controller.PASSWORD);
				int pageNumber = 0;

				do {
					pageNumber++;
					String currentUrl = thread.getUrl() + "?page=" + pageNumber;

					try {
						java.lang.Thread.sleep(500);
						currentDocument = Jsoup.connect(currentUrl).get();
						System.out.println("URL: " + currentUrl + " (Nr. "
								+ (counter++) + ")");
						Question question = new Question(thread.getID(),
								currentDocument);

						question.crawl();
						question.save(Controller.DATABASE, Controller.PASSWORD);

						for (int commentIndex = 0; commentIndex < question
								.getNumberOfComments(); commentIndex++) {
							CommentQuestion comment = new CommentQuestion(
									question.getID(), question.getDocument(),
									commentIndex);
							comment.crawl();
							comment.save(Controller.DATABASE,
									Controller.PASSWORD);
						}
						for (int answerIndex = 0; answerIndex < question
								.getNumberOfAnswers(); answerIndex++) {
							Answer answer = new Answer(question.getID(),
									currentDocument, answerIndex);
							answer.crawl();
							answer.save(Controller.DATABASE,
									Controller.PASSWORD);
							for (int commentIndex = 0; commentIndex < answer
									.getNumberOfComments(); commentIndex++) {
								CommentAnswer comment = new CommentAnswer(
										answer.getID(), answer.getDocument(),
										answerIndex, commentIndex);
								comment.crawl();
								comment.save(Controller.DATABASE,
										Controller.PASSWORD);
							}
						}
					} catch (Exception e) {
						System.out.println("MyCrawler: No connection to: "
								+ currentUrl);
					}
				} while (currentDocument.select("div.pager-answers").html()
						.contains("next"));
			}
		}
	}
}