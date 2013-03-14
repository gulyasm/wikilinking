package hu.bme.tmit.wikilinker;

import java.util.Locale;

import com.google.common.base.Stopwatch;

import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;

public class TitleExtractor {
	
//	private static final String PATH = "e:\\TEMP\\wiki-dumps\\enwiki-latest-pages-articles1.xml-p000000010p000010000";
//	private static final String PATH = "e:\\TEMP\\wiki-dumps\\enwiki-20130304-pages-meta-current12.xml-p001825001p002425000";
	private static final String PATH = "e:\\TEMP\\wiki-dumps\\enwiki-20130304-pages-meta-current23.xml";
	
	public static void main(String[] args) {
		TitleExtractor extractor = new TitleExtractor();
		extractor.extrac();
	}

	private void extrac() {
		WikiXMLParser parser = WikiXMLParserFactory.getSAXParser(PATH);
		try {
			StatisticalCallback callback = new StatisticalCallback();
			callback.setLogPagesInterval(100000);
			parser.setPageCallback(callback);
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			parser.parse();
			stopwatch.stop();
			Locale.setDefault(Locale.US);
			System.out.println("Pages ---> \n" + callback.getParsedPages());
			System.out.println("Time ---> \n" + stopwatch.toString());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
