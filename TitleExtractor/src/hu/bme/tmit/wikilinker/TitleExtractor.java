package hu.bme.tmit.wikilinker;

import java.util.Locale;

import com.google.common.base.Stopwatch;

import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;

public class TitleExtractor {
	
	private static final String PATH = "e:\\TEMP\\wiki-dumps\\enwiki-latest-pages-articles1.xml";
	
	public static void main(String[] args) {
		TitleExtractor extractor = new TitleExtractor();
		extractor.extrac();
	}

	private void extrac() {
		WikiXMLParser parser = WikiXMLParserFactory.getSAXParser(PATH);
		try {
			LinkAggregateCallback callback = new LinkAggregateCallback();
			callback.setLogPagesInterval(100000);
			parser.setPageCallback(callback);
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			parser.parse();
			stopwatch.stop();
			Locale.setDefault(Locale.US);
			System.out.println("Pages:\t" + callback.getParsedPages());
			System.out.println("Time:\t" + stopwatch.toString());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
