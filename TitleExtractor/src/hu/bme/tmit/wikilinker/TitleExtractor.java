package hu.bme.tmit.wikilinker;

import com.google.common.base.Joiner;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;

public class TitleExtractor {
	
	private static final String PATH = "e:\\TEMP\\enwiki-latest-pages-articles1.xml-p000000010p000010000";
	
	public static void main(String[] args) {
		TitleExtractor extractor = new TitleExtractor();
		extractor.extrac();
	}

	private void extrac() {
		WikiXMLParser parser = WikiXMLParserFactory.getSAXParser(PATH);
		try {
			parser.setPageCallback(new PageCallbackHandler() {
				
				@Override
				public void process(WikiPage arg0) {
					System.out.println(Joiner.on(", ").join(arg0.getLinks()));
					
				}
			});
			parser.parse();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
