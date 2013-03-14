package hu.bme.tmit.wikilinker;

import java.text.MessageFormat;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class AbstractPageCallback implements PageCallbackHandler {
	
	private int parsedPages;
	private int logPagesInterval = -1;

	@Override
	public void process(WikiPage page) {
		parsedPages++;
		if(logPagesInterval != -1) {
			if(parsedPages % logPagesInterval == 0) {
				System.out.println(MessageFormat.format("{0} pages parsed", parsedPages));
			}
		}
	}
	
	public int getParsedPages() {
		return parsedPages;
	}
	
	public void setLogPagesInterval(int logPagesInterval) {
		this.logPagesInterval = logPagesInterval;
	}

}
