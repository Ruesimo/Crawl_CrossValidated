/**
 * @author Simon
 */
package ruesimo.com.Crawl_CrossValidated;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {

	public static String PASSWORD;
	public static String DATABASE;

	public static void main(String[] args) throws Exception {

		DATABASE = args[0];
		PASSWORD = args[1];

		String crawlStorageFolder = args[2];
		int numberOfCrawlers = 1;
		int politenessDelay = 500;
		int maxDepth = 1;

		PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream("output.txt"));
			System.setOut(out);
		} catch (FileNotFoundException e) {
			System.err
					.println("Couldn't find output file. Output will be sent to console.");
		}

		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setPolitenessDelay(politenessDelay);
		config.setMaxDepthOfCrawling(maxDepth);

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig,
				pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher,
				robotstxtServer);

		// seed URLs are all pages in CrossValidated that are needed to show the list of all questions
		for (int pageNumber = 1500; pageNumber > 0; pageNumber--) {
			controller.addSeed("http://stats.stackexchange.com/questions?page="
					+ pageNumber + "&pagesize=50&sort=active");
		}

		controller.start(MyCrawler.class, numberOfCrawlers);
	}
}