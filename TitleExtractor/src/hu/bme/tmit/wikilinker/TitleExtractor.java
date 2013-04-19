package hu.bme.tmit.wikilinker;

import hu.bme.tmit.wikilinker.logger.Logger;
import hu.bme.tmit.wikilinker.model.WikiKnowledge;

import java.text.MessageFormat;
import java.util.Locale;

import com.google.common.base.Stopwatch;

import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;

public class TitleExtractor {

	private static final Logger LOG = new Logger(TitleExtractor.class);
	private static final String PATH = "e:\\TEMP\\wiki-dumps\\enwiki-latest-pages-articles1.xml";

	public static void main(String[] args) {
		TitleExtractor extractor = new TitleExtractor();
		extractor.extract();
	}

	private void extract() {
		WikiXMLParser parser = WikiXMLParserFactory.getSAXParser(PATH);
		try {
			Logger.setLevel(Logger.INFO);
//			AbstractPageCallback callback = new DBAggregateCallback();
			AbstractPageCallback callback = new LinkerCallback();
			parser.setPageCallback(callback);

			// Start your engine...
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			// Start!
			parser.parse();
			callback.onFinished();
			// Finish Line
			stopwatch.stop();

			// Excel miatt
			Locale.setDefault(Locale.US);
			System.out.println("-------------------------- STATISTICS --------------------------");
			final int parsedPages = callback.getParsedPages();
			LOG.i("Pages:\t" + parsedPages);
			final int redirects = callback.getRedirects();
			LOG.i(MessageFormat.format("Redirects:\t{0} - {1}% of total", redirects, redirects * 100 / parsedPages));
			LOG.i("Time:\t\t" + stopwatch.toString());
			// LOG.i("FilteredByThreshold:\t\t" +
			// callback.getFilteredByThreshold());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
