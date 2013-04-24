package hu.bme.tmit.wikilinker.callback;

import hu.bme.tmit.wikilinker.logger.Logger;

import java.text.MessageFormat;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;

public class AbstractPageCallback implements PageCallbackHandler {

	private static final Logger LOG = new Logger(AbstractPageCallback.class);
	private int parsedPages = 0;
	private int logPagesInterval = -1;
	private int redirects = 0;

	@Override
	public void process(WikiPage page) {
		parsedPages++;
		if (logPagesInterval != -1) {
			if (parsedPages % logPagesInterval == 0) {
				LOG.i(MessageFormat.format("{0} pages parsed", parsedPages));
			}
		}
		if (page.isRedirect()) {
			redirects++;
		}
	}

	public int getRedirects() {
		return redirects;
	}

	public int getParsedPages() {
		return parsedPages;
	}

	public void setLogPagesInterval(int logPagesInterval) {
		this.logPagesInterval = logPagesInterval;
	}

	public void onFinished() {

	}

}
